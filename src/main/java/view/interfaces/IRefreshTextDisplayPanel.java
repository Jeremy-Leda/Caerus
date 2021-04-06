package view.interfaces;

import view.beans.DirectionTypeEnum;
import view.beans.DisplayText;

/**
 * 
 * Interface pour rafraichir l'affichage des textes
 * 
 * @author jerem
 *
 */
public interface IRefreshTextDisplayPanel extends IAccessPanel {

	/**
	 * Permet de changer de page
	 * @param direction Direction à appliquer
	 */
	void changePage(DirectionTypeEnum direction);
	
	/**
	 * Permet de définir le nb de textes par pages
	 * @param nbTextsByPage nb textes à afficher par pages
	 */
	void setNbTextByPage(Integer nbTextsByPage);
	
	/**
	 * Permet de déterminer si la direction en paramétre est possible
	 * @param direction direction à tester
	 * @return Vrai si c'est possible, Faux sinon
	 */
	Boolean isEnabled(DirectionTypeEnum direction);
	
	/**
	 * Permet de connaitre la page courante
	 * @return la page courante
	 */
	Integer getCurrentPage();
	
	/**
	 * Permet de déterminer le maximum de page
	 * @return le maximum de page
	 */
	Integer getMaxPage();
	
	/**
	 * Permet de se procurer le texte sélectionné
	 * @return le texte selectionné
	 */
	DisplayText getDisplayTextSelected();
	
	/**
	 * Permet de rafraichir l'affichage
	 */
	void refresh();
}
