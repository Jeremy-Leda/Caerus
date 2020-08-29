package view.abstracts;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controler.IConfigurationControler;
import utils.RessourcesUtils;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionOnClose;
import view.interfaces.IModalFrameRepack;

/**
 * 
 * Abstract permettant de g�rer l'affichage avec une fen�tre modal 
 * Fournis les m�thodes utilitaire pour les JFrame
 * 
 * @author jerem
 *
 */
public abstract class ModalJFrameAbstract extends JFrame implements IModalFrameRepack, IActionOnClose {

	/**
	 * 
	 */
	private static final long serialVersionUID = -875604975297814094L;
	private static Logger logger = LoggerFactory.getLogger(ModalJFrameAbstract.class);
	private final IConfigurationControler configurationControler;
	private final JDialog frame;
	private final Boolean isModal;
	private final List<Consumer<?>> consumerForCloseList;
	private final Dimension screenSize;
	private final List<JComponent> optionalComponents;
	private Boolean automaticRepack = Boolean.FALSE;

	/**
	 * Constructeur
	 * @param title titre de la fen�tre
	 */
	public ModalJFrameAbstract(String title, IConfigurationControler configurationControler) {
		this(title, configurationControler, true);
	}
	
	/**
	 * Constructeur
	 * @param title titre de la fen�tre
	 */
	public ModalJFrameAbstract(String title, IConfigurationControler configurationControler, Boolean isModal) {
		logger.debug("Open " + getWindowName());
		this.isModal = isModal;
		this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.consumerForCloseList = new ArrayList<Consumer<?>>();
		this.consumerForCloseList.add(getConsumerOnCloseForLog());
		this.frame = new JDialog((JFrame) null, title, isModal);
		this.configurationControler = configurationControler;
		this.optionalComponents = new ArrayList<>();
	}
	
	/**
	 * Permet de cr�er la fen�tre
	 */
	protected void createWindow() {
		init();
		repack();
		frame.setVisible(true);
	}
	
	/**
	 * Permet de cr�er la fen�tre
	 * @param actionBeforeCreated action � lancer avant que la fen�tre soit cr�� (la jdialog est pass� en param�tre)
	 * @param actionAfterCreated action � lancer une fois la fen�tre cr�� (la jdialog est pass� en param�tre)
	 */
	protected void createWindow(Consumer<JDialog> actionBeforeCreated, Consumer<JDialog> actionAfterCreated) {
		if (null != actionBeforeCreated) {
			actionBeforeCreated.accept(this.frame);
		}
		init();
		repack();
		if (null != actionAfterCreated) {
			actionAfterCreated.accept(this.frame);
		}
		this.frame.setVisible(true);
	}
	
	/**
	 * Permet d'initialiser les informations principales et le placement de la jFrame
	 */
	private void init() {
		initComponents();
		this.frame.add(getContent());
		this.frame.setModal(isModal);
		this.frame.setIconImages(getIconsListImage());
		this.frame.getContentPane().add(getContent(), BorderLayout.CENTER);
		this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.frame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}
	
	/**
	 * Permet de se procurer la liste des icones possible (taille diff�rentes)
	 * @return la liste des icones
	 */
	private List<Image> getIconsListImage() {
		List<Image> allImages = new ArrayList<>();
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_16_16));
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_32_32));
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_64_64));
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_96_96));
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_128_128));
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_256_256));
		return allImages;
	}
	
	/**
	 * Permet d'initialiser les composants
	 */
	public abstract void initComponents();
	
	
	/**
	 * Permet de r�cup�rer le contenu � afficher
	 * @return le contenu
	 */
	public abstract JPanel getContent();
	
	/**
	 * Permet de repack la fen�tre Position centrer
	 */
	@Override
	public void repack() {
		repack(isModal);
	}
	
	@Override
	public void repack(Boolean changeLocation) {
		frame.pack();
		checkLimitSize(changeLocation);
		automaticRepack = Boolean.FALSE;
		if (changeLocation) {
			frame.setLocationRelativeTo(null);
		}
	}
	
	/**
	 * Permet de se procurer le controler
	 * @return le controler
	 */
	protected IConfigurationControler getControler() {
		return this.configurationControler;
	}
	
	/**
	 * Permet de fermer la fen�tre
	 */
	@Override
	public void closeFrame() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	/**
	 * Permet de se procurer le consumer pour fermer automatiquement la fen�tre
	 * @return le consumer
	 */
	@Override
	public Consumer<Void> getConsumerClosingAutomatically() {
		return (v)-> {
			if (null != this) {
				this.closeFrame();
			}
		};
	}
	
	
	/**
	 * Fournis le nom de la fen�tre utilis� dans le code
	 * @return
	 */
	public abstract String getWindowName();
	
	/**
	 * Permet de cr�er un consumer pour logger la fermeture
	 * @return le consumer
	 */
	private Consumer<Void> getConsumerOnCloseForLog() {
		return (v) -> logger.debug("Close " + getWindowName());
	}
	
	private void checkLimitSize(Boolean changeLocation) {
		Double limitHeight = screenSize.getHeight() * 0.95;
		if (this.frame.getHeight() > limitHeight.intValue()) {
			if (!automaticRepack) {
				automaticRepack = Boolean.TRUE;
				optionalComponents.forEach(oc -> oc.setVisible(Boolean.FALSE));
				repack(changeLocation);
			}
			this.frame.setSize(this.frame.getWidth(), limitHeight.intValue());
		}
	}
	
	/**
	 * Permet d'ajouter un consumer � lancer sur la fermeture de la fen�tre
	 * @param consumer consumer � lancer
	 */
	@Override
	public void addActionOnClose(Consumer<?> consumer) {
		this.consumerForCloseList.add(consumer);
	}
	
	@Override
	public void dispose() {
		this.consumerForCloseList.forEach(c -> c.accept(null));
		super.dispose();
	}
	
	/**
	 * permet d'ajouter un composant optionel
	 * @param component composant optionel
	 */
	public void addOptionalFrame(JComponent component) {
		this.optionalComponents.add(component);
	}
	
	/**
	 * Permet de se procurer le progress consumer
	 * 
	 * @param progressMaxValue le maximum de la valeur
	 * @return le progressConsumer
	 */
	public Consumer<Consumer<Integer>> getProgressConsumer(Integer progressMaxValue) {
		return valueProgressSetter -> {
			while (this.configurationControler.getProgress() < progressMaxValue) {
				valueProgressSetter.accept(this.configurationControler.getProgress());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		};
	}
}
