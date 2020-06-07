package utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import model.analyze.beans.Configuration;

/**
 * 
 * Classe utilitaire pour g�rer les Json
 * 
 * @author jerem
 *
 */
public final class JSonFactoryUtils {

	/**
	 * Permet de cr�er un json
	 * @param objet objet � g�n�rer
	 * @param file fichier � cr�er
	 * @return  Vrai si le Json a �t� cr��
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public static <T> Boolean createJsonInFile(T objet, String file) throws JsonGenerationException, JsonMappingException, IOException {
		File fileObject = new File(file);
		return createJsonInFile(objet, fileObject);
	}
	
	/**
	 * Permet de cr�er un json
	 * @param objet objet � g�n�rer
	 * @param file fichier � cr�er
	 * @return  Vrai si le Json a �t� cr��
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public static <T> Boolean createJsonInFile(T objet, File file) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		// Code temporaire pour indenter le temps de cr�er les interfaces graphiques
		// Plus simple pour comprendre la structure
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		//
		mapper.writeValue(file, objet);		
		return file.exists();
	}
	
	/**
	 * Permet de se procurer la configuration � partir d'un fichier Json
	 * @param inputStream Inputstream
	 * @return la configuration
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static Configuration createConfigurationFromJsonFile(InputStream inputStream) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(inputStream, Configuration.class);
	}
	
	/**
	 * Permet de se procurer un objet � partir d'un fichier Json
	 * @param <T> Type d'objet
	 * @param inputStream Inputstream
	 * @param clazz Classe de l'objet
	 * @return l'objet
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T createObjectFromJsonFile(InputStream inputStream, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(inputStream, clazz);
	}
	
	
}
