package hub;

import org.graphicsEditor.global.GStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GSettingsScreen extends GBaseScreen {

    // attributes
    private static boolean antiAlias   = true;
    private static boolean showGrid    = false;
    private static int     strokeWidth = 1;

    // components
    private JLabel    titleLabel, renderLabel, strokeLabel;
    private JButton   backBtn, resetBtn;
    private JCheckBox antiAliasBox, gridBox;
    private JSlider   strokeSlider;
    private JLabel    strokeValLabel;

    // constructors
    public GSettingsScreen(GModeSelector navigator) { super(navigator); }

    // methods — static getters
    public static boolean isAntiAlias()    { return antiAlias; }
    public static boolean isShowGrid()     { return showGrid; }
    public static int     getStrokeWidth() { return strokeWidth; }

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
        this.backBtn.addActionListener(e -> this.navigator.goBack());
        this.backBtn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        header.add(this.backBtn, BorderLayout.WEST);

        this.titleLabel = new JLabel(GStrings.get("settingsTitle"), SwingConstants.CENTER);
        this.titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        this.titleLabel.setForeground(Color.WHITE);
        header.add(this.titleLabel, BorderLayout.CENTER);

        header.add(Box.createHorizontalStrut(80), BorderLayout.EAST);
        this.add(header, BorderLayout.NORTH);

        // 본문
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(new Color(30, 30, 30));
        body.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        this.renderLabel = this.sectionLabel(GStrings.get("rendering"));
        body.add(this.renderLabel);
        this.antiAliasBox = this.makeCheck(GStrings.get("antiAlias"), antiAlias);
        this.antiAliasBox.addActionListener(e -> antiAlias = this.antiAliasBox.isSelected());
        body.add(this.antiAliasBox);
        body.add(Box.createVerticalStrut(8));

        this.gridBox = this.makeCheck(GStrings.get("showGrid"), showGrid);
        this.gridBox.addActionListener(e -> showGrid = this.gridBox.isSelected());
        body.add(this.gridBox);
        body.add(Box.createVerticalStrut(24));

        this.strokeLabel = this.sectionLabel(GStrings.get("strokeWidth"));
        body.add(this.strokeLabel);
        JPanel sliderRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sliderRow.setOpaque(false); sliderRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.strokeSlider = new JSlider(1, 10, strokeWidth);
        this.strokeSlider.setPreferredSize(new Dimension(200, 30));
        this.strokeSlider.setOpaque(false);
        this.strokeSlider.setForeground(Color.WHITE);
        this.strokeValLabel = new JLabel(" " + strokeWidth + "px");
        this.strokeValLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        this.strokeValLabel.setForeground(Color.WHITE);
        this.strokeSlider.addChangeListener(e -> {
            strokeWidth = this.strokeSlider.getValue();
            this.strokeValLabel.setText(" " + strokeWidth + "px");
        });
        sliderRow.add(this.strokeSlider); sliderRow.add(this.strokeValLabel);
        body.add(sliderRow);
        body.add(Box.createVerticalStrut(24));

        this.resetBtn = new JButton(GStrings.get("resetDefault"));
        this.resetBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        this.resetBtn.setForeground(Color.WHITE);
        this.resetBtn.setBackground(new Color(80, 40, 40));
        this.resetBtn.setFocusPainted(false); this.resetBtn.setBorderPainted(false);
        this.resetBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.resetBtn.addActionListener(new ResetHandler());
        body.add(this.resetBtn);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null); scroll.getViewport().setBackground(new Color(30, 30, 30));
        this.add(scroll, BorderLayout.CENTER);

        GStrings.addListener(this::refreshTexts);
    }

    @Override
    public void onEnter() {
        this.antiAliasBox.setSelected(antiAlias);
        this.gridBox.setSelected(showGrid);
        this.strokeSlider.setValue(strokeWidth);
    }

    private void refreshTexts() {
        this.backBtn.setText(GStrings.get("back"));
        this.titleLabel.setText(GStrings.get("settingsTitle"));
        this.renderLabel.setText(GStrings.get("rendering"));
        this.antiAliasBox.setText(GStrings.get("antiAlias"));
        this.gridBox.setText(GStrings.get("showGrid"));
        this.strokeLabel.setText(GStrings.get("strokeWidth"));
        this.resetBtn.setText(GStrings.get("resetDefault"));
    }

    // helpers
    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        l.setForeground(new Color(180, 180, 180));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return l;
    }

    private JCheckBox makeCheck(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        cb.setForeground(Color.WHITE); cb.setOpaque(false);
        cb.setAlignmentX(Component.LEFT_ALIGNMENT); cb.setFocusPainted(false);
        return cb;
    }

    private JButton makeBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE); btn.setBackground(new Color(40, 40, 40));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // inner class
    private class ResetHandler implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            antiAlias = true; showGrid = false; strokeWidth = 1;
            GSettingsScreen.this.antiAliasBox.setSelected(true);
            GSettingsScreen.this.gridBox.setSelected(false);
            GSettingsScreen.this.strokeSlider.setValue(1);
        }
    }
}