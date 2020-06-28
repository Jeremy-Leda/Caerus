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
	 * @param structuredFieldFound champ trouvé
	 * @param line ligne
	 */
	public MissingBaseCode(StructuredField structuredFieldFound, Integer line) {
		super();
		this.structuredFieldFound = structuredFieldFound;
		this.line = line;
	}

	/**
	 * Permet de se procurer le champ trouvé
	 * @return le champ trouvé
	 */
	public StructuredField getStructuredFieldFound() {
		return structuredFieldFound;
	}

	/**
	 * Permet de se procurer la ligne trouvé
	 * @return la ligne trouvé
	 */
	public Integer getLine() {
		return line;
	}
	
	
	
}
