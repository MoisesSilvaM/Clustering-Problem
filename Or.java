import ec.*;
import ec.gp.*;

public class Or extends GPNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4456947833828449749L;


	public String toString() { return "Or"; }
	
	
	public int expectedChildren() { return 2; }
	
	
	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem)
	{
		MyGPData result = (MyGPData)(input);
		
		MyProblem myproblem = (MyProblem)problem;
		
        //We evaluate our left children.
		//System.out.println("ent OR izq");
		children[0].eval(state,thread,input,stack,individual,myproblem);
		MyGPData leftdata = (MyGPData)(input);
		//System.out.println("fin OR izq");
		
		if(children[0].toString()=="Create_Cp" || children[0].toString()=="Create_CP_Vecino" && children[1].toString()=="Delete_CP_densidad" || children[1].toString()=="Delete_CP_min" || children[1].toString()=="Delete_CP_prom") {
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
		
		if (leftdata.condicion == true)//Short circuit for OR, we must not continue evaluating the boolean expression (left OR right).
        {
        	result.condicion = true; 
        	result.punto = null;
            
            return;
        }
		
		//We evaluate our right children.
        //System.out.println("ent OR der");
        children[1].eval(state,thread,input,stack,individual,myproblem);
		MyGPData rightdata = (MyGPData)(input);
		//System.out.println("fin OR der");
        if (rightdata.condicion == true)//Short circuit for OR, we must not continue evaluating the boolean expression (left OR right).
        {
        	result.condicion = true; 
        	result.punto = null;
            
            return;
        }
		
    	result.condicion = false; 
    	result.punto = null;
	}

}