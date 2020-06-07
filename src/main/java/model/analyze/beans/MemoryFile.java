package model.analyze.beans;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import model.analyze.interfaces.IWriteText;

/**
 * 
 * Bean contenant le fichier en mémoire
 * 
 * @author Jeremy
 *
 */
public class MemoryFile {
	
	private final Path path;
	private final String nameFile;
	private final List<String> linesOrdered = new ArrayList<String>();
	private Iterator<String> iterableLine;
	private Integer currentLine;
	
	/**
	 * Constructeur
	 * @param nameFile nom du fichier
	 */
	public MemoryFile(Path path) {
		this.path = path;
		this.nameFile = path.getFileName().toString();
		this.currentLine = null;
		this.iterableLine = null;
	}
	
	/**
	 * Permet de se procurer la liste des lignes du fichier (ordre conservée)
	 * @return Les lignes du fichier (ordre conservée)
	 */
	public List<String> getLinesOrdered() {
		return this.linesOrdered;
	}
	
	/**
	 * Permet de se procurer le nom du fichier
	 * @return
	 */
	public String nameFile() {
		return this.nameFile;
	}
	
	/**
	 * Permet d'écrire le fichier en mémoire
	 * @param writer writer pour l'écriture
	 * @throws IOException erreur IO
	 */
	public void writeLines(IWriteText writer) throws IOException {
		for (String line : linesOrdered) {
			writer.writeLineWithBreakLineAfter(line);
		}
	}
	
	/**
	 * Permet de mettre à jour une ligne
	 * @param index index
	 * @param newLine la nouvelle ligne
	 */
	public void updateLine(Integer index, String newLine) {
		this.linesOrdered.set(index, newLine);
	}
	
	/**
	 * Permet de se procurer la ligne courante
	 * @return la ligen courante
	 */
	public Integer getCurrentLine() {
		return this.currentLine;
	}
	
	/**
	 * Permet de créer l'itérateur
	 */
	public void createIterator() {
		iterableLine = this.linesOrdered.iterator();
		currentLine = null;
	}

	/**
	 * Permet de savoir si il reste des lignes
	 * @return Vrais si c'est le cas
	 */
	public Boolean hasLine() {
		if (null == iterableLine) {
			return false;
		}
		return iterableLine.hasNext();
	}
	
	/**
	 * Permet de se procurer la ligne suivante
	 * @return la ligne suivante
	 */
	public String getNextLine() {
		if (null == iterableLine) {
			return StringUtils.EMPTY;
		}
		if (null == currentLine) {
			currentLine = 0;
		} else {
			currentLine++;
		}
		return iterableLine.next();
	}

	/**
	 * Permet de se procurer le chemin du fichier
	 * @return le chemin du fichier en mémoire
	 */
	public Path getPath() {
		return path;
	}
}
