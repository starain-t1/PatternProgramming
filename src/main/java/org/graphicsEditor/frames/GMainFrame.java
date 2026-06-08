package org.graphicsEditor.frames;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class GMainFrame extends JFrame {
    // components
    private GMenuBar menuBar;
    private GShapeToolBar toolBar;
    private GDrawingPanel drawingPanel;
    // associations
    // ...

    // [추가] 싱글톤 인스턴스
    private static GMainFrame instance;

    public GMainFrame() {
        // [추가] 생성자 첫 줄 — native 창이 뜨기 전에 숨김
        super.setVisible(false);

        // attributes
        this.setLocation(200, 200);
        this.setSize(600, 400);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // components
        this.menuBar = new GMenuBar();
        this.setJMenuBar(menuBar);

        this.setLayout(new BorderLayout());

        this.toolBar = new GShapeToolBar();
        this.add(toolBar, BorderLayout.NORTH);

        this.drawingPanel = new GDrawingPanel();
        this.add(drawingPanel, BorderLayout.CENTER);

        this.drawingPanel.associateWith(this.toolBar);
    }

    private class TooButtonActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }

    // ── 아래부터 추가 ──────────────────────────────

    public static GMainFrame getInstance() {
        if (instance == null) instance = new GMainFrame();
        return instance;
    }

    public static GMainFrame getInstanceIfExists() {
        return instance;
    }

    public GDrawingPanel getDrawingPanel() {
        return this.drawingPanel;
    }

    public GShapeToolBar getToolBar() {
        return this.toolBar;
    }
}