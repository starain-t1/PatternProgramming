package org.graphicsEditor.frames;

import core.*;
import org.graphicsEditor.global.GConstants;
import org.graphicsEditor.shapes.GShape;
import org.graphicsEditor.shapes.GText;
import org.graphicsEditor.shapes.GTextDialog;
import org.graphicsEditor.transformer.GDrawer;
import org.graphicsEditor.transformer.GTransformer;
import org.graphicsEditor.transformer.GTranslator;
import org.graphicsEditor.toolbar.GSideToolBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class GDrawingPanel extends JPanel {
    private enum EDrawingState { eIdle, eTransforming }

    // attributes
    private EDrawingState eDrawingState;
    private BufferedImage bufferImage;
    private GTransformer  transformer;

    // components
    private final Vector<GShape> shapes;
    private final GRedo          redo;
    private final GRedoAction    redoAction;
    private final GCopy          copy;
    private final GPaste         paste;

    // associations
    private GShapeToolBar toolBar;
    private GSideToolBar  sideToolBar;

    // constructors
    public GDrawingPanel() {
        this.setBackground(Color.WHITE);
        this.eDrawingState = EDrawingState.eIdle;
        this.shapes        = new Vector<>();
        this.bufferImage   = null;
        this.transformer   = null;
        this.redo          = new GRedo();
        this.redoAction    = new GRedoAction();
        this.copy          = new GCopy();
        this.paste         = new GPaste();
        GShape.setRepaintTarget(this);
        MouseHandler mh = new MouseHandler();
        this.addMouseListener(mh);
        this.addMouseMotionListener(mh);
        this.setFocusable(true);
        setupKeyBindings();
    }

    // associations
    public void associateWith(GShapeToolBar toolBar) { this.toolBar = toolBar; }
    public void associateWithSideBar(GSideToolBar sideToolBar) {
        this.sideToolBar = sideToolBar;
        sideToolBar.setFillAction(e -> {
            Color fill = this.sideToolBar.getFillColor();
            for (GShape s : this.shapes) { if (s.isSelected()) s.setFillColor(fill); }
            redrawAll();
        });
    }

    public BufferedImage captureImage() {
        if (this.bufferImage == null) return null;
        BufferedImage c = new BufferedImage(bufferImage.getWidth(), bufferImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = c.createGraphics(); g.drawImage(bufferImage, 0, 0, null); g.dispose(); return c;
    }
    public void loadImage(BufferedImage img) { if (img != null) { this.bufferImage = img; repaint(); } }

    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (g != null) g.drawImage(this.bufferImage, 0, 0, null);
    }

    // methods
    private void prepareDrawing() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        if (bufferImage == null || bufferImage.getWidth() != getWidth() || bufferImage.getHeight() != getHeight()) {
            bufferImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bufferImage.createGraphics();
            g.setColor(getBackground()); g.fillRect(0, 0, getWidth(), getHeight()); g.dispose();
        }
    }

    private void startTransform(int x, int y) {
        this.redo.save(this.shapes);
        this.redoAction.clear();
        if (toolBar.getShapeType() == GConstants.EShapeType.eSelect) {
            startSelect(x, y);
        } else if (toolBar.getShapeType() == GConstants.EShapeType.eText) {
            startText(x, y);
        } else {
            startShape(x, y);
        }
        prepareDrawing();
    }

    private void startText(int x, int y) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        GTextDialog dlg = new GTextDialog(parent);
        dlg.setVisible(true);
        if (!dlg.isConfirmed() || dlg.getInputText().isEmpty()) return;

        GText gt = new GText();
        gt.setText(dlg.getInputText());
        gt.setFontName(dlg.getInputFont());
        gt.setFontSize(dlg.getInputSize());
        gt.setBold(dlg.getInputBold());
        if (this.sideToolBar != null) gt.setStrokeColor(this.sideToolBar.getStrokeColor());

        int estW = dlg.getInputSize() * dlg.getInputText().length();
        int estH = dlg.getInputSize() + 10;
        gt.setLocation0(x, y);
        gt.setLocation1(x + estW, y + estH);

        this.shapes.add(gt);
        gt.setSelected(true);
        redrawAll();
    }

    private void startSelect(int x, int y) {
        this.transformer = null;
        for (GShape shape : this.shapes) {
            GShape.EAnchor eAnchor = shape.onShape(x, y);
            if (eAnchor != null) {
                this.transformer = (eAnchor == GShape.EAnchor.eMove)
                    ? new GTranslator(shape) : new GDrawer(shape);
                this.transformer.start(x, y);
                for (GShape s : this.shapes) s.setSelected(false);
                shape.setSelected(true);
                break;
            }
        }
        if (this.transformer == null) for (GShape s : this.shapes) s.setSelected(false);
        redrawAll();
    }

    private void startShape(int x, int y) {
        GShape cur = toolBar.getShapeType().getShape();
        if (this.sideToolBar != null) {
            cur.setStrokeColor(this.sideToolBar.getStrokeColor());
            cur.setFillColor(this.sideToolBar.getFillColor());
        }
        cur.setStrokeWidth(hub.GSettingsScreen.getStrokeWidth());
        this.shapes.add(cur);
        this.transformer = new GDrawer(cur);
        this.transformer.start(x, y);
    }

    private void keepTransform(int x, int y) {
        if (this.transformer == null) return;
        Graphics2D g = this.bufferImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            hub.GSettingsScreen.isAntiAlias()
                ? RenderingHints.VALUE_ANTIALIAS_ON
                : RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(getBackground()); g.fillRect(0, 0, getWidth(), getHeight());

        if (hub.GSettingsScreen.isShowGrid()) drawGrid(g);
        g.setColor(getForeground());
        this.transformer.keep(x, y);
        for (GShape s : this.shapes) s.draw(g);
        g.dispose(); repaint();
    }

    private void continueDrawing(int x, int y) {
        if (!this.shapes.isEmpty()) {
            GShape last = this.shapes.get(this.shapes.size() - 1);
            if (last instanceof org.graphicsEditor.shapes.GPolygon)
                ((org.graphicsEditor.shapes.GPolygon) last).addVertex(x, y);
        }
    }

    private void finishTransform(int x, int y) {
        if (this.transformer == null) return;
        this.transformer.finish(x, y);
        for (GShape s : this.shapes) s.setSelected(false);
        this.transformer.getShape().setSelected(true);
        redrawAll();
        this.transformer = null;
    }

    // 그리드 그리기
    private void drawGrid(Graphics2D g) {
        int gap = 20;
        g.setColor(new Color(220, 220, 220));
        g.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x < getWidth();  x += gap) g.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += gap) g.drawLine(0, y, getWidth(), y);
    }

    private void setupKeyBindings() {
        InputMap  im = this.getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();
        String[]  n  = {"undo", "redo", "copy", "paste", "cut", "duplicate", "delete", "selectAll"};
        KeyStroke[] k = {
            KeyStroke.getKeyStroke(KeyEvent.VK_Z,      InputEvent.CTRL_DOWN_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_Y,      InputEvent.CTRL_DOWN_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_C,      InputEvent.CTRL_DOWN_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_V,      InputEvent.CTRL_DOWN_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_X,      InputEvent.CTRL_DOWN_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_D,      InputEvent.CTRL_DOWN_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            KeyStroke.getKeyStroke(KeyEvent.VK_A,      InputEvent.CTRL_DOWN_MASK)
        };
        AbstractAction[] a = {
            new AbstractAction() { public void actionPerformed(ActionEvent e) { undoAction(); } },
            new AbstractAction() { public void actionPerformed(ActionEvent e) { redoAction(); } },
            new AbstractAction() { public void actionPerformed(ActionEvent e) { copyAction(); } },
            new AbstractAction() { public void actionPerformed(ActionEvent e) { pasteAction(); } },
            new AbstractAction() { public void actionPerformed(ActionEvent e) { cutAction(); } },
            new AbstractAction() { public void actionPerformed(ActionEvent e) { duplicateAction(); } },
            new AbstractAction() { public void actionPerformed(ActionEvent e) { deleteAction(); } },
            new AbstractAction() { public void actionPerformed(ActionEvent e) { selectAllAction(); } }
        };
        for (int i = 0; i < n.length; i++) { im.put(k[i], n[i]); am.put(n[i], a[i]); }
    }

    public void undoAction() {
        Vector<GShape> p = this.redo.undo(); if (p == null) return;
        this.redoAction.save(this.shapes);
        this.shapes.clear(); this.shapes.addAll(p); redrawAll();
    }
    public void redoAction() {
        Vector<GShape> p = this.redoAction.redo(); if (p == null) return;
        this.redo.save(this.shapes);
        this.shapes.clear(); this.shapes.addAll(p); redrawAll();
    }
    public void copyAction()      { this.copy.execute(this.shapes); this.paste.resetPasteCount(); }
    public void pasteAction()     { this.redo.save(this.shapes); this.redoAction.clear(); this.paste.execute(this.shapes, this.copy.getClipboard()); redrawAll(); }
    public void cutAction()       { this.redo.save(this.shapes); this.redoAction.clear(); this.copy.execute(this.shapes); this.paste.resetPasteCount(); GDeleteSelected.execute(this.shapes); redrawAll(); }
    public void duplicateAction() { this.redo.save(this.shapes); this.redoAction.clear(); GDuplicate.execute(this.shapes); redrawAll(); }
    public void deleteAction()    { this.redo.save(this.shapes); this.redoAction.clear(); GDeleteSelected.execute(this.shapes); redrawAll(); }
    public void deleteAllAction() { this.redo.save(this.shapes); this.redoAction.clear(); GDeleteAll.execute(this.shapes); redrawAll(); }
    public void selectAllAction() { GSelectAll.execute(this.shapes); redrawAll(); }

    private void redrawAll() {
        prepareDrawing(); if (this.bufferImage == null) return;
        Graphics2D g = this.bufferImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            hub.GSettingsScreen.isAntiAlias()
                ? RenderingHints.VALUE_ANTIALIAS_ON
                : RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(getBackground()); g.fillRect(0, 0, getWidth(), getHeight());

        if (hub.GSettingsScreen.isShowGrid()) drawGrid(g);
        g.setColor(getForeground());
        for (GShape s : this.shapes) s.draw(g);
        g.dispose(); repaint();
    }

    // inner class
    private class MouseHandler implements MouseListener, MouseMotionListener {
        @Override public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 1) {
                if (e.getClickCount() == 1) mouseLButton1Clocked(e);
                else if (e.getClickCount() == 2) mouseLButton2Clocked(e);
            }
        }
        @Override public void mouseMoved(MouseEvent e) {
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.eNPoint)
                if (eDrawingState == EDrawingState.eTransforming) keepTransform(e.getX(), e.getY());
        }
        private void mouseLButton1Clocked(MouseEvent e) {
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.eNPoint) {
                if (eDrawingState == EDrawingState.eIdle) { startTransform(e.getX(), e.getY()); eDrawingState = EDrawingState.eTransforming; }
                else continueDrawing(e.getX(), e.getY());
            }
        }
        private void mouseLButton2Clocked(MouseEvent e) {
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.eNPoint)
                if (eDrawingState == EDrawingState.eTransforming) { finishTransform(e.getX(), e.getY()); eDrawingState = EDrawingState.eIdle; }
        }
        @Override public void mousePressed(MouseEvent e) {
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.e2Point)
                if (eDrawingState == EDrawingState.eIdle) { startTransform(e.getX(), e.getY()); eDrawingState = EDrawingState.eTransforming; }
        }
        @Override public void mouseDragged(MouseEvent e) {
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.e2Point)
                if (eDrawingState == EDrawingState.eTransforming) keepTransform(e.getX(), e.getY());
        }
        @Override public void mouseReleased(MouseEvent e) {
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.e2Point)
                if (eDrawingState == EDrawingState.eTransforming) { finishTransform(e.getX(), e.getY()); eDrawingState = EDrawingState.eIdle; }
        }
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e)  {}
    }
}