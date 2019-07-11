import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class Create_CP_Vecino extends GPNode {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Singleton sin = Singleton.getInstance();
		
	public String toString() { return "Create_CP_Vecino"; }
	
	public int expectedChildren() { return 0; }
	
	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
		long startTime = System.currentTimeMillis();
    	MyGPData d = ((MyGPData)(input));
    	//MyProblem myproblem = (MyProblem)problem;
    	//System.out.println("ent create cp vecino");
		boolean condition = ((MyProblem)problem).instancia.Create_CP_Vecino();
		//System.out.println("fin create cp vecino");
		d.punto = null;
		d.condicion = condition;
		long stopTime = System.currentTimeMillis();
		sin.Add_MaxceElapsedTime += stopTime - startTime;
		sin.Create_CP_VecinoCounter++;
		/*if(sin.Create_CP_VecinoCounter > myproblem.instancia.numeroPuntos) {
			d.punto = null;
			d.condicion = false;
			//System.out.print("´");
			return;
		}*/
	}	
}
