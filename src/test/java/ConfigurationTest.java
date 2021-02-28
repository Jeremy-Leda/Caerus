import controler.ConfigurationControler;
import org.junit.jupiter.api.AfterAll;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.ConstantTest.configurationTestName;

/**
 *
 * Permet de tester les paramètres fournis par l'utilisateur
 *
 */
@ExtendWith(MockitoExtension.class)
public class ConfigurationTest {

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
     * Permet de vérifier que la récupération des champ méta
     */
    @Test
    public void checkGetConfigurationFieldMetaFile() {
        configurationControler.setCurrentConfiguration(configurationTestName);
        Map<String, String> configurationFieldMetaFile = configurationControler.getConfigurationFieldMetaFile();
        assertEquals(4, configurationFieldMetaFile.size());
        assertTrue(configurationFieldMetaFile.containsKey("[TITRE]"));
        assertTrue(configurationFieldMetaFile.containsKey("[AUTEUR]"));
        assertTrue(configurationFieldMetaFile.containsKey("[ANNEE]"));
        assertTrue(configurationFieldMetaFile.containsKey("[GENRE]"));
        assertEquals("[TITRE] (Titre du livre)", configurationFieldMetaFile.get("[TITRE]"));
        assertEquals("[AUTEUR] (Auteur du livre)", configurationFieldMetaFile.get("[AUTEUR]"));
        assertEquals("[ANNEE] (Année du livre)", configurationFieldMetaFile.get("[ANNEE]"));
        assertEquals("[GENRE] (Genre du livre)", configurationFieldMetaFile.get("[GENRE]"));
    }

    /**
     * Permet de vérifier que la récupération des champ communs
     */
    @Test
    public void checkGetConfigurationFieldCommonFile() {
        configurationControler.setCurrentConfiguration(configurationTestName);
        Map<String, String> configurationFieldMetaFile = configurationControler.getConfigurationFieldCommonFile();
        assertEquals(3, configurationFieldMetaFile.size());
        assertTrue(configurationFieldMetaFile.containsKey("[NUMERO_CHAPITRE]"));
        assertTrue(configurationFieldMetaFile.containsKey("[TITRE_CHAPITRE]"));
        assertTrue(configurationFieldMetaFile.containsKey("[RESUME_CHAPITRE]"));
        assertEquals("[NUMERO_CHAPITRE] (Numéro du chapitre)", configurationFieldMetaFile.get("[NUMERO_CHAPITRE]"));
        assertEquals("[TITRE_CHAPITRE] (Titre du chapitre)", configurationFieldMetaFile.get("[TITRE_CHAPITRE]"));
        assertEquals("[RESUME_CHAPITRE] (Résumé du chapitre)", configurationFieldMetaFile.get("[RESUME_CHAPITRE]"));
    }

    /**
     * Permet de vérifier que la récupération du nombre de configuration spécifique
     */
    @Test
    public void checkGetNbSpecificConfiguration() {
        configurationControler.setCurrentConfiguration(configurationTestName);
        int nbSpecificConfiguration = configurationControler.getNbSpecificConfiguration();
        assertEquals(2, nbSpecificConfiguration);
    }

    /**
     * Permet de vérifier que la récupération de la map sur les libellé, lié au prefix pour le fichier
     */
    @Test
    public void checkGetConfigurationSpecificLabelNameFileMap() {
        configurationControler.setCurrentConfiguration(configurationTestName);
        Map<String, String> configurationSpecificLabelNameFileMap = configurationControler.getConfigurationSpecificLabelNameFileMap();
        assertEquals(2, configurationSpecificLabelNameFileMap.size());
        assertTrue(configurationSpecificLabelNameFileMap.containsKey("Analyse des mot clés"));
        assertTrue(configurationSpecificLabelNameFileMap.containsKey("Analyse des examinateurs"));
        assertEquals("_mot_cle_analyse", configurationSpecificLabelNameFileMap.get("Analyse des mot clés"));
        assertEquals("_examinateur_analyse", configurationSpecificLabelNameFileMap.get("Analyse des examinateurs"));
    }

