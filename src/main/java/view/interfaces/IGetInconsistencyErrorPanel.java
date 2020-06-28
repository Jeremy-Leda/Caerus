package view.interfaces;

import java.util.List;

import view.beans.InconsistencyError;

/**
 * 
 * Interface permettant de se procurer la liste des erreurs d'incoh�rences
 * 
 * @author jerem
 *
 */
public interface IGetInconsistencyErrorPanel extends IAccessPanel {

	/**
	 * Permet de se procurer la liste ses erreurs d'incoh�rences
	 * @return la liste des erreurs d'incoh�rences
	 */
	List<InconsistencyError> getAllInconsistencyErrorList();
	
}
