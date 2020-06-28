package model.analyze.beans;

/**
 * 
 * Bean permettant d'identifier une ligne ou il manquerait potentiellement le base code
 * 
 * @author jerem
 *
 */
public class MissingBaseCode {

	private final StructuredField structuredFieldFound;
	private final Integer line;
	
	/**
	 * Constructeur
	 * @param structuredFieldFound champ trouv�
	 * @param line ligne
	 */
	public MissingBaseCode(StructuredField structuredFieldFound, Integer line) {
		super();
		this.structuredFieldFound = structuredFieldFound;
		this.line = line;
	}

	/**
	 * Permet de se procurer le champ trouv�
	 * @return le champ trouv�
	 */
	public StructuredField getStructuredFieldFound() {
		return structuredFieldFound;
	}

	/**
	 * Permet de se procurer la ligne trouv�
	 * @return la ligne trouv�
	 */
	public Integer getLine() {
		return line;
	}
	
	
	
}
