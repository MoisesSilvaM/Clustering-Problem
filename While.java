import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.*;
import ec.app.regression.func.Square;
import ec.gp.*;

public class While extends GPNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4002771103570543933L;
	private static Logger logNormal = LogManager.getLogger("logNormal");
	public Singleton sin = Singleton.getInstance();


	public String toString() { return "While"; }
	
	
	public int expectedChildren() { return 2; }
	
	
	/**
	 * {@inheritDoc}
	 * 
	 * while(leftSon)
	 * {
	 * 		rightSon
	 * }
	 **/
	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem)
	{
		MyProblem myproblem = (MyProblem)problem;
		int counterwhile = 0;
		boolean done = false;
		double counterwhilemax = myproblem.instancia.numeroPuntos;
		children[0].eval(state, thread, input, stack, individual, problem);
		MyGPData result = (MyGPData)(input);
        boolean y = result.condicion;
        if(children[0].toString()=="Create_CP_Vecino"||children[0].toString()=="Create_CP_Vecino"&&children[1].toString()=="Delete_CP_densidad"||children[1].toString()=="Delete_CP_min"||children[1].toString()=="Delete_CP_prom") {
        	y=false;
        }
        if(children[1].toString()=="Create_CP_Vecino"||children[1].toString()=="Create_CP_Vecino"&&children[0].toString()=="Delete_CP_densidad"||children[0].toString()=="Delete_CP_min"||children[0].toString()=="Delete_CP_prom") {
            y=false;
        }
        while(counterwhilemax > counterwhile && y){		
		//We evaluate our right children.
			children[1].eval(state,thread,input,stack,individual,myproblem);
			if(done==false) {
				result = (MyGPData)(input);
				if (result.condicion==true){
					done = true;
				}
			}
			children[0].eval(state, thread, input, stack, individual, problem);
			//System.out.println("fin while izquierdo");
			result = (MyGPData)(input);
	        y = result.condicion;
			counterwhile++;
		}	
        	result.condicion = done;
			result.punto = null;
	}
}