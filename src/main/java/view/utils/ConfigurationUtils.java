package view.utils;

import java.util.*;

import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * Classe de configuration permettant de gérer les bundles de ressources Ainsi
 * que les ressources complémentaires
 * 
 * 
 * @author jerem
 *
 */
public final class ConfigurationUtils {

	private ResourceBundle bundleLangage;
	private static final ConfigurationUtils CONFIGURATION_INSTANCE = new ConfigurationUtils();
	private final Map<String, String> mapLanguages;
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);

	/**
	 * Constructeur
	 */
	public ConfigurationUtils() {
		mapLanguages = ImmutableMap.of("Français", "fr-FR", "Español", "es-ES");
		loadBundleLangage(Locale.getDefault().getLanguage());
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
			InformationException informationException = new InformationExceptionBuilder()
					.errorCode(ErrorCode.TECHNICAL_ERROR)
					.objectInError(key)
					.parameters(Set.of("La clé n'a pas été trouvé pour afficher le libellé dans la langue sélectionné"))
					.build();
			logger.error(informationException.toString());
			throw new ServerException().addInformationException(informationException);
		}
	}

	/**
	 * Permet de charger le bundle pour un langage précis
	 * @param language langue à charger
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
