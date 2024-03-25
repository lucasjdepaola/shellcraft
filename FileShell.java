package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.io.*;
import java.nio.channels.Pipe;

/*
Every file block is going to have their own shell, when you use terminal multiplexers like tmux, terminal, etc
you essentially have a tab system where you can access multiple shells in one process. This is going to likely be
the first 3D instance of this. Whenever you want to access a different shell, place a file block, and access your
new shell that gets called.
 */
public class FileShell {
    private String shell;
    private String shellOutput = "";
    private ProcessBuilder pb;
    public FileShell(String shell) {
        this.shell = shell;
        pb = new ProcessBuilder(shell);
        pb.command().add("-c");
        pb.redirectErrorStream(true);
    }

    public String PerformCommand(String command) throws IOException {
        String fullCommand = "cd $(cat ~/shellcraftdir.txt); " + command + "; (pwd).Path > ~/shellcraftdir.txt";
        pb.command().add(fullCommand);
        Process process = pb.start();
        pb.command().removeLast();

        InputStream stream = process.getInputStream();
        String str = fromInput(stream);
        System.out.println(str);
        BitmapFont font = new BitmapFont();
        filecraft.spriteBatch.begin();
        font.draw(filecraft.spriteBatch, str, filecraft.currentFileBlock.getX(), filecraft.currentFileBlock.getY());
        // does not work yet TODO
        filecraft.spriteBatch.end();
        return "";
    }
    private String fromInput(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder out = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            out.append(line).append(System.lineSeparator());
            System.out.println(out);
        }
        return out.toString();
    }
}
