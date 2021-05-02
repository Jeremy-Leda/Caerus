package model.analyze.lexicometric.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.analyze.lexicometric.interfaces.ILexicometricCopyData;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * Bean pour la tokenization
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tokenization implements ILexicometricCopyData<Set<String>> {

    private String profile;
    private Set<String> words = new HashSet<>();

    public String getProfile() {
        return profile;
    }

    @JsonIgnore
    @Override
    public Set<String> getData() {
        return Collections.unmodifiableSet(this.words);
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    // ---- UTILISER PAR JSON ------
    public Set<String> getWords() {
        return words;
    }

    public void setWords(Set<String> words) {
        this.words = words;
    }
    // -----------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tokenization that = (Tokenization) o;
        return Objects.equals(profile, that.profile) && Objects.equals(words, that.words);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile, words);
    }

    @Override
    public void setData(Set<String> data) {
        this.words = data;
    }

    @Override
    public Set<String> clone() {
        return Set.copyOf(this.words);
    }
}
