package net.sinedkadis.terracompositio.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.effect.custom.TCEffectBase;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TCEffects {
    public static final DeferredRegister<MobEffect> MOD_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, TerraCompositio.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> FLOW_SATURATION =
            MOD_EFFECTS.register("flow_saturation",() -> new TCEffectBase(MobEffectCategory.BENEFICIAL,0x1e8dc6));

    public static void register(IEventBus eventBus){
        MOD_EFFECTS.register(eventBus);
    }
}
