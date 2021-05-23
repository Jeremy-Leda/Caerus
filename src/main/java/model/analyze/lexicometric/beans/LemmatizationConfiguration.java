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
 * Configuration à utiliser pour la lemmatisation
 *
 */
public class LemmatizationConfiguration implements ILexicometricConfiguration<String> {

    private final Map<LemmatizationHierarchicalEnum, Integer> lemmatizationHierarchicalEnumIntegerMap;
    private final Set<IRootTable> hierarchicalTableSet;

    public LemmatizationConfiguration(ILexicometricHierarchical<LemmatizationHierarchicalEnum> lexicometricHierarchical, Set<IRootTable> hierarchicalTableSet) {
        this.lemmatizationHierarchicalEnumIntegerMap = lexicometricHierarchical.getHierarchicalIntegerMap();
        this.hierarchicalTableSet = hierarchicalTableSet;
    }

    @Override
    public Set<String> getProfilesSet() {
        return UserLexicometricAnalysisSettings.getInstance().getLemmatizationSet().stream().map(d -> d.getProfile()).collect(Collectors.toSet());
    }

    @Override
    public List<FillTableConfiguration<String>> getFillTableConfigurationList() {
        return Stream.of(getBaseLemmeConfiguration(), getLemmeConfiguration()).collect(Collectors.toList());
    }

    @Override
    public BiConsumer<String, EditTableElement> getEditConsumer() {
        return LexicometricConfigurationEnum.LEMMATIZATION.getEditTableElementBiConsumer();
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
    private FillTableConfiguration<String> getBaseLemmeConfiguration() {
        return new FillTableConfigurationBuilder<String>()
                .source(Optional.empty())
                .dest(lemmatizationHierarchicalEnumIntegerMap.get(LemmatizationHierarchicalEnum.BASE))
                .biFunction((profil, x) -> {
                    Optional<ILexicometricData<Map<String, Set<String>>>> lexicometricData = UserLexicometricAnalysisSettings.getInstance().getLemmatizationSet().stream().filter(data -> data.getProfile().equals(profil)).findFirst();
                    if (lexicometricData.isEmpty()) {
                        return new HashSet<>();
                    }
                    return lexicometricData.get().getData().keySet();
                })
                .build();
    }

    /**
     * Permet de se procurer la configuration pour l'affichage des lemmes en fonction de la base
     * @return la configuration pour l'affichage des lemmes en fonction de la base
     */
    private FillTableConfiguration<String> getLemmeConfiguration() {
        return new FillTableConfigurationBuilder<String>()
                .source(Optional.ofNullable(lemmatizationHierarchicalEnumIntegerMap.get(LemmatizationHierarchicalEnum.BASE)))
                .dest(lemmatizationHierarchicalEnumIntegerMap.get(LemmatizationHierarchicalEnum.LEMME))
                .biFunction((profil, base) -> {
                    Optional<ILexicometricData<Map<String, Set<String>>>> lexicometricData = UserLexicometricAnalysisSettings.getInstance().getLemmatizationSet().stream().filter(data -> data.getProfile().equals(profil)).findFirst();
                    if (lexicometricData.isEmpty()) {
                        return new HashSet<>();
                    }
                    return lexicometricData.get().getData().get(base.get(0));
                })
                .build();
    }
}
