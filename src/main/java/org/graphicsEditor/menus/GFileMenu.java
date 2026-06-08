package org.graphicsEditor.menus;

import loginSection.login.GLocalAuthManager;
import org.graphicsEditor.frames.GDrawingPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GFileMenu extends JMenu {

    // attributes
    private File currentFile;

    // associations
    private GDrawingPanel drawingPanel;

    // constructors
    public GFileMenu() {
        super("File");

        JMenuItem openItem = new JMenuItem("열기...");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(new OpenHandler());
        this.add(openItem);

        this.addSeparator();

        JMenuItem saveItem = new JMenuItem("저장");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(new SaveHandler(false));
        this.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("다른 이름으로 저장...");
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        saveAsItem.addActionListener(new SaveHandler(true));
        this.add(saveAsItem);
    }

    // setters
    public void associateWith(GDrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    // inner class
    private class OpenHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (GFileMenu.this.drawingPanel == null) return;
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("파일 열기");
            fc.setFileFilter(new FileNameExtensionFilter("이미지 파일 (png, jpg)", "png", "jpg", "jpeg"));
            if (fc.showOpenDialog(GFileMenu.this.drawingPanel) != JFileChooser.APPROVE_OPTION) return;
            GFileMenu.this.currentFile = fc.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(GFileMenu.this.currentFile);
                GFileMenu.this.drawingPanel.loadImage(img);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(GFileMenu.this.drawingPanel,
                        "파일을 열 수 없습니다:\n" + ex.getMessage());
            }
        }
    }

    private class SaveHandler implements ActionListener {
        private final boolean saveAs;
        SaveHandler(boolean saveAs) { this.saveAs = saveAs; }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (GFileMenu.this.drawingPanel == null) return;
            if (this.saveAs || GFileMenu.this.currentFile == null) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle(this.saveAs ? "다른 이름으로 저장" : "저장");
                fc.setFileFilter(new FileNameExtensionFilter("PNG 이미지", "png"));
                if (fc.showSaveDialog(GFileMenu.this.drawingPanel) != JFileChooser.APPROVE_OPTION) return;
                File selected = fc.getSelectedFile();
                if (!selected.getName().toLowerCase().endsWith(".png"))
                    selected = new File(selected.getParentFile(), selected.getName() + ".png");
                GFileMenu.this.currentFile = selected;
            }
            BufferedImage img = GFileMenu.this.drawingPanel.captureImage();
            if (img == null) return;
            try {
                ImageIO.write(img, "png", GFileMenu.this.currentFile);
                // [추가] 갤러리 자동 저장
                GalleryHelper.save(img);
                JOptionPane.showMessageDialog(GFileMenu.this.drawingPanel,
                        "저장 완료: " + GFileMenu.this.currentFile.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(GFileMenu.this.drawingPanel,
                        "저장 실패:\n" + ex.getMessage());
            }
        }
    }

    // [추가] 갤러리 자동 저장 헬퍼
    private static class GalleryHelper {
        static void save(BufferedImage img) {
            GLocalAuthManager auth = GLocalAuthManager.getInstance();
            String email = auth.getCurrentEmail();
            if (email == null || email.isEmpty()) return;
            // 이메일 → 폴더명 변환 (@ . → _)
            String folder = email.replace("@", "_").replace(".", "_");
            File dir = new File("data/works/" + folder);
            dir.mkdirs();
            // 파일명: yyyyMMdd_HHmmss.png
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            try {
                ImageIO.write(img, "png", new File(dir, ts + ".png"));
                auth.incrementWorkCount();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}