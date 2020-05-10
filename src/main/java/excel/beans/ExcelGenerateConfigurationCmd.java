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
	 * Permet de d�finir si c'est une configuration sp�cifique
	 * 
	 * @param isSpecificGeneration une configuration sp�cifique oui/non
	 */
	public void setIsSpecificGeneration(Boolean isSpecificGeneration) {
		this.isSpecificGeneration = isSpecificGeneration;
	}

	/**
	 * Permet de se procurer si on souhaite les en t�tes
	 * 
	 * @return Vrai si oui
	 */
	public Boolean getWithHeader() {
		return withHeader;
	}

	/**
	 * Permet de d�finir si on souhaite les en t�tes
	 * 
	 * @param withHeader oui si on souhaite les en t�te
	 */
	public void setWithHeader(Boolean withHeader) {
		this.withHeader = withHeader;
	}

	/**
	 * Permet de se procurer la liste des champs � g�n�rer
	 * 
	 * @return Liste des champs � g�n�rer
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
	 * Permet de d�finir le nom du fichier
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
	 * Permet de d�finir le label specifique choisis
	 * 
	 * @param labelSpecificChoose le label specific choisis
	 */
	public void setLabelSpecificChoose(String labelSpecificChoose) {
		this.labelSpecificChoose = labelSpecificChoose;
	}

	/**
	 * Permet de se procurer la map des label specifique avec le nom associ�s
	 * 
	 * @return le label specifique et le nom associ�s
	 */
	public Map<String, String> getMapLabelSpecificFileName() {
		return Collections.unmodifiableMap(mapLabelSpecificFileName);
	}

	/**
	 * Permet d'ajouter un champ � g�n�rer
	 * 
	 * @param field champ � g�n�rer
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
	 * Permet de d�terminer si on doit g�n�rer les textes de r�f�rences
	 * 
	 * @return Vrai si on doit g�n�rer les textes de r�f�rences
	 */
	public Boolean getHaveToGenerateReferenceText() {
		return haveToGenerateReferenceText;
	}

	/**
	 * Permet de d�finir si on doit g�n�rer les textes de r�f�rences
	 * 
	 * @param haveToGenerateReferenceText vrai si on doit d�finir les textes de
	 *                                    r�f�rences
	 */
	public void setHaveToGenerateReferenceText(Boolean haveToGenerateReferenceText) {
		this.haveToGenerateReferenceText = haveToGenerateReferenceText;
	}

	/**
	 * Permet de vider la liste des champs g�n�r�
	 */
	public void clearFieldListGenerate() {
		this.fieldToGenerateList.clear();
	}

	/**
	 * Permet de se procurer la liste des cl�s � g�n�rer
	 * 
	 * @return la liste des cl�s � g�n�rer
	 */
	public List<String> getUniqueKeyList() {
		return Collections.unmodifiableList(new ArrayList<>(this.uniqueKeyList));
	}

	/**
	 * Permet d'ajouter une cl� unique � la liste des cl�s
	 * 
	 * @param uniquekey Cl� unique � ajouter
	 */
	public void addUniqueKey(String uniquekey) {
		this.uniqueKeyList.add(uniquekey);
	}
}
