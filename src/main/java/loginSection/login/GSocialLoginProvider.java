package loginSection.login;

import java.awt.*;
import java.net.URI;

// 소셜 로그인 제공자 — 표시 정보(라벨, 색상)와 OAuth URL 캡슐화
// GLoginFrame이 values()로 순회하며 버튼 생성 → provider 추가/삭제 시 Frame 무수정
// 구 GAuthConstants.ESocialProvider 흡수 통합
public enum GSocialLoginProvider {

    // enum values
    eGoogle("Google로 시작하기",
            new Color(255, 255, 255), new Color(60,  60,  60),
            new Color(218, 220, 224),
            "https://accounts.google.com/signin"),

    eKakao("Kakao로 시작하기",
           new Color(254, 229, 0),   new Color(60,  60,  60),
           new Color(254, 229, 0),
           "https://kauth.kakao.com/oauth/authorize"),

    eNaver("Naver로 시작하기",
           new Color(3,   199, 90),  new Color(255, 255, 255),
           new Color(3,   199, 90),
           "https://nid.naver.com/oauth2.0/authorize");

    // attributes
    private final String label;
    private final Color  bgColor;
    private final Color  fgColor;
    private final Color  borderColor;
    private final String url;

    // constructors
    GSocialLoginProvider(String label, Color bgColor, Color fgColor,
                         Color borderColor, String url) {
        this.label       = label;
        this.bgColor     = bgColor;
        this.fgColor     = fgColor;
        this.borderColor = borderColor;
        this.url         = url;
    }

    // methods
    public String getLabel()       { return this.label; }
    public Color  getBgColor()     { return this.bgColor; }
    public Color  getFgColor()     { return this.fgColor; }
    public Color  getBorderColor() { return this.borderColor; }

    public void openInBrowser() {
        if (!Desktop.isDesktopSupported()) return;
        try { Desktop.getDesktop().browse(new URI(this.url)); }
        catch (Exception e) { e.printStackTrace(); }
    }
}