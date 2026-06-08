package org.graphicsEditor.frames;

import org.graphicsEditor.menus.GFileMenu;
import org.graphicsEditor.menus.GHelpMenu;

import javax.swing.*;
import java.awt.event.*;

public class GMenuBar extends JMenuBar {
	// components
	private GFileMenu fileMenu;
	private EditMenu editMenu;

	public GMenuBar() {
		this.fileMenu = new GFileMenu();
		this.editMenu = new EditMenu();
		this.add(this.fileMenu);
		this.add(this.editMenu);
		this.add(new SpriteMenu());
		this.add(new LayerMenu());
		this.add(new FrameMenu());
		this.add(new SelectMenu());
		this.add(new GHelpMenu());
	}

	public GFileMenu getFileMenu() { return this.fileMenu; }
	public void associateDrawingPanel(GDrawingPanel panel) { this.editMenu.associateDrawingPanel(panel); }

	private class EditMenu extends JMenu {
		// associations
		private GDrawingPanel panel;

		// 구현된 항목 필드
		private final JMenuItem undoItem;
		private final JMenuItem redoItem;
		private final JMenuItem copyItem;
		private final JMenuItem pasteItem;
		private final JMenuItem cutItem;
		private final JMenuItem duplicateItem;
		private final JMenuItem deleteItem;
		private final JMenuItem selectAllItem;

		EditMenu() {
			super("Edit");

			this.undoItem = item("Undo",           KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
			this.redoItem = item("Redo",           KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
			this.add(this.undoItem);
			this.add(this.redoItem);
			this.add(noKey("Undo History")).setEnabled(false);
			this.addSeparator();

			this.cutItem   = item("Cut",   KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK);
			this.copyItem  = item("Copy",  KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
			this.pasteItem = item("Paste", KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
			this.add(this.cutItem);
			this.add(this.copyItem);
			this.add(noKey("Copy Merged")).setEnabled(false);
			this.add(this.pasteItem);
			this.add(noKey("Paste Special")).setEnabled(false);

			this.deleteItem    = item("Delete",    KeyEvent.VK_DELETE, 0);
			this.duplicateItem = item("Duplicate", KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);
			this.add(this.deleteItem);
			this.add(this.duplicateItem);
			this.addSeparator();

			this.selectAllItem = item("Select All", KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK);
			this.add(this.selectAllItem);
			this.addSeparator();

			// 미구현 항목들 — 단축키 없이 비활성
			for (String s : new String[]{"Fill", "Stroke"})
				this.add(noKey(s)).setEnabled(false);
			this.addSeparator();
			for (String s : new String[]{"Rotate", "Flip Horizontal", "Flip Vertical", "Transform", "Shift"})
				this.add(noKey(s)).setEnabled(false);
			this.addSeparator();
			for (String s : new String[]{"New Brush", "New Sprite From Selection"})
				this.add(noKey(s)).setEnabled(false);
			this.addSeparator();
			for (String s : new String[]{"Replace Color...", "Invert...", "Adjustments", "FX", "Insert Text"})
				this.add(noKey(s)).setEnabled(false);
			this.addSeparator();
			for (String s : new String[]{"Keyboard Shortcuts...", "Preferences..."})
				this.add(noKey(s)).setEnabled(false);

			// 리스너 등록
			ActionHandler ah = new ActionHandler();
			JMenuItem[] items = {undoItem, redoItem, copyItem, pasteItem, cutItem, duplicateItem, deleteItem, selectAllItem};
			for (JMenuItem m : items) m.addActionListener(ah);
		}

		void associateDrawingPanel(GDrawingPanel panel) { this.panel = panel; }

		private class ActionHandler implements ActionListener {
			@Override public void actionPerformed(ActionEvent e) {
				if (EditMenu.this.panel == null) return;
				Object src = e.getSource();
				if      (src == undoItem)      EditMenu.this.panel.undoAction();
				else if (src == redoItem)      EditMenu.this.panel.redoAction();
				else if (src == copyItem)      EditMenu.this.panel.copyAction();
				else if (src == pasteItem)     EditMenu.this.panel.pasteAction();
				else if (src == cutItem)       EditMenu.this.panel.cutAction();
				else if (src == duplicateItem) EditMenu.this.panel.duplicateAction();
				else if (src == deleteItem)    EditMenu.this.panel.deleteAction();
				else if (src == selectAllItem) EditMenu.this.panel.selectAllAction();
			}
		}

		private static JMenuItem item(String text, int key, int mod) {
			JMenuItem m = new JMenuItem(text); m.setAccelerator(KeyStroke.getKeyStroke(key, mod)); return m;
		}
		private static JMenuItem noKey(String text) { return new JMenuItem(text); }
	}

	private static class SpriteMenu extends JMenu { SpriteMenu() { super("Sprite"); } }
	private static class LayerMenu  extends JMenu { LayerMenu()  { super("Layer");  } }
	private static class FrameMenu  extends JMenu { FrameMenu()  { super("Frame");  } }
	private static class SelectMenu extends JMenu { SelectMenu() { super("Select"); } }
	private static class HelpMenu   extends JMenu { HelpMenu()   { super("Help");   } }
}