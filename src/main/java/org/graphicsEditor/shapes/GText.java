package org.graphicsEditor.shapes;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GText extends GShape {

    // attributes
    private String  text      = "";
    private String  fontName  = "맑은 고딕";
    private int     fontSize  = 20;
    private boolean bold      = false;

    public GText() { super(); this.shape = new Rectangle2D.Float(); }

    // setters / getters
    public void    setText(String t)     { this.text     = t; }
    public void    setFontName(String f) { this.fontName  = f; }
    public void    setFontSize(int s)    { this.fontSize  = s; }
    public void    setBold(boolean b)    { this.bold      = b; }
    public String  getText()             { return this.text; }
    public String  getFontName()         { return this.fontName; }
    public int     getFontSize()         { return this.fontSize; }
    public boolean isBold()              { return this.bold; }

    @Override
    public void setLocation0(int x, int y) { ((Rectangle2D.Float)this.shape).setFrame(x, y, 10, 10); }

    @Override
    public void setLocation1(int x, int y) {
        Rectangle2D.Float r = (Rectangle2D.Float) this.shape;
        r.setFrame(r.x, r.y, Math.max(10, x - r.x), Math.max(10, y - r.y));
    }

    @Override
    public void translate(int dx, int dy) {
        Rectangle2D.Float r = (Rectangle2D.Float) this.shape;
        r.setFrame(r.x + dx, r.y + dy, r.width, r.height);
    }

    @Override
    public GShape clone() {
        GText t = (GText) super.clone();
        t.shape = new Rectangle2D.Float();
        Rectangle2D.Float src = (Rectangle2D.Float) this.shape;
        ((Rectangle2D.Float) t.shape).setFrame(src.x, src.y, src.width, src.height);
        t.text = this.text; t.fontName = this.fontName;
        t.fontSize = this.fontSize; t.bold = this.bold;
        return t;
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g); // 선택 앵커 처리
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int style = this.bold ? Font.BOLD : Font.PLAIN;
        g2.setFont(new Font(this.fontName, style, this.fontSize));
        g2.setColor(this.getStrokeColor());
        Rectangle2D.Float r = (Rectangle2D.Float) this.shape;
        FontMetrics fm = g2.getFontMetrics();
        // 텍스트 세로 중앙 정렬
        int ty = (int)(r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
        g2.drawString(this.text.isEmpty() ? "텍스트" : this.text, (int)r.x, ty);
        g2.dispose();
    }
}