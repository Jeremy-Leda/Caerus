package view.panel.model;

import controler.IConfigurationControler;
import view.beans.FrequencyOrder;
import view.beans.FrequencyOrderBuilder;
import view.beans.FrequencyOrderEnum;
import view.beans.SpecificRow;
import view.interfaces.IFrequencyOrderTextModel;
import view.interfaces.IModalFrameRepack;
import view.interfaces.ISpecificTextRefreshPanel;

import javax.swing.*;
import java.util.*;
import java.util.function.Consumer;

public class FrequencyOrderTextModel implements IFrequencyOrderTextModel {

    private final IConfigurationControler controler;
    private final IModalFrameRepack modalFrameRepack;

    private final List<FrequencyOrder> frequencyOrderList;

    private Consumer<?> clearSelectionConsumer;

    private Integer currentSelectedIndexInList;

    private final Map<String, String> mapTextLabelField;

    private final List<ISpecificTextRefreshPanel> specificTextRefreshPanelList;

    private final Map<String, String> mapSelectedValue;

    private final List<Integer> allSelectedIndexList;

    public FrequencyOrderTextModel(IConfigurationControler controler, IModalFrameRepack modalFrameRepack) {
        this.controler = controler;
        this.modalFrameRepack = modalFrameRepack;
        this.frequencyOrderList = new ArrayList<>();
        this.frequencyOrderList.addAll(controler.getFrequencyOrderList());
        this.specificTextRefreshPanelList = new ArrayList<>();
        this.mapTextLabelField = new HashMap<>();
        this.mapSelectedValue = new HashMap<>();
        this.allSelectedIndexList = new ArrayList<>();
        this.currentSelectedIndexInList = null;
        this.mapTextLabelField.put(FrequencyOrderEnum.ORDER.name(), FrequencyOrderEnum.ORDER.getLabel());
        this.mapTextLabelField.put(FrequencyOrderEnum.WORD.name(), FrequencyOrderEnum.WORD.getLabel());
        this.mapTextLabelField.put(FrequencyOrderEnum.FREQUENCY.name(), FrequencyOrderEnum.FREQUENCY.getLabel());
        this.mapTextLabelField.put(FrequencyOrderEnum.NORMALIZE_FREQUENCY.name(), FrequencyOrderEnum.NORMALIZE_FREQUENCY.getLabel());
    }

    public List<FrequencyOrder> getFrequencyOrderList() {
        return frequencyOrderList;
    }

    @Override
    public void setClearSelectionConsumer(Consumer<?> consumer) {
        this.clearSelectionConsumer = consumer;
    }

    @Override
    public void clearAllSelectedIndexList() {
        this.allSelectedIndexList.clear();
        this.currentSelectedIndexInList = null;
    }

    @Override
    public void addIndexToSelectedIndexList(Integer index) {
        this.allSelectedIndexList.add(index);
        fillSelectedValueMap();
        this.specificTextRefreshPanelList.forEach(st -> st.refreshAfterSelectedIndex());
    }

    @Override
    public void setCurrentSelectedIndexInList(Integer index) {
        this.currentSelectedIndexInList = index;
    }

    @Override
    public void updateField(String key, String value) {
        updateFrequencyOrderInControler();
    }

    @Override
    public void addSpecificField(Map<String, JTextField> mapKeyFieldTextField) {
        FrequencyOrder frequencyOrder = new FrequencyOrderBuilder()
                .order(Integer.parseInt(mapKeyFieldTextField.get(FrequencyOrderEnum.ORDER.name()).getText()))
                .word(mapKeyFieldTextField.get(FrequencyOrderEnum.WORD.name()).getText())
                .frequency(Integer.parseInt(mapKeyFieldTextField.get(FrequencyOrderEnum.FREQUENCY.name()).getText()))
                .normalizeFrequency(Double.parseDouble(mapKeyFieldTextField.get(FrequencyOrderEnum.NORMALIZE_FREQUENCY.name()).getText()))
                .build();
        frequencyOrderList.add(frequencyOrder);
        loadAllField();
        updateFrequencyOrderInControler();
    }

