import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class Delete_CP_prom extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Singleton sin = Singleton.getInstance();

	public String toString() { return "Delete_CP_prom"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    	//System.out.println("ent delete prom");
    	MyGPData d = ((MyGPData)(input));
    	//MyProblem myproblem = (MyProblem)problem;
    	//System.out.println("fin delete prom");
    	boolean condition = ((MyProblem)problem).instancia.Delete_CP_prom();
		 
		d.punto = null;
		d.condicion = condition;
		sin.Delete_CP_promCounter++;
		/*if(sin.Delete_CP_promCounter > myproblem.instancia.numeroPuntos) {
			d.punto = null;
			d.condicion = false;
			//System.out.print(".");
			return;
		}*/
    }

}