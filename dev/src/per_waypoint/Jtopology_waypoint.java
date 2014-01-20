/**
 * M2_PER_MMUAVGRA  Copyright (C) 2013
 * Development of Mobility Models for UAV Group Reconnaissance Applications
 * (RandomWalk, RandomWaypoint & Pheromone) on JBotSim.
 * By CASTAGNET Florian, ETCHEVERRY Jérémy, PAZIEWSKI Hayley, 
 * TESSIER Alexis & TESTA Mickaël.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program (The other file named LICENCE).
 * If not, see {http://www.gnu.org/licenses/}.
 */
 
 /**
 * @file Jtopology_waypoint.java
 * @author atessie, fcastagn, hpaziews, jetcheve & mtesta
 * @version 1.0
 * @date january 2014
 */ 
package per_waypoint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import jbotsim.Topology;
import jbotsim.ui.JTopology;

/**
 * @class Jtopology_waypoint
 * @brief Description
 * @details 
 */
@SuppressWarnings("serial")
public class Jtopology_waypoint extends JTopology{

	private ArrayList<Point> _list;  /**< list of scanned area */

	/**
         * @brief Constructor
         * @param a topology
         * @return an instance of Jtopology
         */
	public Jtopology_waypoint(Topology topo) {

		super(topo);
		_list = new ArrayList<>();
	}

	/**
         * @brief draw green point on the frame to show the pheromone
         * @param a graphic
         * @return None
         */
	public void paint(Graphics g){
		super.paint(g);
		for(Point s : _list)
		{
			g.drawOval(s.x, s.y, 1, 1);
			g.setColor(new Color(0, 250, 0));
		}
	}
	
	 /**
         * @brief add the position scanned by an UAV to the list of scanned area
         * @param a point
         * @return None
         */
	public void addPoint(int x,int y)
	{
		_list.add(new Point(x, y));
	}
}
