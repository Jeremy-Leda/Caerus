package view.beans;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Contient une ligne d'affichage pour le sp�cifique
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
	 * Permet de se procurer la liste des champs sp�cifique
	 * @return la liste des champs sp�cifique
	 */
	public List<String> getSpecificList() {
		return specificList;
	}
	
	/**
	 * Permet d'ajouter un specifique � la ligne
	 * @param specificToAdd specific � ajouter
	 */
	public void addSpecific(String specificToAdd) {
		this.specificList.add(specificToAdd);
	}
	
	/**
	 * Permet d'ajouter une liste de sp�cifique � la ligne
	 * @param allSpecificToAdd liste de sp�cifique � ajouter
	 */
	public void addAllSpecific(List<String> allSpecificToAdd) {
		this.specificList.addAll(allSpecificToAdd);
	}
	
}
