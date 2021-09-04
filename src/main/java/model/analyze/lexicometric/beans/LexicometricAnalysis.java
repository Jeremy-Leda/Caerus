package model.analyze.lexicometric.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
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

    private Set<Lemmatization> lemmatizationSet = new HashSet<>();
    private Set<Tokenization> tokenizationSet = new HashSet<>();
    private Set<LemmatizationByGrammaticalCategory> lemmatizationByGrammaticalCategorySet = new HashSet<>();
    private Set<ProperNoun> properNounSet = new HashSet<>();
    private Set<ExcludeTexts> excludeTextsSet = new HashSet<>();

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

    public Set<LemmatizationByGrammaticalCategory> getLemmatizationByGrammaticalCategorySet() {
        return lemmatizationByGrammaticalCategorySet;
    }

    public void setLemmatizationByGrammaticalCategorySet(Set<LemmatizationByGrammaticalCategory> lemmatizationByGrammaticalCategorySet) {
        this.lemmatizationByGrammaticalCategorySet = lemmatizationByGrammaticalCategorySet;
    }

    public Set<ProperNoun> getProperNounSet() {
        return properNounSet;
    }

    public void setProperNounSet(Set<ProperNoun> properNounSet) {
        this.properNounSet = properNounSet;
    }

    public Set<ExcludeTexts> getExcludeTextsSet() {
        return excludeTextsSet;
    }

    public void setExcludeTextsSet(Set<ExcludeTexts> excludeTextsSet) {
        this.excludeTextsSet = excludeTextsSet;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return lemmatizationSet.isEmpty() &&
                tokenizationSet.isEmpty() &&
                lemmatizationByGrammaticalCategorySet.isEmpty() &&
                properNounSet.isEmpty() &&
                excludeTextsSet.isEmpty();
    }

}
