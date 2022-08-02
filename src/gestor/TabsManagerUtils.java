package gestor;

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

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyleContext;
import javax.swing.text.rtf.RTFEditorKit;

import tab.Tab;

public class TabsManagerUtils {

	private static void guardarRTF(TabsManager panel, Tab tab, String ruta) throws IOException, BadLocationException {
				
		// Sacado de:
		// https://www.codota.com/code/java/methods/javax.swing.text.rtf.RTFEditorKit/write
		// y
		// https://stackoverflow.com/questions/17488534/create-a-file-from-a-bytearrayoutputstream

		// Instancio doc, kit y baos
		Document doc = tab.getJTextPane().getDocument();
		RTFEditorKit kit = new RTFEditorKit();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// Escribo el JTextPane en formato RFT en el ByteArray
		kit.write(baos, doc, 0, doc.getLength());

		// Y guardo el ByteArray como archivo .rtf
		try (OutputStream outputStream = new FileOutputStream(ruta)) {
			baos.writeTo(outputStream);
		}

		panel.setTitleAt(panel.getSelectedIndex(), new File(ruta).getName());
		panel.getSelectedComponent().setRuta(ruta);
	}

	private static void guardarTexto(TabsManager panel, Tab tab, String ruta) throws IOException {
		FileWriter writer = new FileWriter(ruta);
		panel.getSelectedComponent().getJTextPane().write(writer);
	}

	public static boolean GuardarTabSeleccioanda(TabsManager panel, boolean cartel)
			throws IOException, BadLocationException, FileNotFoundException {

		Tab tab = panel.getSelectedComponent();

		if (tab.getRuta().isEmpty()) {
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

			chooser.setName("Guardar...");
			
			int option = chooser.showSaveDialog(panel);

			if (option == JFileChooser.CANCEL_OPTION) {
				return false;
			}

			tab.setRuta(chooser.getSelectedFile().getPath());
		}

		// Obtengo el formato del Archivo
		String ruta = tab.getRuta();
		int index = ruta.lastIndexOf('.');

		// Si el archivo no tiene extension que automaticamente lo guarde como RTF
		String ext = index > 0 ? ruta.substring(index + 1) : "rtf";

		if (ext.equals("rtf")) {

			// Sacado de:
			// https://www.codota.com/code/java/methods/javax.swing.text.rtf.RTFEditorKit/write
			// y
			// https://stackoverflow.com/questions/17488534/create-a-file-from-a-bytearrayoutputstream

			// Instancio doc, kit y baos
			Document doc = tab.getJTextPane().getDocument();
			RTFEditorKit kit = new RTFEditorKit();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// Escribo el JTextPane en formato RFT en el ByteArray
			kit.write(baos, doc, 0, doc.getLength());

			// Y guardo el ByteArray como archivo .rtf
			try (OutputStream outputStream = new FileOutputStream(ruta)) {
				baos.writeTo(outputStream);
			}

			panel.setTitleAt(panel.getSelectedIndex(), new File(ruta).getName());
			panel.getSelectedComponent().setRuta(ruta);

		} else if (ext.equals("txt")) {
			FileWriter writer = new FileWriter(ruta);
			panel.getSelectedComponent().getJTextPane().write(writer);
		} else {
			JOptionPane.showMessageDialog(null, "Formato selccionado invalido, la extension: " + ext + " no es valida",
					"Error al guardar", JOptionPane.ERROR_MESSAGE);
		}

		if (cartel) {
			JOptionPane.showMessageDialog(null, "Su archivo se guardo en " + ruta, "Guardado...",
					JOptionPane.INFORMATION_MESSAGE);
		}
		return true;
	}

	public static boolean GuardarComoTabSeleccioanda(TabsManager panel, boolean cartel)
			throws IOException, BadLocationException, FileNotFoundException {

		Tab tab = panel.getSelectedComponent();

		if (tab.getRuta().isEmpty()) {
			return GuardarTabSeleccioanda(panel, cartel);
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

		chooser.setName("Guardar como...");
		chooser.setSelectedFile(new File(tab.getRuta()));
		
		int option = chooser.showSaveDialog(panel);

		if (option == JFileChooser.CANCEL_OPTION) {
			return false;
		}

		tab.setRuta(chooser.getSelectedFile().getPath());

		// Obtengo el formato del Archivo
		String ruta = tab.getRuta();
		int index = ruta.lastIndexOf('.');

		// Si el archivo no tiene extension que automaticamente lo guarde como RTF
		String ext = index > 0 ? ruta.substring(index + 1) : "rtf";

		if (ext.equals("rtf")) {
			guardarRTF(panel, tab, ruta);

		} else if (ext.equals("txt")) {
			guardarTexto(panel, tab, ruta);

		} else {
			int opcion = JOptionPane.showConfirmDialog(panel, "Formato selccionado invalido, ¿Desea guardar igual?", "Aviso de formato no soportado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if(opcion == JOptionPane.YES_OPTION){
				guardarTexto(panel, tab, ruta);
			}
		}

		if (cartel) {
			JOptionPane.showMessageDialog(null, "Su archivo se guardo en " + ruta, "Guardado...",
					JOptionPane.INFORMATION_MESSAGE);
		}

		return true;
	}


	public static boolean AbrirTab(TabsManager panel) throws IOException, BadLocationException, FileNotFoundException {

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

		int option = chooser.showOpenDialog(panel);

		if (option == JFileChooser.CANCEL_OPTION) {
			return false;
		}
			
		String ruta = chooser.getSelectedFile().getPath();
		if(!new File(ruta).exists()) {
			JOptionPane.showMessageDialog(panel, "El archivo seleccionada no exite: "+ruta, "Error al abrir archivo",
					JOptionPane.ERROR_MESSAGE);		
			}
		
		// Obtengo el formato del Archivo
		int index = ruta.lastIndexOf('.');

		// Si el archivo no tiene extension que automaticamente lo guarde como RTF
		String ext = index > 0 ? ruta.substring(index + 1) : "rtf";
		
		// Creo las variables
		JTextPane textTexto = new JTextPane();

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

		} else {

			FileReader reader = new FileReader(ruta);
			textTexto.read(reader, ruta);
			reader.close();
		}

		Tab newTab = new Tab(textTexto, panel);
		newTab.setRuta(ruta);
		panel.addTab(new File(ruta).getName(), newTab);
		return true;

	}

	public static boolean AbrirTab(TabsManager panel, String ruta) throws IOException, BadLocationException, FileNotFoundException {
		
		// Si el archivo no tiene extension que automaticamente lo guarde como RTF
		int index = ruta.lastIndexOf('.');
		String ext = index > 0 ? ruta.substring(index + 1) : "rtf";

		// Creo las variables
		JTextPane textTexto = new JTextPane();

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

		} else {

			FileReader reader = new FileReader(ruta);
			textTexto.read(reader, ruta);
			reader.close();
		}

		Tab newTab = new Tab(textTexto, panel);
		newTab.setRuta(ruta);
		panel.addTab(new File(ruta).getName(), newTab);
		return true;

	}


}
