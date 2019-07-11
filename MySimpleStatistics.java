import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;


public class MySimpleStatistics extends SimpleStatistics {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2838786307301048420L;
	
	/** The worst individual we've found so far */
    public Individual[] worst_of_run = null;
    public float[] avg_of_run;

	/* (non-Javadoc)
	 * @see ec.simple.SimpleStatistics#postInitializationStatistics(ec.EvolutionState)
	 */
	@Override
	public void postInitializationStatistics(EvolutionState state) {
		super.postInitializationStatistics(state);
		worst_of_run = new Individual[state.population.subpops.length];
		avg_of_run = new float[state.population.subpops.length];
	}

	/* (non-Javadoc)
	 * @see ec.simple.SimpleStatistics#postEvaluationStatistics(ec.EvolutionState)
	 */
	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);
		
		Individual[] worst_i = new Individual[state.population.subpops.length];  // quiets compiler complaints
		float[] avg_f = new float[state.population.subpops.length];
		int[] count = new int[state.population.subpops.length];
		for(int x=0;x<state.population.subpops.length;x++){
			worst_i[x] = state.population.subpops[x].individuals[0];
			avg_f[x] = (float) ((KozaFitness)state.population.subpops[x].individuals[0].fitness).standardizedFitness();
			count[x] = 1;
			
	        for(int y=1;y<state.population.subpops[x].individuals.length;y++){
	        	if (worst_i[x].fitness.betterThan(state.population.subpops[x].individuals[y].fitness)) worst_i[x] = state.population.subpops[x].individuals[y];
	        	avg_f[x] += ((KozaFitness)state.population.subpops[x].individuals[y].fitness).standardizedFitness();
	        	count[x]++;
	        }
	        
	        avg_of_run[x] = avg_f[x]/count[x];
	    
	        // now test to see if it's the new best_of_run
	        if (worst_of_run[x]==null || worst_of_run[x].fitness.betterThan(worst_i[x].fitness)) worst_of_run[x] = (Individual)(worst_i[x].clone());
        }
	}
    
    
}
