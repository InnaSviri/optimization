package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.optimizationProcedure.StopCriteria;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Represents an iterative optimization operator.
 * Created by Inna on 26.02.2017.
 */
public interface Algorithm{

    /**
     * Performs one iteration according to the Algorithm.
     * Returns the new value of the argument x on the next iteration of the Algorithm
     * @param x the point from wich the next step will be performed.
     *          Note: Must be in the domain of the cost function
     *          otherwise the IllegalArgumentException is thrown
     * @param function the cost function to optimize
     * @param varParams varying parameters on this iteration of the optimization procedure
     * @return the new value of the argument x on the next iteration of the Algorithm
     * @throws IllegalArgumentException if x is not in the domain of the given cost function
     */
    Vector<Real> conductOneIteration (Vector<Real> x, CostFunction function, VaryingParams varParams)
            throws IllegalArgumentException;

    /**
     * Checks if this Algorithm can optimize given cost function
     * @param function - function to check
     * @return true if can optimize given cost function false otherwise
     */
    boolean isAble(CostFunction function);

    /**
     * Returns stop criteria of the Algorithm
     * @return stop criteria of the Algorithm
     */
    StopCriteria getStopCriteria();

    /**
     * Configures conditions to stop optimization procedure
     * @param errors - array of the errors of the optimization process.
     *               If size of errors array is less than required, rest parameters will be default.
     * @param conditions - flags to switch over special stop conditions. See concrete implementations.
     * @throws IllegalArgumentException if condition length does not correspond the required one
     * in the concrete implementation.
     */
    void configureStopCriteria(double[] errors, boolean... conditions);

    /**
     * Configures algorithm parameters. Number of parameters is clarified in concrete implementations.
     * @param params - algorithm parameters. If size of parameters is less than required, rest parameters will be default.
     * @return true if size of parameters correspond required by concrete implementation number.
     */
    boolean setParams(double... params);

    /**
     * Returns the name of the optimization algorithms type
     * @return the name of the optimization algorithms type
     */
    String getName();

    /**
     * Prints in String algorithm with its configuration
     * @return String with algorithms configuration
     */
    String print();

    /**
     * Returns configured for this implementation varying parameters
     * @return configured for this implementation varying parameters or default parameters if they haven't been configured
     */
    VaryingParams getVaryingParamsConfiguration();

}
