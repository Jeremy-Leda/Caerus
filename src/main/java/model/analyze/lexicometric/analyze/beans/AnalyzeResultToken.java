package model.analyze.lexicometric.analyze.beans;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Bean permettant de se procurer les résultats de l'analyse pour les tokens
 */
public class AnalyzeResultToken {

    private final Set<Text> textSet;
    private final boolean excludeTexts;

    /**
     * Constructeur
     * @param textSet la liste des textes
     * @param excludeTexts contients les textes à exclure
     */
    public AnalyzeResultToken(Set<Text> textSet, boolean excludeTexts) {
        this.textSet = textSet;
        this.excludeTexts = excludeTexts;
    }

    public boolean isExcludeTexts() {
        return excludeTexts;
    }

    /**
     * Permet de se procurer la liste des tokens
     * @return la liste des tokens
     */
    public List<Token> getTokenList() {
       return textSet.stream().flatMap(t -> t.getTokenSet().stream()).collect(Collectors.toList());
    }

    /**
     * Permet de se procurer la liste des mots sans doublons
     * @return la liste des mots sans doublons
     */
    public Set<String> getWordSet() {
        return textSet.stream().flatMap(t -> t.getTokenSet().stream()).map(t -> t.getWord()).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
