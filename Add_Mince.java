import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class Add_Mince extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5779111183607924641L;
	//private static Logger logNormal = LogManager.getLogger("logNormal");
	public Singleton sin = Singleton.getInstance();
	String logantes = "";

	public String toString() { return "Add_Mince"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    	//long startTime = System.currentTimeMillis();
    	//if(sin.selogea) logantes = ((MyProblem)problem).instancia.logantes();
    	MyGPData d = ((MyGPData)(input));
		Punto punto_mince = null;
		System.out.println("entrando add_mince");
		punto_mince = ((MyProblem)problem).instancia.Add_Mince();
		System.out.println("fin add_mince");
		
		//if(sin.selogea) logNormal.debug(logantes + ((MyProblem)problem).instancia.logdespues());
		
		d.punto = punto_mince;
		d.condicion = (d.punto != null);
		//long stopTime = System.currentTimeMillis();
		//sin.Add_MinceElapsedTime += stopTime - startTime;
		sin.Add_MinceCounter++;
    }

}