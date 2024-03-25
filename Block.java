package com.mygdx.game;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Block {

    TextureRegion region; // each block has its own texture

    private int x; // block position coordinates
    public int getX() { return x; }
    private int y;
    public int getY() { return y; }
    private int z;
    public int getZ() { return z; }
    public void setXYZ(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private int type = -1;
    public int getType() {return type;}
    public void setType(int type) { this.type = type;}
    BoundingBox box;
    ModelInstance instance;
    public FileShell shell = null; // null until initialized by fileblock, could be done better.
    public boolean displayFile = false;
    public Label label; // incase we need to render 2d shell output
    public Decal decal; // for shell drawing


    public Block(TextureRegion region, int x, int y, int z, int type) {
        this.region = region;
        this.x = x;
        this.y = y;
        this.z = z;
        instance = instance();
        filecraft.pixelcount += (16 * 16) * 4;
        filecraft.blockCount++;
        box = new BoundingBox();
        instance.calculateBoundingBox(box);
        box.mul(instance.transform);
        this.type = type;
        if(type == 0) {
            shell = new FileShell("powershell.exe"); // spawn shell
//            decal = new Decal();
//            decal.setColor(0.5f, 12, 12, 12);
            displayFile = true;
        }
    }


    /* create a box as a model (inefficient to render) */
    ModelInstance instance() {
        ModelBuilder builder = new ModelBuilder();
        long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates;
        Model model = builder.createBox(16, 16, 16, new Material(new TextureAttribute(TextureAttribute.createDiffuse(region))), attributes);
        return new ModelInstance(model, x, y, z);
    }

    void updateInstance() {
        this.instance = instance();
    }

}
