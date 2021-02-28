import controler.ConfigurationControler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.FileProviderTest;
import utils.SetUpTest;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.ConstantTest.configurationTestName;

/**
 *
 * Permet de tester les paramètres gérés par l'utilisateur
 *
 */
@ExtendWith(MockitoExtension.class)
public class UserConfigurationTest {

    private static ConfigurationControler configurationControler;

    /**
     * Permet d'initialiser l'application pour les tests
     * @throws IOException Exception au niveau de l'écriture des fichiers
     */
    @BeforeAll
    public static void beforeAllTests() throws IOException {
        configurationControler = SetUpTest.get().getConfigurationSetUp();
    }

    /**
     * Permet de vérifier que la définition du dossier d'analyse est ok
     */
    @Test
    public void setFolderAnalyzeTest() {
        File file = FileProviderTest.get().getClassicalTextFile().getParentFile();
        configurationControler.setAnalyzeFolder(file);
        assertEquals(file, configurationControler.getAnalyzeFolder());
    }

    /**
     * Permet de vérifier que le changement de la configuration est bien opérante
     */
    @Test
    public void setCurrentConfigurationTest() {
        configurationControler.setCurrentConfiguration(configurationTestName);
        assertEquals(configurationTestName, configurationControler.getConfigurationName());
    }

    /**
     * Permet de vérifier que la définition du dossier de la bibliothéque est ok
     */
    @Test
    public void setFolderTextsTest() {
        File file = FileProviderTest.get().getClassicalTextFile().getParentFile();
        configurationControler.setTextsFolder(file);
        assertEquals(file, configurationControler.getTextsFolder());
    }

}
