package view.interfaces;

import model.interfaces.IProgressModel;

import java.util.function.Consumer;

/**
 * 
 * Interface pour la gestion de la progressBarPanel
 * 
 * @author jerem
 *
 */
public interface IProgressBarPanel extends IAccessPanel{

	/**
	 * Permet de lancer le traitement
	 */
	void launchTreatment(IProgressModel model);

	/**
	 * Permet de stopper les traitements
	 */
	void stop();

}
