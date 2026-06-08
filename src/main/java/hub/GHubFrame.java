package hub;

import loginSection.profile.GProfileScreen;
import org.graphicsEditor.frames.GDrawingPanel;
import org.graphicsEditor.frames.GMainToolBar;
import org.graphicsEditor.frames.GMenuBar;
import org.graphicsEditor.toolbar.GSideToolBar;
import supporter.GSupporterPopup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class GHubFrame extends JFrame {

    // attributes
    private static GHubFrame instance;

    // associations
    private GDrawingPanel drawingPanel;

    // constructors
    private GHubFrame() {
        super("Null Canvas");
        this.setSize(1200, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JPanel        container = new JPanel();
        GModeSelector navigator = new GModeSelector(container);

        GHubScreen         hubScreen      = new GHubScreen(navigator);
        GDrawingModeScreen drawingScreen  = new GDrawingModeScreen(navigator);
        GProfileScreen     profileScreen  = new GProfileScreen(navigator);
        GGalleryScreen     galleryScreen  = new GGalleryScreen(navigator);
        GLanguageScreen    langScreen     = new GLanguageScreen(navigator);
        GSettingsScreen    settingsScreen = new GSettingsScreen(navigator);

        navigator.registerScreen(GModeSelector.EMode.eHub,         hubScreen);
        navigator.registerScreen(GModeSelector.EMode.eDrawingMode, drawingScreen);
        navigator.registerScreen(GModeSelector.EMode.eProfile,     profileScreen);
        navigator.registerScreen(GModeSelector.EMode.eGallery,     galleryScreen);
        navigator.registerScreen(GModeSelector.EMode.eLanguage,    langScreen);
        navigator.registerScreen(GModeSelector.EMode.eSettings,    settingsScreen);

        this.setContentPane(container);
        this.setVisible(true);
        navigator.navigateTo(GModeSelector.EMode.eHub);
    }

    // methods
    public static GHubFrame getInstance() {
        if (instance == null) instance = new GHubFrame();
        return instance;
    }

    public void openSupporter() {
        GSupporterPopup popup = new GSupporterPopup(this.drawingPanel);
        popup.setLocationRelativeTo(this);
        popup.setVisible(true);
    }

    public void loadImageToDrawingPanel(BufferedImage img) {
        if (this.drawingPanel != null && img != null) this.drawingPanel.loadImage(img);
    }

    // inner class
    private class GDrawingModeScreen extends GBaseScreen {

        // components
        private GMenuBar      menuBar;
        private GMainToolBar  toolBar;
        private GDrawingPanel drawingPanel;
        private GSideToolBar  sideToolBar;

        // constructors
        GDrawingModeScreen(GModeSelector navigator) { super(navigator); }

        // methods
        @Override
        protected void initUI() {
            this.setLayout(new BorderLayout());

            this.toolBar = new GMainToolBar();
            this.toolBar.setHomeAction(new HomeHandler());
            this.toolBar.setLanguageAction(new LangHandler());
            this.toolBar.setSettingsAction(new SettingsHandler());
            this.add(this.toolBar, BorderLayout.NORTH);

            this.sideToolBar = new GSideToolBar();
            this.add(this.sideToolBar, BorderLayout.WEST);

            this.drawingPanel = new GDrawingPanel();
            GHubFrame.this.drawingPanel = this.drawingPanel;
            this.add(this.drawingPanel, BorderLayout.CENTER);

            this.drawingPanel.associateWith(this.toolBar);
            this.drawingPanel.associateWithSideBar(this.sideToolBar);

            this.menuBar = new GMenuBar();
            this.menuBar.getFileMenu().associateWith(this.drawingPanel);
        }

        @Override
        public void onEnter() {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) { frame.setJMenuBar(this.menuBar); frame.revalidate(); }
        }

        @Override
        public void onExit() {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) { frame.setJMenuBar(null); frame.revalidate(); }
        }

        // inner class
        private class HomeHandler implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                GDrawingModeScreen.this.navigator.goBack();
            }
        }

        private class LangHandler implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                GDrawingModeScreen.this.navigator.navigateTo(GModeSelector.EMode.eLanguage);
            }
        }

        private class SettingsHandler implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                GDrawingModeScreen.this.navigator.navigateTo(GModeSelector.EMode.eSettings);
            }
        }
    }
}