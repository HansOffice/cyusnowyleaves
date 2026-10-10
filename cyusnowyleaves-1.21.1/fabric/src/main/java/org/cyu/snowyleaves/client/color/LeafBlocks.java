package org.cyu.snowyleaves.client.color;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.Map;

public final class LeafBlocks {
    public static final Block[] SUPPORTED = {
            Blocks.SPRUCE_LEAVES,
            Blocks.OAK_LEAVES,
            Blocks.BIRCH_LEAVES,
            Blocks.JUNGLE_LEAVES,
            Blocks.ACACIA_LEAVES,
            Blocks.DARK_OAK_LEAVES,
            Blocks.MANGROVE_LEAVES
    };

    private static final Map<Block, Double> STRENGTHS = Map.ofEntries(
            Map.entry(Blocks.SPRUCE_LEAVES, 1.00),
            Map.entry(Blocks.DARK_OAK_LEAVES, 0.85),
            Map.entry(Blocks.OAK_LEAVES, 0.75),
            Map.entry(Blocks.BIRCH_LEAVES, 0.70),
            Map.entry(Blocks.ACACIA_LEAVES, 0.65),
            Map.entry(Blocks.MANGROVE_LEAVES, 0.65),
            Map.entry(Blocks.JUNGLE_LEAVES, 0.45)
    );

    private LeafBlocks() {
    }

    public static double strength(Block block) {
        return STRENGTHS.getOrDefault(block, 0.0);
    }

    public static boolean isSupported(Block block) {
        return STRENGTHS.containsKey(block);
    }
}
