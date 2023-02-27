package model.analyze.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import view.beans.FrequencyOrder;

import java.util.Set;


/**
 *
 * Bean permettant de d'écrire le repo frequency order
 *
 * @author jerem
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FrequencyOrderRepository {

    private Set<FrequencyOrder> frequencyOrderSet;

    public Set<FrequencyOrder> getFrequencyOrderSet() {
        return frequencyOrderSet;
    }

    public void setFrequencyOrderSet(Set<FrequencyOrder> frequencyOrderSet) {
        this.frequencyOrderSet = frequencyOrderSet;
    }
}
