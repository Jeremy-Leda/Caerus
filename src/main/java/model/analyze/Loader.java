package model.analyze;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import model.analyze.beans.MemoryFile;

/**
 * 
 * Classe en charge de la montée en memoire d'un fichier
 * 
 * @author Jeremy
 *
 */
public class Loader {
	
	/**
	 * Fichier
	 */
	private final Path path;

	/**
	 * Constructeur
	 * @param file fichier a traité
	 */
	public Loader(Path path) {
		this.path = path;
	}
	
	/**
	 * Permet de charger un fichier en mémoire
	 * @return le bean du fichier en mémoire
	 * @throws IOException 
	 */
	public MemoryFile getMemoryFile() throws IOException {
		MemoryFile memoryFile = new MemoryFile(path);
		Files.lines(path).forEach(l -> memoryFile.getLinesOrdered().add(l));
		return memoryFile;		
	}
	
}
