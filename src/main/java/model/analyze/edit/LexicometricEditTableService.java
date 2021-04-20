package model.analyze.edit;

import view.beans.EditTableElement;

import java.util.*;

public class LexicometricEditTableService {

    public <T> T add(EditTableElement editTableElement, T origin) {
        if (origin instanceof Set) {
            return (T) addWithSet(editTableElement, (Set<String>) origin);
        }
        if (origin instanceof Map) {
            return (T) addWithMap(editTableElement, (Map<String, ? extends Object>) origin);
        }
        return null;
    }

    public <T> T  remove(EditTableElement editTableElement, T origin) {
        if (origin instanceof Set) {
            return (T) removeWithSet(editTableElement, (Set<String>) origin);
        }
        if (origin instanceof Map) {
            return (T) removeWithMap(editTableElement, (Map<String, ? extends Object>) origin);
        }
        return null;
    }

    public <T> T update(EditTableElement editTableElement, T origin) {
        if (origin instanceof Set) {
            return (T) updateWithSet(editTableElement, (Set<String>) origin);
        }
        if (origin instanceof Map) {
            return (T) updateWithMap(editTableElement, (Map<String, ? extends Object>) origin);
        }
        return null;
    }

    private Set<String> addWithSet(EditTableElement editTableElement, Set<String> origin) {
        Set<String> result = new HashSet<>();
        result.addAll(origin);
        result.add(editTableElement.getValue());
        return result;
    }

    private Set<String> updateWithSet(EditTableElement editTableElement, Set<String> origin) {
        Set<String> result = new HashSet<>();
        result.addAll(origin);
        if (editTableElement.haveChanged()) {
            result.remove(editTableElement.getOldValue());
            result.add(editTableElement.getValue());
        }
        return result;
    }

    private Set<String> removeWithSet(EditTableElement editTableElement, Set<String> origin) {
        Set<String> result = new HashSet<>();
        result.addAll(origin);
        result.remove(editTableElement.getValue());
        return result;
    }

    private <X> Map<String, X> addWithMap(EditTableElement editTableElement, Map<String, X> origin) {
        Map<String, X> result = new HashMap<>();
        result.putAll(origin);
        result.put(editTableElement.getValue(), null);
        return result;
    }

    private <X> Map<String, X> removeWithMap(EditTableElement editTableElement, Map<String, X> origin) {
        Map<String, X> result = new HashMap<>();
        result.putAll(origin);
        result.remove(editTableElement.getValue());
        return result;
    }

    private <X> Map<String, X> updateWithMap(EditTableElement editTableElement, Map<String, X> origin) {
        Map<String, X> result = new HashMap<>();
        result.putAll(origin);
        if (editTableElement.haveChanged()) {
            X valueOfMap = result.get(editTableElement.getOldValue());
            result.remove(editTableElement.getOldValue());
            result.put(editTableElement.getValue(), valueOfMap);
        } else if (editTableElement.getLinkedElement().isPresent()) {
            EditTableElement childEditTableElement = editTableElement.getLinkedElement().get();
            X values = origin.get(editTableElement.getValue());
            X newValues = (X) childEditTableElement.getActionEditTableEnum().getApplyFunction().apply(this, childEditTableElement, values);
            result.put(editTableElement.getValue(), newValues);
        }
        return result;
    }


}
