package hub;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GModeSelector {

    // attributes
    public enum EMode { eHub, eDrawing, eDrawingMode, eProfile, eUnreal, eGallery, eLanguage, eSettings }

    private EMode    currentMode;
    private Runnable onModeChanged;

    // components
    private final JPanel     container;
    private final CardLayout cardLayout;

    // associations
    private final Map<EMode, JPanel> screenMap = new HashMap<>();
    private final Stack<EMode>       history   = new Stack<>();

    // constructors
    public GModeSelector(JPanel container) {
        this.cardLayout = new CardLayout();
        this.container  = container;
        this.container.setLayout(this.cardLayout);
    }

    // methods
    public void setOnModeChanged(Runnable r) { this.onModeChanged = r; }
    public EMode getCurrentMode()            { return this.currentMode; }

    public void registerScreen(EMode mode, JPanel screen) {
        this.screenMap.put(mode, screen);
        this.container.add(screen, mode.name());
    }

    public void navigateTo(EMode mode) {
        if (mode == this.currentMode || !this.screenMap.containsKey(mode)) return;
        if (this.currentMode != null) { this.exit(this.currentMode); this.history.push(this.currentMode); }
        this.enter(mode);
    }

    public void goBack() {
        if (this.history.isEmpty()) return;
        this.exit(this.currentMode);
        this.enter(this.history.pop());
    }

    // private helpers
    private void exit(EMode mode) {
        JPanel p = this.screenMap.get(mode);
        if (p instanceof GBaseScreen) ((GBaseScreen) p).onExit();
    }

    private void enter(EMode mode) {
        this.currentMode = mode;
        this.cardLayout.show(this.container, mode.name());
        JPanel p = this.screenMap.get(mode);
        if (p instanceof GBaseScreen) SwingUtilities.invokeLater(new EnterRunner((GBaseScreen) p));
        if (this.onModeChanged != null) this.onModeChanged.run();
    }

    // inner class
    private static class EnterRunner implements Runnable {
        private final GBaseScreen screen;
        EnterRunner(GBaseScreen screen) { this.screen = screen; }

        @Override
        public void run() { this.screen.onEnter(); }
    }
}