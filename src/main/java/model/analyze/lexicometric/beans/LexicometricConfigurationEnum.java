package model.analyze.lexicometric.beans;

import model.analyze.UserLexicometricAnalysisSettings;
import model.analyze.lexicometric.interfaces.ILexicometricConfiguration;
import model.analyze.lexicometric.interfaces.ILexicometricData;
import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import model.analyze.lexicometric.services.LemmatizationHierarchicalService;
import model.analyze.lexicometric.services.LexicometricEditTableService;
import model.analyze.lexicometric.services.TokenizationHierarchicalService;
import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import view.beans.EditTableElement;
import view.beans.LemmatizationHierarchicalEditEnum;
import view.beans.LexicometricEditEnum;
import view.beans.TokenizationHierarchicalEditEnum;
import view.interfaces.IHierarchicalTable;
import view.interfaces.IRootTable;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 * Enumeration permettant de travailler sur les différentes configuration lexicométrique
 *
 */
public enum LexicometricConfigurationEnum {

    TOKENIZATION(LexicometricConfigurationEnum::editTokenization, LexicometricEditEnum.TOKENIZATION,
            iLexicometricHierarchical -> new TokenizationHierarchicalService((ILexicometricHierarchical<TokenizationHierarchicalEditEnum>) iLexicometricHierarchical),
            LexicometricConfigurationEnum::getTokenizationConfiguration),
    LEMMATIZATION(LexicometricConfigurationEnum::editLemmatization, LexicometricEditEnum.LEMMATIZATION,
            iLexicometricHierarchical -> new LemmatizationHierarchicalService((ILexicometricHierarchical<LemmatizationHierarchicalEditEnum>) iLexicometricHierarchical),
            LexicometricConfigurationEnum::getLemmatisationConfiguration);

    private static final LexicometricEditTableService lexicometricEditTableService = new LexicometricEditTableService();
    private final BiConsumer<String, EditTableElement> editTableElementBiConsumer;
    private final LexicometricEditEnum lexicometricEditEnum;
    private final Function<ILexicometricHierarchical<?>, ILexicometricHierarchical<?>> lexicometricHierarchicalViewToLexicometricHierarchicalServer;
    private final Function<ILexicometricHierarchical<?>, ILexicometricConfiguration> lexicometricHierarchicalILexicometricConfigurationFunction;
    private static Set<IRootTable> hierarchicalTableSet = null;

    /**
     * Constructeur
     * @param editTableElementBiConsumer Le consumer pour éditer les données serveur
     * @param lexicometricEditEnum Enumeration provenant de l'ihm
     * @param lexicometricHierarchicalViewToLexicometricHierarchicalServer
     * @param lexicometricHierarchicalILexicometricConfigurationFunction la configuration lexicometric
     */
    LexicometricConfigurationEnum(BiConsumer<String, EditTableElement> editTableElementBiConsumer, LexicometricEditEnum lexicometricEditEnum, Function<ILexicometricHierarchical<?>, ILexicometricHierarchical<?>> lexicometricHierarchicalViewToLexicometricHierarchicalServer, Function<ILexicometricHierarchical<?>, ILexicometricConfiguration> lexicometricHierarchicalILexicometricConfigurationFunction) {
        this.editTableElementBiConsumer = editTableElementBiConsumer;
        this.lexicometricEditEnum = lexicometricEditEnum;
        this.lexicometricHierarchicalViewToLexicometricHierarchicalServer = lexicometricHierarchicalViewToLexicometricHierarchicalServer;
        this.lexicometricHierarchicalILexicometricConfigurationFunction = lexicometricHierarchicalILexicometricConfigurationFunction;
    }

    /**
     * Permet de se procurer la correspondance avec l'énumération de l'ihm
     * @return la correspondance avec l'énumération de l'ihm
     */
    public LexicometricEditEnum getLexicometricEditEnum() {
        return lexicometricEditEnum;
    }

    /**
     * Permet de se procurer la fonction qui converti les informations hiérarchique en provenance de la vue vers le serveur
     * @return La fonction qui converti les informations hiérarchique en provenance de la vue vers le serveur
     */
    public Function<ILexicometricHierarchical<?>, ILexicometricHierarchical<?>> getLexicometricHierarchicalViewToLexicometricHierarchicalServer() {
        return lexicometricHierarchicalViewToLexicometricHierarchicalServer;
    }

    /**
     * Permet de se procurer la fonction qui permet de se procurer la configuration lexicométrique
     * @return La fonction qui permet de se procurer la configuration lexicométrique
     */
    public Function<ILexicometricHierarchical<?>, ILexicometricConfiguration> getLexicometricHierarchicalILexicometricConfigurationFunction() {
        return lexicometricHierarchicalILexicometricConfigurationFunction;
    }

    /**
     * Permet de se procurer le consumer pour éditer les données serveur
     * @return Le consumer pour éditer les données serveur
     */
    public BiConsumer<String, EditTableElement> getEditTableElementBiConsumer() {
        return editTableElementBiConsumer;
    }

