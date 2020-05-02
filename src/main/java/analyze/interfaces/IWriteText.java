package analyze.interfaces;

import java.io.Closeable;
import java.io.IOException;

/**
 * 
 * Interface permettant de fournir les m�thodes pour �crire un text
 * 
 * @author jerem
 *
 */
public interface IWriteText extends Closeable {

	/**
	 * Permet d'�crire une ligne
	 * @param line ligne
	 */
	void writeLine(String line) throws IOException;
	
	/**
	 * Permet d'�crire une ligne et d'ajouter un saut de ligne
	 * @param line ligne
	 */
	void writeLineWithBreakLineAfter(String line) throws IOException;
	
	/**
	 * Permet d'ajouter un saut de ligne
	 */
	void addBreakLine() throws IOException;
	
}
