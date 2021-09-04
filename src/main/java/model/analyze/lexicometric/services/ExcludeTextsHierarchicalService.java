package model.analyze.lexicometric.services;

import model.analyze.lexicometric.beans.ExcludeTextsHierarchicalEnum;
import model.analyze.lexicometric.beans.ProperNounHierarchicalEnum;
import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import view.beans.ExcludeTextsHierarchicalEditEnum;

import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Service permettant de convertir une tokenization hierarchical en provenance de la vue en tokenization hierarchical serveur
 *
 */
public class ExcludeTextsHierarchicalService implements ILexicometricHierarchical<ExcludeTextsHierarchicalEnum> {

    private final ILexicometricHierarchical<ExcludeTextsHierarchicalEditEnum> excludeTextsHierarchicalEditEnumILexicometricHierarchical;

    /**
     * Constructeur
     * @param excludeTextsHierarchicalEditEnumILexicometricHierarchical lexicometricHierarchical en provenance de la vue
     */
    public ExcludeTextsHierarchicalService(ILexicometricHierarchical<ExcludeTextsHierarchicalEditEnum> excludeTextsHierarchicalEditEnumILexicometricHierarchical) {
        this.excludeTextsHierarchicalEditEnumILexicometricHierarchical = excludeTextsHierarchicalEditEnumILexicometricHierarchical;
    }

    @Override
    public Map<ExcludeTextsHierarchicalEnum, Integer> getHierarchicalIntegerMap() {
        return this.excludeTextsHierarchicalEditEnumILexicometricHierarchical.getHierarchicalIntegerMap().entrySet().stream().collect(
                Collectors.toMap(e -> ExcludeTextsHierarchicalEnum.getExcludeTextsHierarchicalEnumFromProperNounHierarchicalEditEnum(e.getKey()),
                        v -> v.getValue()));
    }
}
