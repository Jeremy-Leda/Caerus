package view.beans;

import view.interfaces.IHierarchicalTable;
import view.interfaces.IRootTable;

import java.util.Set;

/**
 *
 * Enumeration des liste lexicométrique éditable pour la vue
 *
 */
public enum LexicometricEditEnum implements IHierarchicalTable {
    TOKENIZATION(Set.of(TokenizationHierarchicalEditEnum.BASE)),
    LEMMATIZATION(Set.of(LemmatizationHierarchicalEditEnum.BASE, LemmatizationHierarchicalEditEnum.LEMME)),
    LEMMATIZATION_BY_GRAMMATICAL_CATEGORY(Set.of(LemmatizationByGrammaticalCategoryHierarchicalEditEnum.CATEGORY,
            LemmatizationByGrammaticalCategoryHierarchicalEditEnum.BASE, LemmatizationByGrammaticalCategoryHierarchicalEditEnum.LEMME));

    private final Set<IRootTable> hierarchicalTableSet;

    /**
     * Constructeur
     * @param hierarchicalTableSet le set des tables hiérarchique
     */
    LexicometricEditEnum(Set<IRootTable> hierarchicalTableSet) {
        this.hierarchicalTableSet = hierarchicalTableSet;
    }

    /**
     * Permet de se procurer les tables hiérarchique
     * @return le set des tables hiérarchique
     */
    @Override
    public Set<IRootTable> getHierarchicalTableSet() {
        return this.hierarchicalTableSet;
    }
}
