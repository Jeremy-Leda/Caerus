package model.analyze.beans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * Bean permettant de d�crire une configuration
 * 
 * @author jerem
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {

	private String name;
	private String baseCode;
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
	 * Permet de d�finir le nom de la configuration
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
	 * Permet de d�finir le code de base
	 * @param baseCode code de base � d�finir
	 */
	public void setBaseCode(String baseCode) {
		this.baseCode = baseCode;
	}

	/**
	 * Permet de se procurer la liste des configurations sp�cifique
	 * @return la liste des configurations sp�cifique
	 */
	public List<SpecificConfiguration> getSpecificConfigurationList() {
		if (null != specificConfigurationList) {
			return specificConfigurationList;
		}
		return new ArrayList<>();
	}

	/**
	 * Permet de d�finir la liste des configurations sp�cifiques
	 * @param specificConfigurationList La liste des configurations sp�cifique
	 */
	public void setSpecificConfigurationList(List<SpecificConfiguration> specificConfigurationList) {
		this.specificConfigurationList = specificConfigurationList;
	}

	/**
	 * Permet de se procurer la liste des champs structur�s
	 * @return la liste des champs structur�s
	 */
	public List<StructuredField> getStructuredFieldList() {
		return structuredFieldList;
	}

	/**
	 * Permet de d�finir la liste des champs structur�s
	 * @param structuredFieldList la liste des champs structur�s
	 */
	public void setStructuredFieldList(List<StructuredField> structuredFieldList) {
		this.structuredFieldList = structuredFieldList;
	}
	
	/**
	 * Permet de se procurer la liste des champ m�ta
	 * @return la liste des champ m�ta
	 */
	@JsonIgnore
	public List<String> getMetaFieldList() {
		return this.structuredFieldList.stream()
				.filter(s -> s.getIsMetaFile()).map(s -> s.getFieldName()).collect(Collectors.toList());
	}
	
	/**
	 * Permet de se procurer la liste des champ sp�cific
	 * @return la liste des champ sp�cific
	 */
	@JsonIgnore
	public List<String> getSpecificFieldList() {
		return getSpecificConfigurationList().stream().flatMap(sc -> sc.getTreatmentFieldList().stream()).distinct()
				.collect(Collectors.toList());
	}
	
	/**
	 * Permet de se procurer la liste des champ en t�te sp�cific
	 * @return la liste des champ en t�te sp�cific
	 */
	@JsonIgnore
	public List<String> getSpecificHeaderFieldList() {
		return getSpecificConfigurationList().stream().flatMap(sc -> sc.getHeaderFieldList().stream()).distinct()
				.collect(Collectors.toList());
	}
	
	/**
	 * Permet de se procurer la liste des champ commun (ni sp�cifique, ni meta)
	 * @return la liste des champ communs
	 */
	@JsonIgnore
	public List<String> getCommonFieldList() {
		List<String> otherFieldList = new ArrayList<String>();
		otherFieldList.addAll(getSpecificFieldList());
		otherFieldList.addAll(getMetaFieldList());
		otherFieldList.addAll(getSpecificHeaderFieldList());
		return this.structuredFieldList.stream().sorted(Comparator.comparing(StructuredField::getOrder))
				.map(s -> s.getFieldName()).filter(s -> !otherFieldList.contains(s)).collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Configuration that = (Configuration) o;
		return Objects.equals(name, that.name) &&
				Objects.equals(baseCode, that.baseCode) &&
				Objects.equals(specificConfigurationList, that.specificConfigurationList) &&
				Objects.equals(structuredFieldList, that.structuredFieldList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, baseCode, specificConfigurationList, structuredFieldList);
	}
}
