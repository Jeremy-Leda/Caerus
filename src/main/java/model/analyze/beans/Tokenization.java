package model.analyze.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * Bean pour la tokenization
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tokenization {

    private String profile;
    private Set<String> words = new HashSet<>();

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Set<String> getWords() {
        return words;
    }

    public void setWords(Set<String> words) {
        this.words = words;
    }

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
}
