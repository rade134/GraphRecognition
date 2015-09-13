package Neural_Network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.exp;

/**
 * Created by Jayden on 7/31/2015.
 */
public class NeuralNode extends Number{

    private List<Double> weights;
    private double weight;
    private double bios;
    private double output = 0;
    private double rateOfChange = 0;
    private double learning;

    //debug settings
    public static boolean debug_outputToTarget = false;
    public static boolean debug_rate = false;
    public static boolean debug_weight = false;
    public static boolean debug_bios = false;

    private void setupConstants() {
        weight = NeuralConstants.WEIGHTS;
        bios = NeuralConstants.BIOS;
        learning = NeuralConstants.LEARNING_RATE;
        if (NeuralConstants.DEBUGGING) {
            debug_outputToTarget= true;
        }
    }
    private void setupWeights(Double... weights) {
        this.weights = Arrays.asList(weights);
    }

    public NeuralNode() {
        setupConstants();
        weights = new ArrayList<Double>();
    }
    public NeuralNode(int numWeights) {
        setupConstants();
        weights = new ArrayList<Double>();
        for (int i=0;i<numWeights;i++) weights.add(weight);
    }
    public NeuralNode(Double... weights){
        setupConstants();
        setupWeights(weights);
    }
    public NeuralNode(double bios,Double... weights) {
        setupConstants();
        this.weights = Arrays.asList(weights);
        this.bios = bios;
        setupWeights(weights);
    }
    public NeuralNode(double bios, double learning, Double... weights) {
        setupConstants();
        this.bios = bios;
        this.learning = learning;
        setupWeights(weights);
    }

    private double getWeight(int i) {
        return weights.get(i);
    }
    private double getBios() {
        return bios;
    }

    public double getOutput() {
        return output;
    }
    public double getRateOfChange() {
        return rateOfChange;
    }

    private double sigmoid(double x) {
        return 1/(1+ exp(-1*x));
    }
    public void calcRateChange(double target) {
        rateOfChange = output * (1-output) * (output-target);
        if (debug_outputToTarget) System.out.println("output : "+output + " target: " +target);

        if (debug_rate) System.out.println("rate: "+ rateOfChange);
    }
    //
    public void calcRateChange(int whichWeight, List<NeuralNode> latterNodes) {
        // sum of (Sk * Wjk) of previous
        double errorSum = 0;
        for (NeuralNode node : latterNodes) {
            errorSum += node.getRateOfChange()*node.getWeight(whichWeight);
        }
        rateOfChange = output * (1-output) * errorSum;

        if (debug_rate) System.out.println("rate: "+ rateOfChange);
    }

    public <T extends Number> void updateWeightBios(List<T> prevNodes) {
        if (weights.size() == 0) {
            System.out.println("This node is not connected ( has no weights )");
            return;
        }

        for (int i=0;i<prevNodes.size();i++) {
            if (debug_weight) System.out.print("weight "+i+": "+ weights.get(i));
            weights.set(i, weights.get(i) - 1 * learning * rateOfChange * prevNodes.get(i).doubleValue());
            //weights.set(i,weights.get(i) -1 * learning * rateOfChange * output);

            if (debug_weight) System.out.println(", "+ weights.get(i));

        }
        bios -= learning * rateOfChange;
        if (debug_bios) System.out.println("bios: "+bios);

    }
    public <T extends Number> void setInput(List<T> input) {
        double sum = 0;
        if (weights.size() == 0) {
            for (int i=0;i<input.size();i++) {
                weights.add(weight);
            }
        }
        for (int i=0; i<input.size();i++) {
            //System.out.println(weights.size() +" " + input.size());
            for (int j=0;weights.size()<input.size();i++) {
                weights.add(weight);
            }
            sum += input.get(i).doubleValue() * weights.get(i);
        }
        output = sigmoid(sum + bios);
    }
    @Override
    public double doubleValue() {
        return getOutput();
    }
    @Override
    public int intValue() {
        return ((int) getOutput());
    }
    @Override
    public float floatValue() {
        return ((float) getOutput());
    }
    @Override
    public long longValue() {
        return ((long) getOutput());
    }
}
