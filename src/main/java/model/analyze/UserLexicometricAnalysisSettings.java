package model.analyze;

import model.analyze.beans.*;
import model.analyze.constants.FolderSettingsEnum;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JSonFactoryUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 *
 * Permet de gérer les réglages et les instances en cours de l'utilisateur concernant les analyses lexicométrique
 *
 */
public class UserLexicometricAnalysisSettings {

    private static final Logger logger = LoggerFactory.getLogger(UserLexicometricAnalysisSettings.class);
    private static UserLexicometricAnalysisSettings _instance;
    private final LexicometricAnalysis lexicometricAnalysis = new LexicometricAnalysis();
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
    }

    /**
     * Permet de se procurer la configuration pour les analyses lexicométriques
     * @return la configuration pour les analyses lexicométriques
     */
    public LexicometricAnalysis getLexicometricAnalysis() {
        return lexicometricAnalysis;
    }

    /**
     * Permet de se procurer le profile par défaut
     * @return Le profile par défaut
     */
    public String getUserProfile() {
        return userProfile;
    }



}
