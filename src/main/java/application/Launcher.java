package application;

import java.awt.HeadlessException;
import java.io.IOException;

import view.Main;

/**
 * 
 * Launcher pour lancer l'application
 * 
 * @author jerem
 *
 */
public class Launcher {

	public static void main(String[] args) {
		try {
			new Main();
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
