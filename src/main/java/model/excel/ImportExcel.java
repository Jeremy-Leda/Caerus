package model.excel;

import com.monitorjbl.xlsx.StreamingReader;
import model.analyze.beans.StructuredText;
import model.excel.beans.StructuredTextExcel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 * Classe pour les import excel
 *
 */
public class ImportExcel {

    private FileInputStream inputStream;
    private Workbook workbook;
    private final Map<Integer, String> mapColumnField = new HashMap<>();
    private Integer keyColumn;
    private final Map<String, String> headerFieldToCodeFieldMap;
    private final Sheet sheet;
    private static Logger logger = LoggerFactory.getLogger(ImportExcel.class);

    /**
     * Constructeur
     * @param file Fichier excel à charger
     * @param headerFieldToCodeFieldMap map pour convertir le libellé en code
     * @param sheetName Nom de la feuille
     * @throws IOException Erreur d'entrée sortie
     */
    public ImportExcel(File file, Map<String, String> headerFieldToCodeFieldMap, String sheetName) throws IOException {
        inputStream = new FileInputStream(file);
        this.headerFieldToCodeFieldMap = headerFieldToCodeFieldMap;
        workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(inputStream);
        sheet = workbook.getSheet(sheetName);
    }

    /**
     * Permet de valider que le fichier excel est valide
     * Liste des champs à mettre à jour présent et clé technique présente
     * @return Vrai si le fichier est valide
     */
    public boolean checkExcelIsValid() {
        // On valide que tous les éléments de la liste existe et la clé aussi
        Row row = StreamSupport.stream(Spliterators.spliteratorUnknownSize(sheet.rowIterator(), Spliterator.ORDERED), false)
                .findFirst().get();
        StreamSupport.stream(Spliterators.spliteratorUnknownSize(row.cellIterator(), Spliterator.ORDERED), false).forEach(this::prepareMapAndKeyColumn);
        return headerFieldToCodeFieldMap.size() == mapColumnField.size() && Objects.nonNull(keyColumn);
    }

    /**
     * Permet de préparer les map et la clé column par rapport à la cellule en cours
     * @param cell cellule en cours
     */
    private void prepareMapAndKeyColumn(Cell cell) {
        Optional<Map.Entry<String, String>> entryFound = headerFieldToCodeFieldMap.entrySet().stream().filter(e -> e.getKey().equals(cell.getStringCellValue())).findFirst();
        if (entryFound.isPresent()) {
            mapColumnField.put(cell.getColumnIndex(), entryFound.get().getValue());
        }
        if ("key".equals(cell.getStringCellValue())) {
            keyColumn = cell.getColumnIndex();
        }
    }

    /**
     * Permet de lire le fichier excel et de le transformer en structuredText
     * @return la liste des structuredText
     */
    public Set<StructuredTextExcel> readExcelForImport() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(sheet.rowIterator(), Spliterator.ORDERED), false)
                .filter(row -> row.getRowNum() > 0)
                .map(this::toStructuredText)
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(StructuredTextExcel::getNumber))
                ));
    }

    /**
     * Permet de convertir une ligne en structuredText
     * @param row ligne à convertir
     * @return le structuredText
     */
    private StructuredTextExcel toStructuredText(Row row) {
        StructuredTextExcel st = new StructuredTextExcel(row.getRowNum());
        st.setUniqueKey(row.getCell(keyColumn).getStringCellValue());
        mapColumnField.forEach((k, v) -> st.modifyContent(v, row.getCell(k).getStringCellValue()));
        return st;
    }


    /**
     * Permet de fermer les streams et nettoyer
     * @throws IOException Erreur d'entrée sortie
     */
    public void closeAndClean() throws IOException {
        inputStream.close();
        workbook.close();
        inputStream = null;
        workbook = null;
    }

}
