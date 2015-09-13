package Neural_Network_Recursive;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

/**
 * Created by Jayden on 7/31/2015.
 */

//if you refactor name, must do it manually
public class Neural_Node {

    //must be refactored on name change
    public boolean isSubClass() {
        return !this.getClass().toString().split("\\.")[1].equals("Neural_Node");
    }

    private double bios;
    protected List<Neural_Link> before;
    protected List<Neural_Link> after;

    //ALl initialisation
    public void setup(int size,Neural_Link... links) {

        List<Neural_Link> allLinks = new ArrayList<Neural_Link>(links.length);
        Integer middle = allLinks.indexOf(null);
        if (links != null && links.length != 0 && middle != -1) {
            before = allLinks.subList(0, middle - 1);
            after = allLinks.subList(middle + 1, allLinks.size() - 1);
        }else{
            before = new ArrayList<Neural_Link>();
            after = new ArrayList<Neural_Link>();
        }

    }
    //MUST BE DECLARED FOR SUB CLASSES
    public Neural_Node() throws Exception{
        //only allow for classes which inherit from here
        if (!isSubClass()) {
            throw new Exception("Initialize Neural Node first " + this.getClass().toGenericString());
        }
    }
    //FOR DEFAULT BIOS 1
    public Neural_Node(Neural_Link... links) {
        this.bios = 1;
        setup(-1, links);
    }
    //FOR SETTING OF BIOS
    public Neural_Node(double bios,Neural_Link... links) {
        this.bios = bios;
        setup(-1, links);
    }
    //FOR SETTING OF BIOS + NO DYNAMIC ARRAY ALLOCATION
    public Neural_Node(double bios,boolean isFast,Neural_Link... links) {
        this.bios = bios;
        setup(isFast ? links.length : -1, links);
    }

    //output functions
    public double sigmoid(double x) {
        return 1/(1+ exp(x));
    }
    public double getOutput() throws Exception {
        if ( before.size() == 0 ) {
            throw new Exception("Neural link expected");
        }
        double sumOfInput = 0;
        for (Neural_Link link : before) {
            sumOfInput += link.getOutputOfPreviousNode();
        }
        return sigmoid(sumOfInput + bios);
    }

    public void adjustBios(double change) {
        bios += change;
    }
    public double getBios() {
        return bios;
    }

}
