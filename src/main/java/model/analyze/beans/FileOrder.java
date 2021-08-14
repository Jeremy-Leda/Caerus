package model.analyze.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 *
 * Bean permettant d'écrire l'odre d'un fichier
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileOrder {

    private String nameFile;
    private Integer number;

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileOrder fileOrder = (FileOrder) o;
        return Objects.equals(nameFile, fileOrder.nameFile) && Objects.equals(number, fileOrder.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameFile, number);
    }
}
