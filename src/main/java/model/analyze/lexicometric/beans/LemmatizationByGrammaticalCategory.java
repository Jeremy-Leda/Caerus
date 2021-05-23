package model.analyze.lexicometric.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.analyze.lexicometric.interfaces.ILexicometricCopyData;

import java.util.*;

/**
 *
 * Bean pour la lemmatisation
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LemmatizationByGrammaticalCategory implements ILexicometricCopyData<Map<String, Map<String, Set<String>>>>  {

    private String profile;
    private Map<String, Map<String, Set<String>>> baseListWordsMap = new HashMap<>();

    @Override
    public void setData(Map<String, Map<String, Set<String>>> data) {
        this.baseListWordsMap = data;
    }

    @Override
    public Map<String, Map<String, Set<String>>> clone() {
        return Map.copyOf(this.baseListWordsMap);
    }

    @Override
    public String getProfile() {
        return this.profile;
    }

    @JsonIgnore
    @Override
    public Map<String, Map<String, Set<String>>> getData() {
        return Collections.unmodifiableMap(baseListWordsMap);
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    // ---- UTILISER PAR JSON ------
    public Map<String, Map<String, Set<String>>> getBaseListWordsMap() {
        return baseListWordsMap;
    }

    public void setBaseListWordsMap(Map<String, Map<String, Set<String>>> baseListWordsMap) {
        this.baseListWordsMap = baseListWordsMap;
    }
    // -----------------------------


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LemmatizationByGrammaticalCategory that = (LemmatizationByGrammaticalCategory) o;
        return Objects.equals(profile, that.profile) && Objects.equals(baseListWordsMap, that.baseListWordsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile, baseListWordsMap);
    }
}
