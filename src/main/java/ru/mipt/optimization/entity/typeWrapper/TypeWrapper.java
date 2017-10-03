package ru.mipt.optimization.entity.typeWrapper;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.inOut.Config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents the wrapper to go over external data types.
 * Converts type {@link T} to its {@link org.jscience.mathematics.number.Real} interpretation and vice versa.
 * Uses the conversion rules given in constructor.
 * Created by Inna on 17.09.2017.
 */
public class TypeWrapper<T> {
    Map<T, Real> toRealMap = new HashMap<>();
    Map<Real, T> toTypeMap = new HashMap<>();

    final Function<T, Real> toRealRule;
    final Function<Real, T> toTypeRule;
    final Class<T> tClass;// awful thing to get round type erasure

    public TypeWrapper(final Function<T, Double> toRealRule, final Function<Double, T> toTypeRule, Class<T> tClass) {
        if (toRealRule == null || toTypeRule == null || tClass == null)
            throw new IllegalArgumentException("Arguments in TypeWrapper constructor can't be null");

        this.toRealRule = new Function<T, Real>() {
            @Override
            public Real apply(T t) {
                return Real.valueOf(toRealRule.apply(t));
            }
        };
        this.toTypeRule = new Function<Real, T>() {
            @Override
            public T apply(Real real) {
                return toTypeRule.apply(real.doubleValue());
            }
        };

        this.tClass = tClass;
    }

    /**
     *
     * @param r
     * @return
     * @throws
     */
    public T convert(Real r) {
        T typeInterpretation = toTypeMap.get(r);
        if (typeInterpretation == null) typeInterpretation = calculateTypeInterpretation(r);

        if (typeInterpretation == null) throw new RuntimeException("Invalid toTypeRule in TypeWrapper: " +
                "this rule can't return null, it must convert all Real numbers");

        return typeInterpretation;
    }

    /**
     *
     * @param t
     * @return
     */
    public @Nullable Real convert(T t) {
        Real realInterpretation = toRealMap.get(t);
        if (realInterpretation == null) {
            realInterpretation = toRealRule.apply(t);
            if (realInterpretation != null) {
                toRealMap.put(t, realInterpretation);
                toTypeMap.put(realInterpretation, t);
            }
        }
        return realInterpretation;
    }


    public T[] convertPoint(@NotNull Vector<Real> realPoint) {
        if (realPoint == null) throw new IllegalArgumentException("Can't convert null point!");

        T[] a = (T[]) Array.newInstance(tClass, realPoint.getDimension());
        for (int i = 0; i < realPoint.getDimension(); i++ ) a[i] = convert(realPoint.get(i));
        return a;
    }

    public Vector<Real> convertPoint(@NotNull T[] tPoint) {
        if (tPoint == null) throw new IllegalArgumentException("Can't convert null point!");
        List<Real> l = new ArrayList(tPoint.length);
        for(T t: tPoint) l.add(convert(t));
        return DenseVector.valueOf(l);
    }


    // Calculates type interpretation of the given Real number with the help of toTypeRule.
    // Finds its notnull value in case of null answer from toTypeRule
    // (within DefaultSearchRange by DefaultDomainAccuracy step)
    private @Nullable T calculateTypeInterpretation(Real r) {
        T typeInterpretation = null;
        int i = 1;
        while (typeInterpretation == null && i < Config.getDefaultSearchRange()/Config.getDefaultDomainAccuracy()) {
            typeInterpretation = toTypeRule.apply(r.plus(Real.valueOf(Config.getDefaultDomainAccuracy()*i)));
            i = (i + Integer.signum(i)) *(-1);
        }
        if (typeInterpretation != null) {
            toTypeMap.put(r,typeInterpretation);
            toRealMap.put(typeInterpretation,r);
        }
        return typeInterpretation;
    }
}
