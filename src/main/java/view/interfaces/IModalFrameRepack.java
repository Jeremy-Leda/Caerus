package view.interfaces;

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
	 * Repack l'interface
	 * @param changeLocation Définit si l'on doit changer l'emplacement
	 */
	void repack(Boolean changeLocation);

}
