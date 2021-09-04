package model.analyze.cmd;

import model.PojoBuilder;
import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;

@PojoBuilder
public class AnalysisDetailResultDisplayCmd {

    @NotEmpty(message = "La liste des clés filtrés")
    private List<String> keyTextFilteredList;

    @NotEmpty(message = "la liste des clés")
    private Set<String> keyFieldSet;

    @NotNull(message = "la map de prétraitement")
    private Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap;

    public List<String> getKeyTextFilteredList() {
        return keyTextFilteredList;
    }

    public void setKeyTextFilteredList(List<String> keyTextFilteredList) {
        this.keyTextFilteredList = keyTextFilteredList;
    }

    public Set<String> getKeyFieldSet() {
        return keyFieldSet;
    }

    public void setKeyFieldSet(Set<String> keyFieldSet) {
        this.keyFieldSet = keyFieldSet;
    }

    public Map<LexicometricConfigurationEnum, String> getPreTreatmentListLexicometricMap() {
        return preTreatmentListLexicometricMap;
    }

    public void setPreTreatmentListLexicometricMap(Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap) {
        this.preTreatmentListLexicometricMap = preTreatmentListLexicometricMap;
    }
}
