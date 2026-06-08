package org.graphicsEditor.shapes;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Vector;

public class GPolygon extends GShape {
    private final Vector<int[]> points = new Vector<>();
    private int curX, curY;

    public GPolygon() {
        this.shape = new Path2D.Double();
    }

    @Override
    public void setLocation0(int x, int y) {
        this.points.clear();
        this.points.add(new int[]{x, y});
        this.curX = x; this.curY = y;
        rebuildPath();
    }

    @Override
    public void setLocation1(int x, int y) {
        this.curX = x; this.curY = y;
        rebuildPath();
    }

    public void addVertex(int x, int y) {
        this.points.add(new int[]{x, y});
        this.curX = x; this.curY = y;
        rebuildPath();
    }

    @Override
    public void translate(int dx, int dy) {
        for (int[] p : this.points) { p[0] += dx; p[1] += dy; }
        this.curX += dx; this.curY += dy;
        rebuildPath();
    }

    @Override
    public void scale(double sx, double sy, int px, int py) {
        for (int[] p : this.points) {
            p[0] = (int)(px + (p[0] - px) * sx);
            p[1] = (int)(py + (p[1] - py) * sy);
        }
        this.curX = (int)(px + (this.curX - px) * sx);
        this.curY = (int)(py + (this.curY - py) * sy);
        rebuildPath();
    }

    private void rebuildPath() {
        Path2D.Double path = new Path2D.Double();
        if (!this.points.isEmpty()) {
            path.moveTo(this.points.get(0)[0], this.points.get(0)[1]);
            for (int i = 1; i < this.points.size(); i++)
                path.lineTo(this.points.get(i)[0], this.points.get(i)[1]);
            path.lineTo(this.curX, this.curY);
        }
        this.shape = path;
    }

    @Override
    public GShape clone() {
        GPolygon c = new GPolygon();
        for (int[] p : this.points) c.points.add(new int[]{p[0], p[1]});
        c.curX = this.curX; c.curY = this.curY;
        c.rebuildPath();
        return c;
    }
}