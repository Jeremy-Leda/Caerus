package view.interfaces;

/**
 * Permet de mettre � jour les informations du panel
 * @author jerem
 *
 */
public interface IInformationPanel extends IAccessPanel {

	/**
	 * Permet de mettre � jour l'informations
	 * @param informations informations � afficher
	 */
	public void refreshInformations(String informations);
	
}
