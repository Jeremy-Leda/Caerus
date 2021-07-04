package model.analyze.lexicometric.beans;

import io.vavr.Tuple2;
import model.analyze.lexicometric.interfaces.ILexicometricData;

import java.nio.file.Path;
import java.util.*;

/**
 * Bean contenant les données d'une liste de traitement lexicométrique
 */
public class UserLexicometricCleanListData {
    private final Set<ILexicometricData> dataSet = new HashSet<>();
    private final Map<String, Path> profilFileMap = new HashMap<>();
    private Optional<Tuple2<String, Path>> removeProfilFile = Optional.empty();

    public Set<ILexicometricData> getDataSet() {
        return dataSet;
    }

    public Map<String, Path> getProfilFileMap() {
        return profilFileMap;
    }

    public Optional<Tuple2<String, Path>> getRemoveProfilFile() {
        return removeProfilFile;
    }

    public void setRemoveProfilFile(Optional<Tuple2<String, Path>> removeProfilFile) {
        this.removeProfilFile = removeProfilFile;
    }
}
