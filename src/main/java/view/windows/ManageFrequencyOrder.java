package view.windows;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.PictureTypeEnum;
import view.beans.StateCorpusEnum;
import view.interfaces.IAccessPanel;
import view.interfaces.IActionPanel;
import view.interfaces.IFrequencyOrderTextModel;
import view.interfaces.ISpecificTextModel;
import view.panel.ActionPanel;
import view.panel.FrequencyOrderControlAndListPanel;
import view.panel.SpecificControlAndListPanel;
import view.panel.model.FrequencyOrderTextModel;
import view.panel.model.SpecificTextModel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ManageFrequencyOrder extends ModalJFrameAbstract {

    private final IFrequencyOrderTextModel frequencyOrderTextModel;
    private final IAccessPanel frequencyOrderContentPanel;

    private final JPanel content;

    private final IActionPanel actionPanel;

    public ManageFrequencyOrder(IConfigurationControler configurationControler) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FREQUENCY_ORDER_PANEL_TITLE), configurationControler);
        this.frequencyOrderTextModel = new FrequencyOrderTextModel(configurationControler, this);
        this.frequencyOrderContentPanel = new FrequencyOrderControlAndListPanel(this.frequencyOrderTextModel);
        this.actionPanel = new ActionPanel(1);
        this.content = new JPanel();
        createWindow();
    }

    @Override
    public void initComponents() {
        this.frequencyOrderTextModel.loadAllField();
        refreshActionPanelMessage();
        createContent();
    }

    /**
     * Permet de créer le contenu
     */
    private void createContent() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(this.frequencyOrderContentPanel.getJPanel());
        content.add(this.actionPanel.getJPanel());
    }

    /**
     * Permet de rafraichir l'affichage
     */
    private void refreshActionPanelMessage() {
        Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
        messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FREQUENCY_ORDER_SAVE_BUTTON));
        this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_ACTION_PANEL_TITLE), messageButtonMap);
        this.actionPanel.addAction(0, saveFrequencyOrder());
        this.actionPanel.setIconButton(1, PictureTypeEnum.SAVE);
    }

    private ActionListener saveFrequencyOrder() {
       return e -> executeOnServerWithCloseCurrentFrame(() -> getControler().saveFrequencyOrderInDisk());
    }

    @Override
    public JPanel getContent() {
        return this.content;
    }

    @Override
    public String getWindowName() {
        return "Window for manage frequency order";
    }
}
