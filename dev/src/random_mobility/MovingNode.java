package walk;
import java.awt.geom.Point2D;
import jbotsim.Clock;
import jbotsim.Node;
import jbotsim.event.ClockListener;

public class MovingNode extends Node implements ClockListener{
	private static boolean display_trajectory = false;
	private int _lastdirection; //  1 -> left , 2-> straight ahead, 3 -> right
	private static double _angle_towards; //direction of an UAV.
	private static double _amplitude_variation_towards = Math.PI/4;
	private static int _dimension = 500;
	private static long _start;	
	private static int _margin = 5;
	private static double _totalscanpossible= ((_dimension - (2*_margin)) * (_dimension- (2*_margin)));


	public MovingNode(){
		setProperty("icon", "/avion.png");
		setProperty("size", 20);
		setCommunicationRange(-1);
		Clock.addClockListener(this, 1);
		_angle_towards = 3*Math.PI/2;  //direction : upward.
		setDirection(_angle_towards);
		_lastdirection = 2;
		_start = System.currentTimeMillis();
	}

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	public void onClock(){
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
		if(_x < (_dimension - _margin) && _x >= _margin && _y >= _margin && _y < (_dimension - _margin))
			scan((int)_pos.getX(), (int)_pos.getY());
		display_percentage_scan();
	}

	/**
	 * @brief 
	 * @param 
	 * @return
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
	 * @brief 
	 * @param 
	 * @return
	 */
	public void scan(int x, int y){
		if(Main._map[x][y]==0)
		{
			Main._map[x][y]=1;
			Main._totalscan++;
			if(display_trajectory)
				Main._jtopo.addPoint(x, y);
		}
	}

	/**
	 * @brief 
	 * @param 
	 * @return
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
}