package model.analyze.lexicometric.services;

import view.beans.EditTableElement;

import java.util.*;

public class LexicometricEditTableService<F> {

    public <T> T add(EditTableElement editTableElement, T origin) {
        if (origin instanceof Set) {
            return (T) addWithSet(editTableElement, (Set<F>) origin);
        }
        if (origin instanceof Map) {
            return (T) addWithMap(editTableElement, (Map<F, ? extends Object>) origin);
        }
        return null;
    }

    public <T> T  remove(EditTableElement editTableElement, T origin) {
        if (origin instanceof Set) {
            return (T) removeWithSet(editTableElement, (Set<F>) origin);
        }
        if (origin instanceof Map) {
            return (T) removeWithMap(editTableElement, (Map<F, ? extends Object>) origin);
        }
        return null;
    }

    public <T> T update(EditTableElement editTableElement, T origin) {
        if (origin instanceof Set) {
            return (T) updateWithSet(editTableElement, (Set<F>) origin);
        }
        if (origin instanceof Map) {
            return (T) updateWithMap(editTableElement, (Map<F, ? extends Object>) origin);
        }
        return null;
    }

    private Set<F> addWithSet(EditTableElement editTableElement, Set<F> origin) {
        Set<F> result = new HashSet<>();
        result.addAll(origin);
        result.add((F) editTableElement.getValue());
        return result;
    }

    private Set<F> updateWithSet(EditTableElement editTableElement, Set<F> origin) {
        Set<F> result = new HashSet<>();
        result.addAll(origin);
        if (editTableElement.haveChanged()) {
            result.remove(editTableElement.getOldValue());
            result.add((F) editTableElement.getValue());
        }
        return result;
    }

    private Set<F> removeWithSet(EditTableElement editTableElement, Set<F> origin) {
        Set<F> result = new HashSet<>();
        result.addAll(origin);
        result.remove(editTableElement.getValue());
        return result;
    }

    private <X> Map<F, X> addWithMap(EditTableElement editTableElement, Map<F, X> origin) {
        Map<F, X> result = new HashMap<>();
        result.putAll(origin);
        result.put((F) editTableElement.getValue(), null);
        return result;
    }

    private <X> Map<F, X> removeWithMap(EditTableElement editTableElement, Map<F, X> origin) {
        Map<F, X> result = new HashMap<>();
        result.putAll(origin);
        result.remove(editTableElement.getValue());
        return result;
    }

    private <X> Map<F, X> updateWithMap(EditTableElement editTableElement, Map<F, X> origin) {
        Map<F, X> result = new HashMap<>();
        result.putAll(origin);
        if (editTableElement.haveChanged()) {
            X valueOfMap = result.get(editTableElement.getOldValue());
            result.remove(editTableElement.getOldValue());
            result.put((F) editTableElement.getValue(), valueOfMap);
        } else if (editTableElement.getLinkedElement().isPresent()) {
            EditTableElement childEditTableElement = (EditTableElement) editTableElement.getLinkedElement().get();
            X values = origin.get(editTableElement.getValue());
            X newValues = (X) childEditTableElement.getActionEditTableEnum().getApplyFunction().apply(this, childEditTableElement, values);
            result.put((F) editTableElement.getValue(), newValues);
        }
        return result;
    }


}
