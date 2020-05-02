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
 * Classe de configuration permettant de g�rer les bundles de ressources Ainsi
 * que les ressources compl�mentaires
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
