package analyze.beans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * Bean permettant de décrire une configuration
 * 
 * @author jerem
 *
 */
public class Configuration {

	private String name;
	private String baseCode;
	private String excelFileName;
	private List<SpecificConfiguration> specificConfigurationList;
	private List<StructuredField> structuredFieldList;
	
	/**
	 * Constructeur
	 */
	public Configuration() {
		super();
	}
	
	/**
	 * Permet de se procurer le nom de la configuration
	 * @return le nom de la configuration
	 */
	public String getName() {
		return name;
	}

	/**
	 * Permet de définir le nom de la configuration
	 * @param baseCode le nom de la configuration
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Permet de se procurer le code de base
	 * @return le code de base
	 */
	public String getBaseCode() {
		return baseCode;
	}

	/**
	 * Permet de définir le code de base
	 * @param baseCode code de base à définir
	 */
	public void setBaseCode(String baseCode) {
		this.baseCode = baseCode;
	}

	/**
	 * Permet de se procurer le nom du fichier excel
	 * @return le nom du fichier excel
	 */
	public String getExcelFileName() {
		return excelFileName;
	}

	/**
	 * Permet de définir le nom du fichier excel
	 * @param excelFileName nom du fichier excel
	 */
	public void setExcelFileName(String excelFileName) {
		this.excelFileName = excelFileName;
	}

	/**
	 * Permet de se procurer la liste des configurations spécifique
	 * @return la liste des configurations spécifique
	 */
	public List<SpecificConfiguration> getSpecificConfigurationList() {
		if (null != specificConfigurationList) {
			return specificConfigurationList;
		}
		return new ArrayList<>();
	}

	/**
	 * Permet de définir la liste des configurations spécifiques
	 * @param specificConfigurationList La liste des configurations spécifique
	 */
	public void setSpecificConfigurationList(List<SpecificConfiguration> specificConfigurationList) {
		this.specificConfigurationList = specificConfigurationList;
	}

	/**
	 * Permet de se procurer la liste des champs structurés
	 * @return la liste des champs structurés
	 */
	public List<StructuredField> getStructuredFieldList() {
		return structuredFieldList;
	}

	/**
	 * Permet de définir la liste des champs structurés
	 * @param structuredFieldList la liste des champs structurés
	 */
	public void setStructuredFieldList(List<StructuredField> structuredFieldList) {
		this.structuredFieldList = structuredFieldList;
	}
	
	/**
	 * Permet de se procurer la liste des champ méta
	 * @return la liste des champ méta
	 */
	public List<String> getMetaFieldList() {
		return this.structuredFieldList.stream()
				.filter(s -> s.getIsMetaFile()).map(s -> s.getFieldName()).collect(Collectors.toList());
	}
	
	/**
	 * Permet de se procurer la liste des champ spécific
	 * @return la liste des champ spécific
	 */
	public List<String> getSpecificFieldList() {
		return getSpecificConfigurationList().stream().flatMap(sc -> sc.getTreatmentFieldList().stream()).distinct()
				.collect(Collectors.toList());
	}
	
	/**
	 * Permet de se procurer la liste des champ en tête spécific
	 * @return la liste des champ en tête spécific
	 */
	public List<String> getSpecificHeaderFieldList() {
		return getSpecificConfigurationList().stream().flatMap(sc -> sc.getHeaderFieldList().stream()).distinct()
				.collect(Collectors.toList());
	}
	
	/**
	 * Permet de se procurer la liste des champ commun (ni spécifique, ni meta)
	 * @return la liste des champ communs
	 */
	public List<String> getCommonFieldList() {
		List<String> otherFieldList = new ArrayList<String>();
		otherFieldList.addAll(getSpecificFieldList());
		otherFieldList.addAll(getMetaFieldList());
		otherFieldList.addAll(getSpecificHeaderFieldList());
		return this.structuredFieldList.stream().sorted(Comparator.comparing(StructuredField::getOrder))
				.map(s -> s.getFieldName()).filter(s -> !otherFieldList.contains(s)).collect(Collectors.toList());
	}
}
