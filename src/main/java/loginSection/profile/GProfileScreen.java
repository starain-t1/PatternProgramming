package loginSection.profile;

import hub.*;
import loginSection.login.GAuthManager;
import loginSection.login.GLocalAuthManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GProfileScreen extends GBaseScreen {

    // components
    private JLabel            workCountLabel;
    private JTextField        nameField, ageField;
    private JComboBox<String> genderBox;

    // associations
    private final GLocalAuthManager auth;  // getAge/getGender/incrementWorkCount 때문에 구체 타입 유지

    // constructors
    public GProfileScreen(GModeSelector navigator) {
        super(navigator);
        this.auth = GLocalAuthManager.getInstance();
    }

    // methods
    @Override
    protected void initUI() {
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        // 제목
        JLabel title = new JLabel("프로필", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        title.setBounds(0, 20, 420, 40);
        this.add(title);

        // 작업물 수
        this.workCountLabel = new JLabel("내 작업물: 0개", SwingConstants.CENTER);
        this.workCountLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        this.workCountLabel.setForeground(new Color(100, 100, 100));
        this.workCountLabel.setBounds(0, 65, 420, 24);
        this.add(this.workCountLabel);

        JSeparator sep = new JSeparator();
        sep.setBounds(50, 98, 320, 2);
        this.add(sep);

        // 레이블 + 필드
        String[] lb = {"이름", "나이", "성별"};
        int[]    ly = {115, 160, 205};
        for (int i = 0; i < lb.length; i++) {
            JLabel l = new JLabel(lb[i]);
            l.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            l.setBounds(50, ly[i], 60, 26);
            this.add(l);
        }

        this.nameField = new JTextField();
        this.nameField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        this.nameField.setBounds(115, ly[0], 250, 28);
        this.nameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        this.add(this.nameField);

        this.ageField = new JTextField();
        this.ageField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        this.ageField.setBounds(115, ly[1], 250, 28);
        this.ageField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        this.add(this.ageField);

        this.genderBox = new JComboBox<>(new String[]{"선택 안함", "남성", "여성"});
        this.genderBox.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        this.genderBox.setBounds(115, ly[2], 250, 28);
        this.add(this.genderBox);

        // 버튼 — 취소 / 내 작업물 / 저장
        String[]         bt = {"취소", "내 작업물", "저장"};
        int[]            bx = {50, 160, 270};
        Color[]          bg = {new Color(240, 240, 240), new Color(50, 50, 80), new Color(70, 130, 180)};
        Color[]          fg = {new Color(60, 60, 60), Color.WHITE, Color.WHITE};
        ActionListener[] ac = {
            e -> this.navigator.goBack(),
            e -> this.navigator.navigateTo(GModeSelector.EMode.eGallery),
            new SaveHandler()
        };
        for (int i = 0; i < bt.length; i++) {
            JButton b = new JButton(bt[i]);
            b.setBounds(bx[i], 265, 100, 36);
            b.setFont(new Font("맑은 고딕", Font.BOLD, 13));
            b.setBackground(bg[i]); b.setForeground(fg[i]);
            b.setFocusPainted(false); b.setBorderPainted(false);
            b.addActionListener(ac[i]);
            this.add(b);
        }
    }

    @Override
    public void onEnter() {
        this.nameField.setText(this.auth.getCurrentName());
        this.ageField.setText(this.auth.getAge());
        this.workCountLabel.setText("내 작업물: " + this.auth.getWorkCount() + "개");
        String g = this.auth.getGender();
        this.genderBox.setSelectedItem(g.isEmpty() ? "선택 안함" : g);
    }

    // inner class
    private class SaveHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = GProfileScreen.this.nameField.getText().trim();
            if (name.length() < 2 || name.length() > 20) {
                JOptionPane.showMessageDialog(GProfileScreen.this, "이름: 2~20자"); return;
            }
            GProfileScreen.this.auth.updateName(name);
            GProfileScreen.this.auth.updateAge(GProfileScreen.this.ageField.getText().trim());
            String g = (String) GProfileScreen.this.genderBox.getSelectedItem();
            GProfileScreen.this.auth.updateGender("선택 안함".equals(g) ? "" : g);
            JOptionPane.showMessageDialog(GProfileScreen.this, "저장 완료");
            GProfileScreen.this.navigator.goBack();
        }
    }
}