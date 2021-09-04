package model.analyze.lexicometric.beans;

import model.analyze.LexicometricAnalysis;
import model.analyze.cmd.AnalysisDetailResultDisplayCmd;
import view.analysis.beans.AnalysisDetailResultDisplay;
import view.analysis.beans.AnalysisResultDisplay;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * Enumeration coté serveur pour la gestion des analyse
 *
 */
public enum LexicometricAnalyzeTypeEnum {
    NUMBER_TOKENS(c -> LexicometricAnalysis.getInstance().executeNumberTokensAnalyze(c),
            keys -> LexicometricAnalysis.getInstance().getAnalysisResultDisplayForNumberTokens(keys, true),
            cmd -> LexicometricAnalysis.getInstance().getAnalysisDetailResultDisplayForNumberTokens(cmd)),
    LEMME_TYPE(c -> LexicometricAnalysis.getInstance().executeNumberTokensAnalyze(c),
            keys -> LexicometricAnalysis.getInstance().getAnalysisResultDisplayForNumberTokens(keys, true),
            cmd -> LexicometricAnalysis.getInstance().getAnalysisDetailResultDisplayForNumberTokens(cmd)),
    TOKEN_RATIO(c -> LexicometricAnalysis.getInstance().executeNumberTokensAnalyze(c),
            keys -> LexicometricAnalysis.getInstance().getAnalysisResultDisplayForNumberTokens(keys, true),
            cmd -> LexicometricAnalysis.getInstance().getAnalysisDetailResultDisplayForNumberTokens(cmd)),
    FREQUENCY(c -> LexicometricAnalysis.getInstance().executeNumberTokensAnalyze(c),
            keys -> LexicometricAnalysis.getInstance().getAnalysisResultDisplayForNumberTokens(keys, true),
            cmd -> LexicometricAnalysis.getInstance().getAnalysisDetailResultDisplayForNumberTokens(cmd));

    private final Consumer<LexicometricAnalyzeServerCmd> analyzeServerCmdConsumer;
    private final Function<List<String>, Set<AnalysisResultDisplay>> analysisResultDisplayFunction;
    private final Function<AnalysisDetailResultDisplayCmd, Set<AnalysisDetailResultDisplay>> analysisDetailResultDisplayCmdSetFunction;

    LexicometricAnalyzeTypeEnum(Consumer<LexicometricAnalyzeServerCmd> analyzeServerCmdConsumer, Function<List<String>, Set<AnalysisResultDisplay>> analysisResultDisplayFunction, Function<AnalysisDetailResultDisplayCmd, Set<AnalysisDetailResultDisplay>> analysisDetailResultDisplayCmdSetFunction) {
        this.analyzeServerCmdConsumer = analyzeServerCmdConsumer;
        this.analysisResultDisplayFunction = analysisResultDisplayFunction;
        this.analysisDetailResultDisplayCmdSetFunction = analysisDetailResultDisplayCmdSetFunction;
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
    public Function<List<String>, Set<AnalysisResultDisplay>> getAnalysisResultDisplayFunction() {
        return analysisResultDisplayFunction;
    }

    /**
     * Permet de se procurer la fonction pour avoir un set de détail de résultat
     * @return la fonction pour avoir un set de détail de résultat
     */
    public Function<AnalysisDetailResultDisplayCmd, Set<AnalysisDetailResultDisplay>> getAnalysisDetailResultDisplayCmdSetFunction() {
        return analysisDetailResultDisplayCmdSetFunction;
    }
}
