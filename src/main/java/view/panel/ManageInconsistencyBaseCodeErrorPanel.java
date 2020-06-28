package view.panel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import view.beans.BaseCodeError;
import view.interfaces.IManageInconsistencyBaseCodeErrorPanel;
import view.renders.InconsistencyBaseCodeErrorRenderer;

/**
 * 
 * Panel pour l'affichage des erreus d'incohérence au niveau des balises de base
 * code
 * 
 * @author jerem
 *
 */
public class ManageInconsistencyBaseCodeErrorPanel implements IManageInconsistencyBaseCodeErrorPanel {

	private final JPanel content;
	private final JList<BaseCodeError> displayFiltersList;
	private final JScrollPane contentWithScrollBar;
	private final List<BaseCodeError> filtersList;

	/**
	 * Constructeur
	 * 
	 * @param consumerToItemChange Consumer à appliquer en cas de changement de la
	 *                             liste
	 */
	public ManageInconsistencyBaseCodeErrorPanel() {
		this.content = new JPanel();
		this.filtersList = new LinkedList<>();
		this.displayFiltersList = new JList<BaseCodeError>();
		this.displayFiltersList.setCellRenderer(new InconsistencyBaseCodeErrorRenderer());
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
	private DefaultListModel<BaseCodeError> getDisplayTextModel() {
		DefaultListModel<BaseCodeError> dlm = new DefaultListModel<>();
		this.filtersList.forEach(filter -> dlm.addElement(filter));
		return dlm;
	}

	@Override
	public List<BaseCodeError> getAllInconsistencyBaseCodeErrorList() {
		return Collections.unmodifiableList(this.filtersList);
	}

	@Override
	public void addInconsistencyBaseCodeError(BaseCodeError errorToAdd) {
		if (this.filtersList.stream().noneMatch(f -> errorToAdd.equals(f))) {
			this.filtersList.add(errorToAdd);
			refresh();
		}
	}

	@Override
	public BaseCodeError getFilterSelected() {
		return this.displayFiltersList.getSelectedValue();
	}

	@Override
	public void removeInconsistencyBaseCodeError(BaseCodeError errorToRemove) {
		if (this.filtersList.contains(errorToRemove)) {
			this.filtersList.remove(errorToRemove);
			refresh();
		}
	}

}
