package view.utils;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * Classe de configuration permettant de g�rer les bundles de ressources Ainsi
 * que les ressources compl�mentaires
 * 
 * 
 * @author jerem
 *
 */
public final class ConfigurationUtils {

	private ResourceBundle bundleLangage;
	private static final ConfigurationUtils CONFIGURATION_INSTANCE = new ConfigurationUtils();
	private final Map<String, String> mapLanguages;
	private static Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);

	/**
	 * Constructeur
	 */
	public ConfigurationUtils() {
		mapLanguages = ImmutableMap.of("Fran�ais", "fr-FR", "Espa�ol", "es-ES");
		loadBundleLangage(Locale.getDefault().getLanguage());
		// loadBundleLangage(MAP_LANGUAGES.get("Espanol"));
	}

	/**
	 * Permet de se procurer le bundle du langage
	 * 
	 * @return le bundle langage
	 */
	public ResourceBundle getBundleLangage() {
		return bundleLangage;
	}

	/**
	 * Permet de se procurer le texte � afficher
	 * 
	 * @param key cl� du fichier
	 * @return le message
	 */
	public String getDisplayMessage(String key) {
		try {
			return this.bundleLangage.getString(key);			
		} catch (MissingResourceException e) {
			logger.error(String.format("La cl� %s n'a pas �t� trouv� pour afficher le libell�", key));
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Permet de charger le bundle pour un langage pr�cis
	 * @param language langue � charger
	 */
	public void loadBundleLangage(String language) {
		this.bundleLangage = ResourceBundle.getBundle("display", Locale.forLanguageTag(language));
	}

	/**
	 * Permet de se procurer l'instance unique
	 * 
	 * @return l'instance
	 */
	public static ConfigurationUtils getInstance() {
		return CONFIGURATION_INSTANCE;
	}

	/**
	 * Permet de se procurer la map des languages
	 * @return la map
	 */
	public Map<String, String> getMapLanguages() {
		return mapLanguages;
	}
}
