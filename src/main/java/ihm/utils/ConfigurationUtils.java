package ihm.utils;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.ImmutableMap;

import analyze.beans.Configuration;
import utils.JSonFactoryUtils;
import utils.RessourcesUtils;

/**
 * 
 * Classe de configuration permettant de gérer les bundles de ressources Ainsi
 * que les ressources complémentaires
 * 
 * TODO faire l'assembly.xml
 * 
 * @author jerem
 *
 */
public final class ConfigurationUtils {

	private ResourceBundle bundleLangage;
	private static final ConfigurationUtils configuration = new ConfigurationUtils();
	private final Map<String, String> mapLanguages;
	private static Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);

	/**
	 * Constructeur
	 */
	public ConfigurationUtils() {
		mapLanguages = ImmutableMap.of("Français", "fr-FR", "Español", "es-ES");
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
	 * Permet de se procurer le texte à afficher
	 * 
	 * @param key clé du fichier
	 * @return le message
	 */
	public String getDisplayMessage(String key) {
		try {
			return this.bundleLangage.getString(key);			
		} catch (MissingResourceException e) {
			logger.error(String.format("La clé %s n'a pas été trouvé pour afficher le libellé", key));
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public void loadBundleLangage(String language) {
		this.bundleLangage = ResourceBundle.getBundle("display", Locale.forLanguageTag(language));
	}

	/**
	 * Permet de se procurer l'instance unique
	 * 
	 * @return l'instance
	 */
	public static ConfigurationUtils getInstance() {
		return configuration;
	}

	public Map<String, String> getMapLanguages() {
		return mapLanguages;
	}

	/**
	 * Permet de se procurer la configuration classique
	 * 
	 * @return la configuration classique
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public Configuration getClassicalConfiguration() throws JsonParseException, JsonMappingException, IOException {
		return JSonFactoryUtils.createConfigurationFromJsonFile(RessourcesUtils.getInstance().getFileFromResources("configurationClassique.json"));
	}

}
