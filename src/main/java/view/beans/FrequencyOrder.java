package view.beans;

import model.PojoBuilder;

@PojoBuilder
public class FrequencyOrder {

    private int order;
    private String word;
    private int frequency;
    private double normalizeFrequency;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public double getNormalizeFrequency() {
        return normalizeFrequency;
    }

    public void setNormalizeFrequency(double normalizeFrequency) {
        this.normalizeFrequency = normalizeFrequency;
    }
}
