import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import jbotsim.Clock;
import jbotsim.Node;
import jbotsim.event.ClockListener;

public class MovingNode extends Node implements ClockListener{
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
			//Main._jtopo.addPoint(x, y);
		}
		if(x-1 >0 && Main._map[x-1][y]==0 )
		{
			Main._map[x-1][y] = 1;
			Main._totalscan++;
			//Main._jtopo.addPoint(x-1, y);
		}
		if(x+1 < _dimension && Main._map[x+1][y]==0 )
		{
			Main._map[x+1][y] = 1;
			Main._totalscan++;
		//	Main._jtopo.addPoint(x+1, y);
		}
		for(int i = x, j=y-1,area = 3; area > 0; area--,j++){
			if(j >0 && j < _dimension && i < _dimension && i > 0 && Main._map[i][j]==0)
			{
				Main._map[i][j] = 1;
				Main._totalscan++;
			//	Main._jtopo.addPoint(i, j);
			}
		}
	}

	public void displayScanPercentage()
	{
		System.out.println("Scan : "+ (Main._totalscan/_totalscanpossible*100) + "%");
	}
}
