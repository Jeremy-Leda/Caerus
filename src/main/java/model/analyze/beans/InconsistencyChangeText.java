package model.analyze.beans;

/**
 * 
 * Bean permettant de connaitre l'origine d'une erreur d'inconsistence de données structurelles.
 * Cela va amener a décaler les informations contenu dans les textes
 * Il est donc nécessaire d'informer l'utilisateur du riques
 * 
 * @author jerem
 *
 */
public class InconsistencyChangeText {

	private final StructuredField oldStructuredFieldNewText;
	private final StructuredField newStructuredFieldNewText;
	private final Integer oldLine;
	private final Integer newLine;
	
	/**
	 * Constructeur
	 * @param oldStructuredFieldNewText ancienne balise de changement de texte
	 * @param newStructuredFieldNewText nouvelle balise de changement de texte
	 * @param oldLine numéro de la ligne pour l'ancienne balise
	 * @param newLine numéro de la ligne pour la nouvelle balise
	 */
	public InconsistencyChangeText(StructuredField oldStructuredFieldNewText, StructuredField newStructuredFieldNewText,
			Integer oldLine, Integer newLine) {
		this.oldStructuredFieldNewText = oldStructuredFieldNewText;
		this.newStructuredFieldNewText = newStructuredFieldNewText;
		this.oldLine = oldLine;
		this.newLine = newLine;
	}

	/**
	 * Permet de se procurer l'ancien champ structuré
	 * @return l'ancien champ structuré
	 */
	public StructuredField getOldStructuredFieldNewText() {
		return oldStructuredFieldNewText;
	}

	/**
	 * Permet de se procurer le nouveau champ structuré
	 * @return le nouveau champ structuré
	 */
	public StructuredField getNewStructuredFieldNewText() {
		return newStructuredFieldNewText;
	}

	/**
	 * Permet de se procurer le numéro de la ligne de l'ancienne balise
	 * @return le numéro de la ligne de l'ancienne balise
	 */
	public Integer getOldLine() {
		return oldLine;
	}

	/**
	 * Permet de se procurer le numéro de la ligne de la nouvelle balise
	 * @return le numéro de la ligne de la nouvelle balise
	 */
	public Integer getNewLine() {
		return newLine;
	}
	
	
	
}
