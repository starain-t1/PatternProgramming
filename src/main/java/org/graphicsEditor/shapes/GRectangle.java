package org.graphicsEditor.shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class GRectangle extends GShape {

    private int x0, y0; // [추가] 시작점 저장

    public GRectangle() { this.shape = new Rectangle(); }

    public void setLocation0(int x, int y) {
        this.x0 = x; this.y0 = y;
        ((Rectangle) this.shape).setFrame(x, y, 0, 0);
    }
    public void setLocation1(int x, int y) {
        int rx = Math.min(this.x0, x), ry = Math.min(this.y0, y);
        int rw = Math.abs(x - this.x0), rh = Math.abs(y - this.y0);
        ((Rectangle) this.shape).setFrame(rx, ry, rw, rh);
    }
    public void translate(int dx, int dy) {
        Rectangle r = (Rectangle) this.shape;
        r.setFrame(r.getX() + dx, r.getY() + dy, r.getWidth(), r.getHeight());
    }
}