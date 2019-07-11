import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class Add_Maxce extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2277546732930613998L;
	private static Logger logNormal = LogManager.getLogger("logNormal");
	public Singleton sin = Singleton.getInstance();
	String logantes = "";

	public String toString() { return "Add_Maxce"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    	long startTime = System.currentTimeMillis();
    	//if(sin.selogea) logantes = ((MyProblem)problem).instancia.logantes();
    	MyGPData d = ((MyGPData)(input));
		Punto punto_maxce = null;
		System.out.println("ent add maxce");
		punto_maxce = ((MyProblem)problem).instancia.Add_Maxce();
		//if(sin.selogea) logNormal.debug(logantes + ((MyProblem)problem).instancia.logdespues());
		System.out.println("fin addmaxcep");
		d.punto = punto_maxce;
		d.condicion = (d.punto != null);
		long stopTime = System.currentTimeMillis();
		sin.Add_MaxceElapsedTime += stopTime - startTime;
		sin.Add_MaxceCounter++;
    }



}