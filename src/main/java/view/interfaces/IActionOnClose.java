package view.interfaces;

import java.util.function.Consumer;

/**
 * 
 * Interface permettant d'effectuer des actions sur la fermeture d'une fen�tre
 * 
 * @author jerem
 *
 */
public interface IActionOnClose {

	/**
	 * Permet d'ajouter un consumer � lancer sur la fermeture de la fen�tre
	 * @param consumer consumer � lancer
	 */
	void addActionOnClose(Consumer<?> consumer);
	
	
	/**
	 * Permet de se procurer le consumer pour fermer automatiquement la fen�tre
	 * @return le consumer
	 */
	Consumer<Void> getConsumerClosingAutomatically();
	
	/**
	 * permet de fermer l'interface
	 */
	void closeFrame();
	
}
