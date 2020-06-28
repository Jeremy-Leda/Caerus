package view.interfaces;

import view.beans.BaseCodeError;

/**
 * 
 * Interface de gestion des erreurs d'incohérences pour les bases codes
 * 
 * @author jerem
 *
 */
public interface IManageInconsistencyBaseCodeErrorPanel extends IGetInconsistencyBaseCodeErrorPanel {

	/**
	 * Permet d'ajouter une erreur d'incohérence
	 * @param errorToAdd une erreur d'incohérence
	 */
	void addInconsistencyBaseCodeError(BaseCodeError errorToAdd);
	
	/**
	 * Permet de se procurer l'erreur d'incohérence sélectionné
	 * @return l'erreur d'incohérence sélectionné
	 */
	BaseCodeError getFilterSelected();
	
	/**
	 * Permet de supprimer une erreur d'incohérence
	 * @param errorToRemove erreur d'incohérence à supprimer
	 */
	void removeInconsistencyBaseCodeError(BaseCodeError errorToRemove);
	
}
