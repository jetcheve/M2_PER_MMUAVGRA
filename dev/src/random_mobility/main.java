package random_mobility;
import jbotsim.ui.JViewer;
import jbotsim.Topology;
import jbotsim.Node;

public class main{
    public static void main(String[] args)
    {
	Node.setModel("default", new MovingNode());
	new JViewer(new Topology());
    }
}