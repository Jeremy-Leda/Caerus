package view.windows;

import controler.IConfigurationControler;
import io.vavr.Function2;
import org.apache.commons.lang3.StringUtils;
import view.abstracts.ManageListAbstract;
import view.beans.LexicometricEditEnum;
import view.beans.PictureTypeEnum;
import view.beans.ProperNounHierarchicalEditEnum;
import view.beans.TokenizationHierarchicalEditEnum;
import view.cmd.ProfilWithTableCmd;
import view.cmd.ProfilWithTableCmdBuilder;
import view.interfaces.IInformationPanel;
import view.interfaces.IProfileWithTable;
import view.interfaces.IRootTable;
import view.interfaces.ITableWithFilterAndEditPanel;
import view.panel.InformationPanel;
import view.panel.ProfileWithTablePanel;
import view.panel.TableWithFilterAndEditPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Fenêtre pour la gestion des noms propres
 */
public class ManageProperNoun extends ManageListAbstract {

    private final JPanel content;
    private final IProfileWithTable profileWithTable;
    private final IInformationPanel informationPanel;

    /**
     * Constructeur
     * @param configurationControler controler
     */
    public ManageProperNoun(IConfigurationControler configurationControler) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_PROPER_NOUN_PANEL_TITLE),
                configurationControler);
        this.content = new JPanel();
        this.profileWithTable = createProfileWithTable();
        this.informationPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_PROPER_NOUN_INFORMATION_LABEL),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_PROPER_NOUN_INFORMATION_MESSAGE),
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
                        .getDisplayMessage(Constants.WINDOW_MANAGE_PROPER_NOUN_PANEL_TITLE))
                .defaultProfile(getControler().getLexicometricDefaultProfile())
                .titleTablePanel(ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_PROPER_NOUN_TABLE_PANEL_TITLE))
                .lexicometricConfiguration(getControler().getLexicometricConfiguration(LexicometricEditEnum.PROPER_NOUN, ProperNounHierarchicalEditEnum.BASE))
                .lexicometricEditEnum(LexicometricEditEnum.PROPER_NOUN)
                .tableWithFilterAndEditPanelFunction(getTableWithTextFilterAndEditPanelFunction())
                .build();
        IProfileWithTable profileWithTable = new ProfileWithTablePanel(profilWithTableCmd, getControler());
        profileWithTable.setInterfaceForTableAndAddButton(0,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_PROPER_NOUN_ADD_INFORMATION_MESSAGE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_PROPER_NOUN_ADD_TEXT_LABEL));
        profileWithTable.setLabelForAddAndRemoveButton(0,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_PROPER_NOUN_ADD_BUTTON_LABEL),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_MANAGE_PROPER_NOUN_REMOVE_BUTTON_LABEL));
        return profileWithTable;
    }

    /**
     * Pemret de se procurer le filtre par défaut pour la gestion des listes
     * @return le filtre par défaut
     */
    public Function2<IRootTable, Consumer<?>, ITableWithFilterAndEditPanel> getTableWithTextFilterAndEditPanelFunction() {
        return (x, v) -> new TableWithFilterAndEditPanel<String>(StringUtils.EMPTY, x.getHeaderLabel(), v,
                Comparator.naturalOrder(),
                Comparator.comparing(StringUtils::stripAccents),
                s -> StringUtils.isNotBlank(s.getStringValue()),
                (s, f) -> s.contains(f.getStringValue()), super.getTableTextFilterPanel(x.getFilterLabel()));
    }

    @Override
    public void initComponents() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        this.content.add(this.informationPanel.getJPanel());
        this.content.add(this.profileWithTable.getJPanel());
        this.content.add(getNewInstanceOfDefaultAction(LexicometricEditEnum.PROPER_NOUN).getJPanel());
    }

    @Override
    public JPanel getContent() {
        return this.content;
    }

    @Override
    public String getWindowName() {
        return "Manage proper noun";
    }
}
