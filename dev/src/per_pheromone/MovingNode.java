/**
 * @file MovingNode.java
 * @author atessie, fcastagn, hpaziews, jetcheve & mtesta
 * @version 1.0
 * @date 
 */
package per_pheromone;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Map;

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
public class MovingNode extends Node implements ClockListener, MessageListener{

	private double _upward = 3*Math.PI/2;
	private double _upward_leftward = 5*Math.PI/4;
	private double _upward_rightward = 7*Math.PI/4;
	private double _downward = Math.PI/2;
	private double _downward_leftward = 3*Math.PI/4;
	private double _downward_rightward = Math.PI/4;
	private double _leftward = Math.PI;
	private double _rightward = 2*Math.PI;

	private static double _steering_angle;
	private static int _time;
	private static long _start;
	private static int _total_pheromone = 0;
	private static int _dimension = 500;
	private boolean _first_launch = true;
	private static int _margin = 5;
	private static double _total_potential_scan = _dimension*_dimension;

	/**
	 * @brief Constructor ...
	 * @param 
	 * @return
	 */
	public MovingNode() {
		setProperty("icon", "/avion.png");
		setProperty("size", 20);
		setCommunicationRange(50);
		Clock.addClockListener(this, 1);
		addMessageListener(this);
		_steering_angle = _upward;  //direction : vers le haut.
		setDirection(_steering_angle);
		_time = 0;
		_start = System.currentTimeMillis();
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	@Override
	public void onClock() {
		//*************** INITIALIZATION OF THE PHEROMONE'S MAP *****************
		if(_first_launch)
		{
			int[][] pheromoneMap = new int[_dimension][_dimension];
			for(int i=0;i<_dimension;i++)			
				for(int j=0;j<_dimension;j++)
					pheromoneMap[i][j] =0;
			setProperty("map", pheromoneMap);
			_first_launch = false;
		}
		//********************** SCAN OF THE AREA ************************************
		// Scan en x,y : the UAV will scan each point around the position and also the position.
		Point2D pos = getLocation();
		int x = (int)pos.getX();
		int y = (int)pos.getY();
		int [][] tmp = (int[][]) getProperty("map");
		tmp[x-1][y]+=1;
		System.out.println(x-1 +"  "+y);
		if(tmp[x-1][y] ==1){
			Main.totalscan++;
			Main.jtopo.addPoint(x-1, y);
		}
		tmp[x+1][y]+=1;
		System.out.println(x+1 +"  "+y);
		if(tmp[x+1][y] ==1){
			Main.totalscan++;
			Main.jtopo.addPoint(x+1, y);
		}
		for(int i = x, j=y-1,area = 3; area > 0; area--,j++){
			System.out.println(i +"  "+j);
			tmp[i][j] += 1;
			if(tmp[i][j] == 1)
			{
				Main.totalscan++;
				Main.jtopo.addPoint(i, j);
			}
		}
		_total_pheromone+=5;
		setProperty("map", tmp);
		//***************************************************************************
		//******************** CALCULATION OF THE NEW DIRECTION AND MOVING *******
		Double dir = getDirection();
		dir = analysis(dir); //retourne la nouvelle direction
		if(dir != null)
			setDirection(dir);
		avoidEdges(x,y);
		move(1);
		wrapLocation();
		_time++;
		if(_time > 50)
		{
			_time=0;
			send(null,getProperty("map"));
		}
		displayScanPercentage();
		//***************************************************************************
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
		if(dir == _upward) //upward
		{
			int min = minimum(new Point(x-1,y-1),new Point(x,y-1),new Point(x+1,y-1));
			return min_to_dir(0,min);
		}
		else if(dir == _downward)  //downward
		{
			int min = minimum(new Point(x+1,y+1),new Point(x,y+1),new Point(x-1,y+1)); 
			return min_to_dir(1,min);
		}
		else if(dir == _leftward) //leftward
		{
			int min = minimum(new Point(x-1,y+1),new Point(x-1,y),new Point(x-1,y-1)); 
			return min_to_dir(2,min);
		}
		else if(dir == _rightward) //rightward
		{
			int min = minimum(new Point(x+1,y-1),new Point(x+1,y),new Point(x+1,y+1)); 
			return min_to_dir(3,min);
		}
		else if(dir == _upward_leftward)  //upward and leftward
		{
			int min = minimum(new Point(x-2,y),new Point(x-1,y-1),new Point(x,y-2)); 
			return min_to_dir(4,min);
		}
		else if(dir == _upward_rightward)  //upward and rightward
		{
			int min = minimum(new Point(x,y-2),new Point(x+1,y-1),new Point(x+2,y)); 
			return min_to_dir(5,min);
		}
		else if(dir == _downward_leftward)  //downward and leftward
		{
			int min = minimum(new Point(x,y+2),new Point(x-1,y+1),new Point(x-2,y)); 
			return min_to_dir(6,min);
		}
		else if(dir == _downward_rightward)  //downward and rightward
		{
			int min = minimum(new Point(x+2,y),new Point(x+1,y+1),new Point(x,y+2)); 
			return min_to_dir(7,min);
		}
		return null;

	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	private Double min_to_dir(int i, int min) { //1 turn left, 
		switch(i){								// 2 don't change the direction, 3 turn right
		case 0: //upward
			if(min == 1)
				return _upward_leftward;
			else if(min ==2)
				return _upward;
			else if(min ==3)
				return _upward_rightward;
			break;
		case 1:  //downward
			if(min == 1)
				return _downward_leftward;
			else if(min ==2)
				return _downward;
			else if(min ==3)
				return _downward_rightward;
			break;
		case 2: //leftward
			if(min == 1)
				return _downward_leftward;
			else if(min ==2)
				return _leftward;
			else if(min ==3)
				return _upward_leftward;
			break;
		case 3: //rightward
			if(min == 1)
				return _upward_rightward;
			else if(min ==2)
				return _rightward;
			else if(min ==3)
				return _downward_rightward;
			break;
		case 4: //upward and leftward
			if(min == 1)
				return _leftward;
			else if(min ==2)
				return _upward_leftward;
			else if(min ==3)
				return _upward;
			break;
		case 5:  //upward and rightward 
			if(min == 1)
				return _upward;
			else if(min ==2)
				return _upward_rightward;
			else if(min ==3)
				return _rightward;
			break;
		case 6:  //downward and leftward
			if(min == 1)
				return _downward;
			else if(min ==2)
				return _downward_leftward;
			else if(min ==3)
				return _leftward;
			break;
		case 7:  //downward and rightward
			if(min == 1)
				return _rightward;
			else if(min ==2)
				return _downward_rightward;
			else if(min ==3)
				return _downward;
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

		int turnleft = (_total_pheromone - left)/(2*_total_pheromone);
		int turnright = (_total_pheromone - right)/(2*_total_pheromone);
		int straightahead = (_total_pheromone - ahead)/(2*_total_pheromone);

		if(turnleft == turnright && turnright == straightahead){
			int alea = (int) (Math.random()*3);
			if(alea == 0)
				return 1;
			else if (alea ==1)
				return 2;
			else
				return 3;
		}
		else{
			if(turnleft == turnright && turnleft < straightahead){
				int alea = (int) (Math.random()*2);
				if(alea == 0)
					return 1;
				else 
					return 3;
			}
			if(turnleft == straightahead && turnleft < turnright){
				int alea = (int) (Math.random()*2);
				if(alea == 0)
					return 1;
				else 
					return 2;
			}
			if(turnright == straightahead && turnright < turnleft){
				int alea = (int) (Math.random()*2);
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
	public void avoidEdges(int x, int y){

		if((x-_margin < 0))  //touch the left edge
		{
			_steering_angle = _rightward;
			setLocation(x+_margin, y);
			x+=_margin;
			setDirection(_steering_angle);
		}
		if( y-_margin < 0)  //touch the up edge
		{
			_steering_angle = _downward;
			setLocation(x, y+_margin);
			y+=_margin;
			setDirection(_steering_angle);
		}
		if((x+_margin >_dimension))  //touch the right edge
		{
			_steering_angle = _leftward;
			setLocation(x-_margin, y);
			x-=_margin;
			setDirection(_steering_angle);
		}
		if(y+_margin > _dimension)  //touch the down edge
		{
			_steering_angle = _upward;
			setLocation(x, y-_margin);
			y-=_margin;
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
		for(int i=0;i<_dimension;i++){			
			for(int j=0;j<_dimension;j++){			
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
	public void displayScanPercentage()
	{
		System.out.println("Scan : "+ (Main.totalscan/_total_potential_scan*100) + "%");
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
