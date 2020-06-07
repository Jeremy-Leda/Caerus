package model.analyze.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean pour filtrer sur les corpus
 * @author jerem
 *
 */
public class FilterCorpus {

	private final String corpusName;
	private final List<FilterText> fiterTextList;
	
	/**
	 * Constructeur
	 * @param corpusName Nom du corpus pour le filtre
	 * @param fiterTextList liste des filtres pour les textes
	 */
	public FilterCorpus(String corpusName, List<FilterText> fiterTextList) {
		this.corpusName = corpusName;
		this.fiterTextList = new ArrayList<FilterText>();
		if (null != fiterTextList) {
			this.fiterTextList.addAll(fiterTextList);
		}
	}

	/**
	 * Permet de se procurer le nom du corpus sur lequel filtrer
	 * @return le nom du corpus
	 */
	public String getCorpusName() {
		return corpusName;
	}


	/**
	 * Permet de se procurer l'ensemble des filtres sur les textes
	 * @return les filtres sur les textes
	 */
	public List<FilterText> getFiterTextList() {
		return fiterTextList;
	}
	
	
	
}
