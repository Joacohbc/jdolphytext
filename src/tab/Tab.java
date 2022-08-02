package tab;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import gestor.TabsManager;
import gestor.TabsManagerUtils;

public class Tab extends JPanel {

	private TabsManager tabManager;
	private JTextPane textTexto;
	private JLabel lblRuta;
	private Document editorPaneDocument;
	private UndoRedoHandler undoRedoHandler = new UndoRedoHandler();

	// Este constructor es para que cuando abra un nuevoTab
	// El nuevo JTextPane tenga la accion de Undo
	public Tab(JTextPane textTextoEntrada, TabsManager parent) {
		super();
		
		this.tabManager = parent;

		this.setLayout(new BorderLayout(0, 0));
		// Para que en que sea un RTF abierto, mantenga el Style si es RTF o el texto si
		// es .TXT
		textTexto = textTextoEntrada;

		// Habilita el drag/drop de archivos
		enableDragAndDrop();
		// ----------------------------------

		// SACADO DE: https://alvinalexander.com/java/java-undo-redo/
		editorPaneDocument = textTexto.getDocument();
		KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK);
		KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK);

		textTexto.getInputMap().put(undoKeystroke, "undoKeystroke");
		textTexto.getActionMap().put("undoKeystroke", undoRedoHandler.getUndoAction());

		textTexto.getInputMap().put(redoKeystroke, "redoKeystroke");
		textTexto.getActionMap().put("redoKeystroke", undoRedoHandler.getRedoAction());

		editorPaneDocument.addUndoableEditListener(undoRedoHandler);
		// ----------------------------------------------------------

		textTexto.setName("textTexto");
		textTexto.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				shortCuts(e);
			}

			public void keyTyped(KeyEvent e) {
				completar(e);
			}
		});
		textTexto.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				zoomTexto(e);
			}
		});
		JScrollPane scrollPane = new JScrollPane();
		this.add(scrollPane, BorderLayout.CENTER);

		scrollPane.setViewportView(textTexto);

		lblRuta = new JLabel();
		lblRuta.setText("");
		lblRuta.setName("lblRuta");
		lblRuta.setFocusable(false);
		lblRuta.setVisible(true);
		this.add(lblRuta, BorderLayout.SOUTH);

	}

	public Tab(TabsManager parent) {
		this(new JTextPane(), parent);
	}

	public void shortCuts(KeyEvent e) {

		if (seleccion()[1] > 0) {// Si hay algo seleccionado

			StyledDocument doc = (StyledDocument) textTexto.getDocument();
			Element element = doc.getCharacterElement(seleccion()[0]);
			AttributeSet as = element.getAttributes();
			MutableAttributeSet asNew = new SimpleAttributeSet(as.copyAttributes());

			// Si esta Ctrl presionado
			if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {

				// Aplicar negrita
				if (e.getKeyCode() == KeyEvent.VK_B) {

					StyleConstants.setBold(asNew, !StyleConstants.isBold(as));
					doc.setCharacterAttributes(seleccion()[0], seleccion()[1], asNew, true);

					// Aplicar Italica
				} else if (e.getKeyCode() == KeyEvent.VK_K) {

					StyleConstants.setItalic(asNew, !StyleConstants.isItalic(as));
					doc.setCharacterAttributes(seleccion()[0], seleccion()[1], asNew, true);

					// Aplicar sburayado
				} else if (e.getKeyCode() == KeyEvent.VK_U) {

					StyleConstants.setUnderline(asNew, !StyleConstants.isUnderline(as));
					doc.setCharacterAttributes(seleccion()[0], seleccion()[1], asNew, true);

					// Aplicar Rayado
				} else if (e.getKeyCode() == KeyEvent.VK_T) {

					StyleConstants.setStrikeThrough(asNew, !StyleConstants.isStrikeThrough(as));
					doc.setCharacterAttributes(seleccion()[0], seleccion()[1], asNew, true);

					// Quitar todos los estilos(Ctrl+Q)
				} else if (e.getKeyCode() == KeyEvent.VK_Q) {

					StyleConstants.setBold(asNew, false);
					StyleConstants.setItalic(asNew, false);
					StyleConstants.setUnderline(asNew, false);
					StyleConstants.setStrikeThrough(asNew, false);

					doc.setCharacterAttributes(seleccion()[0], seleccion()[1], asNew, true);

				} else if (e.getKeyCode() == KeyEvent.VK_G) {

					try {

						String textoSeleccionado = textTexto.getSelectedText();
						int[] seleccion = { seleccion()[0], (seleccion()[0] + seleccion()[1]) };

						// Pasar de Mayus -> Minus, y viceversa, dependiendo de la 1ra Letra
						if (Character.isUpperCase(textoSeleccionado.charAt(0))) {
							doc.remove(seleccion()[0], seleccion()[1]);
							doc.insertString(seleccion()[0], textoSeleccionado.toLowerCase(), asNew);
						} else {
							doc.remove(seleccion()[0], seleccion()[1]);
							doc.insertString(seleccion()[0], textoSeleccionado.toUpperCase(), asNew);
						}

						// Para dejar la seleccion puesta como estaba
						textTexto.setSelectionStart(seleccion[0]);
						textTexto.setSelectionEnd(seleccion[1]);

					} catch (BadLocationException e1) {
						JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e1.getMessage(),
								"Error de Mayus/Minus", JOptionPane.ERROR_MESSAGE);
					}

				}

				// Si el Tab esta presionado
			} else if (e.getKeyCode() == KeyEvent.VK_TAB) {// Cuando le das al TAB convertir Ns en �s

				e.consume();// Que no la tome a la letra
				try {

					if (textTexto.getSelectedText().equals("n")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("N")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("a")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("A")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("e")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("E")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("i")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("I")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("o")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("O")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("o")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("O")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("u")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);

					} else if (textTexto.getSelectedText().equals("U")) {
						doc.remove(seleccion()[0], 1);
						doc.insertString(seleccion()[0], "�", null);
					}

				} catch (BadLocationException e1) {
					JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e1.getMessage(), "Error de completado",
							JOptionPane.ERROR_MESSAGE);
				}

			}

			// Abrir pestnia
		} else if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
			tabManager.abrirNuevaTab();

			// Devolver pestanias borradas
		} else if ((e.getKeyCode() == KeyEvent.VK_M) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {

			if (!tabManager.abrirTabBorrada()) {
				JOptionPane.showMessageDialog(null, "No hay pestanias borradas guardadas", "Retroceso",
						JOptionPane.INFORMATION_MESSAGE);
			}

			// Borar todaas las pestanias guardadas
		} else if (e.getKeyCode() == KeyEvent.VK_F12) {
			tabManager.vaciarTabsBorradas();

			// Guardar
		} else if (e.getKeyCode() == KeyEvent.VK_S && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
			try {
				TabsManagerUtils.GuardarTabSeleccioanda(tabManager, false);
			} catch (IOException | BadLocationException e1) {
				JOptionPane.showMessageDialog(tabManager, "Error al guardar el archivo: " + e1.getMessage(), "Error al guardar",
						JOptionPane.ERROR_MESSAGE);
			}

			// Abrir
		} else if (e.getKeyCode() == KeyEvent.VK_O && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
			try {
				TabsManagerUtils.AbrirTab(tabManager);
			} catch (IOException | BadLocationException e1) {
				JOptionPane.showMessageDialog(tabManager, "Error al abrir el archivo: " + e1.getMessage(), "Error al abrir",
						JOptionPane.ERROR_MESSAGE);
			}

			// Imprimir
		} else if (e.getKeyCode() == KeyEvent.VK_P && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
			try {
				imprimir();
			} catch (PrinterException e1) {
				JOptionPane.showMessageDialog(tabManager, "Error al imprimir el archivo: " + e1.getMessage(), "Error al imprimir",
						JOptionPane.ERROR_MESSAGE);
			}
		}else if((e.getKeyCode() == KeyEvent.VK_W) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
			if (tabManager.checkearTabs()) {// Si hay +1 de tab
				tabManager.cerrarTabSeleccionada();
			}else {
				JOptionPane.showMessageDialog(tabManager, "No puede cerrar ventanas si solo hay una abierta", "Aviso de cierre",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void imprimir() throws PrinterException {
		textTexto.print();
	}

	public void completarCaracter(KeyEvent e, String queCompeltar) {

		// Sacado de:
		// https://stackoverflow.com/questions/8315859/how-to-ignore-the-key-press-event-in-swing
		e.consume();
		// -------------------------------------------------------------------------------------------------

		StyledDocument doc = textTexto.getStyledDocument();
		try {
			// Sacado de:
			// https://stackoverflow.com/questions/4059198/jtextpane-appending-a-new-string/4059365
			doc.insertString(textTexto.getCaretPosition(), queCompeltar, null);
			textTexto.setCaretPosition(textTexto.getCaretPosition() - 1);
		} catch (BadLocationException e1) {
			JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e1.getMessage(), "Error de completado",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	public void completar(KeyEvent e) {

		switch (e.getKeyChar()) {

		case '(': {
			completarCaracter(e, "()");
			break;
		}
		case '{': {
			completarCaracter(e, "{}");
			break;
		}
		case '[': {
			completarCaracter(e, "[]");
			break;
		}
		case '\"': {
			completarCaracter(e, "\"\"");
			break;
		}
		case '\'': {
			completarCaracter(e, "\'\'");
			break;
		}
		case '?': {
			completarCaracter(e, "�?");
			break;
		}
		case '!': {
			completarCaracter(e, "�!");
			break;
		}
		default:
			break;
		}

	}

	public void zoomTexto(MouseWheelEvent e) {

		if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {

			// Obtengo el StyledDoc del texto
			StyledDocument doc = textTexto.getStyledDocument();

			// Creo un MutableAtrributSet y le seteo el fontSize en el del texto
			MutableAttributeSet as = new SimpleAttributeSet();
			StyleConstants.setFontSize(as, textTexto.getFont().getSize());// Para que no sea simpre el tamanio
																			// default(12) y el max siempre sea 18

			if (e.getWheelRotation() > 0) {// Si esta girando hacia arriba o hacia abajo

				if (Math.round(StyleConstants.getFontSize(as) / 1.2) > 1) {// Para que el MathRound no devuelva 0 y
																			// no se vea el texto

					// Le doy el fontSize nuevo al atributo(el achicado)
					StyleConstants.setFontSize(as, (int) Math.round(StyleConstants.getFontSize(as) / 1.2));
					// Se lo aplica al StyledDoc
					doc.setCharacterAttributes(0, doc.getLength(), as, false);

					// Y Actualizo el tamanio de la fuente y se lo aplica al textPane
					Font f = new Font(textTexto.getFont().getName(), textTexto.getFont().getStyle(),
							(int) Math.round(textTexto.getFont().getSize() / 1.2));
					textTexto.setFont(f);
					// Para que la proxima vez cuando haga la multiplicacion no sea la misma(osea
					// que no sea 12/1.5 siempre)
				}

			} else {

				if ((Math.round(StyleConstants.getFontSize(as) * 1.2) < 1000)) {// Para que el MathRound no devuelva
																				// +1000 y no se vea el texto por
																				// ser demasiado grande

					// Le doy el fontSize nuevo al atributo(el agrandado)
					StyleConstants.setFontSize(as, (int) (Math.round(StyleConstants.getFontSize(as) * 1.2)));
					// Se lo aplica al StyledDoc
					doc.setCharacterAttributes(0, doc.getLength(), as, false);
					// Y Actualizo el tamanio de la fuente y se lo aplica al textPane
					Font f = new Font(textTexto.getFont().getName(), textTexto.getFont().getStyle(),
							(int) Math.round(textTexto.getFont().getSize() * 1.2));
					textTexto.setFont(f);
					// Para que la proxima vez cuando haga la multiplicacion no sea la misma(osea
					// que no sea 12x1.5 siempre)
				}

			}
		}
	}

	public void darFocus() {
		textTexto.requestFocus();
	}

	private int[] seleccion() {
		if (textTexto.getSelectedText() != null) {
			int[] seleccion = { textTexto.getSelectionStart(), textTexto.getSelectedText().length() };
			return seleccion;
		} else {
			int[] seleccion = { textTexto.getSelectionStart(), 0 };
			return seleccion;
		}
	}

	public JTextPane getJTextPane() {
		return textTexto;
	}

	public void setRuta(String ruta) {
		lblRuta.setText(ruta);
	}

	public String getRuta() {
		return lblRuta.getText();
	}

	public JPanel ToJPanel() {
		return this;
	}

	// Sacado de:
	// https://java-demos.blogspot.com/2013/06/drag-and-drop-file-in-jtextarea.html
	private void enableDragAndDrop() {

		new DropTarget(textTexto, new DropTargetListener() {

			public void dragEnter(DropTargetDragEvent e) {
			}

			public void dragExit(DropTargetEvent e) {
			}

			public void dragOver(DropTargetDragEvent e) {
			}

			public void dropActionChanged(DropTargetDragEvent e) {
			}

			public void drop(DropTargetDropEvent e) {
				try {
					// Accept the drop first, important!
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

					// Get the files that are dropped as java.util.List
					List listArchivos = (List) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

					// Now get the first file from the list,
					// File file = (File) listArchivos.get(0);
					for (int i = 0; i < listArchivos.size(); i++) {
						String ruta = ((File) listArchivos.get(i)).getPath();
						TabsManagerUtils.AbrirTab(tabManager, ruta);
					}

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Ocurrio un error: " + ex.getMessage(),
							"Error al abrir (externo)", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	// -----------------------------------------------------------------------------
}
