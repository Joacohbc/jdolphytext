package editor;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.text.*;

import gestor.TabsManager;
import gestor.TabsManagerUtils;

import javax.swing.JSeparator;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class JDolphyText extends JFrame {

	private static JDolphyText frame;

	private JPanel contentPane;
	private JMenuItem mnItemGuardarComo;
	private JMenuItem mnItemGuardar;
	private JMenuItem mnItemAbrir;
	private JMenuItem mnItemImprimir;
	private JRadioButtonMenuItem mnrdbtnSimpreAlFrente;
	private TabsManager tabManager;

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
				tabManager.abrirNuevaTab();
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
				try {
					TabsManagerUtils.AbrirTab(tabManager);
				} catch (IOException | BadLocationException e1) {
					JOptionPane.showMessageDialog(tabManager, "Error al abrir el archivo: " + e1.getMessage(),
							"Error al abrir", JOptionPane.ERROR_MESSAGE);
				}
			}//
		});//

		mnArchivo.add(mnItemAbrir);

		mnItemGuardar = new JMenuItem("Guardar...");
		mnItemGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					TabsManagerUtils.GuardarTabSeleccioanda(tabManager, false);
				} catch (IOException | BadLocationException e1) {
					JOptionPane.showMessageDialog(tabManager, "Error al abrir el archivo: " + e1.getMessage(),
							"Error al abrir", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mnArchivo.add(mnItemGuardar);

		mnItemGuardarComo = new JMenuItem("Guardar como...");
		mnItemGuardarComo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					TabsManagerUtils.GuardarComoTabSeleccioanda(tabManager, false);
				} catch (IOException | BadLocationException e1) {
					JOptionPane.showMessageDialog(tabManager, "Error al abrir el archivo: " + e1.getMessage(),
							"Error al abrir", JOptionPane.ERROR_MESSAGE);
				}
			}//
		});///
		mnArchivo.add(mnItemGuardarComo);

		mnItemImprimir = new JMenuItem("Imprimir...");
		mnItemImprimir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					tabManager.getSelectedComponent().imprimir();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(tabManager, "Error al imprimir el archivo: " + e1.getMessage(),
							"Error al imprimir", JOptionPane.ERROR_MESSAGE);
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

		tabManager = new TabsManager(JTabbedPane.TOP);
		tabManager.setFocusable(false);
		contentPane.add(tabManager);

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

	private void salir() {

		// Pregunto
		int op = JOptionPane.showConfirmDialog(null, "�Quieres salir?", "Cerrar programa", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE);

		// Si acepta cerrar
		if (op == 0) {

			tabManager.setSelectedIndex(0);
			// Pregunta
			int opGuardar = JOptionPane.showConfirmDialog(null, "�Quieres guardar las pesta�as?", "Cerrar programa",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			// Si acepta guardar
			if (opGuardar == 0) {
				for (int i = 0; i < tabManager.getTabCount(); i++) {
					try {
						TabsManagerUtils.GuardarTabSeleccioanda(tabManager, false);
					} catch (IOException | BadLocationException e1) {
						JOptionPane.showMessageDialog(tabManager, "Error al guardar el archivo: " + e1.getMessage(), "Error al guardar",
								JOptionPane.ERROR_MESSAGE);
						continue;
					}
					tabManager.cerrarTabSeleccionada();
				}
			}

			System.exit(-1);
		}
	}
}
