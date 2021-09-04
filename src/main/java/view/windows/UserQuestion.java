package view.windows;

import org.apache.commons.lang3.StringUtils;
import view.abstracts.ModalJFrameAbstract;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
import view.interfaces.ITextBoxPanel;
import view.panel.ActionPanel;
import view.panel.InformationPanel;
import view.panel.TextBoxPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.Map;

/**
 *
 * Classe permettant d'afficher une fenêtre avec la question pour l'utilisateur et un champ texte pour y répondre
 *
 */
public class UserQuestion extends ModalJFrameAbstract {

    private final JPanel content;
    private final InformationPanel infoPanel;
    private final ITextBoxPanel textBoxPanel;
    private final IActionPanel actionPanel;

    public UserQuestion(String message, String label) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_QUESTION_USER_PANEL_TITLE), null);
        this.content = new JPanel();
        this.infoPanel = new InformationPanel(PictureTypeEnum.QUESTION, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_MESSAGE_PANEL_LABEL),
                message, true, true);
        this.textBoxPanel = new TextBoxPanel(1, false, 40);
        this.textBoxPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_ANSWER_USER_PANEL_TITLE), Map.of(0, label));
        this.actionPanel = new ActionPanel(2);
        this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_ACTION_PANEL_LABEL),
                Map.of(0,ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_OPERATION_VALIDATE),
                        1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_ACTION_BUTTON_LABEL)));
        this.textBoxPanel.addConsumerOnChange(0, e -> this.actionPanel.setEnabled(0, StringUtils.isNotBlank(this.textBoxPanel.getValueOfTextBox(0))));
        this.actionPanel.setEnabled(0, false);
        this.actionPanel.addAction(0, e -> closeFrame());
        this.actionPanel.addAction(1, e -> closeFrame());
        createWindow();
    }

    /**
     * Permet de créer le contenu
     */
    private void createContent() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(this.infoPanel.getJPanel());
        content.add(this.textBoxPanel.getJPanel());
        content.add(actionPanel.getJPanel());
    }

    /**
     * Permet de se procurer la réponse
     * @return la réponse de l'utilisateur
     */
    public String getAnswer() {
        return this.textBoxPanel.getValueOfTextBox(0);
    }


    @Override
    public void initComponents() {
        createContent();
    }

    @Override
    public JPanel getContent() {
        return this.content;
    }

    @Override
    public String getWindowName() {
        return "Window for user question";
    }
}