    /**
     * Permet de vérifier que la récupération de la map sur tous les champs est OK
     */
    @Test
    public void checkGetFieldConfigurationNameLabelMap() {
        configurationControler.setCurrentConfiguration(configurationTestName);
        Map<String, String> fieldConfigurationNameLabelMap = configurationControler.getFieldConfigurationNameLabelMap();
        assertEquals(13, fieldConfigurationNameLabelMap.size());
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[TITRE]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[AUTEUR]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[ANNEE]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[GENRE]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[NUMERO_CHAPITRE]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[TITRE_CHAPITRE]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[RESUME_CHAPITRE]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[CATEGORIE_MOT_CLE]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[MOT_CLE]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[NB_OCCURENCE]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[NOMBRE_EXAMINATEURS]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[NOM_EXAMINATEUR]"));
        assertTrue(fieldConfigurationNameLabelMap.containsKey("[NOTE]"));
        assertEquals("Titre du livre", fieldConfigurationNameLabelMap.get("[TITRE]"));
        assertEquals("Auteur du livre",fieldConfigurationNameLabelMap.get("[AUTEUR]"));
        assertEquals("Année du livre",fieldConfigurationNameLabelMap.get("[ANNEE]"));
        assertEquals("Genre du livre",fieldConfigurationNameLabelMap.get("[GENRE]"));
        assertEquals("Numéro du chapitre",fieldConfigurationNameLabelMap.get("[NUMERO_CHAPITRE]"));
        assertEquals("Titre du chapitre",fieldConfigurationNameLabelMap.get("[TITRE_CHAPITRE]"));
        assertEquals("Résumé du chapitre",fieldConfigurationNameLabelMap.get("[RESUME_CHAPITRE]"));
        assertEquals("Catégorie Mot Clé",fieldConfigurationNameLabelMap.get("[CATEGORIE_MOT_CLE]"));
        assertEquals("Mot Clé",fieldConfigurationNameLabelMap.get("[MOT_CLE]"));
        assertEquals("Nb d'occurrence",fieldConfigurationNameLabelMap.get("[NB_OCCURENCE]"));
        assertEquals("Nombre d'examinateurs",fieldConfigurationNameLabelMap.get("[NOMBRE_EXAMINATEURS]"));
        assertEquals("Nom de l'examinateur",fieldConfigurationNameLabelMap.get("[NOM_EXAMINATEUR]"));
        assertEquals("Note sur 5",fieldConfigurationNameLabelMap.get("[NOTE]"));
    }

    /**
     * Permet de vérifier que la récupération des champs pour une récupération d'un spécifique est OK
     */
    @Test
    public void checkGetFieldListToProcess() {
        configurationControler.setCurrentConfiguration(configurationTestName);
        List<String> fieldListToProcess = configurationControler.getFieldListToProcess("Analyse des mot clés");
        assertEquals(3, fieldListToProcess.size());
        assertTrue(fieldListToProcess.contains("[CATEGORIE_MOT_CLE]"));
        assertTrue(fieldListToProcess.contains("[MOT_CLE]"));
        assertTrue(fieldListToProcess.contains("[NB_OCCURENCE]"));
    }

    /**
     * Permet de vérifier que la récupération des champs interdits pour une récupération d'un spécifique est OK
     */
    @Test
    public void checkGetFieldListForbiddenToDisplay() {
        configurationControler.setCurrentConfiguration(configurationTestName);
        List<String> fieldListToProcess = configurationControler.getFieldListForbiddenToDisplay("Analyse des mot clés");
        assertEquals(2, fieldListToProcess.size());
        assertTrue(fieldListToProcess.contains("[NOM_EXAMINATEUR]"));
        assertTrue(fieldListToProcess.contains("[NOTE]"));
    }

