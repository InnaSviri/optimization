package ru.mipt.optimization.entity.optimizationProcedure.costFunction.field;

import javolution.text.Text;
import org.jscience.mathematics.number.Number;
import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.structure.Field;

/**
 * Represents the algebraic field for type {@link T}.
 * Serves as a wrapper class to move from terms of type {@link T} to real numbers.
 * Created by Inna on 08.08.2017.
 */
public  abstract class FieldWrapper<T> extends Number<FieldWrapper<T>> implements Field<FieldWrapper<T>> {

    private T typeInterpretation;
    private Real realInterpretation;

    /**
     * Creates the FieldWrapper for the corresponding double number
     * @param d - Double number
     */
    public FieldWrapper(Double d) {
        realInterpretation = Real.valueOf(d);

    }

    /**
     * Creates the FieldWrapper for the corresponding object of type {@link T}
     * @param t - object of type {@link T}
     */
    public FieldWrapper(T t) {
        typeInterpretation = t;
        realInterpretation = convertToReal(t);
    }

    /**
     * Creates the FieldWrapper by its Real interpretation
     * @param r - Real number
     */
    protected FieldWrapper(Real r) {
        typeInterpretation = convertToType(r);
        realInterpretation = r;
    }

    /**
     * Returns the value represented by this object as a object of type {@link T}
     * @return the value represented by this object after conversion to type {@link T}.
     */
    public T typeValue() {
        return typeInterpretation;
    }

    @Override
    public long longValue() {
        return realInterpretation.longValue();
    }

    @Override
    public double doubleValue() {
        return realInterpretation.doubleValue();
    }

    @Override
    public FieldWrapper<T> inverse() {
        return valueOf(realInterpretation.inverse());
    }

    @Override
    public boolean isLargerThan(FieldWrapper fieldWrapper) {
        return realInterpretation.isLargerThan(fieldWrapper.realInterpretation);
    }


    @Override
    public int compareTo(FieldWrapper fieldWrapper) {
        return realInterpretation.compareTo(fieldWrapper.realInterpretation);
    }

    @Override
    public boolean equals(Object o) {
        return realInterpretation.equals(o);
    }

    @Override
    public int hashCode() {
        return realInterpretation.hashCode();
    }

    @Override
    public Text toText() {
        return Text.intern("Real interpretation: " +realInterpretation.toString()
                + "Type interpretation: " + typeInterpretation.toString());
    }

    @Override
    public Number<FieldWrapper<T>> copy() {
        return valueOf(realInterpretation.copy());
    }

    @Override
    public FieldWrapper<T> times(FieldWrapper fieldWrapper) {
        return valueOf(realInterpretation.times(fieldWrapper.realInterpretation));
    }

    @Override
    public FieldWrapper<T> plus(FieldWrapper fieldWrapper) {
        return valueOf(realInterpretation.plus(fieldWrapper.realInterpretation));
    }

    @Override
    public FieldWrapper<T> opposite() {
        return valueOf(realInterpretation.opposite());
    }

    /**
     *  Returns the FieldWrapper for the given Double number
     * @param d - the Double number
     * @return the corresponding FieldWrapper for the given Double number
     */
    protected abstract FieldWrapper<T> valueOf(Double d);

    /**
     *  Returns the FieldWrapper for the given object of type {@link T}
     * @param t - object of type {@link T}
     * @return the corresponding FieldWrapper for the given object of type {@link T}
     */
    protected abstract FieldWrapper<T> valueOf(T t);


    /**
     * Converts object of type {@link T} to its number (Double) interpretation
     * @param t - object to convert
     * @return corresponding Double interpretation of the given object
     */

    protected abstract Double convertToNumber(T t);

    /**
     * Converts object in its Double interpretation back to the type {@link T}
     * @param d - Double interpretation to convert
     * @return corresponding object in the initial type {@link T}
     */
    protected abstract T convertToType(Double d);

    /**
     *  Returns the FieldWrapper for the given Real
     * @param r - Real number
     * @return the corresponding FieldWrapper for the given Real
     */
    protected abstract FieldWrapper<T> valueOf(Real r);


    //----------------------- wrap up in Real ---------------------------------------------------------------------


    /**
     * Converts object in its Double interpretation back to the type {@link T}
     * @param r - Double interpretation to convert
     * @return corresponding object in the initial type {@link T}
     */
    private T convertToType(Real r){
        return convertToType(r.doubleValue());
    }


    /**
     * Converts object of type {@link T} to its number (Real) interpretation
     * @param t - object to convert
     * @return corresponding Real interpretation of the given object
     */
    private  Real convertToReal(T t){
        return Real.valueOf(convertToNumber(t));
    }


}
