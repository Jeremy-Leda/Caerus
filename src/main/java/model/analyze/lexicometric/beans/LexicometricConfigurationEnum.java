package model.analyze.lexicometric.beans;

import model.analyze.UserLexicometricAnalysisSettings;
import model.analyze.lexicometric.interfaces.ILexicometricConfiguration;
import model.analyze.lexicometric.interfaces.ILexicometricCopyData;
import model.analyze.lexicometric.interfaces.ILexicometricData;
import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import model.analyze.lexicometric.services.LemmatizationByGrammaticalCategoryHierarchicalService;
import model.analyze.lexicometric.services.LemmatizationHierarchicalService;
import model.analyze.lexicometric.services.LexicometricEditTableService;
import model.analyze.lexicometric.services.TokenizationHierarchicalService;
import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import org.apache.commons.lang3.StringUtils;
import view.beans.*;
import view.interfaces.IHierarchicalTable;
import view.interfaces.IRootTable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * Enumeration permettant de travailler sur les différentes configuration lexicométrique
 *
 */
public enum LexicometricConfigurationEnum {

    TOKENIZATION(LexicometricConfigurationEnum::editTokenization, LexicometricEditEnum.TOKENIZATION,
            iLexicometricHierarchical -> new TokenizationHierarchicalService((ILexicometricHierarchical<TokenizationHierarchicalEditEnum>) iLexicometricHierarchical),
            LexicometricConfigurationEnum::getTokenizationConfiguration, getTokenizationCopyConsumer(),
            getAddProfilTokenizationConsumer(), getRemoveProfilTokenizationConsumer(), x -> UserLexicometricAnalysisSettings.getInstance().saveTokenizationConfigurationInFile(x), x -> getTokenizationProfils()),
    LEMMATIZATION(LexicometricConfigurationEnum::editLemmatization, LexicometricEditEnum.LEMMATIZATION,
            iLexicometricHierarchical -> new LemmatizationHierarchicalService((ILexicometricHierarchical<LemmatizationHierarchicalEditEnum>) iLexicometricHierarchical),
            LexicometricConfigurationEnum::getLemmatisationConfiguration, getLemmatisationCopyConsumer(),
            getAddProfilLemmatizationConsumer(), getRemoveProfilLemmatizationConsumer(), x -> UserLexicometricAnalysisSettings.getInstance().saveLemmatizationConfigurationInFile(x), x -> getLemmatizationProfils()),
    LEMMATIZATION_BY_GRAMMATICAL_CATEGORY(LexicometricConfigurationEnum::editLemmatizationByGrammaticalCategory, LexicometricEditEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY,
            iLexicometricHierarchical -> new LemmatizationByGrammaticalCategoryHierarchicalService((ILexicometricHierarchical<LemmatizationByGrammaticalCategoryHierarchicalEditEnum>) iLexicometricHierarchical),
            LexicometricConfigurationEnum::getLemmatisationByGrammaticalCategoryConfiguration, getLemmatisationByGrammaticalCategoryCopyConsumer(),
            getAddProfilLemmatizationByGrammaticalCategoryConsumer(), getRemoveProfilLemmatizationByGrammaticalCategoryConsumer(),
            x -> UserLexicometricAnalysisSettings.getInstance().saveLemmatizationByGrammaticalCategoryConfigurationInFile(x), x -> getLemmatizationByGrammaticalCategoryProfils());

    private static final LexicometricEditTableService lexicometricEditTableService = new LexicometricEditTableService();
    private final BiConsumer<String, EditTableElement> editTableElementBiConsumer;
    private final LexicometricEditEnum lexicometricEditEnum;
    private final Function<ILexicometricHierarchical<?>, ILexicometricHierarchical<?>> lexicometricHierarchicalViewToLexicometricHierarchicalServer;
    private final Function<ILexicometricHierarchical<?>, ILexicometricConfiguration> lexicometricHierarchicalILexicometricConfigurationFunction;
    private final BiConsumer<String, String> copyConsumer;
    private final Consumer<String> addProfilConsumer;
    private final Consumer<String> removeProfilConsumer;
    private final Consumer<String> saveInDiskConsumer;
    private static Set<IRootTable> hierarchicalTableSet = null;
    private final Function<Void, Set<String>> allProfilsFunction;

