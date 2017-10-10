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
     * @return the new value of the argument x on the next iteration of the Algorithm
     * @throws IllegalArgumentException if x is not in the domain of the given cost function
     */
    public Vector<Real> conductOneIteration (Vector<Real> x, CostFunction function)
            throws IllegalArgumentException;

    /**
     * Checks if this Algorithm can optimize given cost function
     * @param function - function to check
     * @return true if can optimize given cost function false otherwise
     */
    public boolean isAble(Function<Vector<Real>, Double> function);

    /**
     * Returns stop criteria of the Algorithm
     * @return stop criteria of the Algorithm
     */
    public StopCriteria getStopCriteria();

    /**
     * Configures conditions to stop optimization procedure
     * @param error - error of the optimization process
     * @param conditions - flags to switch over special stop conditions. See concrete implementations.
     * @throws IllegalArgumentException if condition length does not correspond the required one
     * in the concrete implementation.
     */
    public void configureStopCriteria(double error, boolean... conditions);

    /**
     * Returns the name of the optimization algorithms type
     * @return the name of the optimization algorithms type
     */
    public String getName();

}
