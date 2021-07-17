package model.analyze;

import model.analyze.beans.UserStructuredText;
import model.analyze.constants.FolderSettingsEnum;
import model.analyze.lexicometric.analyze.beans.Text;
import model.analyze.lexicometric.analyze.beans.Token;
import model.analyze.lexicometric.beans.LexicometricAnalyzeServerCmd;
import model.analyze.lexicometric.beans.LexicometricCleanListEnum;
import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;
import model.analyze.lexicometric.interfaces.ILexicometricData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import view.analysis.beans.AnalysisResultDisplay;
import view.analysis.beans.AnalysisResultDisplayBuilder;
import view.analysis.beans.AnalysisTokenDisplay;
import view.analysis.beans.AnalysisTokenDisplayBuilder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LexicometricAnalysis {

    private static final LexicometricAnalysis _instance = new LexicometricAnalysis();
    private final Set<Text> analysisResultSet = new HashSet<>();

    /**
     * Permet de se procurer l'instance statique
     *
     * @return l'instance statique
     */
    public static LexicometricAnalysis getInstance() {
        return _instance;
    }

    /**
     * Permet de se procurer la liste des textes analysés
     * @param cmd commande
     * @return la liste des textes analysés
     */
    public void executeNumberTokensAnalyze(LexicometricAnalyzeServerCmd cmd) {
        analysisResultSet.clear();
        Map<String, Map<String, Long>> keyTextMap = cmd.getKeyTextFilteredList().stream()
                .collect(Collectors.toMap(Function.identity(), s -> getNbTokensOfText(s, cmd.getFieldToAnalyzeSet(), cmd.getPreTreatmentListLexicometricMap())));
        analysisResultSet.addAll(keyTextMap.entrySet().stream().map(this::getTextFromNumberTokensEntry).collect(Collectors.toSet()));
    }

    /**
     * Permet de se procurer le nombre de tokens dans un texte
     * => Renvois une map qui détient en clé un mot et en valeur son nombre total d'apparition
     * @param keyText Clé du texte
     * @param fieldSet Liste des champs à analyser
     * @return une map qui détient en clé un mot et en valeur son nombre total d'apparition
     */
    private Map<String, Long> getNbTokensOfText(String keyText, Set<String> fieldSet, Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap) {
//        Optional<UserStructuredText> textFromKey = UserSettings.getInstance().getTextFromKey(keyText);
//        String textToAnalyze = fieldSet.stream()
//                .map(f -> textFromKey.get().getStructuredText().getContent(f)).reduce((s1, s2) -> s1 + StringUtils.SPACE + s2)
//                .orElse(StringUtils.EMPTY);
//        List<String> cleanWords = Arrays.stream(StringUtils.split(textToAnalyze))
//                .map(s -> s.replaceAll("/[^a-zA-Z ]/g", ""))
//                .map(s -> s.replaceAll("[\\p{Punct}&&[^'-]]+", ""))
//                .map(s -> s.replaceAll("-", ""))
//                .map(s -> s.replaceAll("¿", ""))
//                .map(s -> s.replaceAll("[0-9]", ""))
//                .map(s -> s.replaceAll("^\"|\"$", ""))
//                .map(s -> s.replaceAll("“", ""))
//                .map(s -> s.replaceAll("”", ""))
//                .map(s -> s.replaceAll("»", ""))
//                .map(s -> s.replaceAll("«", ""))
//                .map(s -> s.replaceAll("^\'|\'$", ""))
//                .map(s -> s.replaceAll("‘", ""))
//                .map(s -> s.replaceAll("—", ""))
//                .map(s -> s.toLowerCase())
//                .filter(StringUtils::isNotBlank).collect(Collectors.toList());

        Collection<String> tokenCleanedCollection = getTokenCleaned(keyText, fieldSet);
        if (preTreatmentListLexicometricMap.containsKey(LexicometricConfigurationEnum.PROPER_NOUN)) {
            Set<String> properNounToRemoveSet = getProperNounToRemoveSet(preTreatmentListLexicometricMap.get(LexicometricConfigurationEnum.PROPER_NOUN));
            tokenCleanedCollection.removeAll(properNounToRemoveSet);
        }


        return tokenCleanedCollection.stream().map(s -> s.toLowerCase()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /**
     * Permet de se procurer la liste des noms propres potentiels
     * @param keyTextSet Liste des clés du texte
     * @param fieldSet Liste des champs identifiés
     * @return la liste des noms propres potentiels
     */
    public Collection<String> getPotentialProperNounCollection(Set<String> keyTextSet, Set<String> fieldSet, Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap) {
        Collection<String> tokenCleanedCollection = new HashSet<>();
        keyTextSet.forEach(s -> tokenCleanedCollection.addAll(getPotentialProperNounCollection(s, fieldSet, preTreatmentListLexicometricMap)));
        return tokenCleanedCollection;
    }

    /**
     * Permet de se procurer la liste des noms propres potentiels
     * @param keyText Clé du texte
     * @param fieldSet Liste des champs identifiés
     * @return la liste des noms propres potentiels
     */
    private Collection<String> getPotentialProperNounCollection(String keyText, Set<String> fieldSet, Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap) {
        Collection<String> tokenCleanedCollection = getTokenCleaned(keyText, fieldSet);
        if (preTreatmentListLexicometricMap.containsKey(LexicometricConfigurationEnum.PROPER_NOUN)) {
            Set<String> properNounToRemoveSet = getProperNounToRemoveSet(preTreatmentListLexicometricMap.get(LexicometricConfigurationEnum.PROPER_NOUN));
            tokenCleanedCollection.removeAll(properNounToRemoveSet);
        }
        return tokenCleanedCollection.stream().filter(s -> Character.isUpperCase(s.charAt(0))).collect(Collectors.toSet());
    }

    /**
     * Permet de se procurer la liste des tokens nettoyés
     * @param keyText Clé du texte
     * @param fieldSet Liste des champs à analyser
     * @return la liste des tokens nettoyés
     */
    private Collection<String> getTokenCleaned(String keyText, Set<String> fieldSet) {
        Optional<UserStructuredText> textFromKey = UserSettings.getInstance().getTextFromKey(keyText);
        String textToAnalyze = fieldSet.stream()
                .map(f -> textFromKey.get().getStructuredText().getContent(f)).reduce((s1, s2) -> s1 + StringUtils.SPACE + s2)
                .orElse(StringUtils.EMPTY);
        List<String> tokenList = Arrays.stream(StringUtils.split(textToAnalyze))
                .map(s -> s.replaceAll("/[^a-zA-Z ]/g", ""))
                .map(s -> s.replaceAll("[\\p{Punct}&&[^'-]]+", ""))
                .map(s -> s.replaceAll("-", ""))
                .map(s -> s.replaceAll("¿", ""))
                .map(s -> s.replaceAll("[0-9]", ""))
                .map(s -> s.replaceAll("^\"|\"$", ""))
                .map(s -> s.replaceAll("“", ""))
                .map(s -> s.replaceAll("”", ""))
                .map(s -> s.replaceAll("»", ""))
                .map(s -> s.replaceAll("«", ""))
                .map(s -> s.replaceAll("^\'|\'$", ""))
                .map(s -> s.replaceAll("‘", ""))
                .map(s -> s.replaceAll("—", ""))
                .map(s -> s.replaceAll("¡", ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        return tokenList;
    }

    /**
     * Permet de se procurer le text à partir de l'entrée de la map des nombres de tokens
     * @param entry l'entrée de la map des nombres de tokens
     * @return le text
     */
    private Text getTextFromNumberTokensEntry(Map.Entry<String, Map<String, Long>> entry) {
        Text text = new Text(entry.getKey());
        text.getTokenSet().addAll(entry.getValue().entrySet().stream().map(this::getTokenFromNumberTokensEntry).collect(Collectors.toSet()));
        return text;
    }

    /**
     * Permet de se procurer le token à partir de l'entrée de la map des nombres de tokens
     * @param entry l'entrée de la map des nombres de tokens
     * @return le token
     */
    private Token getTokenFromNumberTokensEntry(Map.Entry<String, Long> entry) {
        Token token = new Token(entry.getKey());
        token.setNbOcurrency(entry.getValue());
        return token;
    }

    /**
     * Permet de se procurer le résultat de l'analyse
     * @param keyTextFilteredList liste des clés à récupérer
     * @return le résultat de l'analyse
     */
    public AnalysisResultDisplay getAnalysisResultDisplayForNumberTokens(List<String> keyTextFilteredList) {
        System.out.println("getAnalysisResultDisplayForNumberTokens GO");

        Set<Text> textSet = this.analysisResultSet.stream().filter(t -> keyTextFilteredList.contains(t.getKey())).collect(Collectors.toSet());

        List<Token> tokenList = textSet.stream().flatMap(t -> t.getTokenSet().stream()).collect(Collectors.toList());
        Set<String> wordSet = textSet.stream().flatMap(t -> t.getTokenSet().stream()).map(t -> t.getWord()).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        System.out.println(wordSet.size());
        Text text = new Text(StringUtils.EMPTY);
        Set<Token> tokenSet = wordSet.parallelStream().map(w -> {
            Long nbOccurrency = tokenList.parallelStream().filter(t -> t.getWord().equals(w)).map(t -> t.getNbOcurrency()).reduce(Long::sum).orElse(0L);
            Token token = new Token(w);
            token.setNbOcurrency(nbOccurrency);
            return token;
        }).collect(Collectors.toSet());
        text.getTokenSet().addAll(tokenSet);

//        System.out.println("START REDUCE");
//        Text text = textSet.parallelStream().reduce(this::reduceText).orElse(new Text(StringUtils.EMPTY));
        System.out.println("START CONVERT");
        AnalysisResultDisplay analysisResultDisplay = convertTextToAnalysisResultDisplay(text);
        System.out.println("getAnalysisResultDisplayForNumberTokens END");
        return analysisResultDisplay;
    }

    /**
     * Permet de réduire la liste des textes à un seul texte pour un résumé
     * @param text1 texte 1
     * @param text2 texte
     * @return le texte réduit
     */
    private Text reduceText(Text text1, Text text2) {
        Text text = new Text(StringUtils.EMPTY);
        text.getTokenSet().addAll(text1.getTokenSet());
        text2.getTokenSet().forEach(t -> {
            Optional<Token> optionalToken = text.getTokenSet().stream().filter(s -> s.equals(t)).findFirst();
            optionalToken.ifPresentOrElse(s -> s.setNbOcurrency(s.getNbOcurrency() + t.getNbOcurrency()), () -> text.getTokenSet().add(t));
        });
        return text;
    }

    /**
     * Permet de convertir un texte en résultat d'analyse
     * @param textToConvert texte à convertir
     * @return le résultat d'analyse
     */
    private AnalysisResultDisplay convertTextToAnalysisResultDisplay(Text textToConvert) {
        Set<AnalysisTokenDisplay> analysisTokenDisplaySet = textToConvert.getTokenSet()
                .stream()
                .map(this::convertTokenToAnalysisTokenDisplay)
                .collect(Collectors.toSet());
        return new AnalysisResultDisplayBuilder()
                .key(textToConvert.getKey())
                .analysisTokenDisplaySet(analysisTokenDisplaySet)
                .nbOccurrency(analysisTokenDisplaySet.stream().map(AnalysisTokenDisplay::getNbOcurrency).reduce(Long::sum).orElse(0L))
                .nbToken(analysisTokenDisplaySet.size())
                .build();
    }

    /**
     * Permet de convertir un token en résultat d'analyse pour un token
     * @param token token
     * @return résultat d'analyse pour un token
     */
    private AnalysisTokenDisplay convertTokenToAnalysisTokenDisplay(Token token) {
        AnalysisTokenDisplay analysisTokenDisplay = new AnalysisTokenDisplay();
        analysisTokenDisplay.setWord(token.getWord());
        analysisTokenDisplay.setNbOcurrency(token.getNbOcurrency());
        return analysisTokenDisplay;
    }

    /**
     * Permet de se procurer le texte pré traité
     * @param text texte
     * @return le texte pré traité
     */
    public String getTextPreTreatment(String text, Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap) {
        Set<String> properNounToRemoveSet = new HashSet<>();
        if (preTreatmentListLexicometricMap.containsKey(LexicometricConfigurationEnum.PROPER_NOUN)) {
            properNounToRemoveSet.addAll(getProperNounToRemoveSet(preTreatmentListLexicometricMap.get(LexicometricConfigurationEnum.PROPER_NOUN)));
        }
        List<String> cleanWords = Arrays.stream(StringUtils.split(text))
                .map(s -> s.replaceAll("/[^a-zA-Z ]/g", ""))
                .map(s -> s.replaceAll("[\\p{Punct}&&[^'-]]+", ""))
                .map(s -> s.replaceAll("-", ""))
                .map(s -> s.replaceAll("¿", ""))
                .map(s -> s.replaceAll("[0-9]", ""))
                .map(s -> s.replaceAll("^\"|\"$", ""))
                .map(s -> s.replaceAll("“", ""))
                .map(s -> s.replaceAll("”", ""))
                .map(s -> s.replaceAll("»", ""))
                .map(s -> s.replaceAll("«", ""))
                .map(s -> s.replaceAll("^\'|\'$", ""))
                .map(s -> s.replaceAll("‘", ""))
                .map(s -> s.replaceAll("—", ""))
                .map(s -> s.replaceAll("¡", ""))
                .filter(s -> !properNounToRemoveSet.contains(s))
                .map(s -> s.toLowerCase())
                .map(s -> StringUtils.SPACE + s + StringUtils.SPACE)
                .filter(StringUtils::isNotBlank).collect(Collectors.toList());
        return cleanWords.stream().reduce(String::concat).get();
    }

    private Set<String> getProperNounToRemoveSet(String profil) {
        Optional<ILexicometricData> optionalILexicometricData = UserLexicometricAnalysisSettings.getInstance().getDataSet(LexicometricCleanListEnum.PROPER_NOUN).stream().filter(s -> s.getProfile().equals(profil)).findFirst();
        if (optionalILexicometricData.isPresent()) {
            ILexicometricData<Set<String>> iLexicometricData = optionalILexicometricData.get();
            return iLexicometricData.getData();
        }
        return new HashSet<>();
    }

    /**
     * Permet de se procurer la liste des clés contenant la liste des mots demandés
     * Si la liste des mots demandés est vide alors la liste de toutes les clés est retournés
     * @param wordSet liste des mots
     * @return la liste des clés associés
     */
    public Collection<String> getKeyTextSetWithSelectedWords(Set<String> wordSet) {
        if (wordSet.isEmpty()) {
            return this.analysisResultSet.stream().map(Text::getKey).collect(Collectors.toSet());
        }
        return this.analysisResultSet.stream()
                .filter(x -> x.getTokenSet().stream()
                        .filter(s -> wordSet.contains(s.getWord()))
                        .findFirst()
                        .isPresent())
                .map(Text::getKey)
                .collect(Collectors.toSet());
    }
}
