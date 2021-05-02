import controler.ConfigurationControler;
import model.analyze.lexicometric.beans.Lemmatization;
import model.analyze.lexicometric.beans.LexicometricAnalysis;
import model.analyze.lexicometric.beans.Tokenization;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.FileProviderTest;
import utils.JSonFactoryUtils;
import utils.SetUpTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        assertEquals(file, configurationControler.getAnalyzeFolder().get());
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
        assertEquals(file, configurationControler.getTextsFolder().get());
    }

    @Test
    public void temp() throws IOException {
        LexicometricAnalysis lexicometricAnalysis = new LexicometricAnalysis();
        Lemmatization lemmatization = new Lemmatization();
        lemmatization.setProfile("Español");
        lemmatization.setData(getLem());
        lexicometricAnalysis.setLemmatizationSet(Set.of(lemmatization));
        Tokenization tokenization = new Tokenization();
        tokenization.setProfile("Español");
        tokenization.setData(getToken());
        lexicometricAnalysis.setTokenizationSet(Set.of(tokenization));

        JSonFactoryUtils.createJsonInFile(lexicometricAnalysis, new File("C:\\Users\\jerem\\Documents\\R\\temp.json"));
    }

    private Map<String, Set<String>> getLem() throws IOException {
        Map<String, Set<String>> resultat = new java.util.HashMap<>();
        Path path = new File("C:\\Users\\jerem\\Documents\\analyse_caerus\\lemmatization-es.txt").toPath();
        Stream<String> lines = Files.lines(path);
        lines.map(StringUtils::split).forEach(array -> {
            String base = array[0];
            String lem = array[1];
            resultat.computeIfPresent(base, (k, v) -> {
                Set<String> newList = new java.util.HashSet<>(Set.copyOf(v));
                newList.add(lem);
                return newList;
            });
            resultat.computeIfAbsent(base, k -> Set.of(lem));
        });
        return resultat;
    }

    private Set<String> getToken() throws IOException {
        Path path = new File("C:\\Users\\jerem\\Documents\\analyse_caerus\\stopwords-es.txt").toPath();
        return Files.lines(path).collect(Collectors.toSet());
    }


}
