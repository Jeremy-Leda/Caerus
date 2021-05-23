package model.analyze.lexicometric.services;

import model.analyze.lexicometric.beans.LemmatizationByGrammaticalCategoryHierarchicalEnum;
import model.analyze.lexicometric.beans.LemmatizationHierarchicalEnum;
import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import view.beans.LemmatizationByGrammaticalCategoryHierarchicalEditEnum;
import view.beans.LemmatizationHierarchicalEditEnum;

import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Service permettant de convertir une lexicometric hierarchical en provenance de la vue en lexicometric hierarchical serveur
 *
 */
public class LemmatizationByGrammaticalCategoryHierarchicalService implements ILexicometricHierarchical<LemmatizationByGrammaticalCategoryHierarchicalEnum> {

    private final ILexicometricHierarchical<LemmatizationByGrammaticalCategoryHierarchicalEditEnum> lexicometricHierarchical;

    /**
     * Constructeur
     * @param lexicometricHierarchical lexicometricHierarchical en provenance de la vue
     */
    public LemmatizationByGrammaticalCategoryHierarchicalService(ILexicometricHierarchical<LemmatizationByGrammaticalCategoryHierarchicalEditEnum> lexicometricHierarchical) {
        this.lexicometricHierarchical = lexicometricHierarchical;
    }

    @Override
    public Map<LemmatizationByGrammaticalCategoryHierarchicalEnum, Integer> getHierarchicalIntegerMap() {
        return this.lexicometricHierarchical.getHierarchicalIntegerMap().entrySet().stream().collect(
                Collectors.toMap(e -> LemmatizationByGrammaticalCategoryHierarchicalEnum.getLemmatizationByGrammaticalCategoryHierarchicalEnumFromLemmatizationByGrammaticalCategoryHierarchicalEditEnum(e.getKey()),
                        v -> v.getValue()));
    }
}
