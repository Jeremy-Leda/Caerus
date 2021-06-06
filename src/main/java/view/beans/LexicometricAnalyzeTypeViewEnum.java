package view.beans;

import controler.IConfigurationControler;
import view.interfaces.IAccessPanel;
import view.panel.LemmeApplyChoosePanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 *
 * Enumération pour se procurer la liste des types d'analyses disponible
 *
 */
public enum LexicometricAnalyzeTypeViewEnum {
    NUMBER_TOKENS(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_NUMBER_LABEL), Optional.empty(), Optional.empty()),
    LEMME_TYPE(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_LEMME_TYPE_LABEL), Optional.ofNullable(new LemmeApplyChoosePanel()), Optional.empty()),
    TOKEN_RATIO(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_RATIO_LABEL), Optional.ofNullable(new LemmeApplyChoosePanel()), Optional.empty()),
    FREQUENCY(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_FREQUENCY_LABEL), Optional.empty(), Optional.empty());


    private final String label;
    private final Optional<IAccessPanel> optionalPanel;
    private final Optional<BiConsumer<IConfigurationControler, LexicometricAnalyzeCmd>> biConsumerAnalysis;

    LexicometricAnalyzeTypeViewEnum(String label, Optional<IAccessPanel> optionalPanel, Optional<BiConsumer<IConfigurationControler, LexicometricAnalyzeCmd>> biConsumerAnalysis) {
        this.label = label;
        this.optionalPanel = optionalPanel;
        this.biConsumerAnalysis = biConsumerAnalysis;
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
    public Optional<IAccessPanel> getOptionalPanel() {
        return optionalPanel;
    }

    /**
     * Permet de se procurer le bi consumer pour lancer l'analyse
     * @return le bi consumer pour lancer l'analyse
     */
    public Optional<BiConsumer<IConfigurationControler, LexicometricAnalyzeCmd>> getBiConsumerAnalysis() {
        return biConsumerAnalysis;
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
