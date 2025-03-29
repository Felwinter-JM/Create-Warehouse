package net.spindle.createwarehouse.block.custom;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class DrumPackagerBlock extends Block /*implements IBE<PackagerBlockEntity>, IWrenchable*/ {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LINKED = BooleanProperty.create("linked");

    public DrumPackagerBlock(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(HALF, DoubleBlockHalf.LOWER)
                        .setValue(POWERED, Boolean.FALSE)
                        .setValue(LINKED, Boolean.FALSE)

        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(HALF, POWERED, LINKED));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)) {
            return this.defaultBlockState();
        } else if (blockpos.getY() > level.getMinBuildHeight() && level.getBlockState(blockpos.below()).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER);
        } else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
        } else if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            level.setBlock(pos.below(), state.setValue(HALF, DoubleBlockHalf.LOWER), 3);
        }
    }



    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        if (explosion.interactsWithBlocks()) {
            preventDropFromLowerHalf(level, pos, state);
            preventDropFromUpperHalf(level, pos, state);
        }

        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
    }

    @Override
    public void onDestroyedByPushReaction(BlockState state, Level level, BlockPos pos, Direction pushDirection, FluidState fluid) {
        preventDropFromLowerHalf(level, pos, state);
        preventDropFromUpperHalf(level, pos, state);
        super.onDestroyedByPushReaction(state, level, pos, pushDirection, fluid);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        preventDropFromLowerHalf(level, pos, state);
        preventDropFromUpperHalf(level, pos, state);

        return super.playerWillDestroy(level, pos, state, player);
    }


    // TODO: updateShape is taking priority over most other and is causing unwanted block item drops
    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = currentPos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockstate1 = Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
            }
        }
        if (doubleblockhalf == DoubleBlockHalf.LOWER) {
            BlockPos blockpos = currentPos.above();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.UPPER) {
                BlockState blockstate1 = Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
            }
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    protected static void preventDropFromLowerHalf(Level level, BlockPos pos, BlockState state) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockstate1 = Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
            }
        }
    }
    protected static void preventDropFromUpperHalf(Level level, BlockPos pos, BlockState state) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.LOWER) {
            BlockPos blockpos = pos.above();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.UPPER) {
                BlockState blockstate1 = Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
            }
        }
    }

    // TODO: test contraption behavior with the drum packager

    // --------------------------------------------------------------------------------------------------------
    // Packager logic    currently on pause until the drum is working
//
//    @Override
//    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
//        if (AllItems.WRENCH.isIn(stack))
//            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
//        if (AllBlocks.FACTORY_GAUGE.isIn(stack))
//            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
//        if (AllBlocks.STOCK_LINK.isIn(stack) && !(state.hasProperty(LINKED) && state.getValue(LINKED)))
//            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
//        if (AllBlocks.PACKAGE_FROGPORT.isIn(stack))
//            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
//
//        if (onBlockEntityUseItemOn(level, pos, be -> {
//            if (be.heldBox.isEmpty()) {
//                if (be.animationTicks > 0)
//                    return ItemInteractionResult.SUCCESS;
//                if (PackageItem.isPackage(stack)) {
//                    if (level.isClientSide())
//                        return ItemInteractionResult.SUCCESS;
//                    if (!be.unwrapBox(stack.copy(), true))
//                        return ItemInteractionResult.SUCCESS;
//                    be.unwrapBox(stack.copy(), false);
//                    be.triggerStockCheck();
//                    stack.shrink(1);
//                    AllSoundEvents.DEPOT_PLOP.playOnServer(level, pos);
//                    if (stack.isEmpty())
//                        player.setItemInHand(hand, ItemStack.EMPTY);
//                    return ItemInteractionResult.SUCCESS;
//                }
//                return ItemInteractionResult.SUCCESS;
//            }
//            if (be.animationTicks > 0)
//                return ItemInteractionResult.SUCCESS;
//            if (!level.isClientSide()) {
//                player.getInventory()
//                        .placeItemBackInInventory(be.heldBox.copy());
//                AllSoundEvents.playItemPickup(player);
//                be.heldBox = ItemStack.EMPTY;
//                be.notifyUpdate();
//            }
//            return ItemInteractionResult.SUCCESS;
//        }).consumesAction())
//            return ItemInteractionResult.SUCCESS;
//
//        return ItemInteractionResult.SUCCESS;
//    }
//
//    @Override
//    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
//        super.onNeighborChange(state, level, pos, neighbor);
//        if (neighbor.relative(Direction.UP)
//                .equals(pos))
//            withBlockEntityDo(level, pos, PackagerBlockEntity::triggerStockCheck);
//    }
//
//    @Override
//    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
//                                boolean isMoving) {
//        if (worldIn.isClientSide)
//            return;
//        boolean previouslyPowered = state.getValue(POWERED);
//        if (previouslyPowered == worldIn.hasNeighborSignal(pos))
//            return;
//        worldIn.setBlock(pos, state.cycle(POWERED), 2);
//        if (!previouslyPowered)
//            withBlockEntityDo(worldIn, pos, PackagerBlockEntity::activate);
//    }
//
//    @Override
//    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
//        IBE.onRemove(pState, pLevel, pPos, pNewState);
//    }
//
//    @Override
//    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
//        return false;
//    }
//
//    @Override
//    public Class<PackagerBlockEntity> getBlockEntityClass() {
//        return PackagerBlockEntity.class;
//    }
//
//    @Override
//    public BlockEntityType<? extends PackagerBlockEntity> getBlockEntityType() {
//        return AllBlockEntityTypes.PACKAGER.get();
//    }
//
//    @Override
//    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
//        return false;
//    }
//
//    @Override
//    public boolean hasAnalogOutputSignal(BlockState pState) {
//        return true;
//    }
//
//    @Override
//    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
//        return getBlockEntityOptional(pLevel, pPos).map(pbe -> {
//                    boolean empty = pbe.inventory.getStackInSlot(0)
//                            .isEmpty();
//                    if (pbe.animationTicks != 0)
//                        empty = false;
//                    return empty ? 0 : 15;
//                })
//                .orElse(0);
//    }
}
