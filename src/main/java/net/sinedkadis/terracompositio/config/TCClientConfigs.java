package net.sinedkadis.terracompositio.config;

import net.minecraft.core.Direction;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec;

public class TCClientConfigs {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final IConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Double> ECF_RENDER_MULTIPLIER;

    public static final ModConfigSpec.ConfigValue<Integer> OVERLAY_X_OFFSET;
    public static final ModConfigSpec.ConfigValue<Integer> OVERLAY_Y_OFFSET;
    public static final ModConfigSpec.EnumValue<Direction> OVERLAY_FADE_DIR;
    public static final ModConfigSpec.EnumValue<Corner> OVERLAY_ANCHOR_CORNER;

    public static final ModConfigSpec.ConfigValue<Boolean> APPLE_PP_ENDPOINTS;
    public static final ModConfigSpec.ConfigValue<Boolean> APPLE_RANGE_CIRCLE;
    public static final ModConfigSpec.ConfigValue<Boolean> APPLE_ITEM_TOOLTIP;
    public static final ModConfigSpec.EnumValue<AppleMode> APPLE_SHOW_MODE;




    static {
        BUILDER.push("Particles");

        ECF_RENDER_MULTIPLIER = BUILDER.comment("How many ecf particles per CFE will be rendered")
                .define("CFE Particle multiplier", 0.1d);

        BUILDER.pop();

        BUILDER.push("Apple of Knowledge");

        APPLE_SHOW_MODE = BUILDER.comment("Normal - some info always, some on shift\nAlways - all info without shift\nOn Shift - all info on shift")
                .defineEnum("Apple Mode", AppleMode.NORMAL);
        APPLE_PP_ENDPOINTS= BUILDER.comment("Show Particles of Path Pointers endpoints when holding wrench")
                .define("Path Pointer Particles", true);
        APPLE_ITEM_TOOLTIP = BUILDER.comment("Show item's ECF info in tooltip")
                .define("Item Tooltip", true);
        APPLE_RANGE_CIRCLE= BUILDER.comment("Show range circle while holding shift")
                .define("Range circle", true);


        BUILDER.push("Knowledge Overlay");

        OVERLAY_X_OFFSET = BUILDER.comment("Left-Right shift")
                .define("X Offset", 0);
        OVERLAY_Y_OFFSET = BUILDER.comment("Up-Down shift")
                .define("Y Offset", 0);
        OVERLAY_FADE_DIR = BUILDER.comment("Side it fades From. North - no alpha fade, South - just alpha fade, West - left, East - right")
                .defineEnum("Fade Direction", Direction.EAST);
        OVERLAY_ANCHOR_CORNER = BUILDER.comment("Fixed overlay corner")
                .defineEnum("Anchor corner", Corner.RIGHT_UP);

        BUILDER.pop();
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public enum Corner {
        LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN
    }
    public enum AppleMode {
        NORMAL,ALWAYS,SHIFT
    }
}
