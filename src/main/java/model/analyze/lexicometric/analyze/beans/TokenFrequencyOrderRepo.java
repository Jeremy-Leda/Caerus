package model.analyze.lexicometric.analyze.beans;

public class TokenFrequencyOrderRepo {

    private int order;
    private String word;
    private int frequency;

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

}
