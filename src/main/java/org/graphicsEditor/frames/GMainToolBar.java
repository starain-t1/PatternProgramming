package org.graphicsEditor.frames;

import org.graphicsEditor.global.GConstants;
import org.graphicsEditor.global.GStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GMainToolBar extends GShapeToolBar {

    // components
    private JLabel  shapeLabel;
    private JButton langBtn;
    private JButton settingsBtn;
    private JButton homeBtn;
    private JButton textBtn; // [추가]

    // constructors
    public GMainToolBar() {
        this.removeAll();
        this.setBackground(new Color(40, 40, 40));
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 8));
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 70)));
        this.setFloatable(false);

        // 도형 드롭다운
        JComboBox<String> shapeCombo = this.createShapeCombo();
        JPanel shapePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        shapePanel.setOpaque(false);
        this.shapeLabel = this.createLabel(GStrings.get("shape"));
        shapePanel.add(this.shapeLabel);
        shapePanel.add(shapeCombo);
        this.add(shapePanel);

        this.textBtn     = this.createToolBtn(GStrings.get("text"));     // [추가]
        this.langBtn     = this.createToolBtn(GStrings.get("language"));
        this.settingsBtn = this.createToolBtn(GStrings.get("settings"));
        this.homeBtn     = this.createToolBtn(GStrings.get("home"));

        this.add(this.textBtn);
        this.add(this.langBtn);
        this.add(this.settingsBtn);
        this.add(this.homeBtn);

        this.textBtn.addActionListener(new TextBtnHandler());

        GStrings.addListener(this::refreshTexts);
    }

    // setters
    public void setHomeAction(ActionListener listener)     { this.homeBtn.addActionListener(listener); }
    public void setLanguageAction(ActionListener listener)  { this.langBtn.addActionListener(listener); }
    public void setSettingsAction(ActionListener listener)  { this.settingsBtn.addActionListener(listener); }

    // methods
    private void refreshTexts() {
        this.shapeLabel.setText(GStrings.get("shape"));
        this.textBtn.setText(GStrings.get("text"));          // [추가]
        this.langBtn.setText(GStrings.get("language"));
        this.settingsBtn.setText(GStrings.get("settings"));
        this.homeBtn.setText(GStrings.get("home"));
    }

    private JComboBox<String> createShapeCombo() {
        GConstants.EShapeType[] types = GConstants.EShapeType.values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) names[i] = types[i].getName();
        JComboBox<String> combo = new JComboBox<>(names);
        combo.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        combo.setBackground(new Color(60, 60, 60));
        combo.setForeground(Color.WHITE);
        combo.setFocusable(false);
        combo.setPreferredSize(new Dimension(100, 26));
        combo.addActionListener(new ShapeComboHandler(combo));
        return combo;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton createToolBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 40, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new HoverHandler(btn));
        return btn;
    }

    // inner class
    private class TextBtnHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GMainToolBar.this.setShapeType(GConstants.EShapeType.eText);
        }
    }

    private class ShapeComboHandler implements ActionListener {
        private final JComboBox<String> combo;
        ShapeComboHandler(JComboBox<String> combo) { this.combo = combo; }

        @Override
        public void actionPerformed(ActionEvent e) {
            int idx = this.combo.getSelectedIndex();
            GMainToolBar.this.setShapeType(GConstants.EShapeType.values()[idx]);
        }
    }

    private static class HoverHandler extends MouseAdapter {
        private final JButton btn;
        HoverHandler(JButton btn) { this.btn = btn; }

        @Override public void mouseEntered(MouseEvent e) { this.btn.setOpaque(true);  this.btn.setBackground(new Color(70, 70, 70)); }
        @Override public void mouseExited(MouseEvent e)  { this.btn.setOpaque(false); this.btn.setBackground(new Color(40, 40, 40)); }
    }
}