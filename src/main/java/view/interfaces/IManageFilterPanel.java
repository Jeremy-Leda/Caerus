package view.interfaces;

import view.beans.Filter;

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
	 * @param filterToAdd filtre à ajouter
	 */
	void addFilter(Filter filterToAdd);
	
	/**
	 * Permet de se procurer le filtre sélectionné
	 * @return le filtre séletionné
	 */
	Filter getFilterSelected();
	
	/**
	 * Permet de supprimer un filtre
	 * @param filterToRemove Filtre à supprimer
	 */
	void removeFilter(Filter filterToRemove);
	
}
