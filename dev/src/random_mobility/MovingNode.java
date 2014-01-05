import java.awt.geom.Point2D;
import jbotsim.Clock;
import jbotsim.Node;
import jbotsim.event.ClockListener;

public class MovingNode extends Node implements ClockListener{
	private int _lastdirection; //  1 -> gauche , 2-> tout droit, 3 -> droite
	private static double _angle_towards; //direction d'un UAV en Radian.
	private static double _amplitude_variation_towards = Math.PI/4; // l'amplitude du changement de direction
	private static double _totalscanpossible = 500 * 500;
	private static long _time;
	private static long _start;	
	private static int _margin = 5;
	
	public MovingNode(){
		setProperty("icon", "/avion.png");
		setProperty("size", 20);
		setCommunicationRange(-1);
		Clock.addClockListener(this, 1);
		_angle_towards = 3*Math.PI/2;  //direction : vers le haut.
		setDirection(_angle_towards);
		_lastdirection = 2;
		_time = 0;
		_start = System.currentTimeMillis();
	}

	public void onClock(){
		_time++;
		int _alea = (int) (Math.random()*10);
		switch (_lastdirection){

		//derniere action gauche :
		case 1:  
			if(_alea <= 2) //30% probabilité d'aller tout droit donc aucun changement direction.
			{
				_lastdirection = 2;
			}
			else   //70% probabilité de tourner a gauche.
			{
				_angle_towards -= _amplitude_variation_towards;
				setDirection(_angle_towards);
			}
			break;

			//derniere action tout droit :
		case 2:    
			if(_alea == 0) //10% probabilité de tourner a gauche.
			{
				_lastdirection = 1;
				_angle_towards -= _amplitude_variation_towards;
				setDirection(_angle_towards);
			}
			else if(_alea <= 8 && _alea !=0) // 80% probabilité d'aller tout droit donc aucun changement direction.
				;
			else if(_alea == 9) //10% probabilité de tourner a droite.
			{
				_lastdirection = 3;
				_angle_towards += _amplitude_variation_towards;

				setDirection(_angle_towards);
			}
			break;

			//derniere action droite :  
		case 3:  
			if(_alea <= 2) //30% probabilité d'aller tout droit donc aucun changement direction.
				;
			else  //70% probabilité de tourner a droite.
			{
				_lastdirection = 2;
				_angle_towards += _amplitude_variation_towards;
				setDirection(_angle_towards);
			}
			break;

		default:
			break;
		}
		Point2D _pos = getLocation();
		int _x = (int)_pos.getX();
		int _y = (int)_pos.getY();


		if((_x-_margin < 0))
		{
			_angle_towards+=Math.PI;
			setLocation(_x+2, _y);
			setDirection(_angle_towards);
		}
		if( _y-_margin < 0)
		{
			_angle_towards+=Math.PI/2;
			setLocation(_x, _y+2);
			setDirection(_angle_towards);
		}
		if((_x+_margin >500))
		{
			_angle_towards+=Math.PI;
			setLocation(_x-2, _y);
			setDirection(_angle_towards);
		}
		if(_y+_margin > 500)
		{
			_angle_towards+=3*Math.PI/2;
			setLocation(_x, _y-2);
			setDirection(_angle_towards);
		}

		move(1);
		wrapLocation();
		_pos = getLocation();
		_x = (int)_pos.getX();
		_y = (int)_pos.getY();
		scan((int)_pos.getX(), (int)_pos.getY());
		display_percentage_scan();
		display_hours();
	}

	private void display_hours() {
		//System.out.println("Le test a commencé depuis " +time + "toc d'horloge");
		
		_time= System.currentTimeMillis(); 
		System.out.println((_time - _start)/1000 + " secondes");
	}

	public void scan(int x, int y){
		if(Main._map[x][y]==0)
		{
			Main._map[x][y]=1;
			Main._totalscan++;
			//Main._jtopo.addPoint(x, y);
		}
		if(Main._map[x-1][y]==0)
		{
			Main._map[x-1][y] = 1;
			Main._totalscan++;
			//Main._jtopo.addPoint(x-1, y);
		}
		if(Main._map[x+1][y]==0)
		{
			Main._map[x+1][y] = 1;
			Main._totalscan++;
			//Main._jtopo.addPoint(x+1, y);
		}
		for(int i = x, j=y-1,area = 3; area > 0; area--,j++){
			if(Main._map[i][j]==0)
			{
				Main._map[i][j] = 1;
				Main._totalscan++;
			//	Main._jtopo.addPoint(i, j);
			}
		}
	}

	public void display_percentage_scan()
	{
		System.out.println("Scan : "+ (Main._totalscan/_totalscanpossible*100) + "%");
	}
}