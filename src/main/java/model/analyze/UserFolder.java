package model.analyze;

import model.analyze.constants.FolderSettingsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * Classe permettant la gestion des dossiers en cours par l'utilisateur
 *
 */
public final class UserFolder {

    private static UserFolder _instance;
    private final Map<FolderSettingsEnum, File> FOLDER_SETTINGS = new HashMap<>();

    /**
     * Permet de se procurer l'instance statique
     *
     * @return l'instance statique
     */
    public static UserFolder getInstance() {
        if (null == _instance) {
            _instance = new UserFolder();
        }
        return _instance;
    }

    /**
     * Permet de se procurer le dossier
     *
     * @param setting Réglage dont on souhaite le dossier
     * @return le dossier
     */
    public Optional<File> getFolder(FolderSettingsEnum setting) {
        if (FOLDER_SETTINGS.containsKey(setting)) {
            return Optional.of(FOLDER_SETTINGS.get(setting));
        }
        return Optional.empty();
    }

    /**
     * Permet de définir un dossier de réglages
     *
     * @param setting type de dossier
     * @param folder  dossier
     */
    public void putFolder(FolderSettingsEnum setting, File folder) {
        FOLDER_SETTINGS.put(setting, folder);
    }

}
