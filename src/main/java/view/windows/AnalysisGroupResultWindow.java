package view.windows;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.analysis.beans.AnalysisGroupDisplay;
import view.beans.LexicometricAnalyzeCmd;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
import view.interfaces.IAddAnalysisGroupDisplay;
import view.interfaces.ICheckBoxPanel;
import view.interfaces.IInformationPanel;
import view.panel.ActionPanel;
import view.panel.CheckBoxPanel;
import view.panel.InformationPanel;
import view.utils.ConfigurationUtils;

import javax.swing.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static view.utils.Constants.*;

/**
 * Fenêtre pour créer des résultats regroupés
 */
public class AnalysisGroupResultWindow extends ModalJFrameAbstract {

    private final ICheckBoxPanel checkBoxFieldsPanel;
    private final IInformationPanel informationPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
            ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_INFORMATION_MESSAGE_PANEL_LABEL),
            getMessage(WINDOW_ANALYSIS_RESULT_GROUP_INFORMATION_MESSAGE),
            false,
            false);
    private final Map<Integer, String> fieldNumberCheckBoxMap = new HashMap<>();
    private final JPanel content = new JPanel();
    private final IActionPanel actionPanel = new ActionPanel(1);
    private final IAddAnalysisGroupDisplay addAnalysisGroupDisplay;
    private final LexicometricAnalyzeCmd cmd;

    public AnalysisGroupResultWindow(IConfigurationControler configurationControler, IAddAnalysisGroupDisplay addTab, LexicometricAnalyzeCmd cmd) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_RESULT_GROUP_PANEL_TITLE), configurationControler);
        this.addAnalysisGroupDisplay = addTab;
        this.cmd = cmd;
        this.checkBoxFieldsPanel = new CheckBoxPanel(getControler().getFieldConfigurationNameLabelMap().size(), true);
        createWindow();
    }

    @Override
    public void initComponents() {
        refreshLabelCheckBoxFields();
        refreshAction();
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(this.informationPanel.getJPanel());
        content.add(this.checkBoxFieldsPanel.getJPanel());
        content.add(this.actionPanel.getJPanel());
    }

    @Override
    public JPanel getContent() {
        return this.content;
    }

    @Override
    public String getWindowName() {
        return "Analysis Group Result Window";
    }

    /**
     * Permet de rafraichir les actions
     */
    private void refreshAction() {
        this.actionPanel.setStaticLabel(getMessage(WINDOW_INFORMATION_ACTION_PANEL_LABEL),
                Map.of(0, getMessage(WINDOW_ANALYSIS_RESULT_GROUP_BUTTON_LABEL)));
        this.actionPanel.addAction(0, x -> constructResult());
    }

    /**
     * Met à jour le libellé des check box
     */
    private void refreshLabelCheckBoxFields() {
        Integer current = 0;
        Map<Integer, String> labels = new HashMap<>();
        for (Map.Entry<String, String> entry : getControler().getFieldConfigurationNameLabelMap().entrySet()) {
            labels.put(current, entry.getKey() + " (" + entry.getValue() + ")");
            this.fieldNumberCheckBoxMap.put(current, entry.getKey());
            current++;
        }
        this.checkBoxFieldsPanel.setStaticLabel(getMessage(WINDOW_ANALYSIS_RESULT_GROUP_CHOOSE_FIELD_PANEL), labels);
    }

    /**
     * Permet de construire les résultats
     */
    private void constructResult() {
        Set<Integer> allIndexChecked = this.checkBoxFieldsPanel.getAllIndexChecked(true);
        Set<String> fieldSet = this.fieldNumberCheckBoxMap.entrySet().stream()
                .filter(x -> allIndexChecked.contains(x.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
        Set<AnalysisGroupDisplay> analysisGroupDisplaySet = getControler().getAnalysisGroupDisplaySet(cmd.getKeyTextFilteredList().stream().collect(Collectors.toSet()), fieldSet);
        analysisGroupDisplaySet.forEach(addAnalysisGroupDisplay::addAnalysisGroupDisplay);
        closeFrame();
    }
}
