	import org.apache.logging.log4j.LogManager;
	import org.apache.logging.log4j.Logger;

	import ec.*;
	import ec.gp.*;

	public class Reasigna_CP extends GPNode {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Singleton sin = Singleton.getInstance();

		public String toString() { return "Reasigna_CP"; }
		
		public int expectedChildren() { return 0; }
		
	    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {

	    	MyGPData d = ((MyGPData)(input));
	    	//System.out.println("ent reasigna cp");
			Punto Reasigna_CP = null;
	    	Reasigna_CP = ((MyProblem)problem).instancia.Reasigna_CP();
			//System.out.println("fin reasigna cp");
			d.punto = Reasigna_CP;
			d.condicion = (d.punto != null);
			sin.Reasigna_CPCounter++;
	    }

	}

