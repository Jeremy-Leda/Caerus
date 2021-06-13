package model.analyze.lexicometric.analyze.beans;

import java.util.Objects;

/**
 *
 * Bean permettant de stocker les informations d'un token
 *
 */
public class Token {

    private final String word;
    private Long nbOcurrency;

    /**
     * Constructeur
     * @param word mot représentant le token
     */
    public Token(String word) {
        this.word = word;
    }

    /**
     * Permet de se procurer le mot
     * @return le mot
     */
    public String getWord() {
        return word;
    }

    /**
     * Permet de se procurer le nombre d'occurence du token
     * @return le nombre d'occurence du token
     */
    public Long getNbOcurrency() {
        return nbOcurrency;
    }

    /**
     * Permet de définir le nombre d'occurence du token
     * @param nbOcurrency le nombre d'occurence du token
     */
    public void setNbOcurrency(Long nbOcurrency) {
        this.nbOcurrency = nbOcurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(word, token.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }

    @Override
    public String toString() {
        return "Token{" +
                "word='" + word + '\'' +
                ", nbOcurrency=" + nbOcurrency +
                '}';
    }
}
