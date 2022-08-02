package tab;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class UndoAction extends AbstractAction {
	
	private UndoManager undoManager;
	private RedoAction redoAction;
	
	public UndoAction(UndoManager undoManager) {
		super("Undo");
		setEnabled(false);
		this.undoManager = undoManager;
	}

	public void setRedoAction(RedoAction redoAction) {
		this.redoAction = redoAction;
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			undoManager.undo();
		} catch (CannotUndoException ex) {
			// TODO deal with this
			ex.printStackTrace();
		}
		update();
		redoAction.update();
	}

	protected void update() {
		if (undoManager.canUndo()) {
			setEnabled(true);
			putValue(Action.NAME, undoManager.getUndoPresentationName());
		} else {
			setEnabled(false);
			putValue(Action.NAME, "Undo");
		}
	}
}
