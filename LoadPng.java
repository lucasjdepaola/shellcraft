package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;

public class LoadPng {
    /* load all regions within the block png, image is 256 x 256
     *  blocks are 16x16
     * */
    public static TextureRegion[][] loadRegions(Texture texture) {
        TextureRegion regions[][] = new TextureRegion[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) { // iterate over 16x16
                // texture, xcoord, ycoord, xlen, ylen
                // tex, x, y, wid, height
                int xcoord = j * 16;
                int ycoord = i * 16;
                regions[i][j] = new TextureRegion(texture, xcoord, ycoord, 16, 16);
            }
        }
        return regions;
    }
}