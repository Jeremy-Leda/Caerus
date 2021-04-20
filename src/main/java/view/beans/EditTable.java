package view.beans;

import model.PojoBuilder;
import model.analyze.constants.LexicometricAnalysisType;

import javax.validation.constraints.NotNull;

/**
 *
 * Bean permettant de définir les modifications à appliquer sur une table
 *
 */
@PojoBuilder
public class EditTable {

    @NotNull
    private String profil;

    private LexicometricAnalysisType lexicometricAnalysisType;

    @NotNull
    private EditTableElement editTableElement;

    /**
     * Permet de se procurer le profil à mettre à jour
     * @return le profil
     */
    public String getProfil() {
        return profil;
    }

    /**
     * Permet de définir le profil à mettre à jour
     * @param profil le profil à mettre à jour
     */
    public void setProfil(String profil) {
        this.profil = profil;
    }

    /**
     * Permet de se procurer le type d'analyse à mettre à jour
     * @return le type d'analyse à mettre à jour
     */
    public LexicometricAnalysisType getLexicometricAnalysisType() {
        return lexicometricAnalysisType;
    }

    /**
     * Permet de définir le type d'analyse à mettre à jour
     * @param lexicometricAnalysisType le type d'analyse à mettre à jour
     */
    public void setLexicometricAnalysisType(LexicometricAnalysisType lexicometricAnalysisType) {
        this.lexicometricAnalysisType = lexicometricAnalysisType;
    }

    /**
     * Permet de se procurer l'élément à mettre à jour
     * @return l'élément à mettre à jour
     */
    public EditTableElement getEditTableElement() {
        return editTableElement;
    }

    /**
     * Permet de définir l'élément à mettre à jour
     * @param editTableElement l'élément à mettre à jour
     */
    public void setEditTableElement(EditTableElement editTableElement) {
        this.editTableElement = editTableElement;
    }

    @Override
    public String toString() {
        return "EditTable{" +
                "profil='" + profil + '\'' +
                ", lexicometricAnalysisType=" + lexicometricAnalysisType +
                ", editTableElement=" + editTableElement +
                '}';
    }
}
