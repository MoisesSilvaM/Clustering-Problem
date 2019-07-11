import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class Move_Min extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5787583177728973996L;
	//private static Logger logNormal = LogManager.getLogger("logNormal");
	public Singleton sin = Singleton.getInstance();
	String logantes = "";

	public String toString() { return "Move_Min"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    	//long startTime = System.currentTimeMillis();
    	//if(sin.selogea) logantes = ((MyProblem)problem).instancia.logantes();
    	MyGPData d = ((MyGPData)(input));
		//System.out.println("ent move min");
    	Punto punto_move_min = null;
    	punto_move_min = ((MyProblem)problem).instancia.Move_Min();
    	//System.out.println("fin move min");
		//if(sin.selogea) logNormal.debug(logantes + ((MyProblem)problem).instancia.logdespues());
		
		d.punto = punto_move_min;
		d.condicion = (d.punto != null);
		//long stopTime = System.currentTimeMillis();
		//sin.Move_MinElapsedTime += stopTime - startTime;
		sin.Move_MinCounter++;
    }

}