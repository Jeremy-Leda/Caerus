package model.analyze.beans;

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
	private String defaultLexicometricAnalysisConfiguration;

	/**
	 * Permet de se procurer le chemin d'accés au configurations
	 * @return le d'accés au configurations
	 */
	public Path getConfigurationPath() {
		return configurationPath;
	}

	/**
	 * Permet de définir le chemi d'accés aux configurations
	 * @param configurationPath chemin d'accés au configurations
	 */
	public void setConfigurationPath(Path configurationPath) {
		this.configurationPath = configurationPath;
	}

	/**
	 * Permet de se procurer la configuration par défaut
	 * @return la configuration par défaut
	 */
	public String getDefaultConfiguration() {
		return defaultConfiguration;
	}

	/**
	 * Permet de définir le nom de la configuration par défaut
	 * @param defaultConfiguration configuration par défaut
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
	 * Permet de définir le chemin de la librairie de texte
	 * @param libraryPath Le chemin de la librairie
	 */
	public void setLibraryPath(Path libraryPath) {
		this.libraryPath = libraryPath;
	}

	/**
	 * Permet de se procurer le nom du profile choisi par l'utilisateur pour les analyses lexicométrique
	 * @return Le nom du profile choisis par l'utilisateur pour les analyses lexicométrique
	 */
	public String getDefaultLexicometricAnalysisConfiguration() {
		return defaultLexicometricAnalysisConfiguration;
	}

	/**
	 * Permet de définir le nom du profile par défaut choisi par l'utilisateur pour les analyses lexicométrique
	 * @param defaultLexicometricAnalysisConfiguration le nom du profile par défaut choisi par l'utilisateur pour les analyses lexicométrique
	 */
	public void setDefaultLexicometricAnalysisConfiguration(String defaultLexicometricAnalysisConfiguration) {
		this.defaultLexicometricAnalysisConfiguration = defaultLexicometricAnalysisConfiguration;
	}
}
