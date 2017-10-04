package ru.mipt.optimization.entity.inOut;

import ru.mipt.optimization.algorithms.Algorithm;
import ru.mipt.optimization.algorithms.Kaczmarz;
import ru.mipt.optimization.entity.optimizationProcedure.StopCriteria;

/**
 * Represents the configurations of the optimization procedure
 * Created by Inna on 01.09.2017.
 */
public class Config {

    private static final double DEFAULT_ACCURACY = 0.01;
    private static final double DEFAULT_SEARCH_RANGE = 10000;

    public final Double accuracyOfDomainSearch;

    public final Algorithm algorithm;
    public final StopCriteria stopCriteria;

    /**
     * Creates Config object with given parameters
     * @param accuracyOfDomainSearch - accuracy with which the search of domain points will be performed
     * @param algorithm - selected and tuned optimization algorithm
     * @param stopCriteria - condition to stop optimization procedure
     */
    public Config(Double accuracyOfDomainSearch, Algorithm algorithm, StopCriteria stopCriteria) {
        if (accuracyOfDomainSearch == null || algorithm == null || stopCriteria == null)
            throw new IllegalArgumentException("Arguments in Config constructor can't be null!");

        this.accuracyOfDomainSearch = accuracyOfDomainSearch;
        this.algorithm = algorithm;
        this.stopCriteria = stopCriteria;
    }

    /**
     * Creates Cofig object with default configurations.
     */
    public Config() {
        accuracyOfDomainSearch = DEFAULT_ACCURACY;
        algorithm = new Kaczmarz();
        stopCriteria = new StopCriteria();
    }

    /**
     * Creates Config object with given parameters and default accuracy of the domain search
     * @param algorithm - selected and tuned optimization algorithm
     * @param stopCriteria - condition to stop optimization procedure
     */
    public Config(Algorithm algorithm, StopCriteria stopCriteria) {
        accuracyOfDomainSearch = DEFAULT_ACCURACY;
        this.algorithm = algorithm;
        this.stopCriteria = stopCriteria;
    }

    {setDefault();}

    /**
     *
     */
    public void setDefault() {

    }

    public static double getDefaultDomainAccuracy() {
        return DEFAULT_ACCURACY;
    }

    public static double getDefaultSearchRange() {
        return DEFAULT_SEARCH_RANGE;
    }

}
