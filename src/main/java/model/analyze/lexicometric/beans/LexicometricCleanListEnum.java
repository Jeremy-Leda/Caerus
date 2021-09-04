package model.analyze.lexicometric.beans;

import model.analyze.lexicometric.interfaces.ILexicometricData;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enumeration pour les listes lexicométriques pour la préparation du traitement ou utilisé dans le traitement
 */
public enum LexicometricCleanListEnum {
    TOKENIZATION((la, ld) -> la.setTokenizationSet(Set.of((Tokenization) ld)),
            profil -> "stopwords_" + profil + ".json",
            Tokenization.class,
            (la, ld) -> la.setTokenizationSet((Set<Tokenization>) ld),
            o -> o,
            s -> {
                Tokenization tokenization = new Tokenization();
                tokenization.setProfile(s);
                return tokenization;
            },
            s -> s.getTokenizationSet().stream().map(Tokenization::getProfile).collect(Collectors.toSet()),
            s -> s.getTokenizationSet().stream().map(x -> (ILexicometricData)x).collect(Collectors.toSet())),
    LEMMATIZATION((la, ld) -> la.setLemmatizationSet(Set.of((Lemmatization) ld)),
            profil -> "lemmatization_" + profil + ".json",
            Lemmatization.class,
            (la, ld) -> la.setLemmatizationSet((Set<Lemmatization>) ld),
            o -> mapSetNullableTreatment((Map<String, Set<String>>)o),
            s -> {
                Lemmatization lemmatization = new Lemmatization();
                lemmatization.setProfile(s);
                return lemmatization;
            },
            s -> s.getLemmatizationSet().stream().map(Lemmatization::getProfile).collect(Collectors.toSet()),
            s -> s.getLemmatizationSet().stream().map(x -> (ILexicometricData)x).collect(Collectors.toSet())),
    LEMMATIZATION_BY_GRAMMATICAL_CATEGORY((la, ld) ->
            la.setLemmatizationByGrammaticalCategorySet(Set.of((LemmatizationByGrammaticalCategory) ld)),
            profil -> "lemmatizationByGrammaticalCategory_" + profil + ".json",
            LemmatizationByGrammaticalCategory.class,
            (la, ld) -> la.setLemmatizationByGrammaticalCategorySet((Set<LemmatizationByGrammaticalCategory>) ld),
            o -> {
                Map<String, Map<String, Set<String>>> stringMapMap = mapNullableTreatment((Map<String, Map<String, Set<String>>>) o);
                stringMapMap.values().forEach(v -> mapSetNullableTreatment(v));
                return stringMapMap;
            },
            s -> {
                LemmatizationByGrammaticalCategory lemmatizationByGrammaticalCategory = new LemmatizationByGrammaticalCategory();
                lemmatizationByGrammaticalCategory.setProfile(s);
                return lemmatizationByGrammaticalCategory;
            },
            s -> s.getLemmatizationByGrammaticalCategorySet().stream().map(LemmatizationByGrammaticalCategory::getProfile).collect(Collectors.toSet()),
            s -> s.getLemmatizationByGrammaticalCategorySet().stream().map(x -> (ILexicometricData)x).collect(Collectors.toSet())),
    PROPER_NOUN((la, ld) -> la.setProperNounSet(Set.of((ProperNoun) ld)),
            profil -> "propernoun_" + profil + ".json",
            ProperNoun.class,
            (la, ld) -> la.setProperNounSet((Set<ProperNoun>) ld),
            o -> o,
            s -> {
                ProperNoun properNoun = new ProperNoun();
                properNoun.setProfile(s);
                return properNoun;
            },
            s -> s.getProperNounSet().stream().map(ProperNoun::getProfile).collect(Collectors.toSet()),
            s -> s.getProperNounSet().stream().map(x -> (ILexicometricData)x).collect(Collectors.toSet())),
    EXCLUDE_TEXTS((la, ld) -> la.setExcludeTextsSet(Set.of((ExcludeTexts) ld)),
            profil -> "excludetexts_" + profil + ".json",
            ExcludeTexts.class,
            (la, ld) -> la.setExcludeTextsSet((Set<ExcludeTexts>) ld),
            o -> o,
            s -> {
                ExcludeTexts excludeTexts = new ExcludeTexts();
                excludeTexts.setProfile(s);
                return excludeTexts;
            },
            s -> s.getExcludeTextsSet().stream().map(ExcludeTexts::getProfile).collect(Collectors.toSet()),
            s -> s.getExcludeTextsSet().stream().map(x -> (ILexicometricData)x).collect(Collectors.toSet()));

