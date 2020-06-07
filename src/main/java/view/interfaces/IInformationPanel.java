package view.interfaces;

/**
 * Permet de mettre à jour les informations du panel
 * @author jerem
 *
 */
public interface IInformationPanel extends IAccessPanel {

	/**
	 * Permet de mettre à jour l'informations
	 * @param informations informations à afficher
	 */
	public void refreshInformations(String informations);
	
}
