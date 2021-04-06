package utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Paths;

/**
 *
 * Classe permettant de fournir des fichiers au besoin
 *
 */
public class FileProviderTest {

    private final String classicalText = "LoadingTextClassical/classical.txt";
    private final String importExcelLibrary = "ImportExcel/";
    private final String importExcelFileTextLibrary = "ImportExcel/Livre Analyse/classical.txt";
    private final String importExcelExcelWithoutSpecificConfiguration = "ImportExcel/Classical.xlsx";
    private final String importExcelExcelWithSpecificConfiguration = "ImportExcel/Classical_mot_cle_analyse.xlsx";
    private final String importExcelExcelWithSpecificConfigurationKO = "ImportExcel/Classical.xlsx";
    private final String importExcelExcelWithoutHeaderKO = "ImportExcel/ClassicalWithoutHeader.xlsx";
    private final String importExcelExcelWithoutKeyKO = "ImportExcel/ClassicalWithoutKey.xlsx";
    private final String bookAnalysis = "analyse_livre.json";
    private static final FileProviderTest fileProvider = new FileProviderTest();

    /**
     * Permet de se procurer le fournisseur de fichier
     * @return le fournisseur de fichier
     */
    public static FileProviderTest get() {
        return fileProvider;
    }

    /**
     * Permet de se procurer le fichier classique d'analyse
     * @return le fichier classique d'analyse
     */
    public File getClassicalTextFile() {
        return getRessourceText(classicalText);
    }

    /**
     * Permet de se procurer le chemin de la bibliothèque pour l'import excel
     * @return le chemin de la bibliothèque pour l'import excel
     */
    public File getImportExcelLibraryFile() {
        return getRessourceText(importExcelLibrary);
    }

    /**
     * Permet de se procurer le chemin du fichier texte de la bibliothèque
     * @return le chemin du fichier texte de la bibliothèque
     */
    public File getImportExcelFileTextLibrary() {
        return getRessourceText(importExcelFileTextLibrary);
    }

    /**
     * Permet de se procurer le fichier excel à importer (sans configuration spécifique)
     * @return le fichier excel à importer (sans configuration spécifique)
     */
    public File getImportExcelExcelWithoutSpecificConfigurationFile() {
        return getRessourceText(importExcelExcelWithoutSpecificConfiguration);
    }

    /**
     * Permet de se procurer le fichier excel à importer (avec configuration spécifique)
     * @return le fichier excel à importer (avec configuration spécifique)
     */
    public File getImportExcelExcelWithSpecificConfigurationFile() {
        return getRessourceText(importExcelExcelWithSpecificConfiguration);
    }

    /**
     * Permet de se procurer le fichier excel à importer (avec configuration spécifique) KO
     * @return le fichier excel à importer (avec configuration spécifique) KO
     */
    public File getImportExcelExcelWithSpecificConfigurationFileKO() {
        return getRessourceText(importExcelExcelWithSpecificConfigurationKO);
    }

    /**
     * Permet de se procurer le fichier excel à importer sans en-tête
     * @return le fichier excel à importer sans en-tête
     */
    public File getImportExcelExcelWithoutHeaderKO() {
        return getRessourceText(importExcelExcelWithoutHeaderKO);
    }

    /**
     * Permet de se procurer le fichier excel à importer sans clé
     * @return le fichier excel à importer sans clé
     */
    public File getImportExcelExcelWithoutKeyKO() {
        return getRessourceText(importExcelExcelWithoutKeyKO);
    }

    /**
     * Permet de se procurer le fichier de configuration d'analyse des livres
     * @return le fichier de configuration d'analyse des livres
     */
    public File getBookAnalysisConfiguration() {
        return getRessourceText(bookAnalysis);
    }

    /**
     * Permet de se procurer le fichier de configuration pour l'analyse
     * @return le fichier de configuration pour l'analyse
     */
    public File getConfigurationAnalysisFile() {
       return new File(PathUtils.getCaerusFolder() + "/configurations/analyse_livre.json");
    }

    /**
     * Permet de se procurer une ressource de test
     * @param nameFile Nom du fichier (chemin compris)
     * @return le fichier ressource
     */
    private File getRessourceText(String nameFile) {
        ClassLoader classLoader = getClass().getClassLoader();
        String filePath = classLoader.getResource(nameFile).getPath();
        String urlDecoder = StringUtils.EMPTY;
        try {
            urlDecoder = URLDecoder.decode(filePath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file = new File(urlDecoder);
        return file;
    }

}
