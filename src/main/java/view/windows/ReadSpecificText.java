package view.windows;

import controler.IConfigurationControler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.abstracts.ModalJFrameAbstract;
import view.beans.StateCorpusEnum;
import view.interfaces.IActionPanel;
import view.interfaces.ISpecificTextModel;
import view.interfaces.ISpecificTextRefreshPanel;
import view.panel.ActionPanel;
import view.panel.SpecificListTablePanel;
import view.panel.model.SpecificTextModel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static view.utils.Constants.WINDOW_INFORMATION_ACTION_BUTTON_LABEL;
import static view.utils.Constants.WINDOW_INFORMATION_ACTION_PANEL_LABEL;

/**
 *
 * Fenêtre pour afficher les informations d'un corpus
 *
 */
public class ReadSpecificText extends ModalJFrameAbstract {

    private static Logger logger = LoggerFactory.getLogger(ReadSpecificText.class);

    //private final IFilePanel filePanel;
    private final ISpecificTextModel specificTextModel;
    private final ISpecificTextRefreshPanel detailListContentSpecificTextPanel;
    private final IActionPanel navigationPanel;
    private final IActionPanel actionPanel;
    private final JPanel content;

    public ReadSpecificText(String title, IConfigurationControler configurationControler, Boolean isModal, String keyText) {
        super(title, configurationControler, isModal);
        this.specificTextModel = new SpecificTextModel(configurationControler, this, StateCorpusEnum.READ);
        this.detailListContentSpecificTextPanel = new SpecificListTablePanel(this.specificTextModel, true);
        this.specificTextModel.addSpecificTextRefresh(this.detailListContentSpecificTextPanel);
        this.navigationPanel = new ActionPanel(2);
        this.specificTextModel.addSpecificTextRefresh(this.navigationPanel);
        this.actionPanel = new ActionPanel(1);
        this.content = new JPanel();
        setKeyText(keyText);
        createWindow();
    }

    @Override
    public void initComponents() {
        addActionPanel();
        addActionListenerToNavigationPanel();
        refreshActionPanelMessage();
        refreshNavigationPanelMessage();
        this.navigationPanel.setEnabled(0, specificTextModel.havePreviousSpecificConfiguration());
        this.navigationPanel.setEnabled(1, specificTextModel.haveNextSpecificConfiguration());
        this.navigationPanel.setEnabled(2, !specificTextModel.haveNextSpecificConfiguration());
        createContent();
    }

    @Override
    public JPanel getContent() {
        return this.content;
    }

    /**
     * Permet d'ajouter les actions au panel d'action
     */
    private void addActionListenerToNavigationPanel() {
        this.navigationPanel.addAction(0, e -> {
            specificTextModel.loadAllField(specificTextModel.getCurrentIndex()-1);
            navigationPanel.setEnabled(0, specificTextModel.havePreviousSpecificConfiguration());
            navigationPanel.setEnabled(1, specificTextModel.haveNextSpecificConfiguration());
            navigationPanel.setEnabled(2, !specificTextModel.haveNextSpecificConfiguration());
        });
        this.navigationPanel.addAction(1, e -> {
            specificTextModel.loadAllField(specificTextModel.getCurrentIndex()+1);
            navigationPanel.setEnabled(0, specificTextModel.havePreviousSpecificConfiguration());
            navigationPanel.setEnabled(1, specificTextModel.haveNextSpecificConfiguration());
            navigationPanel.setEnabled(2, !specificTextModel.haveNextSpecificConfiguration());
        });
    }

    /**
     * Permet de définir la clé du corpus à afficher
     * @param key clé du corpus
     */
    public void setKeyText(String key) {
        this.specificTextModel.setKeyText(key);
        this.specificTextModel.loadAllField(this.specificTextModel.getCurrentIndex());
    }

    /**
     * Permet de créer le contenu
     */
    private void createContent() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(this.detailListContentSpecificTextPanel.getJPanel());
        content.add(this.navigationPanel.getJPanel());
        content.add(this.actionPanel.getJPanel());
    }

    /**
     * Permet d'ajouter les actions au boutons
     */
    private void addActionPanel() {
        this.actionPanel.addAction(0, e -> closeFrame());
    }

    /**
     * Permet de rafraichir l'affichage
     */
    private void refreshActionPanelMessage() {
        this.actionPanel.setStaticLabel(getMessage(WINDOW_INFORMATION_ACTION_PANEL_LABEL), Map.of(0, getMessage(WINDOW_INFORMATION_ACTION_BUTTON_LABEL)));
    }

    /**
     * Permet de rafraichir l'affichage
     */
    private void refreshNavigationPanelMessage() {
        Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
        messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_ACTION_PANEL_BUTTON_PREVIOUS_LABEL));
        messageButtonMap.put(1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_ACTION_PANEL_BUTTON_NEXT_LABEL));
        this.navigationPanel.setStaticLabel(null, messageButtonMap);
        this.navigationPanel.setFunctionRefreshLabelTitleDynamically(getFunctionTitleJPanelActionRefresh());
    }

    /**
     * Permet de se produire la fonction pour la mise à jour du titre du Jpanel Action
     * @return la fonction
     */
    private Function<Void, String> getFunctionTitleJPanelActionRefresh() {
        StringBuilder sb = new StringBuilder(getControler().getConfigurationName());
        sb.append(" %d / %d");
        return (v) -> String.format(sb.toString(),
                this.specificTextModel.getCurrentIndex() + 1, this.specificTextModel.getNbMaxConfiguration());
    }


    @Override
    public String getWindowName() {
        return "Read text window";
    }
}
