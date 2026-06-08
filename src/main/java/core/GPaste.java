package core;

import org.graphicsEditor.shapes.GShape;
import java.util.Vector;

// 붙여넣기 기능 클래스 (Ctrl+V)
// 클립보드에 있는 도형을 약간 오프셋해서 캔버스에 추가
public class GPaste {

    // 붙여넣을 때마다 위치를 조금씩 이동시켜 겹침 방지
    private static final int OFFSET = 10;
    private int pasteCount = 0; // 연속 붙여넣기 횟수 추적

    // clipboard의 도형을 깊은 복사 후 오프셋 적용해 shapes에 추가
    // 매개변수 shapes: 캔버스의 전체 도형 목록
    // 매개변수 clipboard: GCopy.getClipboard()에서 받아온 복사본
    public void execute(Vector<GShape> shapes, Vector<GShape> clipboard) {
        if (clipboard.isEmpty()) return;
        pasteCount++;
        int shift = OFFSET * pasteCount;

        // 기존 선택 해제 후 붙여넣은 도형만 선택 상태로
        for (GShape s : shapes) s.setSelected(false);

        for (GShape s : clipboard) {
            GShape copy = s.clone();
            copy.translate(OFFSET, OFFSET);
            copy.setSelected(true);
            shapes.add(copy);
        }
    }

    // 새 복사 작업 시작 시 오프셋 초기화
    public void resetPasteCount() { pasteCount = 0; }
}