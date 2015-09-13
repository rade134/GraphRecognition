package Neural_Network_Recursive;

/**
 * Created by Jayden on 7/31/2015.
 */
public class Neural_Link {

    private double weight;
    private Neural_Node before;
    private Neural_Node after;

    public Neural_Link(double weight) {
        this.weight = weight;
    }
    public void adjustWeight(double change) {
        weight += change;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    public double getWeight() {
        return weight;
    }
    public double getOutputOfPreviousNode() throws Exception{
        return weight*before.getOutput();
    }
    public void setNodes(Neural_Node before, Neural_Node after) {
        this.before = before;
        this.after = after;
    }
}
