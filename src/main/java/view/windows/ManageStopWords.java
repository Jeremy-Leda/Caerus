package view.windows;

import controler.IConfigurationControler;
import io.vavr.Function2;
import org.apache.commons.lang3.StringUtils;
import view.abstracts.ManageListAbstract;
import view.abstracts.ModalJFrameAbstract;
import view.beans.LexicometricEditEnum;
import view.beans.PictureTypeEnum;
import view.beans.TokenizationHierarchicalEditEnum;
import view.cmd.ProfilWithTableCmd;
import view.cmd.ProfilWithTableCmdBuilder;
import view.interfaces.*;
import view.panel.*;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Fenêtre pour la gestion des stopwords
 */
public class ManageStopWords extends ManageListAbstract {

    private final JPanel content;
    private final IProfileWithTable profileWithTable;
    private final IInformationPanel informationPanel;

    /**
     * Constructeur
     * @param configurationControler controler
     */
    public ManageStopWords(IConfigurationControler configurationControler) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_STOPWORDS_PANEL_TITLE),
                configurationControler);
        this.content = new JPanel();
        this.profileWithTable = createProfileWithTable();
        this.informationPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_STOPWORDS_INFORMATION_LABEL),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_STOPWORDS_INFORMATION_MESSAGE),
                false, true);
        createWindow();
    }

    /**
     * Permet de créer le profil avec la table
     * @return le profil avec la table
     */
    private IProfileWithTable createProfileWithTable() {
        ProfilWithTableCmd profilWithTableCmd = new ProfilWithTableCmdBuilder()
                .titlePanel(ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_STOPWORDS_PANEL_TITLE))
                .defaultProfile(getControler().getLexicometricDefaultProfile())
                .titleTablePanel(ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_STOPWORDS_TABLE_PANEL_TITLE))
                .lexicometricConfiguration(getControler().getLexicometricConfiguration(LexicometricEditEnum.TOKENIZATION, TokenizationHierarchicalEditEnum.BASE))
                .lexicometricEditEnum(LexicometricEditEnum.TOKENIZATION)
                .tableWithFilterAndEditPanelFunction(getTableWithTextFilterAndEditPanelFunction())
                .build();
        IProfileWithTable profileWithTable = new ProfileWithTablePanel(profilWithTableCmd, getControler());
        profileWithTable.setInterfaceForTableAndAddButton(0,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_STOPWORDS_ADD_INFORMATION_MESSAGE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_STOPWORDS_ADD_TEXT_LABEL));
        profileWithTable.setLabelForAddAndRemoveButton(0,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_STOPWORDS_ADD_BUTTON_LABEL),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_STOPWORDS_REMOVE_BUTTON_LABEL));
        return profileWithTable;
    }

    @Override
    public void initComponents() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        this.content.add(this.informationPanel.getJPanel());
        this.content.add(this.profileWithTable.getJPanel());
        this.content.add(getNewInstanceOfDefaultAction(LexicometricEditEnum.TOKENIZATION).getJPanel());
    }

    @Override
    public JPanel getContent() {
        return this.content;
    }

    @Override
    public String getWindowName() {
        return "Manage StopWords";
    }
}
