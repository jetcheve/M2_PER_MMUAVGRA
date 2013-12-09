package random_mobility;

import jbotsim.Node;

public class ScanNode extends Node {
	
	public ScanNode()
	{
		setProperty("icon", "/point_vert.png");
        setProperty("size", 1);
        setCommunicationRange(-1);
	}
}
