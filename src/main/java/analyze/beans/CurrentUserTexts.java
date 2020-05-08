package analyze.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import analyze.beans.specific.ConfigurationStructuredText;

/**
 * 
 * Bean permettant de se procurer les textes en cours et d'interagir avec
 * 
 * @author jerem
 *
 */
public class CurrentUserTexts {

	private final List<StructuredFile> structuredFileList;
	private final List<ConfigurationStructuredText> configurationStructuredTextList;
	private final List<MemoryFile> memoryFileList;
	private final List<UserStructuredText> userStructuredTextList;
	
	/**
	 * Constructeur
	 */
	public CurrentUserTexts() {
		this.structuredFileList = new ArrayList<>();
		this.configurationStructuredTextList = new ArrayList<>();
		this.memoryFileList = new ArrayList<>();
		this.userStructuredTextList = new ArrayList<>();
	}
	
	/**
	 * Permet d'ajouter un structured file
	 * @param structuredFile structured file à ajouter
	 */
	public void addStructuredFile(StructuredFile structuredFile) {
		this.structuredFileList.add(structuredFile);
	}
	
	/**
	 * Permet de se procurer la liste des structured file
	 * @return la liste des structured
	 */
	public List<StructuredFile> getStructuredFileList() {
		return Collections.unmodifiableList(this.structuredFileList);
	}
	
	/**
	 * Permet de vider la liste des fichiers structurés
	 */
	public void clearStructuredFileList() {
		this.structuredFileList.clear();
	}
	
	/**
	 * Permet d'ajouter une configuration structured text (spécifique text)
	 * @param configurationStructuredText configuration structured text à ajouter
	 */
	public void addConfigurationStructuredText(ConfigurationStructuredText configurationStructuredText) {
		this.configurationStructuredTextList.add(configurationStructuredText);
	}
	
	/**
	 * Permet de se procurer la liste des configuration structured text (spécifique text)
	 * @return la liste des configuration structured text (spécifique text)
	 */
	public List<ConfigurationStructuredText> getConfigurationStructuredTextList() {
		return Collections.unmodifiableList(this.configurationStructuredTextList);
	}
	
	/**
	 * Permet de vider la liste des fichiers structurés spécifique
	 */
	public void clearConfigurationStructuredTextList() {
		this.configurationStructuredTextList.clear();
	}
	
	/**
	 * Permet d'ajouter un memory file
	 * @param memoryFile structured file à ajouter
	 */
	public void addMemoryFile(MemoryFile memoryFile) {
		this.memoryFileList.add(memoryFile);
	}
	
	/**
	 * Permet de se procurer la liste des memory file
	 * @return la liste des memory file
	 */
	public List<MemoryFile> getMemoryFileList() {
		return Collections.unmodifiableList(this.memoryFileList);
	}
	
	/**
	 * Permet de vider la liste des fichiers en mémoire
	 */
	public void clearMemoryFileList() {
		this.memoryFileList.clear();
	}
	
	/**
	 * Permet d'ajouter un user structured text
	 * @param userStructuredText user structured text à ajouter
	 */
	public void addUserStructuredText(UserStructuredText userStructuredText) {
		this.userStructuredTextList.add(userStructuredText);
	}
	
	/**
	 * Permet de se procurer la liste des user structured text
	 * @return la liste des user structured text
	 */
	public List<UserStructuredText> getUserStructuredTextList() {
		return Collections.unmodifiableList(this.userStructuredTextList);
	}
	
	/**
	 * Permet de vider la liste des textes structurés utilisateurs
	 */
	public void clearUserStructuredTextList() {
		this.userStructuredTextList.clear();
	}
	
}
