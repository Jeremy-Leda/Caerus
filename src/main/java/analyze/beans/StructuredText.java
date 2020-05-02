package analyze.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Bean contenant le contenu d'un texte
 * 
 * @author Jeremy
 *
 */
public class StructuredText {



	private final List<Content> listContent = new ArrayList<Content>();
	private Boolean haveBlankLine = Boolean.FALSE;
	private Boolean haveMetaBlankLine = Boolean.FALSE;
	private static Logger logger = LoggerFactory.getLogger(StructuredText.class);
	
	public List<Content> getListContent() {
		return this.listContent;
	}
	
	/**
	 * Permet de se procurer le contenu pour un tag donn�
	 * @param tag tag demand�
	 * @return la liste des contenu
	 */
	public String getContent(String tag) {
		Optional<Content> optionalContent = this.listContent.stream().filter(c -> tag.equals(c.getKey())).findFirst();
		if (optionalContent.isPresent()) {
			return optionalContent.get().getValue();
		}
		logger.debug(String.format("Le champ %s n'a pas �t� trouv�", tag));
		return StringUtils.EMPTY;
	}
	
	/**
	 * Permet de modifier le contenu d'une valeur pour un tag donn�
	 * @param tag tag demand�
	 * @param value la nouvelle valeur
	 */
	public void modifyContent(String tag, String value) {
		Optional<Content> optionalContent = this.listContent.stream().filter(c -> tag.equals(c.getKey())).findFirst();
		if (optionalContent.isPresent()) {
			optionalContent.get().setValue(value);
		} else {
			this.listContent.add(new Content(tag, value));
		}
	}
	
	/**
	 * Permet de supprimer un contenu pour tag donn�
	 * @param tag tag � supprimer
	 */
	public void deleteContent(String tag) {
		this.listContent.removeIf(c -> tag.equals(c.getKey()));
	}
	
	/**
	 * Permet de se procurer le contenu d'un tag donn� et de traiter son contenu pour subdiviser ses valeurs
	 * @param tag tag demand�
	 * @param delimiter delimiteur pour la subdivision
	 * @return la liste des valeurs du contenu
	 */
	public List<String> getContentWithDelimiterProcess(String tag, String delimiter) {
		String value = getContent(tag);
		return Arrays.asList(StringUtils.split(value, delimiter));
	}
	
	/**
	 * Permet de dupliquer le texte structur�
	 * @return le texte structur� dupliqu�
	 */
	public StructuredText duplicate() {
		StructuredText duplicateStructuredText = new StructuredText();
		List<Content> collect = this.listContent.stream().map(c -> c.duplicate()).collect(Collectors.toList());
		duplicateStructuredText.getListContent().addAll(collect);
		return duplicateStructuredText;
	}
	
	/*
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((listContent == null) ? 0 : listContent.hashCode());
		return result;
	}

	/*
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		listContent.forEach(c -> sb.append(c.toString()));
		return sb.toString();
	}

	public Boolean getHaveBlankLine() {
		return haveBlankLine;
	}

	public void setHaveBlankLine(Boolean haveBlankLine) {
		this.haveBlankLine = haveBlankLine;
	}

	public Boolean getHaveMetaBlankLine() {
		return haveMetaBlankLine;
	}

	public void setHaveMetaBlankLine(Boolean haveMetaBlankLine) {
		this.haveMetaBlankLine = haveMetaBlankLine;
	}

}
