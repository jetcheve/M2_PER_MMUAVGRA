package random_mobility;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import jbotsim.Clock;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.ClockListener;

public class MovingNode extends Node implements ClockListener{
	private static double totalscanpossible = 500 * 500;
	private Point2D destination = new Point(0,0);

	public MovingNode(/*Function_mobility_model fun*/){
		setProperty("icon", "/avion.png");
		setProperty("size", 20);
		setCommunicationRange(-1);
		Clock.addClockListener(this, 1);
		int x = (int) (Math.random()*501);
		int y = (int) (Math.random()*501);
		destination.setLocation(x, y);
		setDirection(destination);
		new HashMap<Integer, ArrayList<Integer>>();
	}

	public void onClock(){
		if((int)getLocation().getX() >= (int)destination.getX()-1 && (int) getLocation().getX() <=(int)destination.getX()+1
				&& (int)getLocation().getY() >= (int)destination.getY()-1 && (int) getLocation().getY() <=(int)destination.getY()+1)
		{
			System.out.println("Destination reached, changing target");
			int x = (int) (Math.random()*501);
			int y = (int) (Math.random()*501);
			destination.setLocation(x,y);
		}
		setDirection(destination);
		Point2D pos = getLocation();
		move(1);
		wrapLocation();
		scan((int)pos.getX(), (int)pos.getY());
		displayScanPercentage();
	}

	public void scan(int x, int y){
		if(Main.map[x][y]==0)
		{
			Main.map[x][y]=1;
			Main.totalscan++;
			Main.jtopo.addPoint(x, y);
		}
	}

	public void displayScanPercentage()
	{
		System.out.println("Scan : "+ (Main.totalscan/totalscanpossible*100) + "%");
	}
}
