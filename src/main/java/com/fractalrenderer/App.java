package com.fractalrenderer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Stack;

public class App implements WindowListener, MouseListener, KeyListener, MouseMotionListener, MouseWheelListener {
    private boolean running;
    private BufferedImage bufferedImage;
    private final int width;
    private final int height;
    private Stack<Camera> cameraStack;
    private Camera camera;
    private int mouse_x;
    private int mouse_y;
    private Deque<Deque<PointData>> points;
    private int padding;
    private Point pan_last;
    private Point zoomBoxCorner;
    private Rectangle zoomBox;
    private double color_shift = 1;
    private double color_spread = 0.05;
    private boolean clear = false;
    private ArrayList<PointData> trail;

    public App(int width, int height, int padding) {
        this.width = width;
        this.height = height;
        this.padding = padding;
        camera = new Camera(-2, 1, 1, -1, width, height);
        cameraStack = new Stack<>();
        cameraStack.push(camera);
        trail = new ArrayList<>();
        this.points = new ArrayDeque<>();
        this.running = true;
        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int screen_y = -padding; screen_y < height + padding; screen_y++) {
            Deque<PointData> row = new ArrayDeque<>();
            for (int screen_x = -padding; screen_x < width + padding; screen_x++) {
                row.addLast(camera.getPoint(screen_x, screen_y));
            }
            points.addLast(row);
        }

        Graphics buffGraphs = bufferedImage.createGraphics();
        buffGraphs.setColor(Color.WHITE);
        buffGraphs.fillRect(0, 0, width, height);
    }

    public boolean isRunning() {
        return running;
    }

    public void draw(Graphics g) {
        handlePan();
        if (clear) {
            points = new ArrayDeque<>();
            for (int screen_y = -padding; screen_y < height + padding; screen_y++) {
                Deque<PointData> row = new ArrayDeque<>();
                for (int screen_x = -padding; screen_x < width + padding; screen_x++) {
                    row.addLast(camera.getPoint(screen_x, screen_y));
                }
                points.addLast(row);
            }
            clear = false;
        }
        int screen_y = -padding;
        for (Deque<PointData> row : points) {
            int screen_x = -padding;
            for (PointData point : row) {
                point.iterate();
                if (0 <= screen_y && screen_y < height && 0 <= screen_x && screen_x < width) {
                    if (!point.getBreached()) {
                        bufferedImage.setRGB(screen_x, screen_y, Color.BLACK.getRGB());
                    } else {
                        int iterations = point.getIterations();
                        double magic = (iterations * color_spread);
                        double R = Math.sin(magic);
                        double G = Math.sin(magic + color_shift);
                        double B = Math.sin(magic + 2*color_shift);
                        bufferedImage.setRGB(screen_x, screen_y,
                                new Color((int)(R*R*255),(int)(G*G*255),(int)(B*B*255)).getRGB());
                    }
                }
                screen_x++;
            }
            screen_y++;
        }
        PointData point = camera.getPoint(mouse_x, mouse_y);
        g.drawImage(bufferedImage, 0, 0, width, height, null);

        if (zoomBoxCorner != null) {
            g.setColor(Color.WHITE);
            int zoomBoxWidth = mouse_x - zoomBoxCorner.x;
            int zoomBoxHeight = mouse_y - zoomBoxCorner.y;
            zoomBox = new Rectangle(zoomBoxCorner.x, zoomBoxCorner.y,
                    Math.max(zoomBoxHeight * width / height, zoomBoxWidth),
                    Math.max(zoomBoxWidth * height / width, zoomBoxHeight));
            g.drawRect(zoomBox.x, zoomBox.y, zoomBox.width, zoomBox.height);
        }

//        optional zoom coordinate reading:

//        String location = "(" + String.format("%2.2e", point.getC_Real()) + ", " +
//                                String.format("%2.2e", point.getC_Imag()) + "i)";
//        FontMetrics fm = g.getFontMetrics();
//        Rectangle2D rect = fm.getStringBounds(location, g);
//        g.setColor(Color.BLACK);
//        g.fillRect(mouse_x + 20, mouse_y - fm.getAscent() + 13, (int) rect.getWidth() + 5, (int) rect.getHeight() + 4);
//        g.setColor(Color.WHITE);
//        g.drawString(location, mouse_x + 22, mouse_y + 13);
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            color_spread += 0.01;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            color_spread -= 0.01;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            color_shift += 0.05;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            color_shift -= 0.05;
        }
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && cameraStack.size() != 1) {
            cameraStack.pop();
            camera = cameraStack.peek();
            clear = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("clicked");
        if (e.getButton() == MouseEvent.BUTTON2) {
            System.out.println("Mouse coords: " + mouse_x + " " + mouse_y);
            trail = new ArrayList<>();
            PointData start = camera.getPoint(mouse_x, mouse_y);
            start.iterate();
            for (int i = 0; i < 10; i++) {
                trail.add(start);
                start.iterate();
                System.out.println(trail.get(i).getZ_Real() + " " + trail.get(i).getZ_Imag());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3 && pan_last == null) {
            zoomBoxCorner = e.getPoint();
        } else if (e.getButton() == MouseEvent.BUTTON1 && zoomBoxCorner == null){
            pan_last = e.getPoint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (zoomBoxCorner != null && zoomBox != null) {
            clear = true;
            PointData topLeftCorner = camera.getPoint(zoomBoxCorner.x, zoomBoxCorner.y);
            PointData bottomRightCorner = camera.getPoint(zoomBoxCorner.x + zoomBox.width,
                                                          zoomBoxCorner.y + zoomBox.height);
            Camera zoom = new Camera(topLeftCorner.getC_Real(), bottomRightCorner.getC_Real(),
                                     topLeftCorner.getC_Imag(), bottomRightCorner.getC_Imag(), width, height);
            cameraStack.push(zoom);
            camera = zoom;
        }
        zoomBoxCorner = null;
        zoomBox = null;
        pan_last = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        running = false;
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    private void handlePan() {
        if (pan_last == null) {
            return;
        }
        while (mouse_x < pan_last.x) {
            int screen_y = -padding;
            int screen_x = width + padding - 1;
            for (Deque<PointData> row : points) {
                row.removeFirst();
                row.addLast(camera.getPoint(screen_x, screen_y));
                screen_y++;
            }
            pan_last.x -= 1;
            camera.moveHorizontally(1);
        }
        while (mouse_x > pan_last.x) {
            int screen_y = -padding;
            int screen_x = -padding;
            for (Deque<PointData> row : points) {
                row.removeLast();
                row.addFirst(camera.getPoint(screen_x, screen_y));
                screen_y++;
            }
            pan_last.x++;
            camera.moveHorizontally(-1);
        }
        while (mouse_y > pan_last.y) {
            points.removeLast();
            Deque<PointData> row = new ArrayDeque<>();
            int screen_y = -padding;
            int screen_x = -padding;
            for (int j = 0; j < width + 2 * padding; j++) {
                row.addLast(camera.getPoint(screen_x, screen_y));
                screen_x++;
            }
            points.addFirst(row);
            pan_last.y++;
            camera.moveVertically(1);
        }
        while (mouse_y < pan_last.y) {
            points.removeFirst();
            Deque<PointData> row = new ArrayDeque<>();
            int screen_y = height + padding - 1;
            int screen_x = -padding;
            for (int j = 0; j < width + 2 * padding; j++) {
                row.addLast(camera.getPoint(screen_x, screen_y));
                screen_x++;
            }
            points.addLast(row);
            pan_last.y -= 1;
            camera.moveVertically(-1);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
