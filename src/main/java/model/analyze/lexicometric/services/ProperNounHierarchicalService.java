package model.analyze.lexicometric.services;

import model.analyze.lexicometric.beans.ProperNounHierarchicalEnum;
import model.analyze.lexicometric.beans.TokenizationHierarchicalEnum;
import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import view.beans.ProperNounHierarchicalEditEnum;
import view.beans.TokenizationHierarchicalEditEnum;

import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Service permettant de convertir une tokenization hierarchical en provenance de la vue en tokenization hierarchical serveur
 *
 */
public class ProperNounHierarchicalService implements ILexicometricHierarchical<ProperNounHierarchicalEnum> {

    private final ILexicometricHierarchical<ProperNounHierarchicalEditEnum> properNounHierarchical;

    /**
     * Constructeur
     * @param properNounHierarchical lexicometricHierarchical en provenance de la vue
     */
    public ProperNounHierarchicalService(ILexicometricHierarchical<ProperNounHierarchicalEditEnum> properNounHierarchical) {
        this.properNounHierarchical = properNounHierarchical;
    }

    @Override
    public Map<ProperNounHierarchicalEnum, Integer> getHierarchicalIntegerMap() {
        return this.properNounHierarchical.getHierarchicalIntegerMap().entrySet().stream().collect(
                Collectors.toMap(e -> ProperNounHierarchicalEnum.getProperNounHierarchicalEnumFromProperNounHierarchicalEditEnum(e.getKey()),
                        v -> v.getValue()));
    }
}
