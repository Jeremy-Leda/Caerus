package utils;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

public class SwingUtils {

	private static SwingUtils _instance;

	public static SwingUtils getInstance() {
		if (null == _instance) {
			_instance = new SwingUtils();
		}
		return _instance;
	}

	public void addEnterKeyListener(JButton button) {
		button.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					button.doClick();
				}
			}
		});
	}
	
	public KeyListener checkAllFieldAreFilled(Collection<JTextField> textFieldSet, Component button) {
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				long nbFieldEmpty = textFieldSet.stream()
						.filter(k -> StringUtils.isBlank(k.getText())).count();
				Boolean isAllFill = nbFieldEmpty == 0;
				button.setEnabled(isAllFill);
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		};
	}
	
	public KeyListener checkAllFieldAreEmpty(Set<JTextField> textFieldSet, Component button) {
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				long nbFieldEmpty = textFieldSet.stream()
						.filter(k -> StringUtils.isBlank(k.getText())).count();
				button.setEnabled(textFieldSet.size() == nbFieldEmpty);
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		};
	}
	
}
