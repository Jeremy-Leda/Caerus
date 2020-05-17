package ihm.interfaces;

/**
 * 
 * Permet de proposer le repack de la fen�tre modal principal
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
	 * @param changeLocation D�finit si l'on doit changer l'emplacement
	 */
	void repack(Boolean changeLocation);

}
