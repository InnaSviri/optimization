package ru.mipt.optimization;

import ru.mipt.optimization.entity.Optimizator;
import ru.mipt.optimization.entity.inOut.Config;
import ru.mipt.optimization.entity.inOut.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Inna on 28.05.2017.
 */
public class App {
    public static void main( String[] args ) {
        Function<Integer, Double> toNumber = new Function<Integer, Double>() {
            @Override
            public Double apply(Integer integer) {
                return (double) integer;
            }
        };
        Function<Double, Integer> toType = new Function<Double, Integer>() {
            @Override
            public Integer apply(Double aDouble) {
                return aDouble.intValue();
            }
        };
        Class<Integer> clazz = Integer.class;

        Config config = new Config();
        config.configureStopCriteria(0.01,true,true,true,false);
        Optimizator<Integer> optTest = new Optimizator<>(2, toNumber, toType, clazz, config);

        Function<Integer[], Double> costFunc = new Function<Integer[], Double>() {
            @Override
            public Double apply(Integer[] integers) {
                return (double) ((integers[0]+1)*(integers[0]+1) + (integers[1]+1)*(integers[1]+1));
            }
        };
        Integer[] i1 = {1,1};
        Integer[] i2 = {0,0};
        List<Integer[]> startPoits = Arrays.asList(i1, i2);
        Result<Integer> res = optTest.optimize(costFunc,startPoits);
        res.getSortedResults(true,true);

    }

}
