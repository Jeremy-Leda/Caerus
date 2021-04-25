package model.analyze.lexicometric.beans;

import io.vavr.collection.Stream;
import model.analyze.UserLexicometricAnalysisSettings;
import model.analyze.lexicometric.interfaces.ILexicometricConfiguration;
import model.analyze.lexicometric.interfaces.ILexicometricData;
import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import view.beans.EditTableElement;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 *
 * Configuration à utiliser pour la lemmatisation
 *
 */
public class TokenizationConfiguration implements ILexicometricConfiguration<String> {

    private final Map<TokenizationHierarchicalEnum, Integer> tokenizationHierarchicalEnumIntegerMap;

    public TokenizationConfiguration(ILexicometricHierarchical<TokenizationHierarchicalEnum> lexicometricHierarchical) {
        this.tokenizationHierarchicalEnumIntegerMap = lexicometricHierarchical.getHierarchicalIntegerMap();
    }

    @Override
    public Set<String> getProfilesSet() {
        return UserLexicometricAnalysisSettings.getInstance().getTokenizationSet().stream().map(d -> d.getProfile()).collect(Collectors.toSet());
    }

    @Override
    public List<FillTableConfiguration<String>> getFillTableConfigurationList() {
        return Stream.of(getBaseConfiguration()).collect(Collectors.toList());
    }

    @Override
    public BiConsumer<String, EditTableElement> getEditConsumer() {
        return LexicometricConfigurationEnum.TOKENIZATION.getEditTableElementBiConsumer();
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
                .dest(tokenizationHierarchicalEnumIntegerMap.get(TokenizationHierarchicalEnum.BASE))
                .biFunction((profil, x) -> {
                    Optional<ILexicometricData<Set<String>>> lexicometricData = UserLexicometricAnalysisSettings.getInstance().getTokenizationSet().stream().filter(data -> data.getProfile().equals(profil)).findFirst();
                    if (lexicometricData.isEmpty()) {
                        return new HashSet<>();
                    }
                    return lexicometricData.get().getData();
                })
                .build();
    }

}
