package view.beans;

import view.utils.ConfigurationUtils;
import view.utils.Constants;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum FrequencyOrderEnum {
    ORDER(0, ConfigurationUtils.getInstance()
            .getDisplayMessage(Constants.WINDOW_MANAGE_FREQUENCY_ORDER_TABLE_HEADER_ORDER_LABEL), FrequencyOrder::getOrder, (o, v) -> o.setOrder(Integer.parseInt(v.toString()))),
    WORD(1, ConfigurationUtils.getInstance()
            .getDisplayMessage(Constants.WINDOW_MANAGE_FREQUENCY_ORDER_TABLE_HEADER_WORD_LABEL), FrequencyOrder::getWord, (o, v) -> o.setWord((String) v)),
    FREQUENCY(2, ConfigurationUtils.getInstance()
            .getDisplayMessage(Constants.WINDOW_MANAGE_FREQUENCY_ORDER_TABLE_HEADER_FREQUENCY_LABEL), FrequencyOrder::getFrequency, (o, v) -> o.setFrequency(Integer.parseInt(v.toString()))),
    NORMALIZE_FREQUENCY(3, ConfigurationUtils.getInstance()
            .getDisplayMessage(Constants.WINDOW_MANAGE_FREQUENCY_ORDER_TABLE_HEADER_NORMALIZE_FREQUENCY_LABEL), FrequencyOrder::getNormalizeFrequency, (o, v) -> o.setNormalizeFrequency(Double.parseDouble(v.toString())));

    private final int order;
    private final String label;

    private final Function<FrequencyOrder, Object> extractBeanFunction;

    private final BiConsumer<FrequencyOrder, Object> saveValueBiConsumer;

    FrequencyOrderEnum(int order, String label, Function<FrequencyOrder, Object> extractBeanFunction, BiConsumer<FrequencyOrder, Object> saveValueBiConsumer) {
        this.order = order;
        this.label = label;
        this.extractBeanFunction = extractBeanFunction;
        this.saveValueBiConsumer = saveValueBiConsumer;
    }

    public static LinkedList<String> getHeaderList() {
        return Arrays.stream(values()).sorted(Comparator.naturalOrder()).map(FrequencyOrderEnum::getLabel).collect(Collectors.toCollection(LinkedList::new));
    }

    public static FrequencyOrderEnum getFrequencyOrderEnumByOrder(int order) {
        return Arrays.stream(values()).filter(x -> x.order == order).findFirst().orElseThrow(() -> new RuntimeException("Impossible de trouver l'ordre"));
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

    public Function<FrequencyOrder, Object> getExtractBeanFunction() {
        return extractBeanFunction;
    }

    public BiConsumer<FrequencyOrder, Object> getSaveValueBiConsumer() {
        return saveValueBiConsumer;
    }

}
