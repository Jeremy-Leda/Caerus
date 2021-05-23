package view.panel;

import controler.IConfigurationControler;
import model.analyze.lexicometric.beans.FillTableConfiguration;
import model.analyze.lexicometric.interfaces.ILexicometricConfiguration;
import org.apache.commons.lang3.StringUtils;
import view.abstracts.ExecuteServerJFrameAbstract;
import view.beans.EditTableElement;
import view.beans.PictureTypeEnum;
import view.cmd.ProfilWithTableCmd;
import view.interfaces.*;
import view.utils.ConfigurationUtils;
import view.utils.Constants;
import view.windows.UserQuestion;
import view.windows.YesNoQuestion;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * Classe pour la gestion des profiles et des tableaux
 *
 */
public class ProfileWithTablePanel extends ExecuteServerJFrameAbstract implements IProfileWithTable {

    private final JPanel panel;
    private final IComboBoxPanel comboBoxPanel;
    private final Map<Integer, ITableWithFilterAndEditPanel> tableWithFilterAndEditPanelMap;
    private final IActionPanel actionPanel;
    private final JPanel tablePanel;
    private final Map<Integer, BiFunction<String, String, Collection<String>>> functionIdTableMap;

    private final ProfilWithTableCmd profilWithTableCmd;
    private final IConfigurationControler controler;

