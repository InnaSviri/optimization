package ru.mipt.optimization.entity.inOut;

import ru.mipt.optimization.algorithms.Algorithm;
import ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure;
import ru.mipt.optimization.supportive.Tuple;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Inna on 01.09.2017.
 */
public class Result<T> {

    private final Algorithm algorithm;
    private final Function<T[], Double> function; //

    private OptimizationProcedure optimizationProcedure; //

    private Map<T[], OneShot> allStartsResults = new HashMap<>();


    public Result(OptimizationProcedure optimizationProcedure, Function<T[], Double> function) {
        if (optimizationProcedure==null) throw new IllegalArgumentException("optimizationProcedure can't be null");
        this.algorithm = optimizationProcedure.getAlgorithm();
        this.function = function;
        updateResults();
    }

    public void updateResults() {
        try {
            optimizationProcedure.getOptimizedDecision();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Wrong argument for Result constructor: " +
                    "optimizationProcedure is raw. It necessary to start it at first!");
        }

    }

    public double getOptimizationTime() {
        return optimizationProcedure.getOptimizationTime();
    }

    

    //------------------------------------------------------------------------------------------------------------------

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public Function<T[], Double> getFunction() {
        return function;
    }

    //------------------------------------------------------------------------------------------------------------------

    private Comparator<Map.Entry<T[], OneShot>> getResultsComparator(final boolean byFinalDecision, final boolean byTime) {

        return new Comparator<Map.Entry<T[], OneShot>>() {
            @Override
            public int compare(Map.Entry<T[], OneShot> entry1, Map.Entry<T[], OneShot> entry2) {
                int result = 0;

                if (byFinalDecision) result = Double.compare(entry1.getValue().finalDecision.y,
                        entry2.getValue().finalDecision.y);
                if (result == 0 && byTime) result = Double.compare(entry1.getValue().time, entry2.getValue().time);

                return result;

            }};
    }
    //------------------------------------------ inner -----------------------------------------------------------------

    //represents result of the one start of the optimizationProcedure
    public class OneShot {
        T[] startPoint;
        Double time;
        Tuple<T[], Double> finalDecision;
        LinkedHashMap<T[], Double> optimizationProcedureEvolution;
    }

}
