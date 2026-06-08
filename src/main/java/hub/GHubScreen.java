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

public class GHubScreen extends GBaseScreen {

    // components
    private JPanel  recentFilesPanel, recentFoldersPanel;
    private JButton profileBtn, unrealBtn, supporterBtn;
    private JLabel  filesHeaderLabel, foldersHeaderLabel;
    private JButton newFileBtn, openFileBtn, recoverBtn;

    // constructors
    public GHubScreen(GModeSelector navigator) { super(navigator); }

    // methods
    @Override
    protected void initUI() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(173, 216, 230));
        this.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 좌측 버튼 패널
        JPanel leftPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(300, 0));

        this.profileBtn   = this.makeSideBtn(GStrings.get("profile"));
        this.unrealBtn    = this.makeSideBtn(GStrings.get("goUnreal"));
        this.supporterBtn = this.makeSideBtn(GStrings.get("addSupporter"));

        this.profileBtn.addActionListener(new SideBtnHandler(GModeSelector.EMode.eProfile));
        this.unrealBtn.addActionListener(new SideBtnHandler(GModeSelector.EMode.eUnreal));
        this.supporterBtn.addActionListener(new SupporterHandler());

        leftPanel.add(this.profileBtn);
        leftPanel.add(this.unrealBtn);
        leftPanel.add(this.supporterBtn);
        this.add(leftPanel, BorderLayout.WEST);

        // 우측 패널
        this.add(this.makeRightPanel(), BorderLayout.CENTER);

        GStrings.addListener(this::refreshTexts);
    }

    @Override
    public void onEnter() { this.refreshRecent(); }

    private void refreshTexts() {
        this.profileBtn.setText(GStrings.get("profile"));
        this.unrealBtn.setText(GStrings.get("goUnreal"));
        this.supporterBtn.setText(GStrings.get("addSupporter"));
        this.newFileBtn.setText(GStrings.get("newFile"));
        this.openFileBtn.setText(GStrings.get("openFile"));
        this.recoverBtn.setText(GStrings.get("recoverFiles"));
        this.filesHeaderLabel.setText(GStrings.get("recentFiles"));
        this.foldersHeaderLabel.setText(GStrings.get("recentFolders"));
        this.refreshRecent();
    }

    private JPanel makeRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 220), 2),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        panel.add(this.makeMiniToolBar(), BorderLayout.NORTH);

        JPanel recentArea = new JPanel(new GridLayout(2, 1, 0, 0));
        recentArea.setBackground(Color.WHITE);

        JPanel filesSection = new JPanel(new BorderLayout());
        filesSection.setBackground(Color.WHITE);
        this.filesHeaderLabel = new JLabel(GStrings.get("recentFiles"));
        filesSection.add(this.makeSectionHeader(this.filesHeaderLabel), BorderLayout.NORTH);
        this.recentFilesPanel = new JPanel();
        this.recentFilesPanel.setLayout(new BoxLayout(this.recentFilesPanel, BoxLayout.Y_AXIS));
        this.recentFilesPanel.setBackground(Color.WHITE);
        JScrollPane filesScroll = new JScrollPane(this.recentFilesPanel);
        filesScroll.setBorder(null); filesScroll.getViewport().setBackground(Color.WHITE);
        filesSection.add(filesScroll, BorderLayout.CENTER);

        JPanel foldersSection = new JPanel(new BorderLayout());
        foldersSection.setBackground(Color.WHITE);
        foldersSection.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        this.foldersHeaderLabel = new JLabel(GStrings.get("recentFolders"));
        foldersSection.add(this.makeSectionHeader(this.foldersHeaderLabel), BorderLayout.NORTH);
        this.recentFoldersPanel = new JPanel();
        this.recentFoldersPanel.setLayout(new BoxLayout(this.recentFoldersPanel, BoxLayout.Y_AXIS));
        this.recentFoldersPanel.setBackground(Color.WHITE);
        JScrollPane foldersScroll = new JScrollPane(this.recentFoldersPanel);
        foldersScroll.setBorder(null); foldersScroll.getViewport().setBackground(Color.WHITE);
        foldersSection.add(foldersScroll, BorderLayout.CENTER);

        recentArea.add(filesSection);
        recentArea.add(foldersSection);
        panel.add(recentArea, BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeMiniToolBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bar.setBackground(new Color(60, 60, 60));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 40, 40)));

        this.newFileBtn  = this.makeToolBarBtn(GStrings.get("newFile"));
        this.openFileBtn = this.makeToolBarBtn(GStrings.get("openFile"));
        this.recoverBtn  = this.makeToolBarBtn(GStrings.get("recoverFiles"));

        this.newFileBtn.addActionListener(new ToolBarHandler("New"));
        this.openFileBtn.addActionListener(new ToolBarHandler("Open"));

        bar.add(this.newFileBtn); bar.add(this.openFileBtn); bar.add(this.recoverBtn);
        return bar;
    }

    private JPanel makeSectionHeader(JLabel label) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        header.setBackground(new Color(110, 120, 130));
        header.setPreferredSize(new Dimension(0, 26));
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(Color.WHITE);
        header.add(label);
        return header;
    }

    private void refreshRecent() {
        this.recentFilesPanel.removeAll();
        this.recentFoldersPanel.removeAll();

        GLocalAuthManager auth  = GLocalAuthManager.getInstance();
        String            email = auth.getCurrentEmail();
        File[]            files = null;

        if (email != null && !email.isEmpty()) {
            String folder = email.replace("@", "_").replace(".", "_");
            File   dir    = new File("data/works/" + folder);
            if (dir.exists()) {
                files = dir.listFiles(new PngFilter());
                if (files != null) Arrays.sort(files, new FileNameComparator());
            }
        }

        if (files != null && files.length > 0) {
            int limit = Math.min(files.length, 20);
            for (int i = 0; i < limit; i++) this.recentFilesPanel.add(this.makeFileRow(files[i]));
        } else {
            this.recentFilesPanel.add(this.makeEmptyRow(GStrings.get("noRecentFiles")));
        }

        if (email != null && !email.isEmpty()) {
            String folder = email.replace("@", "_").replace(".", "_");
            this.recentFoldersPanel.add(this.makeFolderRow(new File("data/works/" + folder)));
        } else {
            this.recentFoldersPanel.add(this.makeEmptyRow(GStrings.get("noRecentFolders")));
        }

        this.recentFilesPanel.revalidate();   this.recentFilesPanel.repaint();
        this.recentFoldersPanel.revalidate(); this.recentFoldersPanel.repaint();
    }

    private JPanel makeFileRow(File f) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 235, 235)),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel name = new JLabel(f.getName());
        name.setFont(new Font("SansSerif", Font.BOLD, 13)); name.setForeground(new Color(40, 40, 40));
        JLabel path = new JLabel(f.getParent());
        path.setFont(new Font("SansSerif", Font.PLAIN, 11)); path.setForeground(new Color(150, 150, 150));
        row.add(name, BorderLayout.WEST); row.add(path, BorderLayout.CENTER);
        row.addMouseListener(new FileRowHandler(row, f));
        return row;
    }

    private JPanel makeFolderRow(File dir) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 235, 235)),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JLabel name = new JLabel(dir.getName());
        name.setFont(new Font("SansSerif", Font.BOLD, 13)); name.setForeground(new Color(40, 40, 40));
        JLabel path = new JLabel(dir.getAbsolutePath());
        path.setFont(new Font("SansSerif", Font.PLAIN, 11)); path.setForeground(new Color(150, 150, 150));
        row.add(name, BorderLayout.WEST); row.add(path, BorderLayout.CENTER);
        return row;
    }

    private JPanel makeEmptyRow(String msg) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JLabel l = new JLabel(msg);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12)); l.setForeground(new Color(180, 180, 180));
        row.add(l);
        return row;
    }

    private void openFileAndNavigate(File f) {
        try {
            BufferedImage img = ImageIO.read(f);
            GHubFrame.getInstance().loadImageToDrawingPanel(img);
        } catch (Exception e) { e.printStackTrace(); }
        this.navigator.navigateTo(GModeSelector.EMode.eDrawingMode);
    }

    private JButton makeSideBtn(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE); btn.setForeground(new Color(40, 40, 40));
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setFocusPainted(false); btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 220), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeToolBarBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(new Color(210, 210, 210)); btn.setBackground(new Color(60, 60, 60));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        btn.addMouseListener(new ToolBarHoverHandler(btn));
        return btn;
    }

    // inner class
    private class SideBtnHandler implements ActionListener {
        private final GModeSelector.EMode target;
        SideBtnHandler(GModeSelector.EMode target) { this.target = target; }

        @Override
        public void actionPerformed(ActionEvent e) {
            GHubScreen.this.navigator.navigateTo(this.target);
        }
    }

    private class SupporterHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) { GHubFrame.getInstance().openSupporter(); }
    }

    private class ToolBarHandler implements ActionListener {
        private final String type;
        ToolBarHandler(String type) { this.type = type; }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("New".equals(this.type)) {
                GHubScreen.this.navigator.navigateTo(GModeSelector.EMode.eDrawingMode);
            } else if ("Open".equals(this.type)) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Open");
                fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "이미지 파일 (png, jpg)", "png", "jpg", "jpeg"));
                if (fc.showOpenDialog(GHubScreen.this) != JFileChooser.APPROVE_OPTION) return;
                try {
                    BufferedImage img = ImageIO.read(fc.getSelectedFile());
                    GHubFrame.getInstance().loadImageToDrawingPanel(img);
                    GHubScreen.this.navigator.navigateTo(GModeSelector.EMode.eDrawingMode);
                } catch (Exception ex) { JOptionPane.showMessageDialog(GHubScreen.this, "Error"); }
            }
        }
    }

    private class FileRowHandler extends MouseAdapter {
        private final JPanel row;
        private final File   file;
        FileRowHandler(JPanel row, File file) { this.row = row; this.file = file; }

        @Override public void mouseClicked(MouseEvent e) { GHubScreen.this.openFileAndNavigate(this.file); }
        @Override public void mouseEntered(MouseEvent e) { this.row.setBackground(new Color(235, 242, 250)); }
        @Override public void mouseExited(MouseEvent e)  { this.row.setBackground(Color.WHITE); }
    }

    private static class ToolBarHoverHandler extends MouseAdapter {
        private final JButton btn;
        ToolBarHoverHandler(JButton btn) { this.btn = btn; }

        @Override public void mouseEntered(MouseEvent e) { this.btn.setBackground(new Color(85, 85, 85)); }
        @Override public void mouseExited(MouseEvent e)  { this.btn.setBackground(new Color(60, 60, 60)); }
    }

    private static class PngFilter implements java.io.FilenameFilter {
        @Override
        public boolean accept(File dir, String name) { return name.endsWith(".png"); }
    }

    private static class FileNameComparator implements Comparator<File> {
        @Override
        public int compare(File a, File b) { return b.getName().compareTo(a.getName()); }
    }
}