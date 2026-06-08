package core;

import org.graphicsEditor.shapes.GShape;
import java.util.Vector;

// 복사 기능 클래스 (Ctrl+C)
// 선택된 도형들을 클립보드(내부 버퍼)에 깊은 복사로 저장
public class GCopy {

    // 클립보드 역할: 복사된 도형들의 스냅샷을 보관
    private Vector<GShape> clipboard = new Vector<>();

    // 선택된 도형만 골라서 clipboard에 깊은 복사
    // 매개변수 shapes: 캔버스의 전체 도형 목록
    public void execute(Vector<GShape> shapes) {
        clipboard.clear();
        for (GShape s : shapes) {
            if (s.isSelected()) clipboard.add(s.clone());
        }
    }

    // Gpaste가 붙여넣을 때 클립보드 내용을 가져감
    public Vector<GShape> getClipboard() { return clipboard; }
}