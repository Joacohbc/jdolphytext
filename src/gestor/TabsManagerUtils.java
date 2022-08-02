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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyleContext;
import javax.swing.text.rtf.RTFEditorKit;

import mensajes.Mensajes;
import tab.Tab;

public class TabsManagerUtils {

	// Verifica si una ruta existe
	public static boolean RutaExite(String ruta) {
		return new File(ruta).exists();
	}

	// Retorna un JFileChooser ya preparado para solo archivos en formato RTF y TXT
	private static JFileChooser getFileChooser() {
		JFileChooser chooser = new JFileChooser();

		// Para que habra en el escritorio
		chooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());

		// Que no pueda seleccinar mas de una archivo
		chooser.setMultiSelectionEnabled(false);

		// Que solo pueda seleccioanr archivos
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// Agrego que el filtro para el formato RTF
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos RTF", "rtf");
		chooser.addChoosableFileFilter(filter);

		// Agrego que el filtro para el formato TXT
		filter = new FileNameExtensionFilter("Archivos ", "txt");
		chooser.addChoosableFileFilter(filter);
		return chooser;
	}

	// Obtiene el formato del archivo de la ruta y lo guarda en ese formato. Y
	// actualiza la ruta de la Tab
	private static boolean guardarArchivo(Tab tab, String ruta) throws IOException, BadLocationException {

		// Obtengo el formato del Archivo
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

		} else if (ext.equals("txt")) {
			// Guardo el contenido de la Tab en la ruta indicada
			FileWriter writer = new FileWriter(ruta);
			tab.getJTextPane().write(writer);

		} else {

			if (!Mensajes.YesNoMessage("Formato selccionado no es soportado, ¿Desea guardar igual?",
					"Aviso de formato no soportado")) {
				return false;
			}

			// Guardo el contenido de la Tab en la ruta indicada
			FileWriter writer = new FileWriter(ruta);
			tab.getJTextPane().write(writer);
		}

		// Actualizo la ruta de la Tab
		tab.setRuta(ruta);
		return true;
	}

	/*
	 * Obtiene al Tab seleccinada del TabsManager y la guarda en la ruta qu esta
	 * misma tiene indicada.
	 * 
	 * Si no tiene ruta asignada, le pide al usuario una ruta mediante un
	 * FileChooser y lo guarda en esa ruta y actualiza la ruta de la Tab.
	 */
	public static boolean GuardarTabSeleccioanda(TabsManager panel, boolean cartel)
			throws IOException, BadLocationException, FileNotFoundException {

		// Obtengo al Tab seleccionada
		Tab tab = panel.getSelectedComponent();

		// Si esta vacia la ruta pido otra ruta al usuario
		if (tab.getRuta().isEmpty()) {

			JFileChooser chooser = getFileChooser();

			// Le asigno el titulo de "Guardar..."
			chooser.setName("Guardar...");

			// Si se cancela el guardado que retorne falso
			if (chooser.showSaveDialog(panel) == JFileChooser.CANCEL_OPTION) {
				return false;
			}

			// Le actualizo la ruta a la Tab
			tab.setRuta(chooser.getSelectedFile().getPath());
		}

		String ruta = tab.getRuta();
		// Si no pudo guardar el archivo que retorne false
		if (!guardarArchivo(tab, ruta)) {
			return false;
		}

		// Y le asigno el titulo del archivo a la titulo de la Tab
		panel.setTitleAt(panel.getSelectedIndex(), new File(ruta).getName());
		panel.getSelectedComponent().setRuta(ruta);

		// Si el la flag de cartel esta actiavada que muestre un mensaje de exito
		if (cartel) {
			Mensajes.InformationMessage(panel,"Su archivo se guardo en " + ruta, "Guardado...");
		}
		return true;
	}

	// Obtiene al Tab seleccionada del TabsManager y la guarda en la ruta que
	// ingrese
	// el usuario y actualiza la ruta de la Tab.
	public static boolean GuardarComoTabSeleccioanda(TabsManager panel, boolean cartel)
			throws IOException, BadLocationException, FileNotFoundException {

		Tab tab = panel.getSelectedComponent();

		// Si la ruta de la Tab no existe, pido que se guarde (no es adecuado para
		// guardar como)
		if (!RutaExite(tab.getRuta())) {
			return GuardarTabSeleccioanda(panel, cartel);
		}

		JFileChooser chooser = getFileChooser();

		// Le asigno el titulo de Guardar como...
		chooser.setName("Guardar como...");

		// Le pongo como archivo seleccionado la ruta de la Tab
		chooser.setSelectedFile(new File(tab.getRuta()));

		// Si cancela la operacion de guardado, retorno falso
		if (chooser.showSaveDialog(panel) == JFileChooser.CANCEL_OPTION) {
			return false;
		}

		tab.setRuta(chooser.getSelectedFile().getPath());

		// Obtengo el formato del Archivo
		String ruta = tab.getRuta();

		// Si no pudo guardar el archivo que retorne false
		if (!guardarArchivo(tab, ruta)) {
			return false;
		}

		// Y le asigno el titulo del archivo a la titulo de la Tab
		panel.setTitleAt(panel.getSelectedIndex(), new File(ruta).getName());
		panel.getSelectedComponent().setRuta(ruta);

		if (cartel) {
			Mensajes.InformationMessage(panel,"Su archivo se guardo en " + ruta, "Guardado como...");
		}

		return true;
	}

	// Obtiene el formato del archivo de la ruta y lo abre en ese formato. Y
	// actualiza la ruta de la Tab
	public static boolean abrirArchivo(Tab tab, String ruta) throws IOException, BadLocationException {

		// Si el archivo no tiene extension que automaticamente lo guarde como RTF
		int index = ruta.lastIndexOf('.');
		String ext = index > 0 ? ruta.substring(index + 1) : "rtf";

		// Abro en el formato elegido
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

			// Le asigno el contenido a la Tab
			tab.getJTextPane().setDocument(styledDocument);

			inputStream.close();
		} else if (ext.equals("txt")) {
			FileReader reader = new FileReader(ruta);
			tab.getJTextPane().read(reader, ruta);
			reader.close();

		} else {
			if (!Mensajes.YesNoMessage("Formato selccionado no es soportado, ¿Desea abrirlo igual?",
					"Aviso de formato no soportado")) {
				return false;
			}

			FileReader reader = new FileReader(ruta);
			tab.getJTextPane().read(reader, ruta);
			reader.close();
		}

		tab.setRuta(ruta);
		return true;
	}

	// Carga el contenido de la ruta indicada y lo carga en una Tab y la asgina al
	// TabManager
	public static boolean AbrirTab(TabsManager panel, String ruta)
			throws IOException, BadLocationException, FileNotFoundException {

		Tab newTab = new Tab(panel);

		// Si no se pudo abri con exito retono false;
		if (!abrirArchivo(newTab, ruta)) {
			return true;
		}
		// Le asigno titulo a la
		panel.addTab(new File(ruta).getName(), newTab);
		return true;

	}

	// Pide archivo al usuario y lo carga en una Tab y la asgina al Panel
	public static boolean AbrirTab(TabsManager panel) throws IOException, BadLocationException, FileNotFoundException {

		JFileChooser chooser = getFileChooser();

		// Le asigno el tutlo Abrira archivo...
		chooser.setName("Abrir archivo...");

		if (chooser.showOpenDialog(panel) == JFileChooser.CANCEL_OPTION) {
			return false;
		}

		String ruta = chooser.getSelectedFile().getPath();
		if (!RutaExite(ruta)) {
			Mensajes.ErrorMessage(panel, "El archivo seleccionada no exite: " + ruta, Mensajes.ErrorOpenTitle);
			return false;
		}

		return AbrirTab(panel, ruta);
	}

}
