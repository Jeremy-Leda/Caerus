package ihm.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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

import ihm.abstracts.ModalJFrameAbstract;
import ihm.controler.IConfigurationControler;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;

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
	private final JTextField textErrorLine = new JTextField(StringUtils.EMPTY, 30);
	private final JTextField textFixedLine = new JTextField(StringUtils.EMPTY, 30);
	private Integer currentIndex = 0;
	private JPanel content = new JPanel();

	public FixedErrorLine(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_TITLE), configurationControler);
		createWindow();
	}

	@Override
	public void initComponents() {
		fillTextWithErrorAndRefreshDisplay();
		panContent.setLayout(new BoxLayout(panContent, BoxLayout.Y_AXIS));
		JPanel subPanErrorLine = new JPanel();
		JLabel errorLineLabel = new JLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_LINE_ERROR_LABEL));
		subPanErrorLine.add(errorLineLabel);
		subPanErrorLine.add(textErrorLine);
		panContent.add(subPanErrorLine);
		JPanel subPanFixedErrorLine = new JPanel();
		JLabel fixedErrorLineLabel = new JLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_LINE_FIXED_LABEL));
		subPanFixedErrorLine.add(fixedErrorLineLabel);
		subPanFixedErrorLine.add(textFixedLine);
		panContent.add(subPanFixedErrorLine);
		
		JPanel panAction = new JPanel();
		panAction.setBorder(BorderFactory.createTitledBorder(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_ACTION_PANEL_TITLE)));
		panAction.add(nextButton);
		
		nextButton.addActionListener(saveLine());
		textErrorLine.setEnabled(false);
		
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(panContent);
		content.add(panAction);
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}

	private void fillTextWithErrorAndRefreshDisplay() {
		logger.debug("[DEBUT] fillTextWithErrorAndRefreshDisplay");
		logger.debug(String.format("Load index %d", currentIndex));
		String errorLine = getControler().getErrorLine(currentIndex);
		logger.debug(String.format("Ligne chargé %s", errorLine));
		String panContentLabel = String.format(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_TITLE), currentIndex + 1,
				getControler().getNbLinesError());
		panContent.setBorder(BorderFactory.createTitledBorder(panContentLabel));
		textErrorLine.setText(errorLine);
		textFixedLine.setText(errorLine);
		if (currentIndex + 1 == getControler().getNbLinesError()) {
			nextButton.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_ACTION_PANEL_SAVE_QUIT_BUTTON_LABEL));
		} else {
			nextButton.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_ACTION_PANEL_SAVE_NEXT_BUTTON_LABEL));
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
			//TODO A modifier
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		closeFrame();
	}

	@Override
	public String getWindowName() {
		return "Window for fixed error of structured line";
	}

}
