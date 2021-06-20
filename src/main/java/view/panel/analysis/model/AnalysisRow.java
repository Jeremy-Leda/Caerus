package view.panel.analysis.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Bean permettant de définir une ligne d'affichage
 */
public class AnalysisRow {


    private final List<Object> analysisList = new LinkedList<>();

    /**
     * Permet de se procurer la liste des valeurs pour la ligne
     * @return la liste des valeurs pour la ligne
     */
    public List<Object> getAnalysisList() {
        return analysisList;
    }
}
