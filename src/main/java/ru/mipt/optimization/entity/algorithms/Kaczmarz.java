package ru.mipt.optimization.entity.algorithms;

import org.jscience.mathematics.vector.Vector;

import java.util.function.Function;

/**
 * Created by Inna on 15.05.2017.
 */
public class Kaczmarz extends PureAlgorithm {
    
    @Override
    public <X extends Vector> boolean isAble(Function<X, Double> function) {
        //// TODO: 29.05.2017 inspect function  
        return false;
    }

    @Override
    public String getName() {
        return "Kaczmarz";
    }
}
