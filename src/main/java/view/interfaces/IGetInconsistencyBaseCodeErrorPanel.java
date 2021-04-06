package view.interfaces;

import java.util.List;

import view.beans.BaseCodeError;

/**
 * 
 * Interface permettant de se procurer la liste des erreurs d'incohérences au niveau des bases codes
 * 
 * @author jerem
 *
 */
public interface IGetInconsistencyBaseCodeErrorPanel extends IAccessPanel {

	/**
	 * Permet de se procurer la liste ses erreurs d'incohérences au niveau des bases codes
	 * @return la liste des erreurs d'incohérences au niveau des bases codes
	 */
	List<BaseCodeError> getAllInconsistencyBaseCodeErrorList();
	
}
