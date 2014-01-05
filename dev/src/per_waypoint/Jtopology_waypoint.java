import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import jbotsim.Topology;
import jbotsim.ui.JTopology;

@SuppressWarnings("serial")
public class Jtopology_waypoint extends JTopology{

	private ArrayList<Point> _list;

	public Jtopology_waypoint(Topology topo) {

		super(topo);
		_list = new ArrayList<>();
	}

	public void paint(Graphics g){
		super.paint(g);
		for(Point s : _list)
		{
			g.drawOval(s.x, s.y, 1, 1);
			g.setColor(new Color(0, 250, 0));
		}
	}
	

	public void addPoint(int x,int y)
	{
		_list.add(new Point(x, y));
	}
}
