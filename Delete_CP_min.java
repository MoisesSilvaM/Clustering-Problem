import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class Delete_CP_min extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Singleton sin = Singleton.getInstance();

	public String toString() { return "Delete_CP_min"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    	//System.out.println("ent delete min");
    	MyGPData d = ((MyGPData)(input));
    	//MyProblem myproblem = (MyProblem)problem;
    	//System.out.println("fin delete min");
		boolean condition = ((MyProblem)problem).instancia.Delete_CP_min();
		 
		d.punto = null;
		d.condicion = condition;
		sin.Delete_CP_minCounter++;
		/*if(sin.Delete_CP_minCounter > myproblem.instancia.numeroPuntos) {
			d.punto = null;
			d.condicion = false;
			//System.out.print("´");
			return;
		}*/
    }

}
