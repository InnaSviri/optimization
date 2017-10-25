package ru.mipt.optimization;

import ru.mipt.optimization.algorithms.GradientKaczmarzTraining;
import ru.mipt.optimization.entity.Optimizator;
import ru.mipt.optimization.entity.inOut.Config;
import ru.mipt.optimization.entity.inOut.Result;

import java.util.*;
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

        GradientKaczmarzTraining testtttAlgo = new GradientKaczmarzTraining();
        double[] serchRange = {-1000, 1000};
        Config config = new Config(1, serchRange, testtttAlgo);
        double[] errors = {0.01, 3, 0.009, 0.01, 0.1, 50, 60, 30};
        config.configureStopCriteria(errors,true,true,true,true,true,true);
        config.setAlgorithmParams(1.2,0.5,0.5);

        Config config1 = new Config();

        Function<Integer[], Double> costFunc = new Function<Integer[], Double>() {
            @Override
            public Double apply(Integer[] integers) {
                int sum = 0;
                for (int i=0; i<integers.length;i++)
                    sum += Math.abs(integers[i])*(i+1);
                return (double) sum;
            }
        };

        Optimizator<Integer> optTest = new Optimizator<>(2, toNumber, toType, clazz, costFunc);

        Integer[] i1 = {10,10};
        Integer[] i2 = {0,0};
        List<Integer[]> startPoits = Arrays.asList(i1, i2);
        optTest.optimize(config, startPoits);
        LinkedList<Result<Integer>> results = optTest.getHistory().getSortedResults(true,false);
        Set<Integer[]> finalResult = new HashSet<>();
        for (Result<Integer> res: results) {
            System.out.print(res.getConfigurations().toString() + "\n");
            for (Result<Integer>.OneShot oneSh : res.getSortedResults(true, false)) {
                finalResult.add(oneSh.finalDecision.getKey());
                System.out.print(oneSh.print());
            }
        }
        finalResult.size();

    }

}
