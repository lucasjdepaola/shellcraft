package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Chunk {
    Block[][][] blocks;
    public static final int CHUNKSIZE = 8;
    public static final int CHUNKHEIGHT = 256;
    public static final int HEIGHTFILL = 4; // only fill 4 chunks inside of this base, fill entire x-z though
    private int x;
    private int y;

    public Chunk(Block[][][] blocks, int x, int y) {
        this.x = x;
        this.y = y;
        this.blocks = copy(blocks);
        filecraft.chunkCount++;
    }

    private Block[][][] copy(Block[][][] blocks) {
        Block[][][] copy = new Block[CHUNKSIZE][CHUNKHEIGHT][CHUNKSIZE];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                for (int k = 0; k < blocks[i][j].length; k++) {
                    if (checkNull(blocks[i][j][k])) continue;
                    copy[i][j][k] = new Block(blocks[i][j][k].region, blocks[i][j][k].getX() + (128 * x), blocks[i][j][k].getY(), blocks[i][j][k].getZ() + (128 * y), blocks[i][j][k].getType());
                    copy[i][j][k].updateInstance();
                }
            }
        }
        return copy;
    }

    public static boolean checkNull(Block block) {
        return block == null;
    }

    public static Block[][][] initChunk(TextureRegion region, int type) {
        /* make the block arr CHUNKSIZE, but only fill it with the desired amount, not entirely */
        Block[][][] initblocks = new Block[Chunk.CHUNKSIZE][Chunk.CHUNKSIZE][Chunk.CHUNKSIZE];
        for (int i = 0; i < CHUNKSIZE; i++) {
            for (int j = 0; j < HEIGHTFILL; j++) {
                for (int k = 0; k < CHUNKSIZE; k++) {
                    initblocks[i][j][k] = new Block(region, i * 16, j * 16, k * 16, type);
                }
            }
        }
        return initblocks;
    }
}