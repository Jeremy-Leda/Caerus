package model.analyze.lexicometric.interfaces;

/**
 * Interface permettant de se procurer les données lexicometric
 * @param <T> Type de données à récupérer
 */
public interface ILexicometricData<T extends Object> {

    /**
     * Permet de se procurer le profile
     * @return le profile
     */
    String getProfile();

    /**
     * Permet de se procurer les données
     * @return les données
     */
    T getData();
}
