import ec.*;
import ec.gp.*;

public class If extends GPNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7861074072468456085L;


	public String toString() { return "If"; }
	
	
	public int expectedChildren() { return 2; }
	
	
	/**
	 * {@inheritDoc}
	 * 
	 * If (left son)
	 * {
	 * 		rightSson
	 * 		return true;
	 * }
	 * else
	 * {
	 * 		return false;
	 * }
	 **/
	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem)
	{
		MyGPData result = (MyGPData)(input);
		
		MyProblem myproblem = (MyProblem)problem;
		
        //We evaluate our left children.
		//System.out.println("ent IF izq");
		children[0].eval(state,thread,input,stack,individual,myproblem);
		//System.out.println("fin IF izq");
		MyGPData leftdata = (MyGPData)(input);
	
		if(children[0].toString()=="Create_Cp" || children[0].toString()=="Create_CP_Vecino" && children[1].toString()=="Delete_CP_densidad" || children[1].toString()=="Delete_CP_min" || children[1].toString()=="Delete_CP_prom") {
			//System.out.println("y es falsa");
			result.condicion = false; 
        	result.punto = null;
            return;
		}
        if(children[1].toString()=="Create_Cp" || children[1].toString()=="Create_CP_Vecino" && children[0].toString()=="Delete_CP_densidad" || children[0].toString()=="Delete_CP_min" || children[0].toString()=="Delete_CP_prom") {
			//System.out.println("y es falsa");
        	result.condicion = false; 
        	result.punto = null;
            return;
		}
		
        if (leftdata.condicion == true)
        {
			//We evaluate our right children.
        	//System.out.println("ent IF der");
        	children[1].eval(state,thread,input,stack,individual,myproblem);
        	MyGPData rightdata = (MyGPData)(input);
        	//System.out.println("fin IF der");
        	rightdata.condicion = result.condicion; 
        	result.punto = null;
            
            return;
        }
	}

}