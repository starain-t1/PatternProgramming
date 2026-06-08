package core;

import org.graphicsEditor.shapes.GShape; // 도형 클래스. clone() 메서드로 깊은 복사 지원
import java.util.Stack; // LIFO(후입선출) 자료구조. 가장 최근 저장된 것을 먼저 꺼냄
import java.util.Vector; // 도형 목록 자료구조

// 되돌리기 기능 클래스 (Ctrl+Z)
// 편집 직전의 도형 상태를 스냅샷(깊은 복사)으로 저장해두고,
// undo 호출 시 직전 상태를 꺼내서 복원하는 구조
public class GRedo {

    // attributes
    // Stack<Vector<GShape>>: 스택의 각 원소가 "도형 목록 전체의 사본"
    // 편집할 때마다 push, 되돌릴 때마다 pop
    private final Stack<Vector<GShape>> undoStack = new Stack<>();

    // methods

    // 현재 도형 상태를 스냅샷으로 저장
    // GDrawingPanel에서 startTransform(그리기/이동 시작) 직전에 호출됨
    // 매개변수 shapes: 현재 캔버스에 있는 모든 도형 목록
    public void save(Vector<GShape> shapes) {
        // 새 Vector 생성 — 원본과 별개의 독립적인 목록
        Vector<GShape> snapshot = new Vector<>();
        // 각 도형을 clone()으로 깊은 복사
        // clone()은 GShape에 정의된 메서드로, 도형의 좌표/크기 등을 복제
        // 얕은 복사(참조만 복사)하면 원본이 변경될 때 스냅샷도 같이 변해서 무의미
        for (GShape s : shapes) snapshot.add(s.clone());
        // 스택에 push — 가장 위에 쌓임 (나중에 pop하면 이게 먼저 나옴)
        this.undoStack.push(snapshot);
    }

    // 직전 상태로 되돌리기
    // 반환값: 복원할 도형 목록. 스택이 비어있으면 null (되돌릴 게 없음)
    // GDrawingPanel에서 Ctrl+Z 누를 때 호출됨
    public Vector<GShape> undo() {
        // 삼항 연산자: 스택이 비었으면 null, 아니면 pop()으로 가장 최근 스냅샷 꺼냄
        // pop()은 꺼내면서 스택에서 제거됨
        return this.undoStack.isEmpty() ? null : this.undoStack.pop();
    }
}