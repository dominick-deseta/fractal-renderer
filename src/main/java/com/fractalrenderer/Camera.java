package com.fractalrenderer;

public class Camera {
    private double left_bound;
    private double right_bound;
    private double top_bound;
    private double bottom_bound;
    private double width_unit;
    private double height_unit;
    private int screen_width;
    private int screen_height;

    public Camera(double left_bound, double right_bound, double top_bound, double bottom_bound,
                  int screen_width, int screen_height) {
        this.screen_width = screen_width;
        this.screen_height = screen_height;
        this.left_bound = left_bound;
        this.right_bound = right_bound;
        this.top_bound = top_bound;
        this.bottom_bound = bottom_bound;
        width_unit = (right_bound - left_bound) / screen_width;
        height_unit = (top_bound - bottom_bound) / screen_height;
    }

    public PointData getPoint(int x, int y) {
        return new PointData(left_bound + width_unit * x, top_bound - height_unit * y);
    }

    public void moveHorizontally(double pixels) {
        left_bound += width_unit * pixels;
        right_bound += width_unit * pixels;
    }

    public void moveVertically(double pixels) {
        top_bound += height_unit * pixels;
        bottom_bound += height_unit * pixels;
    }
}
