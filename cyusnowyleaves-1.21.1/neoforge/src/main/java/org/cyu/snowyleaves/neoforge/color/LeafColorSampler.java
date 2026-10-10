package org.cyu.snowyleaves.neoforge.color;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class LeafColorSampler {
    private LeafColorSampler() {
    }

    public static int originalColor(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        Block block = state.getBlock();
        if (block == Blocks.SPRUCE_LEAVES) {
            return 0x619961;
        }
        if (block == Blocks.BIRCH_LEAVES) {
            return 0x80A755;
        }
        if (level != null && pos != null) {
            return BiomeColors.getAverageFoliageColor(level, pos);
        }
        return 0x48B518;
    }
}
