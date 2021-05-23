package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import javax.imageio.ImageIO;

import model.analyze.lexicometric.beans.LexicometricAnalysis;
import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import org.apache.commons.io.IOUtils;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import model.analyze.beans.Configuration;
import view.beans.PictureTypeEnum;

import static java.awt.Image.*;

/**
 * Permet de fournir des m�thodes pour ce procurer des ressources simplement
 * @author jerem
 *
 */
public class RessourcesUtils {
	
	private static RessourcesUtils _instance;
	private static final String DEFAULT_CONFIGURATION = "ConfiguraciónBásica.json";
	private static final String DEFAULT_ANALYZE_CONFIGURATION = "LexicometricAnalyze.json";
	private static final String DEFAULT_LEMMATIZATION_BY_GRAMMATICAL_CATEGORY_CONFIGURATION = "LemmatizationByGrammaticalCategory.json";
	private static Logger logger = LoggerFactory.getLogger(RessourcesUtils.class);
	
	/**
	 * Permet de se procurer l'instance
	 * @return
	 */
	public static RessourcesUtils getInstance() {
		if (null == _instance) {
			_instance = new RessourcesUtils();
		}
		return _instance;
	}
	
	/**
	 * Permet de se procurer un fichier depuis les ressources
	 * @param fileName nom du fichier
	 * @return le fichier
	 */
    public URL getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
		URL url = classLoader.getResource(fileName);
        if (url == null) {
        	throw new ServerException().addInformationException(new InformationExceptionBuilder()
				.objectInError(fileName)
				.errorCode(ErrorCode.TECHNICAL_ERROR)
				.parameters(Set.of("resourceAsStream is null (file not found ?)"))
				.build());
        }
        return url;

    }

    public Image getAnimatedImage(PictureTypeEnum typeImage) {
		try {
			return Toolkit.getDefaultToolkit().createImage(IOUtils.toByteArray(getFileFromResources(typeImage.getFileName())));
			//return temp.getScaledInstance(100,100,SCALE_DEFAULT);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
    
    /**
     * Permet de se procurer une image
     * @param typeImage type de l'image
     * @return
     */
    public BufferedImage getImage(PictureTypeEnum typeImage) {
    	try {
			return ImageIO.read(getFileFromResources(typeImage.getFileName()));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
    }
    
	/**
	 * Permet de se procurer la configuration classique
	 * 
	 * @return la configuration classique
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public Configuration getBasicalConfiguration() throws JsonParseException, JsonMappingException, IOException {
		URL fileFromResources = getFileFromResources(DEFAULT_CONFIGURATION);
		try (InputStream is = fileFromResources.openStream()) {
			return JSonFactoryUtils.createConfigurationFromJsonFile(is);
		}
	}

	/**
	 * Permet de se procurer la configuration pour les analyses
	 *
	 * @return la configuration pour l'analyse
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public LexicometricAnalysis getAnalyzeConfiguration() throws JsonParseException, JsonMappingException, IOException {
		URL fileFromResources = getFileFromResources(DEFAULT_ANALYZE_CONFIGURATION);
		try (InputStream is = fileFromResources.openStream()) {
			return JSonFactoryUtils.createAnalyseConfigurationFromJsonFile(is);
		}
	}

	/**
	 * Permet de se procurer la configuration pour la lemmatization par catégorie grammatical
	 *
	 * @return la configuration pour la lemmatization par catégorie grammatical
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public LexicometricAnalysis getLemmatizationByGrammaticalCategoryConfiguration() throws JsonParseException, JsonMappingException, IOException {
		URL fileFromResources = getFileFromResources(DEFAULT_LEMMATIZATION_BY_GRAMMATICAL_CATEGORY_CONFIGURATION);
		try (InputStream is = fileFromResources.openStream()) {
			return JSonFactoryUtils.createAnalyseConfigurationFromJsonFile(is);
		}
	}


}
