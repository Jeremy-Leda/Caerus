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
public class LemmatizationByGrammaticalCategoryConfiguration implements ILexicometricConfiguration<String> {

    private final Map<LemmatizationByGrammaticalCategoryHierarchicalEnum, Integer> lemmatizationByGrammaticalCategoryHierarchicalEnumIntegerMap;
    private final Set<IRootTable> hierarchicalTableSet;

    public LemmatizationByGrammaticalCategoryConfiguration(ILexicometricHierarchical<LemmatizationByGrammaticalCategoryHierarchicalEnum> lexicometricHierarchical, Set<IRootTable> hierarchicalTableSet) {
        this.lemmatizationByGrammaticalCategoryHierarchicalEnumIntegerMap = lexicometricHierarchical.getHierarchicalIntegerMap();
        this.hierarchicalTableSet = hierarchicalTableSet;
    }

    @Override
    public Set<String> getProfilesSet() {
        return UserLexicometricAnalysisSettings.getInstance().getLemmatizationByGrammaticalCategorySet().stream().map(d -> d.getProfile()).collect(Collectors.toSet());
    }

    @Override
    public List<FillTableConfiguration<String>> getFillTableConfigurationList() {
        return Stream.of(getCategoryLemmeConfiguration(), getBaseLemmeConfiguration(), getLemmeConfiguration()).collect(Collectors.toList());
    }

    @Override
    public BiConsumer<String, EditTableElement> getEditConsumer() {
        return LexicometricConfigurationEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY.getEditTableElementBiConsumer();
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
     * Permet de se procurer la configuration pour l'affichage des mots de catégorie
     * @return la configuration pour l'affichage des mots de catégorie
     */
    private FillTableConfiguration<String> getCategoryLemmeConfiguration() {
        return new FillTableConfigurationBuilder<String>()
                .source(Optional.empty())
                .dest(lemmatizationByGrammaticalCategoryHierarchicalEnumIntegerMap.get(LemmatizationByGrammaticalCategoryHierarchicalEnum.CATEGORY))
                .biFunction((profil, x) -> {
                    Optional<ILexicometricData<Map<String, Map<String,Set<String>>>>> lexicometricData = UserLexicometricAnalysisSettings.getInstance().getLemmatizationByGrammaticalCategorySet().stream().filter(data -> data.getProfile().equals(profil)).findFirst();
                    if (lexicometricData.isEmpty()) {
                        return new HashSet<>();
                    }
                    return lexicometricData.get().getData().keySet();
                })
                .build();
    }

    /**
     * Permet de se procurer la configuration pour l'affichage des mots de base en fonction de la catégorie
     * @return la configuration pour l'affichage des mots de base en fonction de la catégorie
     */
    private FillTableConfiguration<String> getBaseLemmeConfiguration() {
        return new FillTableConfigurationBuilder<String>()
                .source(Optional.ofNullable(lemmatizationByGrammaticalCategoryHierarchicalEnumIntegerMap.get(LemmatizationByGrammaticalCategoryHierarchicalEnum.CATEGORY)))
                .dest(lemmatizationByGrammaticalCategoryHierarchicalEnumIntegerMap.get(LemmatizationByGrammaticalCategoryHierarchicalEnum.BASE))
                .biFunction((profil, x) -> {
                    Optional<ILexicometricData<Map<String, Map<String,Set<String>>>>> lexicometricData = UserLexicometricAnalysisSettings.getInstance().getLemmatizationByGrammaticalCategorySet().stream().filter(data -> data.getProfile().equals(profil)).findFirst();
                    if (lexicometricData.isEmpty()) {
                        return new HashSet<>();
                    }
                    return lexicometricData.get().getData().get(x.get(0)).keySet();
                })
                .build();
    }

    /**
     * Permet de se procurer la configuration pour l'affichage des lemmes en fonction de la catégorie et en fonction de la base
     * @return la configuration pour l'affichage des lemmes en fonction de la catégorie et en fonction de la base
     */
    private FillTableConfiguration<String> getLemmeConfiguration() {
        return new FillTableConfigurationBuilder<String>()
                .source(Optional.ofNullable(lemmatizationByGrammaticalCategoryHierarchicalEnumIntegerMap.get(LemmatizationByGrammaticalCategoryHierarchicalEnum.BASE)))
                .dest(lemmatizationByGrammaticalCategoryHierarchicalEnumIntegerMap.get(LemmatizationByGrammaticalCategoryHierarchicalEnum.LEMME))
                .biFunction((profil, base) -> {
                    Optional<ILexicometricData<Map<String, Map<String, Set<String>>>>> lexicometricData = UserLexicometricAnalysisSettings.getInstance().getLemmatizationByGrammaticalCategorySet().stream().filter(data -> data.getProfile().equals(profil)).findFirst();
                    if (lexicometricData.isEmpty()) {
                        return new HashSet<>();
                    }
                    return lexicometricData.get().getData().get(base.get(0)).get(base.get(1));
                })
                .build();
    }
}
