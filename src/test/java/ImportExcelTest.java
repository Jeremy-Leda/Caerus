import controler.ConfigurationControler;
import model.analyze.UserSettings;
import model.analyze.beans.Configuration;
import model.analyze.beans.UserStructuredText;
import model.analyze.constants.FolderSettingsEnum;
import model.excel.beans.ExcelImportConfigurationCmd;
import model.exceptions.ImportExcelException;
import model.exceptions.LoadTextException;
import model.exceptions.ServerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.FileProviderTest;
import utils.PathUtils;
import utils.SetUpTest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * Permet de vérifier le système des imports excel pour la mise à jour
 *
 */
@ExtendWith(MockitoExtension.class)
public class ImportExcelTest {

    private static ConfigurationControler configurationControler;
    private static Configuration configuration;
    private static final String sheetName = "Textos";


    private static final String GENRE_LIVRE = "Thriller/Drame";

    private static final String NOUVEAU_TITRE_1 = "Mon beau sapin";
    private static final String NOUVEAU_RESUME_1 = "Il est le plus beau dans toute la forêt";
    private static final String CATEGORIE_1 = "Temps,Bizarre,Idee,Temps";
    private static final String CLE_1 = "Nuage,Impossible,Important,Pluie";
    private static final String NB_OCCURENCE_1 = "5,9,2,3";

    private static final String NOUVEAU_TITRE_2 = "Le père noël";
    private static final String NOUVEAU_RESUME_2 = "Il a apporté tous les cadeaux que j'attendais";
    private static final String CATEGORIE_2 = "Paysage,Paysage,Temps,Idee";
    private static final String CLE_2 = "Plage,Montagne,Éclaircie,Résultat";
    private static final String NB_OCCURENCE_2 = "1,2,3,4";

    private static final String ERROR = "Une erreur doit survenir";
    private static final String NO_ERROR = "Aucune erreur ne doit survenir";

    /**
     * Permet d'initialiser l'application pour les tests
     * @throws IOException Exception au niveau de l'écriture des fichiers
     */
    @BeforeAll
    public static void beforeAllTests() throws IOException, LoadTextException {
        PathUtils.copyFile(FileProviderTest.get().getClassicalTextFile(), FileProviderTest.get().getImportExcelFileTextLibrary());
        configurationControler = SetUpTest.get().getConfigurationWithLibraryLoaded(FileProviderTest.get().getImportExcelLibraryFile());
        configuration = UserSettings.getInstance().getConfigurationList().stream().filter(configuration1 -> configuration1.getName().equals("Livre Analyse")).findFirst().get();
    }

    /**
     * Permet de vérifier qu'une erreur est levé si le fichier donné ne correspond pas à la commande
     * => cas où on déclare une configuration spécifique alors que le fichier n'est pas dans le format de la configuration spécifique
     */
    @Test
    public void testImportExcelWithSpecificConfiguration_WithFileKO() {
        ExcelImportConfigurationCmd excelImportConfigurationCmd = new ExcelImportConfigurationCmd(FileProviderTest.get().getImportExcelExcelWithSpecificConfigurationFileKO(), sheetName);
        excelImportConfigurationCmd.setIsSpecificImport(true);
        excelImportConfigurationCmd.setLabelSpecificChoose("Analyse des mot clés");
        excelImportConfigurationCmd.addFieldToImport("[CATEGORIE_MOT_CLE]");
        excelImportConfigurationCmd.addFieldToImport("[MOT_CLE]");
        excelImportConfigurationCmd.addFieldToImport("[NB_OCCURENCE]");

        try {
            configurationControler.importExcel(excelImportConfigurationCmd);
            fail(ERROR);
        } catch (ServerException | ImportExcelException | IOException | LoadTextException e) {
            assertTrue(!e.getMessage().isBlank());
        }

    }

    /**
     * Permet de vérifier qu'une erreur est levé si le fichier donné n'est pas dans le bon format
     * => cas où le fichier ne détient pas d'en tête
     */
    @Test
    public void testImportExcelWithFileWithoutHeaderKO() {
        ExcelImportConfigurationCmd excelImportConfigurationCmd = new ExcelImportConfigurationCmd(FileProviderTest.get().getImportExcelExcelWithoutHeaderKO(), sheetName);
        excelImportConfigurationCmd.addFieldToImport("[TITRE_CHAPITRE]");
        excelImportConfigurationCmd.addFieldToImport("[RESUME_CHAPITRE]");

        try {
            configurationControler.importExcel(excelImportConfigurationCmd);
            fail(ERROR);
        } catch (ServerException | ImportExcelException | IOException | LoadTextException e) {
            assertTrue(!e.getMessage().isBlank());
        }

    }

