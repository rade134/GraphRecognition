package Neural_Network_Recursive;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jayden on 7/31/2015.
 */
public class Neural_OutputNode extends Neural_Node{
    public Neural_OutputNode() throws Exception {

    }
    public Neural_OutputNode(Neural_Link... links) throws Exception{
        super(links);
        List<Neural_Link> allLinks = Arrays.asList(links);

        if (before.size() != 0 && after.size() != 0) {
            before = allLinks;
        }
    }
    public double calculateRateOfChange(double output,double target) {
        return output*(1-output)*(output-target);
    }
}
