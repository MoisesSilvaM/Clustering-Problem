import java.io.Serializable;
import java.util.ArrayList;



public class Singleton implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 535195612641048928L;
	public double fitness = Double.MAX_VALUE;
	public ArrayList<Double> error;
	public ArrayList<Double> no_agrupados;
	public ArrayList<Double> ISobt;
	public ArrayList<Integer> time;
	public ArrayList<SuperConjunto> mejoresLCP;
	public ArrayList<Conjunto> quedaLSP;
	public ConjuntoInstancia instancias = new ConjuntoInstancia();
	public boolean selogea = false;
	public long Add_MindotElapsedTime = 0;
	public long Add_MindotCounter = 0;
	public long Create_CpElapsedTime = 0;
	public long Create_CpCounter = 0;
	public long Join_CpCounter = 0;
	public long Join_CpElapsedTime = 0;
	public long Move_MinElapsedTime = 0;
	public long Move_MinCounter = 0;
	public long Add_MincenterElapsedTime = 0;
	public long Add_MincenterCounter = 0;
	public long Add_MinceElapsedTime = 0;
	public long Add_MinceCounter = 0;
	public long Add_MaxceElapsedTime = 0;
	public long Add_MaxceCounter = 0;
	public long Create_Cp_VecinoElapsedTime = 0;
	public long Create_CP_VecinoCounter = 0;
	public long Delete_CP_minElapsedTime = 0;
	public long Delete_CP_minCounter = 0;
	public long Delete_CP_promElapsedTime = 0;
	public long Delete_CP_promCounter = 0;
	public long Reasigna_CPElapsedTime = 0;
	public long Reasigna_CPCounter = 0;
	public long Delete_CP_densidadElapsedTime = 0;
	public long Delete_CP_densidadCounter = 0;
	public long KmeansElapsedTime = 0;
	public long KmeansCounter = 0;
	
	
	
	
	//SINGLETON SETTING
    private static Singleton instance = null;

    
    private Singleton(){}

    public static synchronized Singleton getInstance(){
        if (instance == null){
            instance = new Singleton();
        }
        return instance;
    }

	public void load(String instanciasPath, String[] instanciasFile, float [] SI, double alfa, double beta, double gama) {
		for (int a=0; a < instanciasFile.length; a++) {
			try{
				Instancia ins = new Instancia(instanciasPath, instanciasFile[a], alfa, beta, gama);
				ins.IS_best = SI[a];
				//validacion_SI = SI_v[a];
				ins.load();
				instancias.add(ins);
				
			}
			catch (Exception e) {
				e.printStackTrace(System.out);
				System.exit(0);
			}
		}
		//System.exit(0);
	}
	
	public void reset(){
		instancias = new ConjuntoInstancia();
		fitness = Double.MAX_VALUE;
		error = new ArrayList<Double>();
		no_agrupados = new ArrayList<Double>();
		mejoresLCP = new ArrayList<SuperConjunto>();
		quedaLSP = new ArrayList<Conjunto>();
	}
	public void resetloop(){
		Delete_CP_densidadCounter = 0;
		Delete_CP_promCounter = 0;
		Delete_CP_minElapsedTime = 0;
		Create_CpCounter = 0;
		Create_CP_VecinoCounter = 0;
	}
}