package utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import model.analyze.beans.Configuration;
import model.analyze.beans.FilesOrder;
import model.analyze.lexicometric.beans.LexicometricAnalysis;

/**
 * 
 * Classe utilitaire pour gérer les Json
 * 
 * @author jerem
 *
 */
public final class JSonFactoryUtils {

	/**
	 * Permet de créer un json
	 * @param objet objet à générer
	 * @param file fichier à créer
	 * @return  Vrai si le Json a été créé
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public static <T> Boolean createJsonInFile(T objet, String file) throws JsonGenerationException, JsonMappingException, IOException {
		File fileObject = new File(file);
		return createJsonInFile(objet, fileObject);
	}
	
	/**
	 * Permet de créer un json
	 * @param objet objet à générer
	 * @param file fichier à créer
	 * @return  Vrai si le Json a été créé
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public static <T> Boolean createJsonInFile(T objet, File file) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		// Code temporaire pour indenter le temps de créer les interfaces graphiques
		// Plus simple pour comprendre la structure
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT,true);
		mapper.setSerializationInclusion(Include.NON_NULL);
		//
		mapper.writeValue(file, objet);		
		return file.exists();
	}
	
	/**
	 * Permet de se procurer la configuration à partir d'un fichier Json
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
	 * Permet de se procurer la configuration pour l'analyse à partir d'un fichier Json
	 * @param inputStream Inputstream
	 * @return la configuration pour l'analyse
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static LexicometricAnalysis createAnalyseConfigurationFromJsonFile(InputStream inputStream) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(inputStream, LexicometricAnalysis.class);
	}

	/**
	 * Permet de se procurer le fichier d'ordre des fichiers à partir d'un fichier Json
	 * @param inputStream Inputstream
	 * @return le fichier d'ordre des fichiers
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static FilesOrder createFilesOrderFromJsonFile(InputStream inputStream) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(inputStream, FilesOrder.class);
	}
	
	/**
	 * Permet de se procurer un objet à partir d'un fichier Json
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
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT,true);
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper.readValue(inputStream, clazz);
	}
	
	
}