    /**
     * Permet de vérifier qu'une erreur est levé si le fichier donné n'est pas dans le bon format
     * => cas où le fichier ne détient pas de clé
     */
    @Test
    public void testImportExcelWithFileWithoutKeyKO() {
        ExcelImportConfigurationCmd excelImportConfigurationCmd = new ExcelImportConfigurationCmd(FileProviderTest.get().getImportExcelExcelWithoutKeyKO(), sheetName);
        excelImportConfigurationCmd.addFieldToImport("[TITRE_CHAPITRE]");
        excelImportConfigurationCmd.addFieldToImport("[RESUME_CHAPITRE]");

        try {
            configurationControler.importExcel(excelImportConfigurationCmd);
            fail(ERROR);
        } catch (ServerException | ImportExcelException | IOException | LoadTextException e) {
            assertTrue(!e.getMessage().isBlank());
        }

    }

    /**
     * Permet de vérifier qu'une erreur est levé si un champ n'est pas trouvé
     * => cas où un champ n'est pas trouvé
     */
    @Test
    public void testImportExcelWithFieldKO() {
        ExcelImportConfigurationCmd excelImportConfigurationCmd = new ExcelImportConfigurationCmd(FileProviderTest.get().getImportExcelExcelWithoutKeyKO(), sheetName);
        excelImportConfigurationCmd.addFieldToImport("[TITRE_CHAPITRE]");
        excelImportConfigurationCmd.addFieldToImport("[RESUME_CHAPITRE]");
        excelImportConfigurationCmd.addFieldToImport("GRRR");

        try {
            configurationControler.importExcel(excelImportConfigurationCmd);
            fail(ERROR);
        } catch (ServerException | ImportExcelException | IOException | LoadTextException e) {
            assertTrue(!e.getMessage().isBlank());
        }

    }

    /**
     * Permet de vérifier qu'une erreur est levé si le spécifique n'est pas trouvé
     * => cas où le spécifique n'est pas trouvé
     */
    @Test
    public void testImportExcelWithSpecificConfigurationKO() {
        ExcelImportConfigurationCmd excelImportConfigurationCmd = new ExcelImportConfigurationCmd(FileProviderTest.get().getImportExcelExcelWithoutKeyKO(), sheetName);
        excelImportConfigurationCmd.addFieldToImport("[TITRE_CHAPITRE]");
        excelImportConfigurationCmd.addFieldToImport("[RESUME_CHAPITRE]");
        excelImportConfigurationCmd.setIsSpecificImport(true);
        excelImportConfigurationCmd.setLabelSpecificChoose("grr");

        try {
            configurationControler.importExcel(excelImportConfigurationCmd);
            fail(ERROR);
        } catch (ServerException | ImportExcelException | IOException | LoadTextException e) {
            assertTrue(!e.getMessage().isBlank());
        }

    }

    /**
     * Permet de vérifier l'import d'un fichier excel sans configuration spécifique
     */
    @Test
    public void testImportExcelWithoutSpecificConfiguration() {

        // Au chargement, il faut vérifier que le fichier excel détient les bons en têtes, les clés
        // Si spécifique, il faut valider que les champs spécifiques n'ont pas de séparateur

        ExcelImportConfigurationCmd excelImportConfigurationCmd = new ExcelImportConfigurationCmd(FileProviderTest.get().getImportExcelExcelWithoutSpecificConfigurationFile(), sheetName);
        excelImportConfigurationCmd.addFieldToImport("[TITRE_CHAPITRE]");
        excelImportConfigurationCmd.addFieldToImport("[RESUME_CHAPITRE]");

        try {
            configurationControler.importExcel(excelImportConfigurationCmd);
            configurationControler.loadTexts();
        } catch (ServerException | ImportExcelException | IOException | LoadTextException e) {
            fail(NO_ERROR);
        }

        List<UserStructuredText> userStructuredTextList = UserSettings.getInstance().getUserStructuredTextList(FolderSettingsEnum.FOLDER_TEXTS);

        // Vérification du texte 1
        Optional<UserStructuredText> userStructuredText1 = userStructuredTextList.stream().filter(ust -> "1".equals(ust.getStructuredText().getContent("[NUMERO_CHAPITRE]"))).findFirst();
        assertFalse(userStructuredText1.isEmpty());
        String nouveauTitre1 = userStructuredText1.get().getStructuredText().getContent("[TITRE_CHAPITRE]");
        assertEquals(NOUVEAU_TITRE_1, nouveauTitre1);
        String resumeChapitre1 = userStructuredText1.get().getStructuredText().getContent("[RESUME_CHAPITRE]");
        assertEquals(NOUVEAU_RESUME_1, resumeChapitre1);
        String genre1 = userStructuredText1.get().getStructuredText().getContent("[GENRE]");
        assertEquals(GENRE_LIVRE, genre1);

        // Vérification du texte 2
        Optional<UserStructuredText> userStructuredText2 = userStructuredTextList.stream().filter(ust -> "2".equals(ust.getStructuredText().getContent("[NUMERO_CHAPITRE]"))).findFirst();
        assertFalse(userStructuredText2.isEmpty());
        String nouveauTitre2 = userStructuredText2.get().getStructuredText().getContent("[TITRE_CHAPITRE]");
        assertEquals(NOUVEAU_TITRE_2, nouveauTitre2);
        String resumeChapitre2 = userStructuredText2.get().getStructuredText().getContent("[RESUME_CHAPITRE]");
        assertEquals(NOUVEAU_RESUME_2, resumeChapitre2);
        String genre2 = userStructuredText2.get().getStructuredText().getContent("[GENRE]");
        assertEquals(GENRE_LIVRE, genre2);

    }

