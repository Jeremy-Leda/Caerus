package view.interfaces;

import javax.swing.JComponent;

/**
 * 
 * Permet de cr�er un accesPanel sur mesure
 * 
 * @author jerem
 *
 */
public interface IGenericAccessPanel extends IAccessPanel {

	/**
	 * 
	 * Permet d'ajouter un composant (les composants seront ajout�s les uns en dessous des autres)
	 * 
	 * @param component composant � ajouter
	 */
	void addComponent(JComponent component);
	
}
