package ihm.panel;

import java.math.BigDecimal;
import java.util.function.Consumer;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ihm.beans.DirectionTypeEnum;
import ihm.beans.DisplayText;
import ihm.controler.IConfigurationControler;
import ihm.interfaces.IRefreshTextDisplayPanel;
import ihm.renders.DisplayTextRenderer;

/**
 * 
 * Panel pour l'affichage des textes filtrés
 * 
 * @author jerem
 *
 */
public class DisplayTextsFilteredPanel implements IRefreshTextDisplayPanel {

	private final JPanel content;
	private final JList<DisplayText> displayTextsList;
	private final IConfigurationControler controler;
	private final JScrollPane contentWithScrollBar;
	private Integer currentPage;
	private Integer maxPage;
	private Integer nbTextsByPage;
	private Integer nbTexts;
	
	
	/**
	 * Constructeur
	 * @param controler controleur
	 * @param nbTextsByPage Nb de textes par pages à initialiser
	 * @param consumerToItemChange Consumer à appliquer en cas de changement de la liste
	 */
	public DisplayTextsFilteredPanel(IConfigurationControler controler, Integer nbTextsByPage, Consumer<Void> consumerToItemChange) {
		this.content = new JPanel();
		this.controler = controler;
		this.displayTextsList = new JList<DisplayText>();
		this.displayTextsList.setCellRenderer(new DisplayTextRenderer());
		this.displayTextsList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (null != consumerToItemChange) {
					consumerToItemChange.accept(null);
				}
			}
		});
		this.content.add(displayTextsList);
		this.contentWithScrollBar = new JScrollPane(this.content);
		this.currentPage = 1;
		refresh();
	}
	
	@Override
	public JComponent getJPanel() {
		return this.contentWithScrollBar;
	}

	/**
	 * Permet de rafraichir la liste avec les nouvelles informations
	 */
	@Override
	public void refresh() {
		if (null != this.nbTextsByPage) {
			if (this.currentPage == 0) {
				this.currentPage = 1;
			}
			computePageInformation();
			this.displayTextsList.removeAll();
			this.displayTextsList.setModel(getDisplayTextModel());
		}
	}
	
	private void computePageInformation() {
		this.nbTexts = this.controler.getNbDisplayTextListFromFilteredText();
		BigDecimal nbPageMax = new BigDecimal(this.nbTexts);
		nbPageMax = nbPageMax.divide(new BigDecimal(this.nbTextsByPage));
		if (nbPageMax.equals(new BigDecimal(nbPageMax.intValue()))) {
			this.maxPage = nbPageMax.intValue();
		} else {
			this.maxPage = nbPageMax.intValue() + 1;
		}
		if (this.maxPage == 0) {
			this.currentPage = 0;
		}
	}
	
	/**
	 * Permet de se procurer la liste des textes à afficher
	 * @return la liste des textes à afficher
	 */
	private DefaultListModel<DisplayText> getDisplayTextModel() {
		DefaultListModel<DisplayText> dlm = new DefaultListModel<>();
		if (this.currentPage > 0) {
			Integer start = (this.currentPage - 1) * this.nbTextsByPage;
			this.controler.getDisplayTextListFromFilteredText(start, this.nbTextsByPage).forEach(text -> dlm.addElement(text));
		}
		return dlm;
	}

	@Override
	public void changePage(DirectionTypeEnum direction) {
		if (DirectionTypeEnum.PREVIOUS.equals(direction) && currentPage > 1) {
			currentPage--;
			refresh();
		} else if (DirectionTypeEnum.NEXT.equals(direction) && currentPage < maxPage) {
			currentPage++;
			refresh();
		}
	}

	@Override
	public void setNbTextByPage(Integer nbTextsByPage) {
		if (null != nbTextsByPage) {
			this.nbTextsByPage = nbTextsByPage;
			this.currentPage = 1;
			refresh();
		}
	}

	@Override
	public Boolean isEnabled(DirectionTypeEnum direction) {
		if (DirectionTypeEnum.PREVIOUS.equals(direction) && currentPage > 1) {
			return true;
		} else if (DirectionTypeEnum.NEXT.equals(direction) && currentPage < maxPage) {
			return true;
		}
		return false;
	}

	@Override
	public Integer getCurrentPage() {
		return this.currentPage;
	}

	@Override
	public Integer getMaxPage() {
		return this.maxPage;
	}

	@Override
	public DisplayText getDisplayTextSelected() {
		return this.displayTextsList.getSelectedValue();
	}
}
