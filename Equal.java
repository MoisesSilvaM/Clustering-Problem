import ec.*;
import ec.gp.*;

public class Equal extends GPNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4456947833828449749L;


	public String toString() { return "Equal"; }
	
	
	public int expectedChildren() { return 2; }
	
	
	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem)
	{
		MyGPData result = (MyGPData)(input);
		
		MyProblem myproblem = (MyProblem)problem;
		
        //We evaluate our left children.
        children[0].eval(state,thread,input,stack,individual,myproblem);
		MyGPData leftdata = (MyGPData)(input);
		
		//We evaluate our right children.
        children[1].eval(state,thread,input,stack,individual,myproblem);
		MyGPData rightdata = (MyGPData)(input);
		
		
        if (rightdata.condicion == leftdata.condicion)
        {
        	result.condicion = true; 
        	result.punto = null;
            
            return;
        }
		
    	result.condicion = false; 
    	result.punto = null;
	}

}