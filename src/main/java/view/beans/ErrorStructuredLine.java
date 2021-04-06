package view.beans;

/**
 * 
 * Bean pour l'affichage des informations sur les lignes mal strcuturé
 * 
 * @author jerem
 *
 */
public class ErrorStructuredLine {

	private final String path;
	private final String line;
	private final Integer numLine;
	
	public ErrorStructuredLine(String path, String line, Integer numLine) {
		this.path = path;
		this.line = line;
		this.numLine = numLine;
	}

	public String getLine() {
		return line;
	}

	public Integer getNumLine() {
		return numLine;
	}

	public String getPath() {
		return path;
	}
	
}
