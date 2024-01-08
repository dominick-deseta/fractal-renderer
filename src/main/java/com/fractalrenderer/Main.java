package com.fractalrenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public final class Main {
    public static void main(String[] args) {
        int width = 1080;
        int height = 720;
        JFrame frame = new JFrame("Fun With Fractals");
        Dimension size = new Dimension(width, height);
        frame.setMinimumSize(size);
        frame.setMaximumSize(size);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        App app = new App(width, height, 500);

        frame.addWindowListener(app);
        frame.addMouseListener(app);
        frame.addKeyListener(app);
        frame.addMouseMotionListener(app);
        frame.addMouseWheelListener(app);

        long lastFrameTimestamp = 0;
        while (app.isRunning()) {
            BufferStrategy bufferStrategy = frame.getBufferStrategy();
            if (bufferStrategy == null) {
                frame.createBufferStrategy(2);
                bufferStrategy = frame.getBufferStrategy();
            }
            Graphics g = bufferStrategy.getDrawGraphics();

            app.draw(g);

//            optional FPS counter:

//            long currFrameTimestamp = System.nanoTime();
//            long timeDelta = currFrameTimestamp - lastFrameTimestamp;
//            long fps = 1_000_000_000 / timeDelta;
//            g.setColor(Color.WHITE);
//            g.drawString(String.valueOf(fps), 20, 50);
//            lastFrameTimestamp = currFrameTimestamp;

            g.dispose();
            bufferStrategy.show();
        }

        frame.dispose();
    }
}
