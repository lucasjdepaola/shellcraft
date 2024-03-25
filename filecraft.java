package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;

import java.io.IOException;
import java.util.Arrays;

public class filecraft extends ApplicationAdapter {
    public static int pixelcount = 0;
    public static int blockCount = 0;
    public static int chunkCount = 0;
    public static TextureRegion[][] regions;
    private TextureRegion selectedPlaceRegion;
    private int selectedPlaceRegionType = -1;
    private Block[][][] blocks;
    private Chunks chunks;
    private InputHandler handler;
    private InputMultiplexer multiplexer;
    private ModelBatch batch;
    private Player player;
    public static PerspectiveCamera camera;
    private FirstPersonCameraController controller;
    private Texture texture;
    public static Block currentBlock; // current file block
    public static Block currentFileBlock;
    public static boolean displayFile = false;
    public static SpriteBatch spriteBatch;
    public static DecalBatch decalBatch;


    @Override
    public void create() {
        initCameraAndController(); // set camera settings
        texture = new Texture(Gdx.files.internal("filecraft.png"));
        regions = LoadPng.loadRegions(texture);
        selectedPlaceRegion = regions[0][0]; // first region to start off with
        batch = new ModelBatch();
        batch.getRenderContext().setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        blocks = Chunk.initChunk(regions[2][0], 1); // stone
        chunks = new Chunks(new Chunk[Chunks.CHUNKARRSIZE][Chunks.CHUNKARRSIZE]); // 16 by 16 chunks
        chunks.fillChunks(blocks);
        System.out.println("Pixel count: " + pixelcount + ", block count: " + blockCount + ", chunk count: " + chunkCount);
        player = new Player(chunks);
        spriteBatch = new SpriteBatch();
        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClearColor(135f / 255, 206f / 255, 235f / 255, 1f);
//        rgb(135, 206, 235)
        /* delete this later*/
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        try {
            handleEvents();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        camera.update();
        controller.update();

        batch.begin(camera);
        renderSoleChunks();
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
    }

    private void initCameraAndController() {
        initCursor();
        handler = new InputHandler();
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 32 * 32f;
        camera.position.set(248, 500, 0);
        camera.update();
        controller = new FirstPersonCameraController(camera);
        controller.setDegreesPerPixel(2);

        multiplexer = new InputMultiplexer();
        multiplexer.setProcessors(controller, handler);
        Gdx.input.setInputProcessor(multiplexer);
        controller.setVelocity(200);
        controller.upKey = Input.Keys.SPACE;
        controller.forwardKey = Input.Keys.APOSTROPHE; // tmp
        controller.backwardKey = Input.Keys.COMMA; // tmp
        controller.update();
    }

    private void handleEvents() throws IOException {
        if (player.Mine()) {
            if (handler.leftMouse) {
                System.out.println(camera.position);
                chunks.deleteBlock(player.minecoordsList);
                handler.leftMouse = false;
            } else if (handler.rightMouse) {
                // this could be the case for openfile/shell
                System.out.println(Arrays.toString(Player.least));
                currentBlock = chunks.getBlock(Player.least);
                System.out.println(currentBlock.displayFile);
                if(currentBlock.displayFile) {
//                    currentBlock.shell.PerformCommand("echo 'hello world'");
//                    decalBatch.add(currentBlock.decal);
                    displayFile = true; // turn on file state
                    currentFileBlock = currentBlock;
                } else {
                    System.out.println(currentBlock.getType());
                    chunks.placeBlock(player.minecoordsList, selectedPlaceRegion, selectedPlaceRegionType); // place file editor block
                }
                handler.rightMouse = false;
            } else if (handler.middlemouse) {
                chunks.modifyBlock(player.minecoordsList, regions[0][0]);
                handler.middlemouse = false;
            }
        }
        if (!player.playerCollides()) {
            player.applyGravity(200);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            handler.forwards(100);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            handler.backwards(100);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (handler.currentUp == 0) handler.currentUp = InputHandler.upCount;
            handler.up(1000);
        }
        if (handler.leftMouse || handler.rightMouse) {
            handler.rightMouse = false;
            handler.leftMouse = false;
        }
        if (handler.escape) {
            // handle potential menu
            System.out.println("escape key sequence");
            Gdx.input.setCursorCatched(false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            System.out.println("up key");
            Gdx.app.exit();
        }
        if (handler.zoom) {
            handler.zoom = false;
        }
        handleNumbers();
    }

    private void handleNumbers() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            selectedPlaceRegion = regions[0][0]; // dirt block
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            selectedPlaceRegion = regions[0][1]; // stone block
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            selectedPlaceRegion = regions[1][0]; // file block
            selectedPlaceRegionType = 0;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            selectedPlaceRegion = regions[1][1]; // plank block
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            selectedPlaceRegion = regions[0][2]; // oak block
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            selectedPlaceRegion = regions[1][2]; // black block
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
            selectedPlaceRegion = regions[2][0]; // diamond block
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
            selectedPlaceRegion = regions[1][1]; // some other block
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT)) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    private void initCursor() {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        Cursor cursor = Gdx.graphics.newCursor(pixmap, 16, 16);
        pixmap.dispose();
        Gdx.graphics.setCursor(cursor);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    }


    private void renderSoleChunks() {
        for (Chunk[] c : chunks.getChunks()) {
            for (Chunk chunk : c) {
                for (Block[][] b : chunk.blocks) {
                    for (Block[] bl : b) {
                        for (Block block : bl) {
                            if (block != null)
                                batch.render(block.instance);
                        }
                    }
                }
            }
        }
    }

}
