package view.panel;

import view.beans.TableFilterObject;
import view.beans.TableFilterObjectBuilder;
import view.interfaces.ITableFilterObject;
import view.interfaces.ITableFilterPanel;
import view.interfaces.ITextBoxPanel;

import javax.swing.*;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * Filtre permettant de filtrer par un text sur les tableaux
 *
 */
public class TableFilterTextPanel implements ITableFilterPanel, ITextBoxPanel {

    private final ITextBoxPanel textBoxPanel = new TextBoxPanel(1, false);

    @Override
    public JComponent getJPanel() {
        return this.textBoxPanel.getJPanel();
    }

    @Override
    public ITableFilterObject getFilter() {
        return new TableFilterObjectBuilder()
                .stringValue(getValueOfTextBox(0))
                .build();
    }

    @Override
    public void addConsumerOnChange(Consumer<?> consumer) {
        this.textBoxPanel.addConsumerOnChange(0, consumer);
    }

    @Override
    public void setStaticLabel(String titlePanel, Map<Integer, String> textBoxIdTextMap) {
        this.textBoxPanel.setStaticLabel(titlePanel, textBoxIdTextMap);
    }

    @Override
    public String getValueOfTextBox(Integer number) {
        return textBoxPanel.getValueOfTextBox(number);
    }

    @Override
    public void addConsumerOnChange(Integer number, Consumer<?> consumer) {
        this.textBoxPanel.addConsumerOnChange(number, consumer);
    }
}
