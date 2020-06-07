package view.interfaces;

import java.util.List;

import view.beans.Filter;

/**
 * 
 * Interface permettant de se procurer la liste des filtres
 * 
 * @author jerem
 *
 */
public interface IGetFiltersPanel extends IAccessPanel {

	/**
	 * Permet de se procurer la liste de tous les filtres actifs
	 * @return la liste des filtres actifs
	 */
	List<Filter> getAllFiltersList();
	
}
