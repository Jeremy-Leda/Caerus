package view.beans;

import controler.IConfigurationControler;
import model.analyze.lexicometric.beans.LexicometricAnalyzeServerCmdBuilder;
import model.analyze.lexicometric.beans.LexicometricAnalyzeTypeEnum;
import view.analysis.beans.AnalysisResultDisplay;
import view.interfaces.ILexicometricListApplyChoosePanel;
import view.interfaces.IWizardPanel;
import view.panel.LexicometricListApplyChoosePanel;
import view.services.ExecutionService;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 * Enumération pour se procurer la liste des types d'analyses disponible
 *
 */
public enum LexicometricAnalyzeTypeViewEnum {
    NUMBER_TOKENS(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_NUMBER_LABEL), wizard -> Optional.ofNullable(new LexicometricListApplyChoosePanel(wizard,false, false, true)),
            (controler, lexicometricAnalyzeCmd) -> controler.launchLexicometricAnalyze(
                    new LexicometricAnalyzeServerCmdBuilder()
                            .lexicometricAnalyzeTypeEnum(LexicometricAnalyzeTypeEnum.NUMBER_TOKENS)
                            .keyTextFilteredList(lexicometricAnalyzeCmd.getKeyTextFilteredList())
                            .fieldToAnalyzeSet(lexicometricAnalyzeCmd.getFieldToAnalyzeSet())
                            .preTreatmentListLexicometricMap(lexicometricAnalyzeCmd.toPreTreatmentServerMap())
                            .build()),
            cmd -> getResultDisplay(LexicometricAnalyzeTypeEnum.NUMBER_TOKENS, cmd), LexicometricAnalyzeTypeEnum.NUMBER_TOKENS),
    FREQUENCY(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_FREQUENCY_LABEL), wizard -> Optional.ofNullable(new LexicometricListApplyChoosePanel(wizard,false, false, true)),
            (controler, lexicometricAnalyzeCmd) -> controler.launchLexicometricAnalyze(
                    new LexicometricAnalyzeServerCmdBuilder()
                            .lexicometricAnalyzeTypeEnum(LexicometricAnalyzeTypeEnum.FREQUENCY)
                            .keyTextFilteredList(lexicometricAnalyzeCmd.getKeyTextFilteredList())
                            .fieldToAnalyzeSet(lexicometricAnalyzeCmd.getFieldToAnalyzeSet())
                            .preTreatmentListLexicometricMap(lexicometricAnalyzeCmd.toPreTreatmentServerMap())
                            .build()),
            cmd -> getResultDisplay(LexicometricAnalyzeTypeEnum.FREQUENCY, cmd), LexicometricAnalyzeTypeEnum.FREQUENCY),
    LEMME_TYPE(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_LEMME_TYPE_LABEL), wizard -> Optional.ofNullable(new LexicometricListApplyChoosePanel(wizard,false, true, true)),
            (controler, lexicometricAnalyzeCmd) -> controler.launchLexicometricAnalyze(
                new LexicometricAnalyzeServerCmdBuilder()
                        .lexicometricAnalyzeTypeEnum(LexicometricAnalyzeTypeEnum.LEMME_TYPE)
                        .keyTextFilteredList(lexicometricAnalyzeCmd.getKeyTextFilteredList())
                        .preTreatmentListLexicometricMap(lexicometricAnalyzeCmd.toPreTreatmentServerMap())
                        .fieldToAnalyzeSet(lexicometricAnalyzeCmd.getFieldToAnalyzeSet())
                        .build()),
            cmd -> getResultDisplay(LexicometricAnalyzeTypeEnum.LEMME_TYPE, cmd), LexicometricAnalyzeTypeEnum.LEMME_TYPE),
    TOKEN_RATIO(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_RATIO_LABEL), wizard -> Optional.ofNullable(new LexicometricListApplyChoosePanel(wizard,true, true, true)),
            (controler, lexicometricAnalyzeCmd) -> controler.launchLexicometricAnalyze(
                    new LexicometricAnalyzeServerCmdBuilder()
                            .lexicometricAnalyzeTypeEnum(LexicometricAnalyzeTypeEnum.TOKEN_RATIO)
                            .keyTextFilteredList(lexicometricAnalyzeCmd.getKeyTextFilteredList())
                            .preTreatmentListLexicometricMap(lexicometricAnalyzeCmd.toPreTreatmentServerMap())
                            .fieldToAnalyzeSet(lexicometricAnalyzeCmd.getFieldToAnalyzeSet())
                            .build()),
            cmd -> getResultDisplay(LexicometricAnalyzeTypeEnum.TOKEN_RATIO, cmd), LexicometricAnalyzeTypeEnum.TOKEN_RATIO);


    private final String label;
    private final Function<IWizardPanel, Optional<ILexicometricListApplyChoosePanel>> optionalPanel;
    private final BiConsumer<IConfigurationControler, LexicometricAnalyzeCmd> biConsumerAnalysis;
    private final Function<LexicometricAnalyzeCmd, AnalysisResultDisplay> functionDisplayResult;
    private final LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum;
    private static ExecutionService executionService = new ExecutionService();

    LexicometricAnalyzeTypeViewEnum(String label, Function<IWizardPanel, Optional<ILexicometricListApplyChoosePanel>> optionalPanel,
                                    BiConsumer<IConfigurationControler, LexicometricAnalyzeCmd> biConsumerAnalysis,
                                    Function<LexicometricAnalyzeCmd, AnalysisResultDisplay> functionDisplayResult, LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum) {
        this.label = label;
        this.optionalPanel = optionalPanel;
        this.biConsumerAnalysis = biConsumerAnalysis;
        this.functionDisplayResult = functionDisplayResult;
        this.lexicometricAnalyzeTypeEnum = lexicometricAnalyzeTypeEnum;
    }

    /**
     * Permet de se procurer le libellé de l'analyses
     * @return le libellé de l'analyses
     */
    public String getLabel() {
        return label;
    }

    /**
     * Permet de se procurer le panel optional si il existe
     * @return le panel optional si il existe
     */
    public Function<IWizardPanel, Optional<ILexicometricListApplyChoosePanel>> getOptionalPanel() {
        return optionalPanel;
    }

    /**
     * Permet de se procurer le bi consumer pour lancer l'analyse
     * @return le bi consumer pour lancer l'analyse
     */
    public BiConsumer<IConfigurationControler, LexicometricAnalyzeCmd> getBiConsumerAnalysis() {
        return biConsumerAnalysis;
    }

    /**
     * Permet de se procurer le bi consumer pour l'affichage du résultat
     * En paramètre le controller et la commande
     * @return le bi consumer pour l'affichage du résultat
     */
    public Function<LexicometricAnalyzeCmd, AnalysisResultDisplay> getFunctionDisplayResult() {
        return functionDisplayResult;
    }

    /**
     * Permet de se procurer l'enumération optionelle en fonction du libellé
     * @param label libellé
     * @return l'enumération optionelle en fonction du libellé
     */
    public static Optional<LexicometricAnalyzeTypeViewEnum> fromLabel(String label) {
        return Arrays.stream(values()).filter(e -> e.getLabel().equals(label)).findFirst();
    }

    /**
     * Permet d'ouvrir la fenêtre des résultats
     * @param lexicometricAnalyzeTypeEnum type d'analyse
     * @param cmd commande
     * @return
     */
    private static AnalysisResultDisplay getResultDisplay(LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum, LexicometricAnalyzeCmd cmd) {
        return lexicometricAnalyzeTypeEnum.getAnalysisResultDisplayFunction().apply(cmd.getKeyTextFilteredList());
//        Try.of(() -> lexicometricAnalyzeTypeEnum.getAnalysisResultDisplayFunction().apply(cmd.getKeyTextFilteredList()))
//                .fold(s -> s, analysisResultDisplay -> new AnalysisTokenResultWindow(controler, analysisResultDisplay, cmd, lexicometricAnalyzeTypeEnum));
    }

    /**
     * Permet de se procurer l'enum serveur
     * @return l'enum serveur
     */
    public LexicometricAnalyzeTypeEnum getLexicometricAnalyzeTypeEnum() {
        return lexicometricAnalyzeTypeEnum;
    }
}
