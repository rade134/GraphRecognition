package Neural_Network;

import static Neural_Network.NeuralUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jayden on 7/31/2015.
 */
public class NeuralLayer {
    protected List<NeuralNode> nodes;
    protected List<Double> outputs;
    private int numNodesDefault = 5;

    protected void setupConstants() {
        numNodesDefault = NeuralConstants.NODES_PER_LAYER;
    }
    public NeuralLayer(int size) {
        setupConstants();
        System.out.println("numNodes' " + size);

        this.nodes = new ArrayList<NeuralNode>();
        for (int i = 0 ; i < size ; i++) {
            this.nodes.add(new NeuralNode());
        }
    }
    public NeuralLayer() {
        setupConstants();
        this.nodes = new ArrayList<NeuralNode>();
        for (int i = 0 ; i < numNodesDefault ; i++) {
            this.nodes.add(new NeuralNode());
        }
    }
    public NeuralLayer(NeuralNode... nodes) {
        setupConstants();
        this.nodes = Arrays.asList(nodes);
    }
    public NeuralLayer(List<NeuralNode> nodes) {
        setupConstants();
        this.nodes = nodes;
    }
    public List<NeuralNode> getNodes() {
        return nodes;
    }
    public List<Double> getOutputs() {
        List<Double> newList = new ArrayList<Double>(outputs.size());
        newList = Arrays.asList(outputs.toArray(new Double[outputs.size()]).clone());
        //Collections.copy(outputs,newList);
        return newList;
    }
    protected void setOutputs(List<Double> outputs) {
        this.outputs = outputs;
    }
    protected void setOutput(int index,double output) {
        this.outputs.set(index,output);
    }
    public <T extends Number> void setInput(List<T> input) {
        if (outputs == null) {
            outputs = new ArrayList<Double>();
        }
        int size = outputs.size();
        for (int i =0;i<nodes.size();i++) {
            nodes.get(i).setInput(input);
            if (size == 0) {
                outputs.add(i, nodes.get(i).getOutput());
            }else{
                outputs.set(i,nodes.get(i).getOutput());
            }
            //System.out.println("output: " + nodes.get(i).getOutput());
        }
        //System.out.println(processDoubleArray((outputs.toArray(new Double[outputs.size()]))));
        //System.out.println("outputs size " + nodes.size());
    }
    //calcRateChangeTarget
    public void calcRateChange(List<NeuralNode> latterNodes) {
        for (int i = 0; i < nodes.size();i++) {
            nodes.get(i).calcRateChange(i,latterNodes);
        }
    }
    public void calcRateChangeTarget(List<Double> target) {
        for (int i = 0; i < target.size();i++) {
            nodes.get(i).calcRateChange(target.get(i));
        }
    }

    //updateWeightBios
    public <T extends Number> void updateWeightBios(List<T> prevNodes) {
        for (NeuralNode node : nodes) {
            node.updateWeightBios(prevNodes);
        }
    }

    protected int getNumNodesDefault() {
        return numNodesDefault;
    }
}
