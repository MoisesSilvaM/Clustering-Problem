import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.gp.*;

public class OMRk extends GPNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8629833052067486042L;
	private static Logger logNormal = LogManager.getLogger("logNormal");
	public Singleton sin = Singleton.getInstance();

	public String toString() { return "OMRk"; }
	
	public int expectedChildren() { return 0; }
	
    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
    
    	MyGPData d = ((MyGPData)(input));
			
    	//System.out.println("ent keans");
    	boolean OMRk = ((MyProblem)problem).instancia.OMRk();

    	//System.out.println("fin keans");

		d.punto = null;
		d.condicion = OMRk;
    }

}