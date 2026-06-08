package org.graphicsEditor.toolbar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GSideToolBar extends JPanel {

    private enum ETarget { eStroke, eFill }

    // attributes
    private ETarget  eTarget = ETarget.eStroke;
    private Color    strokeColor = Color.BLACK;
    private Color    fillColor   = null; // 기본 투명

    // components
    private final GColorPanel colorPanel;
    private final JPanel      strokeBox;
    private final JPanel      fillBox;
    private static final int  WIDTH = 150, BOX = 24;

    // associations
    private Runnable onColorChanged;

    // constructors
    public GSideToolBar() {
        this.colorPanel = new GColorPanel();
        this.strokeBox  = new JPanel();
        this.fillBox    = new JPanel();

        this.setLayout(new BorderLayout());
        this.setBackground(new Color(48, 48, 48));
        this.setPreferredSize(new Dimension(WIDTH, 0));
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 30, 30)));

        // 상단 — stroke/fill 선택 박스
        JPanel topPanel = new JPanel(null);
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new Dimension(WIDTH, 60));

        this.strokeBox.setBounds(10, 10, BOX, BOX);
        this.strokeBox.setBackground(this.strokeColor);
        this.strokeBox.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        this.fillBox.setBounds(28, 28, BOX, BOX);
        this.fillBox.setBackground(new Color(48, 48, 48)); // 투명 표시용
        this.fillBox.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 1));

        topPanel.add(this.strokeBox);
        topPanel.add(this.fillBox);

        this.add(topPanel,        BorderLayout.NORTH);
        this.add(this.colorPanel, BorderLayout.CENTER);

        // stroke 박스 클릭 → stroke 활성화
        this.strokeBox.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { selectTarget(ETarget.eStroke); }
        });
        // fill 박스 클릭 → fill 활성화
        this.fillBox.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { selectTarget(ETarget.eFill); }
        });

        // 팔레트에서 색 선택 시 → 활성화된 쪽에 적용
        this.colorPanel.setCallback(c -> {
            if (this.eTarget == ETarget.eStroke) {
                this.strokeColor = (c == null) ? Color.BLACK : c;
                this.strokeBox.setBackground(this.strokeColor);
            } else {
                this.fillColor = c; // null = 투명
                this.fillBox.setBackground(c == null ? new Color(48, 48, 48) : c);
            }
            if (this.onColorChanged != null) this.onColorChanged.run();
        });

        selectTarget(ETarget.eStroke); // 초기 활성화
    }

    // methods
    private void selectTarget(ETarget t) {
        this.eTarget = t;
        this.strokeBox.setBorder(BorderFactory.createLineBorder(
            t == ETarget.eStroke ? Color.WHITE : new Color(120, 120, 120), t == ETarget.eStroke ? 2 : 1));
        this.fillBox.setBorder(BorderFactory.createLineBorder(
            t == ETarget.eFill   ? Color.WHITE : new Color(120, 120, 120), t == ETarget.eFill   ? 2 : 1));
    }

    public Color getStrokeColor()  { return this.strokeColor; }
    public Color getFillColor()    { return this.fillColor; }
    public void  setColorCallback(GColorPanel.Callback cb) { this.colorPanel.setCallback(cb); }
    public void  setOnColorChanged(Runnable r) { this.onColorChanged = r; }
    public void  setFillAction(java.awt.event.ActionListener listener) {} // 호환성 유지
}