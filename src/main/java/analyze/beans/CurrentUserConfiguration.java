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
	private Path configurationPath;
	private String defaultConfiguration;

	/**
	 * Permet de se procurer le chemin d'acc�s au configurations
	 * @return le d'acc�s au configurations
	 */
	public Path getConfigurationPath() {
		return configurationPath;
	}

	/**
	 * Permet de d�finir le chemi d'acc�s aux configurations
	 * @param configurationPath chemin d'acc�s au configurations
	 */
	public void setConfigurationPath(Path configurationPath) {
		this.configurationPath = configurationPath;
	}

	/**
	 * Permet de se procurer la configuration par d�faut
	 * @return la configuration par d�faut
	 */
	public String getDefaultConfiguration() {
		return defaultConfiguration;
	}

	/**
	 * Permet de d�finir le nom de la configuration par d�faut
	 * @param defaultConfiguration configuration par d�faut
	 */
	public void setDefaultConfiguration(String defaultConfiguration) {
		this.defaultConfiguration = defaultConfiguration;
	}

	/**
	 * Permet de se procurer le chemin de la librairie de texte
	 * @return le chemin de la librarie
	 */
	public Path getLibraryPath() {
		return libraryPath;
	}

	/**
	 * Permet de d�finir le chemin de la librairie de texte
	 * @param libraryPath Le chemin de la librairie
	 */
	public void setLibraryPath(Path libraryPath) {
		this.libraryPath = libraryPath;
	}
	
}
