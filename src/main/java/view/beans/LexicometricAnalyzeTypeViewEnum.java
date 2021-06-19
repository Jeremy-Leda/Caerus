package view.beans;

import controler.IConfigurationControler;
import model.analyze.lexicometric.beans.LexicometricAnalyzeServerCmdBuilder;
import model.analyze.lexicometric.beans.LexicometricAnalyzeTypeEnum;
import view.analysis.beans.AnalysisResultDisplay;
import view.interfaces.ILexicometricListApplyChoosePanel;
import view.interfaces.IWizardPanel;
import view.panel.LexicometricListApplyChoosePanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;
import view.windows.AnalysisTokenResultWindow;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 * Enumération pour se procurer la liste des types d'analyses disponible
 *
 */
public enum LexicometricAnalyzeTypeViewEnum {
    NUMBER_TOKENS(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_NUMBER_LABEL), wizard -> Optional.empty(),
            (controler, lexicometricAnalyzeCmd) -> controler.launchLexicometricAnalyze(
                    new LexicometricAnalyzeServerCmdBuilder()
                            .lexicometricAnalyzeTypeEnum(LexicometricAnalyzeTypeEnum.NUMBER_TOKENS)
                            .keyTextFilteredList(lexicometricAnalyzeCmd.getKeyTextFilteredList())
                            .fieldToAnalyzeSet(lexicometricAnalyzeCmd.getFieldToAnalyzeSet())
                            .build()),
            ((controler, keys) -> new AnalysisTokenResultWindow(controler, LexicometricAnalyzeTypeEnum.NUMBER_TOKENS.getAnalysisResultDisplayFunction().apply(keys)))),
    LEMME_TYPE(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_LEMME_TYPE_LABEL), wizard -> Optional.ofNullable(new LexicometricListApplyChoosePanel(wizard,false, true)),
            (controler, lexicometricAnalyzeCmd) -> controler.launchLexicometricAnalyze(
                new LexicometricAnalyzeServerCmdBuilder()
                        .lexicometricAnalyzeTypeEnum(LexicometricAnalyzeTypeEnum.LEMME_TYPE)
                        .keyTextFilteredList(lexicometricAnalyzeCmd.getKeyTextFilteredList())
                        .preTreatmentListLexicometricMap(lexicometricAnalyzeCmd.toPreTreatmentServerMap())
                        .fieldToAnalyzeSet(lexicometricAnalyzeCmd.getFieldToAnalyzeSet())
                        .build()),
            ((controler, keys) -> new AnalysisTokenResultWindow(controler, LexicometricAnalyzeTypeEnum.LEMME_TYPE.getAnalysisResultDisplayFunction().apply(keys)))),
    TOKEN_RATIO(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_RATIO_LABEL), wizard -> Optional.ofNullable(new LexicometricListApplyChoosePanel(wizard,true, true)),
            (controler, lexicometricAnalyzeCmd) -> controler.launchLexicometricAnalyze(
                    new LexicometricAnalyzeServerCmdBuilder()
                            .lexicometricAnalyzeTypeEnum(LexicometricAnalyzeTypeEnum.TOKEN_RATIO)
                            .keyTextFilteredList(lexicometricAnalyzeCmd.getKeyTextFilteredList())
                            .preTreatmentListLexicometricMap(lexicometricAnalyzeCmd.toPreTreatmentServerMap())
                            .fieldToAnalyzeSet(lexicometricAnalyzeCmd.getFieldToAnalyzeSet())
                            .build()),
            ((controler, keys) -> new AnalysisTokenResultWindow(controler, LexicometricAnalyzeTypeEnum.TOKEN_RATIO.getAnalysisResultDisplayFunction().apply(keys)))),
    FREQUENCY(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_FREQUENCY_LABEL), wizard -> Optional.empty(),
            (controler, lexicometricAnalyzeCmd) -> controler.launchLexicometricAnalyze(
                    new LexicometricAnalyzeServerCmdBuilder()
                            .lexicometricAnalyzeTypeEnum(LexicometricAnalyzeTypeEnum.FREQUENCY)
                            .keyTextFilteredList(lexicometricAnalyzeCmd.getKeyTextFilteredList())
                            .fieldToAnalyzeSet(lexicometricAnalyzeCmd.getFieldToAnalyzeSet())
                            .build()),
            ((controler, keys) -> new AnalysisTokenResultWindow(controler, LexicometricAnalyzeTypeEnum.FREQUENCY.getAnalysisResultDisplayFunction().apply(keys))));


    private final String label;
    private final Function<IWizardPanel, Optional<ILexicometricListApplyChoosePanel>> optionalPanel;
    private final BiConsumer<IConfigurationControler, LexicometricAnalyzeCmd> biConsumerAnalysis;
    private final BiConsumer<IConfigurationControler, List<String>> biConsumerDisplayResult;

    LexicometricAnalyzeTypeViewEnum(String label, Function<IWizardPanel, Optional<ILexicometricListApplyChoosePanel>> optionalPanel, BiConsumer<IConfigurationControler, LexicometricAnalyzeCmd> biConsumerAnalysis, BiConsumer<IConfigurationControler, List<String>> biConsumerDisplayResult) {
        this.label = label;
        this.optionalPanel = optionalPanel;
        this.biConsumerAnalysis = biConsumerAnalysis;
        this.biConsumerDisplayResult = biConsumerDisplayResult;
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
     * En paramètre le controller et la liste des clés à afficher
     * @return le bi consumer pour l'affichage du résultat
     */
    public BiConsumer<IConfigurationControler, List<String>> getBiConsumerDisplayResult() {
        return biConsumerDisplayResult;
    }

    /**
     * Permet de se procurer l'enumération optionelle en fonction du libellé
     * @param label libellé
     * @return l'enumération optionelle en fonction du libellé
     */
    public static Optional<LexicometricAnalyzeTypeViewEnum> fromLabel(String label) {
        return Arrays.stream(values()).filter(e -> e.getLabel().equals(label)).findFirst();
    }
}
