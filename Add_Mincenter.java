import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class Add_Mincenter extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2466565451690586188L;
	//private static Logger logNormal = LogManager.getLogger("logNormal");
	public Singleton sin = Singleton.getInstance();
	//String logantes = "";

	public String toString() { return "Add_Mincenter"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    	//long startTime = System.currentTimeMillis();
    	//if(sin.selogea) logantes = ((MyProblem)problem).instancia.logantes();
    	MyGPData d = ((MyGPData)(input));
		
		Punto punto_mincenter = null;
		
		//System.out.println("ent addMinCenter");
		punto_mincenter = ((MyProblem)problem).instancia.Add_Mincenter();
		//System.out.println("fin addMinCenter");
		
		//if(sin.selogea) logNormal.debug(logantes + ((MyProblem)problem).instancia.logdespues());
		 
		d.punto = punto_mincenter;
		d.condicion = (d.punto != null);
		//long stopTime = System.currentTimeMillis();
		//sin.Add_MincenterElapsedTime += stopTime - startTime;
		sin.Add_MincenterCounter++;
    }

}