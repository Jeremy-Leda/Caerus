package model.analyze.lexicometric.services;

import model.analyze.lexicometric.beans.TokenizationHierarchicalEnum;
import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import view.beans.TokenizationHierarchicalEditEnum;

import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Service permettant de convertir une tokenization hierarchical en provenance de la vue en tokenization hierarchical serveur
 *
 */
public class TokenizationHierarchicalService implements ILexicometricHierarchical<TokenizationHierarchicalEnum> {

    private final ILexicometricHierarchical<TokenizationHierarchicalEditEnum> tokenizationHierarchical;

    /**
     * Constructeur
     * @param tokenizationHierarchical lexicometricHierarchical en provenance de la vue
     */
    public TokenizationHierarchicalService(ILexicometricHierarchical<TokenizationHierarchicalEditEnum> tokenizationHierarchical) {
        this.tokenizationHierarchical = tokenizationHierarchical;
    }

    @Override
    public Map<TokenizationHierarchicalEnum, Integer> getHierarchicalIntegerMap() {
        return this.tokenizationHierarchical.getHierarchicalIntegerMap().entrySet().stream().collect(
                Collectors.toMap(e -> TokenizationHierarchicalEnum.getTokenizationHierarchicalEnumFromTokenizationHierarchicalEditEnum(e.getKey()),
                        v -> v.getValue()));
    }
}
