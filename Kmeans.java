import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class Kmeans extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8629833052067486042L;
	private static Logger logNormal = LogManager.getLogger("logNormal");
	public Singleton sin = Singleton.getInstance();
	String logantes = "";

	public String toString() { return "Kmeans"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    	long startTime = System.currentTimeMillis();
    	if(sin.selogea) logantes = ((MyProblem)problem).instancia.logantes();
    	MyGPData d = ((MyGPData)(input));
			
    	//System.out.println("ent keans");
    	boolean kmeans = ((MyProblem)problem).instancia.KMeans(0);

    	//System.out.println("fin keans");
		
    	//if(sin.selogea) logNormal.debug(logantes + ((MyProblem)problem).instancia.logdespues());
		
		d.punto = null;
		d.condicion = kmeans;
		long stopTime = System.currentTimeMillis();
		sin.KmeansElapsedTime += stopTime - startTime;
		sin.KmeansCounter++;
    }

}