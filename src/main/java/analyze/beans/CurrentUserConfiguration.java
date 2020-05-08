package analyze.beans;

import java.nio.file.Path;

/**
 * 
 * Bean permettant d'enregistrer et charger le profil utilisateur
 * 
 * @author jerem
 *
 */
public class CurrentUserConfiguration {

	private Path libraryPath;

	/**
	 * Permet de se procurer le chemin de la librairie de texte
	 * @return le chemin de la librarie
	 */
	public Path getLibraryPath() {
		return libraryPath;
	}

	/**
	 * Permet de définir le chemin de la librairie de texte
	 * @param libraryPath Le chemin de la librairie
	 */
	public void setLibraryPath(Path libraryPath) {
		this.libraryPath = libraryPath;
	}
	
}
