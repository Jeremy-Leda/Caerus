package view.beans;

import controler.IConfigurationControler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 *
 * Enumeration pour la gestion des corpus
 * Permet de définir les différentes actions en fonction du comportement attendu
 *
 *
 * La création d'un texte équivaut à l'édition d'un corpus
 *
 */
public enum StateCorpusEnum {
    CREATE(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
    EDIT(Optional.of((controler, cmd) -> controler.getFieldInEditingCorpus(cmd.getKeyField())),
            Optional.of((controler, cmd) -> controler.updateFieldInEditingCorpus(cmd.getKeyField(), cmd.getValue())),
            Optional.of(((controler, cmd) -> controler.getSpecificFieldInEditingCorpus(cmd.getIndex()))),
            Optional.of(((controler, cmd) -> controler.updateSpecificFieldInEditingCorpus(cmd.getIndex(), cmd.getValueMap())))),
    READ(Optional.of((controler, cmd) -> controler.getValueFromKeyTextAndField(cmd.getKeyText(), cmd.getKeyField())),
            Optional.empty(),
            Optional.of(((controler, cmd) -> controler.getSpecificFieldInUserStructuredText(cmd.getKeyText(), cmd.getIndex()))),
            Optional.empty());


    private final Optional<BiFunction<IConfigurationControler, StateCorpusGetActionCmd, String>> stateCorpusGetActionCmdStringBiFunction;
    private final Optional<BiConsumer<IConfigurationControler, StateCorpusSaveActionCmd>> stateCorpusSaveActionCmdBiConsumer;
    private final Optional<BiFunction<IConfigurationControler, StateCorpusGetSpecificActionCmd, Map<String, List<String>>>> stateCorpusGetSpecificActionCmdMapBiFunction;
    private final Optional<BiConsumer<IConfigurationControler, StateCorpusSaveSpecificActionCmd>> stateCorpusSaveSpecificActionCmdBiConsumer;


    /**
     * Constructeur
     * @param stateCorpusGetActionCmdStringBiFunction stateCorpusGetActionCmdStringBiFunction
     * @param stateCorpusSaveActionCmdBiConsumer stateCorpusSaveActionCmdBiConsumer
     * @param stateCorpusGetSpecificActionCmdMapBiFunction
     * @param stateCorpusSaveSpecificActionCmdBiConsumer
     */
    StateCorpusEnum(Optional<BiFunction<IConfigurationControler, StateCorpusGetActionCmd, String>> stateCorpusGetActionCmdStringBiFunction, Optional<BiConsumer<IConfigurationControler, StateCorpusSaveActionCmd>> stateCorpusSaveActionCmdBiConsumer, Optional<BiFunction<IConfigurationControler, StateCorpusGetSpecificActionCmd, Map<String, List<String>>>> stateCorpusGetSpecificActionCmdMapBiFunction, Optional<BiConsumer<IConfigurationControler, StateCorpusSaveSpecificActionCmd>> stateCorpusSaveSpecificActionCmdBiConsumer) {
        this.stateCorpusGetActionCmdStringBiFunction = stateCorpusGetActionCmdStringBiFunction;
        this.stateCorpusSaveActionCmdBiConsumer = stateCorpusSaveActionCmdBiConsumer;
        this.stateCorpusGetSpecificActionCmdMapBiFunction = stateCorpusGetSpecificActionCmdMapBiFunction;
        this.stateCorpusSaveSpecificActionCmdBiConsumer = stateCorpusSaveSpecificActionCmdBiConsumer;
    }

    /**
     * Permet de se procurer la bifunction pour aller chercher les informations d'un texte
     * @return la bifunction (optionel)
     */
    public Optional<BiFunction<IConfigurationControler, StateCorpusGetActionCmd, String>> getOptionalStateCorpusGetActionCmdStringBiFunction() {
        return stateCorpusGetActionCmdStringBiFunction;
    }

    /**
     * Permet de se procurer le biconsumer pour sauvegarder les informations d'un texte
     * @return le biconsumer (optionel)
     */
    public Optional<BiConsumer<IConfigurationControler, StateCorpusSaveActionCmd>> getOptionalStateCorpusSaveActionCmdBiConsumer() {
        return stateCorpusSaveActionCmdBiConsumer;
    }

    /**
     * Permet de se procurer la bifunction pour aller chercher les informations spécifiques d'un texte
     * @return la bifunction (optionel)
     */
    public Optional<BiFunction<IConfigurationControler, StateCorpusGetSpecificActionCmd, Map<String, List<String>>>> getStateCorpusGetSpecificActionCmdMapBiFunction() {
        return stateCorpusGetSpecificActionCmdMapBiFunction;
    }

    /**
     * Permet de se procurer le biconsumer pour sauvegarder les informations spécifiques d'un texte
     * @return le biconsumer (optionel)
     */
    public Optional<BiConsumer<IConfigurationControler, StateCorpusSaveSpecificActionCmd>> getStateCorpusSaveSpecificActionCmdBiConsumer() {
        return stateCorpusSaveSpecificActionCmdBiConsumer;
    }
}
