package net.sinedkadis.terracompositio.block.custom;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sinedkadis.terracompositio.block.ModBlocks;
//import net.sinedkadis.terracompositio.network.CSpawnParticlesPacket;
// net.sinedkadis.terracompositio.network.PacketHandler;
import net.sinedkadis.terracompositio.particle.ModParticles;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;

import java.util.Random;

import static net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL;
//TODO change enum with tag
public class WedgeBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WedgeFluidTypes> ATTACHED = EnumProperty.create("attached", WedgeFluidTypes.class);
    protected static final VoxelShape NORTH_AABB = Block.box(6.0D, 5.0D, 10.0D, 10.0D, 10.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(6.0D, 5.0D, 0.0D, 10.0D, 10.0D, 6.0D);
    protected static final VoxelShape WEST_AABB = Block.box(10.0D, 5.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 5.0D, 6.0D, 6.0D, 10.0D, 10.0D);
    private int AnimTick = 0;
    //private static final Logger LOGGER = LogUtils.getLogger();

    public WedgeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ATTACHED,WedgeFluidTypes.NONE));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            default -> EAST_AABB;
            case WEST -> WEST_AABB;
            case SOUTH -> SOUTH_AABB;
            case NORTH -> NORTH_AABB;
        };
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        Direction direction = pState.getValue(FACING);
        BlockPos blockpos = pPos.relative(direction.getOpposite());
        BlockState blockstate = pLevel.getBlockState(blockpos);
        return direction.getAxis().isHorizontal() && blockstate.isFaceSturdy(pLevel, blockpos, direction);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState pState, Direction pFacing, @NotNull BlockState pFacingState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pFacingPos) {
        return pFacing.getOpposite() == pState.getValue(FACING) && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = this.defaultBlockState().setValue(ATTACHED, WedgeFluidTypes.NONE);
        LevelReader levelreader = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Direction[] adirection = pContext.getNearestLookingDirections();

        for(Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);
                if (blockstate.canSurvive(levelreader, blockpos)) {
                    return blockstate;
                }
            }
        }

        return null;
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState pState) {
        return true;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
        if (pState.getValue(ATTACHED) != WedgeFluidTypes.NONE) {
            if (AnimTick++ > 20) {
                AnimTick = 0;
                if (pState.getValue(ATTACHED) == WedgeFluidTypes.FLOW) {
                    if (pLevel.isClientSide) {
                        generateParticles(pLevel, pPos, pState, ModParticles.FLOW_PARTICLE.get());
                    }
                }
                if (pState.getValue(ATTACHED) == WedgeFluidTypes.BIRCH) {
                    if (pLevel.isClientSide) {
                        generateParticles(pLevel, pPos, pState, ModParticles.BIRCH_JUICE_PARTICLE.get());
                    }
                }
            }
        }

    }

    @Override
    public void tick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        this.calculateState(pState, pLevel, pPos,true);
        double random = Math.random();
        //pLevel.playSound(null,pPos, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundSource.BLOCKS);
        /*pLevel.addParticle(ModParticles.FLOW_PARTICLE.get(),
                pPos.getX() + 0.5,
                ((float)(pPos.getY() + 1) - 0.6875F) - 0.0625,
                pPos.getZ() + 0.5,
                0,0,0);

         */

        //PacketHandler.sendToAllClients(new CSpawnParticlesPacket(pPos, (byte) 0));
        if (pState.getValue(ATTACHED) == WedgeFluidTypes.FLOW) {
            if (pLevel.getBlockState(pPos.below()).is(ModBlocks.FLOW_CAULDRON.get())) {
                //LOGGER.debug("Flow Cauldron detected, trying increase level");
                int levelValue = pLevel.getBlockState(pPos.below()).getValue(LEVEL);
                if (random < 0.7D) {
                    if (levelValue != 3) {
                        pLevel.setBlock(pPos.below(), ModBlocks.FLOW_CAULDRON.get().defaultBlockState().setValue(LEVEL, levelValue + 1), 2);
                        //LOGGER.debug(random + " - Success " + success);
                    } //else LOGGER.debug(random + " - Fail");
                }
            } else if (pLevel.getBlockState(pPos.below()).is(Blocks.CAULDRON)) {
                //LOGGER.debug("Vanilla Cauldron detected, trying increase level");
                if (random < 0.7D) {
                    pLevel.setBlock(pPos.below(), ModBlocks.FLOW_CAULDRON.get().defaultBlockState().setValue(LEVEL, 1), 2);
                    //LOGGER.debug(random + " - Success " + success);
                } //else LOGGER.debug(random + " - Fail");
            }

        }
        if (pState.getValue(ATTACHED) == WedgeFluidTypes.BIRCH) {
            if (pLevel.getBlockState(pPos.below()).is(ModBlocks.BIRCH_JUICE_CAULDRON.get())) {
                //LOGGER.debug("Flow Cauldron detected, trying increase level");
                int levelValue = pLevel.getBlockState(pPos.below()).getValue(LEVEL);
                if (random < 0.7D) {
                    if (levelValue != 3) {
                        pLevel.setBlock(pPos.below(), ModBlocks.BIRCH_JUICE_CAULDRON.get().defaultBlockState().setValue(LEVEL, levelValue + 1), 2);
                        //LOGGER.debug(random + " - Success " + success);
                    } //else LOGGER.debug(random + " - Fail");
                }
            } else if (pLevel.getBlockState(pPos.below()).is(Blocks.CAULDRON)) {
                //LOGGER.debug("Vanilla Cauldron detected, trying increase level");
                if (random < 0.7D) {
                    pLevel.setBlock(pPos.below(), ModBlocks.BIRCH_JUICE_CAULDRON.get().defaultBlockState().setValue(LEVEL, 1), 2);
                    //LOGGER.debug(random + " - Success " + success);
                } //else LOGGER.debug(random + " - Fail");
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    private void generateParticles(Level pLevel, BlockPos pPos, BlockState pState, ParticleOptions particle) {
        //for (int i = 0; i < 5; i++) {
        float x;
        float z;
        switch (pState.getValue(FACING)){
            case NORTH -> {
                x = 0.5f;
                z = 0.7f;
            }
            case SOUTH -> {
                x = 0.5f;
                z = 0.3f;
            }
            case EAST -> {
                x = 0.3f;
                z = 0.5f;
            }
            case WEST -> {
                x = 0.7f;
                z = 0.5f;
            }
            default -> {
                x = 52;
                z = 52;
            }
        }
            pLevel.addParticle(particle,
                    pPos.getX() + x,
                    pPos.getY()+0.4f,
                    pPos.getZ() + z,
                    0,0,0);
        //}
    }

    @Override
    public void setPlacedBy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @org.jetbrains.annotations.Nullable LivingEntity pPlacer, @NotNull ItemStack pStack) {
        this.calculateState(pState,pLevel,pPos,false);
    }

    public void calculateState(BlockState pState, Level pLevel, BlockPos pPos, boolean pShouldNotifyNeighbours) {
        for (Block block : AllowAttaching(WedgeFluidTypes.FLOW)) {
            if (pLevel.getBlockState(pPos.relative(pState.getValue(FACING).getOpposite())).is(block)
                    && pState.getValue(ATTACHED) != WedgeFluidTypes.FLOW) {
                pLevel.setBlock(pPos, this.defaultBlockState().setValue(FACING, pState.getValue(FACING)).setValue(ATTACHED, WedgeFluidTypes.FLOW), 1);
                if (pShouldNotifyNeighbours) {
                    this.notifyNeighbors(pLevel, pPos, Direction.DOWN);
                }
                return;
            } else if (pState.getValue(ATTACHED) == WedgeFluidTypes.FLOW
                    && !(pLevel.getBlockState(pPos.relative(pState.getValue(FACING).getOpposite())).is(block))){
                pLevel.setBlock(pPos, this.defaultBlockState().setValue(FACING, pState.getValue(FACING)).setValue(ATTACHED, WedgeFluidTypes.NONE), 1);
            }
        }
        for (Block block : AllowAttaching(WedgeFluidTypes.BIRCH)) {
            if (pLevel.getBlockState(pPos.relative(pState.getValue(FACING).getOpposite())).is(block)
                    && pState.getValue(ATTACHED) != WedgeFluidTypes.BIRCH) {
                pLevel.setBlock(pPos, this.defaultBlockState().setValue(FACING, pState.getValue(FACING)).setValue(ATTACHED, WedgeFluidTypes.BIRCH), 1);
                if (pShouldNotifyNeighbours) {
                    this.notifyNeighbors(pLevel, pPos, Direction.DOWN);
                }
                return;
            } else if (pState.getValue(ATTACHED) == WedgeFluidTypes.BIRCH
                    && !(pLevel.getBlockState(pPos.relative(pState.getValue(FACING).getOpposite())).is(block))){
                pLevel.setBlock(pPos, this.defaultBlockState().setValue(FACING, pState.getValue(FACING)).setValue(ATTACHED, WedgeFluidTypes.NONE), 1);
            }
        }
    }
    protected Block[] AllowAttaching(WedgeFluidTypes pType){
        return switch (pType){
            case FLOW -> new Block[]{
                        ModBlocks.FLOW_LOG.get(),
                        ModBlocks.FLOW_WOOD.get(),
                        ModBlocks.FLOW_PORT.get()
                };
            case BIRCH -> new Block[]{
                    Blocks.BIRCH_LOG,
                    Blocks.BIRCH_WOOD
            };
            default -> null;
        };
    }

    private void notifyNeighbors(Level pLevel, BlockPos pPos, Direction pDirection) {
        pLevel.updateNeighborsAt(pPos, this);
        pLevel.updateNeighborsAt(pPos.relative(pDirection), this);
    }

    @Override
    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, ATTACHED);
    }

    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pIsMoving) {
        if (!pIsMoving && !pState.is(pNewState.getBlock())) {
            //if (pState.getValue(ATTACHED)) {
            //    this.calculateState(pState,pLevel, pPos, false);
            //}
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }
}
