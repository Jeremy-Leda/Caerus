package view.windows;

import controler.IConfigurationControler;
import io.vavr.Tuple2;
import model.analyze.UserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.abstracts.ModalJFrameAbstract;
import view.beans.ActionUserTypeEnum;
import view.beans.StateCorpusEnum;
import view.beans.TextIhmTypeEnum;
import view.interfaces.*;
import view.panel.ActionPanel;
import view.panel.ContentTextGenericPanel;
import view.panel.LabelsPanel;
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
    private final ILabelsPanel labelsPanel = new LabelsPanel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_FILE_PANEL_TITLE), 2);

    public ReadCorpus(String title, IConfigurationControler configurationControler, Boolean isModal, String keyText) {
        super(title, configurationControler, isModal);
        this.informationsCorpusPanel = new ContentTextGenericPanel(configurationControler, TextIhmTypeEnum.JTEXTFIELD,
                StateCorpusEnum.READ, Optional.of(keyText));
        this.informationsCorpusPanel.setReadOnly(true);
        this.actionPanel = new ActionPanel(1);
        this.content = new JPanel();
        updateContentInformationsCorpusPanel();
        refreshLabelsPanel(keyText);
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
        refreshLabelsPanel(key);
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
        content.add(this.labelsPanel.getJPanel());
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

    /**
     * Permet de rafraichir les labels
     * @param keyText Clé du texte
     */
    private void refreshLabelsPanel(String keyText) {
        Optional<Tuple2<String, Integer>> informationOfDocument = UserSettings.getInstance().getInformationOfDocument(keyText);
        informationOfDocument.ifPresent(tuple -> {
            this.labelsPanel.setLabel(0, getMessage(Constants.WINDOW_CREATE_TEXT_NAME_LABEL), true, tuple._1(), true);
            this.labelsPanel.setLabel(1, getMessage(Constants.WINDOW_CREATE_TEXT_NUMBER_LABEL), true, tuple._2().toString(), true);
        });
    }

    @Override
    public String getWindowName() {
        return "Read corpus window";
    }
}
