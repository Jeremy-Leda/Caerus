package utils;

import java.io.File;

/**
 *
 * Classe permettant de fournir les constantes
 *
 */
public final class ConstantTest {

    /**
     * Chemin du dossier caerus
     */
    public static final String caerusFolderPath = FileProviderTest.get().getClassicalTextFile().getParent() + File.separator + "caerusFolderTest";

    /**
     * Nom de la configuration pour les tests
     */
    public static final String configurationTestName = "Livre Analyse";
}
