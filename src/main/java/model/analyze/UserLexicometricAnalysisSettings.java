package model.analyze;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import model.analyze.beans.*;
import model.analyze.lexicometric.beans.Lemmatization;
import model.analyze.lexicometric.beans.LexicometricAnalysis;
import model.analyze.constants.FolderSettingsEnum;
import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;
import model.analyze.lexicometric.beans.Tokenization;
import model.analyze.lexicometric.interfaces.ILexicometricCopyData;
import model.analyze.lexicometric.interfaces.ILexicometricData;
import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
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
    private final LexicometricAnalysis lexicometricAnalysis = new LexicometricAnalysis();
    private final Set<ILexicometricData<Set<String>>> tokenizationSet = new HashSet<>();
    private final Set<ILexicometricData<Map<String, Set<String>>>> lemmatizationSet = new HashSet<>();
    private final Map<String, Path> tokenizationFileMap = new HashMap<>();
    private final Map<String, Path> lemmatizationFileMap = new HashMap<>();
    private Optional<Tuple2<String, Path>> removeTokenizationProfil = Optional.empty();
    private Optional<Tuple2<String, Path>> removeLemmatizationProfil = Optional.empty();
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
        if (!lexicometricAnalysis.getTokenizationSet().isEmpty()) {
            userProfile = lexicometricAnalysis.getTokenizationSet().iterator().next().getProfile();
        } else if (!lexicometricAnalysis.getLemmatizationSet().isEmpty()) {
            userProfile = lexicometricAnalysis.getLemmatizationSet().iterator().next().getProfile();
        }
    }

    /**
     * Permet de charger la liste des configurations
     *
     * @throws IOException erreur d'entrée sortie
     */
    private void loadConfigurationsList() throws IOException {
        Optional<File> configurationFolder = UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_CONFIGURATIONS_LEXICOMETRIC_ANALYSIS);
        lexicometricAnalysis.setLemmatizationSet(new HashSet<>());
        lexicometricAnalysis.setTokenizationSet(new HashSet<>());
        if (configurationFolder.isPresent() && configurationFolder.get().exists()) {
            Files.walkFileTree(Paths.get(configurationFolder.get().toURI()), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!Files.isDirectory(file)) {
                        logger.debug(String.format("Loading Configuration Lexicometric %s", file));
                        LexicometricAnalysis configurationFromJsonFile = JSonFactoryUtils
                                .createAnalyseConfigurationFromJsonFile(FileUtils.openInputStream(file.toFile()));
                        lexicometricAnalysis.getLemmatizationSet().addAll(configurationFromJsonFile.getLemmatizationSet());
                        lexicometricAnalysis.getTokenizationSet().addAll(configurationFromJsonFile.getTokenizationSet());
                        configurationFromJsonFile.getLemmatizationSet().stream().map(p -> p.getProfile()).distinct().forEach(p -> lemmatizationFileMap.put(p, file));
                        configurationFromJsonFile.getTokenizationSet().stream().map(p -> p.getProfile()).distinct().forEach(p -> tokenizationFileMap.put(p, file));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        tokenizationSet.addAll(lexicometricAnalysis.getTokenizationSet());
        lemmatizationSet.addAll(lexicometricAnalysis.getLemmatizationSet());
    }

    /**
     * Permet de se procurer le profile par défaut
     * @return Le profile par défaut
     */
    public String getUserProfile() {
        return userProfile;
    }

    /**
     * Permet de se procurer la liste des tokenization disponibles
     * @return la liste des tokenization disponibles
     */
    public Set<ILexicometricData<Set<String>>> getTokenizationSet() {
        return this.tokenizationSet;
    }

    /**
     * Permet de se procurer la liste des lemmatisations disponibles
     * @return la liste des lemmatisations disponibles
     */
    public Set<ILexicometricData<Map<String, Set<String>>>> getLemmatizationSet() {
        return this.lemmatizationSet;
    }

    /**
     * Permet de sauvegarder les éléments de tokenization en mémoire
     * @param userProfile profil utilisateur
     * @param words liste des mots à sauvegarder
     */
    public void saveTokenization(String userProfile, Set<String> words) {
        Optional<ILexicometricData<Set<String>>> optionalTokenization = this.tokenizationSet.stream().filter(tokenization -> tokenization.getProfile().equals(userProfile)).findFirst();
        optionalTokenization.ifPresent(tokenization -> ((Tokenization)tokenization).setData(words));
    }

    /**
     * Permet de sauvegarder les éléments de lemmatisation en mémoire
     * @param userProfile profil utilisateur
     * @param words map des mots à sauvegarder
     */
    public void saveLemmatization(String userProfile, Map<String, Set<String>> words) {
        // On vérifie qu'il n'y a pas de liste à null, sinon on les set
        Set<String> keyForValueNotInitializedSet = words.entrySet().stream().filter(entry -> Objects.isNull(entry.getValue())).map(entry -> entry.getKey()).collect(Collectors.toSet());
        keyForValueNotInitializedSet.forEach(key -> words.put(key, new HashSet<>()));
        // On sauvegarde
        Optional<ILexicometricData<Map<String, Set<String>>>> lemmatizationOptional = this.lemmatizationSet.stream().filter(lemmatization -> lemmatization.getProfile().equals(userProfile)).findFirst();
        lemmatizationOptional.ifPresent(lemmatization -> ((Lemmatization)lemmatization).setData(words));
    }

    /**
     * Permet de sauvegarder la tokenization sur le disque
     * @param profilToSave profil à sauvegarder
     */
    public void saveTokenizationConfigurationInFile(String profilToSave) {
        if (this.removeTokenizationProfil.isPresent() && this.removeTokenizationProfil.get()._1().equals(profilToSave)) {
            Path path = this.removeTokenizationProfil.get()._2();
            LexicometricAnalysis lexicometricAnalysis = constructLexicometricAnalysisForSave(path);
            saveConfigurationInFile(lexicometricAnalysis, path);
            this.removeTokenizationProfil = Optional.empty();
            return;
        }
        if (!saveConfigurationInFileIfUpdate(this.tokenizationFileMap, profilToSave)) {
            LexicometricAnalysis lexicometricAnalysis = new LexicometricAnalysis();
            Optional<ILexicometricData<Set<String>>> tokenization = this.tokenizationSet.stream().filter(t -> t.getProfile().equals(profilToSave)).findFirst();
            tokenization.ifPresent(t -> lexicometricAnalysis.setTokenizationSet(Set.of((Tokenization) t)));
            lexicometricAnalysis.setLemmatizationSet(new HashSet<>());
            Optional<File> configurationFolder = UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_CONFIGURATIONS_LEXICOMETRIC_ANALYSIS);
            configurationFolder.ifPresent(f -> {
                Path path = new File(f, "stopwords_" + profilToSave + ".json").toPath();
                this.tokenizationFileMap.put(profilToSave, path);
                saveConfigurationInFile(lexicometricAnalysis, path);
            });
        }
    }

    /**
     * Permet de sauvegarder la lemmatization sur le disque
     * @param profilToSave profil à sauvegarder
     */
    public void saveLemmatizationConfigurationInFile(String profilToSave) {
        if (this.removeLemmatizationProfil.isPresent() && this.removeLemmatizationProfil.get()._1().equals(profilToSave)) {
            Path path = this.removeLemmatizationProfil.get()._2();
            LexicometricAnalysis lexicometricAnalysis = constructLexicometricAnalysisForSave(path);
            saveConfigurationInFile(lexicometricAnalysis, path);
            this.removeLemmatizationProfil = Optional.empty();
            return;
        }
        if (!saveConfigurationInFileIfUpdate(this.lemmatizationFileMap, profilToSave)) {
            LexicometricAnalysis lexicometricAnalysis = new LexicometricAnalysis();
            Optional<ILexicometricData<Map<String, Set<String>>>> lemmatization = this.lemmatizationSet.stream().filter(t -> t.getProfile().equals(profilToSave)).findFirst();
            lemmatization.ifPresent(t -> lexicometricAnalysis.setLemmatizationSet(Set.of((Lemmatization) t)));
            lexicometricAnalysis.setTokenizationSet(new HashSet<>());
            Optional<File> configurationFolder = UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_CONFIGURATIONS_LEXICOMETRIC_ANALYSIS);
            configurationFolder.ifPresent(f -> {
                Path path = new File(f, "lemmatization_" + profilToSave + ".json").toPath();
                this.lemmatizationFileMap.put(profilToSave, path);
                saveConfigurationInFile(lexicometricAnalysis, path);
            });
        }
    }

    /**
     * Permet de sauvegarder la configuration dans le cadre d'une mise à jour ou
     * @param stringPathMap
     * @param profilToSave
     * @return
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
        Set<String> tokenizationProfileSet = tokenizationFileMap.entrySet().stream().filter(entry -> entry.getValue().equals(file)).map(entry -> entry.getKey()).collect(Collectors.toSet());
        Set<Tokenization> tokenizationSet = this.tokenizationSet.stream().filter(f -> tokenizationProfileSet.contains(f.getProfile())).map(t -> (Tokenization) t).collect(Collectors.toSet());
        lexicometricAnalysis.setTokenizationSet(tokenizationSet);
        Set<String> lemmatizationProfileSet = lemmatizationFileMap.entrySet().stream().filter(entry -> entry.getValue().equals(file)).map(entry -> entry.getKey()).collect(Collectors.toSet());
        Set<Lemmatization> lemmatizationSet = this.lemmatizationSet.stream().filter(f -> lemmatizationProfileSet.contains(f.getProfile())).map(t -> (Lemmatization) t).collect(Collectors.toSet());
        lexicometricAnalysis.setLemmatizationSet(lemmatizationSet);
        return lexicometricAnalysis;
    }

    /**
     * Permet de sauvegarder le lexicométrique analysis dans un fichier
     * @param lexicometricAnalysis le lexicométrique analysis
     * @param file le fichier
     */
    private void saveConfigurationInFile(LexicometricAnalysis lexicometricAnalysis, Path file) {
        try {
            if (lexicometricAnalysis.getTokenizationSet().isEmpty() && lexicometricAnalysis.getLemmatizationSet().isEmpty()) {
                if (file.toFile().exists()) {
                    PathUtils.deleteFile(file.toFile());
                    return;
                }
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
     * Permet de définir le profil à supprimer de la tokenization
     * @param profilToRemove le profil à supprimer de la tokenization
     */
    public void setRemoveTokenizationProfil(String profilToRemove) {
        Path path = this.tokenizationFileMap.get(profilToRemove);
        this.removeTokenizationProfil = Optional.of(Tuple.of(profilToRemove, path));
    }

    /**
     * Permet de définir le profil à supprimer de la lemmatization
     * @param profilToRemove profil à supprimer
     */
    public void setRemoveLemmatizationProfil(String profilToRemove) {
        Path path = this.lemmatizationFileMap.get(profilToRemove);
        this.removeLemmatizationProfil = Optional.of(Tuple.of(profilToRemove, path));
    }
}
