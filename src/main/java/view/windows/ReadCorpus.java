package view.windows;

import controler.IConfigurationControler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.abstracts.ModalJFrameAbstract;
import view.beans.ActionUserTypeEnum;
import view.beans.StateCorpusEnum;
import view.beans.TextIhmTypeEnum;
import view.interfaces.IActionOnClose;
import view.interfaces.IActionPanel;
import view.interfaces.IContentTextGenericPanel;
import view.interfaces.IFilePanel;
import view.panel.ActionPanel;
import view.panel.ContentTextGenericPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static view.utils.Constants.*;

/**
 *
 * Fenêtre pour afficher les informations d'un corpus
 *
 */
public class ReadCorpus extends ModalJFrameAbstract {

    private static Logger logger = LoggerFactory.getLogger(ReadCorpus.class);

    //private final IFilePanel filePanel;
    private final IContentTextGenericPanel informationsCorpusPanel;
    private final IActionPanel actionPanel;
    private final JPanel content;

    public ReadCorpus(String title, IConfigurationControler configurationControler, Boolean isModal, String keyText) {
        super(title, configurationControler, isModal);
        this.informationsCorpusPanel = new ContentTextGenericPanel(configurationControler, TextIhmTypeEnum.JTEXTFIELD,
                StateCorpusEnum.READ, Optional.of(keyText));
        this.informationsCorpusPanel.setReadOnly(true);
        this.actionPanel = new ActionPanel(1);
        this.content = new JPanel();
        updateContentInformationsCorpusPanel();
        createWindow();
    }

    @Override
    public void initComponents() {
        addActionPanel();
        refreshActionPanelMessage();
        createContent();
    }

    @Override
    public JPanel getContent() {
        return this.content;
    }

    /**
     * Permet de définir la clé du corpus à afficher
     * @param key clé du corpus
     */
    public void setKeyText(String key) {
        this.informationsCorpusPanel.setKeyText(key);
        this.informationsCorpusPanel.reloadValue();
    }

    /**
     * Met à jour le contenu de l'information corpus panel
     */
    private void updateContentInformationsCorpusPanel() {
        this.informationsCorpusPanel.refresh(
                ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_CORPUS_CONTENT_PANEL_TITLE));
        this.informationsCorpusPanel.refreshComponents(getControler().getConfigurationFieldMetaFile());
    }

    /**
     * Permet de créer le contenu
     */
    private void createContent() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(this.informationsCorpusPanel.getJPanel());
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

    @Override
    public String getWindowName() {
        return "Read corpus window";
    }
}