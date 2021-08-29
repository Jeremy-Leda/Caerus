package model.analyze.beans;

import model.interfaces.IProgressBean;
import model.interfaces.IProgressModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 
 * Bean permettant de calculer la progression
 * 
 * @author jerem
 *
 */
public class Progress implements IProgressBean, IProgressModel {

	private final Integer nbMaxIterate;
	private Integer currentIterate;
	private Integer nbMaxElementForCurrentIterate;
	private Integer currentElementForCurrentIterate;
	private final static BigDecimal HUNDRED = new BigDecimal(100);

	/**
	 * Création de l'instance
	 * 
	 * @param nbMaxIterate nombre maximum d'itération
	 */
	public Progress(Integer nbMaxIterate) {
		this.nbMaxIterate = nbMaxIterate;
	}

	/**
	 * Permet de définir l'itération en cours (la premiére itération est à 0)
	 * 
	 * @param currentIterate numéro de l'itération
	 */
	@Override
	public void setCurrentIterate(Integer currentIterate) {
		this.currentIterate = currentIterate;
		this.nbMaxElementForCurrentIterate = 0;
		this.currentElementForCurrentIterate = 0;
	}

	/**
	 * Permet de définir le nombre maximum d'éléments dans l'itération en cours
	 * 
	 * @param nbMaxElementForCurrentIterate le nombre maximum d'éléments dans
	 *                                      l'itération en cours
	 */
	@Override
	public void setNbMaxElementForCurrentIterate(Integer nbMaxElementForCurrentIterate) {
		this.nbMaxElementForCurrentIterate = nbMaxElementForCurrentIterate;
	}

	/**
	 * Permet de définir le numéro de l'élément courant pour l'itération en cours
	 * 
	 * @param currentElementForCurrentIterate le numéro de l'élément courant pour
	 *                                        l'itération en cours
	 */
	@Override
	public void setCurrentElementForCurrentIterate(Integer currentElementForCurrentIterate) {
		this.currentElementForCurrentIterate = currentElementForCurrentIterate;
	}

	/**
	 * Permet de se procurer l'état en pourcentage de la progression
	 * 
	 * @return l'état en pourcentage de la progression
	 */
	@Override
	public Integer getProgress() {
		if (Objects.isNull(currentElementForCurrentIterate) || Objects.isNull(currentIterate)
				|| Objects.isNull(nbMaxElementForCurrentIterate)) {
			return 0;
		}
		BigDecimal percentByIteration = HUNDRED.divide(new BigDecimal(this.nbMaxIterate), 2, RoundingMode.DOWN);
		BigDecimal currentPercentForIteration = percentByIteration
				.multiply(new BigDecimal(this.currentIterate).subtract(new BigDecimal(1)));
		if (nbMaxElementForCurrentIterate == 0) {
			return currentPercentForIteration.intValue();
		}
		BigDecimal currentPercentInIteration =  new BigDecimal(this.currentElementForCurrentIterate)
				.add(BigDecimal.ONE).multiply(percentByIteration).divide(new BigDecimal(nbMaxElementForCurrentIterate), 2, RoundingMode.DOWN);
		return currentPercentInIteration.add(currentPercentForIteration).intValue();
	}

	@Override
	public void cancel() {

	}

	@Override
	public boolean treatmentIsCancelled() {
		return false;
	}

	@Override
	public boolean isRunning() {
		return true;
	}

	@Override
	public void resetProgress() {

	}

}
