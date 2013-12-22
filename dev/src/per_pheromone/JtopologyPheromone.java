/**
 * @file JtopologyPheromone.java
 * @author atessie, fcastagn, hpaziews, jetcheve & mtesta
 * @version 1.0
 * @date 
 */
package per_pheromone;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import jbotsim.Topology;
import jbotsim.ui.JTopology;

/**
 * @class JtopologyPheromone
 * @brief Description
 * @details 
 */
@SuppressWarnings("serial")
public class JtopologyPheromone extends JTopology {
	private ArrayList<Point> _list;	/**< Description */

	/**
	 * @brief Constructor ...
	 * @param topo
	 * @return
	 */
	public JtopologyPheromone(Topology topo) {
		super(topo);
		_list = new ArrayList<>();
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	public void paint(Graphics g) {
		super.paint(g);
		for(Point s : _list) {
			g.drawOval(s.x, s.y, 1, 1);
			g.setColor(new Color(0, 250, 0));
		}
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	public void addPoint(int x,int y) {
		_list.add(new Point(x, y));
	}
}
