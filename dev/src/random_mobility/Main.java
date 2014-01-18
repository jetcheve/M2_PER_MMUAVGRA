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
 *
 * @file Main.java
 * @author atessie, fcastagn, hpaziews, jetcheve & mtesta
 * @version 1.0
 * @date 
 */
package random_mobility;

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
