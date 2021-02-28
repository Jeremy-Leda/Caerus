import controler.ConfigurationControler;
import model.analyze.UserSettings;
import model.analyze.constants.FolderSettingsEnum;
import model.exceptions.LoadTextException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ConstantTest;
import utils.FileProviderTest;
import utils.PathUtils;
import utils.SetUpTest;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * Permet de vérifier le chargement des textes
 *
 */
@ExtendWith(MockitoExtension.class)
public class LoadingTextTest {

    private static ConfigurationControler configurationControler;

    /**
     * Permet d'initialiser l'application pour les tests
     * @throws IOException Exception au niveau de l'écriture des fichiers
     */
    @BeforeAll
    public static void beforeAllTests() throws IOException, LoadTextException {
        configurationControler = SetUpTest.get().getConfigurationWithTextLoaded(FileProviderTest.get().getClassicalTextFile());
    }

    /**
     * Permet de vérifier le résultat sur les nombres de lignes en erreurs
     */
    @Test
    public void checkGetNbBlankLinesError() {
       int nbBlankLinesError = configurationControler.getNbBlankLinesError();
       assertEquals(0, nbBlankLinesError);
    }






}
