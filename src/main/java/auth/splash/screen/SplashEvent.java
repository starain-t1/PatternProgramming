package auth.splash.screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SplashEvent extends JFrame {

    // attributes
    private static final int SPLASH_DELAY = 3000;

    // components
    private Image    splashImage;

    // associations
    private Runnable onFinished;

    // constructors
    public SplashEvent(Runnable onFinished) {
        this.onFinished  = onFinished;
        this.splashImage = this.loadImage();

        this.setUndecorated(true);
        this.setSize(400, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.add(new SplashPanel());
        this.setVisible(true);

        Timer timer = new Timer(SPLASH_DELAY, new TimerHandler());
        timer.setRepeats(false);
        timer.start();
    }

    // methods
    private Image loadImage() {
        java.net.URL url = this.getClass().getResource("/Splash/BG spiral gold.png");
        if (url != null) return new ImageIcon(url).getImage();
        System.out.println("[경고] 스플래시 이미지를 찾을 수 없습니다.");
        return null;
    }

    private void finish() {
        this.dispose();
        SwingUtilities.invokeLater(this.onFinished);
    }

    // inner class
    private class SplashPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (SplashEvent.this.splashImage != null) {
                g2d.drawImage(SplashEvent.this.splashImage, 0, 0, this.getWidth(), this.getHeight(), this);
            } else {
                this.drawFallback(g2d);
            }
        }

        private void drawFallback(Graphics2D g2d) {
            g2d.setPaint(new GradientPaint(
                0f, 0f, new Color(200, 230, 255),
                (float) this.getWidth(), (float) this.getHeight(), new Color(180, 160, 240)
            ));
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

            String text = "NULL CANVAS";
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 36));
            g2d.setColor(new Color(50, 50, 120));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(text,
                (this.getWidth()  - fm.stringWidth(text)) / 2,
                (this.getHeight() + fm.getAscent())        / 2
            );
        }
    }

    private class TimerHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) { SplashEvent.this.finish(); }
    }
}