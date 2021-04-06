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
	private final String nameFile;
	
	/**
	 * Constructeur
	 * @param structuredFieldFound champ trouvé
	 * @param line ligne
	 * @param nameFile Nom du fichier
	 */
	public MissingBaseCode(StructuredField structuredFieldFound, Integer line, String nameFile) {
		super();
		this.structuredFieldFound = structuredFieldFound;
		this.line = line;
		this.nameFile = nameFile;
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

	/**
	 * Permet de se procurer le nom du fichier
	 * @return le nom du fichier
	 */
	public String getNameFile() {
		return nameFile;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		result = prime * result + ((nameFile == null) ? 0 : nameFile.hashCode());
		result = prime * result + ((structuredFieldFound == null) ? 0 : structuredFieldFound.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MissingBaseCode other = (MissingBaseCode) obj;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		if (nameFile == null) {
			if (other.nameFile != null)
				return false;
		} else if (!nameFile.equals(other.nameFile))
			return false;
		if (structuredFieldFound == null) {
			if (other.structuredFieldFound != null)
				return false;
		} else if (!structuredFieldFound.equals(other.structuredFieldFound))
			return false;
		return true;
	}
	
}
