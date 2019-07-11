import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import sun.security.util.Length;

import java.io.*;
import java.lang.Math;


public class Instancia implements Cloneable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3982708521484437283L;
	private double IS;
	public double IS_best;
	private String filename;
	private String path;
	private boolean isLoad;
	private boolean hasSolution;
	private int dimension;
	private double alfa;
	private double beta;
	private double gama;
	private Conjunto LSP = new ConjuntoLog();//Lista de secuencia de puntos
	private Conjunto LSP_ORI = new Conjunto();//Lista de secuencia de puntos
	public ArrayList<Double> ELSP = new ArrayList<Double>();
	private SuperConjunto LCP = new SuperConjunto();//Lista de conjuntos de puntos
	private Conjunto LCC = new Conjunto();//Lista de centros de conjuntos. Contiene por cada conjunto de LCP, una coordenada que es el centro geométrico de dicho conjunto
	private Punto CE;
	private SuperConjunto LCPideal = new SuperConjunto();//Lista de conjuntos de puntos
	public int numeroPuntos;
	int contarDBSCAN = 0;
	int contarKmeans = 0;
	int contarOMRk = 0;
	int contardeleteprom = 0;
	int contardeletemin = 0;
	int contardeleteden = 0;
	int contarcreateCP = 0;
	int contarcreateV = 0;
	
	//Crea un conjunto con un punto de LSP. Si hay más de X conjuntos vacios se podria evitar esta operación.
	public boolean Create_Cp(){
		if(!isLoad || contarcreateCP>numeroPuntos) return false;
		double n = Math.sqrt(this.LSP_ORI.size());
		if(LCP.size() < n && LSP.size() > 0){
				Conjunto c = new Conjunto();
				c.add(LSP.get(0));
				LSP.remove(0);
				LCP.add(c);
				contarcreateCP++;
				return true;	
		}
		else return false;
	}
		
	public boolean Create_CP_Vecino() {
		if(!isLoad || contarcreateV>numeroPuntos) return false;
		 
		double n = Math.sqrt(this.LSP_ORI.size());
		if(LCP.size() < n && LSP.size() > 0){
		double dist_min_prom=0;
		double instancias_total=0;
		Conjunto auxLSP = new Conjunto();
		for (int i = 0; i < LSP.size()-1; i++) {
			auxLSP.add(LSP.get(i));
			double dist_min = Double.MAX_VALUE;
			for(int u = i+1; u < LSP.size(); u++){
				double dis_medida = 0;
				try {
					dis_medida = LSP.get(i).distancia(LSP.get(u));
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				
				if(dis_medida < dist_min){
					dist_min = dis_medida;
				}
			 
			}
			dist_min_prom = dist_min_prom + dist_min;
			instancias_total++;
		}
		
		dist_min_prom = dist_min_prom/instancias_total;
		//System.out.println("dis min prom : " + dist_min_prom);
		int s=0;
		for (int i = 0; i < LSP.size()-1; i++) {
			Conjunto c = new Conjunto();
			for (int u = i+1; u < LSP.size(); u++) {
				double dis_medida = 0;
				try {
					dis_medida = LSP.get(i).distancia(LSP.get(u));
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				if(dis_medida <= dist_min_prom){
					c.add(LSP.get(u));				
				}			
			}
			if(c.size()>=1) {
				c.add(LSP.get(i));
				LCP.add(c);
				s=1;
				break;
			}
		}
		//226939278
		//juan de dios rivera 713 depto 301, pobl. laguna redonda
		if(s==0) return false; 
		for(Conjunto co :LCP) {
			for(Punto p : co) {
				if(LSP.contains(p)) {
					LSP.remove(p);
				}
			}
		}
		//System.out.println("se crearon: " + (LCP.size()-lcporiginal));	
		//System.out.println("LSP: " + LSP.size()+"\n");
		contarcreateV++;
		return true;
	}
	else return false;
	}
	
				
	
	public Punto Add_Mindot() {
		if(LSP.size() == 0) return null;
		Punto p = LSP.get(0);
		
		if(LCP.size() == 0 || LCP==null||LSP.size()==LSP_ORI.size()){
		return null;
		}
		else{//SI HAY ENTONCES SE INSERTA EN EL DE DISTANCIA MINIMA
			double dis_min = Double.MAX_VALUE;
			Conjunto con_dis_min = null;
			for(Conjunto c : LCP){
				if(c.size() > 0){
					double dis_aux = 0;
				
					try {
						dis_aux = this.d(p, c);
					}
					catch (Exception e) {
						e.printStackTrace(System.out);
						System.exit(0);
					}
				
					if(dis_aux < dis_min){
						dis_min = dis_aux;
						con_dis_min = c;
					}
				}
			}
			
			if(con_dis_min != null){
				con_dis_min.add(p);
				LSP.remove(0);
			}
			else return null;
		}
		return p;
	}
	
	//ADD_NEXTOP: Toma el punto siguiente en LSP y lo inserta en un nuevo conjunto
	public Punto Add_Nextop(){
		if(LSP.size() == 0) return null;
		Punto p = LSP.remove(0);
		
		Conjunto c = new Conjunto();
		c.add(p);
		LCP.add(c);
		
		return p;
	}
	
	
	//ADD_MINCENTER: Toma un punto de LSP y lo inserta en el conjunto LCP donde la distancia al centro del conjunto, dada por LCC, sea mínima.
	public Punto Add_Mincenter(){
		if(LSP.size() == 0) return null;
		Punto p = LSP.get(0);
		
		if(LCP.size() == 0 || LCP==null || LSP.size()==LSP_ORI.size()) return null;
		else{//SI HAY ENTONCES SE INSERTA EN EL DE DISTANCIA MINIMA
			this.updateLCC();
			double dis_min = Double.MAX_VALUE;
			int index = 0;
			for (int i = 0; i < LCC.size(); i++) {
				double dis_medida = 0;
				try {
					dis_medida = LCC.get(i).distancia(p);
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				if(dis_min > dis_medida){
					dis_min = dis_medida;
					index = i;
				}
			}
			LCP.get(index).add(p);
			LSP.remove(p);
			
		}
		
		return p;
	}
	
	
	//ADD_MINCE: Toma un punto de LSP que esté más cercano a CE y lo inserta al conjunto más cercano a CE.
	public Punto Add_Mince(){
		if(LSP.size() == 0 || !isLoad || LCP==null || LCP.size()==0 ) return null;
		this.updateCE();
		double min_dis = Double.MAX_VALUE;
		int index = 0;
		for(int i = 0; i < LSP.size()-1; i++){
			double dis_medida = 0;
			try {
				dis_medida = LSP.get(i).distancia(CE);
			}
			catch (Exception e) {
				e.printStackTrace(System.out);
				System.exit(0);
			}
			if(dis_medida < min_dis){
				min_dis = dis_medida;
				index = i;
			}
		}
		Punto p = LSP.get(index);
		
		if(LCP.size() == 0){
			Conjunto c = new Conjunto();
			c.add(p);
			LCP.add(c);
			LSP.remove(p);
		}
		else{
			Conjunto con_min = null;
			min_dis = Double.MAX_VALUE;
			for(Conjunto c : LCP){
				if(c.size() > 0){
					double dis_medida = 0;
					try {
						dis_medida = this.d(CE, c);
					}
					catch (Exception e) {
						e.printStackTrace(System.out);
						System.exit(0);
					}
					if(dis_medida < min_dis){
						min_dis = dis_medida;
						con_min = c;
					}
				}
			}
			if(con_min != null){
				con_min.add(p);
				LSP.remove(p);
			}
			else return null;
		}
		return p;
	}
	
	
	//ADD_MAXCE: Toma el punto de LSP que esté más lejano a CE y lo inserta al conjunto más lejano de CE
	public Punto Add_Maxce(){
		if(LSP.size() == 0 || !isLoad) return null;
		
		this.updateCE();
		double max_dis = 0;
		int index = 0;
		for(int i = 0; i < LSP.size(); i++){
			double dis_medida = 0;
			try {
				dis_medida = LSP.get(i).distancia(CE);
			}
			catch (Exception e) {
				e.printStackTrace(System.out);
				System.exit(0);
			}
			
			if(dis_medida > max_dis){
				max_dis = dis_medida;
				index = i;
			}
		}
		Punto p = LSP.get(index);
		
		if(LCP.size() == 0){
			Conjunto c = new Conjunto();
			c.add(p);
			LCP.add(c);
			LSP.remove(p);
		}
		else{
			Conjunto con_min = null;
			max_dis = 0;
			for(Conjunto c : LCP){
				if(c.size() > 0){
					double dis_medida = 0;
					try {
						dis_medida = this.d(CE, c);
					}
					catch (Exception e) {
						e.printStackTrace(System.out);
						System.exit(0);
					}
					
					if(dis_medida > max_dis){
						max_dis = dis_medida;
						con_min = c;
					}
				}
			}
			if(con_min != null){
				con_min.add(p);
				LSP.remove(p);
			}
			else return null;
		}
		
		return p;
	}
	

	
	//JOIN_CP: Une conjuntos cercanos. Sus centros CC tienen distancia minima.
	public boolean Join_Cp(){
		updateLCC();
		if(LCP.size() < 3 || LCP==null || LSP.size()==LSP_ORI.size()) return false; //POR QUE 3
		double dis = Double.MAX_VALUE;
		int p1 = -1;
		int p2 = -1;
		for (int i = 0; i < LCC.size()-1; i++) {
			for(int u = i+1; u < LCC.size(); u++){
				double dis_medida = 0;
				try {
					dis_medida = LCC.get(i).distancia(LCC.get(u));
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				
				if(dis_medida < dis){
					dis = dis_medida;
					p1 = i;
					p2 = u;
				}
			}
		}
		
		if(dis != Double.MAX_VALUE){
			try {
				joinCP(p1,p2);
				return true;
			}
			catch (Exception e) {
				return false;
			}
		}
		else
			return false;	
	}
	/*JOIN_CP: Une conjuntos cercanos. Sus centros CC tienen distancia minima.
	public boolean Join_Repeat() throws Exception{
		int p1 = -1;
		int p2 = -1;
		for (int i = 0; i < LCP.size()-1; i++) {
			//System.out.println(" LCP: "+i+" tiene "+ LCP.get(i).size()+ " elementos ");
			for(int u = i+1; u < LCP.size(); u++){		
				for(int e = 0; e < LCP.get(u).size(); e++){
					if(LCP.get(i).contains(LCP.get(u).get(e))){
				//		System.out.println(" encontre uno: "+LCP.get(i)+ " contiene " +LCP.get(u).get(e));
						p1 = i;
						p2 = u;
				//		System.out.println(" guardo: "+p1+ " y "+ p2);
						//break;
					}
				}
			}

		}
			if(p1!=-1) {
			//	System.out.println(" me voy a meter a join con : "+p1+ " y "+ p2);
				joinCP(p1,p2);
				return true;
			}
			else return false;
	}*/
	public boolean Delete_CP_min(){
		if(LCP.size()<3 || LCP==null || LSP.size()==LSP_ORI.size() || contardeletemin > numeroPuntos) return false;
		int p1=-1;
		double conj_min=Double.MAX_VALUE;
		for(int i=0; i<LCP.size();i++) {
			if(conj_min>LCP.get(i).size()) {
				conj_min=LCP.get(i).size();
				p1=i;
			}
		}
		
		if(p1==-1) return false;
		try {
			joinLSP(p1);
			contardeletemin++;
			return true;
			}
		catch (Exception e) {
			return false;
		}
		
	}
	
	public Punto Reasigna_CP() {
		if(LCP.size()<3 || LCP==null || LCP.get(0).size()==0 || LSP.size()==LSP_ORI.size() || LCP.get(0).get(0)==null) return null;
		Punto p = LCP.get(0).get(0);
		Conjunto conaux =new Conjunto();
		int p1=-1;
		double conj_min=Double.MAX_VALUE;
		for(int i=0; i<LCP.size();i++) {
			if(conj_min>LCP.get(i).size()) {
				conj_min=LCP.get(i).size();
				p1=i;
			}
		}
		if(p1==-1) return null;
		for(Punto h : LCP.get(p1)) {
			conaux.add(h);
		}
		LCP.remove(p1);
		
		this.updateLCC();
		for (Punto k :conaux) {
			
			double dis_min = Double.MAX_VALUE;
			int index = 0;
			for (int i = 0; i < LCC.size(); i++) {
				double dis_medida = 0;
				try {
					dis_medida = LCC.get(i).distancia(k);
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				if(dis_min > dis_medida){
					dis_min = dis_medida;
					index = i;
				}
			}
		LCP.get(index).add(k);
		}
		conaux.clear();
//		if(this.fitness() >= currentlyFitness){
//			mejora=false;
//		}
		
		return p;
	}
	
	public boolean Delete_CP_prom(){
		if(LCP.size()<3 || LCP==null || LSP.size()==LSP_ORI.size() || contardeleteprom > numeroPuntos) return false;
		double suma =0;
		double prom=0;
		for(int i=0; i<LCP.size();i++) {
			suma = suma+LCP.get(i).size();
		}
		prom =suma/LCP.size()/2;
		//System.out.println(prom);
		for(int j=LCP.size()-1; j>=0;j--) { 
			if(LCP.get(j).size()<prom) {
				try {
					//System.out.println(LCP.get(j));
					joinLSP(j);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//return false;
				}
			}
		}
		contardeleteprom++;
		return true;
	}
	
	//MOVE_MIN: Se eligen dos conjunto (A, B), donde su CC es mínima, se elige un punto de A tal que la distancia del punto al CC de B sea mínima. El punto se mueve al otro conjunto (B). Si el conjunto A queda vacío, se elimina.
	public boolean Delete_CP_densidad() {
		if(LCP.size()<3 || LCP==null || LSP.size()==LSP_ORI.size() || contardeleteden > numeroPuntos) return false;
		updateLCC();
		int p1=-1;
		double maximo=0;
		for(int i=0; i<LCP.size(); i++) {
			if(LCP.get(i).size() == 0) {
				p1=i;
				break;
			}
			double suma = 0;
			for(Punto p : LCP.get(i)){
				try{
					suma += LCC.get(i).distancia(p);
					}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				
			}
			double resultado = suma / (LCP.get(i).size());
			if(resultado > maximo) {
				maximo = resultado;
				p1 = i;
			}
		}
		if(p1==-1) return false;
		try{
			joinLSP(p1);
			contardeleteden++;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace(System.out);
			System.exit(0);
		}
		return false;
	}
	
	public Punto Move_Min(){
		if(LSP.size() == 0 || LCP==null || LCP.size()<2 || LCC==null) { return null;} 
		Punto q = LSP.get(0);
		updateLCC();
		if(LCC.size()<2 || LCC==null) return null;
		double dis = Double.MAX_VALUE;
		int p1 = -1;
		int p2 = -1;
		for (int i = 0; i < LCC.size()-1; i++) {
			for(int u = i+1; u < LCC.size(); u++){
				double dis_medida = 0;
				try {
					dis_medida = LCC.get(i).distancia(LCC.get(u));
					}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				
				if(dis_medida < dis){
					dis = dis_medida;
					p1 = i;
					p2 = u;
				}
			}
		}
		
		//SE ELIGE EL PUNTO DE p1 QUE ESTA MAS CERCANO AL CC DE p2 Y SE MUEVE A p2 
		if(dis != Double.MAX_VALUE){
			dis = Double.MAX_VALUE;
			Punto p_move = null;
			
			for(Punto p : LCP.get(p1)){
				double dis_medida = 0;
				try {
					dis_medida = p.distancia(LCC.get(p2));
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				
				if(dis_medida < dis){
					dis = dis_medida;
					p_move = p;
				}
			}
			
			moveDot(p_move, LCP.get(p1), LCP.get(p2));
			return q;
		}
		return null;
	}
	
	
	//Si usamos 1 - s_avg(xi) se puede considerar como error ya que cuando e = 0 entonces s = 1, lo cual hace un agrupamiento perfecto.
	public double fitness(){
		double fitness = 1;
		//CALCULA INDICE SILUETA
		double error = 5;
		try {
			error = error();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//SI NO LOS AGRUPA TODOS
		double no_agrupados = noAgrupados();
		//SI HAY UN SOLO CONJUNTO ENTONCES EL ERROR ES 5
		if(LCP.size() != 1) fitness = error + no_agrupados;
		else fitness = 5;
		
		return fitness;
	}
	
	//FITNESS CON MAXIMO DE NODOS
	public double fitness(long nodesize, int totaltime) {

		/*
		long maxnodesize = 21;
		long minnodesize = 7;
		long prom = (maxnodesize + minnodesize)/2;
		double nodesize_indice = 1;
		
		if(nodesize <= maxnodesize && nodesize >= minnodesize) nodesize_indice = 0;
		else{
			nodesize_indice = Math.abs((double)(nodesize-prom))/prom;
		}
		*/
		//CALCULA INDICE SILUETA
		double error =1;
		try {
			error = error();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//SI NO LOS AGRUPA TODOS
		double no_agrupados = noAgrupados();
		double fitness = 1;

		fitness = error + no_agrupados;

		return fitness;
	}

	
	public double noAgrupados() {
		if(LSP_ORI.size() != 0){
			 double salida = LSP.size()*1.0/LSP_ORI.size();
			return salida;
		}
		else return 1;
	}
	
	public double error() {
		double error = 1;
		try {
			if(IS_best == 0.0) {	
				System.out.print("IS_best=0.0");
				return s_avg(); //this.s_avg();
				}
			else
				error = s_avg();
				//System.out.println("error = " + error);
				error = (error - IS_best)/IS_best;
				error = Math.abs(error);
				return error; // IS_best; // best - IS / best

		}
		catch (Exception e) {		
			//System.out.println ("El error es: " + e.getMessage());
			error =1;
		}
		return error;
	}

	
	public double dist_avrg() throws Exception{
		if(!this.isLoad) throw new Exception("La instancia no tiene puntos cargados");
		if(LCP.size() == 0) throw new Exception("No hay grupos procesados");
		//TODO: ES NECESARIO QUE LSP == 0 PARA EVALUAR EL ERROR?
		
		double suma = 0;
		for(int i =0; i<LCP.size();i++){
			for(Punto p : LCP.get(i)){
				suma += c(p, i);//this.s(p);
			}
		}
		return suma;
	}
	
	
	public double getISobt() {
		double error = 1;
		try {
			error = s_avg();
		}
		catch (Exception e) {
			//System.out.println("getISobt() -> error = 1");
			//System.out.println ("El error es: " + e.getMessage());
			error = 1;
		}
		return error;
	}
		
	public void tam(){
		int tam = LSP.size();
		int tamLCP = 0;
		for(Conjunto c : LCP){
			tam += c.size();
			tamLCP += c.size();
		}
		if(tam != 75){
			System.out.println(LSP.size());
			System.out.println(tamLCP);
			System.exit(0);
		}
		
	}

	
	//PROMEDIO DE SILUETA EN TODA LA INSTANCIA
	public double s_avg() throws Exception{
		if(!this.isLoad) throw new Exception("La instancia no tiene puntos cargados");
		if(LCP.size() == 0) throw new Exception("No hay grupos procesados");
		//TODO: ES NECESARIO QUE LSP == 0 PARA EVALUAR EL ERROR?
		
		double suma = 0;
		int count = 0;
		for(Conjunto c : LCP){
			for(Punto p : c){
				suma += s(p);//this.s(p);
				count++;
			}
		}

		//System.exit(0);
		if(count == 0) throw new Exception("Los conjuntos no tienen puntos");
		suma = suma/count;
		return suma;

	}
	
	//INDICE SILUETA
	public double s(Punto xi) throws Exception{
		if(!this.isLoad) throw new Exception("La instancia no tiene puntos cargados");
		if(LCP.size() == 0) throw new Exception("No hay grupos procesados");
		
		double a = this.a(xi);
		double b = this.b(xi);
		double max_ab = Math.max(a, b);
		if(max_ab == 0) throw new Exception("Silueta: a y b no pueden ser cero al mismo tiempo");
		return (b - a)/max_ab;
	}
	
	//USADO EN INDICE SILUETA
	public double b(Punto xi) throws Exception{
		if(!this.isLoad) throw new Exception("La instancia no tiene puntos cargados");
		if(LCP.size() == 0) throw new Exception("No hay grupos procesados");
		
		double min = Double.MAX_VALUE;
		for(Conjunto c : LCP){
			if(c.contains(xi)){
				continue;
			}
			double d = this.d(xi, c);
			if(min > d) min = d;
		}
		
		return min;
	}
	
	//Calcula el prom de distancia de un punto a un conjunto. El punto no debe estar en el conjunto 
	public double d(Punto xi, Conjunto Ct) throws Exception{
		if(!this.isLoad) throw new Exception("La instancia no tiene puntos cargados");
		if(LCP.size() == 0) throw new Exception("No hay grupos procesados");
		// if(Ct.contains(xi)) return Double.MAX_VALUE;
		// if(Ct.contains(xi)) throw new Exception("El Punto elegido esta en el conjunto elegido");
		if(Ct.size() == 0) throw new Exception("El conjunto no tiene elementos");
		
		//SI EL CONJUNTO TIENE UN ELEMENTO ENTONCES DEVUELVE 0 POR QUE CERO?
		if(Ct.size() == 1) return 0;
		
		double suma = 0;
		for(Punto p : Ct){
			suma += xi.distancia(p);
		}
		double resultado = suma / Ct.size();
		return resultado;
	}

	public double a(Punto xi) throws Exception{
		if(!this.isLoad) throw new Exception("La instancia no tiene puntos cargados");
		if(LCP.size() == 0) throw new Exception("No hay grupos procesados");

		//SE BUSCA EL CONJUNTO AL CUAL PERTENECE EL PUNTO Xi
		int index_conjunto = 0;
		boolean encontrado = false;
		for(Conjunto c : LCP){
			for(Punto p : c){
				if(p.equals(xi)){
					encontrado = true;
					break;
				}
			}
			if(encontrado) break;
			index_conjunto++;
		}

		//SI NO SE ENCUENTRA ERROR
		if(!encontrado) throw new Exception("No se encontro el punto para la función a(Xi)");

		Conjunto c = LCP.get(index_conjunto);

		//SI EL CONJUNTO TIENE UN ELEMENTO ENTONCES DEVUELVE 0
		if(c.size() == 1) return 0;
		//SI NO HAY ELEMENTOS => ERROR
		if(c.size() == 0) throw new Exception("El conjunto no tiene elementos");

		double suma = 0;
		for(Punto p : c){
			if( !xi.equals(p)) suma += xi.distancia(p);
		}
		double resultado = suma / (c.size() - 1);
		return resultado;
	}
	
	public double c(Punto xi, int i) throws Exception{
		if(!this.isLoad) throw new Exception("La instancia no tiene puntos cargados");
		if(LCP.size() == 0) throw new Exception("No hay grupos procesados");

		updateLCC();
		Conjunto c = LCP.get(i);
		double dist = 0;
		//SI EL CONJUNTO TIENE UN ELEMENTO ENTONCES DEVUELVE 0
		if(c.size() == 1) return 0;
		//SI NO HAY ELEMENTOS => ERROR
		if(c.size() == 0) throw new Exception("El conjunto no tiene elementos");
		dist = xi.distancia(LCC.get(i));
		//Math.abs(dist);
		//Math.pow(dist,2);
		
		return dist;
	}

	public Instancia(){
		isLoad = false;
	}

	public Instancia(String path, String filename, double alfa, double beta, double gama){
		this.filename = filename;
		this.path = path;
		this.alfa = alfa;
		this.beta = beta;
		this.gama = gama;
		isLoad = false;
	}

	public boolean load() throws Exception{
		if(!isLoad){
			int fila = 0;
			if(filename != null && path != null){
				//String line =null;
				//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				Scanner scanner = null;
				try{
					scanner =  new Scanner(new FileInputStream(path+filename), "UTF8");
					//line = br.readLine();
					//System.out.println("# " + filename);
				}
				catch(IOException e){
					e.printStackTrace(System.out);
					System.exit(0);
				}
				boolean firstline = true;
				int cant = 0;
				//int fila = 0;
				// Numero de puntos
				String line = scanner.nextLine();
				numeroPuntos = Integer.parseInt(line);
				// dimensión
				line = scanner.nextLine();
				dimension = Integer.parseInt(line);
				while (scanner.hasNextLine()){
					// leer coordenadas
					if(fila == numeroPuntos)
						break;
					Punto p = new Punto(dimension);
					//double p2 = 0;
					for (int i = 0; i < dimension; i++) {
						String coord = scanner.next();
						p.componente[i] = Double.parseDouble(coord);
						//System.out.print(coord + " ");
					}
					String belong = scanner.next();
					LSP.add(p);
					LSP_ORI.add(p);
					//int be = 0;
					//String belong = scanner.next();
					//if(belong.equals("Iris-setosa")) be=1;
					//if(belong.equals("Iris-versicolor")) be=2;
					//if(belong.equals("Iris-virginica")) be=3;
					//p2 = Double.parseDouble(belong);
					//System.out.println(p2);
					//ELSP.add(p2);
					fila++;
				}
				//conjuntoideal();
			}

			isLoad = true;
			// Ya no se lee esto
			/*if(hasSolution){
				//CALCULO DEL INDICE SILUETA DE LA INSTANCIA
				HashMap<String, Conjunto> map = new HashMap<String, Conjunto>();
				for(Punto p : LSP){
					if(map.containsKey(p.grupo)){
						map.get(p.grupo).add(p);
					}
					else{
						Conjunto c = new Conjunto();
						c.add(p);
						map.put(p.grupo, c);
					}
				}

				Iterator<String> keyIterator = map.keySet().iterator();
				while(keyIterator.hasNext()){
					String key = keyIterator.next();
					LCP.add(map.get(key));
				}
				*/
				
				//IS = this.s_avg();
				//this.recargar();
				//System.out.println("#IS de la instancia " + this.filename + " es " + this.IS_best + " se cargaron " + fila + " puntos.");
				//System.out.print(LSP);
				//}

		}
		return isLoad;
	}
	
	public void recargar(){
		if(isLoad){
			LCP.clear();
			LSP.clear();
			LSP = new ConjuntoLog(LSP_ORI);
			contarDBSCAN = 0;
			contarKmeans = 0;
			contardeleteprom = 0;
			contardeletemin = 0;
			contardeleteden = 0;
			contarcreateCP = 0;
			contarcreateV = 0;
		}
	}
	
	public void updateLCC(){
		if(!LSP.isChange && !LCC.isChange) return;//SI NINGUN CONJUNTO CAMBIO

		if(LCC.size() > 0) LCC.clear();
		for(Conjunto c : LCP){
			//if(c==null) return; ////////////////////////////////
			int count = 0;
			Punto suma = null;
			try {
				suma = (new Punto(dimension)).zeros();
				//System.out.print(" y "+suma+")");
			}
			catch (Exception e) {
				return;
			}
			
			if(c.size()==1)	{suma=c.get(0);}
			else {
			for(Punto p : c){
				try {
					if(p==null) return;
					suma = suma.suma(p);
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				
				count++;
			}
			if(count != 0){
				try {
					suma = suma.dividir(count);
					}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
					}
				}
			}	
			LCC.add(suma);
		}
		LSP.isChange = false;
		LCP.isChange = false;
	}
	public Punto updateCE(){
		if(isLoad){
			if(!LSP.isChange && !LCC.isChange) return CE;//SI NINGUN CONJUNTO CAMBIO
			
			if(LCP!= null && LCP.size()!= 0 ) {
			updateLCC();}
			else return null;
			
			Punto suma = null;
			//System.out.print(suma);
			
			try {
				suma = (new Punto(dimension)).zeros();
				//suma = suma.zeros();
			}
			catch (Exception e) {
				LSP.isChange = false;
				LCP.isChange = false;
				return null;
			}
			
			int count = 0;
			for(Punto p : LCC){
				try {
					suma = suma.suma(p);
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				
				count++;
			}
			if(count != 0){
				try {
					suma = suma.dividir(count);
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
			}
			
			this.CE = suma;
			LSP.isChange = false;
			LCP.isChange = false;
			
			return this.CE;
		}
		else return null;
	}
	
	//UNE CONJUNTOS
	private void joinCP(int p1, int p2) throws Exception{
		if(p1 < 0 || p1 > LCP.size()-1) new Exception("p1 esta fuera de rango");
		if(p2 < 0 || p2 > LCP.size()-1) new Exception("p2 esta fuera de rango");
		if(p1 == p2) new Exception("p1 y p2 son iguales");
		//System.out.println(" JOIN: "+LCP.get(p2)+ " a "+ LCP.get(p1));
		for(Punto p : LCP.get(p2)){
			if(!LCP.get(p1).contains(p)) 
				LCP.get(p1).add(p);
		}
		
		LCP.remove(p2);
	}
	private void joinLSP(int p1) throws Exception{
		if(p1 < 0 || p1 > LCP.size()-1) new Exception("p1 esta fuera de rango");
		if(LCP.get(p1).size()==0) LCP.remove(p1);
		else {
			for(Punto p : LCP.get(p1)){ 
				LSP.add(p);
			}
			LCP.remove(p1);
		}
	}
	
	//MUEVE UN PUNTO DESDE UN CONJUNTO A OTRO. SI EL CONJUNTO QUEDA VACIO SE ELIMINA
	private void moveDot(Punto punto, Conjunto c_desde, Conjunto c_hasta){
		c_hasta.add(punto);
		c_desde.remove(punto);
		if(c_desde.size() == 0) { 
			LCP.remove(c_desde);
			//updateLCC();
			//LCC.remove(p2);
		}
	}
	public void setFilename(String filename){
		this.filename = filename;
	}
	
	public String getFilename(){
		return this.filename;
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public String getPath(){
		return this.path;
	}
	
	public Conjunto getLSP(){
		return this.LSP;
	}
	
	public Conjunto getLSP_ORI(){
		return this.LSP_ORI;
	}
	
	public SuperConjunto getLCP(){
		return this.LCP;
	}
	
	public double getfitness(){
		return this.fitness();
	}
	
	public String logantes(){
		double error = error();
		double no_agru = noAgrupados();
		return "\nerror "+ error + " ,ajustado " + error*alfa + "\nno agrupado " + no_agru + " ,ajustado "+ no_agru*beta +"\nAntes en LSP\n" + LSP + "\nLCP\n" + LCP;
	}
	
	public String logdespues() {
		double error = error();
		double no_agru = noAgrupados();
		return "\nerror "+ error + " ,ajustado " + error*alfa + "\nno agrupado " + no_agru + " ,ajustado "+ no_agru*beta +"\nDespues en LSP\n" + LSP + "\nLCP\n" + LCP;
	}
	
	public String toString() {
		//if(hasSolution) 
		return "#Instancia " + filename + " IS:" + IS_best;
		//return "Instancia " + filename;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Instancia clone(){
		Instancia copia = null;
		try {
			copia = (Instancia)super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println(" no se puede duplicar");
			System.exit(0);
		}
		
		copia.LSP = new Conjunto(LSP);
		copia.LSP_ORI = new Conjunto(LSP_ORI);
		copia.LCP = new SuperConjunto(LCP);
		copia.LCC = new Conjunto(LCC);
		try {
			copia.CE = CE.copy();
		} catch (Exception e) {
			
		}
		
		return copia;
	}
	public void conjuntoideal() {
		Conjunto a = new Conjunto();
		Conjunto b = new Conjunto();
		Conjunto c = new Conjunto();
		for(int i=0; i<=49;i++) {	
			a.add(LSP_ORI.get(i));
		}
		LCPideal.add(a);
		for(int i=50; i<=99;i++) {
			b.add(LSP_ORI.get(i));			
		}
		LCPideal.add(b);
		for(int i=100; i<=149;i++) {
			c.add(LSP_ORI.get(i));		
		}
		LCPideal.add(c);
	System.out.println("LCPideal");
	System.out.println(LCPideal);
	}
	public void Matriz() {
		int n1 = LCPideal.size();
		int n2 = LCP.size();
		int[][] matriz = new int[n1][n2];
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((CE == null) ? 0 : CE.hashCode());
		result = prime * result + ((LCC == null) ? 0 : LCC.hashCode());
		result = prime * result + ((LCP == null) ? 0 : LCP.hashCode());
		result = prime * result + ((LSP == null) ? 0 : LSP.hashCode());
		result = prime * result + ((LSP_ORI == null) ? 0 : LSP_ORI.hashCode());
		long temp;
		temp = Double.doubleToLongBits(alfa);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(beta);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + dimension;
		result = prime * result
				+ ((filename == null) ? 0 : filename.hashCode());
		result = prime * result + (hasSolution ? 1231 : 1237);
		result = prime * result + (isLoad ? 1231 : 1237);
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instancia other = (Instancia) obj;
		if (CE == null) {
			if (other.CE != null)
				return false;
		} else if (!CE.equals(other.CE))
			return false;
		if (LCC == null) {
			if (other.LCC != null)
				return false;
		} else if (!LCC.equals(other.LCC))
			return false;
		if (LCP == null) {
			if (other.LCP != null)
				return false;
		} else if (!LCP.equals(other.LCP))
			return false;
		if (LSP == null) {
			if (other.LSP != null)
				return false;
		} else if (!LSP.equals(other.LSP))
			return false;
		if (LSP_ORI == null) {
			if (other.LSP_ORI != null)
				return false;
		} else if (!LSP_ORI.equals(other.LSP_ORI))
			return false;
		if (Double.doubleToLongBits(alfa) != Double
				.doubleToLongBits(other.alfa))
			return false;
		if (Double.doubleToLongBits(beta) != Double
				.doubleToLongBits(other.beta))
			return false;
		if (dimension != other.dimension)
			return false;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (hasSolution != other.hasSolution)
			return false;
		if (isLoad != other.isLoad)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	
	public float [] calculateClique(Conjunto grupo) throws Exception {
		float suma = 0;
		float max = 0;
		int punto1 = -1;
		int punto2 = -1;
		for(int p1 = 0; p1 < grupo.size(); p1++)
		{
			for(int p2 = p1 + 1; p2 < grupo.size(); p2++) {
				if(p1 != p2) {
					//System.out.println(p1 + " " + p2);
					double actual = grupo.get(p1).distancia(grupo.get(p2));
					suma += max;
					if (max < actual) {
						punto1 = p1;
						punto2 = p2;
						max = (float) actual;
					}
						
				}
			}
		}
		//System.out.println(suma);
		float [] datos = new float [4];
		if(punto1 != -1) {
			datos[0] = suma; // costo total
			datos[1] = max; // distancia entre los extremos (p1, p2)
			datos[2] = punto1; // p1
			datos[3] = punto2; // p2
		}
		else {
			datos[0] = -1; // costo total
			datos[1] = -1; // distancia entre los extremos (p1, p2)
			datos[2] = -1; // p1
			datos[3] = -1; // p2
		}
		
		return datos;
	}
	// DISJOIN
	public boolean Disjoin(){
		//updateLCC();
		//if(LCC.size() < 2) return;
		// No hay conjuntos no se separan
		if(LCP.size() < 1) return false;

		float [] masCostoso = new float [4];
		float [] costoActual = new float [4];
		masCostoso[0] = 0; // costo total
		masCostoso[1] = 0; // distancia entre los extremos (p1, p2)
		masCostoso[2] = 0; // p1
		masCostoso[3] = 0; // p2

		Conjunto conjuntoParaDisjoin = null;
		for(Conjunto c : LCP){
			//System.out.println("Conjuntos " + c);
			try {
				costoActual = calculateClique(c);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Tiene que tener más de un punto y el costo ser mayor
			if(c.size() > 1 && masCostoso[0] <= costoActual[0]) {
				conjuntoParaDisjoin = c;
				masCostoso = costoActual;

			}

		}
		
		if(conjuntoParaDisjoin != null)
		{
			double costoActualError = error();
			//System.out.println("El mejor " + conjuntoParaDisjoin);
			//System.out.println("su costo " + masCostoso[0]);
			//System.out.println("Tiene " + conjuntoParaDisjoin.size() + " puntos");
			//System.out.println("La distancia max es " + masCostoso[1]);
			//System.out.println("Entre los puntos " + masCostoso[2] + " y " + masCostoso[3]);
			//System.out.println("Número de conjuntos " + LCP.size());
			// Crea nuevo conjunto
			Conjunto nuevo = new Conjunto();
			LCP.add(nuevo);			
			// Cambiamos el p2 hacia el nuevo conjunto
			moveDot(conjuntoParaDisjoin.get((int) masCostoso[3]), conjuntoParaDisjoin, LCP.get(LCP.size() -1));
			// costo de la mitad
			float costoMitad = masCostoso[1]/2;
			if(conjuntoParaDisjoin.size() > 1) 	{
				Punto p1 = conjuntoParaDisjoin.get((int) masCostoso[2]);
				// recorremos pa ir revisando quién se va al nuevo conjunto
				for(int px = 0; px < conjuntoParaDisjoin.size(); px++) {
					if(p1 != conjuntoParaDisjoin.get(px)) {
						// Si es mayor al costo la mitad se va al nuevo conjunto
						double dist = 0;
						try {
							dist = conjuntoParaDisjoin.get(px).distancia(p1);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(dist > costoMitad) {
							moveDot(conjuntoParaDisjoin.get(px), conjuntoParaDisjoin, LCP.get(LCP.size() -1));
						}							
					}						
				}
				//System.out.println(costoActualError +" > " + error());
				if(costoActualError > error()) {
					//System.out.println("yes");
						return true;
					}
				else {
					Conjunto devolver = LCP.get(LCP.size() -1);
					for(int px = 0; px < devolver.size(); px++)
						moveDot(devolver.get(px), devolver, conjuntoParaDisjoin);
					return false;
				}
			}
			else
				return false;	
		}
		else
			return false;			
	}

	/* KMeans(k) 
	  
	  Comments (C) and Requirements (R)
	   - (R) LSP list must be full (all objects available) OR the source must be updated 
	   - (R) Param int k :: if (k = -1) randon k groups (2 <= k <= sqrt(n))
	   - (C) Heuristic algorithm (depends on inicialization (initial groups))
	   
	   Algorithm
	   1 - Select randomly k objects. They will be initial group (and centroids).
	   2 - Assign each object to the group that has the closest centroid.
	   3 - When all objects have been assigned, recalculate the positions of the K centroids.
	   4 - Repeat Steps 2 and 3 until the centroids no longer move. 
	 */	
	public boolean OMRk() {
		if(LCP.size() < 3 || LSP.size()==LSP_ORI.size() || LSP.size()!=0 || contarOMRk > 2) return false;
		contarOMRk++;
		int k = LCP.size();
		if(OMRk2(k+1)) return true;
		if(OMRk2(k-1)) return true;
		if(OMRk2(k+2)) return true;
		if(OMRk2(k-2)) return true;
		return false;
	}
	public boolean OMRk2(int k) {

			// Only it starts as initial terminal
			Conjunto auxLSP = null;
			SuperConjunto auxLCP = null;
			double currentlyFitness = this.fitness();
			boolean improve = false;
			if(LSP.size() == 0)
			{
				improve = true;
				///////////////////////////////////////////////////////////////////////////77
				//System.out.println("actual " + currentlyFitness);
				///////////////////////////////////////////////////////////////////////////7777
				auxLSP = new ConjuntoLog();
				auxLCP = new SuperConjunto();
				for(int i = 0; i < LSP.size(); i++) {
					//auxLSP.set(i, LSP.get(i));
					auxLSP.add(i, LSP.get(i));
				}

				for(int i = 0; i < LCP.size(); i++) {
					auxLCP.add(i, LCP.get(i));
					}

				LSP.clear();
				for(int i = 0; i < LSP_ORI.size(); i++)
					LSP.add(i, LSP_ORI.get(i));
				LCP.clear();
			}

			int auxIdxP = -1, auxCount = 0, auxIdxSet = -1;
			double auxDist = -1, auxDistMin = -1;
			Punto auxP = null;
			Conjunto auxSet = null;
			Random r = new Random();
			
			//--------------------------------------------------------------------------------------------------------
			//k objects will be randomly selected from a set LSP of n objects
			//Obter de maneira aleat�ria k objetos que ir�o formar k grupos iniciais
			for(int i = 0; i < k; i++){		
				//Select an object
				auxIdxP = r.nextInt(LSP.size());
				auxP = LSP.get(auxIdxP);
				// Create a new set 
				auxSet = new Conjunto();
				LCP.add(auxSet);
				LCP.get(i).add(auxP); 
				LSP.remove(auxP);
				//clear!
				auxP = null;
				auxSet = null;
			}
					
			//--------------------------------------------------------------------------------------------------------
			//The remaining n - k objects must be inserted to the nearest cluster (initial object)
			//Os demais n - k objetos devem se unir ao grupo mais pr�ximo.
			auxCount = LSP.size();
			
			for(int i = auxCount - 1; i >= 0; i--){
				
				auxP = LSP.get(i);
				auxIdxSet = 0;

				try {
					auxDist = LCP.get(0).get(0).distancia(auxP);
					auxDistMin = auxDist;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//finding the nearest cluster -Verificar o grupo mais pr�ximo
				for(int j = 1; j < k; j++){
					try {
						auxDist = LCP.get(j).get(0).distancia(auxP);
					} catch (Exception e) {
						e.printStackTrace();
					}
					//Min distance (nearest cluster)
					if (auxDist < auxDistMin){
						auxIdxSet = j;
						auxDistMin = auxDist;
					}
				}
				
				//Colocar o objeto i no grupo j
				//allocate object i into cluster j
				LCP.get(auxIdxSet).add(auxP); 
				LSP.remove(auxP);
				//clear!
				auxP = null;
			}
				
			//Calculate the new centroids - Calcular centroide dos grupos e realocar os objetos
			
			//Matriz de vizinhos com base na distancia
			ArrayList< ArrayList <Double> > centroid = new ArrayList<ArrayList<Double>>();
			//Calcular centroides
			this.updateLCC();
			
			//Initialize
			for (int i = 0; i < LCC.size(); i++){
				centroid.add(new ArrayList<Double>());
				for (int j = 0; j < LCC.get(0).componente.length; j++){
				  centroid.get(i).add((double) 0);	
				}
			}
			
			//--------------------------------------------------------------------------------
			int auxContador = 0;
			boolean auxNewCentroides = true;
			//Max five iteractions OR the same centroids (do not updated)
			while ((auxNewCentroides) && (auxContador <= 20)) {  
				auxNewCentroides = false;
				
				//Remove the objects aiming to form new clusters - Remover objetos dos grupos e remover os grupos
				for (int i = k - 1; i >= 0; i--){			
					auxCount = LCP.get(i).size(); 
					for (int j = auxCount - 1; j >= 0; j--){
						LSP.add(LCP.get(i).get(j)); 
						LCP.get(i).remove(j);	
					}
				}
				
				//The remaining n - k objects must be inserted to the nearest cluster (centroid)
				//Cada objeto deve ir para o centroide mais pr�ximo
				auxCount = LSP.size();
				for(int i = auxCount - 1; i >= 0; i--){
					
					auxP = LSP.get(i);
					auxIdxSet = 0;
		
					try {
						auxDist = LCC.get(0).distancia(auxP);
						auxDistMin = auxDist;
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//Verificar o grupo mais pr�ximo
					//finding the nearest cluster
					for(int j = 1; j < k; j++){
						try {
							auxDist = LCC.get(j).distancia(auxP);
						} catch (Exception e) {
							e.printStackTrace();
						}
		
						if (auxDist < auxDistMin){
							auxIdxSet = j;
							auxDistMin = auxDist;
						}
					}
					
					//allocate object i into cluster j - Colocar o objeto i no grupo j
					LCP.get(auxIdxSet).add(auxP); 
					LSP.remove(auxP);
					//clear!
					auxP = null;
				}	
				
				//System.out.println("LCP depois" + LCP.toString());
				
				
				//Save the centroids before the update - Armazenar centroides antes da atualizacao
				for (int i = 0; i < LCC.size(); i++){
					for (int j = 0; j < LCC.get(0).componente.length; j++){
						//System.out.println(" LCC[" + i + "," + j + "]=" + LCC.get(i).componente[j]);
						centroid.get(i).set(j, LCC.get(i).componente[j]);	
					}
				}
				//Update centroids - Atualizar centroides
				this.updateLCC();
				
				//centroids were updated? - Os centroides mudaram?
				for (int i = 0; i < LCC.size(); i++){
					for (int j = 0; j < LCC.get(0).componente.length; j++){
						//System.out.println("C[" + i + "," + j + "]=" + centroides.get(i).get(j) + " LCC[" + i + "," + j + "]=" + LCC.get(i).componente[j]);
						if (centroid.get(i).get(j) != LCC.get(i).componente[j]) {
							auxNewCentroides = true;
						}
					}
					if (auxNewCentroides) {
						//System.out.println("------------------ novos centroides");
						break;
					}
				}
				
				auxContador++;
			}
			//contarKmeans++;		
			if(improve){
				//System.out.println("nueva " + this.fitness() + " "  + LCP.size() );
				if(this.fitness() < currentlyFitness){
					LSP.clear();
					LCP.clear();
					//System.out.println("reset " + this.fitness() );
					for(int i = 0; i < auxLSP.size(); i++) {
						LSP.add(i, auxLSP.get(i));
					}
					for(int i = 0; i < auxLCP.size(); i++) {
						LCP.add(i, auxLCP.get(i));
					//System.out.println("se volvio " + this.fitness() + " "  + LCP.size() );
					}
					return false;
					}	
				else return true;
			}
			return false;
		}	
	public boolean KMeans(int pK){

		if(LCP.size() == 0 || LSP.size()==LSP_ORI.size() || LSP.size()!=0) return false;
		pK = LCP.size();
		if(pK == 1) pK++;
					
		if(contarKmeans > numeroPuntos){
			//System.out.println("entre Kmeans : " + contarKmeans);
			contarKmeans++;
			return false;
		}
		// Only it starts as initial terminal
		Conjunto auxLSP = null;
		SuperConjunto auxLCP = null;
		double currentlyFitness = this.fitness();
		boolean improve = false;
		if(LSP.size() != LSP_ORI.size())
		{
			improve = true;
			//System.out.println("actual " + currentlyFitness);
			auxLSP = new ConjuntoLog();
			auxLCP = new SuperConjunto();
			for(int i = 0; i < LSP.size(); i++) {
				//auxLSP.set(i, LSP.get(i));
				auxLSP.add(i, LSP.get(i));
			}

			for(int i = 0; i < LCP.size(); i++) {
				auxLCP.add(i, LCP.get(i));
				}

			LSP.clear();
			for(int i = 0; i < LSP_ORI.size(); i++)
				LSP.add(i, LSP_ORI.get(i));
			LCP.clear();
			
			//System.out.println(LCP.size());
			//System.out.println(LSP.size());
			//return 0;
		}

		//randon k groups (2 <= k <= sqrt(n)) 
		int k = pK;
		int auxIdxP = -1, auxCount = 0, auxIdxSet = -1;
		double auxDist = -1, auxDistMin = -1;
		Punto auxP = null;
		Conjunto auxSet = null;
		Random r = new Random();
		
		//I need to verify these attributes in other methods (DBSCAN, DistK, Hopkins)
		/*
		LCC.isChange = true;
		LSP.isChange = true;
		LCP.isChange = true;
		*/
		//Clusters: 2 <= k <= sqrt(n)
		if (pK == -1){
			k = (int) java.lang.Math.sqrt((double)LSP.size());
			k = r.nextInt(k - 1) + 2;
		}
		
		//System.out.println("k="+k);
		
		//--------------------------------------------------------------------------------------------------------
		//k objects will be randomly selected from a set LSP of n objects
		//Obter de maneira aleat�ria k objetos que ir�o formar k grupos iniciais
		for(int i = 0; i < k; i++){		
			//Select an object
			auxIdxP = r.nextInt(LSP.size());
			auxP = LSP.get(auxIdxP);
			// Create a new set 
			auxSet = new Conjunto();
			LCP.add(auxSet);
			LCP.get(i).add(auxP); 
			LSP.remove(auxP);
			//clear!
			auxP = null;
			auxSet = null;
		}
				
		//--------------------------------------------------------------------------------------------------------
		//The remaining n - k objects must be inserted to the nearest cluster (initial object)
		//Os demais n - k objetos devem se unir ao grupo mais pr�ximo.
		auxCount = LSP.size();
		
		for(int i = auxCount - 1; i >= 0; i--){
			
			auxP = LSP.get(i);
			auxIdxSet = 0;

			try {
				auxDist = LCP.get(0).get(0).distancia(auxP);
				auxDistMin = auxDist;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//finding the nearest cluster -Verificar o grupo mais pr�ximo
			for(int j = 1; j < k; j++){
				try {
					auxDist = LCP.get(j).get(0).distancia(auxP);
				} catch (Exception e) {
					e.printStackTrace();
				}

				//Min distance (nearest cluster)
				if (auxDist < auxDistMin){
					auxIdxSet = j;
					auxDistMin = auxDist;
				}
			}
			
			//Colocar o objeto i no grupo j
			//allocate object i into cluster j
			LCP.get(auxIdxSet).add(auxP); 
			LSP.remove(auxP);
			//clear!
			auxP = null;
		}
			
		//Calculate the new centroids - Calcular centroide dos grupos e realocar os objetos
		
		//Matriz de vizinhos com base na distancia
		ArrayList< ArrayList <Double> > centroid = new ArrayList<ArrayList<Double>>();
		//Calcular centroides
		this.updateLCC();
		
		//Initialize
		for (int i = 0; i < LCC.size(); i++){
			centroid.add(new ArrayList<Double>());
			for (int j = 0; j < LCC.get(0).componente.length; j++){
			  centroid.get(i).add((double) 0);	
			}
		}
		
		//--------------------------------------------------------------------------------
		int auxContador = 0;
		boolean auxNewCentroides = true;
		//Max five iteractions OR the same centroids (do not updated)
		while ((auxNewCentroides) && (auxContador <= 10)) {  
			auxNewCentroides = false;
			
			//System.out.println("auxContador=" + auxContador + " LCP antes" + LCP.toString());
			
			//Remove the objects aiming to form new clusters - Remover objetos dos grupos e remover os grupos
			for (int i = k - 1; i >= 0; i--){			
				auxCount = LCP.get(i).size(); 
				for (int j = auxCount - 1; j >= 0; j--){
					LSP.add(LCP.get(i).get(j)); 
					LCP.get(i).remove(j);	
				}
			}
			
			//The remaining n - k objects must be inserted to the nearest cluster (centroid)
			//Cada objeto deve ir para o centroide mais pr�ximo
			auxCount = LSP.size();
			for(int i = auxCount - 1; i >= 0; i--){
				
				auxP = LSP.get(i);
				auxIdxSet = 0;
	
				try {
					auxDist = LCC.get(0).distancia(auxP);
					auxDistMin = auxDist;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//Verificar o grupo mais pr�ximo
				//finding the nearest cluster
				for(int j = 1; j < k; j++){
					try {
						auxDist = LCC.get(j).distancia(auxP);
					} catch (Exception e) {
						e.printStackTrace();
					}
	
					if (auxDist < auxDistMin){
						auxIdxSet = j;
						auxDistMin = auxDist;
					}
				}
				
				//allocate object i into cluster j - Colocar o objeto i no grupo j
				LCP.get(auxIdxSet).add(auxP); 
				LSP.remove(auxP);
				//clear!
				auxP = null;
			}	
			
			//System.out.println("LCP depois" + LCP.toString());
			
			
			//Save the centroids before the update - Armazenar centroides antes da atualizacao
			for (int i = 0; i < LCC.size(); i++){
				for (int j = 0; j < LCC.get(0).componente.length; j++){
					//System.out.println(" LCC[" + i + "," + j + "]=" + LCC.get(i).componente[j]);
					centroid.get(i).set(j, LCC.get(i).componente[j]);	
				}
			}
			//Update centroids - Atualizar centroides
			this.updateLCC();
			
			//centroids were updated? - Os centroides mudaram?
			for (int i = 0; i < LCC.size(); i++){
				for (int j = 0; j < LCC.get(0).componente.length; j++){
					//System.out.println("C[" + i + "," + j + "]=" + centroides.get(i).get(j) + " LCC[" + i + "," + j + "]=" + LCC.get(i).componente[j]);
					if (centroid.get(i).get(j) != LCC.get(i).componente[j]) {
						auxNewCentroides = true;
					}
				}
				if (auxNewCentroides) {
					//System.out.println("------------------ novos centroides");
					break;
				}
			}
			
			auxContador++;
		}
		contarKmeans++;		
		if(improve)
		{
			//System.out.println("nueva " + this.fitness() + " "  + LCP.size() );
			if(this.fitness() > currentlyFitness)
			{
				LSP.clear();
				LCP.clear();
				//System.out.println("reset " + this.fitness() );
				for(int i = 0; i < auxLSP.size(); i++) {
					LSP.add(i, auxLSP.get(i));
				}
				for(int i = 0; i < auxLCP.size(); i++) {
					LCP.add(i, auxLCP.get(i));
				//System.out.println("se volvió " + this.fitness() + " "  + LCP.size() );
				}
				return false;
				}	
			else return true;
		}
		return false;
	}

	/*
	 * DBSCAN(k*, dist) :: Density-Based Spatial Clustering of Applications with Noise
	  Paper: http://www.dbs.ifi.lmu.de/Publikationen/Papers/KDD-96.final.frame.pdf
	 
	  Comments (C) and Requirements (R)
	   - (R) LSP list must be full (all objects available) 
	   - (R) Params kNeighbor and distance (see Distk Method description)
	   - (C) Constructive deterministic algorithm (it is not heuristic!)
	  	 */

	public boolean DBSCAN(){
		if(LCP.size() == 0 || LSP.size()==LSP_ORI.size() || LSP.size()!=0) return false;
		int kNeighbor = 5;
		double distance = 0;
		
		// Only it starts as initial terminal
		Conjunto auxLSP = null;
		SuperConjunto auxLCP = null;
		double currentlyFitness = this.fitness();
		boolean improve = false;
		
		
		//System.out.println("lala " + contarDBSCAN);
		
		if(contarDBSCAN > numeroPuntos)
			{
				//System.out.println("no entre DBSCAN :( " + contarDBSCAN);
				contarDBSCAN++;
				return false;
			}
		//System.out.println("entre lala " + contarDBSCAN);
		
		if(LSP.size() != LSP_ORI.size()){
			improve = true;
		
			//System.out.println("actual " + currentlyFitness);
			auxLSP = new ConjuntoLog();
			auxLCP = new SuperConjunto();
			for(int i = 0; i < LSP.size(); i++)
				auxLSP.add(LSP.get(i));

			for(int i = 0; i < LCP.size(); i++)
				auxLCP.add(i, LCP.get(i));

			LSP.clear();
			for(int i = 0; i < LSP_ORI.size(); i++)
				LSP.add(i, LSP_ORI.get(i));
			LCP.clear();
			
			//System.out.println(LCP.size());
			//System.out.println(LSP.size());
			//return 0;
		}
		// DistK(k*) :: Calibrate params of DBSCAN
		//------------------------------------------------------------------------------
		distance = this.DistK(kNeighbor);

		//System.out.println("Rule 1: " + distance); 
	
		double auxDistance = 0;
		//k groups
		int k = 0;
		
		//Matriz de vizinhos com base na distancia
		//Matrix indicates if objects i and j are Neighbors (matrix.get(i).get(j)==true) based on the distance
		ArrayList< ArrayList <Boolean> > matrix = new ArrayList<ArrayList<Boolean>>();
		
		//Density (for each object)
		//Densidade de cada objeto com base na distancia
		ArrayList< Integer > density = new ArrayList<Integer>();
		
		//temporary vector (solution groupnumber) - vetor de solucao (temporario)
		ArrayList< Integer > groupNumber = new ArrayList<Integer>();
		
		//Inicialization
		for (int i = 0; i < LSP.size(); i++){
			groupNumber.add(-1);
			density.add(1);
			matrix.add(new ArrayList<Boolean>());
			for (int j = 0; j < LSP.size(); j++){
			  matrix.get(i).add(false);	
			}
		}
		
		//Preencher matrizes com vizinhos e calcular densidade
		//fill the matrix
		for (int i = 0; i < LSP.size(); i++){
			//Main diagonal must be true [i,i] = true!
			matrix.get(i).set(i, true);
			for (int j = i + 1; j < LSP.size(); j++){
				try {
					//Calculate de distance (auxDistance) of objects i and j
					auxDistance = LSP.get(i).distancia(LSP.get(j));
					//System.out.println("auxDistance[" + i + "," + j + "]" + auxDistance);
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				
				//Verify if the objects must be setted as neighbors
				if (auxDistance <= distance){
					//Yes, they are neighbors
					matrix.get(i).set(j, true);
					matrix.get(j).set(i, true);
					//density++ for these objects
					density.set(i, density.get(i) + 1);
					density.set(j, density.get(j) + 1);
				}
			}
		}

		//For each available object (LSP)
		for (int i = 0; i < LSP.size(); i++){
			//Is this object is core?
			if (density.get(i).intValue() >= kNeighbor) {
				//Verify if this object must create a new group 
				if (groupNumber.get(i) == -1){
					groupNumber.set(i, k);
					k++;
				}
				//Your neighbors (core or (density-)reachable) must be allocated into the same cluster
				for (int j = i + 1; j < LSP.size(); j++){
					//is neighbor?
					if (matrix.get(i).get(j)){
						//joining!
						groupNumber.set(j, groupNumber.get(i));
					}
				}
			}
		}
		
		//outlier objects create new singleton groups - Ru�do: grupos singletons
		for (int i = 0; i < LSP.size(); i++){
			if (density.get(i).intValue() < kNeighbor) {
				if (groupNumber.get(i) == -1){
					groupNumber.set(i, k);
					k++;
				}				
			}
		}
		
		Conjunto auxSet;
		Punto auxP;
		
		//Create solution LCP based on groupNumber solution
		for(int i = 0; i < k; i++){		
			auxSet = new Conjunto();
			LCP.add(auxSet);
			auxSet = null;
		}
		
		//Insert objects into groups
		for(int i = 0; i < LSP.size(); i++){	
			auxP = LSP.get(i);
			LCP.get(groupNumber.get(i)).add(auxP); 
			auxP = null;
		}
		LSP.clear();
				
		//Show density and solution -------------------------------------------------
		//System.out.println("   Density:" + density.toString());
		//System.out.println("  Solution:" + groupNumber.toString());
		//System.out.println("Groups (k): "+ k);
		//System.out.println(LCP.toString());
		
		//memory..
		groupNumber.clear();
		density.clear();
		groupNumber = null;
		density = null;
		for (int i = 0; i < LSP.size(); i++){
			matrix.get(i).clear();
			matrix.set(i, null);
		}		
		matrix = null;		
		
		contarDBSCAN++;
		if(improve)	{
			//System.out.println("nueva " + this.fitness() );
			if(this.fitness() > currentlyFitness){
				LSP.clear();
				LCP.clear();
				//System.out.println("reset " + this.fitness() );
				for(int i = 0; i < auxLSP.size(); i++)
					LSP.add(i, auxLSP.get(i));

				for(int i = 0; i < auxLCP.size(); i++)
					LCP.add(i, auxLCP.get(i));
				//System.out.println("se volvió " + this.fitness() );
				return false;
			}
			else return true;
		}
		else return false;	
	}
	/*DistK(k*): Calibrate params of DBSCAN
	  Reference (MRDBSCAN): my thesis (page 39)
	  
	  Comments (C) and Requirements (R)
	   - (R) LSP list must be full (all objects available) OR must be updated (specific points)
	   - (R) Param int kNearestNeighbor: {3, 4, 5, ..., 10}
	   - (C) This method identifies the distance of the k*-nearest neighbor (for the each object).
	   - (C) This way k* must be submitted as parameter. 
	   - (C) The distances are stored in a vector, and the method implements FOUR rules aiming 
	         identify the distance param of DBSCAN. The Rule Higher distance was considered.
	 */	
	public double DistK(int kNearestNeighbor){ 
		
		// Only it starts as initial terminal
		if(LSP.size() != LSP_ORI.size())
			return 0;
		//variables about the commented rules
		//public double[] DistK(int kNearestNeighbor){
		//double dist[] = new double[4];
		//int auxI = 0;
		//double auxDiff = 0, auxDiffMaior = -1;
		
		ArrayList< ArrayList <Double> > matrix = new ArrayList<ArrayList<Double>>();
		ArrayList< Double > auxDist = new ArrayList<Double>();
		double auxDistance = 0;

		//For each object: calculate and order the distances
		for (int i = 0; i < LSP.size(); i++){
			auxDist.clear();
			for (int j = 0; j < LSP.size(); j++){
				try {
					auxDistance = LSP.get(i).distancia(LSP.get(j));
					//System.out.println("auxDistance[" + i + "," + j + "]" + auxDistance);
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
					System.exit(0);
				}
				auxDist.add(auxDistance);		
			}
			//distances between object i and all points in the set LSP.
			Collections.sort(auxDist);				
			//Matrix with LSP.size() lists.. where matrix.get(i)
			//has distances between object i and all points in the set LSP
			matrix.add(new ArrayList<Double>());
			matrix.get(i).addAll(auxDist);
		}
		
		//auxDist must have the distance of the k-Nearest Neighbor of each object
		auxDist.clear();
		for (int i = 0; i < LSP.size(); i++){
			auxDist.add(matrix.get(i).get(kNearestNeighbor));
		}
		//thinking about the four rules
		Collections.sort(auxDist);

		//Here there are four rules aiming to identify the distance param of DBSCAN, 
		//based on kNearestNeighbor (submitted)
		auxDistance  = 0;
		
		//Rule higher 
		auxDistance = auxDist.get(auxDist.size() - 1);

		/* Drafts: rules #############################################################################
		//Mediana: ---------------------------------------------------------------
		auxDistance = auxDist.get(auxDist.size()/2);
		dist[0] = auxDistance;
	 
		//Pico10: 10 parts and max the diference: part(i + 1) - part(i)
		// Distance will be  (part(i + 1) + part(i) ) / 2
		auxI = auxDist.size() / 10;	
		auxDistance  = -1;
		auxDiff = 0; 
		auxDiffMaior = -1;
		if ( auxI > 2 ) {
			for (int i = 0; i < 10; i++){
				auxDiff = auxDist.get((i * auxI) + auxI - 1) - auxDist.get(i * auxI);
				if (auxDiffMaior < auxDiff) {
					auxDiffMaior = auxDiff;
					auxDistance = (auxDist.get(i + 1) + auxDist.get(i)) /2;
				}
			}
		}
		dist[2] = auxDistance;
				
		//Pico20: same idea Pico10
		auxI = auxDist.size() / 20;	
		auxDistance  = -1;
		auxDiff = 0; 
		auxDiffMaior = -1;
		if ( auxI > 2 ) {
			for (int i = 0; i < 20; i++){
				auxDiff = auxDist.get((i * auxI) + auxI - 1) - auxDist.get(i * auxI);
				if (auxDiffMaior < auxDiff) {
					auxDiffMaior = auxDiff;
					auxDistance = (auxDist.get(i + 1) + auxDist.get(i)) /2;
				}
			}
		}
		dist[3] = auxDistance;
	   */
		
		//free memory
		for (int i = 0; i < LSP.size(); i++){
			matrix.get(i).clear();
			matrix.set(i, null);
		}
		auxDist.clear();
		auxDist = null;
		matrix.clear();
		matrix = null;		
		
		//Rule higher 
		return auxDistance;
	}
	
}
