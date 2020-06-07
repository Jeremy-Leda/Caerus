package model.analyze.beans;

import model.analyze.constants.TypeFilterTextEnum;

/**
 * 
 * Bean permettant de filtrer sur les textes d'un corpus
 * 
 * @author jerem
 *
 */
public class FilterText {
	
	private String field;
	private TypeFilterTextEnum typeFilter;
	private String value;
	
	
	/**
	 * Constructeur	
	 * @param field Champ sur lequel on souhaite rechercher
	 * @param typeFilter type de filtre à appliquer
	 * @param value Valeur sur laquelle on va filtrer
	 */
	public FilterText(String field, TypeFilterTextEnum typeFilter, String value) {
		this.field = field;
		this.typeFilter = typeFilter;
		this.value = value;
	}

	/**
	 * Permet de se procurer le champ
	 * @return le champ
	 */
	public String getField() {
		return field;
	}
	
	/**
	 * Permet de se procurer le type de filtre
	 * @return le type de filtre
	 */
	public TypeFilterTextEnum getTypeFilter() {
		return typeFilter;
	}
	
	/**
	 * Permet de se procurer la valeur
	 * @return la valeur
	 */
	public String getValue() {
		return value;
	}
	
}
