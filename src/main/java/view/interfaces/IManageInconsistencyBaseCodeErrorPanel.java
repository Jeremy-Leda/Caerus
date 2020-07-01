package view.interfaces;

import view.beans.BaseCodeError;

/**
 * 
 * Interface de gestion des erreurs d'incoh�rences pour les bases codes
 * 
 * @author jerem
 *
 */
public interface IManageInconsistencyBaseCodeErrorPanel extends IGetInconsistencyBaseCodeErrorPanel {

	/**
	 * Permet d'ajouter une erreur d'incoh�rence
	 * @param errorToAdd une erreur d'incoh�rence
	 */
	void addInconsistencyBaseCodeError(BaseCodeError errorToAdd);
	
	/**
	 * Permet de se procurer l'erreur d'incoh�rence s�lectionn�
	 * @return l'erreur d'incoh�rence s�lectionn�
	 */
	BaseCodeError getFilterSelected();
	
	/**
	 * Permet de supprimer une erreur d'incoh�rence
	 * @param errorToRemove erreur d'incoh�rence � supprimer
	 */
	void removeInconsistencyBaseCodeError(BaseCodeError errorToRemove);
	
}
