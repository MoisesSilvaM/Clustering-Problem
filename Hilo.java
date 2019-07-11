/*import java.util.ArrayList;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;

public class Hilo extends Thread {
    
    	
    	public void run (final EvolutionState state,final Individual ind,final int subpopulation,final int threadnum)
		{
			if (!ind.evaluated) {
			
    		MyGPData input = (MyGPData)(this.input);
			int hits = 0;
            double sum = 0.0;
            double result=0;
            int sumatiempo=0;
            ArrayList<SuperConjunto> LCP_ = new ArrayList<SuperConjunto>();
        	ArrayList<Conjunto> LSP_ = new ArrayList<Conjunto>();
        	ArrayList<Double> error = new ArrayList<Double>();
        	ArrayList<Double> no_agrupados = new ArrayList<Double>();
        	ArrayList<Double> ISobt = new ArrayList<Double>();
        	ArrayList<Integer> time = new ArrayList<Integer>();
        	//System.out.println(ind.toString() + "\n ");
        	//((GPIndividual)ind).trees[0].printStyle = GPTree.PRINT_STYLE_DOT;
        	//System.out.println("ind.size: "+((GPIndividual)ind).size()+"profundidad: " + ((GPIndividual)ind).trees[0].child.depth());
        	//System.out.println(((GPIndividual)ind).trees[0].child.depth());
        	
        	//System.out.println(((GPIndividual)ind).trees[0].printStyle);
        	
			for(Instancia i : contenedor.instancias){
				
				instancia = i;
				instancia.recargar();

				//System.out.println("Entrando"+i.getFilename());
				long startTime = System.currentTimeMillis();
				((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				int totaltime = (int) (System.currentTimeMillis() - startTime);
				//System.out.print(totaltime + ", ");

				
				
				//Tiempo maximo
				if (totaltime > 1200){
					sum = contenedor.instancias.size();
					System.out.print("S");
					//result = result+2;
					break;
				}
				else {
				result = instancia.fitness(((GPIndividual)ind).size(), totaltime);	
				LCP_.add(new SuperConjunto(instancia.getLCP()));
				LSP_.add(new Conjunto(instancia.getLSP()));
				error.add(instancia.error());
				no_agrupados.add(instancia.noAgrupados());
				ISobt.add(instancia.getISobt());
				time.add(totaltime);
				sumatiempo=sumatiempo+totaltime;	
				//HIT
				if (result <= 0.001) { 
					hits++;}
				
				sum += result;
				}
			}
			sum = sum/contenedor.instancias.size();
			//System.out.print("\n(  tiempo: " + sumatiempo);
			//System.out.print(", fitness: " + sum);
	
			if(sin.fitness > sum){			
				System.out.print("--g--"+time);
				sin.fitness = sum;
				sin.mejoresLCP = new ArrayList<SuperConjunto>(LCP_);
				sin.quedaLSP = new ArrayList<Conjunto>(LSP_);
				sin.error = new ArrayList<Double>(error);
				sin.no_agrupados = new ArrayList<Double>(no_agrupados);
				sin.ISobt = new ArrayList<Double>(ISobt);
				sin.time = new ArrayList<Integer>(time);
			}
				
			
			// the fitness better be KozaFitness!
//			if(j==1) {
			KozaFitness f = ((KozaFitness)ind.fitness);
            f.setStandardizedFitness(state,(float)sum);
            
            f.hits = hits;
            ind.evaluated = true;
            //if(sin.selogea){
//
            	//logNormal.debug("fin evaluación *****, fitness {}\n\n", sum);
//            	logResumido.debug("fin evaluación *****, fitness {}\n\n", sum);
            //}
			//System.out.println(("fin evaluando****** \n"));
		
	//		}	
		}
		System.out.print(".");
		
    }
}
*/