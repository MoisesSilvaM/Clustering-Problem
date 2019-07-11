import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class Add_Mindot extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8951805079896942246L;
	//private static Logger logNormal = LogManager.getLogger("logNormal");
	public Singleton sin = Singleton.getInstance();
	//String logantes = "";
	
	public String toString() { return "Add_Mindot"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    	//long startTime = System.currentTimeMillis();
    	//if(sin.selogea) logantes = ((MyProblem)problem).instancia.logantes();
    	MyGPData d = ((MyGPData)(input));
    	
    	//System.out.println("ent mindot");
		Punto punto_mindot = ((MyProblem)problem).instancia.Add_Mindot();
		//System.out.println("fin mindot");
		//if(sin.selogea) logNormal.debug(logantes + ((MyProblem)problem).instancia.logdespues());
		
		d.punto = punto_mindot;
		d.condicion = (d.punto != null);
		//long stopTime = System.currentTimeMillis();
		//sin.Add_MindotElapsedTime += stopTime - startTime;
		sin.Add_MindotCounter++;
    }

}