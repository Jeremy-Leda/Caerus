package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.ErrorStructuredLine;
import view.beans.PictureTypeEnum;
import view.interfaces.IInformationPanel;
import view.interfaces.IListPanel;
import view.panel.InformationPanel;
import view.panel.ListPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * Interface pour corriger les erreurs de lignes mal formaté
 * 
 * @author jerem
 *
 */
public class FixedErrorLine extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2892809132534513107L;
	private static Logger logger = LoggerFactory.getLogger(FixedErrorLine.class);
	private final JPanel panContent = new JPanel();
	private final JButton nextButton = new JButton();
	private final JLabel numberLineValue = new JLabel();
	private final JLabel pathLineValue = new JLabel();
	private final JTextField textErrorLine = new JTextField(StringUtils.EMPTY, 63);
	private final JTextField textFixedLine = new JTextField(StringUtils.EMPTY, 63);
	private final IListPanel listPanel;
	private final JLabel fieldLabel = new JLabel();
	private final IInformationPanel informationPanel;
	private final Map<String, String> fieldsListMap = new LinkedHashMap<String, String>();
	private final JTextField textUserFixed = new JTextField(StringUtils.EMPTY, 63);

	private Integer currentIndex = 0;
	private JPanel content = new JPanel();

	public FixedErrorLine(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_TITLE),
				configurationControler);
		listPanel = new ListPanel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_LIST_FIELD_LABEL));
		informationPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_PANEL_TITLE),
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_MESSAGE),
				false);
		this.fieldsListMap.putAll(getControler().getAllField());
		this.listPanel.addConsumerOnSelectChange(afterChangeSelectionList());
		textFixedLine.setEnabled(false);
		textUserFixed.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				refreshFixedText();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		createWindow();
	}

	@Override
	public void initComponents() {
		fillTextWithErrorAndRefreshDisplay();
		panContent.setLayout(new BoxLayout(panContent, BoxLayout.Y_AXIS));
		JPanel subPanPathErrorLine = new JPanel();
		JLabel pathLineLabel = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_LINE_FILE_LABEL));
		subPanPathErrorLine.add(pathLineLabel);
		subPanPathErrorLine.add(pathLineValue);
		panContent.add(subPanPathErrorLine);
		JPanel subPanNumberErrorLine = new JPanel();
		JLabel numberLineLabel = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_LINE_NUMBER_LABEL));
		subPanNumberErrorLine.add(numberLineLabel);
		subPanNumberErrorLine.add(numberLineValue);
		panContent.add(subPanNumberErrorLine);
		JPanel subPanErrorLine = new JPanel();
		JLabel errorLineLabel = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_LINE_ERROR_LABEL));
		subPanErrorLine.add(errorLineLabel);
		subPanErrorLine.add(textErrorLine);
		panContent.add(subPanErrorLine);

		JPanel panAction = new JPanel();
		panAction.setBorder(BorderFactory.createTitledBorder(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_ACTION_PANEL_TITLE)));
		panAction.add(nextButton);

		nextButton.addActionListener(saveLine());
		textErrorLine.setEnabled(false);

		JPanel panUserContent = new JPanel();
		panUserContent.setLayout(new BoxLayout(panUserContent, BoxLayout.Y_AXIS));
		panUserContent.setBorder(BorderFactory.createTitledBorder(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_LIST_FIELD_PANEL_TITLE)));
		panUserContent.add(this.listPanel.getJPanel());
		JPanel panUserModifyTextContent = new JPanel();
		panUserModifyTextContent.add(this.fieldLabel);
		panUserModifyTextContent.add(this.textUserFixed);
		panUserContent.add(panUserModifyTextContent);
		JPanel subPanFixedErrorLine = new JPanel();
		JLabel fixedErrorLineLabel = new JLabel();
		fixedErrorLineLabel.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_LINE_FIXED_LABEL));
		subPanFixedErrorLine.add(fixedErrorLineLabel);
		subPanFixedErrorLine.add(textFixedLine);
		panUserContent.add(subPanFixedErrorLine);
		
		this.fieldLabel.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_FIXED_USER_DEFAULT_LABEL));
		textUserFixed.setEnabled(false);
		textFixedLine.setEnabled(false);

		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(panContent);
		content.add(informationPanel.getJPanel());
		content.add(panUserContent);
		content.add(panAction);
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}

	/**
	 * Action a effectuer après le changement dans la liste
	 * 
	 * @return Le consumer
	 */
	private Consumer<?> afterChangeSelectionList() {
		return c -> {
			this.fieldLabel.setText(this.listPanel.getLabelSelected() + " : ");
			refreshFixedText();
			repack();
		};
	}

	/**
	 * Permet de rafraichir le texte de la balise corrigé
	 */
	private void refreshFixedText() {
		if (StringUtils.isNotBlank(listPanel.getLabelSelected())) {
			Optional<Entry<String, String>> optionalFieldFound = this.fieldsListMap.entrySet().stream()
					.filter(s -> listPanel.getLabelSelected().equals(s.getValue())).findFirst();
			if (optionalFieldFound.isPresent()) {
				String structuredLine = getControler().getStructuredLine(optionalFieldFound.get().getKey(), this.textUserFixed.getText());
				textFixedLine.setText(structuredLine);
				textFixedLine.setCaretPosition(0);
				textUserFixed.setEnabled(true);
				nextButton.setEnabled(true);
			}
		} else {
			this.fieldLabel.setText(ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_FIXED_USER_DEFAULT_LABEL));
			textUserFixed.setEnabled(false);
		}
	}

	/**
	 * Permet de remplir la liste
	 */
	private void fillList() {
		listPanel.refresh(fieldsListMap.values().stream().collect(Collectors.toList()));
	}

	private void fillTextWithErrorAndRefreshDisplay() {
		logger.debug("[DEBUT] fillTextWithErrorAndRefreshDisplay");
		logger.debug(String.format("Load index %d", currentIndex));
		ErrorStructuredLine errorLine = getControler().getErrorLine(currentIndex);
		if (null != errorLine) {
			logger.debug(String.format("Ligne chargé %s", errorLine));
			String errorTextLine = errorLine.getLine();
			if (errorTextLine.length() > 60) {
				errorTextLine = errorTextLine.substring(0, 60) + "...";
			}
			fillList();
			String panContentLabel = String.format(
					ConfigurationUtils.getInstance()
							.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_TITLE),
					currentIndex + 1, getControler().getNbLinesError());
			panContent.setBorder(BorderFactory.createTitledBorder(panContentLabel));
			textErrorLine.setText(errorTextLine);
			textFixedLine.setText(errorLine.getLine());
			numberLineValue.setText(errorLine.getNumLine().toString());
			pathLineValue.setText(errorLine.getPath());
			textUserFixed.setText(errorLine.getLine());
			textUserFixed.setCaretPosition(0);
			if (currentIndex + 1 == getControler().getNbLinesError()) {
				nextButton.setText(ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_ACTION_PANEL_SAVE_QUIT_BUTTON_LABEL));
			} else {
				nextButton.setText(ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_ACTION_PANEL_SAVE_NEXT_BUTTON_LABEL));
			}
			nextButton.setEnabled(false);
		}
		logger.debug("[FIN] fillTextWithErrorAndRefreshDisplay");
	}

	private ActionListener saveLine() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getControler().updateLineError(currentIndex, textFixedLine.getText());
				if (currentIndex + 1 == getControler().getNbLinesError()) {
					saveFileAndQuit();
				} else {
					currentIndex++;
					fillTextWithErrorAndRefreshDisplay();
				}
			}
		};
	}

	private void saveFileAndQuit() {
		try {
			getControler().saveFileAfteFixedErrorLine();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			// TODO A modifier
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		closeFrame();
	}

	@Override
	public String getWindowName() {
		return "Window for fixed error of structured line";
	}

}