    /**
     * Permet de vérifier la liste des configurations disponible
     */
    @Test
    public void checkGetConfigurationNameList() {
        List<String> configurationNameList = configurationControler.getConfigurationNameList();
        assertEquals(2, configurationNameList.size());
        assertTrue(configurationNameList.contains(configurationTestName));
        assertTrue(configurationNameList.contains("Configuración básica"));
    }

    /**
     * Permet de vérifier le dossier de configuration
     */
    @Test
    public void checkGetConfigurationFolder() {
        File configurationFolder = configurationControler.getConfigurationFolder();
        File expected = new File(PathUtils.getCaerusFolder() + "/configurations");
        assertEquals(expected, configurationFolder);
    }

    /**
     * Permet de vérifier le retour de la récupération de tous les champs en code - libellé
     */
    @Test
    public void checkGetAllFields() {
        Map<String, String> allField = configurationControler.getAllField();
        assertEquals(13, allField.size());
        assertTrue(allField.containsKey("[TITRE]"));
        assertTrue(allField.containsKey("[AUTEUR]"));
        assertTrue(allField.containsKey("[ANNEE]"));
        assertTrue(allField.containsKey("[GENRE]"));
        assertTrue(allField.containsKey("[NUMERO_CHAPITRE]"));
        assertTrue(allField.containsKey("[TITRE_CHAPITRE]"));
        assertTrue(allField.containsKey("[RESUME_CHAPITRE]"));
        assertTrue(allField.containsKey("[CATEGORIE_MOT_CLE]"));
        assertTrue(allField.containsKey("[MOT_CLE]"));
        assertTrue(allField.containsKey("[NB_OCCURENCE]"));
        assertTrue(allField.containsKey("[NOMBRE_EXAMINATEURS]"));
        assertTrue(allField.containsKey("[NOM_EXAMINATEUR]"));
        assertTrue(allField.containsKey("[NOTE]"));
        assertEquals("[TITRE] (Titre du livre)", allField.get("[TITRE]"));
        assertEquals("[AUTEUR] (Auteur du livre)",allField.get("[AUTEUR]"));
        assertEquals("[ANNEE] (Année du livre)",allField.get("[ANNEE]"));
        assertEquals("[GENRE] (Genre du livre)",allField.get("[GENRE]"));
        assertEquals("[NUMERO_CHAPITRE] (Numéro du chapitre)",allField.get("[NUMERO_CHAPITRE]"));
        assertEquals("[TITRE_CHAPITRE] (Titre du chapitre)",allField.get("[TITRE_CHAPITRE]"));
        assertEquals("[RESUME_CHAPITRE] (Résumé du chapitre)",allField.get("[RESUME_CHAPITRE]"));
        assertEquals("[CATEGORIE_MOT_CLE] (Catégorie Mot Clé)",allField.get("[CATEGORIE_MOT_CLE]"));
        assertEquals("[MOT_CLE] (Mot Clé)",allField.get("[MOT_CLE]"));
        assertEquals("[NB_OCCURENCE] (Nb d'occurrence)",allField.get("[NB_OCCURENCE]"));
        assertEquals("[NOMBRE_EXAMINATEURS] (Nombre d'examinateurs)",allField.get("[NOMBRE_EXAMINATEURS]"));
        assertEquals("[NOM_EXAMINATEUR] (Nom de l'examinateur)",allField.get("[NOM_EXAMINATEUR]"));
        assertEquals("[NOTE] (Note sur 5)",allField.get("[NOTE]"));
    }

    /**
     * Permet de vérifier le retour de la récupération de tous les champs en code - libellé
     */
    @Test
    public void checkDelimiterSpecific() {
        String delimiterSpecific = configurationControler.getDelimiterSpecific(0);
        assertEquals(",", delimiterSpecific);
    }

    /**
     * Permet de nettoyer après tous les tests
     */
    @AfterAll
    public static void afterAllTests() {
        PathUtils.deleteFile(FileProviderTest.get().getConfigurationAnalysisFile());
        configurationControler = null;
    }

}
