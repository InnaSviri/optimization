package ru.mipt.optimization.entity;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.inOut.Config;
import ru.mipt.optimization.entity.inOut.Result;
import ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.UndeterminateCostFunc;
import ru.mipt.optimization.entity.typeWrapper.FieldWrapper;
import ru.mipt.optimization.entity.typeWrapper.TypeWrapper;


import java.lang.reflect.Array;
import java.util.List;
import java.util.function.Function;

/** Represents an object for optimization of the given cost functions and
 *  is essentially a wrapper to go over external data types.
 *  Type {@link T} represents type of the elements of the vector argument of the cost functions
 * Created by Inna on 29.05.2017.
 */
public class Optimizator<T> {
    private int dimension;

    private TypeWrapper<T> typeConverter;

    private Config configurations;

    /**
     * Creates an Optimizator object to optimize cost functions of the vector argument with elements of {@link T} type.
     * It is necessary initially to set conversion rules toNumber and toType for
     * {@link ru.mipt.optimization.entity.Optimizator#typeConverter} in accordance with which
     * conversion of argument of type {@link T} to its Double interpretation will be occurred.
     * This rules are responsible for differentiation of the argument of the cost function in the optimization process
     * and the construction of the argument field.
     * On them depends the search time of the optimized decision and overall optimization performance.
     * @param dimension - dimension of the vector argument
     * @param toNumber - rule to convert argument of type {@link T} to its Double interpretation.
     * @param toType - rule to convert argument in its Double interpretation back to the type {@link T}.
     *               This rule cannot return null. It must cover conversion of all double numbers
     *               otherwise optimization can return its decision with an error.
     * @param tClass - Class of the type {@link T} (to get round type erasure).
     * @param configurations - configurations of this Optimizator session
     */
    public Optimizator(int dimension, Function<T, Double> toNumber, Function<Double, T> toType, 
                       Class<T> tClass, Config configurations) {
        if (toNumber == null || toType == null
                || tClass == null )
            throw new IllegalArgumentException("Arguments in Optimizator constructor can't be null");
        this.dimension = dimension;
        typeConverter = new TypeWrapper<T>(toNumber, toType, tClass);
        changeCongigurations(configurations);

    }

    /**
     * Changes configuration of this Optimizator session
     * @param newConfig - new configurations
     */
    public void changeCongigurations(Config newConfig) {
        if (configurations == null) throw new IllegalArgumentException("Configurations can't be null!");
        this.configurations = newConfig;
    }

    /**
     *
     * @param function - cost function over vector argument with elements of {@link T} type.
     *                 Note: dimension of the vector argument of the given function
     *                 must match current Optimizator's {@link ru.mipt.optimization.entity.Optimizator#dimension}
     * @param startPoints - list of points to start optimization process.
     *                    Note: the dimension of given points
     *                    must match current Optimizator's {@link ru.mipt.optimization.entity.Optimizator#dimension}
     * @return results of optimization for all given start points
     * @throws IllegalArgumentException if dimension of the vector argument of the given function  or of some point
     * in startPoints list does not match current Optimizator's {@link ru.mipt.optimization.entity.Optimizator#dimension}
     */
    public Result optimize(Function<T[], Double> function, List<T[]> startPoints) throws IllegalArgumentException {

        OptimizationProcedure procedure = new OptimizationProcedure(configurations.getAlgorithm(),
                createCostFunction(function), configurations.getStopCriteria());
        Result result = new Result<T>(procedure, typeConverter);

        for (T[] startPoint: startPoints) {
            if (startPoint.length != dimension || function.apply(startPoint) == null)
                throw new IllegalArgumentException("Either dimension of the given startPoint does not match Optimizator's dimension" +
                        " or given function does not match given startPoint");

            procedure.start(typeConverter.convertPoint(startPoint));
            result.updateResults();
        }

        return result;
    }

    //------------------------------------------------------------------------------------------------------------------

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    //------------------------------------------------------------------------------------------------------------------
    
    //// TODO: 03.10.2017 change to consider determinate or undeterminate cost function
    private CostFunction createCostFunction(final Function<T[], Double> initialFunc) {
        Function<Vector<Real>, Double> funcReal = new Function<Vector<Real>, Double>() {
            @Override
            public Double apply(Vector<Real> realVector) {
                return initialFunc.apply(typeConverter.convertPoint(realVector));
            }};
        return new UndeterminateCostFunc(funcReal, configurations.accuracyOfDomainSearch);
    }
    //-------------------------------------- inner classes -------------------------------------------------------------
/*

    public class ArgumentField extends FieldWrapper<T> {

        public ArgumentField(Double d) {
            super(d);
        }

        public ArgumentField(T t) {
            super(t);
        }

        // awful but is necessary because of the speed
        public ArgumentField(Real r) {
            super(r);
        }

        @Override
        protected FieldWrapper<T> valueOf(Double d) {
            return new ArgumentField(d);
        }

        @Override
        protected FieldWrapper<T> valueOf(T t) {
            return new ArgumentField(t);
        }

        @Override
        protected Double convertToNumber(T t) {
            return toNumber.apply(t);
        }

        @Override
        protected T convertToType(Double d) {
            return toType.apply(d);
        }

        // awful but is necessary because of the speed
        @Override
        protected FieldWrapper<T> valueOf(Real r) {
            return new ArgumentField(r);
        }
    }
*/

}
