package analyze.beans;

/**
 * 
 * Bean contenant un contenu dans le texte
 * 
 * @author Jeremy
 *
 */
public class Content {

	private final String key;
	private String value;
	
	/**
	 * Utilis� par le chargement du Json
	 */
	@SuppressWarnings("unused")
	private Content() {
		this.key = null;
	}
	
	/**
	 * Constructeur avec la cl�
	 * @param key la cl�
	 * @param value la valeur
	 */
	public Content(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * La cl�
	 * @return la cl�
	 */
	public String getKey() {
		return this.key;
	}
	
	/**
	 * La valeur contenu pour la cl� donn�
	 * @return les valeur
	 */
	public String getValue() {
		return this.value;
	}
	
	
	/**
	 * Permet de modifier la valeur
	 * @param value la valeur
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Permet de dupliquer l'objet
	 * @return l'objet dupliqu�
	 */
	public Content duplicate() {
		return new Content(new String(this.key), new String(this.value));
	}
	
	/*
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getKey());
		sb.append(this.getValue());
		return sb.toString();
	}

	/*
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
}
