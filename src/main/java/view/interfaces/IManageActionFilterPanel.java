package view.interfaces;

import java.util.Map;

/**
 * 
 * Interface pour la gestion des actions sur les filtres
 * 
 * @author jerem
 *
 */
public interface IManageActionFilterPanel extends IGetFiltersPanel {

	/**
	 * Permet d'initialiser la liste des champs sur lesquels on peut filtrer
	 * @param mapKeyFieldValueLabel map des clés valeurs
	 */
	void initMapOfFields(Map<String,String> mapKeyFieldValueLabel);
	
	
}
