package view.interfaces;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IProfileWithTable extends IRefreshPanel, IAccessPanel {
//
//    /**
//     * Permet de remplir la liste des profiles
//     * @param profiles la liste des profiles
//     * @param defaultProfile le profil par défaut
//     */
//    void fillProfileSet(Collection<String> profiles, String defaultProfile);

//    /**
//     * Permet de construire les tables et d'alimenter la map
//     * @param idHeaderMap Map contenant l'id et le header de la table
//     */
//    void createTable(Map<Integer, String> idHeaderMap);

//    /**
//     * Permet de sauvegarder les données en mémoire
//     * @param consumer consumer pour la sauvegarde des données
//     * @param order Ordre de conception de l'arbre
//     */
//    void setSaveDataInMemory(Consumer<EditTable> consumer, LinkedList<Integer> order);

//    /**
//     * Permet de configurer la function qui permet de se procurer la nouvelle liste
//     * @param function la fonction qui permet de se procurer la nouvelle liste
//     */
//    void setGetListFromProfileFunction(Function<String, Collection<String>> function);

//    /**
//     *
//     * Permet de configurer la fonction qui va transmettre la nouvelle liste en fonction de l'objet source
//     *
//     * @param idSource id de la table qui est la source
//     * @param idDest id de la table qui doit recevoir la nouvelle liste
//     * @param function fonction qui va transmettre la nouvelle liste en fonction de l'objet source
//     */
//    void setReferenceFromSourceFunction(Integer idSource, Integer idDest, BiFunction<String, String, Collection<String>> function);

//    /**
//     * Permet de se procurer le nom du profile
//     * @return Le nom du profile
//     */
//    String getProfile();

//    /**
//     * Permet de définir le profile à utiliser
//     * @param profile profile à utiliser
//     */
//    void setProfile(String profile);

//    /**
//     * Permet de remplir la table
//     * @param id Identifiant de la table
//     * @param collection collection pour la table
//     */
//    void fillTable(Integer id, Collection<String> collection);

    /**
     * Permet de configurer le consumer pour le bouton ajouter
     * @param id Identifiant de la table
     * @param informationMessage Information pour l'utilisateur
     * @param label Label devant la zone de saisie
     */
    void setInterfaceForTableAndAddButton(Integer id, String informationMessage, String label);

}
