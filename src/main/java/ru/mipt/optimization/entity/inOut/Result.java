package ru.mipt.optimization.entity.inOut;

import com.sun.istack.internal.NotNull;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.algorithms.Algorithm;
import ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure;
import ru.mipt.optimization.entity.typeWrapper.TypeWrapper;
import ru.mipt.optimization.supportive.Graphics;
import ru.mipt.optimization.supportive.Tuple;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

/**
 * Represents the results of the given optimization procedure.
 * Describes the optimization of fixed cost function by fixed algorithm with fixed parameters.
 * Created by Inna on 01.09.2017.
 */
public class Result<T> {

    private final Algorithm algorithm;
    private OptimizationProcedure optimizationProcedure; //
    private Map<T[], OneShot> allStartsResults = new HashMap<>();

    private final Parser parser;
    private MultiKeyMap<Boolean, LinkedList<OneShot>> sortedResults = new MultiKeyMap<Boolean, LinkedList<OneShot>>();

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

    /**
     * Returns optimization time of the given start of the optimization procedure.
     * @param startPoint - start point of the optimization procedure to determine particular start
     * @return time of the given optimization start
     */
    public double getOptimizationTime(T[] startPoint) {
        return allStartsResults.get(startPoint).time;
    }

    /**
     * Returns optimization time in the best start.
     * If both parameters byFinalDecision and byTime are true sorts by the best cost function first then by time.
     * @param byFinalDecision if true sorts by the best cost function of the final decision
     * @param  byTime if true sorts by the best time of the final decision
     * @return time of the best optimization start
     */
    public double getOptimizationTime(boolean byFinalDecision, boolean byTime) {
        return getSortedResults(false, true).peek().time;
    }


    /**
     * Returns optimized decision of the given start of the optimization procedure.
     * @param startPoint - start point of the optimization procedure to determine particular start
     * @return optimized decision  of the given optimization start
     */
    public Map.Entry<T[], Double> getOptimizedDecision(T[] startPoint) {
        return allStartsResults.get(startPoint).finalDecision;
    }

    /**
     * Returns optimized decision in the best start.
     * If both parameters byFinalDecision and byTime are true sorts by the best cost function first and then by time.
     * @param byFinalDecision if true sorts by the best cost function of the final decision
     * @param  byTime if true sorts by the best time of the final decision
     * @return optimized decision of the best optimization start
     */
    public Map.Entry<T[], Double> getOptimizedDecision(boolean byFinalDecision, boolean byTime) {
        return getSortedResults(false, true).peek().finalDecision;
    }


    /**
     * Returns optimization procedure evolution of the given start of the optimization procedure.
     * @param startPoint - start point of the optimization procedure to determine particular start
     * @return optimization procedure evolution of the given optimization start
     */
    public LinkedHashMap<T[], Double> getProcedurePoints(T[] startPoint) {
        return allStartsResults.get(startPoint).optimizationProcedureEvolution;
    }

    /**
     * Returns optimization procedure evolution in the best start.
     * If both parameters byFinalDecision and byTime are true sorts by the best cost function first and then by time.
     * @param byFinalDecision if true sorts by the best cost function of the final decision
     * @param  byTime if true sorts by the best time of the final decision
     * @return optimization procedure evolution of the best optimization start
     */
    public LinkedHashMap<T[], Double> getProcedurePoints(boolean byFinalDecision, boolean byTime) {
        return getSortedResults(false, true).peek().optimizationProcedureEvolution;
    }

    /**
     * Visualizes the optimization procedure of the given start.
     * @param startPoint - start point of the optimization procedure to determine particular start
     */
    public void visualizeProcedure(T[] startPoint) {
        List<Double> points = new ArrayList<>(allStartsResults.get(startPoint).optimizationProcedureEvolution.values());
        Graphics.drawPlot(points);
    }

    /**
     * Visualizes the optimization procedure in the best start.
     * If both parameters byFinalDecision and byTime are true sorts by the best cost function first and then by time.
     * @param byFinalDecision if true sorts by the best cost function of the final decision
     * @param  byTime if true sorts by the best time of the final decision
     */
    public void visualizeProcedure(boolean byFinalDecision, boolean byTime) {
        List<Double> points = new ArrayList<>(getSortedResults(false, true).peek()
                .optimizationProcedureEvolution.values());
        Graphics.drawPlot(points);
    }

    /**
     * Returns all starts of optimization procedure sorted by given parameters.
     * If both parameters byFinalDecision and byTime are true sorts by the best cost function first and then by time.
     * @param byFinalDecision if true sorts by the best cost function of the final decision
     * @param byTime if true sorts by the best time of the final decision
     * @return all starts of optimization procedure sorted by given parameters
     */
    public LinkedList<OneShot> getSortedResults(boolean byFinalDecision, boolean byTime) {
        LinkedList<OneShot> allStarts = sortedResults.get(byFinalDecision, byTime);
        if (allStarts == null) {
            allStarts = new LinkedList<>(allStartsResults.values());
            Collections.sort(allStarts, getResultsComparator(byFinalDecision, byTime));
            sortedResults.put(byFinalDecision, byTime, allStarts);
        }
        return allStarts;
    }

    //------------------------------------------------------------------------------------------------------------------

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public TypeWrapper<T> getConverter() {
        return parser.converter;
    }

    public Function<T[], Double> getCostFunction() {
        return new Function<T[], Double>() {
            @Override
            public Double apply(T[] t) {
                return optimizationProcedure.getCostFunction().apply(parser.parsePoint(t));
            }
        };
    }

   //------------------------------------------------------------------------------------------------------------------

    private Comparator<OneShot> getResultsComparator(final boolean byFinalDecision, final boolean byTime) {

        return new Comparator<OneShot>() {
            @Override
            public int compare(OneShot shot1, OneShot shot2) {
                int result = 0;

                if (byFinalDecision) result = Double.compare(shot1.finalDecision.getValue(),
                        shot2.finalDecision.getValue());
                if (result == 0 && byTime) result = Double.compare(shot1.time, shot2.time);

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
        final Map.Entry<T[], Double> finalDecision;
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

        public Map.Entry<T[], Double> parseFinalDecision(){
            Tuple<Vector<Real>, Double> decisionToConvert = optimizationProcedure.getOptimizedDecision();
            return new AbstractMap.SimpleEntry<T[], Double>(parsePoint(decisionToConvert.x), decisionToConvert.y);
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

        private Vector<Real> parsePoint(@NotNull T[] tPoint) {
            List<Real> l = new ArrayList(tPoint.length);
            for(T t: tPoint) l.add(converter.convert(t));
            return DenseVector.valueOf(l);
        }

    }

}
