package model.analyze;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import model.analyze.beans.CurrentUserConfiguration;
import model.analyze.constants.FolderSettingsEnum;
import model.analyze.lexicometric.beans.*;
import model.analyze.lexicometric.beans.LexicometricAnalysis;
import model.analyze.lexicometric.interfaces.ILexicometricCopyData;
import model.analyze.lexicometric.interfaces.ILexicometricData;
import model.exceptions.ErrorCode;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JSonFactoryUtils;
import utils.PathUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Permet de gérer les réglages et les instances en cours de l'utilisateur concernant les analyses lexicométrique
 *
 */
public class UserLexicometricAnalysisSettings {

    private static final Logger logger = LoggerFactory.getLogger(UserLexicometricAnalysisSettings.class);
    private static UserLexicometricAnalysisSettings _instance;
    private final Map<LexicometricCleanListEnum, UserLexicometricCleanListData> dataMap = new HashMap<>();
    private String userProfile;


    /**
     * Permet de se procurer l'instance statique
     *
     * @return l'instance statique
     */
    public static UserLexicometricAnalysisSettings getInstance() {
        if (null == _instance) {
            _instance = new UserLexicometricAnalysisSettings();
        }
        return _instance;
    }

    /**
     * Permet de restaurer l'environnement de la configuration de l'utilisateur
     *
     * @param save la configuration de l'utilisateur a restaurer
     */
    public void restoreUserConfiguration(CurrentUserConfiguration save) throws IOException {
        loadConfigurationsList();
        Optional<String> defaultLexicometricAnalysisConfiguration = Optional.ofNullable(save.getDefaultLexicometricAnalysisConfiguration());
        if (defaultLexicometricAnalysisConfiguration.isPresent()) {
            userProfile = defaultLexicometricAnalysisConfiguration.get();
            return;
        }
        Optional<String> optionalProfile = dataMap
                .values()
                .stream()
                .flatMap(s -> s.getDataSet().stream())
                .map(ILexicometricData::getProfile)
                .findFirst();
        optionalProfile.ifPresent(s -> userProfile = s);
    }

