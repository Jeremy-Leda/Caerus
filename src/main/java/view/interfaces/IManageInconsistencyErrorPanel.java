package view.interfaces;

import view.beans.InconsistencyError;

/**
 * 
 * Interface de gestion des erreurs d'incohérences
 * 
 * @author jerem
 *
 */
public interface IManageInconsistencyErrorPanel extends IGetInconsistencyErrorPanel {

	/**
	 * Permet d'ajouter une erreur d'incohérence
	 * @param errorToAdd une erreur d'incohérence
	 */
	void addInconsistencyError(InconsistencyError errorToAdd);
	
	/**
	 * Permet de se procurer l'erreur d'incohérence sélectionné
	 * @return l'erreur d'incohérence sélectionné
	 */
	InconsistencyError getFilterSelected();
	
	/**
	 * Permet de supprimer une erreur d'incohérence
	 * @param errorToRemove erreur d'incohérence à supprimer
	 */
	void removeInconsistencyError(InconsistencyError errorToRemove);
	
}
