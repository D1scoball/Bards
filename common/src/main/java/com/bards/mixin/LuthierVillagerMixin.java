package com.bards.mixin;

import com.bards.worldgen.villages.BardVillagerProfessions;
import com.bards.worldgen.villages.BardVillagerTrades;
import com.bards.worldgen.villages.LuthierSongs;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(VillagerEntity.class)
public abstract class LuthierVillagerMixin {

    @Shadow public abstract VillagerData getVillagerData();
    @Shadow public abstract Brain<VillagerEntity> getBrain();

    @Unique private int bards_tickCounter = 0;
    @Unique private long bards_lastSoundTime = -1L;
    @Unique private long bards_hurtUntil = 0L;

    @WrapOperation(
            method = "initBrain",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;setSchedule(Lnet/minecraft/entity/ai/brain/Schedule;)V")
    )
    private void wrapInitBrain(Brain instance, Schedule schedule, Operation<Void> original) {
        if (getVillagerData().getProfession().equals(BardVillagerProfessions.LUTHIER)) {
            original.call(instance, BardVillagerTrades.LUTHIER_SCHEDULE);
        } else {
            original.call(instance, schedule);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        VillagerEntity villager = (VillagerEntity)(Object)this;
        if (!(villager.getWorld() instanceof ServerWorld serverWorld)) return;
        if (!villager.getVillagerData().getProfession().equals(BardVillagerProfessions.LUTHIER)) return;

        long timeOfDay = serverWorld.getTimeOfDay() % 24000;
        boolean isPerformanceTime = timeOfDay >= 6000 && timeOfDay < 13000;

        if (!isPerformanceTime) {
            if (!villager.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                villager.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
            bards_tickCounter = 0;
            bards_lastSoundTime = -1L;
            return;
        }

        Optional<net.minecraft.util.math.GlobalPos> jobSite =
                villager.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isEmpty()) return;

        BlockPos standPos = jobSite.get().pos();

        BlockState standState = serverWorld.getBlockState(standPos);
        Direction facing = standState.contains(Properties.HORIZONTAL_FACING)
                ? standState.get(Properties.HORIZONTAL_FACING)
                : Direction.NORTH;

        BlockPos targetPos = standPos.offset(facing);
        int dist = villager.getBlockPos().getManhattanDistance(targetPos);

        if (dist > 2) {
            if (villager.getNavigation().isIdle()) {
                villager.getNavigation().startMovingTo(
                        targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 0.5);
            }
            return;
        }

        villager.getNavigation().stop();
        Vec3d vel = villager.getVelocity();
        villager.setVelocity(0, Math.min(vel.y, 0), 0);

        double dx = standPos.getX() + 0.5 - villager.getX();
        double dz = standPos.getZ() + 0.5 - villager.getZ();
        float targetYaw = (float)(Math.atan2(-dx, dz) * (180.0 / Math.PI));
        villager.setYaw(targetYaw);
        villager.setBodyYaw(targetYaw);
        villager.setHeadYaw(targetYaw);

        LuthierSongs.Song song = getDailySong(villager, serverWorld);
        villager.equipStack(EquipmentSlot.MAINHAND, new ItemStack(song.heldInstrument()));

        long currentTime = serverWorld.getTime();

        if (villager.hurtTime > 0 && currentTime >= bards_hurtUntil) {
            bards_hurtUntil = currentTime + 60;
            bards_lastSoundTime = currentTime;
            stopSongForNearbyPlayers(serverWorld, villager, song);
        }

        if (currentTime < bards_hurtUntil) return;

        if (bards_lastSoundTime < 0 || currentTime - bards_lastSoundTime >= song.soundDurationTicks()) {
            SoundEvent soundEvent = song.sound().value();
            villager.playSound(soundEvent, 1.0f, 1.0f);
            bards_lastSoundTime = currentTime;
        }

        if (bards_tickCounter++ % 20 != 0) return;

        double px = villager.getX();
        double py = villager.getY() + 2.2;
        double pz = villager.getZ();
        serverWorld.spawnParticles(ParticleTypes.NOTE, px, py, pz, 0, song.noteHue(), 0.0, 0.0, 1.0);

        Box effectBox = Box.of(villager.getPos(), 8, 4, 8);
        List<PlayerEntity> nearbyPlayers = serverWorld.getEntitiesByClass(PlayerEntity.class, effectBox, p -> true);
        for (PlayerEntity player : nearbyPlayers) {
            player.addStatusEffect(new StatusEffectInstance(
                    song.effect(), 3600, song.effectAmplifier(), false, true, true
            ));
        }
    }

    @Unique
    private static void stopSongForNearbyPlayers(ServerWorld world, VillagerEntity villager, LuthierSongs.Song song) {
        Optional<RegistryKey<SoundEvent>> key = song.sound().getKey();
        if (key.isEmpty()) return;
        Identifier soundId = key.get().getValue();
        StopSoundS2CPacket packet = new StopSoundS2CPacket(soundId, null);
        Box range = Box.of(villager.getPos(), 64, 32, 64);
        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, range, p -> true)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(packet);
            }
        }
    }

    private static LuthierSongs.Song getDailySong(VillagerEntity villager, ServerWorld world) {
        long day = world.getTimeOfDay() / 24000;
        int idx = (int) Math.abs((villager.getUuid().getLeastSignificantBits() ^ day)
                % LuthierSongs.SONGS.size());
        return LuthierSongs.SONGS.get(idx);
    }
}
