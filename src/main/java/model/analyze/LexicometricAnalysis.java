package model.analyze;

import io.vavr.Tuple2;
import model.abstracts.ProgressAbstract;
import model.analyze.beans.CartesianGroup;
import model.analyze.beans.CartesianGroupBuilder;
import model.analyze.beans.UserStructuredText;
import model.analyze.cmd.AnalysisDetailResultDisplayCmd;
import model.analyze.lexicometric.analyze.beans.Text;
import model.analyze.lexicometric.analyze.beans.Token;
import model.analyze.lexicometric.beans.LexicometricAnalyzeServerCmd;
import model.analyze.lexicometric.beans.LexicometricCleanListEnum;
import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;
import model.analyze.lexicometric.interfaces.ILexicometricData;
import org.apache.commons.lang3.StringUtils;
import view.analysis.beans.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LexicometricAnalysis extends ProgressAbstract {

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
        super.createProgressBean(1);
        super.getProgressBean().setCurrentIterate(1);
        analysisResultSet.clear();
        AtomicInteger at = new AtomicInteger(1);
        super.getProgressBean().setNbMaxElementForCurrentIterate(cmd.getKeyTextFilteredList().size());
        Map<String, Map<String, Long>> keyTextMap = cmd.getKeyTextFilteredList().stream()
                .collect(Collectors.toMap(Function.identity(), s -> {
                    super.getProgressBean().setCurrentElementForCurrentIterate(at.getAndIncrement());
                    return getNbTokensOfText(s, cmd.getFieldToAnalyzeSet(), cmd.getPreTreatmentListLexicometricMap());
                }));
        analysisResultSet.addAll(keyTextMap.entrySet().stream().map(this::getTextFromNumberTokensEntry).collect(Collectors.toSet()));
        super.resetProgress();
    }

    /**
     * Permet de se procurer le nombre de tokens dans un texte
     * => Renvois une map qui détient en clé un mot et en valeur son nombre total d'apparition
     * @param keyText Clé du texte
     * @param fieldSet Liste des champs à analyser
     * @return une map qui détient en clé un mot et en valeur son nombre total d'apparition
     */
    private Map<String, Long> getNbTokensOfText(String keyText, Set<String> fieldSet, Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap) {
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
     * Permet de se procurer un set des résultats pour la consultation au détail
     * @param cmd Commande pour l'appel
     * @return un set des résultats pour la consultation au détail
     */
    public Set<AnalysisDetailResultDisplay> getAnalysisDetailResultDisplayForNumberTokens(AnalysisDetailResultDisplayCmd cmd) {
        super.createProgressBean(1);
        super.getProgressBean().setCurrentIterate(1);
        Set<AnalysisDetailResultDisplay> detailResultDisplaySet = cmd.getKeyTextFilteredList().stream().map(k -> {
            AnalysisResultDisplay analysisResultDisplayForNumberTokens = getAnalysisResultDisplayForNumberTokens(List.of(k), false);
            Map<String, String> fieldValueMap = cmd.getKeyFieldSet().stream()
                    .map(f -> new Tuple2<>(f, getTextPreTreatment(getValueFromKeyTextAndField(k, f), cmd.getPreTreatmentListLexicometricMap())))
                    .collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));
            analysisResultDisplayForNumberTokens.setKey(k);
            Tuple2<Integer, Integer> fileAndMaterialNumberOfText = getFileAndMaterialNumberOfText(k);
            return new AnalysisDetailResultDisplayBuilder()
            .analysisResultDisplay(analysisResultDisplayForNumberTokens)
            .fieldValueMap(fieldValueMap)
            .fileNumber(fileAndMaterialNumberOfText._1)
            .materialNumber(fileAndMaterialNumberOfText._2)
            .build();
        }).sorted(Comparator.comparing(AnalysisDetailResultDisplay::getFileNumber).thenComparing(AnalysisDetailResultDisplay::getMaterialNumber))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        super.resetProgress();
        return detailResultDisplaySet;
    }



    /**
     * Permet de se procurer le résultat de l'analyse
     * @param keyTextFilteredList liste des clés à récupérer
     * @return le résultat de l'analyse
     */
    public AnalysisResultDisplay getAnalysisResultDisplayForNumberTokens(List<String> keyTextFilteredList, Boolean driveProgress) {
        if (driveProgress) {
            super.createProgressBean(1);
            super.getProgressBean().setCurrentIterate(1);
        }
        Set<Text> textSet = this.analysisResultSet.stream().filter(t -> keyTextFilteredList.contains(t.getKey())).collect(Collectors.toSet());
        List<Token> tokenList = textSet.stream().flatMap(t -> t.getTokenSet().stream()).collect(Collectors.toList());
        Set<String> wordSet = textSet.stream().flatMap(t -> t.getTokenSet().stream()).map(t -> t.getWord()).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        super.getProgressBean().setNbMaxElementForCurrentIterate(wordSet.size());
        AtomicInteger at = new AtomicInteger(1);
        Text text = new Text(StringUtils.EMPTY);
        Set<Token> tokenSet = wordSet.parallelStream().map(w -> {
            Long nbOccurrency = tokenList.parallelStream().filter(t -> t.getWord().equals(w)).map(t -> t.getNbOcurrency()).reduce(Long::sum).orElse(0L);
            Token token = new Token(w);
            token.setNbOcurrency(nbOccurrency);
            super.getProgressBean().setCurrentElementForCurrentIterate(at.getAndIncrement());
            return token;
        }).collect(Collectors.toSet());
        text.getTokenSet().addAll(tokenSet);
        AnalysisResultDisplay analysisResultDisplay = convertTextToAnalysisResultDisplay(text);
        if (driveProgress) {
            super.resetProgress();
        }
        return analysisResultDisplay;
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
        AnalysisResultDisplay analysisResultDisplay = new AnalysisResultDisplayBuilder()
                .key(textToConvert.getKey())
                .analysisTokenDisplaySet(analysisTokenDisplaySet)
                .nbOccurrency(analysisTokenDisplaySet.stream().map(AnalysisTokenDisplay::getNbOcurrency).reduce(Long::sum).orElse(0L))
                .nbToken(analysisTokenDisplaySet.size())
                .build();
        return analysisResultDisplay;
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
        if (cleanWords.isEmpty()) {
            return StringUtils.EMPTY;
        }
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
     * @param keySet liste des clés pour la recherche
     * @param wordSet liste des mots
     * @return la liste des clés associés
     */
    public Collection<String> getKeyTextSetWithSelectedWords(Set<String> keySet, Set<String> wordSet) {
        if (keySet.isEmpty()) {
            keySet.addAll(this.analysisResultSet.stream().map(Text::getKey).collect(Collectors.toSet()));
        }
        if (wordSet.isEmpty()) {
            return keySet;
        }
        return this.analysisResultSet.stream()
                .filter(x -> keySet.contains(x.getKey()))
                .filter(x -> x.getTokenSet().stream()
                        .filter(s -> wordSet.contains(s.getWord()))
                        .findFirst()
                        .isPresent())
                .map(Text::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Permet de se procurer la liste des groupes généré
     * @param keySet Liste des clés à traiter
     * @param fieldSet liste des champs pour regrouper
     * @return la liste des groupes généré
     */
    public Set<AnalysisGroupDisplay> getAnalysisGroupDisplaySet(Set<String> keySet, Set<String> fieldSet) {
        Map<String, Set<CartesianGroup>> fieldValueSetMap = getFieldValueSetMap(keySet, fieldSet);
        List<List<CartesianGroup>> cartesianGroup = getCartesianGroup(fieldValueSetMap);
        super.createProgressBean(cartesianGroup.size());
        AtomicInteger at = new AtomicInteger(0);
        Set<AnalysisGroupDisplay> result = cartesianGroup.stream()
                .map(s -> {
                    super.getProgressBean().setCurrentIterate(at.getAndIncrement());
                    return constructAnalysisGroupDisplay(keySet, s);
                })
                .filter(s -> !s.getKeySet().isEmpty())
                .collect(Collectors.toSet());
        return result;
    }

    /**
     * Permet de se procurer la liste des valeurs associés aux champs pour l'ensemble des textes passé en paramètres
     * @param keySet Liste des clés du texte
     * @param fieldSet Liste des champs
     * @return la liste des valeurs associés aux champs
     */
    private Map<String, Set<CartesianGroup>> getFieldValueSetMap(Set<String> keySet, Set<String> fieldSet) {
        Map<String, Set<CartesianGroup>> map = new HashMap<>();
        fieldSet.forEach(f -> {
            Set<CartesianGroup> values = new HashSet<>();
            keySet.forEach(k -> {
                Optional<UserStructuredText> textFromKey = UserSettings.getInstance().getTextFromKey(k);
                String content = StringUtils.trim(textFromKey.get().getStructuredText().getContent(f));
                String label = UserSettings.getInstance().getAllListField().get(f);
                values.add(new CartesianGroupBuilder().field(f).value(content).label(label).build());
            });
            map.put(f, values);
        });
        return map;
    }

    /**
     * Permet de créer les groupes cartésiens à partir de la map des valeurs
     * @param valuesMap map des valeurs
     * @return les groupes cartésiens
     */
    private List<List<CartesianGroup>>getCartesianGroup(Map<String, Set<CartesianGroup>> valuesMap) {
        return valuesMap.values().stream()
                .map(list -> list.stream().map(Collections::singletonList)
                        .collect(Collectors.toList()))
                .reduce((list1, list2) -> list1.stream()
                        .flatMap(first -> list2.stream()
                                .map(second -> Stream.of(first, second)
                                        .flatMap(List::stream)
                                        .collect(Collectors.toList())))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * Permet de se procurer la liste des clés du textes répondant à un groupe cartésien
     * @param keySet liste des clés
     * @param cartesianGroupList groupe cartésien recherché
     * @return la liste des clés
     */
    private Set<String> getKeySetFromCartesianGroupList(Set<String> keySet, List<CartesianGroup> cartesianGroupList) {
        Set<String> keyList = new HashSet<>();
        super.getProgressBean().setNbMaxElementForCurrentIterate(keySet.size());
        AtomicInteger at = new AtomicInteger(1);
        keySet.forEach(k -> {
            Optional<UserStructuredText> textFromKey = UserSettings.getInstance().getTextFromKey(k);
            Set<CartesianGroup> cartesianGroupSet = cartesianGroupList.stream()
                    .filter(group -> group.getValue().equals(StringUtils.trim(textFromKey.get().getStructuredText().getContent(group.getField()))))
                    .collect(Collectors.toSet());
            if (cartesianGroupSet.size() == cartesianGroupList.size()) {
                keyList.add(k);
            }
            super.getProgressBean().setCurrentElementForCurrentIterate(at.getAndIncrement());
        });
        return keyList;
    }

    /**
     * Permet de construire le résultat de l'analyse pour les groupes
     * @param keySet liste des clés
     * @param cartesianGroupList liste des groupes cartésien
     * @return le résultat de l'analyse pour les groupes
     */
    private AnalysisGroupDisplay constructAnalysisGroupDisplay(Set<String> keySet, List<CartesianGroup> cartesianGroupList) {
        Set<String> keyFilteredSet = getKeySetFromCartesianGroupList(keySet, cartesianGroupList);
        AnalysisResultDisplay analysisResultDisplayForNumberTokens = getAnalysisResultDisplayForNumberTokens(keyFilteredSet.stream().collect(Collectors.toList()), false);
        return new AnalysisGroupDisplayBuilder()
                .cartesianGroupSet(cartesianGroupList.stream().collect(Collectors.toSet()))
                .analysisResultDisplay(analysisResultDisplayForNumberTokens)
                .keySet(keyFilteredSet)
                .build();
    }

    /**
     * Permet de se procurer la valeur du champ pour une clé de texte
     * @param key clé du texte
     * @param field champ
     * @return la valeur
     */
    private String getValueFromKeyTextAndField(String key, String field) {
        Optional<UserStructuredText> textFromKey = UserSettings.getInstance().getTextFromKey(key);
        if (textFromKey.isPresent()) {
            return textFromKey.get().getStructuredText().getContent(field);
        }
        return StringUtils.EMPTY;
    }

    /**
     * Permet de se procurer un tuple contenant le numéro du fichier et le numéro du matériel
     * @param key clé du texte
     * @return le tuple
     */
    private Tuple2<Integer, Integer> getFileAndMaterialNumberOfText(String key) {
        Optional<UserStructuredText> textFromKey = UserSettings.getInstance().getTextFromKey(key);
        if (textFromKey.isPresent()) {
            UserStructuredText userStructuredText = textFromKey.get();
            return new Tuple2<>(userStructuredText.getDocumentNumber(), userStructuredText.getStructuredText().getNumber());
        }
        return new Tuple2<>(-1,-1);
    }
}
