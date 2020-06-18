package view.panel;

import java.awt.Dimension;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import view.interfaces.IListPanel;

/**
 * 
 * List box panel
 * 
 * @author jerem
 *
 */
public class ListPanel implements IListPanel {

	private final JPanel listPanel;
	private final JLabel labelList;
	private final JList<String> list;
	private final JScrollPane scrollPane;

	/**
	 * Constructeur
	 * @param titlePanel titre du panel
	 * @param label libellé de la liste
	 */
	public ListPanel(String titlePanel, String label) {
		this(label);
		this.listPanel.setBorder(BorderFactory.createTitledBorder(titlePanel));
	}

	/**
	 * Constructeur
	 * @param label libellé de la liste
	 */
	public ListPanel(String label) {
		this.listPanel = new JPanel();
		
		this.labelList = new JLabel(label);
		this.list = new JList<String>();
		this.list.setVisibleRowCount(10);
		this.scrollPane = new JScrollPane(this.list);
		this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		createContent();
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		this.listPanel.add(labelList);
		this.listPanel.add(this.scrollPane);
	}

	@Override
	public JComponent getJPanel() {
		return this.listPanel;
	}

	@Override
	public String getLabelSelected() {
		return this.list.getSelectedValue();
	}

	@Override
	public void addConsumerOnSelectChange(Consumer<?> consumer) {
		this.list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				consumer.accept(null);
			}
		});
	}

	@Override
	public void refresh(List<String> labels) {
		this.list.removeAll();
		DefaultListModel<String> dlm = new DefaultListModel<String>();
		labels.forEach(l -> dlm.addElement(l));
		this.list.setModel(dlm);
	}

	@Override
	public void selectItem(String itemToSelect) {
		this.list.setSelectedValue(itemToSelect, true);
	}

	@Override
	public int getItemCount() {
		return this.list.getModel().getSize();
	}

}
