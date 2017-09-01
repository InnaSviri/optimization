package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.vector.Vector;

import java.util.List;
import java.util.function.Function;

/**
 * An abstract implementation of the Algorithm interface that covers hybrid optimization algorithms,
 * i.e. variations of mixed pure algorithms.
 * Created by Inna on 26.02.2017.
 */
public abstract class HybridAlgorithm implements Algorithm{

    @Override
    public <X extends Vector> X conductOneIteration(X x, Function<X,Double> function) {
        for (PureAlgorithm p: getPureAlgorithms()) p.conductOneIteration(x, function);
        //TODO
        return null;
    }

    /**
     * Returns the list of pure optimization algorithms to mix in hybrid variation
     * @return
     */
    abstract List<PureAlgorithm> getPureAlgorithms();

}
