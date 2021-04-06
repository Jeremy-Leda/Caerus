package utils;

import controler.ConfigurationControler;
import model.analyze.constants.FolderSettingsEnum;
import model.exceptions.LoadTextException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.mockStatic;

/**
 *
 * Classe permettant de faire le setup pour tous les tests
 *
 */
public class SetUpTest {

    private static final SetUpTest setUptest = new SetUpTest();
    private boolean isSetup = false;

    /**
     * Permet de se procurer la classe de setup
     * @return la classe de setup
     */
    public static SetUpTest get() {
        return setUptest;
    }

    /**
     * Permet de préparer l'environnement de test avant d'exécuter les tests
     * @return le controller
     * @throws IOException
     */
    public ConfigurationControler getConfigurationSetUp() throws IOException {
        if (isSetup) {
            return new ConfigurationControler();
        }
        mockStatic(PathUtils.class, invocationOnMock -> {
            if (invocationOnMock.getMethod().getName().equals("getCaerusFolder")) {
                return ConstantTest.caerusFolderPath;
            }
            return invocationOnMock.callRealMethod();
        });
        new ConfigurationControler();
        PathUtils.copyFile(FileProviderTest.get().getBookAnalysisConfiguration(), FileProviderTest.get().getConfigurationAnalysisFile());
        isSetup = true;
        return new ConfigurationControler();
    }

    /**
     * Permet de charger un texte et retourner le controller pour faire les vérifications
     * @param fileToLoad le fichier à charger
     * @return le controller
     */
    public ConfigurationControler getConfigurationWithTextLoaded(File fileToLoad) throws IOException, LoadTextException {
        ConfigurationControler configurationControler = getConfigurationSetUp();
        File parentFile = fileToLoad.getParentFile();
        configurationControler.setCurrentConfiguration("Livre Analyse");
        configurationControler.setAnalyzeFolder(parentFile);
        configurationControler.launchAnalyze(false);
        return configurationControler;
    }

    /**
     * Permet de charger la librairie et retourner le controller pour faire les tests
     * @param textFolder le chemin de la librairie
     * @return le controller
     */
    public ConfigurationControler getConfigurationWithLibraryLoaded(File textFolder) throws IOException, LoadTextException {
        ConfigurationControler configurationControler = getConfigurationSetUp();
        configurationControler.setCurrentConfiguration("Livre Analyse");
        configurationControler.setTextsFolder(textFolder);
        configurationControler.loadTexts();
        return configurationControler;
    }

}
