package com.bards.item;

import com.bards.content.BardsSounds;
import net.fabric_extras.ranged_weapon.api.CustomCrossbow;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class HarpCrossbowItem extends CustomCrossbow {
    public HarpCrossbowItem(Settings settings, RangedConfig config, Supplier<Ingredient> repairIngredientSupplier) {
        super(settings, config, repairIngredientSupplier);
    }

    @Override
    public void shootAll(World world, LivingEntity shooter, Hand hand, ItemStack stack, float speed, float divergence, LivingEntity target) {
        boolean isFirework = false;
        ItemStack fireworkStack = ItemStack.EMPTY;
        ChargedProjectilesComponent charged = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (charged != null) {
            for (ItemStack proj : charged.getProjectiles()) {
                if (proj.isOf(Items.FIREWORK_ROCKET)) {
                    isFirework = true;
                    fireworkStack = proj;
                    break;
                }
            }
        }

        super.shootAll(world, shooter, hand, stack, speed, divergence, target);

        if (world.isClient()) return;

        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        boolean isLightning = itemId.getPath().equals("lightning_harp_crossbow");

        Vec3d look = shooter.getRotationVector();
        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d right = look.crossProduct(up).normalize();
        if (right.lengthSquared() < 1e-6) {
            right = new Vec3d(1, 0, 0);
        }

        Vec3d eyePos = shooter.getEyePos();
        float[] offsets = isLightning ? new float[]{-1.0f, -0.5f, 0.5f, 1.0f} : new float[]{-0.5f, 0.5f};

        for (float offset : offsets) {
            Vec3d spawnPos = eyePos.add(right.multiply(offset));
            if (isFirework) {
                FireworkRocketEntity rocket = new FireworkRocketEntity(world, fireworkStack.copy(), shooter,
                        spawnPos.x, spawnPos.y, spawnPos.z, true);
                rocket.setVelocity(look.x * speed, look.y * speed, look.z * speed);
                world.spawnEntity(rocket);
            } else {
                ArrowEntity arrow = new ArrowEntity(world, spawnPos.x, spawnPos.y, spawnPos.z,
                        Items.ARROW.getDefaultStack(), stack);
                arrow.setOwner(shooter);
                arrow.setVelocity(look.x * speed, look.y * speed, look.z * speed);
                if (shooter instanceof PlayerEntity player && player.getAbilities().creativeMode) {
                    arrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }
                arrow.setCritical(true);
                world.spawnEntity(arrow);
            }
        }

        world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                BardsSounds.harp_crossbow_shoot.soundEvent(), SoundCategory.PLAYERS,
                1.0F, 0.9F + world.getRandom().nextFloat() * 0.2F);
    }
}
