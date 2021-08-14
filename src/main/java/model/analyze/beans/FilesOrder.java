package model.analyze.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * Bean permettant d'écrire l'odre des fichiers
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilesOrder {

    private Set<FileOrder> fileOrderSet = new HashSet<>();

    public Set<FileOrder> getFileOrderSet() {
        return fileOrderSet;
    }

    public void setFileOrderSet(Set<FileOrder> fileOrderSet) {
        this.fileOrderSet = fileOrderSet;
    }

    @JsonIgnore
    public Integer getMaxNumber() {
        return fileOrderSet.stream().max(Comparator.comparing(FileOrder::getNumber)).map(FileOrder::getNumber).orElse(0) + 1;
    }
}
