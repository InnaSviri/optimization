package ru.mipt.optimization.entity.inOut;

import ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure;

/**
 * Created by Inna on 01.09.2017.
 */
public class Result {

    private String algorithmName;

    private OptimizationProcedure optimizationProcedure;


    public Result(OptimizationProcedure optimizationProcedure) {
        this.optimizationProcedure = optimizationProcedure;
        plunder();
    }

    private void plunder() {
        try {
            optimizationProcedure.getOptimizedDecision();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Wrong argument for Result constructor: " +
                    "optimizationProcedure is raw. It necessary to start it at first!");
        }
        algorithmName =  optimizationProcedure.getAlgorithm().getName();
    }

    public double getOptimizationTime() {
        return optimizationProcedure.getOptimizationTime();
    }
}
