package view.beans;

import model.PojoBuilder;
import model.analyze.constants.ActionEditTableEnum;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 *
 * Classe permettant de mettre à jour un élément d'une table
 *
 */
@PojoBuilder
public class EditTableElement {

    private String oldValue;

    @NotNull
    private String value;

    @NotNull
    private ActionEditTableEnum actionEditTableEnum;

    private Optional<EditTableElement> linkedElementOptional = Optional.empty();

    /**
     * Permet de se procurer la valeur de la liste
     * @return la valeur de la liste
     */
    public String getValue() {
        return value;
    }

    /**
     * Permet de définir la valeur de la liste
     * @return la valeur de la liste
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Permet de se procurer l'élément rattaché à cette valeur
     * @return l'élément rattaché à cette valeur
     */
    public Optional<EditTableElement> getLinkedElement() {
        return linkedElementOptional;
    }

    /**
     * Permet de définir l'élément rattaché à cette valeur
     * @param linkedElement l'élément rattaché à cette valeur
     */
    public void setLinkedElement(Optional<EditTableElement> linkedElement) {
        this.linkedElementOptional = linkedElement;
    }

    /**
     * Permet de se procurer l'ancienne valeur de la liste
     * @return l'ancienne valeur de la liste
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * Permet de définir l'ancienne valeur de la liste
     * @param oldValue l'ancienne valeur de la liste
     */
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    /**
     * Permet de se procurer le type d'action qui s'est produite sur cette valeur
     * @return le type d'action qui s'est produite sur cette valeur
     */
    public ActionEditTableEnum getActionEditTableEnum() {
        return actionEditTableEnum;
    }
    /**
     * Permet de définir le type d'action qui s'est produite sur cette valeur
     * @@param actionEditTableEnum  le type d'action qui s'est produite sur cette valeur
     */
    public void setActionEditTableEnum(ActionEditTableEnum actionEditTableEnum) {
        this.actionEditTableEnum = actionEditTableEnum;
    }

    /**
     * Permet de savoir si il y a des changements
     * @return Vrai si il y a des changements
     */
    public Boolean haveChanged() {
        return !value.equals(oldValue);
    }

    @Override
    public String toString() {
        return "EditTableElement{" +
                "oldValue='" + oldValue + '\'' +
                ", value='" + value + '\'' +
                ", actionEditTableEnum=" + actionEditTableEnum +
                ", linkedElementOptional=" + linkedElementOptional +
                '}';
    }
}
