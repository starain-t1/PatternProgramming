package org.graphicsEditor.shapes;

import java.awt.geom.Path2D;

public class GTriangle extends GShape {
    private int x0, y0, x1, y1;

    public GTriangle() { this.shape = new Path2D.Double(); }

    @Override public void setLocation0(int x, int y) { this.x0=x; this.y0=y; this.x1=x; this.y1=y; rebuild(); }
    @Override public void setLocation1(int x, int y) { this.x1=x; this.y1=y; rebuild(); }
    @Override public void translate(int dx, int dy) { this.x0+=dx; this.y0+=dy; this.x1+=dx; this.y1+=dy; rebuild(); }

    private void rebuild() {
        Path2D.Double p = new Path2D.Double();
        p.moveTo((x0+x1)/2.0, y0);  // 꼭대기 중앙
        p.lineTo(x1, y1);            // 오른쪽 아래
        p.lineTo(x0, y1);            // 왼쪽 아래
        p.closePath();
        this.shape = p;
    }

    @Override public GShape clone() {
        GTriangle c = new GTriangle();
        c.x0=this.x0; c.y0=this.y0; c.x1=this.x1; c.y1=this.y1; c.rebuild(); return c;
    }
}