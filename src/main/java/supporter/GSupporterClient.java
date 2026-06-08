package supporter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class GSupporterClient {
    private static final String SERVER_URL = "http://localhost:8000/analyze";
    private static final int INTERVAL = 5000;

    private JPanel  target;
    private Timer   timer;
    private boolean running;
    private Callback callback;

    public interface Callback { void onAdvice(String advice); }

    public GSupporterClient(JPanel target) { this.target = target; }
    public void setCallback(Callback cb) { this.callback = cb; }
    public boolean isRunning() { return this.running; }

    public void start() {
        if (this.running) return;
        this.running = true;
        this.timer = new Timer(INTERVAL, e -> analyze());
        this.timer.setInitialDelay(0);
        this.timer.start();
    }

    public void stop() {
        this.running = false;
        if (this.timer != null) this.timer.stop();
    }

    private void analyze() {
        new Thread(() -> {
            try {
                String b64 = captureBase64();
                if (b64 == null) return;

                HttpURLConnection conn = (HttpURLConnection) new URL(SERVER_URL).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(("{\"image\":\"" + b64 + "\"}").getBytes("UTF-8"));
                }

                StringBuilder sb = new StringBuilder();
                try (BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                    String line; while ((line = r.readLine()) != null) sb.append(line);
                }

                String resp = sb.toString();
                int s = resp.indexOf("\"advice\":\"") + 10, e = resp.lastIndexOf("\"");
                String advice = (s > 10 && e > s) ? resp.substring(s, e).replace("\\n", "\n") : "응답 파싱 실패";
                if (this.callback != null) SwingUtilities.invokeLater(() -> this.callback.onAdvice(advice));
            } catch (Exception ex) {
                if (this.callback != null) SwingUtilities.invokeLater(() -> this.callback.onAdvice("서버 연결 실패"));
            }
        }).start();
    }

    private String captureBase64() {
        int w = this.target.getWidth(), h = this.target.getHeight();
        if (w <= 0 || h <= 0) return null;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.target.paint(img.createGraphics());
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", bos);
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (IOException e) { return null; }
    }
}