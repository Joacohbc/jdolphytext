package gestor;
import java.util.Stack;
import javax.swing.JTabbedPane;

import tab.Tab;

public class TabsManager extends JTabbedPane  {
		
	private final Stack<Tab> tabsBorradas = new Stack<Tab>();
	private final Stack<String> tabsBorradasNombres = new Stack<String>();
	
	public TabsManager(int place) {
		super(place);
	}
	
	@Override
	public Tab getSelectedComponent() {
		return (Tab) super.getSelectedComponent();
	}
	
	public void abrirNuevaTab() {
		Tab newTab = new Tab(this);
		this.addTab("Nueva ventana", newTab);
		newTab.darFocus();// <-- Para que simpre tenga el focus, y simpre pueda usar Control+W y Control+N
	}

	public void cerrarTabSeleccionada() {
		tabsBorradas.push(this.getSelectedComponent());
		tabsBorradasNombres.push(this.getTitleAt(this.getSelectedIndex()));
		this.remove(this.getSelectedComponent());
	}
	
	public boolean abrirTabBorrada() {
		
		if (tabsBorradas.isEmpty() && tabsBorradasNombres.isEmpty()) {
			return false;
		} 
		
		this.add(tabsBorradas.pop(), tabsBorradasNombres.pop());		
		return true;
	}
	
	public void vaciarTabsBorradas() {
		tabsBorradas.clear();
		tabsBorradasNombres.clear();
	}
		
	//Verifica si hay mas de una Tab abierta (retorna true), si hay una o ninguna retorna false
	public boolean checkearTabs() {
		if (this.getTabCount() > 1) {
			return true;
		}
		return false;
	}

}	
