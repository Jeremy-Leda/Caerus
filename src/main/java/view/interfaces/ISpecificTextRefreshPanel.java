package view.interfaces;

/**
 * 
 * Interface permettant de fournir une m�thode pour le rechargement du panel
 * 
 * @author jerem
 *
 */
public interface ISpecificTextRefreshPanel extends IAccessPanel {

	/**
	 * Permet de rafraichir le panel
	 */
	void refresh();
	
	/**
	 * Permet de rafraichir les boutons
	 */
	void refreshAfterSelectedIndex();
	
}
