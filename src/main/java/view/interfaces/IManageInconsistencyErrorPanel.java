package view.interfaces;

import view.beans.InconsistencyError;

/**
 * 
 * Interface de gestion des erreurs d'incoh�rences
 * 
 * @author jerem
 *
 */
public interface IManageInconsistencyErrorPanel extends IGetInconsistencyErrorPanel {

	/**
	 * Permet d'ajouter une erreur d'incoh�rence
	 * @param errorToAdd une erreur d'incoh�rence
	 */
	void addInconsistencyError(InconsistencyError errorToAdd);
	
	/**
	 * Permet de se procurer l'erreur d'incoh�rence s�lectionn�
	 * @return l'erreur d'incoh�rence s�lectionn�
	 */
	InconsistencyError getFilterSelected();
	
	/**
	 * Permet de supprimer une erreur d'incoh�rence
	 * @param errorToRemove erreur d'incoh�rence � supprimer
	 */
	void removeInconsistencyError(InconsistencyError errorToRemove);
	
}
