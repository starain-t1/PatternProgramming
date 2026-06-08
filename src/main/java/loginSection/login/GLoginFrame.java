package loginSection.login;

import hub.GHubFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// 로그인 화면 — 이메일/비밀번호 + 소셜 로그인 버튼
// GSocialLoginProvider.values() 순회로 버튼 생성 → provider 추가 시 이 클래스 무수정
public class GLoginFrame extends JFrame {

    // attributes
    private static final long serialVersionUID = 1L;

    // components
    private JTextField     emailField;
    private JPasswordField pwField;

    // associations
    private final GAuthManager auth;

    // constructors
    public GLoginFrame() {
        this.auth = GLocalAuthManager.getInstance();
        this.setTitle("Null Canvas — 로그인");
        this.setSize(420, 510);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        this.getContentPane().setBackground(Color.WHITE);
        this.initUI();
        this.setVisible(true);
    }

    // methods
    private void initUI() {

        JLabel title = new JLabel("Null Canvas");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        title.setBounds(115, 28, 220, 42);
        this.add(title);

        String[] lb = {"이메일", "비밀번호"};
        int[]    lw = {60, 65};
        int[]    ly = {95, 143};
        for (int i = 0; i < lb.length; i++) {
            JLabel l = new JLabel(lb[i]);
            l.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            l.setForeground(new Color(80, 80, 80));
            l.setBounds(50, ly[i], lw[i], 26);
            this.add(l);
        }

        this.emailField = this.makeField(115, 95, 250);
        this.add(this.emailField);

        this.pwField = new JPasswordField();
        this.pwField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        this.pwField.setBounds(115, 143, 250, 28);
        this.pwField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        this.pwField.addKeyListener(new KeyHandler()); // [추가] 엔터 → 로그인
        this.add(this.pwField);

        String[]         bt = {"로그인", "회원가입"};
        int[]            bx = {50, 215};
        int[]            bw = {155, 150};
        Color[]          bg = {new Color(70, 130, 180), new Color(240, 240, 240)};
        Color[]          fg = {Color.WHITE, new Color(60, 60, 60)};
        ActionListener[] ac = {new LoginHandler(), new SignUpHandler()};
        for (int i = 0; i < bt.length; i++) {
            JButton b = this.makeBtn(bt[i], bx[i], 200, bw[i], 42, bg[i], fg[i]);
            b.addActionListener(ac[i]);
            this.add(b);
        }

        JLabel hint = new JLabel("계정이 없으면 회원가입을 눌러주세요.");
        hint.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        hint.setForeground(new Color(160, 160, 160));
        hint.setBounds(90, 255, 260, 20);
        this.add(hint);

        JSeparator sep = new JSeparator();

        sep.setBounds(50, 285, 320, 2);
        this.add(sep);

        JLabel sl = new JLabel("소셜 계정으로 시작하기", SwingConstants.CENTER);
        sl.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        sl.setForeground(new Color(160, 160, 160));
        sl.setBounds(50, 293, 320, 20);
        this.add(sl);

        int sy = 321;
        for (GSocialLoginProvider p : GSocialLoginProvider.values()) {
            JButton sb = new JButton(p.getLabel());
            sb.setBounds(50, sy, 320, 40);
            sb.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            sb.setBackground(p.getBgColor());
            sb.setForeground(p.getFgColor());
            sb.setFocusPainted(false);
            sb.setOpaque(true);
            sb.setBorder(BorderFactory.createLineBorder(p.getBorderColor(), 1));
            sb.addActionListener(ev -> p.openInBrowser());
            this.add(sb);
            sy += 48;
        }
    }

    // helpers
    private JTextField makeField(int x, int y, int w) {
        JTextField f = new JTextField();
        f.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        f.setBounds(x, y, w, 28);
        f.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return f;
    }

    private JButton makeBtn(String text, int x, int y, int w, int h, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        b.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        return b;
    }

    // inner class — handlers
    private class KeyHandler extends KeyAdapter { // [추가]
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) new LoginHandler().actionPerformed(null);
        }
    }

    private class LoginHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String em = emailField.getText().trim();
            String pw = new String(pwField.getPassword());
            if (em.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(GLoginFrame.this, "이메일과 비밀번호를 입력하세요.");
                return;
            }
            if (auth.login(em, pw)) {
                JOptionPane.showMessageDialog(GLoginFrame.this,
                    auth.getCurrentName() + "님, 환영합니다!", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                GHubFrame.getInstance().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(GLoginFrame.this, "이메일 또는 비밀번호가 올바르지 않습니다.");
                pwField.setText("");
                pwField.requestFocus();
            }
        }
    }

    private class SignUpHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            new GSignUpFrame(GLoginFrame.this, auth);
        }
    }
}