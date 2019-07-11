	import org.apache.logging.log4j.LogManager;
	import org.apache.logging.log4j.Logger;

	import ec.*;
	import ec.gp.*;

	public class Delete_CP_densidad extends GPNode {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		//private static Logger logNormal = LogManager.getLogger("logNormal");
		public Singleton sin = Singleton.getInstance();
		//String logantes = "";

		public String toString() { return "Delete_CP_densidad"; }
		
		public int expectedChildren() { return 0; }
		
	    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
	    	//long startTime = System.currentTimeMillis();
	    	//if(sin.selogea) logantes = ((MyProblem)problem).instancia.logantes();
	    	MyGPData d = ((MyGPData)(input));
	    	//MyProblem myproblem = (MyProblem)problem;
	    	//System.out.println("ent delete densidad");
	    	boolean condition = ((MyProblem)problem).instancia.Delete_CP_densidad();
	    	//System.out.println("fin delete densidad");
			//if(sin.selogea) logNormal.debug(logantes + ((MyProblem)problem).instancia.logdespues());
			
			d.punto = null;
			d.condicion = condition;
			//long stopTime = System.currentTimeMillis();
			//sin.Create_CpElapsedTime += stopTime - startTime;
			sin.Delete_CP_densidadCounter++;
			/*if(sin.Delete_CP_densidadCounter > myproblem.instancia.numeroPuntos) {
				d.punto = null;
				d.condicion = false;
				//System.out.print(",");
				return;
			}*/
	    }

	}