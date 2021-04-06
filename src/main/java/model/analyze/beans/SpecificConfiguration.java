package model.analyze.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 
 * Bean déterminant une configuration spécifique
 * 
 * @author jerem
 *
 */
public class SpecificConfiguration {
	private Integer order;
	private String label;
	private String delimiter;
	private String nameFileSuffix;
	private List<String> ignoredFieldList;
	private List<String> treatmentFieldList;
	private List<String> headerFieldList;
	
	/**
	 * Constructeur
	 */
	public SpecificConfiguration() {
		super();
	}

	/**
	 * Permet de se procurer le délimiteur
	 * @return le délimiteur
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * Permet de définir le délimiteur
	 * @param delimiter délimiteur
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Permet de se procurer le suffixe pour le nom du fichier excel
	 * @return le suffixe pour le nom du fichier excel
	 */
	public String getNameFileSuffix() {
		return nameFileSuffix;
	}

	/**
	 * Permet de définir le suffixe pour le nom du fichier excel
	 * @param nameFileSuffix le suffixe pour le nom du fichier excel
	 */
	public void setNameFileSuffix(String nameFileSuffix) {
		this.nameFileSuffix = nameFileSuffix;
	}

	/**
	 * Permet de se procurer les champs à ignorer
	 * @return les champs à ignorer
	 */
	public List<String> getIgnoredFieldList() {
		if (null == ignoredFieldList) {
			this.ignoredFieldList = new ArrayList<String>();
		}
		return ignoredFieldList;
	}

	/**
	 * Permet de d�finir les champs à ignorer
	 * @param ignoredFieldList les champs à ignorer
	 */
	public void setIgnoredFieldList(List<String> ignoredFieldList) {
		this.ignoredFieldList = ignoredFieldList;
	}

	/**
	 * Permet de se procurer les champs à retraiter
	 * @return les champs à retraiter
	 */
	public List<String> getTreatmentFieldList() {
		return treatmentFieldList;
	}

	/**
	 * Permet de définir les champs à retraiter
	 * @param treatmentFieldList les champsà retraiter
	 */
	public void setTreatmentFieldList(List<String> treatmentFieldList) {
		this.treatmentFieldList = treatmentFieldList;
	}
	
	/**
	 * Permet de se procurer les champs d'en tête
	 * @return les champs d'en tête
	 */
	public List<String> getHeaderFieldList() {
		if (null == headerFieldList) {
			this.headerFieldList = new ArrayList<String>();
		}
		return headerFieldList;
	}

	/**
	 * Permet de définir les champs d'en tête
	 * @param headerFieldList les champs d'en tête
	 */
	public void setHeaderFieldList(List<String> headerFieldList) {
		this.headerFieldList = headerFieldList;
	}
	
	
	/**
	 * Permet de se procurer l'order
	 * @return l'order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * Permet de définir l'order
	 * @param order l'order
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * Permet de se procurer le label
	 * @return le label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Permet de définir le label
	 * @param label label à définir
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public boolean equals(Object obj) {
		return this.getLabel().equals(((SpecificConfiguration)obj).getLabel());
	}

	@Override
	public int hashCode() {
		return Objects.hash(label);
	}
}
