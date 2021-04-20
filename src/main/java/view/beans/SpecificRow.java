package view.beans;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 
 * Contient une ligne d'affichage pour le spécifique
 * 
 * @author jerem
 *
 */
public class SpecificRow {

	private List<String> specificList;
	
	public SpecificRow() {
		this.specificList = new LinkedList<String>();
	}
	
	/**
	 * Permet de se procurer la liste des champs spécifique
	 * @return la liste des champs spécifique
	 */
	public List<String> getSpecificList() {
		return specificList;
	}
	
	/**
	 * Permet d'ajouter un specifique à la ligne
	 * @param specificToAdd specific à ajouter
	 */
	public void addSpecific(String specificToAdd) {
		this.specificList.add(specificToAdd);
	}
	
	/**
	 * Permet d'ajouter une liste de spécifique à la ligne
	 * @param allSpecificToAdd liste de spécifique à ajouter
	 */
	public void addAllSpecific(List<String> allSpecificToAdd) {
		this.specificList.addAll(allSpecificToAdd);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SpecificRow that = (SpecificRow) o;
		return Objects.equals(specificList, that.specificList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(specificList);
	}
}
