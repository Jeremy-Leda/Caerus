package model.analyze;

import io.vavr.Tuple2;
import model.abstracts.ProgressAbstract;
import model.analyze.beans.CartesianGroup;
import model.analyze.beans.CartesianGroupBuilder;
import model.analyze.beans.UserStructuredText;
import model.analyze.cmd.AnalysisDetailResultDisplayCmd;
import model.analyze.lexicometric.analyze.beans.AnalyzeResultToken;
import model.analyze.lexicometric.analyze.beans.Text;
import model.analyze.lexicometric.analyze.beans.Token;
import model.analyze.lexicometric.beans.LexicometricAnalyzeServerCmd;
import model.analyze.lexicometric.beans.LexicometricCleanListEnum;
import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;
import model.analyze.lexicometric.interfaces.ILexicometricData;
import model.exceptions.ErrorCode;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import org.apache.commons.collections4.CollectionUtils;
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
    private final Set<String> analysisExcludeTextsKeySet = new HashSet<>();

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
        Collection<String> tokenCleanedCollection = getTokenCleaned(keyText, fieldSet, preTreatmentListLexicometricMap);
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
        Collection<String> tokenCleanedCollection = getTokenCleaned(keyText, fieldSet, preTreatmentListLexicometricMap);
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
     * @param preTreatmentListLexicometricMap map pour le prétraitement
     * @return la liste des tokens nettoyés
     */
    private Collection<String> getTokenCleaned(String keyText, Set<String> fieldSet, Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap) {
        Optional<UserStructuredText> textFromKey = UserSettings.getInstance().getTextFromKey(keyText);
        String textToAnalyze = fieldSet.stream()
                .map(f -> textFromKey.get().getStructuredText().getContent(f)).reduce((s1, s2) -> s1 + StringUtils.SPACE + s2)
                .orElse(StringUtils.EMPTY);
        if (preTreatmentListLexicometricMap.containsKey(LexicometricConfigurationEnum.EXCLUDE_TEXTS)) {
            Set<String> properNounToRemoveSet = getExcludeTextsToRemoveSet(preTreatmentListLexicometricMap.get(LexicometricConfigurationEnum.EXCLUDE_TEXTS));
            String textToControl = textToAnalyze.replaceAll("\\s+",StringUtils.SPACE);
            if (properNounToRemoveSet.contains(textToControl)) {
                analysisExcludeTextsKeySet.add(keyText);
            }
        }
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
        AtomicInteger nbTreatElement = new AtomicInteger(1);
        super.getProgressBean().setNbMaxElementForCurrentIterate(cmd.getKeyTextFilteredList().size());
        Set<AnalysisDetailResultDisplay> detailResultDisplaySet = cmd.getKeyTextFilteredList().stream()
                .map(k -> transformKeyTextToDisplayResult(nbTreatElement, cmd, k))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(AnalysisDetailResultDisplay::getFileNumber).thenComparing(AnalysisDetailResultDisplay::getMaterialNumber))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        super.resetProgress();
        return detailResultDisplaySet;
    }

    private AnalysisDetailResultDisplay transformKeyTextToDisplayResult(AtomicInteger nbTreatElement, AnalysisDetailResultDisplayCmd cmd, String k) {
        if (treatmentIsCancelled()) {
            return null;
        }
        Set<AnalysisResultDisplay> analysisResultDisplayForNumberTokensSet = getAnalysisResultDisplayForNumberTokens(List.of(k), false);
        AnalysisResultDisplay analysisResultDisplayForNumberTokens = getUniqueResult(analysisResultDisplayForNumberTokensSet,
                "Le résultat de l'analyse ne peut être supérieure à 1 dans le cadre d'une consultation au détail");
        Map<String, String> fieldValueMap = cmd.getKeyFieldSet().stream()
                .map(f -> new Tuple2<>(f, getTextPreTreatment(getValueFromKeyTextAndField(k, f), cmd.getPreTreatmentListLexicometricMap())))
                .collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));
        analysisResultDisplayForNumberTokens.setKey(k);
        Tuple2<Integer, Integer> fileAndMaterialNumberOfText = getFileAndMaterialNumberOfText(k);
        super.getProgressBean().setCurrentElementForCurrentIterate(nbTreatElement.getAndIncrement());
        return new AnalysisDetailResultDisplayBuilder()
                .analysisResultDisplay(analysisResultDisplayForNumberTokens)
                .fieldValueMap(fieldValueMap)
                .fileNumber(fileAndMaterialNumberOfText._1)
                .materialNumber(fileAndMaterialNumberOfText._2)
                .build();
    }

    private AnalysisResultDisplay getUniqueResult(Set<AnalysisResultDisplay> analysisResultDisplayForNumberTokensSet, String errorMessage) {
        AnalysisResultDisplay defaultAnalysisResultDisplay = new AnalysisResultDisplayBuilder()
                .key(StringUtils.EMPTY)
                .excludeTexts(false)
                .analysisTokenDisplaySet(Collections.emptySet())
                .keySet(Collections.emptySet())
                .nbToken(0)
                .nbOccurrency(0L)
                .build();
        if (analysisResultDisplayForNumberTokensSet.size() > 1) {
            throw new ServerException().addInformationException(new InformationExceptionBuilder()
                    .errorCode(ErrorCode.TECHNICAL_ERROR)
                    .parameters(Set.of(errorMessage))
                    .build());
        }
        return analysisResultDisplayForNumberTokensSet.stream().findFirst().orElse(defaultAnalysisResultDisplay);
    }



    /**
     * Permet de se procurer le résultat de l'analyse
     * @param keyTextFilteredList liste des clés à récupérer
     * @return le résultat de l'analyse
     */
    public Set<AnalysisResultDisplay> getAnalysisResultDisplayForNumberTokens(List<String> keyTextFilteredList, Boolean driveProgress) {
        if (driveProgress) {
            super.createProgressBean(1);
            super.getProgressBean().setCurrentIterate(1);
        }
        Set<Text> textSetWithoutExludeTexts = this.analysisResultSet.stream().filter(t -> keyTextFilteredList.contains(t.getKey())).filter(t -> !this.analysisExcludeTextsKeySet.contains(t.getKey())).collect(Collectors.toSet());
        AnalyzeResultToken resultWithoutExcludeTexts = new AnalyzeResultToken(textSetWithoutExludeTexts, false);
        Set<Text> textSetWithExludeTexts = this.analysisResultSet.stream().filter(t -> this.analysisExcludeTextsKeySet.contains(t.getKey())).filter(t -> keyTextFilteredList.contains(t.getKey())).collect(Collectors.toSet());
        AnalyzeResultToken resultOnlyExcludeTexts = new AnalyzeResultToken(textSetWithExludeTexts, true);

        Set<String> wordSetWithoutExcludeTexts = resultWithoutExcludeTexts.getWordSet();
        List<Token> tokenListWithoutExcludeTexts = resultWithoutExcludeTexts.getTokenList();
        Set<String> wordSetOnlyExcludeTexts = resultOnlyExcludeTexts.getWordSet();
        List<Token> tokenListOnlyExcludeTexts = resultOnlyExcludeTexts.getTokenList();
        Integer maxCurrentProgress = wordSetWithoutExcludeTexts.size() + wordSetOnlyExcludeTexts.size();
        if (driveProgress) {
            super.getProgressBean().setNbMaxElementForCurrentIterate(maxCurrentProgress);
        }
        AtomicInteger at = new AtomicInteger(1);
        Set<String> keyWithoutExcludeTextSet = new HashSet<>();
        keyWithoutExcludeTextSet.addAll(keyTextFilteredList);
        keyWithoutExcludeTextSet.removeAll(this.analysisExcludeTextsKeySet);
        AnalysisResultDisplay globalResult = getAnalysisResultDisplayForNumberTokens(wordSetWithoutExcludeTexts, tokenListWithoutExcludeTexts, at, driveProgress, false, keyWithoutExcludeTextSet);
        AnalysisResultDisplay excludeTextsResult = getAnalysisResultDisplayForNumberTokens(wordSetOnlyExcludeTexts, tokenListOnlyExcludeTexts, at, driveProgress, true, new HashSet<>(CollectionUtils.intersection(keyTextFilteredList, this.analysisExcludeTextsKeySet)));
        if (driveProgress) {
            super.resetProgress();
        }
        return Set.of(globalResult, excludeTextsResult).stream().filter(s -> s.getNbOccurrency() > 0).collect(Collectors.toSet());
    }

    private AnalysisResultDisplay getAnalysisResultDisplayForNumberTokens(Set<String> wordSet, List<Token> tokenList, AtomicInteger atProgress, Boolean driveProgress, boolean isExcludeTexts, Set<String> keySet) {
        Text text = new Text(StringUtils.EMPTY);
        Set<Token> tokenSet = wordSet.parallelStream().map(w -> {
            if (treatmentIsCancelled()) {
                return new Token(StringUtils.EMPTY);
            }
            Long nbOccurrency = tokenList.parallelStream().filter(t -> t.getWord().equals(w)).map(t -> t.getNbOcurrency()).reduce(Long::sum).orElse(0L);
            Token token = new Token(w);
            token.setNbOcurrency(nbOccurrency);
            if (driveProgress) {
                super.getProgressBean().setCurrentElementForCurrentIterate(atProgress.getAndIncrement());
            }
            return token;
        }).collect(Collectors.toSet());
        text.getTokenSet().addAll(tokenSet);
        return convertTextToAnalysisResultDisplay(text, isExcludeTexts, keySet);
    }

    /**
     * Permet de convertir un texte en résultat d'analyse
     * @param textToConvert texte à convertir
     * @return le résultat d'analyse
     */
    private AnalysisResultDisplay convertTextToAnalysisResultDisplay(Text textToConvert, boolean isExcludeTexts, Set<String> keySet) {
        Set<AnalysisTokenDisplay> analysisTokenDisplaySet = textToConvert.getTokenSet()
                .stream()
                .map(this::convertTokenToAnalysisTokenDisplay)
                .collect(Collectors.toSet());
        AnalysisResultDisplay analysisResultDisplay = new AnalysisResultDisplayBuilder()
                .key(textToConvert.getKey())
                .analysisTokenDisplaySet(analysisTokenDisplaySet)
                .nbOccurrency(analysisTokenDisplaySet.stream().map(AnalysisTokenDisplay::getNbOcurrency).reduce(Long::sum).orElse(0L))
                .nbToken(analysisTokenDisplaySet.size())
                .excludeTexts(isExcludeTexts)
                .keySet(keySet)
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

    private Set<String> getExcludeTextsToRemoveSet(String profil) {
        Optional<ILexicometricData> optionalILexicometricData = UserLexicometricAnalysisSettings.getInstance().getDataSet(LexicometricCleanListEnum.EXCLUDE_TEXTS).stream().filter(s -> s.getProfile().equals(profil)).findFirst();
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
                    if (treatmentIsCancelled()) {
                        return null;
                    }
                    super.getProgressBean().setCurrentIterate(at.getAndIncrement());
                    return constructAnalysisGroupDisplay(keySet, s);
                })
                .filter(Objects::nonNull)
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
        Set<AnalysisResultDisplay> analysisResultDisplayForNumberTokensSet = getAnalysisResultDisplayForNumberTokens(keyFilteredSet.stream().collect(Collectors.toList()), false);
        AnalysisResultDisplay analysisResultDisplayForNumberTokens = getUniqueResult(analysisResultDisplayForNumberTokensSet,
                "Le résultat de l'analyse ne peut être supérieure à 1 dans le cadre d'un regroupement");
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
