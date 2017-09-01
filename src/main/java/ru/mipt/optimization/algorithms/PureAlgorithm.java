package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.vector.Vector;

import java.util.function.Function;

/**
 * An abstract implementation of the Algorithm interface that covers pure optimization algorithms, not hybrid variations.
 * Created by Inna on 26.02.2017.
 */
public abstract class PureAlgorithm implements Algorithm {

    @Override
    public <X extends Vector> X conductOneIteration(X x, Function<X,Double> function) throws IllegalArgumentException {
        //// TODO: 29.05.2017
        return null;
    }

}
