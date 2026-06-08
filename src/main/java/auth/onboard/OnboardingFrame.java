package auth.onboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OnboardingFrame extends JFrame {

    private static final String[][] PAGES = {
        {"🎨", "빈 화면, 무한한 가능성.", "Null Canvas에 오신 것을 환영합니다.<br>아무것도 없는 곳에서 모든 것이 시작됩니다.<br>당신의 첫 번째 획을 그어보세요."},
        {"✏️", "당신의 상상력을<br>자유롭게 그려보세요.", "형태, 색깔, 구도 — 모든 선택이 당신의 것입니다.<br>제한 없는 캔버스 위에서<br>오직 당신만의 작품을 완성해보세요."},
        {"🚀", "이제, 시작해볼까요?", "당신의 첫 번째 캔버스가 기다리고 있습니다.<br>로그인하고 창작의 세계로 뛰어드세요."}
    };

    // attributes
    private int currentPage = 0;
    private Runnable onFinished;

    // components
    private JLabel  iconLabel;
    private JLabel  titleLabel;
    private JLabel  descLabel;
    private JPanel  dotPanel;
    private JButton nextButton;

    // constructors
    public OnboardingFrame(Runnable onFinished) {
        this.onFinished = onFinished;

        this.setUndecorated(true);
        this.setSize(400, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // 상단 건너뛰기
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(Color.WHITE);
        JButton skipButton = new JButton("건너뛰기");
        skipButton.setBorderPainted(false);
        skipButton.setContentAreaFilled(false);
        skipButton.setForeground(new Color(150, 150, 150));
        skipButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { finish(); }
        });
        topPanel.add(skipButton);
        root.add(topPanel, BorderLayout.NORTH);

        // 중앙 콘텐츠
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 20, 30));

        this.iconLabel = new JLabel("", SwingConstants.CENTER);
        this.iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 80));
        this.iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.iconLabel.setPreferredSize(new Dimension(340, 220));
        this.iconLabel.setMaximumSize(new Dimension(340, 220));
        this.iconLabel.setOpaque(true);
        this.iconLabel.setBackground(new Color(230, 230, 230));
        centerPanel.add(this.iconLabel);
        centerPanel.add(Box.createVerticalStrut(30));

        this.titleLabel = new JLabel("", SwingConstants.CENTER);
        this.titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        this.titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(this.titleLabel);
        centerPanel.add(Box.createVerticalStrut(16));

        this.descLabel = new JLabel("", SwingConstants.CENTER);
        this.descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        this.descLabel.setForeground(new Color(100, 100, 100));
        this.descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(this.descLabel);

        root.add(centerPanel, BorderLayout.CENTER);

        // 하단
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        this.dotPanel = new JPanel();
        this.dotPanel.setBackground(Color.WHITE);
        this.dotPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(this.dotPanel);
        bottomPanel.add(Box.createVerticalStrut(20));

        this.nextButton = new JButton("다음");
        this.nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.nextButton.setMaximumSize(new Dimension(340, 50));
        this.nextButton.setPreferredSize(new Dimension(340, 50));
        this.nextButton.setBackground(new Color(100, 180, 160));
        this.nextButton.setForeground(Color.WHITE);
        this.nextButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        this.nextButton.setBorderPainted(false);
        this.nextButton.setFocusPainted(false);
        this.nextButton.setOpaque(true);
        this.nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { onNextClicked(); }
        });
        bottomPanel.add(this.nextButton);

        root.add(bottomPanel, BorderLayout.SOUTH);

        this.setContentPane(root);
        updatePage();
        this.getRootPane().setDefaultButton(this.nextButton); // [추가] 엔터 → 다음/시작하기
        this.setVisible(true);
    }

    // methods
    private void updatePage() {
        String[] page = PAGES[this.currentPage];
        this.iconLabel.setText(page[0]);
        this.titleLabel.setText("<html><div style='text-align:center'>" + page[1] + "</div></html>");
        this.descLabel.setText("<html><div style='text-align:center'>" + page[2] + "</div></html>");

        this.dotPanel.removeAll();
        for (int i = 0; i < PAGES.length; i++) {
            JLabel dot = new JLabel("●");
            dot.setFont(new Font("SansSerif", Font.PLAIN, 12));
            dot.setForeground(i == this.currentPage ? new Color(100, 180, 160) : new Color(200, 200, 200));
            this.dotPanel.add(dot);
        }
        this.dotPanel.revalidate();
        this.dotPanel.repaint();

        this.nextButton.setText(this.currentPage == PAGES.length - 1 ? "시작하기" : "다음");
    }

    private void onNextClicked() {
        if (this.currentPage < PAGES.length - 1) { this.currentPage++; updatePage(); }
        else finish();
    }

    private void finish() {
        this.dispose();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { onFinished.run(); }
        });
    }
}