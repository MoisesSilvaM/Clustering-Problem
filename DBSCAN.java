import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class DBSCAN extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8629833052067486042L;
	//private static Logger logNormal = LogManager.getLogger("logNormal");
	public Singleton sin = Singleton.getInstance();
//	String logantes = "";

	public String toString() { return "DBSCAN"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    	long startTime = System.currentTimeMillis();
    	//if(sin.selogea) logantes = ((MyProblem)problem).instancia.logantes();
    	MyGPData d = ((MyGPData)(input));	
    	//System.out.println("ent DBSCAN");
    	boolean dbscan = ((MyProblem)problem).instancia.DBSCAN();
    	//System.out.println("fin DBSCAN");
		//if(sin.selogea) logNormal.debug(logantes + ((MyProblem)problem).instancia.logdespues());
		
		d.punto = null;
		d.condicion = dbscan;
		long stopTime = System.currentTimeMillis();
		sin.Create_CpElapsedTime += stopTime - startTime;
		sin.Create_CpCounter++;
    }

}