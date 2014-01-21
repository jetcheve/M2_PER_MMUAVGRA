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
 * @file MovingNode.java
 * @author atessie, fcastagn, hpaziews, jetcheve & mtesta
 * @version 1.0
 * @date january 2014
 */ 
package per_waypoint;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import jbotsim.Clock;
import jbotsim.Message;
import jbotsim.Node;
import jbotsim.event.ClockListener;
import jbotsim.event.MessageListener;

/**
 * @class MovingNode
 * @brief A node that represent an UAV
 * @details 
 */
public class MovingNode extends Node implements ClockListener, MessageListener{
	private static long _start;    /**< used to calculate the time of the experiment*/
	private static boolean _display_trajectory = false;	
	private static int _dimension = 480;  /**< dimension of the side of the frame */
	private static double _totalscanpossible = _dimension * _dimension;  /**< number of the total point that can be scanned */
	private Point2D _destination = new Point(0,0);
	private static int _time;
	private boolean _first_launch = true;


	/**
	 * @brief Constructor of an UAV
	 * @param None
	 * @return an instance of Moving Node
	 */
	public MovingNode(int i){
		setProperty("icon", "/avion.png");
		setProperty("size", 20);
		setProperty("visited", false);
		setProperty("name",i);
		setState("UAV");
		if(Main.usingCandC)
			setCommunicationRange(133);
		else
			setCommunicationRange(-1);
		Clock.addClockListener(this, 1);
		addMessageListener(this);
		int x = (int) (Math.random()*_dimension+1);
		int y = (int) (Math.random()*_dimension+1);
		_destination.setLocation(x, y);
		setDirection(_destination);
		new HashMap<Integer, ArrayList<Integer>>();
		_start = System.currentTimeMillis();
		_time = 0;

	}


	/**
	 * @brief initialize the pheromone map of each UAV,
	 * scan the area at the position of the UAV,
	 * calcule the new direction and move to it.
	 * @param None
	 * @return None
	 */
	public void onClock(){
		//*************** INITIALIZATION OF THE PHEROMONE'S MAP *****************
		if(_first_launch)
		{
			int[][] ScanMap = new int[_dimension][_dimension];
			for(int i=0;i<_dimension;i++){                        
				for(int j=0;j<_dimension;j++) {
					ScanMap[i][j] =0;
				}
			}
			setProperty("map", ScanMap);
			_first_launch = false;
		}
		//*********************************************************************
		if((int)getLocation().getX() >= (int)_destination.getX()-1 && (int) getLocation().getX() <=(int)_destination.getX()+1
				&& (int)getLocation().getY() >= (int)_destination.getY()-1 && (int) getLocation().getY() <=(int)_destination.getY()+1)
		{
			System.out.println("Destination reached, changing target");
			int x = (int) (Math.random()*_dimension+1);
			int y = (int) (Math.random()*_dimension+1);
			_destination.setLocation(x,y);
		}
		setDirection(_destination);
		Point2D pos = getLocation();
		move(1);
		wrapLocation();
		_time++;
		if(_time > 50)
		{
			_time=0;
			if(isOnCommunicationWithCandC()){
				setProperty("map", neighborsRoute((Node)this));
				for(Node n : getTopology().getNodes())
					if(n.getState().toString().compareTo("C&C") != 0)
						n.setProperty("visited", false);
				send(null,getProperty("map"));  /* broadcast the pheromone map of the UAV */
			}
		}
		scan((int)pos.getX(), (int)pos.getY());
		if(!Main.usingCandC){
			displayScanPercentage();
		}
	}

	/**
	 * @brief analyse if there are a path between the UAV and C&C
	 * @param None
	 * @return true if there are a path,else false
	 */
	private boolean isOnCommunicationWithCandC() {
		java.util.List<Node> neighbors = this.getNeighbors();
		for(Node n : neighbors)
			if(n.getState().toString().compareTo("C&C") == 0)
				return true;
		return false;
	}
	
	/**
	 * @brief look over all its neighbors and update its map with their map
	 * @param Node actual
	 * @return the map update by its neighbors
	 */
	private int[][] neighborsRoute(Node node) {
		int[][] map = new int [_dimension][_dimension];
		if(!(boolean) node.getProperty("visited")){
			node.setProperty("visited",true);
			map = (int[][]) node.getProperty("map");
			for(Node n : node.getNeighbors()){
				map = update(map, neighborsRoute(n));
			}
		}
		return map;

	}
	
	/**
	 * @brief update both map
	 * @param both map
	 * @return the update
	 */
	private int[][] update(int[][] map, int[][] neighborsRoute) {
		for(int i = 0; i < _dimension; i++){
			for(int j=0;j<_dimension;j++){
				if(neighborsRoute[i][j] > 0)
					map[i][j] = 1;
			}	
		}
		return map;
	}

	/**
	 * @brief scan the map at the point x,y
	 * @param the position of the UAV
	 * @return None
	 */
	public void scan(int x, int y){
		if(!Main.usingCandC){
			if(Main._map[x][y]==0)
			{
				Main._map[x][y]=1;
				Main._totalscan++;
				if(_display_trajectory)
					Main._jtopo.addPoint(x, y);
			}
		}
		int [][] tmp = (int[][]) getProperty("map");
		if(tmp[x][y] == 0)
			tmp[x][y] = 1;
		setProperty("map", tmp);
	}

	/**
	 * @brief display the percentage of scanned area and the time of the experiment
	 * @param None
	 * @return None
	 */
	public void displayScanPercentage()
	{
		long s = (System.currentTimeMillis()-_start)/1000;
		long min = 0;
		if(s > 60){
			min = (s-(s%60))/60;
			s = s%60;
		}

		System.out.println("Scan : "+ (Main._totalscan/_totalscanpossible*100) + "% during " + min + " min " + s + " sec");
	}

	/**
	 * @brief receive the message and update his data with this message
	 * @param the message sent by an UAV
	 * @return None
	 */
	@Override
	public void onMessage(Message msg) {
	}


}
