package model.analyze.lexicometric.interfaces;

/**
 *
 * Interface permettant de gérer la copie des données pour les données lexicométrique
 *
 * @param <T> Type de données lexicométrique
 */
public interface ILexicometricCopyData<T extends Object> extends ILexicometricData<T>, Cloneable {

    /**
     * Permet de définir les data
     * @param data data à définir
     */
    void setData(T data);
    
    /**
     * Permet de se récupérer un clone des data
     * @return le clone des data
     */
    T clone();

}
