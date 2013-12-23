package random_mobility;

import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.ui.JViewer;

public class Main {

	private static int dimens = 500;
	public static double totalscan = 0;
	public static Jtopology_waypoint jtopo;
	public static int[][] map = new int[dimens][dimens];
	public static void main(String[] args)
	{
		Topology topo = new Topology();
		topo.setDimensions(dimens, dimens);
		Node.setModel("default", new MovingNode());
		jtopo = new Jtopology_waypoint(topo);
		JViewer jv = new JViewer(jtopo);
		for(int i= 0;i<10;i++)
			topo.addNode(dimens/2-10, dimens-50);
	}
}
