package org.graphicsEditor.menus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

public class GHelpMenu extends JMenu {

    public GHelpMenu() {
        super("Help");

        JMenuItem readmeItem = new JMenuItem("Readme");
        readmeItem.addActionListener(new ReadmeHandler());
        this.add(readmeItem);

        this.addSeparator();

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(new AboutHandler());
        this.add(aboutItem);
    }

    // inner class
    private class ReadmeHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextArea text = new JTextArea(
                "Null Canvas - Java Swing Graphics Editor\n\n" +
                "도형 그리기, 선택, 이동, 크기 조절, 회전을 지원합니다.\n" +
                "File > 열기/저장으로 PNG 파일을 불러오거나 저장할 수 있습니다.\n\n" +
                "단축키:\n" +
                "  Ctrl+Z  : 실행 취소\n" +
                "  Ctrl+A  : 전체 선택\n" +
                "  Ctrl+O  : 파일 열기\n" +
                "  Ctrl+S  : 저장"
            );
            text.setEditable(false);
            text.setFont(new Font("Monospaced", Font.PLAIN, 12));
            text.setBackground(UIManager.getColor("Panel.background"));
            JScrollPane scroll = new JScrollPane(text);
            scroll.setPreferredSize(new Dimension(380, 220));
            JOptionPane.showMessageDialog(
                null, scroll, "Readme", JOptionPane.PLAIN_MESSAGE
            );
        }
    }

    private class AboutHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new AboutDialog().setVisible(true);
        }
    }

    // About 팝업 다이얼로그
    private static class AboutDialog extends JDialog {
        AboutDialog() {
            super((Frame) null, "About Null Canvas", true);
            this.setResizable(false);
            this.setLayout(new BorderLayout());

            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));

            String[][] lines = {
                {"plain",  "Null Canvas v1.0.3"},
                {"plain",  "Java Swing Graphics Editor"},
                {"spacer", ""},
                {"link",   "Authors & Credits"},
                {"spacer", ""},
                {"sep",    ""},
                {"plain",  "Copyright (C) 2025  Myongji Univ."},
                {"link",   "https://github.com/starain-t1"}
            };

            for (String[] line : lines) {
                switch (line[0]) {
                    case "plain"  -> content.add(makeLabel(line[1], false));
                    case "link"   -> content.add(makeLabel(line[1], true));
                    case "spacer" -> content.add(Box.createVerticalStrut(6));
                    case "sep"    -> {
                        JSeparator sep = new JSeparator();
                        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                        content.add(Box.createVerticalStrut(4));
                        content.add(sep);
                        content.add(Box.createVerticalStrut(4));
                    }
                }
            }

            JButton closeBtn = new JButton("Close");
            closeBtn.addActionListener(ev -> AboutDialog.this.dispose());
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnPanel.add(closeBtn);

            this.add(content, BorderLayout.CENTER);
            this.add(btnPanel, BorderLayout.SOUTH);
            this.pack();
            this.setMinimumSize(new Dimension(280, 0));
            this.setLocationRelativeTo(null);
        }

        private static JLabel makeLabel(String text, boolean isLink) {
            JLabel label = new JLabel(text);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            if (isLink) {
                label.setForeground(new Color(70, 130, 200));
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            return label;
        }
    }
}