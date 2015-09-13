package Neural_Network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static Neural_Network.NeuralUtils.*;
/**
 * Created by Jayden on 7/31/2015.
 */
public class NeuralNetwork {
    private List<NeuralLayer> layers;
    private List<List<Double>> inputSet;
    private List<List<Double>> expectedSet;
    private List<List<Double>> outputSet;
    private int numOfLayers;
    private int iterations;
    private int reviews;
    private boolean adaptive;

    private void setupConstants() {
        numOfLayers = NeuralConstants.LAYERS;
        iterations = NeuralConstants.ITERATIONS;
        reviews = NeuralConstants.REVIEWS;
        adaptive = NeuralConstants.ADAPTIVE_OUTPUT;
    }
    private void setupLayers(int numOfLayers) {
        layers = new ArrayList<NeuralLayer>(numOfLayers);
        for (int i = 0 ; i < numOfLayers ; i++) {
            if (i == numOfLayers - 1) {
                layers.add(new NeuralLayer(expectedSet.get(0).size()));
            }else {
                layers.add(new NeuralLayer());
            }
        }
    }
    public NeuralNetwork(List<List<Double>> inputSet,List<List<Double>> expectedSet) {
        this.inputSet = inputSet;
        this.expectedSet = expectedSet;
        setupConstants();
        setupLayers(numOfLayers);
    }
    public List<Double> runNetwork(List<Double> inputs) {
        if (inputs == null) return null;
        if (layers == null) return null;
        int start = 0;
        List<Double> storedOutput = null;
        for (NeuralLayer layer : layers) {
            if (start == 0) {
                layer.setInput(inputs);
                storedOutput = layer.getOutputs();
                start +=1;

            }else{
                layer.setInput(storedOutput);
                storedOutput = layer.getOutputs();
            }
        }
        return storedOutput;
    }
    //does one repetition
    public void trainNetwork(List<Double> inputs, List<Double> expected) {
        //compute rate for each output node
        layers.get(layers.size()-1).calcRateChangeTarget(expected);

        //compute rate for each hidden node
        for (int i = 0; i < layers.size()-1;i++) {
            layers.get(i).calcRateChange(layers.get(i+1).getNodes());
        }

        //update weights and biases
        for (int i = 1; i < layers.size();i++) {
            layers.get(i).updateWeightBios(layers.get(i-1).getNodes());
        }
        layers.get(0).updateWeightBios(inputs);
    }

    public void trainNetworkOnAllSets() {
        if (outputSet == null) {
            outputSet = new ArrayList<List<Double>>(inputSet.size());
        }
        if (outputSet.size() == 0) {
            for (int i=0;i<expectedSet.size();i++) {
                outputSet.add(i, new ArrayList<Double>(expectedSet.get(0).size()));
            }
        }

        int setLength = inputSet.size();
        for (int review = 0;review<reviews;review++) {
            for (int inputs = 0; inputs < setLength; inputs++) {
                for (int iteration = 0; iteration < (review==1?iterations*0.1:iterations); iteration++) {
                    outputSet.set(inputs,runNetwork(inputSet.get(inputs)));
                    trainNetwork(inputSet.get(inputs), expectedSet.get(inputs));
                }
            }
        }
    }

    public List<List<Double>> getOutput() {
        return outputSet;
    }

}
