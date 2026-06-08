package loginSection.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GSignUpFrame extends JFrame {

    // attributes
    private static final long serialVersionUID = 1L;

    // components
    private JTextField     nameField, emailField;
    private JPasswordField pwField;

    // associations
    private final GLoginFrame  loginFrame;
    private final GAuthManager auth;

    // constructors
    public GSignUpFrame(GLoginFrame loginFrame, GAuthManager auth) {
        this.loginFrame = loginFrame;
        this.auth       = auth;
        this.setTitle("Null Canvas — 회원가입");
        this.setSize(420, 360);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        this.getContentPane().setBackground(Color.WHITE);
        this.addWindowListener(new CloseHandler());
        this.initUI();
        this.setVisible(true);
    }

    // methods
    private void initUI() {
        JLabel title = new JLabel("회원가입");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        title.setBounds(145, 25, 160, 40);
        this.add(title);

        String[] lb = {"닉네임", "이메일", "비밀번호"};
        int[]    lw = {60, 60, 65};
        int[]    ly = {90, 136, 182};
        JTextField[] fields = new JTextField[3];
        for (int i = 0; i < lb.length; i++) {
            JLabel l = new JLabel(lb[i]);
            l.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            l.setForeground(new Color(80, 80, 80));
            l.setBounds(50, ly[i], lw[i], 26);
            this.add(l);
            fields[i] = (i == 2) ? new JPasswordField() : new JTextField();
            fields[i].setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            fields[i].setBounds(115, ly[i], 250, 28);
            fields[i].setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            this.add(fields[i]);
        }
        this.nameField  = fields[0];
        this.emailField = fields[1];
        this.pwField    = (JPasswordField) fields[2];

        // 가입하기 / 취소 버튼
        String[]         bt = {"가입하기", "취소"};
        int[]            bx = {50, 215};
        int[]            bw = {155, 150};
        Color[]          bg = {new Color(70, 130, 180), new Color(240, 240, 240)};
        Color[]          fg = {Color.WHITE, new Color(60, 60, 60)};
        ActionListener[] ac = {new SignUpHandler(), new CancelHandler()};
        for (int i = 0; i < bt.length; i++) {
            JButton b = new JButton(bt[i]);
            b.setBounds(bx[i], 240, bw[i], 42);
            b.setFont(new Font("맑은 고딕", Font.BOLD, 15));
            b.setBackground(bg[i]); b.setForeground(fg[i]);
            b.setFocusPainted(false); b.setBorderPainted(false);
            b.addActionListener(ac[i]);
            this.add(b);
        }
    }

    // inner class — handlers
    private class SignUpHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = GSignUpFrame.this.nameField.getText().trim();
            String em   = GSignUpFrame.this.emailField.getText().trim();
            String pw   = new String(GSignUpFrame.this.pwField.getPassword());
            if (name.isEmpty() || em.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(GSignUpFrame.this, "모든 항목을 입력하세요."); return;
            }
            if (GSignUpFrame.this.auth.signup(name, em, pw)) {
                JOptionPane.showMessageDialog(GSignUpFrame.this, "가입이 완료되었습니다!", "회원가입 성공", JOptionPane.INFORMATION_MESSAGE);
                GSignUpFrame.this.dispose();
                GSignUpFrame.this.loginFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(GSignUpFrame.this, "이미 사용 중인 이메일입니다.");
            }
        }
    }

    private class CancelHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GSignUpFrame.this.dispose();
            GSignUpFrame.this.loginFrame.setVisible(true);
        }
    }

    private class CloseHandler extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            GSignUpFrame.this.dispose();
            GSignUpFrame.this.loginFrame.setVisible(true);
        }
    }
}