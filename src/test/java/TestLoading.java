import model.analyze.UserFolder;
import model.analyze.constants.FolderSettingsEnum;
import model.analyze.lexicometric.beans.LemmatizationByGrammaticalCategory;
import model.analyze.lexicometric.beans.LexicometricAnalysis;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JSonFactoryUtils;
import utils.RessourcesUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class TestLoading {

    private static Logger logger = LoggerFactory.getLogger(TestLoading.class);
    private LexicometricAnalysis lexicometricAnalysis = new LexicometricAnalysis();

    public void test() throws IOException {
        loadConfigurationsList();


    }

    /**
     * Permet de charger la liste des configurations
     *
     * @throws IOException erreur d'entrée sortie
     */
    private void loadConfigurationsList() throws IOException {
        File file = new File("C:\\Users\\jerem\\Documents\\Caerus\\type");

        Map<String, Map<String, Set<String>>> baseListWordsMap = new HashMap<>();
        if (file.exists()) {
            Files.walkFileTree(Paths.get(file.toURI()), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!Files.isDirectory(file)) {
                        logger.debug(String.format("Loading Configuration Lexicometric Grammatical %s", file));
                        Map<String, Set<String>> lemmeMap = convertFile(file);
                        baseListWordsMap.put(file.getFileName().toString(), lemmeMap);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        lexicometricAnalysis.setLemmatizationByGrammaticalCategorySet(new HashSet<>());
        LemmatizationByGrammaticalCategory lemmatization = new LemmatizationByGrammaticalCategory();
        lemmatization.setProfile("español");
        lemmatization.setData(baseListWordsMap);
        lexicometricAnalysis.getLemmatizationByGrammaticalCategorySet().add(lemmatization);
        lexicometricAnalysis.setLemmatizationSet(new HashSet<>());
        lexicometricAnalysis.setTokenizationSet(new HashSet<>());
        JSonFactoryUtils.createJsonInFile(lexicometricAnalysis, new File("C:\\Users\\jerem\\Documents\\Caerus\\type\\Sortie.json"));
    }

    private Map<String, Set<String>> convertFile(Path path) {
        Map<String, Set<String>> map = new HashMap<>();
        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(l -> fillMap(l, map));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return map;
    }

    private void fillMap(String line, Map<String, Set<String>> map) {
        String[] splitBaseLemme = StringUtils.split(line, "===");
        Set<String> lemmeSet = map.getOrDefault(splitBaseLemme[0], new HashSet<>());
        String[] splitLemme = StringUtils.split(splitBaseLemme[1], ";");
        lemmeSet.addAll(Arrays.stream(splitLemme).collect(Collectors.toSet()));
        map.put(splitBaseLemme[0], lemmeSet);
    }
}
