package hub;

import loginSection.login.GLocalAuthManager;
import org.graphicsEditor.global.GStrings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class GGalleryScreen extends GBaseScreen {

    // attributes
    private static final int THUMB_W = 190, THUMB_H = 140, COLS = 4, GAP = 14;

    // components
    private JPanel  gridPanel;
    private JLabel  emptyLabel, countLabel, titleLabel;
    private JButton backBtn;

    // constructors
    public GGalleryScreen(GModeSelector navigator) { super(navigator); }

    // methods
    @Override
    protected void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(22, 22, 28));

        // 헤더
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 80)));
        header.setPreferredSize(new Dimension(0, 54));

        this.backBtn = this.makeBtn(GStrings.get("back"));
        this.backBtn.addActionListener(new BackHandler());
        this.backBtn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        header.add(this.backBtn, BorderLayout.WEST);

        this.titleLabel = new JLabel(GStrings.get("gallery"), SwingConstants.CENTER);
        this.titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 17));
        this.titleLabel.setForeground(Color.WHITE);
        header.add(this.titleLabel, BorderLayout.CENTER);

        this.countLabel = new JLabel("", SwingConstants.RIGHT);
        this.countLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        this.countLabel.setForeground(new Color(160, 160, 180));
        this.countLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        header.add(this.countLabel, BorderLayout.EAST);
        this.add(header, BorderLayout.NORTH);

        // 그리드
        this.gridPanel = new JPanel();
        this.gridPanel.setBackground(new Color(22, 22, 28));
        this.gridPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));

        this.emptyLabel = new JLabel(GStrings.get("galleryEmpty"), SwingConstants.CENTER);
        this.emptyLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        this.emptyLabel.setForeground(new Color(120, 120, 140));

        JScrollPane scroll = new JScrollPane(this.gridPanel);
        scroll.setBorder(null); scroll.getViewport().setBackground(new Color(22, 22, 28));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scroll, BorderLayout.CENTER);

        GStrings.addListener(this::refreshTexts);
    }

    @Override
    public void onEnter() { this.loadGallery(); }

    private void refreshTexts() {
        this.backBtn.setText(GStrings.get("back"));
        this.titleLabel.setText(GStrings.get("gallery"));
        this.emptyLabel.setText(GStrings.get("galleryEmpty"));
    }

    private void loadGallery() {
        this.gridPanel.removeAll();
        GLocalAuthManager auth  = GLocalAuthManager.getInstance();
        String            email = auth.getCurrentEmail();
        if (email == null || email.isEmpty()) { this.showEmpty(); return; }

        String folder = email.replace("@", "_").replace(".", "_");
        File   dir    = new File("data/works/" + folder);
        File[] files  = dir.exists() ? dir.listFiles(f -> f.getName().endsWith(".png")) : null;
        if (files == null || files.length == 0) { this.showEmpty(); return; }

        Arrays.sort(files, new FileNameComparator());
        this.countLabel.setText(files.length + "  ");

        int rows = (int) Math.ceil((double) files.length / COLS);
        this.gridPanel.setLayout(new GridLayout(rows, COLS, GAP, GAP));
        for (File f : files) this.gridPanel.add(this.makeThumbCard(f));
        int rem = (COLS - files.length % COLS) % COLS;
        for (int i = 0; i < rem; i++) { JPanel b = new JPanel(); b.setOpaque(false); this.gridPanel.add(b); }
        this.gridPanel.revalidate(); this.gridPanel.repaint();
    }

    private void showEmpty() {
        this.countLabel.setText("0  ");
        this.gridPanel.setLayout(new GridBagLayout());
        this.gridPanel.add(this.emptyLabel);
        this.gridPanel.revalidate(); this.gridPanel.repaint();
    }

    private JPanel makeThumbCard(File f) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(new Color(34, 34, 44));
        card.setBorder(this.cardBorder(false));
        card.setPreferredSize(new Dimension(THUMB_W, THUMB_H + 32));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setBackground(new Color(28, 28, 36)); imgLabel.setOpaque(true);
        try {
            BufferedImage raw = ImageIO.read(f);
            if (raw != null) {
                int    tw    = THUMB_W - 16, th = THUMB_H - 8;
                double scale = Math.min((double) tw / raw.getWidth(), (double) th / raw.getHeight());
                imgLabel.setIcon(new ImageIcon(raw.getScaledInstance(
                    (int)(raw.getWidth() * scale), (int)(raw.getHeight() * scale), Image.SCALE_SMOOTH)));
            }
        } catch (Exception e) { imgLabel.setText("N/A"); imgLabel.setForeground(Color.GRAY); }
        card.add(imgLabel, BorderLayout.CENTER);

        String name    = f.getName().replace(".png", "");
        String dateStr = name.length() >= 15
            ? name.substring(0,4)+"."+name.substring(4,6)+"."+name.substring(6,8)
              +"  "+name.substring(9,11)+":"+name.substring(11,13)
            : name;
        JLabel nameLabel = new JLabel(dateStr, SwingConstants.CENTER);
        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        nameLabel.setForeground(new Color(180, 180, 200));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        card.add(nameLabel, BorderLayout.SOUTH);

        card.addMouseListener(new ThumbCardHandler(card, f));
        return card;
    }

    private void openViewer(File f) {
        Window ancestor = SwingUtilities.getWindowAncestor(this);
        JDialog dialog  = new JDialog(ancestor instanceof Frame ? (Frame) ancestor : null, true);
        dialog.setTitle(f.getName().replace(".png", ""));
        dialog.setSize(820, 620); dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(15, 15, 20));

        JLabel viewer = new JLabel(); viewer.setHorizontalAlignment(SwingConstants.CENTER);
        viewer.setBackground(new Color(15, 15, 20)); viewer.setOpaque(true);
        try {
            BufferedImage img = ImageIO.read(f);
            if (img != null) {
                double scale = Math.min(780.0 / img.getWidth(), 520.0 / img.getHeight());
                viewer.setIcon(new ImageIcon(img.getScaledInstance(
                    (int)(img.getWidth() * scale), (int)(img.getHeight() * scale), Image.SCALE_SMOOTH)));
            }
        } catch (Exception e) { viewer.setText("Error"); viewer.setForeground(Color.WHITE); }
        dialog.add(viewer, BorderLayout.CENTER);

        JPanel  btPanel  = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btPanel.setBackground(new Color(25, 25, 32));
        JButton closeBtn = this.makeBtn(GStrings.get("close"));
        closeBtn.setPreferredSize(new Dimension(100, 34));
        closeBtn.addActionListener(new CloseDialogHandler(dialog));
        btPanel.add(closeBtn);
        dialog.add(btPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private javax.swing.border.Border cardBorder(boolean hover) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(hover ? new Color(100,100,200) : new Color(55,55,75), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    private JButton makeBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE); btn.setBackground(new Color(50, 50, 70));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // inner class
    private class BackHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) { GGalleryScreen.this.navigator.goBack(); }
    }

    private class ThumbCardHandler extends MouseAdapter {
        private final JPanel card;
        private final File   file;
        ThumbCardHandler(JPanel card, File file) { this.card = card; this.file = file; }

        @Override public void mouseClicked(MouseEvent e) { GGalleryScreen.this.openViewer(this.file); }
        @Override public void mouseEntered(MouseEvent e) { this.card.setBackground(new Color(50, 50, 65));  this.card.setBorder(GGalleryScreen.this.cardBorder(true)); }
        @Override public void mouseExited(MouseEvent e)  { this.card.setBackground(new Color(34, 34, 44)); this.card.setBorder(GGalleryScreen.this.cardBorder(false)); }
    }

    private static class CloseDialogHandler implements ActionListener {
        private final JDialog dialog;
        CloseDialogHandler(JDialog dialog) { this.dialog = dialog; }

        @Override
        public void actionPerformed(ActionEvent e) { this.dialog.dispose(); }
    }

    private static class FileNameComparator implements Comparator<File> {
        @Override
        public int compare(File a, File b) { return b.getName().compareTo(a.getName()); }
    }
}