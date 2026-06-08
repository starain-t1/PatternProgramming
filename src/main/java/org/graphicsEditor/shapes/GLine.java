package org.graphicsEditor.shapes;

import java.awt.*;
import java.awt.geom.Line2D;

public class GLine extends GShape {

    public GLine() {
        this.shape = new Line2D.Double();
    }

    @Override
    public void setLocation0(int x, int y) {
        ((Line2D.Double) this.shape).setLine(x, y, x, y);
    }

    @Override
    public void setLocation1(int x, int y) {
        Line2D.Double l = (Line2D.Double) this.shape;
        l.setLine(l.x1, l.y1, x, y);
    }

    @Override
    public GShape clone() {
        GLine c = new GLine();
        Line2D.Double o = (Line2D.Double) this.shape;
        ((Line2D.Double) c.shape).setLine(o.x1, o.y1, o.x2, o.y2);
        return c;
    }
}