package view.interfaces;

import javax.swing.JProgressBar;

/**
 * 
 * Interface pour l'utilisation du modéle de la progressBar
 * 
 * @author jerem
 *
 */
public interface IProgressBarModel {

	/**
	 * Permet de lancer le traitement sur la progressBar en paramétre
	 * @param progressBar progressBar à définir
	 */
	void launchTreatment(JProgressBar progressBar);
}