    /**
     * Constructeur
     * @param editTableElementBiConsumer Le consumer pour éditer les données serveur
     * @param lexicometricEditEnum Enumeration provenant de l'ihm
     * @param lexicometricHierarchicalViewToLexicometricHierarchicalServer
     * @param lexicometricHierarchicalILexicometricConfigurationFunction la configuration lexicometric
     * @param copyConsumer
     * @param addProfilConsumer
     * @param removeProfilConsumer
     * @param saveInDiskConsumer
     * @param allProfils
     */
    LexicometricConfigurationEnum(BiConsumer<String, EditTableElement> editTableElementBiConsumer, LexicometricEditEnum lexicometricEditEnum, Function<ILexicometricHierarchical<?>, ILexicometricHierarchical<?>> lexicometricHierarchicalViewToLexicometricHierarchicalServer, Function<ILexicometricHierarchical<?>, ILexicometricConfiguration> lexicometricHierarchicalILexicometricConfigurationFunction, BiConsumer<String, String> copyConsumer, Consumer<String> addProfilConsumer, Consumer<String> removeProfilConsumer, Consumer<String> saveInDiskConsumer, Function<Void, Set<String>> allProfilsFunction) {
        this.editTableElementBiConsumer = editTableElementBiConsumer;
        this.lexicometricEditEnum = lexicometricEditEnum;
        this.lexicometricHierarchicalViewToLexicometricHierarchicalServer = lexicometricHierarchicalViewToLexicometricHierarchicalServer;
        this.lexicometricHierarchicalILexicometricConfigurationFunction = lexicometricHierarchicalILexicometricConfigurationFunction;
        this.copyConsumer = copyConsumer;
        this.addProfilConsumer = addProfilConsumer;
        this.removeProfilConsumer = removeProfilConsumer;
        this.saveInDiskConsumer = saveInDiskConsumer;
        this.allProfilsFunction = allProfilsFunction;
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
     * Permet de se procurer la lemmatisation par catégorie grammatical si elle existe
     * @param profil profil de recherche
     * @return la lemmatisation par catégorie grammatical si elle existe
     */
    private static Optional<ILexicometricData> getLemmatizationByGrammaticalCategoryData(String profil) {
        return UserLexicometricAnalysisSettings.getInstance().getLemmatizationByGrammaticalCategorySet().stream().filter(lemmatization -> lemmatization.getProfile().equals(profil)).map(x -> (ILexicometricData) x).findFirst();
    }

    /**
     * Permet de se procurer la liste des profils pour la lemmatisation
     * @return la liste des profils pour la lemmatisation
     */
    private static Set<String> getLemmatizationProfils() {
        return UserLexicometricAnalysisSettings.getInstance().getLemmatizationSet().stream().map(lemmatization -> lemmatization.getProfile()).collect(Collectors.toSet());
    }

    /**
     * Permet de se procurer la liste des profils pour la tokenization
     * @return la liste des profils pour la tokenization
     */
    private static Set<String> getTokenizationProfils() {
        return UserLexicometricAnalysisSettings.getInstance().getTokenizationSet().stream().map(tokenization -> tokenization.getProfile()).collect(Collectors.toSet());
    }

    /**
     * Permet de se procurer la liste des profils pour la lemmatisation par catégorie grammatical
     * @return la liste des profils pour la lemmatisation par catégorie grammatical
     */
    private static Set<String> getLemmatizationByGrammaticalCategoryProfils() {
        return UserLexicometricAnalysisSettings.getInstance().getLemmatizationByGrammaticalCategorySet().stream().map(lemmatization -> lemmatization.getProfile()).collect(Collectors.toSet());
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
     * Permet d'éditer les données serveur concernant la lemmatisation par catégorie grammatical
     * @param profil profil de recherche
     * @param editTableElement données nécessaire à l'édition
     */
    private static void editLemmatizationByGrammaticalCategory(String profil, EditTableElement editTableElement) {
        Optional<ILexicometricData> optionalILexicometricData = getLemmatizationByGrammaticalCategoryData(profil);
        optionalILexicometricData.ifPresent(iLexicometricData -> {
            Map<String, Map<String, Set<String>>> data = (Map<String, Map<String, Set<String>>>) iLexicometricData.getData();
            Map<String, Map<String, Set<String>>> result = (Map<String, Map<String, Set<String>>>) editTableElement.getActionEditTableEnum().getApplyFunction().apply(lexicometricEditTableService, editTableElement, data);
            UserLexicometricAnalysisSettings.getInstance().saveLemmatizationByGrammaticalCategory(profil, result);
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
     * Permet de se procurer la configuration pour la lemmatisation
     * @param lexicometricHierarchical interface contenant les informations des datas
     * @return La configuration pour la lemmatisation
     */
    private static ILexicometricConfiguration getLemmatisationConfiguration(ILexicometricHierarchical<?> lexicometricHierarchical) {
        return new LemmatizationConfiguration((ILexicometricHierarchical<LemmatizationHierarchicalEnum>) lexicometricHierarchical, hierarchicalTableSet);
    }

    /**
     * Permet de se procurer la configuration pour la lemmatisation par catégorie grammatical
     * @param lexicometricHierarchical interface contenant les informations des datas
     * @return La configuration pour la lemmatisation par catégorie grammatical
     */
    private static ILexicometricConfiguration getLemmatisationByGrammaticalCategoryConfiguration(ILexicometricHierarchical<?> lexicometricHierarchical) {
        return new LemmatizationByGrammaticalCategoryConfiguration((ILexicometricHierarchical<LemmatizationByGrammaticalCategoryHierarchicalEnum>) lexicometricHierarchical, hierarchicalTableSet);
    }

    /**
     * Permet de se procurer un consumer de copie pour la lemmatisation
     * @return le consumer de copie pour la lemmatisation
     */
    private static BiConsumer<String, String> getLemmatisationCopyConsumer() {
        return (x,v) -> {
            Optional<ILexicometricData> lemmatizationDataOrigin = getLemmatizationData(x);
            Optional<ILexicometricData> lemmatizationDataDest = getLemmatizationData(v);
            if (lemmatizationDataOrigin.isPresent() && lemmatizationDataDest.isPresent()) {
                copyLexicometricData((ILexicometricCopyData) lemmatizationDataOrigin.get(), (ILexicometricCopyData) lemmatizationDataDest.get());
            }
        };
    }

    /**
     * Permet de se procurer un consumer de copie pour la tokenization
     * @return le consumer de copie pour la tokenization
     */
    private static BiConsumer<String, String> getTokenizationCopyConsumer() {
        return (x,v) -> {
            Optional<ILexicometricData> tokenizationDataOrigin = getTokenizationData(x);
            Optional<ILexicometricData> tokenizationDataDest = getTokenizationData(v);
            if (tokenizationDataOrigin.isPresent() && tokenizationDataDest.isPresent()) {
                copyLexicometricData((ILexicometricCopyData) tokenizationDataOrigin.get(), (ILexicometricCopyData) tokenizationDataDest.get());
            }
        };
    }

    /**
     * Permet de se procurer un consumer de copie pour la lemmatisation par catégorie grammatical
     * @return le consumer de copie pour la lemmatisation par catégorie grammatical
     */
    private static BiConsumer<String, String> getLemmatisationByGrammaticalCategoryCopyConsumer() {
        return (x,v) -> {
            Optional<ILexicometricData> lemmatizationDataOrigin = getLemmatizationByGrammaticalCategoryData(x);
            Optional<ILexicometricData> lemmatizationDataDest = getLemmatizationByGrammaticalCategoryData(v);
            if (lemmatizationDataOrigin.isPresent() && lemmatizationDataDest.isPresent()) {
                copyLexicometricData((ILexicometricCopyData) lemmatizationDataOrigin.get(), (ILexicometricCopyData) lemmatizationDataDest.get());
            }
        };
    }

    /**
     * Permet de copier des données lexicométrique
     * @param origin origine des données
     * @param dest destination des données
     */
    private static void copyLexicometricData(ILexicometricCopyData origin, ILexicometricCopyData dest) {
        dest.setData(origin.clone());
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

    /**
     * Permet de se procurer le consumer pour la copie
     * Param 1 : profil d'origine
     * Param 2 : profil de dest
     * @return le consumer
     */
    public BiConsumer<String, String> getCopyConsumer() {
        return copyConsumer;
    }

    /**
     * Permet de se procurer le consumer pour la création de profil pour la lemmatization
     * @return le consumer pour la création de profil pour la lemmatization
     */
    private static Consumer<String> getAddProfilLemmatizationConsumer() {
        return x -> {
            if (StringUtils.isBlank(x)) {
                return;
            }
            Boolean exist = UserLexicometricAnalysisSettings.getInstance().getLemmatizationSet().stream().anyMatch(s -> s.getProfile().toLowerCase(Locale.ROOT).equals(x.toLowerCase(Locale.ROOT)));
            if (exist) {
                throw new ServerException().addInformationException(new InformationExceptionBuilder()
                        .errorCode(ErrorCode.VALUE_EXIST)
                        .objectInError(x)
                        .build());
            }
            Lemmatization lemmatization = new Lemmatization();
            lemmatization.setProfile(x);
            UserLexicometricAnalysisSettings.getInstance().getLemmatizationSet().add(lemmatization);
        };
    }

    /**
     * Permet de se procurer le consumer pour la création de profil pour la tokenization
     * @return le consumer pour la création de profil pour la tokenization
     */
    private static Consumer<String> getAddProfilTokenizationConsumer() {
        return x -> {
            if (StringUtils.isBlank(x)) {
                return;
            }
            Boolean exist = UserLexicometricAnalysisSettings.getInstance().getTokenizationSet().stream().anyMatch(s -> s.getProfile().toLowerCase(Locale.ROOT).equals(x.toLowerCase(Locale.ROOT)));
            if (exist) {
                throw new ServerException().addInformationException(new InformationExceptionBuilder()
                        .errorCode(ErrorCode.VALUE_EXIST)
                        .objectInError(x)
                        .build());
            }
            Tokenization tokenization = new Tokenization();
            tokenization.setProfile(x);
            UserLexicometricAnalysisSettings.getInstance().getTokenizationSet().add(tokenization);
        };
    }

    /**
     * Permet de se procurer le consumer pour la création de profil pour la lemmatization par catégorie grammatical
     * @return le consumer pour la création de profil pour la lemmatization par catégorie grammatical
     */
    private static Consumer<String> getAddProfilLemmatizationByGrammaticalCategoryConsumer() {
        return x -> {
            if (StringUtils.isBlank(x)) {
                return;
            }
            Boolean exist = UserLexicometricAnalysisSettings.getInstance().getLemmatizationByGrammaticalCategorySet().stream().anyMatch(s -> s.getProfile().toLowerCase(Locale.ROOT).equals(x.toLowerCase(Locale.ROOT)));
            if (exist) {
                throw new ServerException().addInformationException(new InformationExceptionBuilder()
                        .errorCode(ErrorCode.VALUE_EXIST)
                        .objectInError(x)
                        .build());
            }
            LemmatizationByGrammaticalCategory lemmatization = new LemmatizationByGrammaticalCategory();
            lemmatization.setProfile(x);
            UserLexicometricAnalysisSettings.getInstance().getLemmatizationByGrammaticalCategorySet().add(lemmatization);
        };
    }

    /**
     * Permet de se procurer le consumer pour la suppression d'un profil pour la lemmatization
     * @return le consumer pour la suppression d'un profil pour la lemmatization
     */
    private static Consumer<String> getRemoveProfilLemmatizationConsumer() {
        return x -> {
            UserLexicometricAnalysisSettings.getInstance().getLemmatizationSet().removeIf(s -> s.getProfile().toLowerCase(Locale.ROOT).equals(x.toLowerCase(Locale.ROOT)));
            UserLexicometricAnalysisSettings.getInstance().setRemoveLemmatizationProfil(x);
        };
    }

    /**
     * Permet de se procurer le consumer pour la suppression d'un profil pour la tokenization
     * @return le consumer pour la suppression d'un profil pour la tokenization
     */
    private static Consumer<String> getRemoveProfilTokenizationConsumer() {
        return x -> {
            UserLexicometricAnalysisSettings.getInstance().getTokenizationSet().removeIf(s -> s.getProfile().toLowerCase(Locale.ROOT).equals(x.toLowerCase(Locale.ROOT)));
            UserLexicometricAnalysisSettings.getInstance().setRemoveTokenizationProfil(x);
        };
    }

    /**
     * Permet de se procurer le consumer pour la suppression d'un profil pour la lemmatization par catégorie grammatical
     * @return le consumer pour la suppression d'un profil pour la lemmatization par catégorie grammatical
     */
    private static Consumer<String> getRemoveProfilLemmatizationByGrammaticalCategoryConsumer() {
        return x -> {
            UserLexicometricAnalysisSettings.getInstance().getLemmatizationByGrammaticalCategorySet().removeIf(s -> s.getProfile().toLowerCase(Locale.ROOT).equals(x.toLowerCase(Locale.ROOT)));
            UserLexicometricAnalysisSettings.getInstance().setRemoveLemmatizationByGrammaticalCategoryProfil(x);
        };
    }

    /**
     * Permet de se procurer le consumer pour la création de profil
     * @return le consumer pour la création de profil
     */
    public Consumer<String> getAddProfilConsumer() {
        return addProfilConsumer;
    }

    /**
     * Permet de se procurer le consumer pour la suppression de profil
     * @return le consumer pour la suppression de profil
     */
    public Consumer<String> getRemoveProfilConsumer() {
        return removeProfilConsumer;
    }

    /**
     * Permet de se procurer le consumer pour la sauvegarde
     * @return le consumer pour la sauvegarde
     */
    public Consumer<String> getSaveInDiskConsumer() {
        return saveInDiskConsumer;
    }

    /**
     * Permet de se procurer la function de récupération de la liste des profils
     * @return la function de récupération de la liste des profils
     */
    public Function<Void, Set<String>> getAllProfils() {
        return allProfilsFunction;
    }
}
