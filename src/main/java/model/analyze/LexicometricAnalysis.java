package model.analyze;

import model.analyze.beans.UserStructuredText;
import model.analyze.constants.FolderSettingsEnum;
import model.analyze.lexicometric.analyze.beans.Text;
import model.analyze.lexicometric.analyze.beans.Token;
import model.analyze.lexicometric.beans.LexicometricAnalyzeServerCmd;
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
        Map<String, Map<String, Long>> keyTextMap = cmd.getKeyTextFilteredList().stream()
                .collect(Collectors.toMap(Function.identity(), s -> getNbTokensOfText(s, cmd.getFieldToAnalyzeSet())));
        analysisResultSet.addAll(keyTextMap.entrySet().stream().map(this::getTextFromNumberTokensEntry).collect(Collectors.toSet()));
    }

    /**
     * Permet de se procurer le nombre de tokens dans un texte
     * => Renvois une map qui détient en clé un mot et en valeur son nombre total d'apparition
     * @param keyText Clé du texte
     * @param fieldSet Liste des champs à analyser
     * @return une map qui détient en clé un mot et en valeur son nombre total d'apparition
     */
    private Map<String, Long> getNbTokensOfText(String keyText, Set<String> fieldSet) {
        UserSettings.getInstance().loadFilteredText(keyText);
        String textToAnalyze = fieldSet.stream()
                .map(f -> UserSettings.getInstance().getFieldInEditingCorpus(f)).reduce((s1, s2) -> s1 + StringUtils.SPACE + s2)
                .orElse(StringUtils.EMPTY);
        List<String> cleanWords = Arrays.stream(StringUtils.split(textToAnalyze))
                .map(s -> s.replaceAll("[^A-Za-z0-9]", ""))
                .map(s -> s.toLowerCase())
                .filter(StringUtils::isNotBlank).collect(Collectors.toList());
        return cleanWords.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
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
        Set<Text> textSet = this.analysisResultSet.stream().filter(t -> keyTextFilteredList.contains(t.getKey())).collect(Collectors.toSet());
        Text text = textSet.stream().reduce(this::reduceText).orElse(new Text(StringUtils.EMPTY));
        return convertTextToAnalysisResultDisplay(text);
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
        return new AnalysisTokenDisplayBuilder()
                .word(token.getWord())
                .nbOcurrency(token.getNbOcurrency())
                .build();
    }
}
