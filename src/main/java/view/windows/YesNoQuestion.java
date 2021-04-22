package view.windows;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
import view.panel.ActionPanel;
import view.panel.InformationPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Classe permettant de poser une question Oui ou non à l'utilisateur
 *
 */
public class YesNoQuestion extends ModalJFrameAbstract {

    private final JPanel content;
    private final InformationPanel infoPanel;
    private final IActionPanel actionPanel;
    private int answer = -1;

    public YesNoQuestion(String title, IConfigurationControler configurationControler, String message) {
        super(title, configurationControler);
        this.content = new JPanel();
        this.infoPanel = new InformationPanel(PictureTypeEnum.QUESTION, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_MESSAGE_PANEL_LABEL), message, true, true);
        this.actionPanel = new ActionPanel(2);
        createWindow();
    }

    /**
     * Permet de se procurer la réponse au format {@link JOptionPane}
     * @return la réponse au format {@link JOptionPane}
     */
    public int getAnswer() {
        return answer;
    }

    /**
     * Permet de créer le contenu
     */
    private void createContent() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(this.infoPanel.getJPanel());
        content.add(actionPanel.getJPanel());
    }

    /**
     * Permet de rafraichir l'affichage
     */
    private void refreshActionPanelMessage() {
        Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
        messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_YES_LABEL));
        messageButtonMap.put(1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_NO_LABEL));
        this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_ACTION_PANEL_LABEL), messageButtonMap);
        this.actionPanel.addAction(0, action(JOptionPane.YES_OPTION));
        this.actionPanel.addAction(1, action(JOptionPane.NO_OPTION));
    }

    /**
     * Permet de se procurer {@link ActionListener} pour répondre et de fermer la fenêtre
     * @return l'action listener
     */
    private ActionListener action(int userAnswer) {
        return e -> {
            answer = userAnswer;
            closeFrame();
        };
    }

    @Override
    public void initComponents() {
        refreshActionPanelMessage();
        createContent();
    }

    @Override
    public JPanel getContent() {
        return this.content;
    }

    @Override
    public String getWindowName() {
        return "Window for yes no question";
    }
}
