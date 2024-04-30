package net.sinedkadis.terracompositio.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.block.ModBlocks;
import net.sinedkadis.terracompositio.block.custom.FlowCauldron;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TerraCompositio.MOD_ID);

    public static final RegistryObject<Item> PEBBLE = ITEMS.register("pebble",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_STAFF = ITEMS.register("stone_staff",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FLOW_BOTTLE = ITEMS.register("flow_bottle",
            ()-> new Item(new Item.Properties().stacksTo(1)){
                @Override
                public InteractionResult useOn(UseOnContext pContext) {
                    ItemStack itemStack = pContext.getItemInHand();
                    BlockState blockState = pContext.getLevel().getBlockState(pContext.getClickedPos());
                    int levelValue = blockState.getValue(FlowCauldron.LEVEL);
                    if (blockState.hasProperty(FlowCauldron.LEVEL)){
                        if (itemStack.getCount()==1){
                            if (levelValue !=3) {
                                pContext.getLevel().setBlock(pContext.getClickedPos(),blockState.setValue(FlowCauldron.LEVEL, levelValue + 1),1);
                                pContext.getPlayer().setItemInHand(pContext.getHand(),new ItemStack(Items.GLASS_BOTTLE));
                                pContext.getPlayer().playSound(SoundEvents.BOTTLE_EMPTY);
                                return InteractionResult.SUCCESS;
                            }
                        }else {
                            if (levelValue !=3) {
                                pContext.getLevel().setBlock(pContext.getClickedPos(),blockState.setValue(FlowCauldron.LEVEL, levelValue + 1),1);
                                if (!pContext.getPlayer().addItem(new ItemStack(Items.GLASS_BOTTLE))){
                                    pContext.getPlayer().drop(new ItemStack(Items.GLASS_BOTTLE),false);
                                }
                                pContext.getPlayer().playSound(SoundEvents.BOTTLE_EMPTY);
                                return InteractionResult.SUCCESS;
                            }
                        }

                    }else if (blockState == Blocks.CAULDRON.defaultBlockState()){
                        pContext.getLevel().setBlock(pContext.getClickedPos(), ModBlocks.FLOW_CAULDRON.get().defaultBlockState().setValue(FlowCauldron.LEVEL,1),1);
                        if (itemStack.getCount()==1){
                            pContext.getPlayer().setItemInHand(pContext.getHand(),new ItemStack(Items.GLASS_BOTTLE));
                            pContext.getPlayer().playSound(SoundEvents.BOTTLE_EMPTY);
                            return InteractionResult.SUCCESS;
                        }else {
                            if (!pContext.getPlayer().addItem(new ItemStack(Items.GLASS_BOTTLE))){
                                pContext.getPlayer().drop(new ItemStack(Items.GLASS_BOTTLE),false);
                            }
                            pContext.getPlayer().playSound(SoundEvents.BOTTLE_EMPTY);
                            return InteractionResult.SUCCESS;
                        }
                    }
                    return InteractionResult.PASS;
                }
            });
    public static final RegistryObject<Item> OAK_STAFF = ITEMS.register("oak_staff",
            ()-> new Item(new Item.Properties().stacksTo(1)));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
