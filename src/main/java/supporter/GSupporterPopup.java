package supporter; // supporter 패키지 — AI 조언 기능을 담당하는 패키지

import javax.imageio.ImageIO;        // BufferedImage를 JPEG 바이트로 변환할 때 사용
import javax.swing.*;                // JFrame, JButton, JLabel, Timer 등 Swing UI 컴포넌트
import java.awt.*;                   // BorderLayout, Color, Font 등 AWT 그래픽 클래스
import java.awt.image.BufferedImage; // 오프스크린 이미지 — 캔버스를 캡처할 때 사용
import java.io.*;                    // OutputStream, BufferedReader, ByteArrayOutputStream 등 I/O
import java.net.HttpURLConnection;   // HTTP 요청을 보내는 Java 표준 클래스
import java.net.URL;                 // URL 문자열을 URL 객체로 변환
import java.util.Base64;             // 바이트 배열을 base64 문자열로 인코딩 (이미지 전송용)

// AI Supporter 팝업 — On/Off 토글 + 캔버스 캡처 + Claude API 서버로 분석 요청 + 조언 표시
// GSupporterClient를 별도 클래스로 분리하지 않고 이 안에 통합 (1파일 구조)
public class GSupporterPopup extends JFrame {

    // TypeScript 서버 주소 — Express가 8000 포트에서 대기
    private static final String API = "http://localhost:8000/analyze";

    // target: 캡처 대상 패널 (GDrawingPanel). GHubFrame에서 전달받음
    private final JPanel  target;
    // btn: On/Off 토글 버튼
    private final JButton btn;
    // label: AI 조언 텍스트를 표시하는 라벨
    private final JLabel  label;
    // timer: 5초마다 analyze() 호출하는 Swing Timer. null이면 비활성 상태
    private Timer timer;

    // 생성자 — GHubFrame.openSupporter()에서 호출
    // 매개변수 target: 캡처할 JPanel (GDrawingPanel 인스턴스)
    public GSupporterPopup(JPanel target) {
        super("AI Supporter"); // JFrame 타이틀
        this.target = target;  // 캡처 대상 저장
        this.setSize(320, 120);             // 작은 팝업 크기
        this.setAlwaysOnTop(true);          // 항상 최상위에 표시 — 그림판 위에 떠야 하므로
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // X 누르면 이 팝업만 닫힘 (앱 종료 아님)
        this.setResizable(false);           // 크기 조절 불가
        this.setLayout(new BorderLayout(5, 5)); // BorderLayout (간격 5px)

        // On/Off 토글 버튼 — 상단(NORTH)에 배치
        this.btn = new JButton("OFF");
        this.btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        this.btn.setBackground(new Color(200, 60, 60)); // 빨간색 = OFF 상태
        this.btn.setForeground(Color.WHITE);             // 흰색 글자
        this.btn.setFocusPainted(false);                 // 포커스 테두리 제거
        this.btn.addActionListener(e -> toggle());       // 클릭 시 toggle() 호출 (람다)
        this.add(this.btn, BorderLayout.NORTH);          // 상단 배치

        // 조언 라벨 — 중앙(CENTER)에 배치
        this.label = new JLabel("AI 조언 대기", SwingConstants.CENTER); // 가운데 정렬
        this.label.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        this.add(this.label, BorderLayout.CENTER);
    }

    // On/Off 전환 메서드
    private void toggle() {
        // timer가 존재하고 실행 중이면 현재 ON 상태
        boolean on = this.timer != null && this.timer.isRunning();

        if (on) {
            this.timer.stop(); // 타이머 정지 → 분석 중단
        } else {
            // 새 Timer 생성 — 5000ms(5초) 간격으로 analyze() 반복 호출
            this.timer = new Timer(5000, e -> analyze());
            this.timer.setInitialDelay(0); // 첫 호출은 즉시 (대기 없이)
            this.timer.start();            // 타이머 시작
        }

        // UI 상태 갱신 — 삼항 연산자로 ON/OFF 분기
        this.btn.setText(on ? "OFF" : "ON");
        // ON이었으면 빨간색(OFF로 전환), OFF였으면 초록색(ON으로 전환)
        this.btn.setBackground(on ? new Color(200, 60, 60) : new Color(60, 160, 60));
        this.label.setText(on ? "꺼짐" : "분석 중...");
    }

