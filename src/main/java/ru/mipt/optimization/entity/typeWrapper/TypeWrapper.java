package ru.mipt.optimization.entity.typeWrapper;

import com.sun.istack.internal.Nullable;
import org.jscience.mathematics.number.Real;
import ru.mipt.optimization.entity.inOut.Config;

import java.util.HashMap;
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

    public TypeWrapper(final Function<T, Double> toRealRule, final Function<Double, T> toTypeRule) {
        if (toRealRule == null || toTypeRule == null)
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
        if (realInterpretation == null) realInterpretation = toRealRule.apply(t);
        return realInterpretation;
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
        return typeInterpretation;
    }
}
