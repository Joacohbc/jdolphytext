package mensajes;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Mensajes {

    public static final String ErrorOpenTitle = "Error al abrir";
    public static final String ErrorSaveTitle = "Error al guardar";
    public static final String ErrorPrintTitle = "Error al imprimir";
    public static final String WarningTitle = "Advertencia";

    // Mensajes de Error

    // Muestra un mensaje de error al usuario
    public static void ErrorMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // Muestra un mensaje de error al usuario
    public static void ErrorMessage(String message, String title) {
        ErrorMessage(null, message, title);
    }

    // Mensajes de Informacion

    // Muestra un mensaje informativo al usuario
    public static void InformationMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Muestra un mensaje informativo al usuario
    public static void InformationMessage(String message, String title) {
        InformationMessage(null, message, title);
    }

    // Mensajes de advertencia
    
    // Muestra un mensaje de advertencia al usuario
    public static void WarningMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    // Muestra un mensaje de advertencia al usuario
    public static void WarningMessage(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    // Mensajes de confirmacion

    // Muestra un mensaje de confirmacion al usuario
    public static boolean YesNoMessage(Component parent, String message, String title) {
        int op = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return op == JOptionPane.YES_OPTION ? true : false;
    }

    // Muestra un mensaje de confirmacion al usuario
    public static boolean YesNoMessage(String message, String title) {
        int op = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return op == JOptionPane.YES_OPTION ? true : false;
    }
}
