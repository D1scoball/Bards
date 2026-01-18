package com.bards.mixin;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

import static com.bards.BardsMod.MOD_ID;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow
    protected abstract void loadItemModel(ModelIdentifier id);
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadItemModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 1, shift = At.Shift.AFTER))
    public void addItemModel(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        ///LUTES
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "wooden_lute_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "diamond_lute_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "netherite_lute_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "ruby_lute_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "aether_lute_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "unique_lute_0_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "unique_lute_1_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "ender_dragon_lute_model")));
        ///LYRES
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "golden_lyre_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "diamond_lyre_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "netherite_lyre_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "aeternium_lyre_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "aether_lyre_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "unique_lyre_0_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "unique_lyre_1_model")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(MOD_ID, "elder_guardian_lyre_model")));
    }
}

