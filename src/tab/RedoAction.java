package tab;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

public 	class RedoAction extends AbstractAction {
	
	private UndoManager undoManager;
	private UndoAction undoAction;

	public RedoAction(UndoManager undoManager, UndoAction undoAction) {
		super("Redo");
		setEnabled(false);
		this.undoManager = undoManager;
		this.undoAction = undoAction;
	}


	public void actionPerformed(ActionEvent e) {
		try {
			undoManager.redo();
		} catch (CannotRedoException ex) {
			// TODO deal with this
			ex.printStackTrace();
		}
		update();
		undoAction.update();
	}

	protected void update() {
		if (undoManager.canRedo()) {
			setEnabled(true);
			putValue(Action.NAME, undoManager.getRedoPresentationName());
		} else {
			setEnabled(false);
			putValue(Action.NAME, "Redo");
		}
	}
}


