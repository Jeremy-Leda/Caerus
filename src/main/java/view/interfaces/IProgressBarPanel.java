package view.interfaces;

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
	void launchTreatment();

	/**
	 * Permet de lancer le traitement
	 * @param maximumValue valeur maximum
	 * @param updateProgressBarConsumer Le consumer pour la progression
	 */
	void launchTreatment(Integer maximumValue, Consumer<Consumer<Integer>> updateProgressBarConsumer);
	
}
