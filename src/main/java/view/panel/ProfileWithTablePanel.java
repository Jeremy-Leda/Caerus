package view.panel;

import model.analyze.lexicometric.beans.FillTableConfiguration;
import model.analyze.lexicometric.interfaces.ILexicometricConfiguration;
import org.apache.commons.lang3.StringUtils;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * Classe pour la gestion des profiles et des tableaux
 *
 */
public class ProfileWithTablePanel implements IProfileWithTable {

    private final JPanel panel;
    private final IComboBoxPanel comboBoxPanel;
    private final Map<Integer, ITableWithFilterAndEditPanel> tableWithFilterAndEditPanelMap;
    private final IActionPanel actionPanel;
    private final JPanel tablePanel;
    private Consumer<?> saveProfileConsumer;
//    private Consumer<EditTable> saveDataConsumer;
//    private LinkedList<Integer> saveDataTableOrder;
    private Function<String, Collection<String>> functionProfileElements;
    private final Map<Integer, BiFunction<String, String, Collection<String>>> functionIdTableMap;

    private final ILexicometricConfiguration lexicometricConfiguration;

    /**
     *
     * Constructeur
     *
     * @param profilWithTableCmd Cmd
     */
    public ProfileWithTablePanel(ProfilWithTableCmd profilWithTableCmd) {
        this.panel = new JPanel();
        this.tablePanel = new JPanel();
        this.comboBoxPanel = new ComboBoxPanel(StringUtils.EMPTY, ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_PROFILE_LABEL));
        this.tableWithFilterAndEditPanelMap = new TreeMap<>(Comparator.naturalOrder());
        this.functionIdTableMap = new HashMap<>();
        this.actionPanel = new ActionPanel(3);
        this.lexicometricConfiguration = profilWithTableCmd.getLexicometricConfiguration();
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
                0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_PROFILE_NEW_BUTTON_LABEL),
                1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_PROFILE_REMOVE_BUTTON_LABEL),
                2, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_PROFILE_SAVE_BUTTON_LABEL)));
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
    }

    /**
     * Permet de construire les tables et d'alimenter la map
     * @param profilWithTableCmd Commande
     */
    private void createTables(ProfilWithTableCmd profilWithTableCmd) {
        tableWithFilterAndEditPanelMap.clear();
        Set<IRootTable> rootTableSet = profilWithTableCmd.getLexicometricConfiguration().getHierarchicalTableSet();
        rootTableSet.stream().forEach(iRootTable ->
                tableWithFilterAndEditPanelMap.put(iRootTable.displayOrder(), new TableWithFilterAndEditPanel(StringUtils.EMPTY, iRootTable.getHeaderLabel(), getConsumerForSaveData(profilWithTableCmd))));
        this.tablePanel.removeAll();
        tableWithFilterAndEditPanelMap.values().forEach(v -> this.tablePanel.add(v.getJPanel()));
    }

    private void fillRootTable(String profile) {
        // TODO Clear toutes les tables
        Set<IRootTable> rootTableSet = lexicometricConfiguration.getHierarchicalTableSet();
        Integer id = rootTableSet.stream().filter(IRootTable::isRoot).findFirst().get().displayOrder();
        ILexicometricConfiguration<String> lexicometricConfigurationString = lexicometricConfiguration;
        Optional<FillTableConfiguration<String>> tableConfiguration = lexicometricConfigurationString.getFillTableConfigurationList().stream().filter(table -> table.getDest().equals(id)).findFirst();
        tableConfiguration.ifPresent(table ->  fillTable(id, table.getBiFunction().apply(profile, null)));
    }

    private void addTableLink(ProfilWithTableCmd profilWithTableCmd) {
        profilWithTableCmd.getLexicometricConfiguration().getFillTableConfigurationList().forEach(tableConf -> {
            FillTableConfiguration<String> tableConfiguration = (FillTableConfiguration<String>) tableConf;
            if (tableConfiguration.getSource().isPresent()) {
                this.tableWithFilterAndEditPanelMap.get(tableConfiguration.getSource().get()).setConsumerForRowChanged(rowValue -> fillTable(tableConfiguration.getDest(), tableConfiguration.getBiFunction().apply(this.comboBoxPanel.getLabelSelected(), (String) rowValue)));
                this.fillTable(tableConfiguration.getDest(), Collections.emptyList());
            }
        });
    }

    /**
     * Permet de remplir la table
     * @param id Identifiant de la table
     * @param collection collection pour la table
     */
    private void fillTable(Integer id, Collection<String> collection) {
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

//    @Override
//    public void setSaveDataInMemory(Consumer<EditTable> consumer, LinkedList<Integer> order) {
//        this.saveDataConsumer = consumer;
//        this.saveDataTableOrder = order;
//    }

//    @Override
//    public void setGetListFromProfileFunction(Function<String, Collection<String>> function) {
//        this.functionProfileElements = function;
//    }
//
//    @Override
//    public void setReferenceFromSourceFunction(Integer idSource, Integer idDest, BiFunction<String, String, Collection<String>> function) {
//        //this.functionIdTableMap.put(id, function);
//        this.tableWithFilterAndEditPanelMap.get(idSource).setConsumerForRowChanged(rowValue -> fillTable(idDest, function.apply(this.comboBoxPanel.getLabelSelected(), rowValue)));
//        this.fillTable(idDest, Collections.emptyList());
//    }

//    @Override
//    public String getProfile() {
//        return this.comboBoxPanel.getLabelSelected();
//    }

    /**
     * permet de définir le profile sélectionné
     * @param profile profile à sélectionner
     */
    private void setProfile(String profile) {
        this.comboBoxPanel.selectItem(profile);
        fillRootTable(profile);
//        Collection<String> elementCollection = functionProfileElements.apply(profile);
//        fillTable(0, elementCollection);
    }


    @Override
    public JComponent getJPanel() {
        return this.panel;
    }

    @Override
    public void refresh() {

    }

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
            YesNoQuestion yesNoQuestion = new YesNoQuestion(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_PROFILE_NEW_BUTTON_LABEL),
                    null,
                    ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_PROFILE_NEW_BUTTON_COPY_OR_NEW_MESSAGE));
            int result = yesNoQuestion.getAnswer();
            String newProfileName = this.comboBoxPanel.getLabelSelected();
            Boolean noneItems = this.comboBoxPanel.getItemCount() == 0;
            while (noneItems || StringUtils.isNotBlank(newProfileName) && this.comboBoxPanel.itemExist(newProfileName)) {
                UserQuestion userQuestion = new UserQuestion(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_PROFILE_NEW_BUTTON_NEW_NAME_MESSAGE),
                        ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_PROFILE_NEW_BUTTON_LABEL));
                newProfileName = userQuestion.getAnswer();
                noneItems = false;
            }
            if (StringUtils.isBlank(newProfileName)) {
                return;
            }
            if (result != 0) {
                tableWithFilterAndEditPanelMap.values().forEach(t -> t.fillTable(Collections.EMPTY_LIST));
            }
            this.comboBoxPanel.addAndSelectItem(newProfileName);
        };
    }

    /**
     * Permet de se procurer le consumer pour la suppression d'une liste
     * @return Le consumer pour la suppression d'une liste
     */
    private ActionListener getConsumerForRemoveListe() {
        return e -> {
            YesNoQuestion yesNoQuestion = new YesNoQuestion(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_PROFILE_REMOVE_BUTTON_LABEL),
                    null,
                    ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_PROFILE_REMOVE_BUTTON_CONFIRMATION_MESSAGE));
            int result = yesNoQuestion.getAnswer();
            if (result == 0) {
                this.comboBoxPanel.delete(this.comboBoxPanel.getLabelSelected());
            }
            if (StringUtils.isBlank(this.comboBoxPanel.getLabelSelected())) {
                this.tableWithFilterAndEditPanelMap.values().forEach(t -> t.fillTable(Collections.EMPTY_LIST));
            }
        };
    }

    /**
     * Permet de se procurer le consumer pour sauvegarder
     * @return Le consumer pour sauvegarder
     */
    private ActionListener getConsumerForSaveListe() {
        return e -> {
            saveProfileConsumer.accept(null);
        };
    }

    /**
     * Permet de sauvegarder les datas en mémoire
     * @return le consumer pour la sauvegarde des datas
     */
    private Consumer<?> getConsumerForSaveData(ProfilWithTableCmd profilWithTableCmd) {
        return e -> {
            Optional<EditTableElement> parent = Optional.empty();
            EditTableElement lastEditTableElement = null;
            Set<IRootTable> rootTableSet = lexicometricConfiguration.getHierarchicalTableSet();
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
        return e -> fillRootTable(this.comboBoxPanel.getLabelSelected());
        //return e -> this.fillTable(0, functionProfileElements.apply(this.comboBoxPanel.getLabelSelected()));
    }
}
