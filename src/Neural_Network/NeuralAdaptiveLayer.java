package Neural_Network;

import java.util.ArrayList;
import java.util.List;

import static Neural_Network.NeuralUtils.*;

/**
 * Created by Jayden on 8/1/2015.
 */
public class NeuralAdaptiveLayer extends NeuralLayer {

    private int subList;

    public NeuralAdaptiveLayer() {
        subList = nodes.size();
    }

    public void setNumNodes(int numNodes) {
        subList=numNodes;
        //creates nodes that don't exist
        for (int i=nodes.size();i<numNodes;i++) {
            nodes.add(new NeuralNode());
        }
    }
    @Override
    public List<Double> getOutputs() {
        return outputs.subList(0,subList);
    }
    @Override
    public void calcRateChange(List<NeuralNode> latterNodes) {
        for (int i = 0; i < subList;i++) {
            nodes.get(i).calcRateChange(i,latterNodes);
        }
    }
    @Override
    public <T extends Number> void updateWeightBios(List<T> prevNodes) {
        for (int i=0;i<subList;i++) {
            nodes.get(i).updateWeightBios(prevNodes);
        }
    }

}
