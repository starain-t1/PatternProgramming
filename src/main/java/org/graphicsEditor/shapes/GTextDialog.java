package org.graphicsEditor.shapes;

import javax.swing.*;
import java.awt.*;

public class GTextDialog extends JDialog {

    // components
    private final JTextArea  textArea;
    private final JComboBox<String> fontCombo;
    private final JSpinner   sizeSpinner;
    private final JCheckBox  boldCheck;
    private boolean confirmed = false;

    public GTextDialog(Frame parent) {
        super(parent, "텍스트 삽입", true);
        this.setSize(360, 280);
        this.setLocationRelativeTo(parent);
        this.setLayout(null);
        this.setResizable(false);

        // 내용 입력
        JLabel tl = new JLabel("내용");
        tl.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        tl.setBounds(20, 15, 50, 24);
        this.add(tl);

        this.textArea = new JTextArea();
        this.textArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(this.textArea);
        scroll.setBounds(20, 40, 310, 70);
        this.add(scroll);

        // 폰트
        JLabel fl = new JLabel("폰트");
        fl.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        fl.setBounds(20, 125, 50, 24);
        this.add(fl);

        String[] fonts = {"맑은 고딕", "굴림", "돋움", "Arial", "Times New Roman"};
        this.fontCombo = new JComboBox<>(fonts);
        this.fontCombo.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        this.fontCombo.setBounds(75, 125, 150, 26);
        this.add(this.fontCombo);

        // 크기
        JLabel sl = new JLabel("크기");
        sl.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        sl.setBounds(20, 165, 50, 24);
        this.add(sl);

        this.sizeSpinner = new JSpinner(new SpinnerNumberModel(20, 8, 120, 2));
        this.sizeSpinner.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        this.sizeSpinner.setBounds(75, 165, 70, 26);
        this.add(this.sizeSpinner);

        // 굵기
        this.boldCheck = new JCheckBox("굵게");
        this.boldCheck.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        this.boldCheck.setBounds(160, 165, 70, 26);
        this.add(this.boldCheck);

        // 버튼
        JButton ok     = makeBtn("확인", new Color(70, 130, 180), Color.WHITE);
        JButton cancel = makeBtn("취소", new Color(220, 220, 220), Color.BLACK);
        ok.setBounds(145, 215, 90, 34);
        cancel.setBounds(245, 215, 80, 34);
        ok.addActionListener(e -> { this.confirmed = true; this.dispose(); });
        cancel.addActionListener(e -> this.dispose());
        this.add(ok); this.add(cancel);
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        return b;
    }

    // getters
    public boolean isConfirmed() { return this.confirmed; }
    public String  getInputText()  { return this.textArea.getText().trim(); }
    public String  getInputFont()  { return (String) this.fontCombo.getSelectedItem(); }
    public int     getInputSize()  { return (int) this.sizeSpinner.getValue(); }
    public boolean getInputBold()  { return this.boldCheck.isSelected(); }
}