    /**
     * Permet de charger la liste des configurations
     *
     * @throws IOException erreur d'entrée sortie
     */
    private void loadConfigurationsList() throws IOException {
        Optional<File> configurationFolder = UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_CONFIGURATIONS_LEXICOMETRIC_ANALYSIS);
        dataMap.clear();
        dataMap.put(LexicometricCleanListEnum.TOKENIZATION, new UserLexicometricCleanListData());
        dataMap.put(LexicometricCleanListEnum.LEMMATIZATION, new UserLexicometricCleanListData());
        dataMap.put(LexicometricCleanListEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY, new UserLexicometricCleanListData());
        dataMap.put(LexicometricCleanListEnum.PROPER_NOUN, new UserLexicometricCleanListData());
        if (configurationFolder.isPresent() && configurationFolder.get().exists()) {
            Files.walkFileTree(Paths.get(configurationFolder.get().toURI()), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!Files.isDirectory(file)) {
                        logger.debug(String.format("Loading Configuration Lexicometric %s", file));
                        LexicometricAnalysis configurationFromJsonFile = JSonFactoryUtils
                                .createAnalyseConfigurationFromJsonFile(FileUtils.openInputStream(file.toFile()));
                        dataMap.get(LexicometricCleanListEnum.TOKENIZATION).getDataSet().addAll(configurationFromJsonFile.getTokenizationSet());
                        dataMap.get(LexicometricCleanListEnum.LEMMATIZATION).getDataSet().addAll(configurationFromJsonFile.getLemmatizationSet());
                        dataMap.get(LexicometricCleanListEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY).getDataSet().addAll(configurationFromJsonFile.getLemmatizationByGrammaticalCategorySet());
                        dataMap.get(LexicometricCleanListEnum.PROPER_NOUN).getDataSet().addAll(configurationFromJsonFile.getProperNounSet());
                        configurationFromJsonFile.getLemmatizationSet().stream().map(Lemmatization::getProfile).distinct().forEach(p -> dataMap.get(LexicometricCleanListEnum.LEMMATIZATION).getProfilFileMap().put(p, file));
                        configurationFromJsonFile.getTokenizationSet().stream().map(Tokenization::getProfile).distinct().forEach(p -> dataMap.get(LexicometricCleanListEnum.TOKENIZATION).getProfilFileMap().put(p, file));
                        configurationFromJsonFile.getLemmatizationByGrammaticalCategorySet().stream().map(LemmatizationByGrammaticalCategory::getProfile).distinct().forEach(p -> dataMap.get(LexicometricCleanListEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY).getProfilFileMap().put(p, file));
                        configurationFromJsonFile.getProperNounSet().stream().map(ProperNoun::getProfile).distinct().forEach(p -> dataMap.get(LexicometricCleanListEnum.PROPER_NOUN).getProfilFileMap().put(p, file));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     * Permet de se procurer le profile par défaut
     * @return Le profile par défaut
     */
    public String getUserProfile() {
        return userProfile;
    }

    /**
     * Permet de se procurer un data set
     * @param lexicometricCleanListEnum type du data set recherché
     * @return le data set
     */
    public Set<ILexicometricData> getDataSet(LexicometricCleanListEnum lexicometricCleanListEnum) {
        return dataMap.get(lexicometricCleanListEnum).getDataSet();
    }

    /**
     * Permet de sauvegarder les éléments en mémoire
     * @param typeList Type de liste
     * @param userProfile profil utilisateur
     * @param data liste des données à sauvegarder
     */
    public void saveDataWithProfil(LexicometricCleanListEnum typeList, String userProfile, Object data) {
        Object cleanData = typeList.getCleanNullableFunction().apply(data);
        Optional<ILexicometricData> dataOptional = getDataSet(typeList).stream().filter(d -> d.getProfile().equals(userProfile)).findFirst();
        dataOptional.ifPresent(d -> ((ILexicometricCopyData)d).setData(cleanData));
    }

    /**
     * Permet de sauvegarder la données lexicométrique sur le disque
     * @param lexicometricCleanListEnum type de données lexicométriques
     * @param profilToSave profil à sauvegarder
     */
    public void saveLexicometricConfigurationInFile(LexicometricCleanListEnum lexicometricCleanListEnum, String profilToSave) {
        Optional<Tuple2<String, Path>> removeProfilFile = this.dataMap.get(lexicometricCleanListEnum).getRemoveProfilFile();
        if (removeProfilFile.isPresent() && removeProfilFile.get()._1().equals(profilToSave)) {
            Path path = removeProfilFile.get()._2();
            LexicometricAnalysis lexicometricAnalysis = constructLexicometricAnalysisForSave(path);
            saveConfigurationInFile(lexicometricAnalysis, path);
            this.dataMap.get(lexicometricCleanListEnum).setRemoveProfilFile(Optional.empty());
            return;
        }
        Map<String, Path> profilFileMap = this.dataMap.get(lexicometricCleanListEnum).getProfilFileMap();
        if (!saveConfigurationInFileIfUpdate(profilFileMap, profilToSave)) {
            Set<ILexicometricData> dataSet = getDataSet(lexicometricCleanListEnum);
            LexicometricAnalysis lexicometricAnalysis = new LexicometricAnalysis();
            Optional<ILexicometricData> optionalILexicometricData = dataSet.stream().filter(t -> t.getProfile().equals(profilToSave)).findFirst();
            optionalILexicometricData.ifPresent(t -> lexicometricCleanListEnum.getDataSetBiConsumer().accept(lexicometricAnalysis, t));
            Optional<File> configurationFolder = UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_CONFIGURATIONS_LEXICOMETRIC_ANALYSIS);
            configurationFolder.ifPresent(f -> {
                String nameFile = lexicometricCleanListEnum.getNameFileFunction().apply(profilToSave);
                Path path = new File(f, nameFile).toPath();
                profilFileMap.put(profilToSave, path);
                saveConfigurationInFile(lexicometricAnalysis, path);
            });
        }
    }

    /**
     * Permet de sauvegarder la configuration dans le cadre d'une mise à jour
     * @param stringPathMap map contenant les chemin d'accés au fichier
     * @param profilToSave profil pour la sauvegarde
     * @return Vrai si Ok
     */
    private Boolean saveConfigurationInFileIfUpdate(Map<String, Path> stringPathMap, String profilToSave) {
        if (stringPathMap.containsKey(profilToSave)) {
            Path path = stringPathMap.get(profilToSave);
            LexicometricAnalysis lexicometricAnalysis = constructLexicometricAnalysisForSave(path);
            saveConfigurationInFile(lexicometricAnalysis, path);
            return true;
        }
        return false;
    }

    /**
     * Permet de se procurer le lexicometric analysis à sauvegarder
     * @param file fichier pour la sauvegarde
     * @return le lexicometric analysis à sauvegarder
     */
    private LexicometricAnalysis constructLexicometricAnalysisForSave(Path file) {
        LexicometricAnalysis lexicometricAnalysis = new LexicometricAnalysis();
        Arrays.asList(LexicometricCleanListEnum.values()).forEach(v -> setLexicometricDataForSave(v, file, lexicometricAnalysis));
        return lexicometricAnalysis;
    }

    /**
     * Permet de sauvegarder la liste dans l'objet de lexicométrique analyse.
     * @param lexicometricCleanListEnum type de liste
     * @param file fichier pour la sauvegarde
     * @param lexicometricAnalysis l'objet de lexicométrique analyse.
     */
    private void setLexicometricDataForSave(LexicometricCleanListEnum lexicometricCleanListEnum, Path file, LexicometricAnalysis lexicometricAnalysis) {
        Set<ILexicometricData> dataSet = getDataSet(lexicometricCleanListEnum);
        Map<String, Path> profilFileMap = dataMap.get(lexicometricCleanListEnum).getProfilFileMap();
        Set<String> profilSet = profilFileMap.entrySet().stream().filter(entry -> entry.getValue().equals(file)).map(Map.Entry::getKey).collect(Collectors.toSet());
        Set<?> dataSetProfilToSave = dataSet.stream().filter(f -> profilSet.contains(f.getProfile())).map(t -> lexicometricCleanListEnum.getType().cast(t)).collect(Collectors.toSet());
        lexicometricCleanListEnum.getAllDataSetBiConsumer().accept(lexicometricAnalysis, dataSetProfilToSave);
    }

    /**
     * Permet de sauvegarder le lexicométrique analysis dans un fichier
     * @param lexicometricAnalysis le lexicométrique analysis
     * @param file le fichier
     */
    private void saveConfigurationInFile(LexicometricAnalysis lexicometricAnalysis, Path file) {
        try {
            if (lexicometricAnalysis.isEmpty() && file.toFile().exists()) {
                PathUtils.deleteFile(file.toFile());
                return;
            }
            if (!JSonFactoryUtils.createJsonInFile(lexicometricAnalysis, file.toFile())) {
                throw new ServerException().addInformationException(new InformationExceptionBuilder()
                        .errorCode(ErrorCode.TECHNICAL_ERROR)
                        .objectInError(Set.of(lexicometricAnalysis, file))
                        .build());
            }
        } catch (IOException e) {
            throw new ServerException().addInformationException(new InformationExceptionBuilder()
                    .errorCode(ErrorCode.TECHNICAL_ERROR)
                    .objectInError(e)
                    .stackTraceElements(e.getStackTrace())
                    .build());
        }
    }

    /**
     * Permet de définir le profil à supprimer
     * @param lexicometricCleanListEnum type de liste
     * @param profilToRemove le profil à supprimer
     */
    public void setRemoveProfil(LexicometricCleanListEnum lexicometricCleanListEnum, String profilToRemove) {
        UserLexicometricCleanListData userData = this.dataMap.get(lexicometricCleanListEnum);
        Path path = userData.getProfilFileMap().get(profilToRemove);
        userData.setRemoveProfilFile(Optional.of(Tuple.of(profilToRemove, path)));
    }

}
