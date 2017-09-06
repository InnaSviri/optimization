package ru.mipt.optimization.entity.inOut;

import ru.mipt.optimization.algorithms.Algorithm;
import ru.mipt.optimization.algorithms.Kaczmarz;

/**
 * Represents the configurations of the optimization procedure
 * Created by Inna on 01.09.2017.
 */
public class Config {

    private final double DEFAULT_ACCURACY = 0.01;
    public Double accuracyOfDomainSearch;

    private Algorithm algorithm;


    {setDefault();}


    /**
     * Sets default configurations including static parameters.
     */
    public void setDefault() {
        accuracyOfDomainSearch = DEFAULT_ACCURACY;
        algorithm = new Kaczmarz();
    }

}
