package walk;
import jbotsim.ui.JViewer;
import jbotsim.Topology;
import jbotsim.Node;

public class Main{

	private static int _dimension = 500;
	public  static int[][] _map = new int[_dimension][_dimension];
	public static double _totalscan = 0;
	public static JtopologyWalk _jtopo;	/**< An instance of our toopology */
	public static JViewer _jv;					/**< An instance of a JViewer */
	static long _time;
	public static int _UAV_number = 10;
	public static void main(String[] args)
	{
		Topology topo = new Topology();
		topo.setDimensions(_dimension, _dimension);
		Node.setModel("default", new MovingNode());
		_jtopo = new JtopologyWalk(topo);
		_jv = new JViewer(_jtopo);   
		for(int i= 0;i<_UAV_number;i++)
			topo.addNode(_dimension/2-10, _dimension-50);
		
	}
}