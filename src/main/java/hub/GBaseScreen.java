package hub;

import javax.swing.JPanel;

// 모든 Hub 화면의 공통 부모 클래스
// 생명주기: initUI() → (표시 시) onEnter() → (이탈 시) onExit()
// CardLayout 위에서 화면은 파괴되지 않고 숨겨지기만 하므로 상태는 자동 보존
public abstract class GBaseScreen extends JPanel {

    // associations
    protected final GModeSelector navigator;

    // constructors
    public GBaseScreen(GModeSelector navigator) {
        this.navigator = navigator;
        this.initUI(); // UI는 생성 시 딱 한 번만 구성
    }

    // methods — 하위 클래스가 반드시 구현
    protected abstract void initUI();

    // methods — 선택적 생명주기 오버라이드
    public void onEnter() {} // 화면 활성화 시 — 데이터 갱신, 포커스 설정 등
    public void onExit()  {} // 화면 이탈 시  — 작업 일시정지, 임시 저장 등
}