import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.ui.JViewer;

public class Main {

	private static int _dimension = 500;
	public static double _totalscan = 0;
	public static Jtopology_waypoint _jtopo;
	public static int[][] _map = new int[_dimension][_dimension];
	public static int _UAV_number = 10;
	
	public static void main(String[] args)
	{
		Topology topo = new Topology();
		topo.setDimensions(_dimension, _dimension);
		Node.setModel("default", new MovingNode());
		_jtopo = new Jtopology_waypoint(topo);
		JViewer jv = new JViewer(_jtopo);
		for(int i= 0;i<_UAV_number;i++)
			topo.addNode(_dimension/2-10, _dimension-50);
	}
}
