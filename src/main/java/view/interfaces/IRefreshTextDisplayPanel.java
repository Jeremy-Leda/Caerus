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
	 * @param direction Direction � appliquer
	 */
	void changePage(DirectionTypeEnum direction);
	
	/**
	 * Permet de d�finir le nb de textes par pages
	 * @param nbTextsByPage nb textes � afficher par pages
	 */
	void setNbTextByPage(Integer nbTextsByPage);
	
	/**
	 * Permet de d�terminer si la direction en param�tre est possible
	 * @param direction direction � tester
	 * @return Vrai si c'est possible, Faux sinon
	 */
	Boolean isEnabled(DirectionTypeEnum direction);
	
	/**
	 * Permet de connaitre la page courante
	 * @return la page courante
	 */
	Integer getCurrentPage();
	
	/**
	 * Permet de d�terminer le maximum de page
	 * @return le maximum de page
	 */
	Integer getMaxPage();
	
	/**
	 * Permet de se procurer le texte s�lectionn�
	 * @return le texte selectionn�
	 */
	DisplayText getDisplayTextSelected();
	
	/**
	 * Permet de rafraichir l'affichage
	 */
	void refresh();
}
