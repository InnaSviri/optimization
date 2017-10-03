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

    public Double accuracyOfDomainSearch;

    private Algorithm algorithm;
    private StopCriteria stopCriteria;


    {setDefault();}

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public StopCriteria getStopCriteria() {
        return stopCriteria;
    }

    /**
     * Sets default configurations including static parameters.
     */
    public void setDefault() {
        accuracyOfDomainSearch = DEFAULT_ACCURACY;
        algorithm = new Kaczmarz();
    }

    public static double getDefaultDomainAccuracy() {
        return DEFAULT_ACCURACY;
    }

    public static double getDefaultSearchRange() {
        return DEFAULT_SEARCH_RANGE;
    }

}
