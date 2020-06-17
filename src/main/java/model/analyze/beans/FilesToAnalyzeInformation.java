package model.analyze.beans;

import java.util.List;

/**
 * Bean permettant de fournir les informations sur le chargement des fichiers
 * @author jerem
 *
 */
public class FilesToAnalyzeInformation {

	private final List<String> nameFileList;
	private final Boolean launchAnalyzeIsOk;
	
	/**
	 * Constructeur
	 * @param nameFileList liste des noms de fichiers
	 * @param launchAnalyzeIsOk Détermine si on peut lancer l'analyse
	 */
	public FilesToAnalyzeInformation(List<String> nameFileList, Boolean launchAnalyzeIsOk) {
		this.nameFileList = nameFileList;
		this.launchAnalyzeIsOk = launchAnalyzeIsOk;
	}

	/**
	 * Permet de se procurer la liste des fichiers
	 * @return la liste des fichiers
	 */
	public List<String> getNameFileList() {
		return nameFileList;
	}

	/**
	 * Permet de définir si on peut lancer l'analyse
	 * @return Vrai si on peut lancer l'analyse
	 */
	public Boolean getLaunchAnalyzeIsOk() {
		return launchAnalyzeIsOk;
	}
	
	
}