    /**
     * Permet de se procurer la lemmatisation si elle existe
     * @param profil profil de recherche
     * @return la lemmatisation si elle existe
     */
    private static Optional<ILexicometricData> getLemmatizationData(String profil) {
        return UserLexicometricAnalysisSettings.getInstance().getLemmatizationSet().stream().filter(lemmatization -> lemmatization.getProfile().equals(profil)).map(x -> (ILexicometricData) x).findFirst();
    }

    /**
     * Permet de se procurer la tokenisation si elle existe
     * @param profil profil de recherche
     * @return la tokenisation si elle existe
     */
    private static Optional<ILexicometricData> getTokenizationData(String profil) {
        return UserLexicometricAnalysisSettings.getInstance().getTokenizationSet().stream().filter(lemmatization -> lemmatization.getProfile().equals(profil)).map(x -> (ILexicometricData) x).findFirst();
    }

    /**
     * Permet de déterminer les tables hiérarchiques en provenance de l'IHM
     * @return Les tables hiérarchiques en provenance de l'IHM
     */
    public void setHierarchicalTableSet(Set<IRootTable> hierarchicalTableSet) {
        this.hierarchicalTableSet = hierarchicalTableSet;
    }

    /**
     * Permet d'éditer les données serveur concernant la lemmatisation
     * @param profil profil de recherche
     * @param editTableElement données nécessaire à l'édition
     */
    private static void editLemmatization(String profil, EditTableElement editTableElement) {
        Optional<ILexicometricData> optionalILexicometricData = getLemmatizationData(profil);
        optionalILexicometricData.ifPresent(iLexicometricData -> {
            Map<String, Set<String>> data = (Map<String, Set<String>>) iLexicometricData.getData();
            Map<String, Set<String>> result = (Map<String, Set<String>>) editTableElement.getActionEditTableEnum().getApplyFunction().apply(lexicometricEditTableService, editTableElement, data);
            UserLexicometricAnalysisSettings.getInstance().saveLemmatization(profil, result);
        });
    }

    /**
     * Permet d'éditer les données serveur concernant la tokenisation
     * @param profil profil de recherche
     * @param editTableElement données nécessaire à l'édition
     */
    private static void editTokenization(String profil, EditTableElement editTableElement) {
        Optional<ILexicometricData> optionalILexicometricData = getTokenizationData(profil);
        optionalILexicometricData.ifPresent(iLexicometricData -> {
            Set<String> data = (Set<String>) iLexicometricData.getData();
            Set<String> result = (Set<String>) editTableElement.getActionEditTableEnum().getApplyFunction().apply(lexicometricEditTableService, editTableElement, data);
            UserLexicometricAnalysisSettings.getInstance().saveTokenization(profil, result);
        });
    }

    /**
     * Permet de se procurer la configuration pour la tokenization
     * @param lexicometricHierarchical interface contenant les informations des datas
     * @return La configuration pour la tokenization
     */
    private static ILexicometricConfiguration getTokenizationConfiguration(ILexicometricHierarchical<?> lexicometricHierarchical) {
        return new TokenizationConfiguration((ILexicometricHierarchical<TokenizationHierarchicalEnum>) lexicometricHierarchical, hierarchicalTableSet);
    }

    /**
     * Permet de se procurer la configuration pour la tokenization
     * @param lexicometricHierarchical interface contenant les informations des datas
     * @return La configuration pour la tokenization
     */
    private static ILexicometricConfiguration getLemmatisationConfiguration(ILexicometricHierarchical<?> lexicometricHierarchical) {
        return new LemmatizationConfiguration((ILexicometricHierarchical<LemmatizationHierarchicalEnum>) lexicometricHierarchical, hierarchicalTableSet);
    }

    /**
     * Permet de se procurer la configuration lexicometric serveur en fonction de l'énumération de la vue
     * @param lexicometricEditEnum énumération en provenance de la vue
     * @return la configuration lexicometric serveur
     */
    public static LexicometricConfigurationEnum getLexicometricConfigurationEnumFromViewEnum(IHierarchicalTable lexicometricEditEnum) {
        LexicometricConfigurationEnum lexicometricConfigurationEnumServer = Arrays.stream(values()).filter(lexicometricConfigurationEnum -> lexicometricConfigurationEnum.getLexicometricEditEnum().equals(lexicometricEditEnum)).findFirst().orElseThrow(() -> {
            InformationException informationException = new InformationExceptionBuilder()
                    .errorCode(ErrorCode.TECHNICAL_ERROR)
                    .objectInError(lexicometricEditEnum)
                    .build();
            throw new ServerException().addInformationException(informationException);
        });
        lexicometricConfigurationEnumServer.setHierarchicalTableSet(lexicometricEditEnum.getHierarchicalTableSet());
        return lexicometricConfigurationEnumServer;
    }
}
