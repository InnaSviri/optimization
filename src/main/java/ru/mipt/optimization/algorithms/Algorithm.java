package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.vector.Vector;

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
     * @param function the cost function
     * @param <X> type of the domain of the given cost function.
     *           Extends {@link org.jscience.mathematics.vector.Vector}
     * @return the new value of the argument x on the next iteration of the Algorithm
     * @throws IllegalArgumentException if x is not in the domain of the given cost function
     */
    public <X extends Vector> X conductOneIteration (X x, Function<X, Double> function)
            throws IllegalArgumentException;

    /**
     *
     * @param function
     * @param <X>
     * @return
     */
    public <X extends Vector> boolean isAble(Function<X, Double> function);

    /**
     * Returns the name of the optimization algorithms type
     * @return the name of the optimization algorithms type
     */
    public String getName();
}
