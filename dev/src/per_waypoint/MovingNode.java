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
 * @date 
 */ 
package per_waypoint;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import jbotsim.Clock;
import jbotsim.Node;
import jbotsim.event.ClockListener;

public class MovingNode extends Node implements ClockListener{
	private static long _start;
	private static boolean display_trajectory = false;	
	private static int _dimension = 480;
	private static double _totalscanpossible = _dimension * _dimension;
	private Point2D _destination = new Point(0,0);
	
	
	public MovingNode(){
		setProperty("icon", "/avion.png");
		setProperty("size", 20);
		setCommunicationRange(-1);
		Clock.addClockListener(this, 1);
		int x = (int) (Math.random()*_dimension+1);
		int y = (int) (Math.random()*_dimension+1);
		_destination.setLocation(x, y);
		setDirection(_destination);
		new HashMap<Integer, ArrayList<Integer>>();
		_start = System.currentTimeMillis();
	}

	public void onClock(){
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
		scan((int)pos.getX(), (int)pos.getY());
		displayScanPercentage();
	}

	public void scan(int x, int y){
		if(Main._map[x][y]==0)
		{
			Main._map[x][y]=1;
			Main._totalscan++;
			if(display_trajectory)
				Main._jtopo.addPoint(x, y);
		}
	}

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
}
