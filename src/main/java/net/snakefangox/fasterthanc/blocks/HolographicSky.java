package net.snakefangox.fasterthanc.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.snakefangox.fasterthanc.FRegister;

public class HolographicSky extends BlockWithEntity {
    public static class BE extends BlockEntity implements Tickable {
        public BE() {
            super(FRegister.holographic_sky_type);
        }

        @Environment(EnvType.CLIENT)
        public boolean shouldDrawSide(Direction direction) {
            if (getWorld() != null) {
                return Block.shouldDrawSide(getCachedState(), getWorld(), getPos(), direction);
            } else {
                return true;
            }
        }

        @Override
        public void tick() {
            if (getWorld() != null && !getWorld().isClient()) {
                BlockState state = getWorld().getBlockState(getPos());
                int oldLevel = state.get(LIGHT_LEVEL);
                int newLevel = getWorld().getLightLevel(LightType.SKY, new BlockPos(getPos().getX(), 1024, getPos().getZ())) - getWorld().getAmbientDarkness();
                if (oldLevel != newLevel) {
                    getWorld().setBlockState(getPos(), state.with(LIGHT_LEVEL, newLevel));
                }
            }
        }
    }

    private static final IntProperty LIGHT_LEVEL = IntProperty.of("light_level", 0, 15);

    public HolographicSky(Settings settings) {
        super(settings.lightLevel(state -> state.get(LIGHT_LEVEL)));
        setDefaultState(getDefaultState().with(LIGHT_LEVEL, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIGHT_LEVEL);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BE();
    }
}
