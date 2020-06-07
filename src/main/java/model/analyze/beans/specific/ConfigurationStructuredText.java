package model.analyze.beans.specific;

import java.util.ArrayList;
import java.util.List;

import model.analyze.beans.SpecificConfiguration;
import model.analyze.beans.StructuredFile;

/**
 * 
 * Bean permettant de configurer le retraitement afin de g�n�rer un
 * structuredText sp�cifique
 * 
 * @author Jeremy
 *
 */
public class ConfigurationStructuredText {
	private final SpecificConfiguration specificConfiguration;
	private final List<StructuredFile> structuredFileList = new ArrayList<StructuredFile>();
	


	/**
	 * Constructeur
	 * @param delimiter delimiteur pour la configuration (utilis� pour les tag � traiter)
	 */
	public ConfigurationStructuredText(SpecificConfiguration specificConfiguration) {
		super();
		this.specificConfiguration = specificConfiguration;
	}
	
	/**
	 * Permet de se procurer la liste des fichiers structur�s
	 * @return la liste des fichiers structur�s
	 */
	public List<StructuredFile> getStructuredFileList() {
		return structuredFileList;
	}

	/**
	 * Permet de se procurer la configuration specifique
	 * @return Configuration specifique
	 */
	public SpecificConfiguration getSpecificConfiguration() {
		return specificConfiguration;
	}
	
	
}
