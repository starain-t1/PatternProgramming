package core;

import org.graphicsEditor.shapes.GShape;
import java.util.Vector;

public class GDeleteSelected {
    public static void execute(Vector<GShape> shapes) {
        shapes.removeIf(GShape::isSelected);
    }
}