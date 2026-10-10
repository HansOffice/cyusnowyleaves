package org.cyu.snowyleaves.client.color;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public final class LeafColorSampler {
    private LeafColorSampler() {
    }

    public static int originalColor(BlockState state, BlockRenderView view, BlockPos pos) {
        Block block = state.getBlock();
        if (block == Blocks.SPRUCE_LEAVES) {
            return 0x619961;
        }
        if (block == Blocks.BIRCH_LEAVES) {
            return 0x80A755;
        }
        if (view != null && pos != null) {
            return BiomeColors.getFoliageColor(view, pos);
        }
        return 0x48B518;
    }
}
