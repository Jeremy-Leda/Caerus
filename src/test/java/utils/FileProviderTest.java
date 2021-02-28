package utils;

import java.io.File;

/**
 *
 * Classe permettant de fournir des fichiers au besoin
 *
 */
public class FileProviderTest {

    private final String classicalText = "LoadingTextClassical/classical.txt";
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
        File file = new File(classLoader.getResource(nameFile).getFile());
        return file;
    }

}
