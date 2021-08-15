package model.analyze.cache;

import io.vavr.control.Either;
import model.analyze.beans.Configuration;
import model.analyze.beans.FileOrder;
import model.analyze.beans.FilesOrder;
import model.exceptions.ErrorCode;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import utils.JSonFactoryUtils;
import utils.PathUtils;
import utils.RessourcesUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Classe contenant le cache des ordres des fichiers
 *
 */
public final class FileOrderCache {

    private final Map<Configuration, FilesOrder> configurationOrderMap = new HashMap<>();
    private final static FileOrderCache _instance = new FileOrderCache();

    public static FileOrderCache getInstance() {
        return _instance;
    }

    /**
     * Permet de charger une configuration dans le cache
     * @param configuration configuration à charger
     */
    private void loadConfigurationInCache(Configuration configuration) {
        File fileOfOrders = getFileOfOrders(configuration);
        FilesOrder configurationOrderFromFile = getConfigurationOrderFromFile(fileOfOrders);
        checkDistinctOrder(configuration.getName(), configurationOrderFromFile.getFileOrderSet());
        this.configurationOrderMap.put(configuration, configurationOrderFromFile);
    }

    /**
     * Permet de se procurer l'ordre du document
     * @param configuration configuration
     * @param fileName nom du fichier
     * @return l'objet {@link FileOrder} si présent
     */
    public Optional<FileOrder> getFileOrderOfDocument(Configuration configuration, String fileName) {
        loadCacheIfNotLoaded(configuration);
        return configurationOrderMap.get(configuration).getFileOrderSet()
                .stream()
                .filter(fileOrder -> fileOrder.getNameFile().equals(fileName))
                .findFirst();
    }

    /**
     * Permet de supprimer un fichier et son ordre
     * @param configuration configuration
     * @param fileName nom du fichier
     */
    public void deleteFileOrderInCacheAndSave(Configuration configuration, String fileName) {
        loadCacheIfNotLoaded(configuration);
        FilesOrder filesOrder = configurationOrderMap.get(configuration);
        filesOrder.getFileOrderSet().removeIf(fileOrder -> fileOrder.getNameFile().equals(fileName));
        saveConfigurationOrderInFile(getFileOfOrders(configuration), filesOrder);
    }

    /**
     * Permet de se procurer le prochain numéro d'ordre
     * @param configuration configuration
     * @return le prochain numéro d'ordre
     */
    public Integer getNextNumber(Configuration configuration) {
        return configurationOrderMap.get(configuration).getNextNumber();
    }

    /**
     * Permet d'ajouter un ordre de document dans le cache et de sauvegarder sur le disque
     * @param configuration configuration
     * @param fileName nom du fichier
     * @param order l'ordre à associé
     */
    public void putOrderOfDocumentAndSave(Configuration configuration, String fileName, Integer order) {
        loadCacheIfNotLoaded(configuration);
        FileOrder fileOrder = new FileOrder();
        fileOrder.setNameFile(fileName);
        fileOrder.setNumber(order);
        FilesOrder filesOrder = configurationOrderMap.get(configuration);
        filesOrder.getFileOrderSet().add(fileOrder);
        File fileOfOrders = getFileOfOrders(configuration);
        saveConfigurationOrderInFile(fileOfOrders, filesOrder);
    }

    /**
     * Permet de charger le cache s'il n'est pas chargé
     * @param configuration configuration à charger
     */
    private void loadCacheIfNotLoaded(Configuration configuration) {
        if (!configurationOrderMap.containsKey(configuration)) {
            loadConfigurationInCache(configuration);
        }
    }

    /**
     * Permet de vérifier si les ordres sont bien distinct
     * @param configurationName nom de la configuration
     * @param fileOrderSet liste des ordres et fichiers
     */
    private void checkDistinctOrder(String configurationName, Set<FileOrder> fileOrderSet) {
        List<Integer> numberList = fileOrderSet.stream().map(FileOrder::getNumber).collect(Collectors.toList());
        Set<Integer> items = new HashSet<>();
        Set<Integer> duplicateSet = numberList.stream()
                .filter(n -> !items.add(n))
                .collect(Collectors.toSet());
        if (!duplicateSet.isEmpty()) {
            throw new ServerException().addInformationException(new InformationExceptionBuilder()
                    .errorCode(ErrorCode.ORDER_CONFIGURATION_DUPLICATE)
                    .messageParameters(List.of(configurationName, StringUtils.join(duplicateSet, ",")))
                    .build());
        }
    }


    /**
     * Permet de sauvegarder la configuration dans un fichier
     * @param file fichier
     * @param filesOrder objet à conserver
     */
    private void saveConfigurationOrderInFile(File file, FilesOrder filesOrder) {
        try {
            JSonFactoryUtils.createJsonInFile(filesOrder, file);
        } catch (IOException e) {
            throw new ServerException().addInformationException(new InformationExceptionBuilder()
                    .errorCode(ErrorCode.TECHNICAL_ERROR)
                    .parameters(Set.of(e.getMessage()))
                    .exceptionParent(e)
                    .build());
        }
    }

    /**
     * Permet de charger la configuration depuis un fichier
     * @param file fichier
     */
    private FilesOrder getConfigurationOrderFromFile(File file) {
        try (InputStream is = FileUtils.openInputStream(file)) {
            return JSonFactoryUtils.createFilesOrderFromJsonFile(is);
        } catch (IOException e) {
            throw new ServerException().addInformationException(new InformationExceptionBuilder()
                    .errorCode(ErrorCode.TECHNICAL_ERROR)
                    .parameters(Set.of(e.getMessage()))
                    .exceptionParent(e)
                    .build());
        }
    }

    /**
     * Permet de se procurer le fichier des ordres
     * @return le fichier des ordres
     */
    private File getFileOfOrders(Configuration configuration) {
        String rootPath = PathUtils.getCaerusFolder();
        File directory = new File(rootPath, RessourcesUtils.FOLDER_ORDER_CONFIGURATION);
        return new File(directory, configuration.getConfigurationOrderNameFile());
    }

}
