package org.graphicsEditor.shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Timer; // [추가]

abstract public class GShape implements Cloneable {
    public enum EAnchor {
        eNW,
        eNN,
        eNE,
        eEE,
        eSE,
        eSS,
        eSW,
        eWW,
        eRotate,
        eMove,
    }

    private static float dashOffset = 0;
    private static Component repaintTarget;
    private static final BasicStroke HIT_STROKE = new BasicStroke(8);
    private static final Timer ANIM = new Timer(80, e -> {
        dashOffset = (dashOffset + 2) % 20;
        if (repaintTarget != null) repaintTarget.repaint();
    });

    static {
        ANIM.start();
    }

    public static void setRepaintTarget(Component c) {
        repaintTarget = c;
    }

    private boolean isSelected;

    protected Shape shape;
    private final Anchors anchors;
    private double angle = 0;

    private Color strokeColor = Color.BLACK;
    private Color fillColor = null; // null = 투명
    private float strokeWidth = 1f;

    public GShape() {
        this.isSelected = false;
        this.anchors = new Anchors();
    }

    public GShape clone() {
        try {
            GShape cloned = (GShape) super.clone();
            cloned.shape = (Shape) (((RectangularShape) this.shape).clone());
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStrokeColor(Color c) {
        this.strokeColor = c;
    }

    public void setFillColor(Color c) {
        this.fillColor = c;
    }

    public void setStrokeWidth(float w) {
        this.strokeWidth = w;
    }

    public Color getStrokeColor() {
        return this.strokeColor;
    }

    public Color getFillColor() {
        return this.fillColor;
    }

    public float getStrokeWidth() {
        return this.strokeWidth;
    }

    // getter and setter
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if (isSelected) {
            this.anchors.setPosition(this.shape.getBounds());
            this.anchors.fixPositions(this.shape.getBounds()); // [추가] 올바른 8방향 + 회전 핸들
        }
    }

    public EAnchor onShape(int x, int y) {
        EAnchor eAnchor = null;
        if (isSelected) {
            eAnchor = this.anchors.onShape(x, y);
        }
        if (eAnchor == null) {
            if (this.shape.contains(x, y)) {
                eAnchor = EAnchor.eMove;
            }
        }
        if (eAnchor == null) {
            if (HIT_STROKE.createStrokedShape(this.shape).contains(x, y)) {
                eAnchor = EAnchor.eMove;
            }
        }
        return eAnchor;
    }

    public void draw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        if (this.angle != 0) {
            Rectangle b = this.shape.getBounds();
            g2.rotate(this.angle, b.getCenterX(), b.getCenterY());
        }
        if (isSelected) {

            Stroke old = g2.getStroke();
            Color oldC = g2.getColor();
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6, 4}, dashOffset));
            g2.setColor(new Color(0, 120, 215));
            g2.draw(shape);
            g2.setStroke(old);
            g2.setColor(oldC);
            this.anchors.draw(g2);
        }

        if (this.fillColor != null) {
            g2.setColor(this.fillColor);
            g2.fill(this.shape);
        }

        g2.setStroke(new BasicStroke(this.strokeWidth));
        g2.setColor(this.strokeColor);
        g2.draw(shape);
        g2.dispose();
    }

    public void setLocation0(int x, int y) {
    }

    public void setLocation1(int x, int y) {
    }

    public void translate(int dx, int dy) {
    }

    public void scale(double sx, double sy, int px, int py) {
    }

    public Shape getTransformedShape() {
        return this.shape;
    }

    public void addAngle(double delta) {
        this.angle += delta;
    }

    public double getAngle() {
        return this.angle;
    }


    private static class Anchors {
        public int w = 15;
        public int h = 15;

        private final Ellipse2D[] anchors;

        public Anchors() {
            anchors = new Ellipse2D[EAnchor.values().length - 1];
            for (int i = 0; i < anchors.length - 1; i++) {
                this.anchors[i] = new Ellipse2D.Float();
            }
        }

        public EAnchor onShape(int x, int y) {
            for (int i = 0; i < anchors.length - 1; i++) {
                if (this.anchors[i].contains(x, y)) {
                    return EAnchor.values()[i];
                }
            }
            return null;
        }

        public void setPosition(Rectangle br) {
            int brw = br.width;
            int brh = br.height;
            this.anchors[EAnchor.eNW.ordinal()].setFrame(br.x, br.y - h, w, h);
            this.anchors[EAnchor.eNN.ordinal()].setFrame(br.x, br.y - h, w, h);
            this.anchors[EAnchor.eNE.ordinal()].setFrame(br.x, br.y - h, w, h);
            this.anchors[EAnchor.eEE.ordinal()].setFrame(br.x, br.y - h, w, h);
            this.anchors[EAnchor.eSE.ordinal()].setFrame(br.x + brw, br.y + brh - h, w, h);
            this.anchors[EAnchor.eSS.ordinal()].setFrame(br.x, br.y - h, w, h);
            this.anchors[EAnchor.eSW.ordinal()].setFrame(br.x, br.y - h, w, h);
            this.anchors[EAnchor.eWW.ordinal()].setFrame(br.x, br.y - h, w, h);
        }

        public void fixPositions(Rectangle br) {
            int hw = w / 2, hh = h / 2;
            int cx = br.x + br.width / 2, cy = br.y + br.height / 2;
            int r = br.x + br.width, b = br.y + br.height;
            int[][] pos = {
                    {br.x, br.y}, {cx, br.y}, {r, br.y}, {r, cy},
                    {r, b}, {cx, b}, {br.x, b}, {br.x, cy}
            };
            for (int i = 0; i < pos.length; i++)
                this.anchors[i].setFrame(pos[i][0] - hw, pos[i][1] - hh, w, h);
            // 회전 핸들 — 상단 중앙 위 25px
            this.anchors[EAnchor.eRotate.ordinal()] = new Ellipse2D.Float(cx - hw, br.y - 25 - h, w, h);
        }

        public void draw(Graphics2D g) {
            for (int i = 0; i < anchors.length - 1; i++) {
                g.draw(anchors[i]);
            }
            Ellipse2D rot = this.anchors[EAnchor.eRotate.ordinal()];
            if (rot != null) {
                g.setColor(new Color(0, 120, 215));
                g.fill(rot);
                g.draw(rot);
            }
        }
    }
}