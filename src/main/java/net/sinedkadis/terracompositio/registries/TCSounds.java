package net.sinedkadis.terracompositio.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sinedkadis.terracompositio.TerraCompositio;

import java.util.Objects;

public class TCSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, TerraCompositio.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> FLOW_EVAPORATION = registerSoundEvents("flow_evaporation");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Objects.requireNonNull(ResourceLocation.tryBuild(TerraCompositio.MOD_ID, name))));
    }

    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
