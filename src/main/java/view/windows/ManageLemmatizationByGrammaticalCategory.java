package view.windows;

import controler.IConfigurationControler;
import view.abstracts.ManageListAbstract;
import view.beans.LemmatizationByGrammaticalCategoryHierarchicalEditEnum;
import view.beans.LemmatizationHierarchicalEditEnum;
import view.beans.LexicometricEditEnum;
import view.beans.PictureTypeEnum;
import view.cmd.ProfilWithTableCmd;
import view.cmd.ProfilWithTableCmdBuilder;
import view.interfaces.IInformationPanel;
import view.interfaces.IProfileWithTable;
import view.panel.InformationPanel;
import view.panel.ProfileWithTablePanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;

/**
 * Fenêtre pour la gestion des lemmes par catégorie grammatical
 */
public class ManageLemmatizationByGrammaticalCategory extends ManageListAbstract {

    private final JPanel content;
    private final IProfileWithTable profileWithTable;
    private final IInformationPanel informationPanel;

    /**
     * Constructeur
     * @param configurationControler controler
     */
    public ManageLemmatizationByGrammaticalCategory(IConfigurationControler configurationControler) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_PANEL_TITLE),
                configurationControler);
        this.content = new JPanel();
        this.profileWithTable = createProfileWithTable();
        this.informationPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_INFORMATION_LABEL),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_INFORMATION_MESSAGE),
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
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_PANEL_TITLE))
                .defaultProfile(getControler().getLexicometricDefaultProfile())
                .titleTablePanel(ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_TABLE_PANEL_TITLE))
                .lexicometricConfiguration(getControler().getLexicometricConfiguration(LexicometricEditEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY, LemmatizationByGrammaticalCategoryHierarchicalEditEnum.CATEGORY))
                .lexicometricEditEnum(LexicometricEditEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY)
                .tableWithFilterAndEditPanelFunction(getTableWithTextFilterAndEditPanelFunction())
                .build();
        IProfileWithTable profileWithTable = new ProfileWithTablePanel(profilWithTableCmd, getControler());
        profileWithTable.setInterfaceForTableAndAddButton(0,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_CATEGORY_ADD_INFORMATION_MESSAGE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_CATEGORY_ADD_TEXT_LABEL));
        profileWithTable.setInterfaceForTableAndAddButton(1,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_ADD_INFORMATION_MESSAGE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_ADD_TEXT_LABEL));
        profileWithTable.setInterfaceForTableAndAddButton(2,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_VARIATION_ADD_INFORMATION_MESSAGE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_VARIATION_ADD_TEXT_LABEL));
        profileWithTable.setLabelForAddAndRemoveButton(0,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_CATEGORY_ADD_BUTTON_LABEL),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_CATEGORY_REMOVE_BUTTON_LABEL));
        profileWithTable.setLabelForAddAndRemoveButton(1,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_ADD_BUTTON_LABEL),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_REMOVE_BUTTON_LABEL));
        profileWithTable.setLabelForAddAndRemoveButton(2,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_VARIATION_ADD_BUTTON_LABEL),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_VARIATION_REMOVE_BUTTON_LABEL));
        return profileWithTable;
    }

    @Override
    public void initComponents() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        this.content.add(this.informationPanel.getJPanel());
        this.content.add(this.profileWithTable.getJPanel());
        this.content.add(getNewInstanceOfDefaultAction(LexicometricEditEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY).getJPanel());
    }

    @Override
    public JPanel getContent() {
        return this.content;
    }

    @Override
    public String getWindowName() {
        return "Manage lemmatization";
    }
}
