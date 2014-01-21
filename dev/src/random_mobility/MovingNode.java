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
 * @file MovingNode.java
 * @author atessie, fcastagn, hpaziews, jetcheve & mtesta
 * @version 1.0
 * @date january 2014
 */
package random_mobility;

import java.awt.List;
import java.awt.geom.Point2D;
import java.util.HashSet;

import javax.rmi.CORBA.Util;

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
	private static boolean _display_trajectory = false;
	private int _lastdirection; //  1 -> left , 2-> straight ahead, 3 -> right
	private static double _angle_towards; /**<direction of an UAV */
	private static double _amplitude_variation_towards = Math.PI/4;
	private static int _dimension = 500;  /**< dimension of the side of the frame */
	private static long _start;	/**< used to calculate the time of the experiment*/
	//private static int _time;
	private static int _margin = 5;
	private static double _totalscanpossible= ((_dimension - (2*_margin)) * (_dimension- (2*_margin)));  /**< number of the total point that can be scanned */
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
		_angle_towards = 3*Math.PI/2;  //direction : upward.
		setDirection(_angle_towards);
		_lastdirection = 2;
		_start = System.currentTimeMillis();
		//_time = 0;

	}

	/**
	 * @brief change the direction of an UAV every second,
	 * scan the area at the position of the UAV.
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
		analysis();  //change the direction according to the last direction chosen
		Point2D _pos = getLocation();
		int _x = (int)_pos.getX();
		int _y = (int)_pos.getY();
		avoidEdges(_x, _y);
		move(1);
		wrapLocation();
		_pos = getLocation();
		_x = (int)_pos.getX();
		_y = (int)_pos.getY();
		/*_time++;
		if(_time > 5)
		{
			_time=0;
		}*/
		setCommunicationRange(133);
		if(Main.usingCandC){
			if(isOnCommunicationWithCandC()){
				setProperty("map", neighborsRoute((Node)this));
				for(Node n : getTopology().getNodes())
					if(n.getState().toString().compareTo("C&C") != 0)
						n.setProperty("visited", false);
				send(Main.nodeCandC,getProperty("map"));  /* broadcast the pheromone map of the UAV */
			}
		}
		if(_x < (_dimension - _margin) && _x >= _margin && _y >= _margin && _y < (_dimension - _margin))
			scan((int)_pos.getX(), (int)_pos.getY());
		if(!Main.usingCandC){
			display_percentage_scan();
		}
	}


	/**
	 * @brief analyse if there are a path between the UAV and C&C
	 * @param None
	 * @return true if there are a path,else false
	 */
	private boolean isOnCommunicationWithCandC() {		
		for(Node n : this.getNeighbors()){
			if(n.getState().toString().compareTo("C&C") == 0)
				return true;
		}
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
	 * @brief analyse the pheromone map and change the current direction of the UAV
	 * @param None
	 * @return None
	 */
	public void analysis(){
		int _alea = (int) (Math.random()*10);
		switch (_lastdirection){

		//last action left:
		case 1:  
			if(_alea <= 2) //30% probability straight ahead.
			{
				_lastdirection = 2;
			}
			else   //70% probability turn left
			{
				_angle_towards -= _amplitude_variation_towards;
				setDirection(_angle_towards);
			}
			break;

			//last action straight ahead :
		case 2:    
			if(_alea == 0) //10% probability turn left.
			{
				_lastdirection = 1;
				_angle_towards -= _amplitude_variation_towards;
				setDirection(_angle_towards);
			}
			else if(_alea <= 8 && _alea !=0) // 80% probability straight ahead.
				;
			else if(_alea == 9) //10% probability turn right.
			{
				_lastdirection = 3;
				_angle_towards += _amplitude_variation_towards;

				setDirection(_angle_towards);
			}
			break;

			//last action right :  
		case 3:  
			if(_alea <= 2) //30% probability straight ahead.
				;
			else  //70% probability turn right.
			{
				_lastdirection = 2;
				_angle_towards += _amplitude_variation_towards;
				setDirection(_angle_towards);
			}
			break;

		default:
			break;
		}
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
	 * @brief the UAV avoid the edges of the frame
	 * @param position of the UAV
	 * @return None
	 */
	public void avoidEdges(int x, int y){
		if((x-_margin < 0))
		{
			_angle_towards+=Math.PI;
			setLocation(x+_margin, y);
			setDirection(_angle_towards);
		}
		if( y-_margin < 0)
		{
			_angle_towards+=Math.PI/2;
			setLocation(x, y+_margin);
			setDirection(_angle_towards);
		}
		if((x+_margin >500))
		{
			_angle_towards+=Math.PI;
			setLocation(x-_margin, y);
			setDirection(_angle_towards);
		}
		if(y+_margin > 500)
		{
			_angle_towards+=3*Math.PI/2;
			setLocation(x, y-_margin);
			setDirection(_angle_towards);
		}
	}

	/**
	 * @brief display the percentage of scanned area and the time of the experiment
	 * @param None
	 * @return None
	 */
	public void display_percentage_scan()
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
	 * @brief None communication between UAV
	 * @param None
	 * @return None
	 */
	@Override
	public void onMessage(Message msg) {
	}

}
