import ec.*;
import ec.gp.*;

public class Not extends GPNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8441276216331510235L;


	public String toString() { return "Not"; }
	
	
	public int expectedChildren() { return 1; }
	
	
	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem)
	{
		MyGPData result = (MyGPData)(input);
		
		MyProblem myproblem = (MyProblem)problem;
		
        //We evaluate our left children.
        children[0].eval(state,thread,input,stack,individual,myproblem);
		MyGPData data = (MyGPData)(input);
		
		
        if (data.condicion == true)
        {
			result.condicion = false; 
        	result.punto = null;
            
            return;
        }
		else{
			result.condicion = true; 
        	result.punto = null;
            
            return;
		}
	}

}