package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import java.io.IOException;

public class InputHandler implements InputProcessor {
    public boolean leftMouse = false;
    public boolean rightMouse = false;
    public boolean escape = false;
    public boolean middlemouse = false;
    public boolean zoom = false;
    public boolean jump = false;
    private Vector3 tmp = new Vector3();
    public static final int upCount = 60 * 2; // 20 frames to jump
    public int currentUp = upCount;
    private String command = "";

    public InputHandler() {
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            escape = true;
        } else if (keycode == Input.Keys.Z) {
            zoom = true;
        } else if (keycode == Input.Keys.SPACE) {
            System.out.println("space has been hit");
            jump = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            System.out.println("this somehow works");
            jump = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if(filecraft.displayFile) {
            System.out.println("display file state");
            if(character == '\n') {
                System.out.println("enter case");
                try {
                    filecraft.currentFileBlock.shell.PerformCommand(command);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                command = "";
            } else command += character;
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            leftMouse = true;
        } else if (button == Input.Buttons.RIGHT) {
            rightMouse = true;
        } else if (button == Input.Buttons.MIDDLE) {
            middlemouse = true;
        }
        return true;
    }

    private float getDelta() {
        return Gdx.graphics.getDeltaTime();
    }

    public void forwards(float velocity) {
        if(filecraft.displayFile) return;
        tmp.set(filecraft.camera.direction).nor();//.scl(getDelta() * velocity);
        tmp.y = 0;
        tmp.scl(getDelta() * velocity);
        filecraft.camera.position.add(tmp);
    }

    public void backwards(float velocity) {
        if(filecraft.displayFile) return;
        tmp.set(filecraft.camera.direction).nor();//.scl(getDelta() * velocity);
        tmp.y = 0;
        tmp.scl(-getDelta() * velocity);
        filecraft.camera.position.add(tmp);
    }

    public void down(float velocity) {
        tmp.set(filecraft.camera.up).nor().scl(getDelta() * velocity);
        filecraft.camera.position.add(tmp);
    }

    public void up(float velocity) {
        if(filecraft.displayFile) return;
        if (currentUp <= 0) {
            jump = false;
            return;
        }
        tmp.set(filecraft.camera.up).nor().scl(getDelta() * velocity);
        filecraft.camera.position.add(tmp);
        currentUp--; // decrement jump frames
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }


    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        float deltaX = -Gdx.input.getDeltaX() * 2;
        float deltaY = -Gdx.input.getDeltaY() * 2;
        filecraft.camera.direction.rotate(filecraft.camera.up, deltaX);
        tmp.set(filecraft.camera.direction).crs(filecraft.camera.up).nor();
        filecraft.camera.direction.rotate(tmp, deltaY);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}