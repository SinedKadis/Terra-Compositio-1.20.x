package net.sinedkadis.terracompositio.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.effect.custom.TCEffectBase;
import net.sinedkadis.terracompositio.util.accessors.PlayerKnowledgeAccessor;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TCEffects {
    public static final DeferredRegister<MobEffect> MOD_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, TerraCompositio.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> FLOW_SATURATION =
            MOD_EFFECTS.register("flow_saturation",() -> new TCEffectBase(MobEffectCategory.BENEFICIAL,0x1e8dc6));
    public static final DeferredHolder<MobEffect, MobEffect> CREATION_KNOWLEDGE =
            MOD_EFFECTS.register("creation_knowledge", () -> new TCEffectBase(MobEffectCategory.BENEFICIAL, 0x1e8dc6) {
                @Override
                public boolean isInstantenous() {
                    return true;
                }

                @Override
                public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
                    if (pLivingEntity instanceof PlayerKnowledgeAccessor accessor) {
                        accessor.setCreationKnowledge(true);
                        return true;

                    }
                    return false;
                }
            });
    public static final DeferredHolder<MobEffect, MobEffect> IGNORANCE =
            MOD_EFFECTS.register("ignorance", () -> new TCEffectBase(MobEffectCategory.BENEFICIAL, 0x1e8dc6) {
                @Override
                public boolean isInstantenous() {
                    return true;
                }

                @Override
                public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
                    if (pLivingEntity instanceof PlayerKnowledgeAccessor accessor) {
                        accessor.setCreationKnowledge(false);
                        return true
                    }
                    return false;
                }
            });
    public static void register(IEventBus eventBus){
        MOD_EFFECTS.register(eventBus);
    }
}
