package Neural_Network_Recursive;

import java.util.Arrays;

/**
 * Created by Jayden on 7/31/2015.
 */
public class Neural_InputNode extends Neural_Node {


    private double input;
    public Neural_InputNode(double input,Neural_Link... links) throws Exception{
        this.input = input;
        this.after = Arrays.asList(links);
    }
    public void setInput(double input) {
        this.input = input;
    }
    @Override
    public double getOutput() {
        return input;
    }
}
