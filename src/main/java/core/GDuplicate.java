package core;

import org.graphicsEditor.shapes.GShape;
import java.util.Vector;

public class GDuplicate {
    private static final int OFFSET = 10;

    public static void execute(Vector<GShape> shapes) {
        Vector<GShape> copies = new Vector<>();
        for (GShape s : shapes) {
            if (s.isSelected()) {
                GShape copy = s.clone();
                copy.translate(OFFSET, OFFSET);
                copies.add(copy);
            }
        }
        for (GShape s : shapes) s.setSelected(false);
        shapes.addAll(copies);
    }
}