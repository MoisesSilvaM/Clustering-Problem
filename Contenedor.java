import java.io.Serializable;



public class Contenedor implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8976325939990349058L;
	public static final String INSTANCIAS_PATH = ".//dataset//";
	public static String[] instancias_file;
	public static String[] validacion_file;
	public static float[] instancias_SI;
	public static float[] validacion_SI;
//  public static final String[] instancias_file = {"synthetic//50B2C2.data", "synthetic//100B1C2.data", "synthetic//100B3C2.data"};
//	public static final String[] instancias_file = {"synthetic//50B2C2.data", "synthetic//100B1C2.data", "synthetic//100B3C2.data", "test.data", "ruspini.data" , "synthetic//150B2C3.data"};
//	public static final String[] instancias_file = {"test.data", "ruspini.data" , "synthetic//150B2C3.data"};
//	public static final String[] validacion_file = {"test.data", "ruspini.data", "iris.data", "vowel.data","synthetic//150B2C3.data", "synthetic//200B2C4.data", "synthetic//1000B2C2.data","synthetic//3000B1C3.data", "synthetic//3000B3C2.data", "synthetic//5000B2C3.data", "synthetic//5000B3C4.data", "synthetic//10000B1C6.data", "synthetic//10000B2C7.data"};
//	public static final String[] validacion_file = {"test.data", "ruspini.data", "iris.data", "vowel.data","synthetic//150B2C3.data", "synthetic//200B2C4.data", "synthetic//1000B2C2.data", "synthetic//1000B3C3.data"};
//	public static final String[] validacion_file = {"iris.data", "vowel.data", "synthetic//50B2C2.data","synthetic//100B1C2.data", "synthetic//100B3C2.data", "synthetic//200B2C4.data", "synthetic//1000B2C2.data", "synthetic//1000B3C3.data"};
	private boolean isLoad = false;
	public ConjuntoInstancia instancias = new ConjuntoInstancia();
	public int cantidaderrores = 0;
	public static final double alfa = 0.8;
	public static final double beta = 0.2;
	public static final double gama = 0.0;
	
	public Contenedor(ConjuntoInstancia instancias2) {
		instancias = new ConjuntoInstancia(instancias2);
		isLoad = true;
	}


	public Contenedor() {
	}


	public  boolean isLoad(){
		return isLoad;
	}

	public  boolean loadideal(){
		if(!isLoad){
			for (int a=0; a < instancias_file.length; a++) {
				try{
					Instancia ideal = new Instancia(INSTANCIAS_PATH, instancias_file[a], alfa, beta, gama);
					ideal.load();
					instancias.add(ideal);
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					return false;
				}
			}
			isLoad = true;
		}
		return isLoad;
	}	
	
	public  boolean load(){
		if(!isLoad){
			for (int a=0; a < instancias_file.length; a++) {
				try{
					Instancia ins = new Instancia(INSTANCIAS_PATH, instancias_file[a], alfa, beta, gama);
					ins.load();
					instancias.add(ins);
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					return false;
				}
			}
			isLoad = true;
		}
		return isLoad;
	}
	
	
	
	public void loadvalidacion(){
		instancias = new ConjuntoInstancia();
		isLoad = true;
		for (int a=0; a < validacion_file.length; a++) {
			try{
				Instancia ins = new Instancia(INSTANCIAS_PATH, validacion_file[a], alfa, beta, gama);
				ins.IS_best = validacion_SI[a];
				ins.load();
				//System.out.println("que ondi " + validacion_SI[a]);
				instancias.add(ins);
			}
			catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
		
	}
}