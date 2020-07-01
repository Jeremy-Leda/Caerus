package view.panel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import view.beans.InconsistencyError;
import view.interfaces.IManageInconsistencyErrorPanel;
import view.renders.InconsistencyErrorRenderer;

/**
 * 
 * Panel pour l'affichage des textes filtrés
 * 
 * @author jerem
 *
 */
public class ManageInconsistencyErrorPanel implements IManageInconsistencyErrorPanel {

	private final JPanel content;
	private final JList<InconsistencyError> displayFiltersList;
	private final JScrollPane contentWithScrollBar;
	private final List<InconsistencyError> filtersList;

	/**
	 * Constructeur
	 * 
	 * @param consumerToItemChange Consumer à appliquer en cas de changement de la
	 *                             liste
	 */
	public ManageInconsistencyErrorPanel() {
		this.content = new JPanel();
		this.filtersList = new LinkedList<>();
		this.displayFiltersList = new JList<InconsistencyError>();
		this.displayFiltersList.setCellRenderer(new InconsistencyErrorRenderer());
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
	 * 
	 * @return la liste des filtres à afficher
	 */
	private DefaultListModel<InconsistencyError> getDisplayTextModel() {
		DefaultListModel<InconsistencyError> dlm = new DefaultListModel<>();
		this.filtersList.forEach(filter -> dlm.addElement(filter));
		return dlm;
	}

	@Override
	public List<InconsistencyError> getAllInconsistencyErrorList() {
		return Collections.unmodifiableList(this.filtersList);
	}

	@Override
	public void addInconsistencyError(InconsistencyError errorToAdd) {
		if (this.filtersList.stream().noneMatch(f -> errorToAdd.equals(f))) {
			this.filtersList.add(errorToAdd);
			refresh();
		}
	}

	@Override
	public InconsistencyError getFilterSelected() {
		return this.displayFiltersList.getSelectedValue();
	}

	@Override
	public void removeInconsistencyError(InconsistencyError errorToRemove) {
		if (this.filtersList.contains(errorToRemove)) {
			this.filtersList.remove(errorToRemove);
			refresh();
		}
	}

}
