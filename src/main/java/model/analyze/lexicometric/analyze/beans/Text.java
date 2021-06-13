package model.analyze.lexicometric.analyze.beans;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * Bean permettant de stocker les informations d'un texte
 *
 */
public class Text {

    private final String key;
    private final Set<Token> tokenSet = new HashSet<>();

    /**
     * Constructeur
     * @param key Clé du texte
     */
    public Text(String key) {
        this.key = key;
    }

    /**
     * Permet de se procurer la clé du texte
     * @return la clé du texte
     */
    public String getKey() {
        return key;
    }

    /**
     * Permet de se procurer la liste des tokens du textes
     * @return la liste des tokens du textes
     */
    public Set<Token> getTokenSet() {
        return tokenSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Text text = (Text) o;
        return Objects.equals(key, text.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "Text{" +
                "key='" + key + '\'' +
                ", tokenSet=" + tokenSet +
                '}';
    }
}
