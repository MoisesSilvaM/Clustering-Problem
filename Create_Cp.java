import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class Create_Cp extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8629833052067486042L;
	//private static Logger logNormal = LogManager.getLogger("logNormal");
	public Singleton sin = Singleton.getInstance();
	//String logantes = "";

	public String toString() { return "Create_Cp"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    	//long startTime = System.currentTimeMillis();
    	//if(sin.selogea) logantes = ((MyProblem)problem).instancia.logantes();
    	MyGPData d = ((MyGPData)(input));
    	//MyProblem myproblem = (MyProblem)problem;
    	//System.out.println("ent create cp");
    	boolean condition = ((MyProblem)problem).instancia.Create_Cp();
    	//System.out.println("fin create cp");
		//if(sin.selogea) logNormal.debug(logantes + ((MyProblem)problem).instancia.logdespues());
    	

		d.punto = null;
		d.condicion = condition;
		//long stopTime = System.currentTimeMillis();
		//sin.Create_CpElapsedTime += stopTime - startTime;
		sin.Create_CpCounter++;
		/*if(sin.Create_CpCounter > myproblem.instancia.numeroPuntos) {
			d.punto = null;
			d.condicion = false;
			//System.out.print("´");
			return;
		}*/
		
    }
}