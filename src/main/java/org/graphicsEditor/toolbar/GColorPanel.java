package org.graphicsEditor.toolbar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GColorPanel extends JPanel {

    // attributes
    private static final int COLS = 8, CELL = 18, HUE_H = 14, PREVIEW_H = 18, PAD = 3;
    private float hue = 0f;
    private Color selected = Color.BLACK;

    // associations
    private Callback callback;

    public interface Callback { void onColorSelected(Color c); }

    // constructors
    public GColorPanel() {
        this.setOpaque(false);
        MouseHandler mh = new MouseHandler();
        this.addMouseListener(mh);
        this.addMouseMotionListener(mh);
    }

    // methods
    public void setCallback(Callback cb) { this.callback = cb; }
    public Color getSelectedColor()      { return this.selected; }
    public Color getStrokeColor()        { return this.selected; }
    public Color getFillColor()          { return this.selected; }

    private int rows() {
        int paletteH = getHeight() - PAD - HUE_H - PAD - PREVIEW_H - PAD;
        return Math.max(1, paletteH / CELL);
    }

    private Color cellColor(int col, int row) {
        int rows = rows();
        if (col == 0 && row == 0) return null; // 투명
        float s = (float) col / (COLS - 1);
        float b = 1f - (float) row / (rows - 1);
        return Color.getHSBColor(this.hue, s, b);
    }

    private void pick(int x, int y) {
        int rows = rows(), paletteH = rows * CELL;
        int hy = PAD + paletteH + PAD;
        if (y >= PAD && y < PAD + paletteH) {
            int col = (x - PAD) / CELL, row = (y - PAD) / CELL;
            if (col < 0 || col >= COLS || row < 0 || row >= rows) return;
            apply(cellColor(col, row));
        } else if (y >= hy && y < hy + HUE_H) {
            this.hue = Math.max(0f, Math.min(1f, (float)(x - PAD) / (COLS * CELL)));
            apply(Color.getHSBColor(this.hue, 1f, 1f));
        }
    }

    private void apply(Color c) {
        this.selected = c; // null(투명) 그대로 유지
        if (this.callback != null) this.callback.onColorSelected(c);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getWidth() <= 0 || getHeight() <= 0) return;
        Graphics2D g2 = (Graphics2D) g;
        int rows = rows(), paletteH = rows * CELL;
        int hy = PAD + paletteH + PAD, w = COLS * CELL;

        // 팔레트 격자
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < COLS; col++) {
                int px = PAD + col * CELL, py = PAD + row * CELL;
                Color c = cellColor(col, row);
                if (c == null) {
                    g2.setColor(Color.LIGHT_GRAY); g2.fillRect(px, py, CELL/2, CELL/2); g2.fillRect(px+CELL/2, py+CELL/2, CELL/2, CELL/2);
                    g2.setColor(Color.WHITE);      g2.fillRect(px+CELL/2, py, CELL/2, CELL/2); g2.fillRect(px, py+CELL/2, CELL/2, CELL/2);
                } else { g2.setColor(c); g2.fillRect(px, py, CELL, CELL); }
                g2.setColor(new Color(0, 0, 0, 60)); g2.drawRect(px, py, CELL-1, CELL-1);
            }
        }

        // Hue 바
        for (int x = 0; x < w; x++) {
            g2.setColor(Color.getHSBColor((float) x / w, 1f, 1f));
            g2.fillRect(PAD + x, hy, 1, HUE_H);
        }
        g2.setColor(new Color(0,0,0,80)); g2.drawRect(PAD, hy, w-1, HUE_H-1);

        // 선택색 미리보기 — null(투명)이면 체크무늬
        int px = PAD, py = hy + HUE_H + PAD;
        if (this.selected != null) {
            g2.setColor(this.selected); g2.fillRect(px, py, w, PREVIEW_H);
        } else {
            g2.setColor(Color.LIGHT_GRAY); g2.fillRect(px,        py,              w/2, PREVIEW_H/2);
            g2.setColor(Color.WHITE);      g2.fillRect(px + w/2,  py,              w/2, PREVIEW_H/2);
            g2.setColor(Color.WHITE);      g2.fillRect(px,        py + PREVIEW_H/2, w/2, PREVIEW_H/2);
            g2.setColor(Color.LIGHT_GRAY); g2.fillRect(px + w/2,  py + PREVIEW_H/2, w/2, PREVIEW_H/2);
        }
        g2.setColor(Color.DARK_GRAY); g2.drawRect(px, py, w-1, PREVIEW_H-1);
    }

    // inner class
    private class MouseHandler extends MouseAdapter {
        @Override public void mouseClicked(MouseEvent e)  { pick(e.getX(), e.getY()); }
        @Override public void mouseDragged(MouseEvent e)  { pick(e.getX(), e.getY()); }
    }
}