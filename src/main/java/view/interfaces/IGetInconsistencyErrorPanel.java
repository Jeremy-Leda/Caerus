package view.interfaces;

import java.util.List;

import view.beans.InconsistencyError;

/**
 * 
 * Interface permettant de se procurer la liste des erreurs d'incohérences
 * 
 * @author jerem
 *
 */
public interface IGetInconsistencyErrorPanel extends IAccessPanel {

	/**
	 * Permet de se procurer la liste ses erreurs d'incohérences
	 * @return la liste des erreurs d'incohérences
	 */
	List<InconsistencyError> getAllInconsistencyErrorList();
	
}
