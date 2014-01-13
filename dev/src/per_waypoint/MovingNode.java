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
	private static int _dimension = 500;
	private static double _totalscanpossible = _dimension * _dimension;
	private Point2D _destination = new Point(0,0);
	
	
	public MovingNode(){
		setProperty("icon", "/avion.png");
		setProperty("size", 20);
		setCommunicationRange(-1);
		Clock.addClockListener(this, 1);
		int x = (int) (Math.random()*501);
		int y = (int) (Math.random()*501);
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
			int x = (int) (Math.random()*501);
			int y = (int) (Math.random()*501);
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
