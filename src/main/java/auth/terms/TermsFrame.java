package auth.terms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Desktop;
import java.util.Arrays;
import java.util.List;

public class TermsFrame extends JFrame {

    // attributes
    private static final int   W      = 400;
    private static final int   H      = 700;
    private static final Color ACCENT = new Color(100, 180, 160);
    private static final Color GRAY   = new Color(200, 200, 200);
    private static final Color MUTED  = new Color(150, 150, 150);

    private static final String[] HTML_FILES = {
        "/Terms_Service.html",
        "/Terms_Privacy.html",
        "/Terms_Marketing.html",
    };

    private static class TermItem {
        final String  label;
        final boolean required;
        boolean       checked;
        TermItem(String label, boolean required) { this.label = label; this.required = required; this.checked = false; }
    }

    // components
    private final List<TermItem> terms = Arrays.asList(
        new TermItem("(필수) 서비스 이용약관",          true),
        new TermItem("(필수) 개인정보 수집/이용 동의",   true),
        new TermItem("(선택) 마케팅 수신 동의",          false)
    );
    private final JCheckBox   allCheck    = new JCheckBox("약관 전체 동의");
    private final JCheckBox[] checkBoxes  = new JCheckBox[3];
    private final JButton     nextButton  = new JButton("다음");

    // associations
    private final Runnable onFinished;

    // constructors
    public TermsFrame(Runnable onFinished) {
        this.onFinished = onFinished;

        this.setUndecorated(true);
        this.setSize(W, H);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        for (int i = 0; i < this.terms.size(); i++) this.checkBoxes[i] = new JCheckBox(this.terms.get(i).label);

        this.setContentPane(this.buildMainPanel());
        this.getRootPane().setDefaultButton(this.nextButton); // [추가] 엔터 → 다음 (비활성 시 무반응)
        this.setVisible(true);
    }

    // methods
    private JPanel buildMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // 상단 타이틀
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(30, 20, 10, 20));
        JLabel titleLabel = new JLabel("서비스 이용 동의");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        root.add(topPanel, BorderLayout.NORTH);

        // 중앙 약관 목록
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        this.styleCheckBox(this.allCheck, true);
        this.allCheck.addActionListener(e -> {
            boolean selected = this.allCheck.isSelected();
            for (int i = 0; i < this.checkBoxes.length; i++) {
                this.checkBoxes[i].setSelected(selected);
                this.terms.get(i).checked = selected;
            }
            this.updateNextButton();
        });
        centerPanel.add(this.allCheck);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(220, 220, 220));
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(sep);
        centerPanel.add(Box.createVerticalStrut(8));

        for (int i = 0; i < this.terms.size(); i++) {
            final int idx = i;
            JCheckBox cb = this.checkBoxes[i];
            this.styleCheckBox(cb, false);
            cb.addActionListener(e -> {
                this.terms.get(idx).checked = cb.isSelected();
                boolean allSelected = true;
                for (JCheckBox c : this.checkBoxes) if (!c.isSelected()) { allSelected = false; break; }
                this.allCheck.setSelected(allSelected);
                this.updateNextButton();
            });

            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            row.add(cb, BorderLayout.CENTER);

            JButton detailBtn = new JButton("상세보기 >");
            detailBtn.setBorderPainted(false);
            detailBtn.setContentAreaFilled(false);
            detailBtn.setForeground(MUTED);
            detailBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            detailBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            detailBtn.addActionListener(e -> this.openHtmlDetail(idx));
            row.add(detailBtn, BorderLayout.EAST);

            centerPanel.add(row);
        }
        root.add(centerPanel, BorderLayout.CENTER);

        // 하단 버튼
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(0, 20, 30, 20));
        this.nextButton.setPreferredSize(new Dimension(360, 50));
        this.nextButton.setBackground(GRAY);
        this.nextButton.setForeground(Color.WHITE);
        this.nextButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        this.nextButton.setBorderPainted(false);
        this.nextButton.setFocusPainted(false);
        this.nextButton.setOpaque(true);
        this.nextButton.setEnabled(false);
        this.nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.nextButton.addActionListener(e -> this.finish());
        bottomPanel.add(this.nextButton);
        root.add(bottomPanel, BorderLayout.SOUTH);

        return root;
    }

    private void styleCheckBox(JCheckBox cb, boolean bold) {
        cb.setBackground(Color.WHITE);
        cb.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 14));
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void updateNextButton() {
        boolean ok = true;
        for (int i = 0; i < this.terms.size(); i++)
            if (this.terms.get(i).required && !this.checkBoxes[i].isSelected()) { ok = false; break; }
        this.nextButton.setEnabled(ok);
        this.nextButton.setBackground(ok ? ACCENT : GRAY);
    }

    private void openHtmlDetail(int index) {
        try {
            java.net.URL url = this.getClass().getResource(HTML_FILES[index]);
            if (url == null) {
                JOptionPane.showMessageDialog(this, "약관 파일을 찾을 수 없습니다: " + HTML_FILES[index]
                    + "\n\nsrc/main/resources/ 폴더에 HTML 파일이 있는지 확인해 주세요.", "파일 없음", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Desktop.getDesktop().browse(url.toURI());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "브라우저를 열 수 없습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finish() {
        this.dispose();
        SwingUtilities.invokeLater(this.onFinished);
    }
}