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
import java.util.List;

import javax.swing.JLabel;
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
import mensajes.Mensajes;

public class Tab extends JPanel {

	private TabsManager tabManager;
	private JTextPane textTexto;
	private JLabel lblRuta;

	// Este constructor es para que cuando hay que cargar una nueva Tab con un
	// contenido que ya existe
	public Tab(JTextPane textTextoEntrada, TabsManager parent) {
		super();

		// Asigno los atributos de parent y textTexto
		this.tabManager = parent;
		this.textTexto = textTextoEntrada; // No uso setText() para que el texto no pierda el estilo

		// Le asigno el BorderLayout a nuestro JPanel
		this.setLayout(new BorderLayout(0, 0));

		// Habilita el drag/drop de archivos
		// Sacado de:
		// https://java-demos.blogspot.com/2013/06/drag-and-drop-file-in-jtextarea.html
		new DropTarget(textTexto, new DropTargetListener() {

			public void dragEnter(DropTargetDragEvent e) {
			}

			public void dragExit(DropTargetEvent e) {
			}

			public void dragOver(DropTargetDragEvent e) {
			}

			public void dropActionChanged(DropTargetDragEvent e) {
			}

			@SuppressWarnings("unchecked")
			public void drop(DropTargetDropEvent e) {
				try {
					// Accept the drop first, important!
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

					// Get the files that are dropped as java.util.List
					List<File> listArchivos = (List<File>) e.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);

					// Now get the first file from the list,
					for (File file : listArchivos) {
						TabsManagerUtils.AbrirTab(tabManager, file.getPath());
					}

				} catch (Exception ex) {
					Mensajes.ErrorMessage(tabManager,
							"Ocurrio un error al abrir el archivo: " + ex.getMessage(), Mensajes.ErrorOpenTitle);
				}
			}
		});

		// SACADO DE: https://alvinalexander.com/java/java-undo-redo/
		Document editorPaneDocument = textTexto.getDocument();
		UndoRedoHandler undoRedoHandler = new UndoRedoHandler();
		
		KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK);
		KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK);

		textTexto.getInputMap().put(undoKeystroke, "undoKeystroke");
		textTexto.getActionMap().put("undoKeystroke", undoRedoHandler.getUndoAction());

		textTexto.getInputMap().put(redoKeystroke, "redoKeystroke");
		textTexto.getActionMap().put("redoKeystroke", undoRedoHandler.getRedoAction());

		editorPaneDocument.addUndoableEditListener(undoRedoHandler);

		textTexto.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (getTextoSeleccionado()[1] > 0) {// Si hay algo seleccionado

					StyledDocument doc = (StyledDocument) textTexto.getDocument();
					Element element = doc.getCharacterElement(getTextoSeleccionado()[0]);
					AttributeSet as = element.getAttributes();
					MutableAttributeSet asNew = new SimpleAttributeSet(as.copyAttributes());

					// Si esta Ctrl presionado
					if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {

						// Si B esta presionado, aplicar negrita
						if (e.getKeyCode() == KeyEvent.VK_B) {

							StyleConstants.setBold(asNew, !StyleConstants.isBold(as));
							doc.setCharacterAttributes(getTextoSeleccionado()[0], getTextoSeleccionado()[1], asNew,
									true);

							// Si K esta presionado, aplicar Italica
						} else if (e.getKeyCode() == KeyEvent.VK_K) {

							StyleConstants.setItalic(asNew, !StyleConstants.isItalic(as));
							doc.setCharacterAttributes(getTextoSeleccionado()[0], getTextoSeleccionado()[1], asNew,
									true);

							// Si U esta presionado, aplicar sburayado
						} else if (e.getKeyCode() == KeyEvent.VK_U) {

							StyleConstants.setUnderline(asNew, !StyleConstants.isUnderline(as));
							doc.setCharacterAttributes(getTextoSeleccionado()[0], getTextoSeleccionado()[1], asNew,
									true);

							// Si T esta presionado, aplicar tachado
						} else if (e.getKeyCode() == KeyEvent.VK_T) {

							StyleConstants.setStrikeThrough(asNew, !StyleConstants.isStrikeThrough(as));
							doc.setCharacterAttributes(getTextoSeleccionado()[0], getTextoSeleccionado()[1], asNew,
									true);

							// Si Q esta presionado, quitar todos los estilos
						} else if (e.getKeyCode() == KeyEvent.VK_Q) {

							StyleConstants.setBold(asNew, false);
							StyleConstants.setItalic(asNew, false);
							StyleConstants.setUnderline(asNew, false);
							StyleConstants.setStrikeThrough(asNew, false);

							doc.setCharacterAttributes(getTextoSeleccionado()[0], getTextoSeleccionado()[1], asNew,
									true);

							// Si G esta presionado, cambiar Mayusculas por Minusculso y viceversa
						} else if (e.getKeyCode() == KeyEvent.VK_G) {

							try {

								String textoSeleccionado = textTexto.getSelectedText();
								int[] seleccion = { getTextoSeleccionado()[0],
										(getTextoSeleccionado()[0] + getTextoSeleccionado()[1]) };

								// Pasar de Mayus -> Minus, y viceversa, dependiendo de la 1ra Letra
								if (Character.isUpperCase(textoSeleccionado.charAt(0))) {
									doc.remove(getTextoSeleccionado()[0], getTextoSeleccionado()[1]);
									doc.insertString(getTextoSeleccionado()[0], textoSeleccionado.toLowerCase(), asNew);
								} else {
									doc.remove(getTextoSeleccionado()[0], getTextoSeleccionado()[1]);
									doc.insertString(getTextoSeleccionado()[0], textoSeleccionado.toUpperCase(), asNew);
								}

								// Para dejar la seleccion puesta como estaba
								textTexto.setSelectionStart(seleccion[0]);
								textTexto.setSelectionEnd(seleccion[1]);

							} catch (BadLocationException e1) {
								Mensajes.ErrorMessage(tabManager,
										"Ocurrio un error al autocompletar: " + e1.getMessage(),
										"Error de Mayus/Minus");
							}

						}

						// Si el Tab esta presionado
					} else if (e.getKeyCode() == KeyEvent.VK_TAB) {

						e.consume();// Que no aplique la tabulacion
						try {

							if (textTexto.getSelectedText().equals("n")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "ñ", null);

							} else if (textTexto.getSelectedText().equals("N")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "Ñ", null);

							} else if (textTexto.getSelectedText().equals("a")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "á", null);

							} else if (textTexto.getSelectedText().equals("A")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "Á", null);

							} else if (textTexto.getSelectedText().equals("e")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "é", null);

							} else if (textTexto.getSelectedText().equals("E")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "É", null);

							} else if (textTexto.getSelectedText().equals("i")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "í", null);

							} else if (textTexto.getSelectedText().equals("I")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "Í", null);

							} else if (textTexto.getSelectedText().equals("o")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "ó", null);

							} else if (textTexto.getSelectedText().equals("O")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "Ó", null);

							} else if (textTexto.getSelectedText().equals("u")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "ú", null);

							} else if (textTexto.getSelectedText().equals("U")) {
								doc.remove(getTextoSeleccionado()[0], 1);
								doc.insertString(getTextoSeleccionado()[0], "Ú", null);
							}

						} catch (BadLocationException e1) {
							Mensajes.ErrorMessage(tabManager,
									"Ocurrio un error al intentar autocompletar: " + e1.getMessage(),
									"Error de completado");
						}

					}

					// Si Ctrl+N es presionado, abrir nueva Tab
				} else if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
					tabManager.abrirNuevaTab();

					// Si Ctrl+M es presionado, devolver ultiam tab borrada
				} else if ((e.getKeyCode() == KeyEvent.VK_M) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {

					if (!tabManager.abrirTabBorrada()) {
						Mensajes.ErrorMessage(tabManager, "No hay pestanias borradas guardadas", "Retroceso");
					}

					// Si F12 es presionado, Borar todaas las pestanias guardadas
				} else if (e.getKeyCode() == KeyEvent.VK_F12) {
					tabManager.vaciarTabsBorradas();

					// Si Ctrl+M es presionado, Guardar
				} else if (e.getKeyCode() == KeyEvent.VK_S && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
					try {
						TabsManagerUtils.GuardarTabSeleccioanda(tabManager, false);
					} catch (Exception e1) {
						Mensajes.ErrorMessage(tabManager, "Error al guardar el archivo: " + e1.getMessage(),
								Mensajes.ErrorSaveTitle);
					}

					// Si Ctrl+O es presionado, abrir uan nueva Tab
				} else if (e.getKeyCode() == KeyEvent.VK_O && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
					try {
						TabsManagerUtils.AbrirTab(tabManager);
					} catch (Exception e1) {
						Mensajes.ErrorMessage(tabManager, "Error al abrir el archivo: " + e1.getMessage(),
								Mensajes.ErrorOpenTitle);
					}

					// Si Ctrl+P es presionado, imprimir la tab actual
				} else if (e.getKeyCode() == KeyEvent.VK_P && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
					try {
						imprimir();
					} catch (PrinterException e1) {
						Mensajes.ErrorMessage(tabManager, "Error al imprimir el archivo: " + e1.getMessage(),
								Mensajes.ErrorPrintTitle);
					}

					// Si Ctrl+W es presionado, borrar la Tab actual
				} else if ((e.getKeyCode() == KeyEvent.VK_W) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
					if (tabManager.checkearTabs()) {// Si hay +1 de tab
						tabManager.cerrarTabSeleccionada();
					} else {
						Mensajes.WarningMessage(tabManager, "No puede cerrar ventanas si solo hay una abierta",
								Mensajes.WarningTitle);
					}
				}
			}

			// Agrego la opcion completado
			@Override
			public void keyTyped(KeyEvent e) {
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
						completarCaracter(e, "¿?");
						break;
					}
					case '!': {
						completarCaracter(e, "¡!");
						break;
					}
					default:
						break;
				}
			}
		});

		// Agrego la opcion de Zoom
		textTexto.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

				if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {

					// Obtengo el StyledDoc del texto
					StyledDocument doc = textTexto.getStyledDocument();

					// Creo un MutableAtrributSet y le seteo el fontSize en el del texto
					MutableAttributeSet as = new SimpleAttributeSet();
					StyleConstants.setFontSize(as, textTexto.getFont().getSize());// Para que no sea simpre el tamanio
																					// default(12) y el max siempre sea
																					// 18

					if (e.getWheelRotation() > 0) {// Si esta girando hacia arriba o hacia abajo

						if (Math.round(StyleConstants.getFontSize(as) / 1.2) > 1) {// Para que el MathRound no devuelva
																					// 0 y
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

						if ((Math.round(StyleConstants.getFontSize(as) * 1.2) < 1000)) {// Para que el MathRound no
																						// devuelva
																						// +1000 y no se vea el texto
																						// por
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
		});
		JScrollPane scrollPane = new JScrollPane();
		this.add(scrollPane, BorderLayout.CENTER);

		scrollPane.setViewportView(textTexto);

		lblRuta = new JLabel();
		lblRuta.setText("");
		lblRuta.setFocusable(false);
		lblRuta.setVisible(true);
		this.add(lblRuta, BorderLayout.SOUTH);

	}

	// Este constructor es para que cuando abra un nuevoTab
	public Tab(TabsManager parent) {
		this(new JTextPane(), parent);
	}

	private void completarCaracter(KeyEvent e, String queCompeltar) {

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
			Mensajes.ErrorMessage("Ocurrio un error: " + e1.getMessage(), "Error de completado");
		}

	}

	private int[] getTextoSeleccionado() {
		if (textTexto.getSelectedText() != null) {
			int[] seleccion = { textTexto.getSelectionStart(), textTexto.getSelectedText().length() };
			return seleccion;
		} else {
			int[] seleccion = { textTexto.getSelectionStart(), 0 };
			return seleccion;
		}
	}

	public void imprimir() throws PrinterException {
		textTexto.print();
	}

	public void darFocus() {
		textTexto.requestFocus();
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
}
