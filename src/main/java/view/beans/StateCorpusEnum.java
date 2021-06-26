package view.beans;

import controler.IConfigurationControler;

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
    CREATE(Optional.empty(), Optional.empty()),
    EDIT(Optional.of((controler, cmd) -> controler.getFieldInEditingCorpus(cmd.getKeyField())),
            Optional.of((controler, cmd) -> controler.updateFieldInEditingCorpus(cmd.getKeyField(), cmd.getValue()))),
    READ(Optional.of((controler, cmd) -> controler.getValueFromKeyTextAndField(cmd.getKeyText(), cmd.getKeyField())),
            Optional.empty());


    private final Optional<BiFunction<IConfigurationControler, StateCorpusGetActionCmd, String>> stateCorpusGetActionCmdStringBiFunction;
    private final Optional<BiConsumer<IConfigurationControler, StateCorpusSaveActionCmd>> stateCorpusSaveActionCmdBiConsumer;

    /**
     * Constructeur
     * @param stateCorpusGetActionCmdStringBiFunction stateCorpusGetActionCmdStringBiFunction
     * @param stateCorpusSaveActionCmdBiConsumer stateCorpusSaveActionCmdBiConsumer
     */
    StateCorpusEnum(Optional<BiFunction<IConfigurationControler, StateCorpusGetActionCmd, String>> stateCorpusGetActionCmdStringBiFunction, Optional<BiConsumer<IConfigurationControler, StateCorpusSaveActionCmd>> stateCorpusSaveActionCmdBiConsumer) {
        this.stateCorpusGetActionCmdStringBiFunction = stateCorpusGetActionCmdStringBiFunction;
        this.stateCorpusSaveActionCmdBiConsumer = stateCorpusSaveActionCmdBiConsumer;
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
}
