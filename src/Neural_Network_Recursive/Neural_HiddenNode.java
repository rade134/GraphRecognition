package Neural_Network_Recursive;

/**
 * Created by Jayden on 7/31/2015.
 */
public class Neural_HiddenNode extends Neural_OutputNode {
    public Neural_HiddenNode() throws Exception {

    }
    public Neural_HiddenNode(Neural_Link... links) throws Exception{
        super(links);
    }

    public double calculateRateOfChange(double output) {
        return 0;
    }
}
