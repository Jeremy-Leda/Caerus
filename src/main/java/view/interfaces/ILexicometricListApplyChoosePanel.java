package view.interfaces;

/**
 *
 * Interface pour la gestion du panel Lexicometric list choose
 *
 */
public interface ILexicometricListApplyChoosePanel extends IAccessPanel{

    /**
     * Permet de se procurer la configuration pour la lemmatization
     * @return la configuration pour la lemmatization
     */
    ILexicometricConfigurationChoosePanel getLemmatizationConfiguration();

    /**
     * Permet de se procurer la configuration pour les stopwords
     * @return la configuration pour les stopword
     */
    ILexicometricConfigurationChoosePanel getStopWordConfiguration();

}