    @Override
    public void updateSpecificField(Map<String, JTextField> mapKeyFieldTextField) {
        if (haveCurrentSelectedIndexInList()) {
            FrequencyOrder frequencyOrder = this.frequencyOrderList.get(this.currentSelectedIndexInList);
            frequencyOrder.setOrder(Integer.parseInt(mapKeyFieldTextField.get(FrequencyOrderEnum.ORDER.name()).getText()));
            frequencyOrder.setWord(mapKeyFieldTextField.get(FrequencyOrderEnum.WORD.name()).getText());
            frequencyOrder.setFrequency(Integer.parseInt(mapKeyFieldTextField.get(FrequencyOrderEnum.FREQUENCY.name()).getText()));
            frequencyOrder.setNormalizeFrequency(Double.parseDouble(mapKeyFieldTextField.get(FrequencyOrderEnum.NORMALIZE_FREQUENCY.name()).getText()));
            loadAllField();
            updateFrequencyOrderInControler();
        }
    }

    @Override
    public void removeSpecificField() {
        if (haveCurrentSelectedIndexInList() && !this.allSelectedIndexList.isEmpty()) {
            List<FrequencyOrder> newList = new LinkedList<>();
            for (int i = 0; i < this.frequencyOrderList.size(); i++) {
                if (!this.allSelectedIndexList.contains(i)) {
                    newList.add(this.frequencyOrderList.get(i));
                }
            }
            this.frequencyOrderList.clear();
            this.frequencyOrderList.addAll(newList);
            this.allSelectedIndexList.clear();
            loadAllField();
            updateFrequencyOrderInControler();
        }
    }

    @Override
    public Map<String, String> getCurrentSelectedKeyValueMap() {
        return this.mapSelectedValue;
    }

    @Override
    public Map<String, String> getMapTextLabelField() {
        return Collections.unmodifiableMap(this.mapTextLabelField);
    }

    @Override
    public JLabel createJLabel(String text) {
        return new JLabel(text);
    }

    @Override
    public Boolean haveCurrentSelectedIndexInList() {
        return null != this.currentSelectedIndexInList && this.currentSelectedIndexInList >= 0;
    }

    @Override
    public void addSpecificTextRefresh(ISpecificTextRefreshPanel specificTextRefreshPanel) {
        this.specificTextRefreshPanelList.add(specificTextRefreshPanel);
    }

    @Override
    public void loadAllField() {
        this.specificTextRefreshPanelList.forEach(ISpecificTextRefreshPanel::refresh);
        this.modalFrameRepack.repack();
    }

    @Override
    public void updateFromCell(Integer rowIndex, Integer columnIndex, Object newValue) {
        FrequencyOrder frequencyOrder = frequencyOrderList.get(rowIndex);
        FrequencyOrderEnum.getFrequencyOrderEnumByOrder(columnIndex).getSaveValueBiConsumer().accept(frequencyOrder, newValue);
        updateFrequencyOrderInControler();
    }

    /**
     * Permet de remplir la map des valeurs
     */
    private void fillSelectedValueMap() {
        if (haveCurrentSelectedIndexInList()) {
            FrequencyOrder frequencyOrder = this.frequencyOrderList.get(this.currentSelectedIndexInList);
            this.mapSelectedValue.put(FrequencyOrderEnum.ORDER.name(), String.valueOf(frequencyOrder.getOrder()));
            this.mapSelectedValue.put(FrequencyOrderEnum.WORD.name(), frequencyOrder.getWord());
            this.mapSelectedValue.put(FrequencyOrderEnum.FREQUENCY.name(), String.valueOf(frequencyOrder.getFrequency()));
            this.mapSelectedValue.put(FrequencyOrderEnum.NORMALIZE_FREQUENCY.name(), String.valueOf(frequencyOrder.getNormalizeFrequency()));
        }
    }

    /**
     * Permet de mettre à jour tous les champs coté serveur
     */
    private void updateFrequencyOrderInControler() {
        controler.saveFrequencyOrder(this.frequencyOrderList);
    }
}
