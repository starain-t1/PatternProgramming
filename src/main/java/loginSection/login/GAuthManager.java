package loginSection.login;

// 인증/프로필 기능 추상화 인터페이스 — Strategy 패턴
// GLocalAuthManager가 구현, 추후 서버 기반 구현체로 교체 가능
public interface GAuthManager {

    // 인증
    boolean login(String email, String password);
    boolean signup(String name, String email, String password);

    // 프로필 조회
    String getCurrentEmail();
    String getCurrentName();
    String getImagePath();

    // 프로필 수정
    void    updateName(String name);
    boolean updatePassword(String oldPw, String newPw);
    void    updateImagePath(String path);
}