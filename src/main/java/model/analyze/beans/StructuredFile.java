package model.analyze.beans;

import utils.KeyGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * Bean contenant le fichier structuré
 * 
 * @author Jeremy
 *
 */
public class StructuredFile {

	private final List<StructuredText> listStructuredText = new ArrayList<StructuredText>();
	private final List<StructuringError> listStructuringError = new ArrayList<StructuringError>();
	private final MemoryFile memoryFile;
	private final String key;
	
	/**
	 * Constructeur
	 * @param memoryFile Fichier mémoire
	 */
	public StructuredFile(MemoryFile memoryFile) {
		this.memoryFile = memoryFile;
		this.key = KeyGenerator.generateKey(this.memoryFile.nameFile());
	}
	
	/**
	 * Permet de se procurer la liste des textes structurées
	 * @return la liste des textes structurées
	 */
	public List<StructuredText> getListStructuredText() {
		return this.listStructuredText;
	}
	
	/**
	 * Permet de se procurer le contenu pour un tag donné
	 * @param tag tag demandé
	 * @return la liste des contenu
	 */
	public List<String> getContent(String tag) {
		return this.listStructuredText.stream().map(st -> st.getContent(tag)).collect(Collectors.toList());
	}
	
	/**
	 * Permet de se procurer le nom du fichier
	 * @return le nom du fichier
	 */
	public String getFilename() {
		return this.memoryFile.nameFile();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Fichier => ").append(getFilename()).append("\n");
		sb.append("------------- DEBUT FICHIER ------------").append("\n");
		listStructuredText.forEach(l -> sb.append(l.toString()).append("\n"));
		sb.append("------------- FIN  FICHIER ------------");
		return sb.toString();
	}

	/**
	 * Permet de se procurer la liste des erreurs de structure
	 * @return la liste des erreurs
	 */
	public List<StructuringError> getListStructuringError() {
		return listStructuringError;
	}

	/**
	 * Permet de se procurer le numéro affecté au fichier
	 * @return le numéro du fichier
	 */
	public int getNumber() {
		return this.memoryFile.getNumber();
	}

	/**
	 * Permet de se procurer la clé
	 * @return la clé du fichier
	 */
	public String getKey() {
		return key;
	}
}
