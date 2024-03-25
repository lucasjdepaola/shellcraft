package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import java.util.ArrayList;

public class Player {
    private Chunk[][] chunkArr;
    public ArrayList<int[]> minecoordsList;
    public static Vector3 intersection = new Vector3();
    public int[] minecoords;
    public static int[] least;
    public Block lookingAt;
    private Vector3 tmp = new Vector3();
    private Ray ray;
    private Block tmpblock;
    private Block lsBlock;
    private Vector3 tmp1 = new Vector3(); // incase using both

    public Player(Chunks chunks) {
        this.chunkArr = chunks.getChunks();
        minecoords = new int[5];
        least = new int[5];
        minecoordsList = new ArrayList<>();
    }

    /* raycast for determining what block to mine */
    public boolean Mine() {
        Vector3 position = filecraft.camera.position;
        float dst = 10000f;
        minecoordsList.clear(); // clear coordinate list
        ray = new Ray(new Vector3(filecraft.camera.position), new Vector3(filecraft.camera.direction));
        for (int i = 0; i < chunkArr.length; i++) {
            for (int j = 0; j < chunkArr[i].length; j++) {
                for (int k = 0; k < chunkArr[i][j].blocks.length; k++) {
                    for (int l = 0; l < chunkArr[i][j].blocks[k].length; l++) {
                        for (int m = 0; m < chunkArr[i][j].blocks[k][l].length; m++) {
                            if (chunkArr[i][j].blocks[k][l][m] == null) continue;
                            tmpblock = chunkArr[i][j].blocks[k][l][m];
                            BoundingBox box = tmpblock.box;
                            if (Intersector.intersectRayBounds(ray, box, tmp)) {
                                tmp1.set(tmpblock.getX(), tmpblock.getY(), tmpblock.getZ());
                                float distance = position.dst(tmp1);
                                if (distance < dst) {
                                    least[0] = i;
                                    least[1] = j;
                                    least[2] = k;
                                    least[3] = l;
                                    least[4] = m;
                                    intersection.set(tmp);
                                    dst = distance;
                                    lsBlock = tmpblock;
                                }
                                minecoords[0] = i;
                                minecoords[1] = j;
                                minecoords[2] = k;
                                minecoords[3] = l;
                                minecoords[4] = m;
                                minecoordsList.add(new int[]{i, j, k, l, m});
                                if (intersection.len() > tmp.len()) {
                                    //pseudo intersection
                                    intersection.set(tmp);
                                }
                            }
                        }
                    }
                }
            }
        }
        filecraft.currentBlock = lsBlock;
        if (!minecoordsList.isEmpty())
            drawFrame(findClosestCoord(minecoordsList, chunkArr), chunkArr);
        return !minecoordsList.isEmpty();
    }

    public boolean playerCollides() {
        for (Chunk[] c : chunkArr) {
            for (Chunk ch : c) {
                for (Block[][] b : ch.blocks) {
                    for (Block[] bl : b) {
                        for (Block block : bl) {
                            if (block == null) continue;
                            tmp.set(block.getX(), block.getY(), block.getZ());
                            Vector3 campos = filecraft.camera.position;
                            float xzdst = Math.abs(campos.x - tmp.x) + Math.abs(campos.z - tmp.z);
                            float ydst = Math.abs(filecraft.camera.position.y - block.getY());
                            if (xzdst <= 16 && ydst <= 32) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void applyGravity(float velocity) {
        // move down maybe
        float deltaTime = Gdx.graphics.getDeltaTime();
        tmp1.set(filecraft.camera.up).nor().scl(-deltaTime * velocity);
        filecraft.camera.position.add(tmp1);
    }

    private void drawFrame(int[] coord, Chunk[][] chunkArr) {
    }

    private int[] findClosestCoord(ArrayList<int[]> coords, Chunk[][] chunks) {
        Vector3 position = filecraft.camera.position;
        int[] lst = null;
        float dst = 10000f;
        for (int[] coord : coords) {
            if (chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]] == null) continue;
            Vector3 blockpos = new Vector3(chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]].getX(), chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]].getY(), chunks[coord[0]][coord[1]].blocks[coord[2]][coord[3]][coord[4]].getZ());
            float distance = position.dst(blockpos);
            if (distance < dst) {
                lst = coord;
                dst = distance;
            }
        }
        return lst;
    }

}