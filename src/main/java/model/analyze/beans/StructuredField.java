package model.analyze.beans;

import java.util.Objects;

/**
 * 
 * Bean d�terminant un champ structur�
 * 
 * @author jerem
 *
 */
public class StructuredField {

	private Boolean isMetaFile;
	private Integer order;
    private String label;
    private String fieldName;
    
    /**
     * Constructeur
     */
	public StructuredField() {
		super();
	}

	/**
	 * Permet de d�finir si le champ est une m�ta donn�es
	 * @return vrai si le champ est une m�ta donn�e
	 */
	public Boolean getIsMetaFile() {
		return isMetaFile;
	}

	/**
	 * Permet de d�finir si le champ est une m�ta donn�e
	 * @param isMetaFile Vrai si c'est une m�ta donn�e
	 */
	public void setIsMetaFile(Boolean isMetaFile) {
		this.isMetaFile = isMetaFile;
	}

	/**
	 * Permet se procurer la liste des libell�s pour le champ structur�
	 * @return la liste des libell�s
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Permet de d�finir la liste des libell�s
	 * @param labels libell�s � d�finir
	 */
	public void setLabels(String label) {
		this.label = label;
	}

	/**
	 * Permet de se procurer le nom du champ
	 * @return le nom du champ
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Permet de d�finir le nom du champ
	 * @param fieldName la nom du champ structur�
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	/**
	 * Permet de se procurer l'ordre
	 * @return l'ordre
	 */
	public Integer getOrder() {
		return order;
	}
	
	/**
	 * Permet de d�finir l'ordre
	 * @param order l'ordre
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}
    
	@Override
	public boolean equals(Object obj) {
		return this.getFieldName().equals(((StructuredField)obj).getFieldName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldName);
	}
    
}
