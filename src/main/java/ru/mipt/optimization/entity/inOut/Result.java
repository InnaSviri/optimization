package ru.mipt.optimization.entity.inOut;

import com.sun.istack.internal.NotNull;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.algorithms.Algorithm;
import ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure;
import ru.mipt.optimization.entity.typeWrapper.TypeWrapper;
import ru.mipt.optimization.supportive.Graphics;
import ru.mipt.optimization.supportive.Tuple;

import java.util.*;
import java.util.function.Function;

/**
 * Represents the results of the given optimization procedure.
 * Describes the optimization of fixed cost function by fixed algorithm with fixed parameters.
 * Created by Inna on 01.09.2017.
 */
public class Result<T> {

    private OptimizationProcedure optimizationProcedure; //
    private Map<T[], OneShot> allStartsResults = new HashMap<>();

    private final Parser parser;
    private MultiKeyMap<Boolean, LinkedList<Result.OneShot>> sortedResults = new MultiKeyMap<Boolean, LinkedList<OneShot>>();

    /**
     * Creates empty Result object
     * @param optimizationProcedure - optimization procedure to get results from.
     *                              Can be raw, but it must be started before method {@link Result#updateResults()} is used.
     * @param converter - converter from type {@link T} to Real and conversely.
     */
    public Result(OptimizationProcedure optimizationProcedure, TypeWrapper<T> converter) {
        if (optimizationProcedure==null) throw new IllegalArgumentException("optimizationProcedure can't be null");
        this.optimizationProcedure = optimizationProcedure;
        this.parser = new Parser(converter);
    }

    /**
     * Adds result of the current start of the {@link ru.mipt.optimization.entity.inOut.Result#optimizationProcedure}
     *  Note: you must first start optimization procedure otherwise IllegalArgumentException is thrown.
     *  @throws IllegalArgumentException if optimization procedure hasn't been started.
     *  @throws RuntimeException if some arguments for optimization is wrong
     */
    public void updateResults() {
        try {
            OneShot shotToAdd = new OneShot();
            allStartsResults.put(shotToAdd.startPoint, shotToAdd);
        } catch (IllegalArgumentException ie) {
            throw new RuntimeException("Some arguments you enter are wrong: " + ie.getMessage()
                    + ". See initial message: \n" + ie.getMessage());
        } catch (RuntimeException re) {
            throw new IllegalArgumentException("OptimizationProcedure is raw. It necessary to start it at first!");
        }

    }

    /**
     * Returns optimization time of the given start of the optimization procedure.
     * @param startPoint - start point of the optimization procedure to determine particular start
     * @return time of the given optimization start
     */
    public double getOptimizationTime(T[] startPoint) {
        return (allStartsResults.get(startPoint) != null)
                ? allStartsResults.get(startPoint).time
                : 0;
    }

    /**
     * Returns optimization time in the best start.
     * If both parameters byFinalDecision and byTime are true sorts by the best cost function first then by time.
     * @param byFinalDecision if true sorts by the best cost function of the final decision
     * @param  byTime if true sorts by the best time of the final decision
     * @return time of the best optimization start
     */
    public double getOptimizationTime(boolean byFinalDecision, boolean byTime) {
        return (getSortedResults(false, true).peek() != null)
                ? getSortedResults(false, true).peek().time
                : 0;
    }


    /**
     * Returns optimized decision of the given start of the optimization procedure.
     * @param startPoint - start point of the optimization procedure to determine particular start
     * @return optimized decision  of the given optimization start
     */
    public Map.Entry<T[], Double> getOptimizedDecision(T[] startPoint) {
        return (allStartsResults.get(startPoint) != null)
                ? allStartsResults.get(startPoint).finalDecision
                : null;
    }

    /**
     * Returns optimized decision in the best start.
     * If both parameters byFinalDecision and byTime are true sorts by the best cost function first and then by time.
     * @param byFinalDecision if true sorts by the best cost function of the final decision
     * @param  byTime if true sorts by the best time of the final decision
     * @return optimized decision of the best optimization start
     */
    public Map.Entry<T[], Double> getOptimizedDecision(boolean byFinalDecision, boolean byTime) {
        return (getSortedResults(false, true).peek() != null)
                ? getSortedResults(false, true).peek().finalDecision
                : null;
    }


    /**
     * Returns optimization procedure evolution of the given start of the optimization procedure.
     * @param startPoint - start point of the optimization procedure to determine particular start
     * @return optimization procedure evolution of the given optimization start
     */
    public LinkedHashMap<T[], Double> getProcedurePoints(T[] startPoint) {
        return (allStartsResults.get(startPoint) != null)
                ? allStartsResults.get(startPoint).optimizationProcedureEvolution
                : new LinkedHashMap<T[], Double>();
    }

