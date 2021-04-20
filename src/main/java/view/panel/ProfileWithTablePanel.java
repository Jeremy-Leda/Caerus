package view.panel;

import org.apache.commons.lang3.StringUtils;
import view.beans.EditTable;
import view.beans.EditTableBuilder;
import view.beans.EditTableElement;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
import view.interfaces.IComboBoxPanel;
import view.interfaces.IProfileWithTable;
import view.interfaces.ITableWithFilterAndEditPanel;
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
    private Consumer<EditTable> saveDataConsumer;
    private LinkedList<Integer> saveDataTableOrder;
    private Function<String, Collection<String>> functionProfileElements;
    private final Map<Integer, BiFunction<String, String, Collection<String>>> functionIdTableMap;

    /**
     *
     * Constructeur
     *
     * @param titlePanel Titre du panel
     */
    public ProfileWithTablePanel(String titlePanel, String titleTablePanel) {
        this.panel = new JPanel();
        this.tablePanel = new JPanel();
        this.comboBoxPanel = new ComboBoxPanel(StringUtils.EMPTY, ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_PROFILE_LABEL));
        this.tableWithFilterAndEditPanelMap = new HashMap<>();
        this.functionIdTableMap = new HashMap<>();
        this.actionPanel = new ActionPanel(3);
        init(titlePanel, titleTablePanel);
    }

    /**
     * Permet d'initialiser les composants
     *
     * @param titlePanel Titre du panel
     */
    private void init(String titlePanel, String titleTablePanel) {
        this.panel.setBorder(
                BorderFactory.createTitledBorder(titlePanel));
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
        this.tablePanel.setBorder(BorderFactory.createTitledBorder(titleTablePanel));
        BoxLayout boxlayoutTable = new BoxLayout(tablePanel, BoxLayout.X_AXIS);
        this.tablePanel.setLayout(boxlayoutTable);
        this.panel.add(this.tablePanel);
        configureActionButtons();
    }

    /**
     * Permet de construire les tables et d'alimenter la map
     * @param idHeaderMap Map contenant l'id et le header de la table
     */
    public void createTable(Map<Integer, String> idHeaderMap) {
        tableWithFilterAndEditPanelMap.clear();
        idHeaderMap.forEach((key,value) -> tableWithFilterAndEditPanelMap.put(key, new TableWithFilterAndEditPanel(StringUtils.EMPTY, value, getConsumerForSaveData())));
        this.tablePanel.removeAll();
        tableWithFilterAndEditPanelMap.values().forEach(v -> this.tablePanel.add(v.getJPanel()));
    }

    /**
     * Permet de remplir la table
     * @param id Identifiant de la table
     * @param collection collection pour la table
     */
    @Override
    public void fillTable(Integer id, Collection<String> collection) {
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

    @Override
    public Set<String> getValues(Integer id) {
        return tableWithFilterAndEditPanelMap.get(id).getValues();
    }

    @Override
    public void setSaveProfileConsumer(Consumer<?> consumer) {
        this.saveProfileConsumer = consumer;
    }

    @Override
    public void setSaveDataInMemory(Consumer<EditTable> consumer, LinkedList<Integer> order) {
        this.saveDataConsumer = consumer;
        this.saveDataTableOrder = order;
    }

    @Override
    public void setGetListFromProfileFunction(Function<String, Collection<String>> function) {
        this.functionProfileElements = function;
    }

    @Override
    public void setReferenceFromSourceFunction(Integer idSource, Integer idDest, BiFunction<String, String, Collection<String>> function) {
        //this.functionIdTableMap.put(id, function);
        this.tableWithFilterAndEditPanelMap.get(idSource).setConsumerForRowChanged(rowValue -> fillTable(idDest, function.apply(this.comboBoxPanel.getLabelSelected(), rowValue)));
        this.fillTable(idDest, Collections.emptyList());
    }

    @Override
    public String getProfile() {
        return this.comboBoxPanel.getLabelSelected();
    }

    @Override
    public void setProfile(String profile) {
        this.comboBoxPanel.selectItem(profile);
        Collection<String> elementCollection = functionProfileElements.apply(profile);
        fillTable(0, elementCollection);
    }


    @Override
    public JComponent getJPanel() {
        return this.panel;
    }

    @Override
    public void refresh() {

    }

    @Override
    public void fillProfileSet(Collection<String> profiles, String defaultProfile) {
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
            String newProfileName = getProfile();
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
    private Consumer<?> getConsumerForSaveData() {
        return e -> {
            Optional<EditTableElement> parent = Optional.empty();
            EditTableElement lastEditTableElement = null;
            for (Integer id : saveDataTableOrder) {
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
            parent.ifPresent(x -> this.saveDataConsumer.accept(new EditTableBuilder()
                    .editTableElement(x)
                    .profil(getProfile())
                    .build()));
        };
    }

    /**
     * Consumer pour le changement de la liste en fonction du profile
     * @return le consumer
     */
    private Consumer<String> getConsumerOnProfileChange() {
        return e -> this.fillTable(0, functionProfileElements.apply(this.comboBoxPanel.getLabelSelected()));
    }
}
