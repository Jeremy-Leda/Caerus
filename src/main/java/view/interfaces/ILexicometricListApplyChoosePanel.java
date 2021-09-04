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

    /**
     * Permet de se procurer la configuration pour les noms propres
     * @return la configuration pour les noms propres
     */
    ILexicometricConfigurationChoosePanel getProperNounConfiguration();

    /**
     * Permet de se procurer la configuration pour les textes à exclure
     * @return la configuration pour les textes à exclure
     */
    ILexicometricConfigurationChoosePanel getExcludeTextsConfiguration();

    /**
     * Permet de savoir si les données ont bien été remplis par l'utilisateur
     * @return vrai si les données ont bien été remplis par l'utilisateur
     */
    Boolean isValid();

}
