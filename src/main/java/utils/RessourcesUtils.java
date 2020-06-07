package utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import model.analyze.beans.Configuration;
import view.beans.PictureTypeEnum;

/**
 * Permet de fournir des méthodes pour ce procurer des ressources simplement
 * @author jerem
 *
 */
public class RessourcesUtils {
	
	private static RessourcesUtils _instance;
	private static final String DEFAULT_CONFIGURATION = "ConfiguraciónBásica.json";
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
    public InputStream getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(fileName);
        if (resourceAsStream == null) {
            throw new IllegalArgumentException("file is not found!");
        }
        return resourceAsStream;

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
		return JSonFactoryUtils.createConfigurationFromJsonFile(getFileFromResources(DEFAULT_CONFIGURATION));
	}


}
