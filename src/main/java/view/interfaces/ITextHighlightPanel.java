package view.interfaces;

import java.awt.*;

/**
 *
 * Interface pour l'affichage d'un texte avec mise en forme de mot
 *
 */
public interface ITextHighlightPanel extends IAccessPanel {

    /**
     * Permet d'afficher un mot en couleur
     * @param word mot à mettre en couleur
     * @param color couleur du mot
     */
    void highlightWord(String word, Color color);


    /**
     * Permet de mettre à jour le texte
     * @param text texte
     */
    void setText(String text);

}
