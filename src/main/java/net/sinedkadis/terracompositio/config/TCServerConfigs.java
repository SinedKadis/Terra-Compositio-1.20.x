package net.sinedkadis.terracompositio.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TCServerConfigs {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> IF_DURATION;
    public static final ModConfigSpec.ConfigValue<Double> IF_RANDOM_TICK_PER_TICK;

    public static final ModConfigSpec.ConfigValue<Integer> LAZY_UPDATE_RATE;

    public static final ModConfigSpec.ConfigValue<Integer> STRESS_UNIT_GEN;

    static {
        BUILDER.push("Infused Fertiliser");

        IF_DURATION = BUILDER.comment("How many ticks will infused fertiliser work")
                .define("Infused Fertilizer Duration", 100);
        IF_RANDOM_TICK_PER_TICK = BUILDER.comment("How many random ticks per tick will infused fertiliser do")
                .define("Infused Fertilizer random ticks per Tick", 0.5d);

        BUILDER.pop();
        BUILDER.push("CFE");

        LAZY_UPDATE_RATE = BUILDER.comment("How often will lazy update occur, 20 means 1 per second")
                .define("Lazy Tick Rate", 100);


        BUILDER.pop();
        BUILDER.push("Compatibility");

        STRESS_UNIT_GEN = BUILDER.comment("How many su cedar gearbox generated(infused version generates twice more)")
                .define("Stress Unit Generation", 2048);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
