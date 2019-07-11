import ec.*;
import ec.gp.*;

public class And extends GPNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3809619002033827584L;


	public String toString() { return "And"; }
	
	
	public int expectedChildren() { return 2; }
	
	
	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem)
	{
		MyGPData result = (MyGPData)(input);
		
		MyProblem myproblem = (MyProblem)problem;
		
        //We evaluate our left children.
		//System.out.println("ent AND izq");
		children[0].eval(state,thread,input,stack,individual,myproblem);
		MyGPData leftdata = (MyGPData)(input);
        
		if(children[0].toString()=="Create_Cp" || children[0].toString()=="Create_CP_Vecino" && children[1].toString()=="Delete_CP_densidad" || children[1].toString()=="Delete_CP_min" || children[1].toString()=="Delete_CP_prom") {
			result.condicion = false; 
        	result.punto = null;
            return;
		}
        if(children[1].toString()=="Create_Cp" || children[1].toString()=="Create_CP_Vecino" && children[0].toString()=="Delete_CP_densidad" || children[0].toString()=="Delete_CP_min" || children[0].toString()=="Delete_CP_prom") {
        	result.condicion = false; 
        	result.punto = null;
            return;
		}
		
		//System.out.println("fin AND izq");
        if (leftdata.condicion == false)//Short circuit for AND, we must not continue evaluating the boolean expression (left AND right).
        {
        	result.condicion = false; 
        	result.punto = null;
            
            return;
        }
		
		//We evaluate our right children.
        //System.out.println("ent AND der");
        children[1].eval(state,thread,input,stack,individual,myproblem);
		MyGPData rightdata = (MyGPData)(input);
		//System.out.println("fin AND der");
        if (rightdata.condicion == false)//Short circuit for AND, we must not continue evaluating the boolean expression (left AND right).
        {
        	result.condicion = false; 
        	result.punto = null;
            
            return;
        }
		
    	result.condicion = true; 
    	result.punto = null;
	}

}