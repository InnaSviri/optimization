package ru.mipt.optimization.entity.inOut;

import ru.mipt.optimization.algorithms.Algorithm;
import ru.mipt.optimization.algorithms.GradientDescent;
import ru.mipt.optimization.entity.optimizationProcedure.StopCriteria;

/**
 * Represents the configurations of the optimization procedure
 * Created by Inna on 01.09.2017.
 */
public class Config {

    private static final double DEFAULT_ACCURACY = 0.01;
    private static final double DEFAULT_SEARCH_RANGE = 10000;

    public final double accuracyOfDomainSearch;

    private final Algorithm algorithm;

    /**
     * Creates Config object with given parameters
     * @param accuracyOfDomainSearch - accuracy with which the search of domain points will be performed
     * @param algorithm - selected and tuned optimization algorithm
     */
    public Config(double accuracyOfDomainSearch, Algorithm algorithm) {
        if (algorithm == null)
            throw new IllegalArgumentException("Arguments in Config constructor can't be null!");

        this.accuracyOfDomainSearch = accuracyOfDomainSearch;
        this.algorithm = algorithm;
    }

    /**
     * Creates Config object with default configurations:
     * algorithm - {@link ru.mipt.optimization.algorithms.GradientDescent};
     * accuracyOfDomainSearch - 0.01.
     */
    public Config() {
        accuracyOfDomainSearch = DEFAULT_ACCURACY;
        algorithm = new GradientDescent();
    }

    /**
     * Creates Config object with given parameters and default accuracy of the domain search
     * @param algorithm - selected and tuned optimization algorithm
     */
    public Config(Algorithm algorithm) {
        accuracyOfDomainSearch = DEFAULT_ACCURACY;
        this.algorithm = algorithm;
    }

    /**
     * Configures conditions to stop optimization procedure
     * in the chosen optimization {@link ru.mipt.optimization.entity.inOut.Config#algorithm}
     * @param error - error of the optimization process
     * @param conditions - flags to switch over special stop conditions.
     *                   See stop criteria in the chosen implementation
     *                   of the {@link ru.mipt.optimization.algorithms.Algorithm} interface.
     * @throws IllegalArgumentException if condition length does not correspond the required one
     * in the chosen implementation of the {@link ru.mipt.optimization.algorithms.Algorithm} interface.
     */
    public void configureStopCriteria(double error, boolean... conditions) {
        algorithm.configureStopCriteria(error, conditions);
    }

    //------------------------------------------------------------------------------------------------------------------

    public Algorithm getAlgorithm() { return algorithm;}

    public static double getDefaultDomainAccuracy() {
        return DEFAULT_ACCURACY;
    }

    public static double getDefaultSearchRange() {
        return DEFAULT_SEARCH_RANGE;
    }

}
