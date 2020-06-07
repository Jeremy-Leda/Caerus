package view.abstracts;

import java.awt.FlowLayout;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import view.interfaces.IContentTextPanel;

/**
 * 
 * Abstract permettant d'effectuer la saisie des textes Permet la completion
 * automatique des champs dans l'interface
 *
 * @param <T> Type attendu pour l'affichage
 * @author jerem
 * 
 */
public abstract class ContentTextPanelAbstract<T extends JComponent> implements IContentTextPanel {

	private final Map<T, JLabel> mapTextLabelField = new LinkedHashMap<T, JLabel>();
	private final Map<String, T> mapKeyFieldTextField = new LinkedHashMap<String, T>();
	private JPanel panContent = new JPanel(new FlowLayout(FlowLayout.LEFT));

	protected void clearAndFillMap(Map<String, String> informationFieldMap) {
		this.mapTextLabelField.clear();
		this.mapKeyFieldTextField.clear();
		panContent.removeAll();
		informationFieldMap.forEach((k, v) -> {
			T element = createNewComponentWithText(k);
			//getFocusListenerList(k).forEach(focus -> element.addFocusListener(focus));
			StringBuilder sb = new StringBuilder(v.replace("[", "").replace("]", ""));
			sb.append(" : ");
			JLabel labelField = new JLabel(sb.toString());
			this.mapTextLabelField.put(element, labelField);
			this.mapKeyFieldTextField.put(k, element);
		});
		mapTextLabelField.forEach((textField, labelField) -> {
			JPanel panMetaField = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panMetaField.add(labelField);
			panMetaField.add(textField);
			panContent.add(panMetaField);
		});
	}

	/**
	 * Permet de créer le nouveau composant avec un text chargé
	 * 
	 * @param key Clé pour se procurer le texte associé
	 * @return le composant créé et alimenté
	 */
	public abstract T createNewComponentWithText(String key);

	/**
	 * Permet de se procurer la valeur d'un champ
	 * 
	 * @param field champ dont on veut la valeur
	 * @return la valeur
	 */
	public abstract String getValueFromField(T field);
	
	/**
	 * Permet de définir la valeur d'un champ
	 * 
	 * @param field champ dont on veut définir la valeur
	 * @param value valeur à définir
	 */
	public abstract void setValueToField(T field, String value);
	
	/**
	 * Permet de se procurer le champ pour ajouter un listner
	 * 
	 * @param field champ dont on veut ajouter un listener
	 * @return l'objet pouvant prendre le listener
	 */
	public abstract JComponent getObjectForListener(T field);

	@Override
	public JPanel getJPanel() {
		return this.panContent;
	}

	@Override
	public void refresh(String titlePanel) {
		panContent.setBorder(BorderFactory.createTitledBorder(titlePanel));
		panContent.setLayout(new BoxLayout(panContent, BoxLayout.Y_AXIS));
	}

	@Override
	public void addKeyListenerOnAllField(KeyListener keyListener) {
		this.mapKeyFieldTextField.keySet().forEach(key -> addKeyListener(key, keyListener));
	}

	@Override
	public void addKeyListener(String key, KeyListener keyListener) {
		if (this.mapKeyFieldTextField.containsKey(key)) {
			getObjectForListener(this.mapKeyFieldTextField.get(key)).addKeyListener(keyListener);
		}
	}

	@Override
	public String getValue(String key) {
		if (this.mapKeyFieldTextField.containsKey(key)) {
			return getValueFromField(this.mapKeyFieldTextField.get(key));
		}
		return null;
	}

	@Override
	public Map<String, String> getFieldValueMap() {
		return this.mapKeyFieldTextField.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> getValueFromField(e.getValue())));
	}

	@Override
	public void addFocusListenerOnAllField(FocusListener focusListener) {
		this.mapKeyFieldTextField.keySet().forEach(key -> addFocusListener(key, focusListener));
	}

	@Override
	public void addFocusListener(String key, FocusListener focusListener) {
		if (this.mapKeyFieldTextField.containsKey(key)) {
			getObjectForListener(this.mapKeyFieldTextField.get(key)).addFocusListener(focusListener);
		}
	}

	@Override
	public void setEnabled(String key, boolean enabled) {
		if (this.mapKeyFieldTextField.containsKey(key)) {
			this.mapKeyFieldTextField.get(key).setEnabled(enabled);
		}
	}

	@Override
	public void setEnabledOnAllField(boolean enabled) {
		this.mapKeyFieldTextField.values().forEach(textField -> textField.setEnabled(enabled));
	}
	
	@Override
	public void setValue(String key, String newValue) {
		if (this.mapKeyFieldTextField.containsKey(key)) {
			setValueToField(this.mapKeyFieldTextField.get(key), newValue);
		}
	}

}
