package Neural_Network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jayden on 8/1/2015.
 */
public class NeuralUtils {
    public static Double[] d2D(double... ds) {
        Double[] ret = new Double[ds.length];
        for (int i=0;i<ds.length;i++) {
            ret[i] = ds[i];

        }
        return ret;
    }
    public static List<List<Double>> d2List2D(double[]... ds) {
        List<List<Double>> ret = new ArrayList<List<Double>>(ds.length);
        for (int i=0;i<ds.length;i++) {
            ret.add(i, Arrays.asList(d2D(ds[i])));

        }
        return ret;
    }
    public static double[][] TwoDListTod(List<List<Double>> ds) {
        double[][] ret = new double[ds.size()][];
        for (int i=0;i<ds.size();i++) {
            ret[i] = D2d(ds.get(i).toArray(new Double[ds.get(0).size()]));
        }
        return ret;
    }
    public static double[] D2d(Double... ds) {
        double[] ret = new double[ds.length];
        for (int i=0;i<ds.length;i++) {
            ret[i] = ds[i];

        }
        return ret;
    }
    public static void printComparison(double[][] input,double[][] output, double[][] expected) {
        for (int i = 0; i < input.length;i++) {
            //System.out.println(input.length+" " + output.length + " "+ expected.length);
            if (input != null) System.out.print("input: "+processDoubleArray(d2D(input[i])));
            if (output != null) System.out.print(" output: "+processDoubleArray(d2D(output[i])));
            if (expected != null)System.out.println(" expected: "+processDoubleArray(d2D(expected[i])));
            else System.out.println();
        }
    }
    public static void printList(List<Double> list) {
        System.out.println(processDoubleArray(list.toArray(new Double[list.size()])));
    }
    public static <T extends Number> String processDoubleArray(T[] thisArray) {
        String s ="";
        for (T element : thisArray) {
            s +=element+" ";
        }
        return s;
    }
}
