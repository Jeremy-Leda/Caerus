package excel.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Bean permettant de configurer la generation des fichiers excel
 * 
 * @author jerem
 *
 */
public class ExcelGenerateConfigurationCmd {
	private Boolean isSpecificGeneration;
	private Boolean withHeader;
	private Boolean haveToGenerateReferenceText;
	private final List<String> fieldToGenerateList;
	private String fileName;
	private final Map<String, String> mapLabelSpecificFileName;
	private String labelSpecificChoose;
	private final Set<String> uniqueKeyList;

	public ExcelGenerateConfigurationCmd() {
		this.mapLabelSpecificFileName = new HashMap<String, String>();
		this.fieldToGenerateList = new ArrayList<String>();
		this.uniqueKeyList = new HashSet<String>();
	}

	/**
	 * Permet de se procurer si il s'agit d'une generation specifique
	 * 
	 * @return Vrai si oui
	 */
	public Boolean getIsSpecificGeneration() {
		return isSpecificGeneration;
	}

	/**
	 * Permet de définir si c'est une configuration spécifique
	 * 
	 * @param isSpecificGeneration une configuration spécifique oui/non
	 */
	public void setIsSpecificGeneration(Boolean isSpecificGeneration) {
		this.isSpecificGeneration = isSpecificGeneration;
	}

	/**
	 * Permet de se procurer si on souhaite les en têtes
	 * 
	 * @return Vrai si oui
	 */
	public Boolean getWithHeader() {
		return withHeader;
	}

	/**
	 * Permet de définir si on souhaite les en têtes
	 * 
	 * @param withHeader oui si on souhaite les en tête
	 */
	public void setWithHeader(Boolean withHeader) {
		this.withHeader = withHeader;
	}

	/**
	 * Permet de se procurer la liste des champs à générer
	 * 
	 * @return Liste des champs à générer
	 */
	public List<String> getFieldToGenerateList() {
		return Collections.unmodifiableList(fieldToGenerateList);
	}

	/**
	 * Permet de se procurer le nom du fichier
	 * 
	 * @return le nom du fichier
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Permet de définir le nom du fichier
	 * 
	 * @param fileName le nom du fichier
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Permet de se procurer le type de label specific choisis Vide si aucun
	 * (generation classique)
	 * 
	 * @return le label choisis
	 */
	public String getLabelSpecificChoose() {
		return labelSpecificChoose;
	}

	/**
	 * Permet de définir le label specifique choisis
	 * 
	 * @param labelSpecificChoose le label specific choisis
	 */
	public void setLabelSpecificChoose(String labelSpecificChoose) {
		this.labelSpecificChoose = labelSpecificChoose;
	}

	/**
	 * Permet de se procurer la map des label specifique avec le nom associés
	 * 
	 * @return le label specifique et le nom associés
	 */
	public Map<String, String> getMapLabelSpecificFileName() {
		return Collections.unmodifiableMap(mapLabelSpecificFileName);
	}

	/**
	 * Permet d'ajouter un champ à générer
	 * 
	 * @param field champ à générer
	 */
	public void addFieldToGenerate(String field) {
		if (!this.getFieldToGenerateList().contains(field)) {
			this.fieldToGenerateList.add(field);
		}
	}

	/**
	 * Permet d'ajouter le couple label et nom du fichier
	 * 
	 * @param label    label du specifique
	 * @param fileName nom du fichier
	 */
	public void addLabelSpecificFileName(String label, String fileName) {
		if (!this.getMapLabelSpecificFileName().containsKey(label)) {
			this.mapLabelSpecificFileName.put(label, fileName);
		}
	}

	/**
	 * Permet de déterminer si on doit générer les textes de références
	 * 
	 * @return Vrai si on doit générer les textes de références
	 */
	public Boolean getHaveToGenerateReferenceText() {
		return haveToGenerateReferenceText;
	}

	/**
	 * Permet de définir si on doit générer les textes de références
	 * 
	 * @param haveToGenerateReferenceText vrai si on doit définir les textes de
	 *                                    références
	 */
	public void setHaveToGenerateReferenceText(Boolean haveToGenerateReferenceText) {
		this.haveToGenerateReferenceText = haveToGenerateReferenceText;
	}

	/**
	 * Permet de vider la liste des champs généré
	 */
	public void clearFieldListGenerate() {
		this.fieldToGenerateList.clear();
	}

	/**
	 * Permet de se procurer la liste des clés à générer
	 * 
	 * @return la liste des clés à générer
	 */
	public List<String> getUniqueKeyList() {
		return Collections.unmodifiableList(new ArrayList<>(this.uniqueKeyList));
	}

	/**
	 * Permet d'ajouter une clé unique à la liste des clés
	 * 
	 * @param uniquekey Clé unique à ajouter
	 */
	public void addUniqueKey(String uniquekey) {
		this.uniqueKeyList.add(uniquekey);
	}
}
