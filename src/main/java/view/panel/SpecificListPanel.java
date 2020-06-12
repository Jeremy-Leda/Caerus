package view.panel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringUtils;

import view.interfaces.ISpecificTextModel;
import view.interfaces.ISpecificTextRefreshPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Classe permettant la création des listes pour l'affichage des informations sous formes de listes
 * 
 * @author jerem
 *
 */
public class SpecificListPanel implements ISpecificTextRefreshPanel  {
	
	private final ISpecificTextModel specificTextModel;
	private final JPanel detailsListPanel;
	private final JScrollPane scrollPanel;
	private Boolean autoPilotSelectedIndex = Boolean.FALSE;
	private final Map<String, JList<String>> mapKeyJList; 

	/**
	 * Constructeur
	 * @param specificTextModel Model specifique
	 */
	public SpecificListPanel(ISpecificTextModel specificTextModel) {
		this.specificTextModel = specificTextModel;
		this.detailsListPanel = new JPanel();
		this.scrollPanel = new JScrollPane(detailsListPanel);
		this.mapKeyJList = new HashMap<String, JList<String>>();
		detailsListPanel.setBorder(BorderFactory
				.createTitledBorder(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_DETAILS_PANEL_TITLE)));

	}
	
	/**
	 * Permet de charger l'affichage de la fenêtre
	 */
	private void loadDisplayDetailsListPanel() {
		detailsListPanel.removeAll();
		this.specificTextModel.getMapKeyFieldListField().forEach((key,valueList) -> {
			JPanel contentDetail = new JPanel();
			BoxLayout boxlayout = new BoxLayout(contentDetail, BoxLayout.Y_AXIS);
			contentDetail.setLayout(boxlayout);
			contentDetail.add(createLabel(key));
			contentDetail.add(createListValuePanel(key, valueList));
			detailsListPanel.add(contentDetail);
		});
		boolean allValueOfListIsEmpty = this.specificTextModel.getMapKeyFieldListField().values().stream().allMatch(valueList -> valueList.stream().filter(s -> StringUtils.isNotBlank(s)).count() == 0);
		if (allValueOfListIsEmpty) {
			this.mapKeyJList.values().forEach(list -> ((DefaultListModel<String>)list.getModel()).removeAllElements());
		}
	}

	/**
	 * Permet de créer le label
	 * @param key clé
	 * @return le label
	 */
	private JLabel createLabel(String key) {
		return this.specificTextModel.createJLabel(this.specificTextModel.getMapTextLabelField().get(key));
	}
	
	/**
	 * Permet de créer la liste des valeur panel
	 * @param key clé
	 * @param value liste des valeurs
	 * @return le panel
	 */
	private JPanel createListValuePanel(String key, List<String> value) {
		JPanel listPanel = new JPanel();
		DefaultListModel<String> dlm = new DefaultListModel<String>();
		for (int i = 0; i < value.size(); i++) {
			Integer numberLine = i+1;
			StringBuilder sb = new StringBuilder(numberLine.toString());
			sb.append(". ");
			sb.append(value.get(i));
			dlm.addElement(sb.toString());
		}
		JList<String> detailsList = new JList<String>(dlm);
		detailsList.addListSelectionListener(updateIndex());
		this.mapKeyJList.put(key, detailsList);
		listPanel.add(detailsList);
		return listPanel;
	}
	
	
	/**
	 * Permet de mettre à jour l'index sélectionné
	 * @return
	 */
	private ListSelectionListener updateIndex() {
		return new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!autoPilotSelectedIndex) {
					autoPilotSelectedIndex = Boolean.TRUE;
					JList<?> lsm = (JList<?>) e.getSource();
					specificTextModel.setCurrentSelectedIndexInList(lsm.getSelectedIndex());
					autoPilotSelectedIndex = Boolean.FALSE;
				}
			}
		};

	}
	
	@Override
	public JComponent getJPanel() {
		return scrollPanel;
	}

	@Override
	public void refresh() {
		loadDisplayDetailsListPanel();
	}
	

	@Override
	public void refreshAfterSelectedIndex() {
		this.mapKeyJList.values().forEach(jlist -> jlist.setSelectedIndex(specificTextModel.getCurrentSelectedIndexInList()));
	}
	
}
