package model.analyze.lexicometric.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 *
 * Bean utilisé pour les analyses lexicométriques
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LexicometricAnalysis {

    private Set<Lemmatization> lemmatizationSet;
    private Set<Tokenization> tokenizationSet;

    public Set<Lemmatization> getLemmatizationSet() {
        return lemmatizationSet;
    }

    public void setLemmatizationSet(Set<Lemmatization> lemmatizationSet) {
        this.lemmatizationSet = lemmatizationSet;
    }

    public Set<Tokenization> getTokenizationSet() {
        return tokenizationSet;
    }

    public void setTokenizationSet(Set<Tokenization> tokenizationSet) {
        this.tokenizationSet = tokenizationSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LexicometricAnalysis that = (LexicometricAnalysis) o;
        return Objects.equals(lemmatizationSet, that.lemmatizationSet) && Objects.equals(tokenizationSet, that.tokenizationSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lemmatizationSet, tokenizationSet);
    }
}
