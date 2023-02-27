package view.panel;

import view.interfaces.IAccessPanel;
import view.interfaces.IFrequencyOrderTextModel;
import view.interfaces.ISpecificTextRefreshPanel;

import javax.swing.*;

public class FrequencyOrderControlAndListPanel implements IAccessPanel {
    private final JPanel content;
    private final IFrequencyOrderTextModel frequencyOrderTextModel;
    private final ISpecificTextRefreshPanel entryContentSpecificTextPanel;
    private final ISpecificTextRefreshPanel detailListContentSpecificTextPanel;

    /**
     * Constructeur
     * @param frequencyOrderTextModel specific text model
     */
    public FrequencyOrderControlAndListPanel(IFrequencyOrderTextModel frequencyOrderTextModel) {
        this.frequencyOrderTextModel = frequencyOrderTextModel;
        this.entryContentSpecificTextPanel = new FrequencyOrderControlPanel(this.frequencyOrderTextModel);
        this.detailListContentSpecificTextPanel = new FrequencyOrderTablePanel(this.frequencyOrderTextModel, false);
        this.frequencyOrderTextModel.addSpecificTextRefresh(this.entryContentSpecificTextPanel);
        this.frequencyOrderTextModel.addSpecificTextRefresh(this.detailListContentSpecificTextPanel);
        this.content = new JPanel();
        createContent();
    }

    /**
     * Permet de créer le contenu
     */
    private void createContent() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(this.entryContentSpecificTextPanel.getJPanel());
        content.add(this.detailListContentSpecificTextPanel.getJPanel());
    }

    @Override
    public JComponent getJPanel() {
        return this.content;
    }
}