    private void analyze() {
        new Thread(() -> {
            try {
                int w = this.target.getWidth(), h = this.target.getHeight();
                System.out.println("[DEBUG] w=" + w + " h=" + h + " target=" + this.target); // 추가
                if (w <= 0 || h <= 0) return;
                // TYPE_INT_RGB: 각 픽셀을 24비트 RGB로 저장하는 버퍼 이미지 생성
                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                // target.paint(): 패널의 현재 상태를 이 이미지에 그림 (스크린샷 효과)
                this.target.paint(img.createGraphics());

                // ── 2단계: JPEG → base64 변환 ──
                // ByteArrayOutputStream: 메모리 내 바이트 버퍼 (파일 안 거침)
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // ImageIO.write(): BufferedImage를 JPEG 포맷으로 인코딩하여 bos에 쓰기
                ImageIO.write(img, "jpg", bos);
                // Base64 인코딩: 바이너리 바이트를 텍스트 문자열로 변환 (HTTP JSON으로 전송 가능하게)
                String b64 = Base64.getEncoder().encodeToString(bos.toByteArray());

                // ── 3단계: HTTP POST 요청 ──
                // HttpURLConnection: Java 표준 HTTP 클라이언트
                HttpURLConnection c = (HttpURLConnection) new URL(API).openConnection();
                c.setRequestMethod("POST");                              // POST 방식
                c.setRequestProperty("Content-Type", "application/json"); // JSON 형식임을 서버에 알림
                c.setDoOutput(true);                                     // 요청 본문 쓰기 허용

                // try-with-resources로 OutputStream 자동 닫기
                // JSON 형식: {"image":"base64문자열..."}
                try (OutputStream os = c.getOutputStream()) {
                    os.write(("{\"image\":\"" + b64 + "\"}").getBytes("UTF-8"));
                }

                // ── 4단계: 응답 읽기 ──
                StringBuilder sb = new StringBuilder();
                // try-with-resources로 BufferedReader 자동 닫기
                try (BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"))) {
                    String line;
                    // 응답을 한 줄씩 읽어서 StringBuilder에 누적
                    while ((line = r.readLine()) != null) sb.append(line);
                }

                // ── 5단계: JSON 응답 파싱 ──
                // 서버 응답 형식: {"advice":"조언 텍스트"}
                String resp = sb.toString();
                // "advice":" 다음 위치 = 실제 조언 텍스트 시작점
                int s = resp.indexOf("\"advice\":\"") + 10;
                // 마지막 " 위치 = 텍스트 끝점
                int e = resp.lastIndexOf("\"");
                // 삼항: 파싱 성공이면 substring, 실패면 에러 메시지
                String advice = (s > 10 && e > s)
                    ? resp.substring(s, e).replace("\\n", "\n") // \n 이스케이프 복원
                    : "파싱 실패";

                // ── 6단계: UI 갱신 ──
                // SwingUtilities.invokeLater(): EDT(이벤트 디스패치 스레드)에서 실행
                // Swing 컴포넌트는 EDT에서만 안전하게 수정 가능 — 다른 스레드에서 직접 수정하면 깨짐
                // <html><center> 태그로 줄바꿈 + 가운데 정렬
                SwingUtilities.invokeLater(() ->
                    this.label.setText("<html><center>" + advice + "</center></html>")
                );
            } catch (Exception ex) {
                // 서버 미실행, 네트워크 에러 등 모든 예외 → "서버 연결 실패" 표시
                SwingUtilities.invokeLater(() -> this.label.setText("서버 연결 실패"));
            }
        }).start(); // 스레드 시작
    }
}