package model.analyze.lexicometric.beans;

import io.vavr.collection.Stream;
import model.analyze.UserLexicometricAnalysisSettings;
import model.analyze.lexicometric.interfaces.ILexicometricConfiguration;
import model.analyze.lexicometric.interfaces.ILexicometricData;
import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import view.beans.EditTableElement;
import view.interfaces.IRootTable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 *
 * Configuration à utiliser pour les noms propres
 *
 */
public class ProperNounConfiguration implements ILexicometricConfiguration<String> {

    private final Map<ProperNounHierarchicalEnum, Integer> tokenizationHierarchicalEnumIntegerMap;
    private final Set<IRootTable> hierarchicalTableSet;

    public ProperNounConfiguration(ILexicometricHierarchical<ProperNounHierarchicalEnum> lexicometricHierarchical, Set<IRootTable> hierarchicalTableSet) {
        this.tokenizationHierarchicalEnumIntegerMap = lexicometricHierarchical.getHierarchicalIntegerMap();
        this.hierarchicalTableSet = hierarchicalTableSet;
    }

    @Override
    public Set<String> getProfilesSet() {
        return UserLexicometricAnalysisSettings.getInstance().getDataSet(LexicometricCleanListEnum.PROPER_NOUN).stream().map(d -> d.getProfile()).collect(Collectors.toSet());
    }

    @Override
    public List<FillTableConfiguration<String>> getFillTableConfigurationList() {
        return Stream.of(getBaseConfiguration()).collect(Collectors.toList());
    }

    @Override
    public BiConsumer<String, EditTableElement> getEditConsumer() {
        return LexicometricConfigurationEnum.PROPER_NOUN.getEditTableElementBiConsumer();
    }

    @Override
    public Set<IRootTable> getHierarchicalTableSet() {
        return this.hierarchicalTableSet;
    }
//
//    @Override
//    public SortedSet<Integer> getSaveHierarchicalOrder() {
//        return Stream.of(0,1).collect(Collectors.toCollection(TreeSet::new));
//    }

    /**
     * Permet de se procurer la configuration pour l'affichage des mots de base
     * @return la configuration pour l'affichage des mots de base
     */
    private FillTableConfiguration<String> getBaseConfiguration() {
        return new FillTableConfigurationBuilder<String>()
                .source(Optional.empty())
                .dest(tokenizationHierarchicalEnumIntegerMap.get(ProperNounHierarchicalEnum.BASE))
                .biFunction((profil, x) -> {
                    Optional<ILexicometricData> lexicometricData = UserLexicometricAnalysisSettings.getInstance().getDataSet(LexicometricCleanListEnum.PROPER_NOUN).stream().filter(data -> data.getProfile().equals(profil)).findFirst();
                    if (lexicometricData.isEmpty()) {
                        return new HashSet<>();
                    }
                    return (Set<String>) lexicometricData.get().getData();
                })
                .build();
    }

}
