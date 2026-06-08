package core; // core 패키지 소속 — 편집 기능을 모아둔 패키지

import org.graphicsEditor.shapes.GShape; // 도형 클래스. isSelected(), setSelected() 등 사용
import java.util.Vector; // 도형 목록 자료구조. ArrayList와 유사하나 동기화 지원

// 전체선택 기능 클래스 (Ctrl+A)
// 인스턴스를 만들 필요 없이 GSelectAll.execute()로 바로 호출하는 유틸리티 클래스
public class GSelectAll {

    // static 메서드: 객체 생성 없이 클래스명.메서드명()으로 호출 가능
    // 매개변수 shapes: GDrawingPanel이 관리하는 도형 목록을 통째로 받음
    public static void execute(Vector<GShape> shapes) {
        // 향상된 for문: shapes의 모든 GShape 객체를 순회
        // s.setSelected(true): 각 도형의 선택 상태를 true로 설정
        // → 선택된 도형은 앵커(조절점)가 표시되고, Delete로 삭제 가능해짐
        for (GShape s : shapes) s.setSelected(true);
    }
}