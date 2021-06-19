package model.analyze.lexicometric.beans;

import model.analyze.LexicometricAnalysis;
import view.analysis.beans.AnalysisResultDisplay;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * Enumeration coté serveur pour la gestion des analyse
 *
 */
public enum LexicometricAnalyzeTypeEnum {
    NUMBER_TOKENS(c -> LexicometricAnalysis.getInstance().executeNumberTokensAnalyze(c), keys -> LexicometricAnalysis.getInstance().getAnalysisResultDisplayForNumberTokens(keys)),
    LEMME_TYPE(c -> LexicometricAnalysis.getInstance().executeNumberTokensAnalyze(c), keys -> LexicometricAnalysis.getInstance().getAnalysisResultDisplayForNumberTokens(keys)),
    TOKEN_RATIO(c -> LexicometricAnalysis.getInstance().executeNumberTokensAnalyze(c), keys -> LexicometricAnalysis.getInstance().getAnalysisResultDisplayForNumberTokens(keys)),
    FREQUENCY(c -> LexicometricAnalysis.getInstance().executeNumberTokensAnalyze(c), keys -> LexicometricAnalysis.getInstance().getAnalysisResultDisplayForNumberTokens(keys));

    private final Consumer<LexicometricAnalyzeServerCmd> analyzeServerCmdConsumer;
    private final Function<List<String>, AnalysisResultDisplay> analysisResultDisplayFunction;

    LexicometricAnalyzeTypeEnum(Consumer<LexicometricAnalyzeServerCmd> analyzeServerCmdConsumer, Function<List<String>, AnalysisResultDisplay> analysisResultDisplayFunction) {
        this.analyzeServerCmdConsumer = analyzeServerCmdConsumer;
        this.analysisResultDisplayFunction = analysisResultDisplayFunction;
    }

    /**
     * Consumer permettant d'exécuter l'analyse sur le serveur
     * @return le consumer
     */
    public Consumer<LexicometricAnalyzeServerCmd> getAnalyzeServerCmdConsumer() {
        return analyzeServerCmdConsumer;
    }

    /**
     * Function pour se procurer le résultat de l'analyse
     * @return la function pour se procurer le résultat de l'analyse
     */
    public Function<List<String>, AnalysisResultDisplay> getAnalysisResultDisplayFunction() {
        return analysisResultDisplayFunction;
    }
}
