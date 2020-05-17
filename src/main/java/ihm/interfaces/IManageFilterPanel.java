package ihm.interfaces;

import ihm.beans.Filter;

/**
 * 
 * Interface de gestion des filtres
 * 
 * @author jerem
 *
 */
public interface IManageFilterPanel extends IGetFiltersPanel {

	/**
	 * Permet d'ajouter un filtre
	 * @param filterToAdd filtre � ajouter
	 */
	void addFilter(Filter filterToAdd);
	
	/**
	 * Permet de se procurer le filtre s�lectionn�
	 * @return le filtre s�letionn�
	 */
	Filter getFilterSelected();
	
	/**
	 * Permet de supprimer un filtre
	 * @param filterToRemove Filtre � supprimer
	 */
	void removeFilter(Filter filterToRemove);
	
}