    /**
     * Returns optimization procedure evolution in the best start.
     * If both parameters byFinalDecision and byTime are true sorts by the best cost function first and then by time.
     * @param byFinalDecision if true sorts by the best cost function of the final decision
     * @param  byTime if true sorts by the best time of the final decision
     * @return optimization procedure evolution of the best optimization start
     */
    public LinkedHashMap<T[], Double> getProcedurePoints(boolean byFinalDecision, boolean byTime) {
        return (getSortedResults(false, true).peek() != null)
            ? getSortedResults(false, true).peek().optimizationProcedureEvolution
                : new LinkedHashMap<T[], Double>();
    }

    /**
     * Visualizes the optimization procedure of the given start.
     * @param startPoint - start point of the optimization procedure to determine particular start
     */
    public void visualizeProcedure(T[] startPoint) {
        List<Double> points = new ArrayList<>();
        if (allStartsResults.get(startPoint) != null)
            points.addAll(allStartsResults.get(startPoint).optimizationProcedureEvolution.values());
        Graphics.drawPlot(points);
    }

    /**
     * Visualizes the optimization procedure in the best start.
     * If both parameters byFinalDecision and byTime are true sorts by the best cost function first and then by time.
     * @param byFinalDecision if true sorts by the best cost function of the final decision
     * @param  byTime if true sorts by the best time of the final decision
     */
    public void visualizeProcedure(boolean byFinalDecision, boolean byTime) {
        List<Double> points = new ArrayList<Double>();
        if (getSortedResults(false, true).peek() != null)
            points.addAll(getSortedResults(false, true).peek().optimizationProcedureEvolution.values());
        Graphics.drawPlot(points);
    }

    /**
     * Returns all starts of optimization procedure sorted by given parameters.
     * If both parameters byFinalDecision and byTime are true sorts by the best cost function first and then by time.
     * @param byFinalDecision if true sorts by the best cost function of the final decision
     * @param byTime if true sorts by the best time of the final decision
     * @return all starts of optimization procedure sorted by given parameters
     */
    public LinkedList<Result.OneShot> getSortedResults(boolean byFinalDecision, boolean byTime) {
        LinkedList<Result.OneShot> allStarts = sortedResults.get(byFinalDecision, byTime);
        if (allStarts == null) {
            allStarts = new LinkedList<Result.OneShot>(allStartsResults.values());
            if (!allStarts.isEmpty()) {
                Collections.sort(allStarts, getResultsComparator(byFinalDecision, byTime));
                sortedResults.put(byFinalDecision, byTime, allStarts);
            }
        }
        return allStarts;
    }

    //------------------------------------------------------------------------------------------------------------------

    public Config getConfigurations() {
        return optimizationProcedure.getConfigurations();
    }

    public TypeWrapper<T> getConverter() {
        return parser.converter;
    }

    public Function<T[], Double> getCostFunction() {
        return new Function<T[], Double>() {
            @Override
            public Double apply(T[] t) {
                return optimizationProcedure.getCostFunction().apply(parser.converter.convertPoint(t));
            }
        };
    }

   //------------------------------------------------------------------------------------------------------------------

    private Comparator<Result.OneShot> getResultsComparator(final boolean byFinalDecision, final boolean byTime) {

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
        public final T[] startPoint;
        public final Double time;
        public final Map.Entry<T[], Double> finalDecision;
        public final LinkedHashMap<T[], Double> optimizationProcedureEvolution;

        public OneShot() {
            this.finalDecision = parser.parseFinalDecision(); // if optimization procedure has't been started
            this.startPoint = parser.parseStartPoint();
            this.time = optimizationProcedure.getOptimizationTime();
            this.optimizationProcedureEvolution = parser.parseOptimizationProcedureEvolution();
        }
    }

    // parses results from current optimizationProcedure
    private class Parser {
        private final TypeWrapper<T> converter; //convert back to external type T

        public Parser(TypeWrapper<T> converter) {
            this.converter = converter;
        }

        public T[] parseStartPoint(){
            return converter.convertPoint(optimizationProcedure.getProcedurePoints().peek());
        }

        public Map.Entry<T[], Double> parseFinalDecision(){
            Tuple<Vector<Real>, Double> decisionToConvert = optimizationProcedure.getOptimizedDecision();
            return new AbstractMap.SimpleEntry<T[], Double>(converter.convertPoint(decisionToConvert.x),
                    decisionToConvert.y);
        }

        public LinkedHashMap<T[], Double> parseOptimizationProcedureEvolution() {
            Function<Vector<Real>,Double> costFunc = optimizationProcedure.getCostFunction();
            LinkedHashMap<T[], Double> optimizationProcedureEvolution = new LinkedHashMap<>();
            for (Vector<Real> point: optimizationProcedure.getProcedurePoints())
                optimizationProcedureEvolution.put(converter.convertPoint(point), costFunc.apply(point));
            return optimizationProcedureEvolution;
        }

    }

}
