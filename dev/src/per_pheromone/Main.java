/**
 * @file Main.java
 * @author atessie, fcastagn, hpaziews, jetcheve & mtesta
 * @version 1.0
 * @date 
 */
package per_pheromone;

import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.ui.JViewer;

/**
 * @class Main
 * @brief Description
 * @details 
 */
public class Main {

	private static int _dimension = 500;		/**< Dimension of the topology */
	public static double _totalscan = 0;		/**< The percentage of the total scan */
	public static JtopologyPheromone _jtopo;	/**< An instance of our toopology */
	public static JViewer _jv;					/**< An instance of a JViewer */

	/**
	 * @brief 
	 * @param 
	 * @return
	 */
	public static void main(String[] args) {
		Topology topo = new Topology();
		topo.setDimensions(_dimension, _dimension);
		Node.setModel("default", new MovingNode());
		_jtopo = new JtopologyPheromone(topo);
		_jv = new JViewer(_jtopo);

		/** 
		 * @TO_DO changer les nombres par des variables, pour eviter les nombres magiques
		 * Par exemple, _UAV_number = 10;
		 */
		for(int i = 0; i < 10; i++)
			topo.addNode(_dimension / 2 - 10, _dimension - 50);
	}
}
