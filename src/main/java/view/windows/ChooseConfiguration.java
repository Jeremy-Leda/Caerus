package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.exceptions.ErrorCode;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import org.apache.commons.lang3.StringUtils;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.PictureTypeEnum;
import view.interfaces.IAccessPanel;
import view.interfaces.IActionPanel;
import view.panel.ActionPanel;
import view.panel.InformationPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Fenêtre permettant de changer la configuration courante du programme caerus
 * 
 * @author jerem
 *
 */
public class ChooseConfiguration extends ModalJFrameAbstract {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2243489998955670907L;
	private final IAccessPanel textInformationPanel;
	private final IActionPanel actionPanel;
	private final JPanel panConfigurations;
	private final JLabel configurationListLabel;
	private final JComboBox<String> configurationListComboBox;
	private final JPanel content;

	public ChooseConfiguration(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CHANGE_CONFIGURATION_TITLE), configurationControler);
		this.panConfigurations = new JPanel();
		this.configurationListLabel = new JLabel();
		this.configurationListComboBox = new JComboBox<String>();
		this.actionPanel = new ActionPanel(2);
		this.content = new JPanel();
		File configurationFolder = configurationControler.getConfigurationFolder().orElseThrow(() ->
				new ServerException().addInformationException(new InformationExceptionBuilder()
					.errorCode(ErrorCode.ERROR_CONFIGURATION)
					.build()));
		this.textInformationPanel = new InformationPanel(PictureTypeEnum.INFORMATION, 
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CHANGE_CONFIGURATION_MESSAGE_PANEL_TITLE),
				String.format(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CHANGE_CONFIGURATION_MESSAGE_CONTENT),
						configurationFolder), true, false);
		createWindow();
	}

	@Override
	public void initComponents() {
		loadConfigurations();
		createConfigurationsPanel();
		refreshMessage();
		addActions();
		createContent();
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}
	
	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.textInformationPanel.getJPanel());
		content.add(this.panConfigurations);
		content.add(actionPanel.getJPanel());
	}
	
	/***
	 * Permet de créer le panel pour le choix de la configuration
	 */
	private void createConfigurationsPanel() {
		panConfigurations.setLayout(new BoxLayout(panConfigurations, BoxLayout.Y_AXIS));
		JPanel subPanConfiguration= new JPanel();
		subPanConfiguration.add(configurationListLabel);
		subPanConfiguration.add(configurationListComboBox);
		panConfigurations.add(subPanConfiguration);
	}
	
	/**
	 * Permet de rafraichir les messages
	 */
	private void refreshMessage() {
		panConfigurations.setBorder(BorderFactory.createTitledBorder(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CHANGE_CONFIGURATION_PANEL_TITLE)));
		configurationListLabel.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CHANGE_CONFIGURATION_LIST_LABEL));
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CHANGE_CONFIGURATION_BUTTON_APPLY_AND_CLOSE));
		messageButtonMap.put(1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CHANGE_CONFIGURATION_BUTTON_CLOSE));
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CHANGE_CONFIGURATION_BUTTONS_PANEL_TITLE), messageButtonMap);
	}
	 
	/**
	 * Permet d'ajouter les actions
	 */
	private void addActions() {
		this.actionPanel.addAction(0, changeConfigurationAndCloseAction());
		this.actionPanel.addAction(1, closeAction());
		this.actionPanel.setIconButton(0, PictureTypeEnum.SAVE);
	}

	/**
	 * Action de fermeture de la fenêtre
	 * @return l'action listener
	 */
	private ActionListener closeAction() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		};
	}
	
	/**
	 * Permet de modifier la configuration et de fermer la fenêtre
	 * @return l'action listener
	 */
	private ActionListener changeConfigurationAndCloseAction() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selectedItem = configurationListComboBox.getSelectedItem();
				if (null != selectedItem && StringUtils.isNotBlank(selectedItem.toString())) {
					getControler().setCurrentConfiguration(selectedItem.toString());
				}
				closeFrame();
			}
		};
	}
	
	/**
	 * Permet de charger les configurations
	 */
	private void loadConfigurations() {
		getControler().getConfigurationNameList().forEach(name -> configurationListComboBox.addItem(name));
		this.configurationListComboBox.setSelectedItem(getControler().getConfigurationName());
	}
	
	@Override
	public String getWindowName() {
		return "Window for change configuration";
	}

}
