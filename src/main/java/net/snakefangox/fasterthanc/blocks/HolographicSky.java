package net.snakefangox.fasterthanc.blocks;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.snakefangox.fasterthanc.FRegister;

public class HolographicSky extends BlockWithEntity {
    public static class BE extends BlockEntity {
        public BE() {
            super(FRegister.holographic_sky_type);
        }
    }

    public HolographicSky(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BE();
    }
}
