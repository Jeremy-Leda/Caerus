package view.panel.model;

import view.beans.SelectedObjectTable;
import view.interfaces.ISelectedEditTableModel;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 *
 * Model pour la table de sélection
 *
 * @param <T> Type de l'objet de donnée
 */
public class SelectedEditTableModel<T> implements ISelectedEditTableModel<T> {

    private final LinkedList<SelectedObjectTable<T>> rowList = new LinkedList<>();
    private final LinkedList<SelectedObjectTable<T>> rowListFiltered = new LinkedList<>();


    @Override
    public Collection<SelectedObjectTable<T>> getRows() {
        return Collections.unmodifiableCollection(rowListFiltered);
    }

    @Override
    public SelectedObjectTable getRow(Integer rowIndex) {
        return rowListFiltered.get(rowIndex);
    }

    @Override
    public void cleanAndFill(Collection<SelectedObjectTable<T>> collection) {
        this.rowList.clear();
        this.rowList.addAll(collection);
        this.rowListFiltered.clear();
        this.rowListFiltered.addAll(collection);
    }

    @Override
    public Collection<SelectedObjectTable<T>> getRowsByState(boolean isSelected) {
        return Collections.unmodifiableCollection(
                this.rowListFiltered.stream()
                        .filter(s -> s.isChecked() == isSelected)
                        .collect(Collectors.toCollection(LinkedList::new)));
    }

    @Override
    public void filterData(BiFunction<Collection<SelectedObjectTable<T>>, T, Collection<SelectedObjectTable<T>>> filterFunction, Optional<T> filter) {
        this.rowListFiltered.clear();
        if (filter.isPresent()) {
            this.rowListFiltered.addAll(filterFunction.apply(this.rowList, filter.get()));
        } else {
            this.rowListFiltered.addAll(this.rowList);
        }
    }


}
