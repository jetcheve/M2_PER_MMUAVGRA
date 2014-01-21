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
 * @file Main.java
 * @author atessie, fcastagn, hpaziews, jetcheve & mtesta
 * @version 1.0
 * @date january 2014
 */ 
package per_waypoint;

import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.ui.JViewer;

/**
 * @class Main
 * @brief Description
 * @details 
 */
public class Main {

	private static int _dimension = 480;   /**< Dimension of the topology */
	public static double _totalscan = 0;   /**< The percentage of the total scan */
	public static Jtopology_waypoint _jtopo;  /**< An instance of our topology */
	public static int[][] _map = new int[_dimension][_dimension];  /**< Matrix of scan */
	public static int _UAV_number = 10; /**< Number of UAV */
	public static boolean usingCandC = true;   /**< boolean that permit to use C&C with the UAV or not */
	public static CandC nodeCandC;


	/**
	 * @brief execute the main program
	 * @param None
	 * @return None
	 */
	public static void main(String[] args)
	{
		Topology topo = new Topology();
		topo.setDimensions(_dimension, _dimension);
		Node.setModel("default", new MovingNode(42));
		_jtopo = new Jtopology_waypoint(topo);
		JViewer jv = new JViewer(_jtopo);

		if(usingCandC){
			nodeCandC = new CandC();
			topo.addNode(_dimension / 2 - 10, _dimension - 50, nodeCandC);
		}
		for(int i= 0;i<_UAV_number;i++)
			topo.addNode(_dimension/2-10, _dimension-50,new MovingNode(i));
	}
}
