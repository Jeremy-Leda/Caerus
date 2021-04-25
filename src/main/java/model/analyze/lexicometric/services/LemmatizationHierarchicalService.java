package model.analyze.lexicometric.services;

import model.analyze.lexicometric.beans.LemmatizationHierarchicalEnum;
import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import view.beans.LemmatizationHierarchicalEditEnum;

import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Service permettant de convertir une lexicometric hierarchical en provenance de la vue en lexicometric hierarchical serveur
 *
 */
public class LemmatizationHierarchicalService implements ILexicometricHierarchical<LemmatizationHierarchicalEnum> {

    private final ILexicometricHierarchical<LemmatizationHierarchicalEditEnum> lexicometricHierarchical;

    /**
     * Constructeur
     * @param lexicometricHierarchical lexicometricHierarchical en provenance de la vue
     */
    public LemmatizationHierarchicalService(ILexicometricHierarchical<LemmatizationHierarchicalEditEnum> lexicometricHierarchical) {
        this.lexicometricHierarchical = lexicometricHierarchical;
    }

    @Override
    public Map<LemmatizationHierarchicalEnum, Integer> getHierarchicalIntegerMap() {
        return this.lexicometricHierarchical.getHierarchicalIntegerMap().entrySet().stream().collect(
                Collectors.toMap(e -> LemmatizationHierarchicalEnum.getLemmatizationHierarchicalEnumFromLemmatizationHierarchicalEditEnum(e.getKey()),
                        v -> v.getValue()));
    }
}
