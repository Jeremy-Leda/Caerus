package model.analyze.lexicometric.beans;

import model.analyze.UserLexicometricAnalysisSettings;
import model.analyze.lexicometric.interfaces.ILexicometricConfiguration;
import model.analyze.lexicometric.interfaces.ILexicometricCopyData;
import model.analyze.lexicometric.interfaces.ILexicometricData;
import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import model.analyze.lexicometric.services.*;
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

    TOKENIZATION((profil, x) -> editData(LexicometricCleanListEnum.TOKENIZATION, profil, x),
            LexicometricEditEnum.TOKENIZATION,
            iLexicometricHierarchical -> new TokenizationHierarchicalService((ILexicometricHierarchical<TokenizationHierarchicalEditEnum>) iLexicometricHierarchical),
            LexicometricConfigurationEnum::getTokenizationConfiguration,
            getCopyConsumer(LexicometricCleanListEnum.TOKENIZATION),
            getAddProfilConsumer(LexicometricCleanListEnum.TOKENIZATION),
            getRemoveProfilConsumer(LexicometricCleanListEnum.TOKENIZATION),
            x -> UserLexicometricAnalysisSettings.getInstance().saveLexicometricConfigurationInFile(LexicometricCleanListEnum.TOKENIZATION, x),
            x -> getProfilSet(LexicometricCleanListEnum.TOKENIZATION),
            false),
    LEMMATIZATION((profil, x) -> editData(LexicometricCleanListEnum.LEMMATIZATION, profil, x),
            LexicometricEditEnum.LEMMATIZATION,
            iLexicometricHierarchical -> new LemmatizationHierarchicalService((ILexicometricHierarchical<LemmatizationHierarchicalEditEnum>) iLexicometricHierarchical),
            LexicometricConfigurationEnum::getLemmatisationConfiguration,
            getCopyConsumer(LexicometricCleanListEnum.LEMMATIZATION),
            getAddProfilConsumer(LexicometricCleanListEnum.LEMMATIZATION),
            getRemoveProfilConsumer(LexicometricCleanListEnum.LEMMATIZATION),
            x -> UserLexicometricAnalysisSettings.getInstance().saveLexicometricConfigurationInFile(LexicometricCleanListEnum.LEMMATIZATION, x),
            x -> getProfilSet(LexicometricCleanListEnum.LEMMATIZATION),
            true),
    LEMMATIZATION_BY_GRAMMATICAL_CATEGORY((profil, x) -> editData(LexicometricCleanListEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY, profil, x),
            LexicometricEditEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY,
            iLexicometricHierarchical -> new LemmatizationByGrammaticalCategoryHierarchicalService((ILexicometricHierarchical<LemmatizationByGrammaticalCategoryHierarchicalEditEnum>) iLexicometricHierarchical),
            LexicometricConfigurationEnum::getLemmatisationByGrammaticalCategoryConfiguration,
            getCopyConsumer(LexicometricCleanListEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY),
            getAddProfilConsumer(LexicometricCleanListEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY),
            getRemoveProfilConsumer(LexicometricCleanListEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY),
            x -> UserLexicometricAnalysisSettings.getInstance().saveLexicometricConfigurationInFile(LexicometricCleanListEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY, x),
            x -> getProfilSet(LexicometricCleanListEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY),
            true),
    PROPER_NOUN((profil, x) -> editData(LexicometricCleanListEnum.PROPER_NOUN, profil, x),
            LexicometricEditEnum.PROPER_NOUN,
            iLexicometricHierarchical -> new ProperNounHierarchicalService((ILexicometricHierarchical<ProperNounHierarchicalEditEnum>) iLexicometricHierarchical),
            LexicometricConfigurationEnum::getProperNounConfiguration,
            getCopyConsumer(LexicometricCleanListEnum.PROPER_NOUN),
            getAddProfilConsumer(LexicometricCleanListEnum.PROPER_NOUN),
            getRemoveProfilConsumer(LexicometricCleanListEnum.PROPER_NOUN),
            x -> UserLexicometricAnalysisSettings.getInstance().saveLexicometricConfigurationInFile(LexicometricCleanListEnum.PROPER_NOUN, x),
            x -> getProfilSet(LexicometricCleanListEnum.PROPER_NOUN),
            false),
    EXCLUDE_TEXTS((profil, x) -> editData(LexicometricCleanListEnum.EXCLUDE_TEXTS, profil, x),
            LexicometricEditEnum.EXCLUDE_TEXTS,
            iLexicometricHierarchical -> new ExcludeTextsHierarchicalService((ILexicometricHierarchical<ExcludeTextsHierarchicalEditEnum>) iLexicometricHierarchical),
            LexicometricConfigurationEnum::getExcludeTextsConfiguration,
            getCopyConsumer(LexicometricCleanListEnum.EXCLUDE_TEXTS),
            getAddProfilConsumer(LexicometricCleanListEnum.EXCLUDE_TEXTS),
            getRemoveProfilConsumer(LexicometricCleanListEnum.EXCLUDE_TEXTS),
            x -> UserLexicometricAnalysisSettings.getInstance().saveLexicometricConfigurationInFile(LexicometricCleanListEnum.EXCLUDE_TEXTS, x),
            x -> getProfilSet(LexicometricCleanListEnum.EXCLUDE_TEXTS),
            false);

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
    private final Boolean isLemmatization;

    /**
     * Constructeur
     * @param editTableElementBiConsumer Le consumer pour éditer les données serveur
     * @param lexicometricEditEnum Enumeration provenant de l'ihm
     * @param lexicometricHierarchicalViewToLexicometricHierarchicalServer fonction pour la conversion de type
     * @param lexicometricHierarchicalILexicometricConfigurationFunction la configuration lexicometric
     * @param copyConsumer Consumer pour la copy
     * @param addProfilConsumer consumer pour l'ajout de profil
     * @param removeProfilConsumer consumer pour la suppression de profil
     * @param saveInDiskConsumer consumer pour la sauvegarde sur le disque
     * @param isLemmatization Vrai si c'est une liste de lemmatization
     */
    LexicometricConfigurationEnum(BiConsumer<String, EditTableElement> editTableElementBiConsumer, LexicometricEditEnum lexicometricEditEnum, Function<ILexicometricHierarchical<?>, ILexicometricHierarchical<?>> lexicometricHierarchicalViewToLexicometricHierarchicalServer, Function<ILexicometricHierarchical<?>, ILexicometricConfiguration> lexicometricHierarchicalILexicometricConfigurationFunction, BiConsumer<String, String> copyConsumer, Consumer<String> addProfilConsumer, Consumer<String> removeProfilConsumer, Consumer<String> saveInDiskConsumer, Function<Void, Set<String>> allProfilsFunction, Boolean isLemmatization) {
        this.editTableElementBiConsumer = editTableElementBiConsumer;
        this.lexicometricEditEnum = lexicometricEditEnum;
        this.lexicometricHierarchicalViewToLexicometricHierarchicalServer = lexicometricHierarchicalViewToLexicometricHierarchicalServer;
        this.lexicometricHierarchicalILexicometricConfigurationFunction = lexicometricHierarchicalILexicometricConfigurationFunction;
        this.copyConsumer = copyConsumer;
        this.addProfilConsumer = addProfilConsumer;
        this.removeProfilConsumer = removeProfilConsumer;
        this.saveInDiskConsumer = saveInDiskConsumer;
        this.allProfilsFunction = allProfilsFunction;
        this.isLemmatization = isLemmatization;
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
     * Permet de se procurer les datas si elle existe
     * @param typeList type de liste
     * @param profil profil de recherche
     * @return les datas si elle existe
     */
    private static Optional<ILexicometricData> getData(LexicometricCleanListEnum typeList, String profil) {
        return UserLexicometricAnalysisSettings.getInstance().getDataSet(typeList).stream().filter(x -> x.getProfile().equals(profil)).findFirst();
    }

    /**
     * Permet de se procurer la liste des profils
     * @param typeList Type de liste
     * @return la liste des profils
     */
    private static Set<String> getProfilSet(LexicometricCleanListEnum typeList) {
        return UserLexicometricAnalysisSettings.getInstance().getDataSet(typeList).stream().map(ILexicometricData::getProfile).collect(Collectors.toSet());
    }

    /**
     * Permet d'éditer les données serveur
     * @param typeList type de liste
     * @param profil profil de recherche
     * @param editTableElement données nécessaire à l'édition
     */
    private static void editData(LexicometricCleanListEnum typeList, String profil, EditTableElement editTableElement) {
        Optional<ILexicometricData> optionalILexicometricData = getData(typeList, profil);
        optionalILexicometricData.ifPresent(iLexicometricData -> {
            Object data = iLexicometricData.getData();
            Object result = editTableElement.getActionEditTableEnum().getApplyFunction().apply(lexicometricEditTableService, editTableElement, data);
            UserLexicometricAnalysisSettings.getInstance().saveDataWithProfil(typeList, profil, result);
        });
    }

    /**
     * Permet de se procurer un consumer de copie
     * @param typeList Type de liste
     * @return le consumer de copie
     */
    private static BiConsumer<String, String> getCopyConsumer(LexicometricCleanListEnum typeList) {
        return (x,v) -> {
            Optional<ILexicometricData> lemmatizationDataOrigin = getData(typeList, x);
            Optional<ILexicometricData> lemmatizationDataDest = getData(typeList, v);
            if (lemmatizationDataOrigin.isPresent() && lemmatizationDataDest.isPresent()) {
                copyLexicometricData((ILexicometricCopyData) lemmatizationDataOrigin.get(), (ILexicometricCopyData) lemmatizationDataDest.get());
            }
        };
    }

    /**
     * Permet de se procurer le consumer pour la création de profil pour la lemmatization
     * @return le consumer pour la création de profil pour la lemmatization
     */
    private static Consumer<String> getAddProfilConsumer(LexicometricCleanListEnum typeList) {
        return x -> {
            if (StringUtils.isBlank(x)) {
                return;
            }
            boolean exist = UserLexicometricAnalysisSettings.getInstance().getDataSet(typeList).stream().anyMatch(s -> s.getProfile().toLowerCase(Locale.ROOT).equals(x.toLowerCase(Locale.ROOT)));
            if (exist) {
                throw new ServerException().addInformationException(new InformationExceptionBuilder()
                        .errorCode(ErrorCode.VALUE_EXIST)
                        .objectInError(x)
                        .build());
            }
            ILexicometricData lexicometricData = typeList.getNewInstanceProfilFunction().apply(x);
            UserLexicometricAnalysisSettings.getInstance().getDataSet(typeList).add(lexicometricData);
        };
    }

    /**
     * Permet de se procurer le consumer pour la suppression d'un profil
     * @param typeList Type de liste
     * @return le consumer pour la suppression d'un profil
     */
    private static Consumer<String> getRemoveProfilConsumer(LexicometricCleanListEnum typeList) {
        return x -> {
            UserLexicometricAnalysisSettings.getInstance().getDataSet(typeList).removeIf(s -> s.getProfile().toLowerCase(Locale.ROOT).equals(x.toLowerCase(Locale.ROOT)));
            UserLexicometricAnalysisSettings.getInstance().setRemoveProfil(typeList, x);
        };
    }

    /**
     * Permet de déterminer les tables hiérarchiques en provenance de l'IHM
     */
    public void setHierarchicalTableSet(Set<IRootTable> hierarchicalTableSet) {
        LexicometricConfigurationEnum.hierarchicalTableSet = hierarchicalTableSet;
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
     * Permet de se procurer la configuration pour les noms propres
     * @param lexicometricHierarchical interface contenant les informations des datas
     * @return La configuration pour les noms propres
     */
    private static ILexicometricConfiguration getProperNounConfiguration(ILexicometricHierarchical<?> lexicometricHierarchical) {
        return new ProperNounConfiguration((ILexicometricHierarchical<ProperNounHierarchicalEnum>) lexicometricHierarchical, hierarchicalTableSet);
    }

    /**
     * Permet de se procurer la configuration pour les textes à exclure
     * @param lexicometricHierarchical interface contenant les informations des datas
     * @return La configuration pour les textes à exclure
     */
    private static ILexicometricConfiguration getExcludeTextsConfiguration(ILexicometricHierarchical<?> lexicometricHierarchical) {
        return new ExcludeTextsConfiguration((ILexicometricHierarchical<ExcludeTextsHierarchicalEditEnum>) lexicometricHierarchical, hierarchicalTableSet);
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

    /**
     * Permet de savoir si c'est de la lemmatization
     * @return Vrai si c'est de la lemmatization
     */
    public Boolean isLemmatization() {
        return isLemmatization;
    }

}
