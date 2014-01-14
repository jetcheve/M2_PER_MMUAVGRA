import jbotsim.Clock;
import jbotsim.Message;
import jbotsim.Node;
import jbotsim.event.ClockListener;
import jbotsim.event.MessageListener;


public class CandC extends Node implements ClockListener, MessageListener{
	private boolean display_pheromone = false;

	private static long _start;
	private boolean _first_launch = true;
	private static int _dimension = 500;
	private static int _margin = 5;
	private static double _total_potential_scan = ((_dimension - (2*_margin)) * (_dimension- (2*_margin)));

	/**
	 * @brief Constructor ...
	 * @param 
	 * @return
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
	 * @brief 
	 * @param 
	 * @return
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
	private void update(int[][] content){
		Main._totalscan = 0;
		Main._jtopo.clearListe();
		for(int i=0;i<_dimension;i++){                        
			for(int j=0;j<_dimension;j++){                  
				if(Main._map_scan[i][j] < content[i][j])
					Main._map_scan[i][j] = content[i][j];
				if(Main._map_scan[i][j] > 0){
					if(display_pheromone){
						Main._jtopo.addPoint(i, j);
					}
					Main._totalscan++;
				}
			}
		}
	}

	/**
	 * @brief 
	 * @param 
	 * @return
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
