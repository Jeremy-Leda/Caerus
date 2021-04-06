package view.panel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import view.beans.Filter;
import view.interfaces.IManageFilterPanel;
import view.renders.FilterRenderer;

/**
 * 
 * Panel pour l'affichage des textes filtrés
 * 
 * @author jerem
 *
 */
public class ManageFilterPanel implements IManageFilterPanel {

	private final JPanel content;
	private final JList<Filter> displayFiltersList;
	private final JScrollPane contentWithScrollBar;
	private final List<Filter> filtersList;
	
	
	/**
	 * Constructeur
	 * @param consumerToItemChange Consumer à appliquer en cas de changement de la liste
	 */
	public ManageFilterPanel(Consumer<Void> consumerToItemChange) {
		this.content = new JPanel();
		this.filtersList = new LinkedList<>();
		this.displayFiltersList = new JList<Filter>();
		this.displayFiltersList.setCellRenderer(new FilterRenderer());
		this.displayFiltersList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (null != consumerToItemChange) {
					consumerToItemChange.accept(null);
				}
			}
		});
		this.content.add(displayFiltersList);
		this.contentWithScrollBar = new JScrollPane(this.content);
	}
	
	@Override
	public JComponent getJPanel() {
		return this.contentWithScrollBar;
	}

	/**
	 * Permet de rafraichir l'affichage
	 */
	private void refresh() {
		this.displayFiltersList.removeAll();
		this.displayFiltersList.setModel(getDisplayTextModel());
	}
	
	
	/**
	 * Permet de se procurer la liste des filtre à afficher
	 * @return la liste des filtres à afficher
	 */
	private DefaultListModel<Filter> getDisplayTextModel() {
		DefaultListModel<Filter> dlm = new DefaultListModel<>();
		this.filtersList.forEach(filter -> dlm.addElement(filter));
		return dlm;
	}


	@Override
	public void addFilter(Filter filterToAdd) {
		if (this.filtersList.stream().noneMatch(f -> filterToAdd.getField().equals(f.getField()))) {
			this.filtersList.add(filterToAdd);
			refresh();
		}
	}

	@Override
	public Filter getFilterSelected() {
		return this.displayFiltersList.getSelectedValue();
	}

	@Override
	public void removeFilter(Filter filterToRemove) {
		if (this.filtersList.contains(filterToRemove)) {
			this.filtersList.remove(filterToRemove);
			refresh();
		}
	}

	@Override
	public List<Filter> getAllFiltersList() {
		return Collections.unmodifiableList(this.filtersList);
	}
}
