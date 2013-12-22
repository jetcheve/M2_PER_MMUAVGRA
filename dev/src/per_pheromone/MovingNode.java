/**
 * @file MovingNode.java
 * @author atessie, fcastagn, hpaziews, jetcheve & mtesta
 * @version 1.0
 * @date 
 */
package per_pheromone;

import java.awt.Point;
import java.awt.geom.Point2D;

import jbotsim.Clock;
import jbotsim.Message;
import jbotsim.Node;
import jbotsim.event.ClockListener;
import jbotsim.event.MessageListener;

/**
 * @class MovingNode
 * @brief Description
 * @details 
 */
public class MovingNode extends Node implements ClockListener, MessageListener {

	private static double _steering_angle;									/**< The direction angle of an UAV */
	private static int _time;												/**< Description */
	private static long _start;												/**< Description */
	private static int _total_pheromone = 0;								/**< Description */
	private static int _dimension = 500;									/**< Description */
	private boolean _first_launch = true;									/**< Description */
	private static int _margin = 5;											/**< Description */
	private static double _total_potential_scan = _dimension*_dimension;	/**< Description */

	/**
	 * @brief Constructor ...
	 * @param 
	 * @return
	 */
	public MovingNode() {
		setProperty("icon", "/avion.png");
		setProperty("size", 20);
		setCommunicationRange(50);
		Clock.addClockListener(this, 5);
		addMessageListener(this);
		_steering_angle = 3 * Math.PI / 2;  //direction : upward
		setDirection(_steering_angle);
		_time = 0;
		set_start(System.currentTimeMillis());
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	@Override
	public void onClock() {
		if(_first_launch) {
			int[][] pheromoneMap = new int[_dimension][_dimension];
			for(int i = 0; i < _dimension; i++)			
				for(int j = 0; j < _dimension; j++)
					pheromoneMap[i][j] = 0;
			setProperty("map", pheromoneMap);
			_first_launch = false;
		}

		Point2D pos = getLocation();
		int x = (int)pos.getX();
		int y = (int)pos.getY();
		int [][] tmp = (int[][]) getProperty("map");
		tmp[x/2][y/2] += 1;

		if(tmp[x/2][y/2] == 1) {
			Main._totalscan++;
			Main._jtopo.addPoint(x, y);
		}
		_total_pheromone++;
		setProperty("map", tmp);
		Double dir = getDirection();
		dir = analysis(dir); //return the new direction
		if(dir != null)
			setDirection(dir);
		avoidEdges(x,y);
		move(1);
		wrapLocation();
		_time++;
		if(_time > 50) {
			_time = 0;
			send(null,getProperty("map"));
		}
		displayScanPercentage();
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	private Double analysis(double dir) {
		Point2D pos = getLocation();
		int x = (int)pos.getX();
		int y = (int)pos.getY();

		if(dir == 3*Math.PI/2) { //upward
			int min = minimum(new Point(x-1,y),new Point(x,y-1),new Point(x+1,y));
			return min_to_dir(0,min);
		}
		else if(dir == Math.PI/2) { //downward
			int min = minimum(new Point(x+1,y),new Point(x,y+1),new Point(x-1,y)); 
			return min_to_dir(1,min);
		}
		else if(dir == Math.PI) { //leftward
			int min = minimum(new Point(x,y+1),new Point(x-1,y),new Point(x,y-1)); 
			return min_to_dir(2,min);
		}
		else if(dir == 2*Math.PI) { //rightward
			int min = minimum(new Point(x,y-1),new Point(x+1,y),new Point(x,y+1)); 
			return min_to_dir(3,min);
		}
		return null;
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	private Double min_to_dir(int i, int min) {
		switch(i) {
		case 0:
			if(min == 1)
				return Math.PI;
			else if(min == 3)
				return 2 * Math.PI;
			break;
		case 1:
			if(min == 1)
				return 2 * Math.PI;
			else if(min ==3)
				return Math.PI;
			break;
		case 2:
			if(min == 1)
				return Math.PI / 2;
			else if(min == 3)
				return 3 * Math.PI / 2;
			break;
		case 3:
			if(min == 1)
				return 3 * Math.PI / 2;
			else if(min == 3)
				return Math.PI / 2;
			break;
		default:
			break;
		}
		return null;
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	private int minimum(Point point, Point point2, Point point3) { //1: left point; 2: straight point; 3: right point
		int left = 0;
		int right = 0;
		int ahead = 0;
		int[][]	pheromonMap = (int[][]) getProperty("map");

		if(isInMatrix(point))
			left = pheromonMap[point.x][point.y];
		if(isInMatrix(point2))
			ahead =  pheromonMap[point2.x][point2.y];
		if(isInMatrix(point3))
			right =  pheromonMap[point3.x][point3.y];

		int turnleft = (_total_pheromone - left) / (2*_total_pheromone);
		int turnright = (_total_pheromone - right) / (2*_total_pheromone);
		int straightahead = (_total_pheromone - ahead) / (2*_total_pheromone);

		if(turnleft == turnright && turnright == straightahead) {
			int alea = (int) (Math.random() * 3);
			if(alea == 0)
				return 1;
			else if (alea == 1)
				return 2;
			else
				return 3;
		}
		else {
			if(turnleft == turnright && turnleft < straightahead) {
				int alea = (int) (Math.random() * 2);
				if(alea == 0)
					return 1;
				else 
					return 3;
			}
			if(turnleft == straightahead && turnleft < turnright) {
				int alea = (int) (Math.random() * 2);
				if(alea == 0)
					return 1;
				else 
					return 2;
			}
			if(turnright == straightahead && turnright < turnleft) {
				int alea = (int) (Math.random() * 2);
				if(alea == 0)
					return 2;
				else 
					return 3;
			}
			if(turnleft < turnright && turnleft < straightahead)
				return 1;
			if(straightahead < turnright && straightahead < turnleft)
				return 2;
			if(turnright < turnleft && turnright < straightahead)
				return 3;
			return 0;
		}	
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	private boolean isInMatrix(Point point) {
		return point.x <=0 && point.x >=_dimension && point.y <=0 && point.y >=_dimension;
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	public void avoidEdges(int x, int y) {
		if(x - _margin < 0) { //touch the left edge
			_steering_angle = 2 * Math.PI;
			setLocation(x + _margin, y);
			x += _margin;
			setDirection(_steering_angle);
		}
		if(y - _margin < 0) { //touch the up edge
			_steering_angle = Math.PI / 2;
			setLocation(x, y + _margin);
			y += _margin;
			setDirection(_steering_angle);
		}
		if(x + _margin > _dimension) { //touch the right edge
			_steering_angle = Math.PI;
			setLocation(x - _margin, y);
			x -= _margin;
			setDirection(_steering_angle);
		}
		if(y + _margin > _dimension) { //touch the down edge
			_steering_angle = 3 * Math.PI / 2;
			setLocation(x, y - _margin);
			y -= _margin;
			setDirection(_steering_angle);
		}
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	@Override
	public void onMessage(Message msg) {
		update((int[][])msg.content);
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	private void update(int[][] content) {
		int tmp[][] = (int[][]) getProperty("map");
		for(int i = 0; i < _dimension; i++) {			
			for(int j = 0; j < _dimension; j++) {			
				if(tmp[i][j] < content[i][j])
					tmp[i][j] = content[i][j];
			}
		}
		setProperty("map", tmp);			
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	public void displayScanPercentage()	{
		System.out.println("Scan : " + (Main._totalscan / _total_potential_scan*100) + "%");
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	public static long get_start() {
		return _start;
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	public static void set_start(long _start) {
		MovingNode._start = _start;
	}
}
