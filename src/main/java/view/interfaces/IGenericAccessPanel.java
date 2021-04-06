package view.interfaces;

import javax.swing.JComponent;

/**
 * 
 * Permet de créer un accesPanel sur mesure
 * 
 * @author jerem
 *
 */
public interface IGenericAccessPanel extends IAccessPanel {

	/**
	 * 
	 * Permet d'ajouter un composant (les composants seront ajoutés les uns en dessous des autres)
	 * 
	 * @param component composant à ajouter
	 */
	void addComponent(JComponent component);
	
	/**
	 * Permet de mettre à jour le titre du panel
	 * @param title titre du panel
	 */
	void refreshTitle(String title);
	
}
