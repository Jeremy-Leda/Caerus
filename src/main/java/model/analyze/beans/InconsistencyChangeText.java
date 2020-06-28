package model.analyze.beans;

/**
 * 
 * Bean permettant de connaitre l'origine d'une erreur d'inconsistence de donn�es structurelles.
 * Cela va amener a d�caler les informations contenu dans les textes
 * Il est donc n�cessaire d'informer l'utilisateur du riques
 * 
 * @author jerem
 *
 */
public class InconsistencyChangeText {

	private final StructuredField oldStructuredFieldNewText;
	private final StructuredField newStructuredFieldNewText;
	private final Integer oldLine;
	private final Integer newLine;
	private final String nameFile;
	
	/**
	 * Constructeur
	 * @param oldStructuredFieldNewText ancienne balise de changement de texte
	 * @param newStructuredFieldNewText nouvelle balise de changement de texte
	 * @param oldLine num�ro de la ligne pour l'ancienne balise
	 * @param newLine num�ro de la ligne pour la nouvelle balise
	 * @param nameFile Nom du fichier
	 */
	public InconsistencyChangeText(StructuredField oldStructuredFieldNewText, StructuredField newStructuredFieldNewText,
			Integer oldLine, Integer newLine, String nameFile) {
		this.oldStructuredFieldNewText = oldStructuredFieldNewText;
		this.newStructuredFieldNewText = newStructuredFieldNewText;
		this.oldLine = oldLine;
		this.newLine = newLine;
		this.nameFile = nameFile;
	}

	/**
	 * Permet de se procurer l'ancien champ structur�
	 * @return l'ancien champ structur�
	 */
	public StructuredField getOldStructuredFieldNewText() {
		return oldStructuredFieldNewText;
	}

	/**
	 * Permet de se procurer le nouveau champ structur�
	 * @return le nouveau champ structur�
	 */
	public StructuredField getNewStructuredFieldNewText() {
		return newStructuredFieldNewText;
	}

	/**
	 * Permet de se procurer le num�ro de la ligne de l'ancienne balise
	 * @return le num�ro de la ligne de l'ancienne balise
	 */
	public Integer getOldLine() {
		return oldLine;
	}

	/**
	 * Permet de se procurer le num�ro de la ligne de la nouvelle balise
	 * @return le num�ro de la ligne de la nouvelle balise
	 */
	public Integer getNewLine() {
		return newLine;
	}
	
	/**
	 * Permet de se procurer le nom du fichier
	 * @return le nom du fichier
	 */
	public String getNameFile() {
		return nameFile;
	}
	
}
