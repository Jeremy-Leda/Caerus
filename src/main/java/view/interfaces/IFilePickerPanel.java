package view.interfaces;

import java.util.function.Consumer;

/**
 * 
 * Interface pour se procurer les informations du file picker
 * 
 * @author jerem
 *
 */
public interface IFilePickerPanel extends IAccessPanel {

	/**
	 * Permet de se procurer le chemin du fichier
	 * @return le chemin du fichier
	 */
	String getFile();
	
	/**
	 * Permet d'ajouter un consommateur dans le cas ou le fichier choisis est OK
	 * @param consumer consommateur
	 */
	void addConsumerOnChooseFileOk(Consumer<?> consumer);
	
}
