package model.analyze.lexicometric.analyze.beans;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * Bean permettant de stocker les informations d'un token
 *
 */
public class Token {

    private final String word;
    private Long nbOcurrency;

    private Integer frequencyOrder;

    private Optional<TokenFrequencyOrderRepo> tokenFrequencyOrderRepoOptional;

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

    public Optional<TokenFrequencyOrderRepo> getTokenFrequencyOrderRepoOptional() {
        return tokenFrequencyOrderRepoOptional;
    }

    public void setTokenFrequencyOrderRepoOptional(Optional<TokenFrequencyOrderRepo> tokenFrequencyOrderRepoOptional) {
        this.tokenFrequencyOrderRepoOptional = tokenFrequencyOrderRepoOptional;
    }

    public Integer getFrequencyOrder() {
        return frequencyOrder;
    }

    public void setFrequencyOrder(Integer frequencyOrder) {
        this.frequencyOrder = frequencyOrder;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Token token = (Token) o;
//        return Objects.equals(word, token.word);
//    }
//
//    @Override
//    public int hashCode() {
//        int result = 17;
//        if (word != null) {
//            result = 31 * result + word.hashCode();
//        }
//        return result;
//    }

    @Override
    public String toString() {
        return "Token{" +
                "word='" + word + '\'' +
                ", nbOcurrency=" + nbOcurrency +
                '}';
    }
}
