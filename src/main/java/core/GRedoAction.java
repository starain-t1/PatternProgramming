package core;

import org.graphicsEditor.shapes.GShape;
import java.util.Stack;
import java.util.Vector;

public class GRedoAction {
    private final Stack<Vector<GShape>> redoStack = new Stack<>();

    public void save(Vector<GShape> shapes) {
        Vector<GShape> snapshot = new Vector<>();
        for (GShape s : shapes) snapshot.add(s.clone());
        redoStack.push(snapshot);
    }

    public Vector<GShape> redo() {
        return redoStack.isEmpty() ? null : redoStack.pop();
    }

    public void clear() { redoStack.clear(); }
}