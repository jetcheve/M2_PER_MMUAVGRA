package per_pheromone;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Map;

import jbotsim.Clock;
import jbotsim.Message;
import jbotsim.Node;
import jbotsim.event.ClockListener;
import jbotsim.event.MessageListener;

public class MovingNode extends Node implements ClockListener, MessageListener{

	private static double angle_direction;
	private static int time;
	private static long start;
	private static int total_pheromone = 0;
	//private static int[][] pheromoneMap;
	private static int dimencion = 500;
	private boolean premierLancement = true;
	private static int marge = 5;
	private static double totalscanpossible = dimencion*dimencion;
	
	public MovingNode() {
		setProperty("icon", "/avion.png");
		setProperty("size", 20);
		setCommunicationRange(50);
		Clock.addClockListener(this, 5);
		addMessageListener(this);
		angle_direction = 3*Math.PI/2;  //direction : vers le haut.
		setDirection(angle_direction);
		time = 0;
		start = System.currentTimeMillis();
	}

	@Override
	public void onClock() {
		if(premierLancement)
		{
			int[][] pheromoneMap = new int[dimencion][dimencion];
			for(int i=0;i<dimencion;i++)			
				for(int j=0;j<dimencion;j++)
					pheromoneMap[i][j] =0;
			setProperty("map", pheromoneMap);
			premierLancement = false;
		}
		Point2D pos = getLocation();
		int x = (int)pos.getX();
		int y = (int)pos.getY();
		int [][] tmp = (int[][]) getProperty("map");
		tmp[x/2][y/2] += 1;
		if(tmp[x/2][y/2] == 1)
		{
			Main.totalscan++;
			Main.jtopo.ajouterpt(x, y);
			
		}
		total_pheromone++;
		setProperty("map", tmp);
		Double dir = getDirection();
		dir = analyse(dir); //retourne la nouvelle direction
		if(dir != null)
			setDirection(dir);
		eviter_bord(x,y);
		move(1);
		wrapLocation();
		time++;
		if(time > 50)
		{
			time=0;
			send(null,getProperty("map"));
			//System.out.println("Broadcast de la map");
		}
		afficher_pourcentage_scan();
	}

	private Double analyse(double dir) {
		//System.out.println("la direction est "+dir);
		Point2D pos = getLocation();
		int x = (int)pos.getX();
		int y = (int)pos.getY();
		if(dir == 3*Math.PI/2) //vers le haut
		{
			int min = minimum(new Point(x-1,y),new Point(x,y-1),new Point(x+1,y));
			return min_to_dir(0,min);
		}
		else if(dir == Math.PI/2)  //vers le bas
		{
			int min = minimum(new Point(x+1,y),new Point(x,y+1),new Point(x-1,y)); 
			return min_to_dir(1,min);
		}
		else if(dir == Math.PI) //vers la gauche
		{
			int min = minimum(new Point(x,y+1),new Point(x-1,y),new Point(x,y-1)); 
			return min_to_dir(2,min);
		}
		else if(dir == 2*Math.PI) //vers la droite
		{
			int min = minimum(new Point(x,y-1),new Point(x+1,y),new Point(x,y+1)); 
			return min_to_dir(3,min);
		}
		return null;

	}

	private Double min_to_dir(int i, int min) {
		switch(i){
		case 0:
			if(min == 1)
				return Math.PI;
			else if(min ==3)
				return 2*Math.PI;
			break;
		case 1:
			if(min == 1)
				return 2*Math.PI;
			else if(min ==3)
				return Math.PI;
			break;
		case 2:
			if(min == 1)
				return Math.PI/2;
			else if(min ==3)
				return 3*Math.PI/2;
			break;
		case 3:
			if(min == 1)
				return 3*Math.PI/2;
			else if(min ==3)
				return Math.PI/2;
			break;
		default:
			break;
		}
		return null;
	}

	private int minimum(Point point, Point point2, Point point3) { //1 a gauche; 2 tout droit; 3 a droite
		int left = 0;
		int right = 0;
		int ahead = 0;
		int[][]	pheromonMap = (int[][]) getProperty("map");
		if(estdanslamatrice(point))
			left = pheromonMap[point.x][point.y];
		if(estdanslamatrice(point2))
			ahead =  pheromonMap[point2.x][point2.y];
		if(estdanslamatrice(point3))
			right =  pheromonMap[point3.x][point3.y];

		int turnleft = (total_pheromone - left)/(2*total_pheromone);
		int turnright = (total_pheromone - right)/(2*total_pheromone);
		int straightahead = (total_pheromone - ahead)/(2*total_pheromone);

		if(turnleft == turnright && turnright == straightahead)
		{
			int alea = (int) (Math.random()*3);
			if(alea == 0)
				return 1;
			else if (alea ==1)
				return 2;
			else
				return 3;
		}
		else{
			if(turnleft == turnright && turnleft < straightahead)
			{
				int alea = (int) (Math.random()*2);
				if(alea == 0)
					return 1;
				else 
					return 3;
			}
			if(turnleft == straightahead && turnleft < turnright)
			{
				int alea = (int) (Math.random()*2);
				if(alea == 0)
					return 1;
				else 
					return 2;
			}
			if(turnright == straightahead && turnright < turnleft)
			{
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

	private boolean estdanslamatrice(Point point) {
		return point.x <=0 && point.x >=dimencion && point.y <=0 && point.y >=dimencion;
	}

	public void eviter_bord(int x, int y){

		if((x-marge < 0))
		{
			angle_direction = 2*Math.PI;
			System.out.println("ca touche a gauche");
			setLocation(x+marge, y);
			x+=marge;
			setDirection(angle_direction);
		}
		if( y-marge < 0)
		{
			angle_direction = Math.PI/2;
			System.out.println("ca touche en haut");
			setLocation(x, y+marge);
			y+=marge;
			setDirection(angle_direction);
		}
		if((x+marge >dimencion))
		{
			angle_direction = Math.PI;
			System.out.println("ca touche en droite");
			setLocation(x-marge, y);
			x-=marge;
			setDirection(angle_direction);
		}
		if(y+marge > dimencion)
		{
			angle_direction = 3*Math.PI/2;
			System.out.println("ca touche en bas");
			setLocation(x, y-marge);
			y-=marge;
			setDirection(angle_direction);
		}
	}

	@Override
	public void onMessage(Message msg) {
		update((int[][])msg.content);
	}

	private void update(int[][] content) {
		//System.out.println("MISE A JOUR DE MA MAPPPPP");
		int tmp[][] = (int[][]) getProperty("map");
		for(int i=0;i<dimencion;i++){			
			for(int j=0;j<dimencion;j++){			
			//	if(content[i][j] !=0)
		//		System.out.println("MAAJJJJJJJJ    "+i+"       "+j+ "         ="+content[i][j]+ "   <>   "+tmp[i][j]);
				if(tmp[i][j] < content[i][j])
				{
					//System.out.println("DIFFERENCE POSITION   "+i+" "+j+ "         ="+content[i][j]+ "   <>   "+tmp[i][j]);
					tmp[i][j] = content[i][j];
				}
			}
		}
		setProperty("map", tmp);			
	}
	public void afficher_pourcentage_scan()
	{
		System.out.println("Scan : "+ (Main.totalscan/totalscanpossible*100) + "%");
	}
	
	
}
