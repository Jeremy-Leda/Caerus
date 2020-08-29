package view.interfaces;

import javax.swing.JProgressBar;

/**
 * 
 * Interface pour l'utilisation du mod�le de la progressBar
 * 
 * @author jerem
 *
 */
public interface IProgressBarModel {

	/**
	 * Permet de lancer le traitement sur la progressBar en param�tre
	 * @param progressBar progressBar � d�finir
	 */
	void launchTreatment(JProgressBar progressBar);
}
