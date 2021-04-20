package model.analyze.constants;

import io.vavr.Function3;
import model.ConfigurationModel;
import model.IConfigurationModel;
import model.analyze.UserLexicometricAnalysisSettings;
import view.beans.EditTable;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 *
 * Enumeration pour les types d'analyses lexicométrique
 *
 */
public enum LexicometricAnalysisType {
    TOKENIZATION(UserLexicometricAnalysisSettings.getInstance().getLexicometricAnalysis().getTokenizationSet().stream().map(d -> d.getProfile()).collect(Collectors.toSet()), IConfigurationModel::saveTokenization),
    LEMMATIZATION(UserLexicometricAnalysisSettings.getInstance().getLexicometricAnalysis().getLemmatizationSet().stream().map(d -> d.getProfile()).collect(Collectors.toSet()), IConfigurationModel::saveLemmatization);

    private Set<String> profileSet;
    private BiConsumer<IConfigurationModel, EditTable> saveConsumer;

    LexicometricAnalysisType(Set<String> profileSet, BiConsumer<IConfigurationModel, EditTable> saveConsumer) {
        this.profileSet = profileSet;
        this.saveConsumer = saveConsumer;
    }

    /**
     * Permet de se procurer la liste des profiles disponibles
     * @return la liste des profiles disponibles
     */
    public Set<String> getProfileSet() {
        return profileSet;
    }

    /**
     * Permet de se procurer le consommateur pour la sauvegarde
     * @return le consommateur pour la sauvegarde
     */
    public BiConsumer<IConfigurationModel, EditTable> getSaveConsumer() {
        return saveConsumer;
    }
}
