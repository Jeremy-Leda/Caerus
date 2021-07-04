package model.analyze.lexicometric.beans;

import model.PojoBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

/**
 *
 * Bean pour la configuration de remplissage des tables
 *
 * @param <T> Type d'objet à retourner
 */
@PojoBuilder
public class FillTableConfiguration<T> {

    @NotNull(message = "source ne peut pas être null")
    private Optional<Integer> source;

    @NotNull(message = "dest ne peut pas être null")
    @Min(0)
    private Integer dest;

    @NotNull(message = "biFunction ne peut pas être null")
    private BiFunction<String, LinkedList<T>, Set<T>> biFunction;

    /**
     * Permet de se procurer la source si existante
     * @return la source
     */
    public Optional<Integer> getSource() {
        return source;
    }

    /**
     * Permet de définir la source si existante
     * @param source la source
     */
    public void setSource(Optional<Integer> source) {
        this.source = source;
    }

    /**
     * Permet de se procurer la destination
     * @return la destination
     */
    public Integer getDest() {
        return dest;
    }

    /**
     * Permet de définir la destination
     * @param dest la destination
     */
    public void setDest(Integer dest) {
        this.dest = dest;
    }

    /**
     * Permet de se procurer la fonction de remplissage
     * @return la fonction de remplissage
     */
    public BiFunction<String, LinkedList<T>, Set<T>> getBiFunction() {
        return biFunction;
    }

    /**
     * Permet de définir la fonction de remplissage
     * @param biFunction  la fonction de remplissage
     */
    public void setBiFunction(BiFunction<String, LinkedList<T>, Set<T>> biFunction) {
        this.biFunction = biFunction;
    }
}
