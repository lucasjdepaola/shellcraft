package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Chunks {
    public static final int CHUNKARRSIZE = 4;
    private Chunk[][] chunks;
    private Block tmpBlock;
    private Vector3 tmp = new Vector3();

    public Chunk[][] getChunks() {
        return chunks;
    }

    public Chunks(Chunk[][] chunks) {
        this.chunks = chunks;
    }

    public void fillChunks(Block[][][] blocks) {
        System.out.println("chunks length is, " + chunks.length);
        for (int i = 0; i < chunks.length; i++) {
            for (int j = 0; j < chunks[i].length; j++) {
                chunks[i][j] = new Chunk(blocks, i, j);
            }
        }
    }

    public void modifyBlock(ArrayList<int[]> coords, TextureRegion region) {
        int[] coord = findClosestCoord(coords);
        chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]].region = region;
        chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]].updateInstance();
    }

    /* place a block like in minecraft */
    public void placeBlock(ArrayList<int[]> coords, TextureRegion region, int type) throws IOException {
        int[] coord = findClosestCoord(coords);
        int[] closest = Player.least;
        setTmpBlock(closest);
        if(tmpBlock.getType() == 0) {
            /* this is the fileblock case, do not place anything, instead enter right click actionable state */
            // COMMAND HERE
//            String out = EnterFileBlock("ls /");
//            System.out.println(out);
        }
        tmp.set(Player.intersection).add(-tmpBlock.getX(), -tmpBlock.getY(), -tmpBlock.getZ());
        System.out.println(tmp + " is new vec");

        if (Math.abs(tmp.x) > Math.abs(tmp.y) && Math.abs(tmp.x) > Math.abs(tmp.z)) {
            if (tmp.x > 0) {
                System.out.println("x greater than zero");
                if (coord[2] + 1 < Chunk.CHUNKSIZE) // maybe check height bound too
                    chunks[coord[0]][coord[1]].blocks[coord[2] + 1][coord[3]][coord[4]] = new Block(region, tmpBlock.getX() + 16, tmpBlock.getY(), tmpBlock.getZ(), type);
                else
                    chunks[coord[0]][coord[1] + 1].blocks[coord[2]][coord[3]][coord[4]] = new Block(region, tmpBlock.getX() + 16, tmpBlock.getY(), tmpBlock.getZ(), type);
            } else {
                System.out.println("x less than zero");
                if (coord[2] - 1 >= 0) // maybe check height bound too
                    chunks[coord[0]][coord[1]].blocks[coord[2] - 1][coord[3]][coord[4]] = new Block(region, tmpBlock.getX() - 16, tmpBlock.getY(), tmpBlock.getZ(), type);
                else {
                    if(coord[1] - 1 < 0) {
                        // maybe not right im not sure
                    } else {
                        chunks[coord[0]][coord[1] - 1].blocks[0][coord[3]][coord[4]] = new Block(region, tmpBlock.getX() - 16, tmpBlock.getY(), tmpBlock.getZ(), type);
                    }
                    // TODO fix when this case also does not work, aka the actual chunk array is out of bounds
                }
            }
        } else if (Math.abs(tmp.y) > Math.abs(tmp.x) && Math.abs(tmp.y) > Math.abs(tmp.z)) {
            if (tmp.y > 0) {
                System.out.println("y greater than zero");
                if (coord[3] + 1 >= 0) // maybe check height bound too
                    chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3] + 1][coord[4]] = new Block(region, tmpBlock.getX(), tmpBlock.getY() + 16, tmpBlock.getZ(), type);
            } else {
                if (coord[3] - 1 >= 0)
                    chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3] - 1][coord[4]] = new Block(region, tmpBlock.getX(), tmpBlock.getY() - 16, tmpBlock.getZ(), type);

            }
        } else if (Math.abs(tmp.z) > Math.abs(tmp.y) && Math.abs(tmp.z) > Math.abs(tmp.x)) {
            if (tmp.z > 0) {
                System.out.println("z greater than zero");
                if (coord[4] + 1 < Chunk.CHUNKSIZE) // maybe check height bound too
                    chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4] + 1] = new Block(region, tmpBlock.getX(), tmpBlock.getY(), tmpBlock.getZ() + 16, type);
                else // go one chunk forward
                    chunks[coord[0]][coord[1] + 1].blocks[coord[2]][coord[3]][0] = new Block(region, tmpBlock.getX(), tmpBlock.getY(), tmpBlock.getZ() + 16, type);
            } else {
                if (coord[4] - 1 >= 0) // maybe check height bound too
                    chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4] - 1] = new Block(region, tmpBlock.getX(), tmpBlock.getY(), tmpBlock.getZ() - 16, type);
            }
        }
        // TODO find x y z of closest block
    }

    public Block getBlock(int[] coord) {
        return chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]];
    }

    private String EnterFileBlock(String command) throws IOException {
        // handle file block case here
        return tmpBlock.shell.PerformCommand(command);
    }

    private void setTmpBlock(int[] coord) {
        tmpBlock = chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]];
    }

    public void deleteBlock(ArrayList<int[]> coords) {
        int[] coord = findClosestCoord(coords);
        System.out.println(chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]].box);
        chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]] = null;
    }

    private int[] findClosestCoord(ArrayList<int[]> coords) {
        Vector3 position = filecraft.camera.position;
        int[] least = null;
        float dst = 10000f;
        for (int[] coord : coords) {
            if (chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]] == null) continue;
            Vector3 blockpos = new Vector3(chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]].getX(), chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]].getY(), chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]].getZ());
            float distance = position.dst(blockpos);
            if (distance < dst) {
                least = coord;
                dst = distance;
            }
        }
        System.out.println("the closest coordinate mined was: " + Arrays.toString(least));
        return least;
    }
}