    /**
     *
     * Constructeur
     *
     * @param profilWithTableCmd Cmd
     */
    public ProfileWithTablePanel(ProfilWithTableCmd profilWithTableCmd, IConfigurationControler configurationControler) {
        super(configurationControler);
        this.panel = new JPanel();
        this.tablePanel = new JPanel();
        this.comboBoxPanel = new ComboBoxPanel(StringUtils.EMPTY, ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_EDIT_PROFILE_LABEL));
        this.tableWithFilterAndEditPanelMap = new TreeMap<>(Comparator.naturalOrder());
        this.functionIdTableMap = new HashMap<>();
        this.actionPanel = new ActionPanel(3);
        this.controler = configurationControler;
        this.profilWithTableCmd = profilWithTableCmd;
        init(profilWithTableCmd);
    }

    /**
     * Permet d'initialiser les composants
     *
     * @param profilWithTableCmd Commande
     */
    private void init(ProfilWithTableCmd profilWithTableCmd) {
        this.panel.setBorder(
                BorderFactory.createTitledBorder(profilWithTableCmd.getTitlePanel()));
        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxlayout);
        this.comboBoxPanel.addConsumerOnSelectChange(getConsumerOnProfileChange());
        this.panel.add(this.comboBoxPanel.getJPanel());
        this.actionPanel.setStaticLabel(StringUtils.EMPTY, Map.of(
                0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EDIT_PROFILE_NEW_BUTTON_LABEL),
                1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EDIT_PROFILE_REMOVE_BUTTON_LABEL),
                2, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EDIT_PROFILE_SAVE_BUTTON_LABEL)));
        this.actionPanel.setIconButton(0, PictureTypeEnum.SAVE);
        this.actionPanel.setIconButton(1, PictureTypeEnum.SAVE);
        this.actionPanel.setIconButton(2, PictureTypeEnum.SAVE);
        this.panel.add(this.actionPanel.getJPanel());
        this.tablePanel.setBorder(BorderFactory.createTitledBorder(profilWithTableCmd.getTitleTablePanel()));
        BoxLayout boxlayoutTable = new BoxLayout(tablePanel, BoxLayout.X_AXIS);
        this.tablePanel.setLayout(boxlayoutTable);
        this.panel.add(this.tablePanel);
        configureActionButtons();
        createTables(profilWithTableCmd);
        fillProfileSet(profilWithTableCmd.getLexicometricConfiguration().getProfilesSet(), profilWithTableCmd.getDefaultProfile());
        addTableLink(profilWithTableCmd);
        addConsumerForRefreshStateOfAddButtons();
    }

    /**
     * Permet de construire les tables et d'alimenter la map
     * @param profilWithTableCmd Commande
     */
    private void createTables(ProfilWithTableCmd profilWithTableCmd) {
        tableWithFilterAndEditPanelMap.clear();
        Set<IRootTable> rootTableSet = profilWithTableCmd.getLexicometricConfiguration().getHierarchicalTableSet();
        rootTableSet.stream().forEach(iRootTable ->
                tableWithFilterAndEditPanelMap.put(iRootTable.displayOrder(),
                        profilWithTableCmd.getTableWithFilterAndEditPanelFunction().apply(iRootTable, getConsumerForSaveData(profilWithTableCmd))));
        this.tablePanel.removeAll();
        tableWithFilterAndEditPanelMap.values().forEach(v -> this.tablePanel.add(v.getJPanel()));
    }

    /**
     * Permet de remplir la table root et de vider les autres tables
     * @param profile profile à utiliser
     */
    private void fillRootTable(String profile) {
        //FIXME comprendre pourquoi c'est appelé tous le temps (tous les profil au démarrage)
        if (profile.equals(getSelectedProfil())) {
            Set<IRootTable> rootTableSet = this.profilWithTableCmd.getLexicometricConfiguration().getHierarchicalTableSet();
            Integer id = rootTableSet.stream().filter(IRootTable::isRoot).findFirst().get().displayOrder();
            ILexicometricConfiguration<String> lexicometricConfigurationString = this.profilWithTableCmd.getLexicometricConfiguration();
            lexicometricConfigurationString.getFillTableConfigurationList().forEach(table -> {
                Collection<?> newCollection = Collections.EMPTY_LIST;
                if (table.getDest().equals(id)) {
                    newCollection = table.getBiFunction().apply(profile, null);
                }
                fillTable(table.getDest(), newCollection);
            });
        }
    }

    /**
     * Permet de se procurer le profil courant
     * @return le profil courant
     */
    private String getSelectedProfil() {
        return this.comboBoxPanel.getLabelSelected();
    }

    /**
     * Permet d'ajouter un liaison à une table
     * @param profilWithTableCmd la commande qui permet de gérer les liaisons
     */
    private void addTableLink(ProfilWithTableCmd profilWithTableCmd) {
        profilWithTableCmd.getLexicometricConfiguration().getFillTableConfigurationList().forEach(tableConf -> {
            FillTableConfiguration<String> tableConfiguration = (FillTableConfiguration<String>) tableConf;
            if (tableConfiguration.getSource().isPresent()) {
                this.tableWithFilterAndEditPanelMap.get(tableConfiguration.getSource().get()).setConsumerForRowChanged(rowValue -> fillTable(tableConfiguration.getDest(), tableConfiguration.getBiFunction().apply(this.comboBoxPanel.getLabelSelected(), getSelectedValues(tableConfiguration.getSource().get()))));
                this.fillTable(tableConfiguration.getDest(), Collections.emptyList());
            }
        });
    }

    /**
     * Permet d'ajouter le consumer pour rafraichir l'état des boutons ajouter
     */
    private void addConsumerForRefreshStateOfAddButtons() {
        this.tableWithFilterAndEditPanelMap.values().forEach(table -> table.setConsumerForRefreshStateOfAllAddButton(x -> refreshAddButtonOfAllTable()));
    }

    /**
     * Permet de se procurer les valeurs sélectionné pour chaque tableau inférieur ou égale
     * @param destListNum numéro de la liste source
     * @return la liste des valeurs sélectionné
     */
    private LinkedList<String> getSelectedValues(Integer destListNum) {
        return this.tableWithFilterAndEditPanelMap.entrySet().stream().filter(x -> x.getKey() <= destListNum).map(x -> (String) x.getValue().getSelectedValue()).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Permet de remplir la table
     * @param id Identifiant de la table
     * @param collection collection pour la table
     */
    private void fillTable(Integer id, Collection<?> collection) {
        tableWithFilterAndEditPanelMap.get(id).fillTable(collection);
    }

    /**
     * Permet de configurer le consumer pour le bouton ajouter
     * @param id Identifiant de la table
     * @param informationMessage Information pour l'utilisateur
     * @param label Label devant la zone de saisie
     */
    @Override
    public void setInterfaceForTableAndAddButton(Integer id, String informationMessage, String label) {
        tableWithFilterAndEditPanelMap.get(id).setInterfaceForAddButton(informationMessage, label);
    }

    /**
     * Permet de modifier le label pour le bouton ajouter et supprimer
     * @param id Identifiant de la table
     * @param addLabel Label du bouton ajouter
     * @param removeLabel Label du bouton supprimer
     */
    @Override
    public void setLabelForAddAndRemoveButton(Integer id, String addLabel, String removeLabel) {
        tableWithFilterAndEditPanelMap.get(id).setLabelForAddAndRemoveButton(addLabel, removeLabel);
    }

    /**
     * permet de définir le profile sélectionné
     * @param profile profile à sélectionner
     */
    private void setProfile(String profile) {
        this.comboBoxPanel.selectItem(profile);
        fillRootTable(profile);
        refreshAddButtonOfAllTable();
    }


    @Override
    public JComponent getJPanel() {
        return this.panel;
    }

    @Override
    public void refresh() {

    }

    /**
     * Permet de remplir la liste des profiles
     * @param profiles liste des profiles
     * @param defaultProfile profiles par défaut
     */
    private void fillProfileSet(Collection<String> profiles, String defaultProfile) {
        this.comboBoxPanel.refresh(profiles);
        setProfile(defaultProfile);
    }

    /**
     * Permet de configurer les boutons pour la gestion des listes
     */
    private void configureActionButtons() {
        this.actionPanel.addAction(0, getConsumerForNewListe());
        this.actionPanel.addAction(1, getConsumerForRemoveListe());
        this.actionPanel.addAction(2, getConsumerForSaveListe());
    }

    /**
     * Permet de se procurer le consumer pour la création d'une nouvelle liste
     * @return Le consumer pour la création d'une nouvelle liste
     */
    private ActionListener getConsumerForNewListe() {
        return e -> {
            YesNoQuestion yesNoQuestion = new YesNoQuestion(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EDIT_PROFILE_NEW_BUTTON_LABEL),
                    null,
                    ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EDIT_PROFILE_NEW_BUTTON_COPY_OR_NEW_MESSAGE));
            int result = yesNoQuestion.getAnswer();
            UserQuestion userQuestion = new UserQuestion(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EDIT_PROFILE_NEW_BUTTON_NEW_NAME_MESSAGE),
                    ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EDIT_PROFILE_NEW_BUTTON_LABEL));
            String newProfileName = userQuestion.getAnswer();
            if (StringUtils.isNotBlank(newProfileName)) {
                executeOnServer(() -> {
                    this.controler.addConfigurationLexicometricProfile(newProfileName, this.profilWithTableCmd.getLexicometricEditEnum(), result == 0);
                    fillProfileSet(profilWithTableCmd.getLexicometricConfiguration().getProfilesSet(), newProfileName);
                    getControler().saveLexicometricProfilInDisk(this.profilWithTableCmd.getLexicometricEditEnum(), newProfileName);
                }, true);
            }
        };
    }

    /**
     * Permet de se procurer le consumer pour la suppression d'une liste
     * @return Le consumer pour la suppression d'une liste
     */
    private ActionListener getConsumerForRemoveListe() {
        return e -> {
            YesNoQuestion yesNoQuestion = new YesNoQuestion(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EDIT_PROFILE_REMOVE_BUTTON_LABEL),
                    null,
                    ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EDIT_PROFILE_REMOVE_BUTTON_CONFIRMATION_MESSAGE));
            int result = yesNoQuestion.getAnswer();
            String profilToRemove = this.comboBoxPanel.getLabelSelected();
            executeOnServer(() -> {
                if (result == 0) {
                    this.controler.removeConfigurationLexicometricProfile(profilToRemove, this.profilWithTableCmd.getLexicometricEditEnum());
                }
                Set<String> profilesSet = profilWithTableCmd.getLexicometricConfiguration().getProfilesSet();
                fillProfileSet(profilesSet, profilesSet.stream().findFirst().orElse(StringUtils.EMPTY));
                getControler().saveLexicometricProfilInDisk(this.profilWithTableCmd.getLexicometricEditEnum(), profilToRemove);
            }, true);
        };
    }

    /**
     * Permet de se procurer le consumer pour sauvegarder
     * @return Le consumer pour sauvegarder
     */
    private ActionListener getConsumerForSaveListe() {
       return x -> executeOnServer(() -> getControler().saveLexicometricProfilInDisk(this.profilWithTableCmd.getLexicometricEditEnum(), this.comboBoxPanel.getLabelSelected()), true);
    }

    /**
     * Permet de sauvegarder les datas en mémoire
     * @return le consumer pour la sauvegarde des datas
     */
    private Consumer<?> getConsumerForSaveData(ProfilWithTableCmd profilWithTableCmd) {
        return e -> {
            Optional<EditTableElement> parent = Optional.empty();
            EditTableElement lastEditTableElement = null;
            Set<IRootTable> rootTableSet = this.profilWithTableCmd.getLexicometricConfiguration().getHierarchicalTableSet();
            List<Integer> saveOrderSet = rootTableSet.stream().sorted(Comparator.comparing(IRootTable::hierarchicalOrder)).map(IRootTable::displayOrder).collect(Collectors.toList());
            for (Integer id : saveOrderSet) {
                ITableWithFilterAndEditPanel tableWithFilterAndEditPanel = this.tableWithFilterAndEditPanelMap.get(id);
                Optional<EditTableElement> editTableElementOptional = tableWithFilterAndEditPanel.getEditTableElement();
                if (editTableElementOptional.isPresent()) {
                    EditTableElement editTableElement = editTableElementOptional.get();
                    if (parent.isPresent()) {
                        lastEditTableElement.setLinkedElement(Optional.ofNullable(editTableElement));
                    } else {
                        parent = Optional.of(editTableElement);
                    }
                    lastEditTableElement = editTableElement;
                }
            }
            parent.ifPresent(x -> profilWithTableCmd.getLexicometricConfiguration().getEditConsumer().accept(this.comboBoxPanel.getLabelSelected(), x));
        };
    }

    /**
     * Consumer pour le changement de la liste en fonction du profile
     * @return le consumer
     */
    private Consumer<String> getConsumerOnProfileChange() {
        return e -> {
            fillRootTable(this.comboBoxPanel.getLabelSelected());
            refreshAddButtonOfAllTable();
        };
    }

    /**
     * Permet de rafraichir l'état des boutons ajouter sur tous les tableaux
     */
    private void refreshAddButtonOfAllTable() {
        this.tableWithFilterAndEditPanelMap.get(0).setEnabledAddButton(true);
        if (this.tableWithFilterAndEditPanelMap.size() > 1) {
            IntStream.range(1, this.tableWithFilterAndEditPanelMap.size())
                    .boxed()
                    .forEach(integer -> {
                        Boolean haveSelectedValue = this.tableWithFilterAndEditPanelMap.get(integer - 1).haveSelectedValue();
                        this.tableWithFilterAndEditPanelMap.get(integer).setEnabledAddButton(haveSelectedValue);
                        if (!haveSelectedValue) {
                            this.tableWithFilterAndEditPanelMap.get(integer).fillTable(Collections.emptyList());
                        }
                    });
        }
    }

    @Override
    public void closeFrame() {

    }
}
