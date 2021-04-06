package view.interfaces;

import java.util.function.Consumer;

/**
 * 
 * Interface permettant d'effectuer des actions sur la fermeture d'une fenêtre
 * 
 * @author jerem
 *
 */
public interface IActionOnClose {

	/**
	 * Permet d'ajouter un consumer à lancer sur la fermeture de la fenêtre
	 * @param consumer consumer à lancer
	 */
	void addActionOnClose(Consumer<?> consumer);
	
	
	/**
	 * Permet de se procurer le consumer pour fermer automatiquement la fenêtre
	 * @return le consumer
	 */
	Consumer<Void> getConsumerClosingAutomatically();
	
	/**
	 * permet de fermer l'interface
	 */
	void closeFrame();
	
}
