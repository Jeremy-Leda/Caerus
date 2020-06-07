package view.interfaces;

/**
 * 
 * Interface pour le panel qui affiche l'information sur le fichier enc ours
 * 
 * @author jerem
 *
 */
public interface IFilePanel extends IAccessPanel {

	/**
	 * Permet de rafraichir l'IHM
	 * @param titlePanel Titre du panel
	 * @param labelFile label pour le fichier
	 * @param valueFile Valeur pour le fichier
	 */
	void refresh(String titlePanel, String labelFile, String valueFile);
	
}