    private final BiConsumer<LexicometricAnalysis, ILexicometricData> dataSetBiConsumer;
    private final Function<String, String> nameFileFunction;
    private final Class<?> type;
    private final BiConsumer<LexicometricAnalysis, Set<?>> allDataSetBiConsumer;
    private final Function<Object, Object> cleanNullableFunction;
    private final Function<String, ILexicometricData> newInstanceProfilFunction;
    private final Function<LexicometricAnalysis, Set<String>> profilSetFunction;
    private final Function<LexicometricAnalysis, Set<ILexicometricData>> lexicometricDataSetFunction;

    /**
     * Constructeur
     * @param dataSetBiConsumer biconsumer pour l'enregistrement d'une données lexicométrique
     * @param nameFileFunction Permet de se procurer le nouveau non du fichier
     * @param type Permet de se procurer le type de l'objet
     * @param allDataSetBiConsumer Permet de sauvegarder la liste compléte des données
     * @param cleanNullableFunction
     * @param newInstanceProfilFunction
     * @param profilSetFunction
     * @param lexicometricDataSetFunction
     */
    LexicometricCleanListEnum(BiConsumer<LexicometricAnalysis, ILexicometricData> dataSetBiConsumer, Function<String, String> nameFileFunction, Class<?> type, BiConsumer<LexicometricAnalysis, Set<?>> allDataSetBiConsumer, Function<Object, Object> cleanNullableFunction, Function<String, ILexicometricData> newInstanceProfilFunction, Function<LexicometricAnalysis, Set<String>> profilSetFunction, Function<LexicometricAnalysis, Set<ILexicometricData>> lexicometricDataSetFunction) {
        this.dataSetBiConsumer = dataSetBiConsumer;
        this.nameFileFunction = nameFileFunction;
        this.type = type;
        this.allDataSetBiConsumer = allDataSetBiConsumer;
        this.cleanNullableFunction = cleanNullableFunction;
        this.newInstanceProfilFunction = newInstanceProfilFunction;
        this.profilSetFunction = profilSetFunction;
        this.lexicometricDataSetFunction = lexicometricDataSetFunction;
    }

    /**
     * Permet de se procurer le biconsumer pour l'enregistrement des données lexicométriques
     * @return
     */
    public BiConsumer<LexicometricAnalysis, ILexicometricData> getDataSetBiConsumer() {
        return dataSetBiConsumer;
    }

    /**
     * Permet de se procurer la fonction pour avoir le nom du fichier
     * @return la fonction pour avoir le nom du fichier
     */
    public Function<String, String> getNameFileFunction() {
        return nameFileFunction;
    }

    /**
     * Permet de se procurer le type de l'objet à utiliser
     * @return le type de l'objet à utiliser
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Permet de se procurer le biconsumer pour la sauvegarde compléte des données
     * @return le biconsumer pour la sauvegarde compléte des données
     */
    public BiConsumer<LexicometricAnalysis, Set<?>> getAllDataSetBiConsumer() {
        return allDataSetBiConsumer;
    }

    /**
     * Permet de se procurer la fonction pour nettoyer les listes
     * @return la fonction pour nettoyer les listes
     */
    public Function<Object, Object> getCleanNullableFunction() {
        return cleanNullableFunction;
    }

    /**
     * Fonction pour la création d'une nouvelle instance de données avec un nouveau profil
     * @return la fonction
     */
    public Function<String, ILexicometricData> getNewInstanceProfilFunction() {
        return newInstanceProfilFunction;
    }

    /**
     * Permet de traiter une map de map pour gérer les nulls
     * @param map map
     * @param <T> Type d'objet
     */
    private static <T> Map<String, Map<String,T>> mapNullableTreatment(Map<String, Map<String,T>> map) {
        Set<String> keyForValueNotInitializedSet = map.entrySet().stream().filter(entry -> Objects.isNull(entry.getValue())).map(entry -> entry.getKey()).collect(Collectors.toSet());
        keyForValueNotInitializedSet.forEach(key -> map.put(key, new HashMap<>()));
        return map;
    }

    /**
     * Permet de traiter une map de set pour gérer les nulls
     * @param map map
     * @param <T> Type d'objet
     */
    private static <T> Map<String, Set<T>> mapSetNullableTreatment(Map<String, Set<T>> map) {
        Set<String> keyForValueNotInitializedSet = map.entrySet().stream().filter(entry -> Objects.isNull(entry.getValue())).map(entry -> entry.getKey()).collect(Collectors.toSet());
        keyForValueNotInitializedSet.forEach(key -> map.put(key, new HashSet<>()));
        return map;
    }

    /**
     * Permet de se procurer la fonction pour avoir la liste des profils
     * @return la fonction pour avoir la liste des profils
     */
    public Function<LexicometricAnalysis, Set<String>> getProfilSetFunction() {
        return profilSetFunction;
    }

    /**
     * Permet de se procurer la fonction pour la liste des données
     * @return la fonction pour la liste des données
     */
    public Function<LexicometricAnalysis, Set<ILexicometricData>> getLexicometricDataSetFunction() {
        return lexicometricDataSetFunction;
    }
}
