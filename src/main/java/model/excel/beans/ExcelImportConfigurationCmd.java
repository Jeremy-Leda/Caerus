package model.excel.beans;

import model.analyze.beans.Configuration;
import model.exceptions.ErrorCode;
import model.exceptions.ImportExcelException;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Bean de commande pour l'import du fichier excel
 *
 */
public class ExcelImportConfigurationCmd {
    private Boolean isSpecificImport;
    private String labelSpecificChoose;
    private final Set<String> fieldToImportList;
    private final File fileToImport;
    private Configuration configuration;
    private final String sheetName;

    /**
     * Constructeur
     * @param fileToImport fichier à importer
     * @param configuration configuration à utiliser
     * @param sheetName le nom de la feuille à importer
     */
    public ExcelImportConfigurationCmd(File fileToImport, String sheetName) {
        this.sheetName = sheetName;
        this.fieldToImportList = new HashSet<>();
        this.fileToImport = fileToImport;
        this.isSpecificImport = Boolean.FALSE;
    }

    /**
     * Permet de se procurer si il s'agit d'un import Excel avec du spécifique
     * @return Vrai si il s'agit d'un import Excel avec du spécifique
     */
    public Boolean getIsSpecificImport() {
        return isSpecificImport;
    }

    /**
     * Permet de définir si il s'agit d'un import Excel avec du spécifique
     * @param specificImport Vrai si il s'agit d'un import Excel avec du spécifique
     */
    public void setIsSpecificImport(Boolean specificImport) {
        isSpecificImport = specificImport;
    }

    /**
     * Permet de se procurer le label spécifique qui a été choisi
     * @return le label spécifique qui a été choisi
     */
    public String getLabelSpecificChoose() {
        return labelSpecificChoose;
    }

    /**
     * Permet de définir le label spécifique qui a été choisi
     * @param labelSpecificChoose le label spécifique qui a été choisi
     */
    public void setLabelSpecificChoose(String labelSpecificChoose) {
        this.labelSpecificChoose = labelSpecificChoose;
    }

    /**
     * Permet d'ajouter un champ à importer
     * @param field champ à importer
     */
    public void addFieldToImport(String field) {
        this.fieldToImportList.add(field);
    }

    /**
     * Permet de se procurer la liste des champs à importer
     * @return la liste des champs à importer
     */
    public Set<String> getFieldToImportList() {
        return fieldToImportList;
    }

    /**
     * Permet de se procuer le fichier à importer
     * @return le fichier à importer
     */
    public File getFileToImport() {
        return fileToImport;
    }

    /**
     * Permet de se procurer la configuration à utiliser
     * @return la configuration à utiliser
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Permet de définir la configuration à utiliser
     * @param configuration configuration à utiliser
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Permet de se procurer le nom de la feuille excel à traiter
     * @return Le nom de la feuille excel à traiter
     */
    public String getSheetName() {
        return sheetName;
    }

    /**
     * Validation du bean
     * @return la liste des erreurs trouvé, vide si pas d'erreur
     */
    public Set<InformationException> validate() {
        Set<InformationException> errorSet = new HashSet<>();
        boolean fieldsAllMatch = this.getFieldToImportList().stream().allMatch(code ->
                this.getConfiguration().getStructuredFieldList().stream().filter(structuredField -> structuredField.getFieldName().equals(code)).findFirst().isPresent());
        if (!fieldsAllMatch) {
            errorSet.add(new InformationExceptionBuilder()
                    .errorCode(ErrorCode.INVALID_FIELD_WITH_CONFIGURATION)
                    .objectInError(this)
                    .build());
        }
        return errorSet;
    }
}
