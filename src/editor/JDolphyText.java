package editor;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.PrinterException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import javax.swing.JSeparator;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class JDolphyText extends JFrame {

	private static JDolphyText frame;

	private final ArrayList<JPanel> pestaniasBorradas = new ArrayList<JPanel>();
	private final ArrayList<String> pestaniasBorradasNombres = new ArrayList<String>();

	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private JMenuItem mnItemGuardarComo;
	private JMenuItem mnItemGuardar;
	private JMenuItem mnItemAbrir;
	private JMenuItem mnItemImprimir;
	private JRadioButtonMenuItem mnrdbtnSimpreAlFrente;

	private static TrayIcon trayIcon = null;
	private static SystemTray tray = SystemTray.getSystemTray();

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new JDolphyText();
					frame.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e.getMessage(),
							"Error al abrir el programa", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});
	}

	// Constructor del JFrame
	public JDolphyText() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}

			@Override
			public void windowOpened(WindowEvent e) {
				abrirPestaniaNueva();
			}
		});
		setMinimumSize(new Dimension(250, 200));
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
		setTitle("DolphyText");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 443, 297);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setFocusable(false);
		setJMenuBar(menuBar);

		JMenu mnArchivo = new JMenu("Archivo");
		menuBar.add(mnArchivo);

		mnItemAbrir = new JMenuItem("Abrir...");
		mnItemAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Llamo al form
				String elegido = llamarFileDialog(0);
				if (!elegido.equals("")) {
					abrir(elegido);
				}
			}//
		});//

		mnArchivo.add(mnItemAbrir);

		mnItemGuardar = new JMenuItem("Guardar...");
		mnItemGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String ruta;

				try {
					JLabel lbl = getLabelFromTab();
					ruta = lbl.getText();
				} catch (NullPointerException ex) {
					ex.printStackTrace();
					ruta = "";
				}

				if (new File(ruta).exists()) {
					guardar(getJTextPaneFromTab(), ruta, false);
				} else {
					mnItemGuardarComo.doClick();
				}
			}
		});
		mnArchivo.add(mnItemGuardar);

		mnItemGuardarComo = new JMenuItem("Guardar como...");
		mnItemGuardarComo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JTextPane textTexto = getJTextPaneFromTab();
				String elegido = llamarFileDialog(1);
				if (!elegido.equals("")) {
					guardar(textTexto, new File(elegido).getAbsolutePath(), true);
				}

			}//
		});///
		mnArchivo.add(mnItemGuardarComo);

		mnItemImprimir = new JMenuItem("Imprimir...");
		mnItemImprimir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JTextPane textTexto = getJTextPaneFromTab();
				try {
					textTexto.print();
				} catch (PrinterException e1) {
					JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e1.getMessage(), "Error al imprimir",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mnArchivo.add(mnItemImprimir);

		mnrdbtnSimpreAlFrente = new JRadioButtonMenuItem("Simpre al frente");
		mnrdbtnSimpreAlFrente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAlwaysOnTop(mnrdbtnSimpreAlFrente.isSelected());
			}
		});
		mnArchivo.add(mnrdbtnSimpreAlFrente);

		JSeparator separator = new JSeparator();
		mnArchivo.add(separator);

		JMenuItem mnItemSalir = new JMenuItem("Salir");
		mnItemSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				salir();
			}
		});
		mnArchivo.add(mnItemSalir);

		contentPane = new JPanel();
		contentPane.setFocusable(false);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFocusable(false);
		contentPane.add(tabbedPane);

		if (!SystemTray.isSupported()) {
			JOptionPane.showMessageDialog(null, "SystemTray no es soportado por el sistema",
					"Error al agreagar Iconito", JOptionPane.ERROR_MESSAGE);
		} else {

			try {

				// Poner la imagen de 16x16 sacado de:
				// https://stackoverflow.com/questions/31054799/tray-icon-simply-not-showing-in-java
				// URL resource = getClass().getResource("icon.png");
				// Toolkit.getDefaultToolkit().getImage(resource)

				// Sacado de:
				// https://stackoverflow.com/questions/12287137/system-tray-icon-looks-distorted#:~:text=To%20display%20the%20icon%20at,case%20of%20your%20example%20image.
				BufferedImage trayIconImage = ImageIO.read(getClass().getResource("icon.png"));

				int trayIconWidth = (int) SystemTray.getSystemTray().getTrayIconSize().getWidth();
				int trayIconHeight = (int) SystemTray.getSystemTray().getTrayIconSize().getHeight();

				PopupMenu popUpIconito = new PopupMenu();

				trayIcon = new TrayIcon(
						trayIconImage.getScaledInstance(trayIconWidth, trayIconHeight, Image.SCALE_SMOOTH),
						"DolphyText", popUpIconito);

				// trayIcon.setImageAutoSize(true);

				trayIcon.addMouseListener(new MouseAdapter() {

					public void mouseClicked(MouseEvent e) {

						if (frame.isVisible()) {
							frame.setVisible(false);
						} else {
							frame.setVisible(true);
							frame.setExtendedState(JFrame.NORMAL);
							frame.toFront();
						}

					}

				});

				MenuItem mnMostrarOcultar = new MenuItem("Mostrar/Ocultar");
				mnMostrarOcultar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (frame.isVisible()) {
							frame.setVisible(false);
						} else {
							frame.setVisible(true);
							frame.setExtendedState(JFrame.NORMAL);
							frame.toFront();
						}
					}
				});
				popUpIconito.add(mnMostrarOcultar);

				MenuItem mnSalir = new MenuItem("Salir");
				mnSalir.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						salir();
					}
				});
				popUpIconito.add(mnSalir);

				tray.add(trayIcon);
			} catch (AWTException e) {
				JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e.getMessage(), "Error al agreagar Iconito",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e1.getMessage(), "Error al agreagar Iconito",
						JOptionPane.ERROR_MESSAGE);
			}

		}

	}

	// Sacado de:
	// http://www.java2s.com/Code/Java/Swing-JFC/GetAllComponentsinacontainer.htm
	// Obtiene todos los elementos de un Container
	private List<Component> getAllComponents(Container container) {
		Component[] components = container.getComponents();
		List<Component> result = new ArrayList<Component>();
		for (Component component : components) {
			result.add(component);
			if (component instanceof Container) {
				result.addAll(getAllComponents((Container) component));
			}
		}
		return result;
	}

	// Obtiene el JTextPane de la tab seleccionada
	private JTextPane getJTextPaneFromTab() {
		// Obtiene todos los componentes del JPanel (la Tab) seleccionado dentro del
		// TabPane
		List<Component> components = getAllComponents((JPanel) tabbedPane.getSelectedComponent());

		// Recorro todos los compoentnes
		for (Component c : components) {

			// Busca el JTextPane y lo retorno
			if (c instanceof JTextPane) {
				JTextPane textTexto = (JTextPane) c;
				return textTexto;
			}
		}
		return null;
	}

	// Obtiene el Label de la tab seleccionada
	private JLabel getLabelFromTab() {

		// Obtiene todos los componentes del JPanel (la Tab) seleccionado dentro del
		// TabPane
		List<Component> components = getAllComponents((JPanel) tabbedPane.getSelectedComponent());
		for (Component c : components) {
			if (c instanceof JLabel) {// Busca el JTextPane
				JLabel lblRuta = (JLabel) c;
				return lblRuta;
			}
		}
		return null;
	}

	// Abre un FileDialog de abrir o guaradar un archivo, y retorna la Path seleccionada por el usuario
	// Ingresar 0 para abrir archivos y 1 para guardar archivos
	private String llamarFileDialog(int tipoDialogo) {

		// Para desativar el SimpreAlFrente cuando se
		// abra el FileDialog, para que este no quede detras
		if (mnrdbtnSimpreAlFrente.isSelected()) {
			if(tipoDialogo == 0) {
				JOptionPane.showMessageDialog(null, "Para abrir un archivo debe desactivar la opcion \"Siempre al frente\"", "Aviso",
						JOptionPane.WARNING_MESSAGE);
			}else {
				JOptionPane.showMessageDialog(null, "Para abrir un guardar debe desactivar la opcion \"Siempre al frente\"", "Aviso",
						JOptionPane.WARNING_MESSAGE);
			}
			return "";
		}

		JFileChooser chooser = new JFileChooser();

		// Para que habra en el escritorio
		chooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// Agrego que el filtro para el formato RTF
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos RTF", "rtf");
		chooser.addChoosableFileFilter(filter);

		// Agrego que el filtro para el formato TXT
		filter = new FileNameExtensionFilter("Archivos ", "txt");
		chooser.addChoosableFileFilter(filter);

		int option = -1;
		if (tipoDialogo == 1) {// Save
			option = chooser.showSaveDialog(null);
		} else if (tipoDialogo == 0) { // Open
			option = chooser.showOpenDialog(null);
		}
		
		return option == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile().getPath() : "";
	}

	private void guardar(JTextPane textTexto, String ruta, boolean cartel) {

		try {

			int index = ruta.lastIndexOf('.');
			String ext = "";
			if (index > 0) {
				ext = ruta.substring(index + 1);
			}

			if (ext.equals("rtf")) {

				// Sacado de:
				// https://www.codota.com/code/java/methods/javax.swing.text.rtf.RTFEditorKit/write
				// y
				// https://stackoverflow.com/questions/17488534/create-a-file-from-a-bytearrayoutputstream

				// Instancio doc, kit y baos
				Document doc = textTexto.getDocument();
				RTFEditorKit kit = new RTFEditorKit();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				// Escribo el JTextPane en formato RFT en el ByteArray
				kit.write(baos, doc, 0, doc.getLength());

				// Y guardo el ByteArray como archivo .rtf
				try (OutputStream outputStream = new FileOutputStream(ruta)) {
					baos.writeTo(outputStream);
				}

				tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), new File(ruta).getName());
				getLabelFromTab().setText(ruta);

			} else {
				FileWriter writer = new FileWriter(ruta);
				textTexto.write(writer);
			}

			if (cartel) {
				JOptionPane.showMessageDialog(null, "Su archivo se guardo en " + ruta, "Guardado...",
						JOptionPane.INFORMATION_MESSAGE);
			}

		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e.getMessage(), "Error al guardar",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e.getMessage(), "Error al guardar",
					JOptionPane.ERROR_MESSAGE);
		} catch (BadLocationException e) {
			JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e.getMessage(), "Error al guardar",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private void abrir(String ruta) {

		try {

			// Creo las variables
			JTextPane textTexto = new JTextPane();

			int index = ruta.lastIndexOf('.');
			String ext = "";
			if (index > 0) {
				ext = ruta.substring(index + 1);
			}

			if (ext.equals("rtf")) {

				RTFEditorKit kit = new RTFEditorKit();

				// Guardo el .rft que quiero abrir en InputStream
				InputStream inputStream = new FileInputStream(ruta);

				// Sacado de:
				// https://stackoverflow.com/questions/29524208/load-rtf-into-jtextpane

				// Creo un styled document
				DefaultStyledDocument styledDocument = new DefaultStyledDocument(new StyleContext());

				// Escribo el inputstream en el styled document
				kit.read(inputStream, styledDocument, 0);

				// Y se lo doy a al JTextPane
				textTexto.setDocument(styledDocument);
				inputStream.close();

				NuevaTab pn = new NuevaTab(textTexto);
				pn.darRuta(ruta);
				tabbedPane.addTab(new File(ruta).getName(), pn);

			} else {

				FileReader reader = new FileReader(ruta);
				textTexto.read(reader, ruta);
				reader.close();

				NuevaTab pn = new NuevaTab(textTexto);
				pn.darRuta(ruta);
				tabbedPane.addTab(new File(ruta).getName(), pn);

			}

		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e.getMessage(), "Error al abrir",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e.getMessage(), "Error al abrir",
					JOptionPane.ERROR_MESSAGE);
		} catch (BadLocationException e) {
			JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e.getMessage(), "Error al abrir",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void abrirPestaniaNueva() {
		NuevaTab pn = new NuevaTab(new JTextPane());
		tabbedPane.addTab("Nueva ventana", pn);
		pn.darFocus();// <-- Para que simpre tenga el focus, y simpre pueda usar Control+W y Control+N
	}

	private void cerrarPestania() {
		pestaniasBorradas.add((JPanel) tabbedPane.getSelectedComponent());
		pestaniasBorradasNombres.add(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
		tabbedPane.remove(tabbedPane.getSelectedComponent());
	}

	private void salir() {

		// Pregunto
		int op = JOptionPane.showConfirmDialog(null, "�Quieres salir?", "Cerrar programa", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE);

		// Si acepta cerrar
		if (op == 0) {

			tabbedPane.setSelectedIndex(0);
			// Pregunta
			int opGuardar = JOptionPane.showConfirmDialog(null, "�Quieres guardar las pesta�as?", "Cerrar programa",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			// Si acepta guardar
			if (opGuardar == 0) {
				for (int i = 0; i < tabbedPane.getTabCount(); i++) {
					// La guarda
					mnItemGuardar.doClick();
					// La borra
					cerrarPestania();
				}
			}

			System.exit(-1);
		}
	}

	private boolean checkearTabs() {

		if (tabbedPane.getTabCount() > 1) {
			return true;
		}
		return false;

	}

	private class NuevaTab extends JPanel {

		JTextPane textTexto;
		JLabel lblRuta;

		// SACADO DE: https://alvinalexander.com/java/java-undo-redo/
		private Document editorPaneDocument;
		private UndoManager undoManager = new UndoManager();
		private UndoHandler undoHandler = new UndoHandler();
		private UndoAction undoAction = null;
		private RedoAction redoAction = null;

		// Este constructor es para que cuando abra un nuevoTab
		// El nuevo JTextPane tenga la accion de Undo
		public NuevaTab(JTextPane textTextoEntrada) {

			setLayout(new BorderLayout(0, 0));

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

			undoAction = new UndoAction();
			textTexto.getInputMap().put(undoKeystroke, "undoKeystroke");
			textTexto.getActionMap().put("undoKeystroke", undoAction);

			redoAction = new RedoAction();
			textTexto.getInputMap().put(redoKeystroke, "redoKeystroke");
			textTexto.getActionMap().put("redoKeystroke", redoAction);

			editorPaneDocument.addUndoableEditListener(undoHandler);
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
			add(scrollPane, BorderLayout.CENTER);

			scrollPane.setViewportView(textTexto);

			lblRuta = new JLabel();
			lblRuta.setText("");
			lblRuta.setName("lblRuta");
			lblRuta.setFocusable(false);
			lblRuta.setVisible(true);
			add(lblRuta, BorderLayout.SOUTH);

		}

		// SACADO DE: https://alvinalexander.com/java/java-undo-redo/
		class UndoHandler implements UndoableEditListener {

			/**
			 * Messaged when the Document has created an edit, the edit is added to
			 * <code>undoManager</code>, an instance of UndoManager.
			 */
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
				undoAction.update();
				redoAction.update();
			}
		}

		class UndoAction extends AbstractAction {
			public UndoAction() {
				super("Undo");
				setEnabled(false);
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

		class RedoAction extends AbstractAction {
			public RedoAction() {
				super("Redo");
				setEnabled(false);
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
		// ---------------------------------------------------------

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
						JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e1.getMessage(),
								"Error de completado", JOptionPane.ERROR_MESSAGE);
					}

				}

				// Abrir pestnia
			} else if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
				abrirPestaniaNueva();

				// Devolver pestanias borradas
			} else if ((e.getKeyCode() == KeyEvent.VK_M) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {

				if (!pestaniasBorradas.isEmpty() && !pestaniasBorradasNombres.isEmpty()) {

					String ultimoNombre = pestaniasBorradasNombres.get(pestaniasBorradasNombres.size() - 1);

					tabbedPane.add(ultimoNombre, pestaniasBorradas.get(pestaniasBorradas.size() - 1));

					pestaniasBorradasNombres.remove(pestaniasBorradasNombres.size() - 1);
					pestaniasBorradas.remove(pestaniasBorradas.get(pestaniasBorradas.size() - 1));

				} else {
					JOptionPane.showMessageDialog(null, "No hay pestanias borradas guardadas", "Retroceso",
							JOptionPane.INFORMATION_MESSAGE);
				}

				// Borar todaas las pestanias guardadas
			} else if (e.getKeyCode() == KeyEvent.VK_F12) {

				pestaniasBorradas.clear();
				pestaniasBorradasNombres.clear();

				// Guardar
			} else if (e.getKeyCode() == KeyEvent.VK_S && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
				mnItemGuardar.doClick();

				// Abrir
			} else if (e.getKeyCode() == KeyEvent.VK_O && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
				mnItemAbrir.doClick();

				// Imprimir
			} else if (e.getKeyCode() == KeyEvent.VK_P && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
				mnItemImprimir.doClick();

				// Salir
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				salir();
			}

			if (checkearTabs()) {// Si hay +1 de tab

				// Que la borres
				if ((e.getKeyCode() == KeyEvent.VK_W) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
					cerrarPestania();
				}
			}

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

		public void darRuta(String ruta) {
			lblRuta.setText(ruta);
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
							abrir(((File) listArchivos.get(i)).getPath());
						}

					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "Ocurrio un error: " + ex.getMessage(),
								"Error al abrir(externo)", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
		// -----------------------------------------------------------------------------
	}

}
