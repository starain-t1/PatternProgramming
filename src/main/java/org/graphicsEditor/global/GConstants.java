package org.graphicsEditor.global;

import org.graphicsEditor.shapes.*;

public class GConstants {
    public enum EDrawingType { e2Point, eNPoint; }

    public enum EShapeType {
        eSelect  ("선택",   new GRectangle(), EDrawingType.e2Point),
        eRectangle("네모",  new GRectangle(), EDrawingType.e2Point),
        eOval    ("동그라미",new GOval(),      EDrawingType.e2Point),
        eLine    ("라인",   new GLine(),      EDrawingType.e2Point),
        ePolygon ("폴리곤", new GPolygon(),   EDrawingType.eNPoint),
        eTriangle("삼각형", new GTriangle(),  EDrawingType.e2Point),
        eText    ("텍스트",  new GText(),      EDrawingType.e2Point); // <-- 추가된 부분

        private final String name;
        private final GShape shape;
        private final EDrawingType eDrawingType;

        private EShapeType(String name, GShape shape, EDrawingType drawingType) {
            this.name = name; this.shape = shape; this.eDrawingType = drawingType;
        }
        public String getName() { return this.name; }
        public GShape getShape() { return this.shape.clone(); }
        public EDrawingType getDrawingType() { return this.eDrawingType; }
    };
}