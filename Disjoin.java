import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class Disjoin extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2469474234216912290L;
	private static Logger logNormal = LogManager.getLogger("logNormal");
	public Singleton sin = Singleton.getInstance();
	String logantes = "";

	public String toString() { return "DisJoin"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    	long startTime = System.currentTimeMillis();
    	if(sin.selogea) logantes = ((MyProblem)problem).instancia.logantes();
    	MyGPData d = ((MyGPData)(input));
		
    	boolean condition = ((MyProblem)problem).instancia.Disjoin();
		if(sin.selogea) logNormal.debug(logantes + ((MyProblem)problem).instancia.logdespues());
		
		d.punto = null;
		d.condicion = condition;
		long stopTime = System.currentTimeMillis();
		sin.Join_CpElapsedTime += stopTime - startTime;
		sin.Join_CpCounter++;
    }

}