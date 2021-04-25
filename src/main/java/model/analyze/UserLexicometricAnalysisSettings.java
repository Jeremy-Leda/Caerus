package model.analyze;

import model.analyze.beans.*;
import model.analyze.lexicometric.beans.Lemmatization;
import model.analyze.lexicometric.beans.LexicometricAnalysis;
import model.analyze.constants.FolderSettingsEnum;
import model.analyze.lexicometric.beans.Tokenization;
import model.analyze.lexicometric.interfaces.ILexicometricData;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JSonFactoryUtils;

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
                        logger.debug(String.format("Loading Configuration Lexicometric %s", file.toString()));
                        LexicometricAnalysis configurationFromJsonFile = JSonFactoryUtils
                                .createAnalyseConfigurationFromJsonFile(FileUtils.openInputStream(file.toFile()));
                        lexicometricAnalysis.getLemmatizationSet().addAll(configurationFromJsonFile.getLemmatizationSet());
                        lexicometricAnalysis.getTokenizationSet().addAll(configurationFromJsonFile.getTokenizationSet());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        tokenizationSet.addAll(lexicometricAnalysis.getTokenizationSet());
        lemmatizationSet.addAll(lexicometricAnalysis.getLemmatizationSet());
    }

//    /**
//     * Permet de se procurer la configuration pour les analyses lexicométriques
//     * @return la configuration pour les analyses lexicométriques
//     */
//    public LexicometricAnalysis getLexicometricAnalysis() {
//        return lexicometricAnalysis;
//    }

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

    public void saveTokenization(String userProfile, Set<String> words) {
        Optional<Tokenization> optionalTokenization = this.lexicometricAnalysis.getTokenizationSet().stream().filter(tokenization -> tokenization.getProfile().equals(userProfile)).findFirst();
        optionalTokenization.ifPresent(tokenization -> tokenization.setWords(words));
    }

    public void saveLemmatization(String userProfile, Map<String, Set<String>> words) {
        // On vérifie qu'il n'y a pas de liste à null, sinon on les set
        Set<String> keyForValueNotInitializedSet = words.entrySet().stream().filter(entry -> Objects.isNull(entry.getValue())).map(entry -> entry.getKey()).collect(Collectors.toSet());
        keyForValueNotInitializedSet.forEach(key -> words.put(key, new HashSet<>()));
        // On sauvegarde
        Optional<Lemmatization> lemmatizationOptional = this.lexicometricAnalysis.getLemmatizationSet().stream().filter(lemmatization -> lemmatization.getProfile().equals(userProfile)).findFirst();
        lemmatizationOptional.ifPresent(lemmatization -> lemmatization.setBaseListWordsMap(words));
    }


}
