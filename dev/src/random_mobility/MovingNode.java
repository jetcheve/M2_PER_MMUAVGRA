package random_mobility;
import jbotsim.Clock;
import jbotsim.Node;
import jbotsim.event.ClockListener;

public class MovingNode extends Node implements ClockListener{
	private int lastdirection; //  1 -> gauche , 2-> tout droit, 3 -> droite
	private double angle_direction; //direction d'un UAV en Radian.
	private static double amplitude_variation_direction = Math.PI/4; // l'amplitude du changement de direction

	public MovingNode(){
		setProperty("icon", "/avion.png");
        setProperty("size", 20);
		Clock.addClockListener(this, 1);
		angle_direction = 3*Math.PI/2;  //direction : vers le haut.
		setDirection(angle_direction);
		lastdirection = 2;
	}

	public void onClock(){
		int alea = (int) (Math.random()*10);
		switch (lastdirection){

		//derniere action gauche :
		case 1:  
			if(alea <= 2) //30% probabilité d'aller tout droit donc aucun changement direction.
			{
				lastdirection = 2;
			}
			else   //70% probabilité de tourner a gauche.
			{
				angle_direction -= amplitude_variation_direction;
				setDirection(angle_direction);
			}
			break;

			//derniere action tout droit :
		case 2:    
			if(alea == 0) //10% probabilité de tourner a gauche.
			{
				lastdirection = 1;
				angle_direction -= amplitude_variation_direction;
				setDirection(angle_direction);
			}
			else if(alea <= 8 && alea !=0) // 80% probabilité d'aller tout droit donc aucun changement direction.
				;
			else if(alea == 9) //10% probabilité de tourner a droite.
			{
				lastdirection = 3;
				angle_direction += amplitude_variation_direction;
				setDirection(angle_direction);
			}
			break;

			//derniere action droite :  
		case 3:  
			if(alea <= 2) //30% probabilité d'aller tout droit donc aucun changement direction.
				;
			else  //70% probabilité de tourner a droite.
			{
				lastdirection = 2;
				angle_direction += amplitude_variation_direction;
				setDirection(angle_direction);
			}
			break;

		default:
			break;
		}      
		move(1);
		wrapLocation();
	}
}
