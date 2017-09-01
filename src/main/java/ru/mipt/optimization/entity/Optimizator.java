package ru.mipt.optimization.entity;

import org.jscience.mathematics.number.Real;
import ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure;
import ru.mipt.optimization.entity.optimizationProcedure.FieldWrapper;


import java.util.function.Function;

/** Represents an object for optimization of the given cost functions and
 *  is essentially a wrapper to go over external data types.
 *  Type {@link T} represents type of the elements of the vector argument of the cost functions
 * Created by Inna on 29.05.2017.
 */
public class Optimizator<T> {
    private int dimension;

    private Function<T, Double> toNumber;
    private Function<Double, T> toType;


    /**
     * Creates an Optimizator object to optimize cost functions of the vector argument with elements of {@link T} type.
     * It is necessary initially to set rules {@link ru.mipt.optimization.entity.Optimizator#toNumber}
     * and {@link ru.mipt.optimization.entity.Optimizator#toType} for
     * {@link ru.mipt.optimization.entity.Optimizator.ArgumentField} in accordance with which
     * conversion of argument of type {@link T} to its Double interpretation will be occurred.
     * This rules are responsible for differentiation of the argument of the cost function in the optimization process.
     * On them depends the search time of the optimized decision and overall optimization performance.
     * @param dimension - dimension of the vector argument
     * @param toNumber - rule to convert argument of type {@link T} to its Double interpretation
     * @param toType - rule to convert argument in its Double interpretation back to the type {@link T}
     */
    public Optimizator(int dimension, Function<T, Double> toNumber, Function<Double, T> toType) {
        if (toNumber == null || toType == null)
            throw new IllegalArgumentException("Arguments in Optimizator constructor can't be null");
        this.dimension = dimension;
        setArgumentField(toNumber, toType);
    }

    /**
     * Sets rules {@link ru.mipt.optimization.entity.Optimizator#toNumber}
     * and {@link ru.mipt.optimization.entity.Optimizator#toType} for
     * {@link ru.mipt.optimization.entity.Optimizator.ArgumentField} in accordance with which
     * conversion of argument of type {@link T} to its Double interpretation is occurred.
     * This rules are responsible for differentiation of the argument of the cost function in the optimization process.
     * On them depends the search time of the optimized decision and overall optimization performance.
     * @param toNumber - rule to convert argument of type {@link T} to its Double interpretation
     * @param toType - rule to convert argument in its Double interpretation back to the type {@link T}
     */
    public void setArgumentField(final Function<T, Double> toNumber, final Function<Double, T> toType) {
        this.toNumber = toNumber;
        this.toType = toType;
    }

    /**
     *
     * @param function - cost function over vector argument with elements of {@link T} type.
     *                 Note: dimension of the vector argument of the given function
     *                 must match current Optimizator's {@link ru.mipt.optimization.entity.Optimizator#dimension}
     * @param startPoint - point to start optimization process. Note: the dimension of
     * @throws IllegalArgumentException if dimension of the vector argument of the given function  or of startPoint
     * does not match current Optimizator's {@link ru.mipt.optimization.entity.Optimizator#dimension}
     */
    public OptimizationProcedure optimize(Function<T[], Double> function, T[] startPoint) throws IllegalArgumentException{
        if (startPoint.length != dimension
                || function.apply(startPoint) == null)
            throw new IllegalArgumentException("Either dimension of the given startPoint does not match Optimizator's dimension" +
                    " or given function doues not match given startPoint");
        return null; // TODO
    }

    //------------------------------------------------------------------------------------------------------------------

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    //-------------------------------------- inner classes -------------------------------------------------------------

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

}
