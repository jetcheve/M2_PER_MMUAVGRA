package random_mobility;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import jbotsim.Topology;
import jbotsim.ui.JTopology;

@SuppressWarnings("serial")
public class Jtopology_waypoint extends JTopology{

	private ArrayList<Point> list;

	public Jtopology_waypoint(Topology topo) {

		super(topo);
		list = new ArrayList<>();
	}

	public void paint(Graphics g){
		super.paint(g);
		for(Point s : list)
		{
			g.drawOval(s.x, s.y, 1, 1);
			g.setColor(new Color(0, 250, 0));
		}

	}
	
	/*public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for(Point s : list)
		{
			g.drawOval(s.x, s.y, 1, 1);
			g.setColor(new Color(0, 250, 0));
		}
		super.repaint();
	}*/

	public void addPoint(int x,int y)
	{
		list.add(new Point(x, y));
	}
}
