package view.interfaces;

import view.panel.analysis.model.AnalysisRow;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * Interface pour le tableau de résultat des analyses
 *
 */
public interface ITableAnalysisPanel extends IAccessPanel{

    /**
     * Permet d'effacer la sélection
     */
    void clearSelection();

    /**
     * Permet de mettre à jour les résultats
     * @param analysisRowLinkedList lignes de résultats
     */
    void updateAnalysisResult(List<AnalysisRow> analysisRowLinkedList);

    /**
     * Permet d'ajouter un consumer qui permet de récupérer le contenu sélectionné
     * @param consumer consumer
     */
    void addConsumerOnSelectedChangeForWord(Consumer<List<Object>> consumer);

    /**
     * Permet de se procurer la liste des mots sélectionnés
     * @return la liste des mots sélectionnés
     */
    Set<String> getSelectedWords();


}
