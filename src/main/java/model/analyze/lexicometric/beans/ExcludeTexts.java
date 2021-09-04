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
 * Bean pour les textes à exclure
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExcludeTexts implements ILexicometricCopyData<Set<String>> {

    private String profile;
    private Set<String> properNounSet = new HashSet<>();

    public String getProfile() {
        return profile;
    }

    @JsonIgnore
    @Override
    public Set<String> getData() {
        return Collections.unmodifiableSet(this.properNounSet);
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    // ---- UTILISER PAR JSON ------
    public Set<String> getProperNounSet() {
        return properNounSet;
    }

    public void setProperNounSet(Set<String> properNounSet) {
        this.properNounSet = properNounSet;
    }
    // -----------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExcludeTexts that = (ExcludeTexts) o;
        return Objects.equals(profile, that.profile) && Objects.equals(properNounSet, that.properNounSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile, properNounSet);
    }

    @Override
    public void setData(Set<String> data) {
        this.properNounSet = data;
    }

    @Override
    public Set<String> clone() {
        return Set.copyOf(this.properNounSet);
    }
}
