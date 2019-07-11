import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyProblem extends GPProblem implements SimpleProblemForm
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2446963225855949037L;
	private static Logger logNormal = LogManager.getLogger("salida/logNormal");
	private static Logger logResumido = LogManager.getLogger("salida/logResumido");
	private static Logger logEstadistica = LogManager.getLogger("salida/logEstadistica");
	public static final String entrenamiento = "DSN1.txt";
	public static final String validacion = "validacionALL3.txt";
	public Instancia instancia;
	public Singleton sin = Singleton.getInstance();
	public Contenedor contenedor;
	public static long startGenerationTime;
	public static long endGenerationTime;
	public static long endGenerationTime2;
	public Individual mejor; 
	public int i=1;
	
	public static final String P_DATA = "data";
	
	
	public void setup(final EvolutionState state, final Parameter base)
    {
		logNormal.info("setup problem!");
	    logResumido.info("setup problem!");
	    logEstadistica.info("setup problem!");
		// very important, remember this
	    super.setup(state,base);

	    // verify our input is the right class (or subclasses from it)
	    if (!(input instanceof MyGPData)) state.output.fatal("GPData class must subclass from " + MyGPData.class, base.push(P_DATA), null);

		startGenerationTime = System.nanoTime();
		
		//Lectura de los nombres de las instancias de entrenamiento y validacion
		if(Contenedor.INSTANCIAS_PATH != null && entrenamiento != null){
			Scanner scanner = null;
			try{
				
				scanner =  new Scanner(new FileInputStream(Contenedor.INSTANCIAS_PATH+entrenamiento), "UTF8");
			}
			catch(IOException e){
				e.printStackTrace(System.out);
				System.exit(0);
			}
			String line = scanner.nextLine();
			int numEntreno = Integer.parseInt(line);
			int i = 0;
			Contenedor.instancias_file = new String[numEntreno];
			Contenedor.instancias_SI = new float[numEntreno];
			while (scanner.hasNextLine()){
				line = scanner.nextLine();
				String[] tokens = line.split(",");
				Contenedor.instancias_file[i] = tokens[0];
				Contenedor.instancias_SI[i]=  Float.parseFloat(tokens[1]);
				i++;
			}
			// Ahora validación
			scanner = null;
			try{
				
				scanner =  new Scanner(new FileInputStream(Contenedor.INSTANCIAS_PATH+validacion), "UTF8");
			}
			catch(IOException e){
				e.printStackTrace(System.out);
				System.exit(0);
			}
			line = scanner.nextLine();
			numEntreno = Integer.parseInt(line);
			i = 0;
			Contenedor.validacion_file = new String[numEntreno];
			Contenedor.validacion_SI = new float[numEntreno];
			while (scanner.hasNextLine()){
				line = scanner.nextLine();
				String[] tokens = line.split(",");
				Contenedor.validacion_file[i] = tokens[0];
				Contenedor.validacion_SI[i]=  Float.parseFloat(tokens[1]);
				i++;
			}
			
		}
	    sin.load(Contenedor.INSTANCIAS_PATH, Contenedor.instancias_file, Contenedor.instancias_SI, Contenedor.alfa, Contenedor.beta, Contenedor.gama);
    }
	
	public void evaluate(final EvolutionState state,final Individual ind,final int subpopulation,final int threadnum)
    {
    	if (!ind.evaluated)
		{
    		MyGPData input = (MyGPData)(this.input);
			int hits = 0;
            double sum = 0.0;
            double result=0;
            ArrayList<SuperConjunto> LCP_ = new ArrayList<SuperConjunto>();
        	ArrayList<Conjunto> LSP_ = new ArrayList<Conjunto>();
        	ArrayList<Double> error = new ArrayList<Double>();
        	ArrayList<Double> no_agrupados = new ArrayList<Double>();
        	ArrayList<Double> ISobt = new ArrayList<Double>();
        	ArrayList<Integer> time = new ArrayList<Integer>();
        	int j = contenedor.instancias.size();
        	
        	//int j=contenedor.instancias.size();//System.out.println(ind.toString() + "\n ");
        	//((GPIndividual)ind).trees[0].printStyle = GPTree.PRINT_STYLE_DOT;
        	//System.out.println("ind.size: "+((GPIndividual)ind).size()+"profundidad: " + ((GPIndividual)ind).trees[0].child.depth());
        	//System.out.println(((GPIndividual)ind).trees[0].child.depth());
        	
        	//System.out.println(((GPIndividual)ind).trees[0].printStyle);
        	
			for(Instancia i : contenedor.instancias){
				
				instancia = i;
				instancia.recargar();
				//sin.resetloop();
				//System.out.println("Entrando"+i.getFilename());
				long startTime = System.currentTimeMillis();
				((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				int totaltime = (int) (System.currentTimeMillis() - startTime);				
				
				//Tiempo maximo
				if (totaltime > instancia.numeroPuntos){
					sum = totaltime+instancia.numeroPuntos*j;
					break;
				}
				else {
				result = instancia.fitness(((GPIndividual)ind).size(), totaltime);	
				LCP_.add(new SuperConjunto(instancia.getLCP()));
				LSP_.add(new Conjunto(instancia.getLSP()));
				error.add(instancia.error());
				no_agrupados.add(instancia.noAgrupados());
				ISobt.add(instancia.getISobt());//getpurityobt());  //getISobt2());
				time.add(totaltime);	
				//HIT
				if (result <= 0.001) { 
					hits++;}
				
				sum += result;
				}
				j--;
			//System.out.println(sin.Delete_CP_densidadCounter+ " - " + sin.Delete_CP_promCounter + " - " + sin.Delete_CP_minElapsedTime + " - " + sin.Create_CpCounter + " - " + sin.Create_CP_VecinoCounter);
			}
			
			sum = sum/contenedor.instancias.size();
	
			if(sin.fitness > sum && time.size()==contenedor.instancias.size()){		
				sin.fitness = sum;
				sin.mejoresLCP = new ArrayList<SuperConjunto>(LCP_);
				sin.quedaLSP = new ArrayList<Conjunto>(LSP_);
				sin.error = new ArrayList<Double>(error);
				sin.no_agrupados = new ArrayList<Double>(no_agrupados);
				sin.ISobt = new ArrayList<Double>(ISobt);
				sin.time = new ArrayList<Integer>(time);
			}
			KozaFitness f = ((KozaFitness)ind.fitness);
            f.setStandardizedFitness(state,(float)sum);
            
            f.hits = hits;
            ind.evaluated = true;	
		}
    }
	
	public void evaluatefinal(final EvolutionState state,final Individual ind,final int subpopulation,final int threadnum)
    {

    	if (!ind.evaluated)
		{			
    		MyGPData input = (MyGPData)(this.input);
			int hits = 0;
            double sum = 0.0;
            double result=0;
            ArrayList<SuperConjunto> LCP_ = new ArrayList<SuperConjunto>();
        	ArrayList<Conjunto> LSP_ = new ArrayList<Conjunto>();
        	ArrayList<Double> error = new ArrayList<Double>();
        	ArrayList<Double> no_agrupados = new ArrayList<Double>();
        	ArrayList<Double> ISobt = new ArrayList<Double>();
        	ArrayList<Integer> time = new ArrayList<Integer>();
        	
			for(Instancia i : contenedor.instancias){
				
				instancia = i;
				instancia.recargar();
				//System.out.println("Entrando"+i.getFilename());
				long startTime = System.currentTimeMillis();
				((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				int totaltime = (int) (System.currentTimeMillis() - startTime);

				if(instancia.error()==1.0 || instancia.getISobt()==1.0) {
					((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				}
				if(instancia.error()==1.0 || instancia.getISobt()==1.0) {
					((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				}
				if(instancia.error()==1.0 || instancia.getISobt()==1.0) {
					((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				}
				if(instancia.error()==1.0 || instancia.getISobt()==1.0) {
					((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				}
				if(instancia.error()==1.0 || instancia.getISobt()==1.0) {
					((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				}
				if(instancia.error()==1.0 || instancia.getISobt()==1.0) {
					((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				}
				if(instancia.error()==1.0 || instancia.getISobt()==1.0) {
					((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				}
				if(instancia.error()==1.0 || instancia.getISobt()==1.0) {
					((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				}
				if(instancia.error()==1.0 || instancia.getISobt()==1.0) {
					((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				}
				if(instancia.error()==1.0 || instancia.getISobt()==1.0) {
					((GPIndividual)ind).trees[0].child.eval(state,threadnum,input,stack,((GPIndividual)ind),this);
				}
				result = instancia.fitness(((GPIndividual)ind).size(), totaltime);	
				LCP_.add(new SuperConjunto(instancia.getLCP()));
				LSP_.add(new Conjunto(instancia.getLSP()));
				error.add(instancia.error());
				no_agrupados.add(instancia.noAgrupados());
				ISobt.add(instancia.getISobt());
				time.add(totaltime);
				//HIT
				if (result <= 0.001) { 
					hits++;
					}
				
				sum += result;
				}

			sum = sum/contenedor.instancias.size();
			//System.out.print("\n(  tiempo: " + sumatiempo);
			
	
			//if(sin.fitness > sum){			
				sin.fitness = sum;
				sin.mejoresLCP = new ArrayList<SuperConjunto>(LCP_);
				sin.quedaLSP = new ArrayList<Conjunto>(LSP_);
				sin.error = new ArrayList<Double>(error);
				sin.no_agrupados = new ArrayList<Double>(no_agrupados);
				sin.ISobt = new ArrayList<Double>(ISobt);
				sin.time = new ArrayList<Integer>(time);
			//}
							
			// the fitness better be KozaFitness!
//			if(j==1) {
			KozaFitness f = ((KozaFitness)ind.fitness);
            f.setStandardizedFitness(state,(float)sum);
            
            f.hits = hits;
            ind.evaluated = true;
            if(sin.selogea){
//
            	logNormal.debug("fin evaluación *****, fitness {}\n\n", sum);
//            	logResumido.debug("fin evaluación *****, fitness {}\n\n", sum);
            }
			System.out.println(("fin evaluando****** \n"));
		
			}	
    }	
	public void describe(EvolutionState state, Individual ind, int subpopulation,int threadnum, int log){
		//GUARDO EL MEJOR IND
		state.output.println("#SAVE BEST",0);
		endGenerationTime = System.nanoTime();	//fin cronometro evolución
		state.output.message("#Evolution duration: " + (endGenerationTime - startGenerationTime) / 1000000 + " ms");	//duración evolución en ms
		//System.out.println((endGenerationTime - startGenerationTime) / 1000000);
		PrintWriter printWriter = null;
		
		try {
			printWriter = new PrintWriter(new FileWriter("salida/bestInd.txt", true));
			((GPIndividual)ind).printIndividual(state, printWriter);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		printWriter.close();
		
		//SE EVALUA Y LOGEA LA INFO DEL MEJOR INDIVIDUO
		ind.evaluated = false;
		sin.selogea = true;
		String tamNodo = "#Tamano del nodo: " + ((GPIndividual)ind).size();
		state.output.message("#Nodos: "+((GPIndividual)ind).size());
		double valorFinal = 0.0;
		System.out.println("contenedor.instancias.size() :"+contenedor.instancias.size());
		int ev=contenedor.instancias.size();
		//System.out.println("que xuxa " + ((GPIndividual)ind).fitness.fitness());
		if(sin.selogea){
			String treelatex = ((GPIndividual)ind).trees[0].child.makeLatexTree();
			String treegraphviz = ((GPIndividual)ind).trees[0].child.makeGraphvizTree();
			String fitness = ((GPIndividual)ind).fitness.fitnessToStringForHumans();
			
			logNormal.debug("\n" + fitness + "\n" + treelatex + "\n" + tamNodo + "\n" + treegraphviz + "\n");
			logResumido.debug("\n" + fitness + "\n" + treelatex + "\n" + tamNodo + "\n" + treegraphviz + "\n");
		}
		
		state.output.println(tamNodo, 0);
		for(int i = 0; i < contenedor.instancias.size(); i++){
			state.output.println(contenedor.instancias.get(i).toString()+" - "+sin.ISobt.get(i)+" - "+sin.mejoresLCP.get(i).size()+" - "+sin.error.get(i)+" - "+sin.no_agrupados.get(i), 0);
			/*
			state.output.println(contenedor.instancias.get(i).toString(), 0);
			state.output.println("#Error " + sin.error.get(i) + ",ajustado " + 1.0/(1.0 + sin.error.get(i)),0);
			state.output.println("#No agrupados " + sin.no_agrupados.get(i), 0);
			*/
			valorFinal += sin.error.get(i);
			//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
			//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
			if(sin.selogea){
				logResumido.debug(contenedor.instancias.get(i).toString()+ " IS: " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best );
				logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
				logResumido.debug("Error: " + sin.error.get(i) + " ,ajustado " + 1.0/(1.0 + sin.error.get(i)),0);
				logResumido.debug("No agrupados " + sin.no_agrupados.get(i),0);
				//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
				//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
			}
		}
		double promedioentrenamiento=valorFinal/ev;
//		this.evaluate(state, ind, subpopulation, threadnum);
		if(sin.selogea) logResumido.debug("Termino");
		if(sin.selogea) logResumido.debug("Tiempo Total:"+(endGenerationTime - startGenerationTime) / 1000000 + "");
		
		//SI EL FITNEES ES MEJOR QUE 0.5 SE USA INSTANCIA DE VALIDACION
		//KozaFitness f = ((KozaFitness)((GPIndividual)ind).fitness);
		//if(f.standardizedFitness() <= 0.5)
		
			state.output.println("\n#USANDO INSTANCIAS DE VALIDACION************\n",0);
			logResumido.debug("\nUSANDO INSTANCIAS DE VALIDACION***************\n");
			contenedor.loadvalidacion();
			ind.evaluated = false;
			sin.reset();
			sin.selogea = false;
			this.evaluatefinal(state, ind, subpopulation, threadnum);
			sin.selogea = true;
			String fitness = ((GPIndividual)ind).fitness.fitnessToStringForHumans();
			state.output.println("\n" + fitness + "\n" + tamNodo + "\n",0);
			logResumido.debug("\n" + fitness + "\n" + tamNodo + "\n");
			double valorFinal2 = 0;
			System.out.println("contenedor.instancias.size() 2 :"+contenedor.instancias.size());
			int ev2=contenedor.instancias.size();
			for(int i = 0; i < contenedor.instancias.size(); i++){
				state.output.println(contenedor.instancias.get(i).toString()+" - "+sin.ISobt.get(i)+" - "+sin.mejoresLCP.get(i).size()+" - "+sin.error.get(i)+" - "+sin.no_agrupados.get(i), 0);
				/*
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Numero de conjuntos: " + sin.mejoresLCP.get(i).size(), 0);
				state.output.println("#Error " + sin.error.get(i) + ",ajustado " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				for(int e=0; e<sin.mejoresLCP.get(i).size(); e++) {
				state.output.println("#LCP: " + sin.mejoresLCP.get(i).get(e), 0);
				}
				*/
				valorFinal2 += sin.error.get(i);
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
			}
			System.out.println("\nPromedio de error de entrenamiento es: " + promedioentrenamiento);
			System.out.println("Promedio de error de validacion es: " + valorFinal2/ev2);
			
	}
			//*******************************************
			//*******************todos*******************
			//*******************************************
		/*	
			double dsn1=0;
			double promediototal=0;
			
			for(int i = 0; i<=18; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn1 = dsn1+sin.error.get(i);
			}
			promediototal =promediototal+dsn1; 
			System.out.println("\nPromedio de error de DSN1 es: " + dsn1/19+"\n\n\n");
			
			/////////////////////////////////////////#######################
			
			double dsn2=0;
			
			for(int i = 19; i<=44; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn2 = dsn2+sin.error.get(i);
			}
			promediototal =promediototal+dsn2;
			System.out.println("\nPromedio de error de DSN2 es: " + dsn2/26+"\n\n\n");
			
			
			////////////////////////////////////////////////////////////////
		
			double dsn3=0;
			
			for(int i = 45; i<=73; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn3 = dsn3+sin.error.get(i);
			}
			promediototal =promediototal+dsn3;
			System.out.println("\nPromedio de error de DSN3 es: " + dsn3/29+"\n\n\n");
			
			
			///////////////////////////////////////////////////////////////7
			
			double dsn4=0;
			
			for(int i = 74; i<=111; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn4 = dsn4+sin.error.get(i);
			}
			promediototal =promediototal+dsn4;
			System.out.println("\nPromedio de error de DSN4 es: " + dsn4/38+"\n\n\n");
			
			///////////////////////////////////////////////////////////////

			double dsn5=0;
			
			for(int i = 112; i<=133; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn5 = dsn5+sin.error.get(i);
			}
			promediototal =promediototal+dsn5;
			System.out.println("\nPromedio de error de DSN5 es: " + dsn5/22+"\n\n\n");
			
			//////////////////////////////////////////////////////////////////
		endGenerationTime2 = System.nanoTime();
		state.output.message("#Evaluating duration: " + (endGenerationTime2 - endGenerationTime) / 1000000 + " ms");	//duración evolución en ms
		
		System.out.println("RunsExecuted = 1");
		System.out.println("CPUTime_Mean = "+(double)(endGenerationTime - startGenerationTime) / 1000000000);
		
		System.out.println("Tamaño del nodo: " + ((GPIndividual)ind).size());
		System.out.println("Promedio de error de las " + ev + " instancias de entrenamiento: " + promedioentrenamiento);
		System.out.println("Promedio de error de las instancias DSN1 : " + dsn1/19);
		System.out.println("Promedio de error de las instancias DSN2 : " + dsn2/26);
		System.out.println("Promedio de error de las instancias DSN3 : " + dsn3/29);
		System.out.println("Promedio de error de las instancias DSN4 : " + dsn4/38);
		System.out.println("Promedio de error de las instancias DSN5 : " + dsn5/22);
		System.out.println("Promedio de error de las " + sin.mejoresLCP.size() + " instancias de validacion: " + promediototal/sin.mejoresLCP.size());
		}
	*/
		/*	
		 	double dsn1=0;
			double promediototal=0;
			
			for(int i = 0; i<=10; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn1 = dsn1+sin.error.get(i);
			}
			promediototal =promediototal+dsn1; 
			System.out.println("\nPromedio de error de DSN1 es: " + dsn1/11+"\n\n\n");
			
			/////////////////////////////////////////#######################
			
			double dsn2=0;
			
			for(int i = 11; i<=36; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn2 = dsn2+sin.error.get(i);
			}
			promediototal =promediototal+dsn2;
			System.out.println("\nPromedio de error de DSN2 es: " + dsn2/26+"\n\n\n");
			
			
			////////////////////////////////////////////////////////////////
		
			double dsn3=0;
			
			for(int i = 37; i<=65; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn3 = dsn3+sin.error.get(i);
			}
			promediototal =promediototal+dsn3;
			System.out.println("\nPromedio de error de DSN3 es: " + dsn3/29+"\n\n\n");
			
			
			///////////////////////////////////////////////////////////////7
			
			double dsn4=0;
			
			for(int i = 66; i<=103; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn4 = dsn4+sin.error.get(i);
			}
			promediototal =promediototal+dsn4;
			System.out.println("\nPromedio de error de DSN4 es: " + dsn4/38+"\n\n\n");
			
			///////////////////////////////////////////////////////////////

			double dsn5=0;
			
			for(int i = 104; i<=125; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn5 = dsn5+sin.error.get(i);
			}
			promediototal =promediototal+dsn5;
			System.out.println("\nPromedio de error de DSN5 es: " + dsn5/22+"\n\n\n");
			
			//////////////////////////////////////////////////////////////////
		endGenerationTime2 = System.nanoTime();
		state.output.message("#Evaluating duration: " + (endGenerationTime2 - endGenerationTime) / 1000000 + " ms");	//duración evolución en ms
		
		System.out.println("RunsExecuted = 1");
		System.out.println("CPUTime_Mean = "+(double)(endGenerationTime - startGenerationTime) / 1000000000);
		
		System.out.println("Tamaño del nodo: " + ((GPIndividual)ind).size());
		System.out.println("Promedio de error de las " + ev + " instancias de entrenamiento: " + promedioentrenamiento);
		System.out.println("Promedio de error de las instancias DSN1 : " + dsn1/11);
		System.out.println("Promedio de error de las instancias DSN2 : " + dsn2/26);
		System.out.println("Promedio de error de las instancias DSN3 : " + dsn3/29);
		System.out.println("Promedio de error de las instancias DSN4 : " + dsn4/38);
		System.out.println("Promedio de error de las instancias DSN5 : " + dsn5/22);
		System.out.println("Promedio de error de las " + sin.mejoresLCP.size() + " instancias de validacion: " + promediototal/sin.mejoresLCP.size());
	}
	*/
	/*
	 double dsn1=0;
			double promediototal=0;
			
			for(int i = 0; i<=18; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn1 = dsn1+sin.error.get(i);
			}
			promediototal =promediototal+dsn1; 
			System.out.println("\nPromedio de error de DSN1 es: " + dsn1/19+"\n\n\n");
			
			/////////////////////////////////////////#######################
			
			double dsn2=0;
			
			for(int i = 19; i<=35; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn2 = dsn2+sin.error.get(i);
			}
			promediototal =promediototal+dsn2;
			System.out.println("\nPromedio de error de DSN2 es: " + dsn2/17+"\n\n\n");
			
			
			////////////////////////////////////////////////////////////////
		
			double dsn3=0;
			
			for(int i = 36; i<=64; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn3 = dsn3+sin.error.get(i);
			}
			promediototal =promediototal+dsn3;
			System.out.println("\nPromedio de error de DSN3 es: " + dsn3/29+"\n\n\n");
			
			
			///////////////////////////////////////////////////////////////7
			
			double dsn4=0;
			
			for(int i = 65; i<=102; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn4 = dsn4+sin.error.get(i);
			}
			promediototal =promediototal+dsn4;
			System.out.println("\nPromedio de error de DSN4 es: " + dsn4/38+"\n\n\n");
			
			///////////////////////////////////////////////////////////////

			double dsn5=0;
			
			for(int i = 103; i<=124; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn5 = dsn5+sin.error.get(i);
			}
			promediototal =promediototal+dsn5;
			System.out.println("\nPromedio de error de DSN5 es: " + dsn5/22+"\n\n\n");
			
			//////////////////////////////////////////////////////////////////
		endGenerationTime2 = System.nanoTime();
		state.output.message("#Evaluating duration: " + (endGenerationTime2 - endGenerationTime) / 1000000 + " ms");	//duración evolución en ms
		
		System.out.println("RunsExecuted = 1");
		System.out.println("CPUTime_Mean = "+(double)(endGenerationTime - startGenerationTime) / 1000000000);
		
		System.out.println("Tamaño del nodo: " + ((GPIndividual)ind).size());
		System.out.println("Promedio de error de las " + ev + " instancias de entrenamiento: " + promedioentrenamiento);
		System.out.println("Promedio de error de las instancias DSN1 : " + dsn1/19);
		System.out.println("Promedio de error de las instancias DSN2 : " + dsn2/17);
		System.out.println("Promedio de error de las instancias DSN3 : " + dsn3/29);
		System.out.println("Promedio de error de las instancias DSN4 : " + dsn4/38);
		System.out.println("Promedio de error de las instancias DSN5 : " + dsn5/22);
		System.out.println("Promedio de error de las " + sin.mejoresLCP.size() + " instancias de validacion: " + promediototal/sin.mejoresLCP.size());
	}*/
	/*		
			double dsn1=0;
			double promediototal=0;
			
			for(int i = 0; i<=18; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn1 = dsn1+sin.error.get(i);
			}
			promediototal =promediototal+dsn1; 
			System.out.println("\nPromedio de error de DSN1 es: " + dsn1/19+"\n\n\n");
			
			/////////////////////////////////////////#######################
			
			double dsn2=0;
			
			for(int i = 19; i<=44; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn2 = dsn2+sin.error.get(i);
			}
			promediototal =promediototal+dsn2;
			System.out.println("\nPromedio de error de DSN2 es: " + dsn2/26+"\n\n\n");
			
			
			////////////////////////////////////////////////////////////////
		
			double dsn3=0;
			
			for(int i = 45; i<=63; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn3 = dsn3+sin.error.get(i);
			}
			promediototal =promediototal+dsn3;
			System.out.println("\nPromedio de error de DSN3 es: " + dsn3/19+"\n\n\n");
			
			
			///////////////////////////////////////////////////////////////7
			
			double dsn4=0;
			
			for(int i = 64; i<=101; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn4 = dsn4+sin.error.get(i);
			}
			promediototal =promediototal+dsn4;
			System.out.println("\nPromedio de error de DSN4 es: " + dsn4/38+"\n\n\n");
			
			///////////////////////////////////////////////////////////////

			double dsn5=0;
			
			for(int i = 102; i<=123; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn5 = dsn5+sin.error.get(i);
			}
			promediototal =promediototal+dsn5;
			System.out.println("\nPromedio de error de DSN5 es: " + dsn5/22+"\n\n\n");
			
			//////////////////////////////////////////////////////////////////
		endGenerationTime2 = System.nanoTime();
		state.output.message("#Evaluating duration: " + (endGenerationTime2 - endGenerationTime) / 1000000 + " ms");	//duración evolución en ms
		
		System.out.println("RunsExecuted = 1");
		System.out.println("CPUTime_Mean = "+(double)(endGenerationTime - startGenerationTime) / 1000000000);
		System.out.println("DSN3");
		System.out.println("Tamaño del nodo: " + ((GPIndividual)ind).size());
		System.out.println("Promedio de error de las " + ev + " instancias de entrenamiento: " + promedioentrenamiento);
		System.out.println("Promedio de error de las instancias DSN1 : " + dsn1/19);
		System.out.println("Promedio de error de las instancias DSN2 : " + dsn2/26);
		System.out.println("Promedio de error de las instancias DSN3 : " + dsn3/19);
		System.out.println("Promedio de error de las instancias DSN4 : " + dsn4/38);
		System.out.println("Promedio de error de las instancias DSN5 : " + dsn5/22);
		System.out.println("Promedio de error de las " + sin.mejoresLCP.size() + " instancias de validacion: " + promediototal/sin.mejoresLCP.size());
		}
	 */
		/*
			double dsn1=0;
			double promediototal=0;
			
			for(int i = 0; i<=18; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn1 = dsn1+sin.error.get(i);
			}
			promediototal =promediototal+dsn1; 
			System.out.println("\nPromedio de error de DSN1 es: " + dsn1/19+"\n\n\n");
			
			/////////////////////////////////////////#######################
			
			double dsn2=0;
			
			for(int i = 19; i<=44; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn2 = dsn2+sin.error.get(i);
			}
			promediototal =promediototal+dsn2;
			System.out.println("\nPromedio de error de DSN2 es: " + dsn2/26+"\n\n\n");
			
			
			////////////////////////////////////////////////////////////////
		
			double dsn3=0;
			
			for(int i = 45; i<=73; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn3 = dsn3+sin.error.get(i);
			}
			promediototal =promediototal+dsn3;
			System.out.println("\nPromedio de error de DSN3 es: " + dsn3/29+"\n\n\n");
			
			
			///////////////////////////////////////////////////////////////7
			
			double dsn4=0;
			
			for(int i = 74; i<=99; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn4 = dsn4+sin.error.get(i);
			}
			promediototal =promediototal+dsn4;
			System.out.println("\nPromedio de error de DSN4 es: " + dsn4/26+"\n\n\n");
			
			///////////////////////////////////////////////////////////////

			double dsn5=0;
			
			for(int i = 100; i<=121; i++){

				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn5 = dsn5+sin.error.get(i);
			}
			promediototal =promediototal+dsn5;
			System.out.println("\nPromedio de error de DSN5 es: " + dsn5/22+"\n\n\n");
			
			//////////////////////////////////////////////////////////////////
		endGenerationTime2 = System.nanoTime();
		state.output.message("#Evaluating duration: " + (endGenerationTime2 - endGenerationTime) / 1000000 + " ms");	//duración evolución en ms
		
		System.out.println("RunsExecuted = 1");
		System.out.println("CPUTime_Mean = "+(double)(endGenerationTime - startGenerationTime) / 1000000000);
		
		System.out.println("Tamaño del nodo: " + ((GPIndividual)ind).size());
		System.out.println("Promedio de error de las " + ev + " instancias de entrenamiento: " + promedioentrenamiento);
		System.out.println("Promedio de error de las instancias DSN1 : " + dsn1/19);
		System.out.println("Promedio de error de las instancias DSN2 : " + dsn2/26);
		System.out.println("Promedio de error de las instancias DSN3 : " + dsn3/29);
		System.out.println("Promedio de error de las instancias DSN4 : " + dsn4/26);
		System.out.println("Promedio de error de las instancias DSN5 : " + dsn5/22);
		System.out.println("Promedio de error de las " + sin.mejoresLCP.size() + " instancias de validacion: " + promediototal/sin.mejoresLCP.size());
		}
		*/
		/*	
			double dsn1=0;
			double promediototal=0;
			
			for(int i = 0; i<=18; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn1 = dsn1+sin.error.get(i);
			}
			promediototal =promediototal+dsn1; 
			System.out.println("\nPromedio de error de DSN1 es: " + dsn1/19+"\n\n\n");
			
			/////////////////////////////////////////#######################
			
			double dsn2=0;
			
			for(int i = 19; i<=44; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn2 = dsn2+sin.error.get(i);
			}
			promediototal =promediototal+dsn2;
			System.out.println("\nPromedio de error de DSN2 es: " + dsn2/26+"\n\n\n");
			
			
			////////////////////////////////////////////////////////////////
		
			double dsn3=0;
			
			for(int i = 45; i<=73; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn3 = dsn3+sin.error.get(i);
			}
			promediototal =promediototal+dsn3;
			System.out.println("\nPromedio de error de DSN3 es: " + dsn3/29+"\n\n\n");
			
			
			///////////////////////////////////////////////////////////////7
			
			double dsn4=0;
			
			for(int i = 74; i<=111; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn4 = dsn4+sin.error.get(i);
			}
			promediototal =promediototal+dsn4;
			System.out.println("\nPromedio de error de DSN4 es: " + dsn4/38+"\n\n\n");
			
			///////////////////////////////////////////////////////////////

			double dsn5=0;
			
			for(int i = 112; i<=125; i++){

				//System.out.println("que ondi");
				state.output.println(contenedor.instancias.get(i).toString(), 0);
				state.output.println("#IS obt       : " + sin.ISobt.get(i), 0);
				state.output.println("#Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)),0);
				state.output.println("#No agrupados : " + sin.no_agrupados.get(i), 0);
				//state.output.println("LCP\n" + sin.mejoresLCP.get(i).toString(), 0);
				//state.output.println("LSP\n" + sin.quedaLSP.get(i).toString(), 0);
				state.output.println("#Time: " + sin.time.get(i) + " ms", 0);
				
				if(sin.selogea){
					sin.selogea = false;
					logResumido.debug(contenedor.instancias.get(i).toString());
					logResumido.debug("IS obt       : " + sin.ISobt.get(i) + " / " + contenedor.instancias.get(i).IS_best);
					logResumido.debug("Número de conjuntos: " + sin.mejoresLCP.get(i).size());
					logResumido.debug("Error        : " + sin.error.get(i)  + " , ajustado : " + 1.0/(1.0 + sin.error.get(i)));
					logResumido.debug("No agrupados : " + sin.no_agrupados.get(i));
					//logResumido.debug("LCP\n" + sin.mejoresLCP.get(i).toString());
					//logResumido.debug("LSP\n" + sin.quedaLSP.get(i).toString());
					logResumido.debug("Time: " + sin.time.get(i) + " ms");
					sin.selogea = true;
				}
				
				dsn5 = dsn5+sin.error.get(i);
			}
			promediototal =promediototal+dsn5;
			System.out.println("\nPromedio de error de DSN5 es: " + dsn5/14+"\n\n\n");
			
			//////////////////////////////////////////////////////////////////
		endGenerationTime2 = System.nanoTime();
		state.output.message("#Evaluating duration: " + (endGenerationTime2 - endGenerationTime) / 1000000 + " ms");	//duración evolución en ms
		
		System.out.println("RunsExecuted = 1");
		System.out.println("CPUTime_Mean = "+(double)(endGenerationTime - startGenerationTime) / 1000000000);
		
		System.out.println("Tamaño del nodo: " + ((GPIndividual)ind).size());
		System.out.println("Promedio de error de las " + ev + " instancias de entrenamiento: " + promedioentrenamiento);
		System.out.println("Promedio de error de las instancias DSN1 : " + dsn1/19);
		System.out.println("Promedio de error de las instancias DSN2 : " + dsn2/26);
		System.out.println("Promedio de error de las instancias DSN3 : " + dsn3/29);
		System.out.println("Promedio de error de las instancias DSN4 : " + dsn4/38);
		System.out.println("Promedio de error de las instancias DSN5 : " + dsn5/14);
		System.out.println("Promedio de error de las " + sin.mejoresLCP.size() + " instancias de validacion: " + promediototal/sin.mejoresLCP.size());
		}
		*/	
			
	/* (non-Javadoc)
	 * @see ec.gp.GPProblem#clone()
	 */
	@Override
	public MyProblem clone() {
		MyProblem p = (MyProblem)super.clone();
		p.contenedor = new Contenedor(sin.instancias);
		return p;
	}
	
	
//	public void prepareToEvaluate(final EvolutionState state, final int threadnum){
//		if(state.generation == 0){
//			GPIndividual ind = (GPIndividual) state.population.subpops[0].individuals[0];
//		    LineNumberReader reader = null;
//		    try {
//				reader = new LineNumberReader(new FileReader("bestIndofAll.txt"));
//				ind.readIndividual(state, reader);
//				ind.evaluated = false;
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	
	
	public void finishEvaluating(EvolutionState state, int thread){
		/*if(thread == 0){
			//LOG ESTADISTICA
			MySimpleStatistics s = (MySimpleStatistics) state.statistics;
			GPIndividual indbest = (GPIndividual) s.best_of_run[0];
			GPIndividual indworst = (GPIndividual) s.worst_of_run[0];
			float avg_f = s.avg_of_run[0];
			String log = "";
			if(indbest != null){
				KozaFitness f = ((KozaFitness)indbest.fitness);
				state.output.message("Nodos: "+(indbest.size()));
				log += f.standardizedFitness();
			}
			if(indworst != null){
				KozaFitness f = ((KozaFitness)indworst.fitness);
				log += " " + f.standardizedFitness();
			}
			if(state.generation == 0) 
				{
					log += "best worst avg";
					logEstadistica.info(log);
					
				}
			if(state.generation != 0) log += " " + avg_f;
			if(state.generation != 0) logEstadistica.info(log);
		}
		*/
	}
	
}
