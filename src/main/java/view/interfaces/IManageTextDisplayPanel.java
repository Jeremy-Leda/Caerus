package view.interfaces;

import java.util.function.Consumer;

/**
 * 
 * Interface permettant de manipuler la fenêtre de gestion des textes
 * 
 * @author jerem
 *
 */
public interface IManageTextDisplayPanel extends IAccessPanel {

	/**
	 * Permet de fermer la fenêtre
	 */
	void close();
	
	/**
	 * Permet de déactiver les boutons
	 * @param enable Vrai si activer, faux sinon
	 */
	void setEnabledAllButton(Boolean enable);
	
	/**
	 * Permet d'ajouter une action sur l'ouverture de la fenêtre d'édition
	 * @param consumer consumer
	 */
	void addConsumerOnOpenEditText(Consumer<Void> consumer);
	
	/**
	 * Permet d'ajouter une action sur la fermeture de la fenêtre d'édition
	 * @param consumer consumer
	 */
	void addConsumerOnCloseEditText(Consumer<Void> consumer);
	
	/**
	 * Permet de rafraichir l'interface et de récupérer de nouveau les informations coté serveur
	 */
	void refresh();
	
}
