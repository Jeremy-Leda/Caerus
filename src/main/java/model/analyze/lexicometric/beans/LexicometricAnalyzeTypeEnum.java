package model.analyze.lexicometric.beans;

import model.analyze.LexicometricAnalysis;
import model.analyze.lexicometric.analyze.beans.Text;

import java.util.Set;
import java.util.function.Function;

/**
 *
 * Enumeration coté serveur pour la gestion des analyse
 *
 */
public enum LexicometricAnalyzeTypeEnum {
    NUMBER_TOKENS(c -> LexicometricAnalysis.getInstance().getNumberTokensAnalyzeResult(c)),
    LEMME_TYPE(c -> LexicometricAnalysis.getInstance().getNumberTokensAnalyzeResult(c)),
    TOKEN_RATIO(c -> LexicometricAnalysis.getInstance().getNumberTokensAnalyzeResult(c)),
    FREQUENCY(c -> LexicometricAnalysis.getInstance().getNumberTokensAnalyzeResult(c));

    private final Function<LexicometricAnalyzeServerCmd, Set<Text>> analyzeServerCmdSetFunction;

    LexicometricAnalyzeTypeEnum(Function<LexicometricAnalyzeServerCmd, Set<Text>> analyzeServerCmdSetFunction) {
        this.analyzeServerCmdSetFunction = analyzeServerCmdSetFunction;
    }

    /**
     * Fonction permettant d'exécuter l'analyse sur le serveur
     * @return la function
     */
    public Function<LexicometricAnalyzeServerCmd, Set<Text>> getAnalyzeServerCmdSetFunction() {
        return analyzeServerCmdSetFunction;
    }
}
