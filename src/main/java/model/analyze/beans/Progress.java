package model.analyze.beans;

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
public class Progress {

	private final Integer nbMaxIterate;
	private Integer currentIterate;
	private Integer nbMaxElementForCurrentIterate;
	private Integer currentElementForCurrentIterate;
	private final static BigDecimal HUNDRED = new BigDecimal(100);

	/**
	 * Cr�ation de l'instance
	 * 
	 * @param nbMaxIterate nombre maximum d'it�ration
	 */
	public Progress(Integer nbMaxIterate) {
		this.nbMaxIterate = nbMaxIterate;
	}

	/**
	 * Permet de d�finir l'it�ration en cours (la premi�re it�ration est � 0)
	 * 
	 * @param currentIterate num�ro de l'it�ration
	 */
	public void setCurrentIterate(Integer currentIterate) {
		this.currentIterate = currentIterate;
		this.nbMaxElementForCurrentIterate = 0;
		this.currentElementForCurrentIterate = 0;
	}

	/**
	 * Permet de d�finir le nombre maximum d'�l�ments dans l'it�ration en cours
	 * 
	 * @param nbMaxElementForCurrentIterate le nombre maximum d'�l�ments dans
	 *                                      l'it�ration en cours
	 */
	public void setNbMaxElementForCurrentIterate(Integer nbMaxElementForCurrentIterate) {
		this.nbMaxElementForCurrentIterate = nbMaxElementForCurrentIterate;
	}

	/**
	 * Permet de d�finir le num�ro de l'�l�ment courant pour l'it�ration en cours
	 * 
	 * @param currentElementForCurrentIterate le num�ro de l'�l�ment courant pour
	 *                                        l'it�ration en cours
	 */
	public void setCurrentElementForCurrentIterate(Integer currentElementForCurrentIterate) {
		this.currentElementForCurrentIterate = currentElementForCurrentIterate;
	}

	/**
	 * Permet de se procurer l'�tat en pourcentage de la progression
	 * 
	 * @return l'�tat en pourcentage de la progression
	 */
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

}
