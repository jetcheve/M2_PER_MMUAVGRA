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
 * @file CandC.java
 * @author atessie, fcastagn, hpaziews, jetcheve & mtesta
 * @version 1.0
 * @date january 2014
 */ 

import jbotsim.Clock;
import jbotsim.Message;
import jbotsim.Node;
import jbotsim.event.ClockListener;
import jbotsim.event.MessageListener;

/**
 * @class CandC
 * @brief A node that represent a command and control center (C&C)
 * @details 
 */
public class CandC extends Node implements ClockListener, MessageListener{
	private boolean _display_pheromone = true; 

	private static long _start;           /**< used to calculate the time of the experiment*/
	private boolean _first_launch = true;
	private static int _dimension = 500;  /**< dimension of the side of the frame */
	private static int _margin = 5;
	private static double _total_potential_scan = ((_dimension - (2*_margin)) * (_dimension- (2*_margin)));  /**< number of the total point that can be scanned */

	/**
	 * @brief Constructor of the C&C
	 * @param None
	 * @return an instance of C&C
	 */
	public CandC() {
		setProperty("icon", "/tower.png");
		setState("C&C");
		setProperty("size", 20);
		setCommunicationRange(133);
		addMessageListener(this);
		Clock.addClockListener(this, 1);
		_start = System.currentTimeMillis();
	}

	/**
	 * @brief initialize the pheromone's map of the C&C 
	 *	and display the scan percentage every top of clock
	 * @param None
	 * @return None
	 */
	@Override
	public void onClock() {
		//*************** INITIALIZATION OF THE PHEROMONE'S MAP *****************
		if(_first_launch)
		{
			for(int i=0;i<_dimension;i++){                        
				for(int j=0;j<_dimension;j++) {
					Main._map_scan[i][j] = 0;
				}
			}
			_first_launch = false;
		}
		//***********************************************************************
		displayScanPercentage();
	}

	/**
	 * @brief receive the message and update his data with this message
	 * @param the message sent by an UAV
	 * @return None
	 */
	@Override
	public void onMessage(Message msg) {
		update((int[][])msg.content);
	}

	/**
	 * @brief update the pheromone map of the C&C with the pheromone map from an UAV
	 * @param pheromone map of an UAV
	 * @return None
	 */
	private void update(int[][] content){
		Main._totalscan = 0;
		Main._jtopo.clearListe();
		for(int i=0;i<_dimension;i++){                        
			for(int j=0;j<_dimension;j++){                  
				if(Main._map_scan[i][j] < content[i][j])
					Main._map_scan[i][j] = content[i][j];
				if(Main._map_scan[i][j] > 0){
					if(_display_pheromone){
						Main._jtopo.addPoint(i, j);
					}
					Main._totalscan++;
				}
			}
		}
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
		
		System.out.println("Scan : "+ (Main._totalscan/_total_potential_scan*100) + "% during " + min + " min " + s + " sec");
	}

}
