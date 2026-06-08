package hub;

import org.graphicsEditor.global.GStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// 언어 설정 화면 — 선택 시 GStrings.setLang() 호출 → 전체 UI 자동 갱신
public class GLanguageScreen extends GBaseScreen {

    // attributes
    private static final String[][] LANGS = {
        {"ko", "한국어",  "Korean"},
        {"en", "English", "영어 / English"},
        {"ja", "日本語",  "일본어 / Japanese"}
    };

    // components
    private JLabel  titleLabel;
    private JButton backBtn;

    // constructors
    public GLanguageScreen(GModeSelector navigator) { super(navigator); }

    // methods
    @Override
    protected void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(30, 30, 30));

        // 헤더
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(40, 40, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 70)));
        header.setPreferredSize(new Dimension(0, 50));

        this.backBtn = this.makeBtn(GStrings.get("back"));
        this.backBtn.addActionListener(new BackHandler());
        this.backBtn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        header.add(this.backBtn, BorderLayout.WEST);

        this.titleLabel = new JLabel(GStrings.get("langSettings"), SwingConstants.CENTER);
        this.titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        this.titleLabel.setForeground(Color.WHITE);
        header.add(this.titleLabel, BorderLayout.CENTER);
        header.add(Box.createHorizontalStrut(80), BorderLayout.EAST);
        this.add(header, BorderLayout.NORTH);

        // 중앙 — 언어 카드 목록
        JPanel center = new JPanel(new GridLayout(LANGS.length, 1, 0, 12));
        center.setBackground(new Color(30, 30, 30));
        center.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        for (String[] lang : LANGS) center.add(this.makeLangCard(lang[0], lang[1], lang[2]));

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(30, 30, 30));
        this.add(scroll, BorderLayout.CENTER);

        GStrings.addListener(this::refreshTexts);
    }

    private void refreshTexts() {
        this.backBtn.setText(GStrings.get("back"));
        this.titleLabel.setText(GStrings.get("langSettings"));
    }

    private JPanel makeLangCard(String code, String nativeName, String desc) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(400, 55));
        card.setMaximumSize(new Dimension(400, 55));
        card.setBackground(new Color(45, 45, 45));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nativeLabel = new JLabel(nativeName);
        nativeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        nativeLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        descLabel.setForeground(new Color(180, 180, 180));

        card.add(nativeLabel, BorderLayout.WEST);
        card.add(descLabel,   BorderLayout.EAST);
        card.addMouseListener(new LangCardHandler(card, code, nativeName));
        return card;
    }

    private JButton makeBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 40, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // inner class
    private class BackHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) { GLanguageScreen.this.navigator.goBack(); }
    }

    private class LangCardHandler extends MouseAdapter {
        private final JPanel  card;
        private final String  code;
        private final String  nativeName;

        LangCardHandler(JPanel card, String code, String nativeName) {
            this.card       = card;
            this.code       = code;
            this.nativeName = nativeName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            GStrings.setLang(this.code);
            JOptionPane.showMessageDialog(GLanguageScreen.this,
                this.nativeName + " " + GStrings.get("langChanged"),
                GStrings.get("langSettings"), JOptionPane.INFORMATION_MESSAGE);
        }

        @Override public void mouseEntered(MouseEvent e) { this.card.setBackground(new Color(60, 60, 60)); }
        @Override public void mouseExited(MouseEvent e)  { this.card.setBackground(new Color(45, 45, 45)); }
    }
}