    /**
     * Permet de vérifier l'import d'un fichier excel sans configuration spécifique
     */
    @Test
    public void testImportExcelWithSpecificConfiguration() {
        ExcelImportConfigurationCmd excelImportConfigurationCmd = new ExcelImportConfigurationCmd(FileProviderTest.get().getImportExcelExcelWithSpecificConfigurationFile(), sheetName);
        excelImportConfigurationCmd.addFieldToImport("[CATEGORIE_MOT_CLE]");
        excelImportConfigurationCmd.addFieldToImport("[MOT_CLE]");
        excelImportConfigurationCmd.addFieldToImport("[NB_OCCURENCE]");
        excelImportConfigurationCmd.setIsSpecificImport(true);
        excelImportConfigurationCmd.setLabelSpecificChoose("Analyse des mot clés");

        try {
            configurationControler.importExcel(excelImportConfigurationCmd);
            configurationControler.loadTexts();
        } catch (ServerException | ImportExcelException | IOException | LoadTextException e) {
            fail(NO_ERROR);
        }

        List<UserStructuredText> userStructuredTextList = UserSettings.getInstance().getUserStructuredTextList(FolderSettingsEnum.FOLDER_TEXTS);

        // Vérification du texte 1
        Optional<UserStructuredText> userStructuredText1 = userStructuredTextList.stream().filter(ust -> "1".equals(ust.getStructuredText().getContent("[NUMERO_CHAPITRE]"))).findFirst();
        assertFalse(userStructuredText1.isEmpty());
        String categorieMotCle1 = userStructuredText1.get().getStructuredText().getContent("[CATEGORIE_MOT_CLE]");
        assertEquals(CATEGORIE_1, categorieMotCle1);
        String motCle1 = userStructuredText1.get().getStructuredText().getContent("[MOT_CLE]");
        assertEquals(CLE_1, motCle1);
        String nbOccurence1 = userStructuredText1.get().getStructuredText().getContent("[NB_OCCURENCE]");
        assertEquals(NB_OCCURENCE_1, nbOccurence1);
        String genre1 = userStructuredText1.get().getStructuredText().getContent("[GENRE]");
        assertEquals(GENRE_LIVRE, genre1);

        // Vérification du texte 2
        Optional<UserStructuredText> userStructuredText2 = userStructuredTextList.stream().filter(ust -> "2".equals(ust.getStructuredText().getContent("[NUMERO_CHAPITRE]"))).findFirst();
        assertFalse(userStructuredText2.isEmpty());
        String categorieMotCle2 = userStructuredText2.get().getStructuredText().getContent("[CATEGORIE_MOT_CLE]");
        assertEquals(CATEGORIE_2, categorieMotCle2);
        String motCle2 = userStructuredText2.get().getStructuredText().getContent("[MOT_CLE]");
        assertEquals(CLE_2, motCle2);
        String nbOccurence2 = userStructuredText2.get().getStructuredText().getContent("[NB_OCCURENCE]");
        assertEquals(NB_OCCURENCE_2, nbOccurence2);
        String genre2 = userStructuredText2.get().getStructuredText().getContent("[GENRE]");
        assertEquals(GENRE_LIVRE, genre2);
    }

}
