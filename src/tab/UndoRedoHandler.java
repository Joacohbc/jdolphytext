package tab;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

// SACADO DE: https://alvinalexander.com/java/java-undo-redo/
public class UndoRedoHandler implements UndoableEditListener {

	private UndoManager undoManager;
	private UndoAction undoAction;
	private RedoAction redoAction;
	
	public UndoRedoHandler() {
		this.undoManager = new UndoManager();
		this.undoAction = new UndoAction(undoManager, redoAction);
		this.redoAction = new RedoAction(undoManager, undoAction);
	}
	
	public UndoManager getUndoManager() {
		return undoManager;
	}

	public UndoAction getUndoAction() {
		return undoAction;
	}

	public RedoAction getRedoAction() {
		return redoAction;
	}
	
	public void undoableEditHappened(UndoableEditEvent e) {
		undoManager.addEdit(e.getEdit());
		undoAction.update();
		redoAction.update();
	}
}
