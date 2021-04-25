package view.beans;

import model.analyze.lexicometric.beans.Lemmatization;
import model.analyze.lexicometric.beans.Tokenization;

/**
 * Bean contenant la vue de la configuration pour les analyses lexicométrique
 */
public class LexicometricConfigurationView {
    private final Tokenization tokenization;
    private final Lemmatization lemmatization;

    /**
     * Constructeur
     * @param tokenization configuration de la tokenization
     * @param lemmatization configuration de la lemmatisation
     */
    public LexicometricConfigurationView(Tokenization tokenization, Lemmatization lemmatization) {
        this.tokenization = tokenization;
        this.lemmatization = lemmatization;
    }

    /**
     * Permet de se procurer la configuration de la tokenisation
     * @return la configuration de la tokenisation
     */
    public Tokenization getTokenization() {
        return tokenization;
    }

    /**
     * Permet de se procurer la configuration de la lemmatisation
     * @return la configuration de la lemmatisation
     */
    public Lemmatization getLemmatization() {
        return lemmatization;
    }
}
