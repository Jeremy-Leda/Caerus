package view.interfaces;

import javax.swing.*;

/**
 * 
 * Interface pour l'utilisation du modéle de la progressBar
 * 
 * @author jerem
 *
 */
public interface IProgressBarModel {

	/**
	 * Permet de lancer le traitement sur la progressBar en paramètre
	 * @param progressBar progressBar à définir
	 * @param labelProgress Le label avec l'animation pour la progression
	 */
	void launchTreatment(JProgressBar progressBar, JLabel labelProgress);
}
