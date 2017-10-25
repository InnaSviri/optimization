package ru.mipt.optimization.entity.inOut;

import ru.mipt.optimization.algorithms.Algorithm;
import ru.mipt.optimization.algorithms.GradientDescent;
import ru.mipt.optimization.algorithms.Kaczmarz;
import ru.mipt.optimization.entity.optimizationProcedure.StopCriteria;

/**
 * Represents the configurations of the optimization procedure
 * Created by Inna on 01.09.2017.
 */
public class Config {

    private static final double DEFAULT_ACCURACY = 0.01;
    private static final double DEFAULT_SEARCH_RANGE[] = {-10000,10000};
    private static final int DEFAULT_MAX_RECURSION_NUM = 400;

    public final double accuracyOfDomainSearch;
    public final double[] searchRange;

    private final Algorithm algorithm;

    /**
     * Creates Config object with given parameters
     * @param accuracyOfDomainSearch - accuracy with which the search of domain points will be performed.
     *                               Note: can't be zero, if zero default accuracy is set.
     * @param searchRange - search range, i.e. range of elements of the vector argument in their number interpretation.
     *                    Size of the array must be 2: first element lower bound and the second one - the upper bound.
     * @param algorithm - selected and tuned optimization algorithm
     * @throws IllegalArgumentException if the size of search range is not equal 2
     * or the first element is larger than the second one or algorithm is null.
     */
    public Config(double accuracyOfDomainSearch, double[] searchRange, Algorithm algorithm) {
        if (algorithm == null)
            throw new IllegalArgumentException("Arguments in Config constructor can't be null!");
        if (searchRange.length != 2 || searchRange[0]>searchRange[1])
            throw new IllegalArgumentException(" searchRange must be of size two " +
                    "and first element must be less than the second one!");
        this.searchRange = searchRange;
        this.accuracyOfDomainSearch = (accuracyOfDomainSearch!=0) ? accuracyOfDomainSearch : DEFAULT_ACCURACY;
        this.algorithm = algorithm;
    }

    /**
     * Creates Config object with default configurations:
     * algorithm - {@link ru.mipt.optimization.algorithms.GradientDescent};
     * accuracyOfDomainSearch - 0.01.
     */
    public Config() {
        accuracyOfDomainSearch = DEFAULT_ACCURACY;
        searchRange = DEFAULT_SEARCH_RANGE;
        algorithm = new GradientDescent();
    }

    /**
     * Creates Config object with given parameters and default accuracy of the domain search and search range
     * @param algorithm - selected and tuned optimization algorithm
     */
    public Config(Algorithm algorithm) {
        accuracyOfDomainSearch = DEFAULT_ACCURACY;
        searchRange = DEFAULT_SEARCH_RANGE;
        this.algorithm = algorithm;
    }

    /**
     * Configures conditions to stop optimization procedure
     * in the chosen optimization {@link ru.mipt.optimization.entity.inOut.Config#algorithm}
     * @param error - array of the errors of the optimization process.
     *               If size of errors array is less than required, rest parameters will be default.
     * @param conditions - flags to switch over special stop conditions.
     *                   See stop criteria in the chosen implementation
     *                   of the {@link ru.mipt.optimization.algorithms.Algorithm} interface.
     * @throws IllegalArgumentException if condition length does not correspond the required one
     * in the chosen implementation of the {@link ru.mipt.optimization.algorithms.Algorithm} interface.
     */
    public void configureStopCriteria(double[] error, boolean... conditions) {
        algorithm.configureStopCriteria(error, conditions);
    }

    /**
     * Configures the chosen optimization {@link ru.mipt.optimization.entity.inOut.Config#algorithm}'s parameters.
     * Number of parameters is clarified in concrete implementations
     * of the {@link ru.mipt.optimization.algorithms.Algorithm} interface.
     * @param params algorithm parameters. If size of parameters is less than required, rest parameters will be default.
     * @return true if size of parameters correspond required by concrete implementation number.
     */
    public boolean setAlgorithmParams(double... params) {
        return algorithm.setParams(params);
    }

    //------------------------------------------------------------------------------------------------------------------
    public int getMaxRecursionNumber() {
        double givenMax = (searchRange[1] - searchRange[0])/accuracyOfDomainSearch;
        Double max = (givenMax < DEFAULT_MAX_RECURSION_NUM) ? givenMax : DEFAULT_MAX_RECURSION_NUM;
        return max.intValue();
    }

    public Algorithm getAlgorithm() { return algorithm;}

    public static double getDefaultDomainAccuracy() {
        return DEFAULT_ACCURACY;
    }

    public static double[] getDefaultSearchRange() {
        return DEFAULT_SEARCH_RANGE;
    }

    public static int getDefaultMaxRecursionNum() {
        return DEFAULT_MAX_RECURSION_NUM;
    }

    @Override
    public String toString() {
        String str = "Congig: ";
        str += "accuracy of domain search is "+ accuracyOfDomainSearch
                + ", search range is [" + searchRange[0] + ", " + searchRange[1] + "]"
                     +"; \n algorithm is " +
                    algorithm.print();
        return str;
    }
}
