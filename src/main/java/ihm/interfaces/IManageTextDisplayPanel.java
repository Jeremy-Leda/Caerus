package ihm.interfaces;

import java.util.function.Consumer;

/**
 * 
 * Interface permettant de manipuler la fen�tre de gestion des textes
 * 
 * @author jerem
 *
 */
public interface IManageTextDisplayPanel extends IAccessPanel {

	/**
	 * Permet de fermer la fen�tre
	 */
	void close();
	
	/**
	 * Permet de d�activer les boutons
	 * @param enable Vrai si activer, faux sinon
	 */
	void setEnabledAllButton(Boolean enable);
	
	/**
	 * Permet d'ajouter une action sur l'ouverture de la fen�tre d'�dition
	 * @param consumer consumer
	 */
	void addConsumerOnOpenEditText(Consumer<Void> consumer);
	
	/**
	 * Permet d'ajouter une action sur la fermeture de la fen�tre d'�dition
	 * @param consumer consumer
	 */
	void addConsumerOnCloseEditText(Consumer<Void> consumer);
	
	/**
	 * Permet de rafraichir l'interface et de r�cup�rer de nouveau les informations cot� serveur
	 */
	void refresh();
	
}
