package application;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.SingleInstance;
import view.Main;
import view.beans.PictureTypeEnum;
import view.utils.ConfigurationUtils;
import view.utils.Constants;
import view.windows.UserInformation;

/**
 * 
 * Launcher pour lancer l'application
 * 
 * @author jerem
 *
 */
public class Launcher {

	private static Logger log = LoggerFactory.getLogger(Launcher.class);

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		createInstanceIfAlreadyNotLaunch();
	}

	/**
	 * Permet de créer l'instance si elle n'est pas déjà lancé
	 */
	private static void createInstanceIfAlreadyNotLaunch() {
		if (SingleInstance.createInstance()) {
			try {
				new Main(consumerForDeleteInstance());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		} else {
			log.error("Instance already launch");
			new UserInformation(
					ConfigurationUtils.getInstance()
							.getDisplayMessage(Constants.WINDOW_ALERT_MORE_ONE_CAERUS_LAUNCH_MESSAGE_TITLE),
					null, PictureTypeEnum.WARNING, ConfigurationUtils.getInstance()
							.getDisplayMessage(Constants.WINDOW_ALERT_MORE_ONE_CAERUS_LAUNCH_MESSAGE_CONTENT));
		}
	}

	/**
	 * Permet d'effectuer une action sur la fermeture de l'application
	 * 
	 * @return le consumer à executer
	 */
	private static Consumer<?> consumerForDeleteInstance() {
		return v -> SingleInstance.closeAndDeleteLockFile();
	}

}
