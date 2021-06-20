package view.panel;

import io.vavr.Tuple2;
import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import org.apache.commons.lang3.StringUtils;
import view.interfaces.ITextHighlightPanel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * Classe pour instancier l'affichage d'un texte avec mise en forme de mot
 *
 */
public class TextHighlightPanel implements ITextHighlightPanel {

    private final JPanel content = new JPanel();
    private final JTextArea textArea = new JTextArea(1, 50);

    /**
     * Constructeur
     * @param titlePanel titre du panel
     */
    public TextHighlightPanel(String titlePanel) {
        if (StringUtils.isNotBlank(titlePanel)) {
            this.content.setBorder(BorderFactory.createTitledBorder(titlePanel));
        }
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setRows(10);
        content.add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
    }

    @Override
    public JComponent getJPanel() {
        return content;
    }

    @Override
    public void highlightWord(String word, Color color) {
        Set<Tuple2<Integer, Integer>> position = loadPositionWordInText(word);
        Highlighter highlighter = textArea.getHighlighter();
        highlighter.removeAllHighlights();
        Highlighter.HighlightPainter painter =
                new DefaultHighlighter.DefaultHighlightPainter(color);
        position.forEach(tuple2 -> {
            try {
                highlighter.addHighlight(tuple2._1, tuple2._2, painter);
            } catch (BadLocationException e) {
                throw new ServerException().addInformationException(new InformationExceptionBuilder()
                .errorCode(ErrorCode.TECHNICAL_ERROR)
                .objectInError(tuple2)
                .build());
            }
        });
    }

    @Override
    public void setText(String text) {
        this.textArea.setText(text);
    }

    /**
     * Permet de se procurer les positions du mot dans le texte
     * @param word mot à rechercher
     * @return le set des positions
     */
    private Set<Tuple2<Integer, Integer>> loadPositionWordInText(String word) {
        String text = this.textArea.getText().toLowerCase(Locale.ROOT);
        String wordToSearch = StringUtils.SPACE + word + StringUtils.SPACE;
        int wordLength = word.length();
        int index = 0;
        Set<Tuple2<Integer, Integer>> result = new HashSet<>();
        while(index != -1){
            index = text.indexOf(wordToSearch, index + wordLength);
            if (index != -1) {
                result.add(new Tuple2(index + 1, index + wordLength + 1));
            }
        }
        return result;
    }
//
//    /**
//     * Permet de rafraichir le nombre du ligne du contenu de texte
//     *
//     * @param textArea zone de texte
//     */
//    private void refreshNbLines(JTextArea textArea) {
//        StringTokenizer st = new StringTokenizer(textArea.getText(), StringUtils.LF);
//        Integer nbLines = 0;
//        while (st.hasMoreTokens()) {
//            String text = (String) st.nextToken();
//            BigDecimal nbLinesForThisLine = new BigDecimal(text.length()).setScale(0, RoundingMode.DOWN)
//                    .divide(new BigDecimal(102), RoundingMode.DOWN);
//            nbLines += nbLinesForThisLine.intValueExact();
//            nbLines++;
//        }
//        textArea.setRows(nbLines);
//        if (nbLines > 20) {
//            textArea.setRows(20);
//        }
//    }
}
