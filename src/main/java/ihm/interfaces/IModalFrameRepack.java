package ihm.interfaces;

import java.awt.Dimension;

/**
 * 
 * Permet de proposer le repack de la fenêtre modal principal
 * 
 * @author jerem
 *
 */
public interface IModalFrameRepack {

	/**
	 * Repack l'interface
	 */
	void repack();
	
	/**
	 * Permet de définir la taille maximum
	 * @param size taille
	 */
	void setMaximumSize(Dimension size);
}
