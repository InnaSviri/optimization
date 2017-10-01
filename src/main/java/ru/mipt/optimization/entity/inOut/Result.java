package ru.mipt.optimization.entity.inOut;

import com.sun.istack.internal.NotNull;
import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.algorithms.Algorithm;
import ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure;
import ru.mipt.optimization.entity.typeWrapper.FieldWrapper;
import ru.mipt.optimization.entity.typeWrapper.TypeWrapper;
import ru.mipt.optimization.supportive.Tuple;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

/**
 * Created by Inna on 01.09.2017.
 */
public class Result<T> {
    private final Parser parser;

    private final Algorithm algorithm;
    private OptimizationProcedure optimizationProcedure; //

    private Map<T[], OneShot> allStartsResults = new HashMap<>();


    public Result(OptimizationProcedure optimizationProcedure, TypeWrapper<T> converter, Class<T> tClass) {
        if (optimizationProcedure==null) throw new IllegalArgumentException("optimizationProcedure can't be null");
        this.algorithm = optimizationProcedure.getAlgorithm();
        this.parser = new Parser(converter, tClass);
        updateResults();
    }

    public void updateResults() {
        try {
            OneShot shotToAdd = new OneShot();
            allStartsResults.put(shotToAdd.startPoint, shotToAdd);
        } catch (IllegalArgumentException ie) {
            throw new RuntimeException("Some arguments you enter are wrong: " + ie.getMessage()
                    + ". See stack trace: \n" + ie.getStackTrace());
        } catch (RuntimeException re) {
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

    public TypeWrapper<T> getConverter() {
        return parser.converter;
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

    /**
     * Represents result of the one start of the optimizationProcedure
     */
    public class OneShot {
        final T[] startPoint;
        final Double time;
        final Tuple<T[], Double> finalDecision;
        final LinkedHashMap<T[], Double> optimizationProcedureEvolution;

        public OneShot() {
            this.finalDecision = parser.parseFinalDecision(); // if optimization procedure has't been started
            this.startPoint = parser.parseStartPoint();
            this.time = optimizationProcedure.getOptimizationTime();
            this.optimizationProcedureEvolution = parser.parseOptimizationProcedureEvolution();
        }
    }

    // parses results from current optimizationProcedure
    private class Parser {
        private Class<T> tClass; // awful thing to get round type erasure
        private final TypeWrapper<T> converter; //convert back to external type T

        public Parser(TypeWrapper<T> converter, Class<T> tClass) {
            this.converter = converter;
            this.tClass = tClass;
        }

        public T[] parseStartPoint(){
            return parsePoint(optimizationProcedure.getProcedurePoints().peek());
        }

        public Tuple<T[], Double> parseFinalDecision(){
            Tuple<Vector<Real>, Double> decisionToConvert = optimizationProcedure.getOptimizedDecision();
            return new Tuple<T[], Double>(parsePoint(decisionToConvert.x), decisionToConvert.y);
        }

        public LinkedHashMap<T[], Double> parseOptimizationProcedureEvolution() {
            Function<Vector<Real>,Double> costFunc = optimizationProcedure.getCostFunction();
            LinkedHashMap<T[], Double> optimizationProcedureEvolution = new LinkedHashMap<>();
            for (Vector<Real> point: optimizationProcedure.getProcedurePoints())
                optimizationProcedureEvolution.put(parsePoint(point), costFunc.apply(point));
            return optimizationProcedureEvolution;
        }

        private T[] parsePoint(@NotNull Vector<Real> realPoint) {
            T[] a = (T[]) Array.newInstance(tClass, realPoint.getDimension());
            for (int i = 0; i < realPoint.getDimension(); i++ ) a[i] = converter.convert(realPoint.get(i));
            return a;
        }

    }

}
