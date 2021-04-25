package model.analyze.constants;

import io.vavr.Function3;
import model.analyze.lexicometric.services.LexicometricEditTableService;
import view.beans.EditTableElement;

/**
 *
 * Enumeration pour les types d'action que l'on peut faire sur les tables d'édition
 *
 */
public enum ActionEditTableEnum {
    ADD(LexicometricEditTableService::add),
    UPDATE(LexicometricEditTableService::update),
    REMOVE(LexicometricEditTableService::remove);

    private final Function3<LexicometricEditTableService, EditTableElement, Object, Object> applyFunction;

    /**
     * Constructeur
     * @param applyFunction la fonction qui va appliquer les changements et retourné la liste modifiée
     */
    ActionEditTableEnum(Function3<LexicometricEditTableService, EditTableElement, Object, Object> applyFunction) {
        this.applyFunction = applyFunction;
    }

    /**
     * Permet de se procurer la fonction qui va appliquer les changements et retourné la liste modifiée
     * @return la fonction qui va appliquer les changements et retourné la liste modifiée
     */
    public Function3<LexicometricEditTableService, EditTableElement, Object, Object> getApplyFunction() {
        return applyFunction;
    }
}
