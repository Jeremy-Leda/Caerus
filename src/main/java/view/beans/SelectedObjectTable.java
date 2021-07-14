package view.beans;

public class SelectedObjectTable<T> {

    private boolean checked = false;
    private final T data;

    public SelectedObjectTable(T data) {
        this.data = data;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public T getData() {
        return data;
    }
}
