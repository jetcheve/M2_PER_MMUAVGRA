package random_mobility;
import jbotsim.ui.JViewer;
import jbotsim.Topology;
import jbotsim.Node;

public class Main{

	private static int dimension = 500;
	public  static int[][] map = new int[500][500];
	public static double totalscan = 0;

	public static void main(String[] args)
	{
		Topology topo = new Topology();
		topo.setDimensions(dimension, dimension);
		Node.setModel("default", new MovingNode());
		Node.setModel("scan", new ScanNode());
		JViewer jv = new JViewer(topo);
		for(int i= 0;i<10;i++)
			topo.addNode(dimension/2, dimension);
	}
}