package view.panel;

import org.apache.commons.lang3.StringUtils;
import view.interfaces.ILabelsPanel;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * Panel pour l'affichage de label
 *
 */
public class LabelsPanel implements ILabelsPanel {

    private final List<JLabel> headerLabelList = new LinkedList<>();
    private final List<JLabel> valueLabelList = new LinkedList<>();
    private final JPanel content = new JPanel();

    /**
     * Constructeur
     * @param nbLabel nombre de label
     */
    public LabelsPanel(String title, Integer nbLabel) {
        if (StringUtils.isNotBlank(title)) {
            this.content.setBorder(BorderFactory.createTitledBorder(title));
        }
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        IntStream.range(0, nbLabel)
                .forEach(s -> {
                    this.headerLabelList.add(new JLabel());
                    this.valueLabelList.add(new JLabel());
                    content.add(createLabelPanel(s));
                });
    }

    @Override
    public JComponent getJPanel() {
        return this.content;
    }

    @Override
    public void setLabel(Integer id, String header, String value) {
        setLabel(id, header, false, value, false);
    }

    @Override
    public void setLabel(Integer id, String header, Boolean boldHeader, String value, Boolean boldValue) {
        JLabel headerLabel = this.headerLabelList.get(id);
        headerLabel.setText(header + " : ");
        if (boldHeader) {
            headerLabel.setFont(getBoldFont(headerLabel));
        }
        JLabel valueLabel = this.valueLabelList.get(id);
        valueLabel.setText(value);
        if (boldValue) {
            valueLabel.setFont(getBoldFont(valueLabel));
        }
    }

    @Override
    public void addLabel(String header, Boolean boldHeader, String value, Boolean boldValue) {
        int id = this.valueLabelList.size();
        this.headerLabelList.add(new JLabel());
        this.valueLabelList.add(new JLabel());
        content.add(createLabelPanel(id));
        setLabel(id, header, boldHeader, value, boldValue);
    }

    private Font getBoldFont(JLabel label) {
        Font font = label.getFont();
        return font.deriveFont(Font.BOLD);
    }

    /**
     * Permet de créer le panel pour l'affichage d'un label
     * @param id identifiant
     * @return le panel
     */
    private JPanel createLabelPanel(Integer id) {
        JPanel panel = new JPanel();
        panel.add(this.headerLabelList.get(id));
        panel.add(this.valueLabelList.get(id));
        return panel;
    }
}