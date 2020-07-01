package model.analyze.beans;

/**
 * 
 * Bean déterminant un champ structuré
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
	 * Permet de définir si le champ est une méta données
	 * @return vrai si le champ est une méta donnée
	 */
	public Boolean getIsMetaFile() {
		return isMetaFile;
	}

	/**
	 * Permet de définir si le champ est une méta donnée
	 * @param isMetaFile Vrai si c'est une méta donnée
	 */
	public void setIsMetaFile(Boolean isMetaFile) {
		this.isMetaFile = isMetaFile;
	}

	/**
	 * Permet se procurer la liste des libellés pour le champ structuré
	 * @return la liste des libellés
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Permet de définir la liste des libellés
	 * @param labels libellés à définir
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
	 * Permet de définir le nom du champ
	 * @param fieldName la nom du champ structuré
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
	 * Permet de définir l'ordre
	 * @param order l'ordre
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}
    
	@Override
	public boolean equals(Object obj) {
		return this.getFieldName().equals(((StructuredField)obj).getFieldName());
	}
	
    
}
