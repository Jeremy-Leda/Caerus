package model.analyze.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * Bean pour la lemmatisation
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lemmatization {

    private String profile;
    private Map<String, Set<String>> baseListWordsMap = new HashMap<>();

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Map<String, Set<String>> getBaseListWordsMap() {
        return baseListWordsMap;
    }

    public void setBaseListWordsMap(Map<String, Set<String>> baseListWordsMap) {
        this.baseListWordsMap = baseListWordsMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lemmatization that = (Lemmatization) o;
        return Objects.equals(profile, that.profile) && Objects.equals(baseListWordsMap, that.baseListWordsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile, baseListWordsMap);
    }
}
