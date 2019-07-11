class test_instancia {
	public static void main(String[] args) throws Exception{
		//String[] instancias = {"test.data"};
		String[] instancias = {"iris.data", "ruspini.data", "vowel.data"};
		for (int a=0; a < instancias.length; a++) {
			Instancia ins = new Instancia("dataset/", instancias[a], 0.5, 0.5, 0);
			try{
				ins.load();
			}
			catch (Exception e) {
				e.printStackTrace(System.out);
			}
			
			Punto p1 = ins.getLSP().get(0).copy();
// 			Punto p2 = ins.getLSP().get(3).copy();
			
			double suma = 0;
			
			for(Punto p : ins.getLSP()){
				for (int i = 0; i < p.componente.length; i++) {
					System.out.print(p.componente[i]+",");
				}
				if(p.hasSolution) System.out.print(p.grupo);
				System.out.print(",DIS: "+ p1.distancia(p));
				suma += p1.distancia(p);
				System.out.println(",EQUAL: "+ (p1.equals(p)));
			}
			
			// ins.dividirDIB1();
// 			
// 			System.out.println("SE DIVIDIO EN dividirDIB1");
// 			
// 			int count = 0;
// 			for(Conjunto c : ins.getLCP()){
// 				for(Punto p : c.getConjunto()){
// 					System.out.print("C" + count + ", ");
// 					for (int i = 0; i < p.componente.length; i++) {
// 						System.out.print(p.componente[i]+",");
// 					}
// 					
// 					System.out.print(", a(Xi):" + ins.a(p));
// 					System.out.print(", b(Xi): " + ins.b(p));
// 					System.out.println(", s(Xi): " + ins.s(p));
// 				}
// 				count++;
// 			}
// 			
// 			System.out.println("ERROR: " + ins.fitness());
// 			
// 			ins.recargar();
// 			ins.dividirDIB2();
// 			System.out.println("SE DIVIDIO EN dividirDIB2");
// 			
// 			count = 0;
// 			for(Conjunto c : ins.getLCP()){
// 				for(Punto p : c.getConjunto()){
// 					System.out.print("C" + count + ", ");
// 					for (int i = 0; i < p.componente.length; i++) {
// 						System.out.print(p.componente[i]+",");
// 					}
// 					
// 					System.out.print(", a(Xi):" + ins.a(p));
// 					System.out.print(", b(Xi): " + ins.b(p));
// 					System.out.println(", s(Xi): " + ins.s(p));
// 				}
// 				count++;
// 			}
// 			
// 			System.out.println("ERROR: " + ins.fitness());
// 			
// 			ins.recargar();
// 			ins.dividirDIB3();
// 			System.out.println("SE DIVIDIO EN dividirDIB3");
// 			
// 			count = 0;
// 			for(Conjunto c : ins.getLCP()){
// 				for(Punto p : c.getConjunto()){
// 					System.out.print("C" + count + ", ");
// 					for (int i = 0; i < p.componente.length; i++) {
// 						System.out.print(p.componente[i]+",");
// 					}
// 					
// 					System.out.print(", a(Xi):" + ins.a(p));
// 					System.out.print(", b(Xi): " + ins.b(p));
// 					System.out.println(", s(Xi): " + ins.s(p));
// 				}
// 				count++;
// 			}
// 			
// 			System.out.println("ERROR: " + ins.fitness());
// 			
// 			
// 			ins.recargar();
// 			ins.dividirDIB4();
// 			System.out.println("SE DIVIDIO EN dividirDIB4");
// 			
// 			count = 0;
// 			for(Conjunto c : ins.getLCP()){
// 				for(Punto p : c.getConjunto()){
// 					System.out.print("C" + count + ", ");
// 					for (int i = 0; i < p.componente.length; i++) {
// 						System.out.print(p.componente[i]+",");
// 					}
// 					
// 					System.out.print(", a(Xi):" + ins.a(p));
// 					System.out.print(", b(Xi): " + ins.b(p));
// 					System.out.println(", s(Xi): " + ins.s(p));
// 				}
// 				count++;
// 			}
// 			
// 			System.out.println("ERROR: " + ins.fitness());
// 			
// 			
// 			ins.recargar();
// 			ins.dividirUNO();
// 			System.out.println("SE DIVIDIO EN dividirUNO");
// 			
// 			count = 0;
// 			for(Conjunto c : ins.getLCP()){
// 				for(Punto p : c.getConjunto()){
// 					System.out.print("C" + count + ", ");
// 					System.out.print(p);
// 					
// 					System.out.print(", a(Xi):" + ins.a(p));
// 					System.out.print(", b(Xi): " + ins.b(p));
// 					System.out.println(", s(Xi): " + ins.s(p));
// 				}
// 				count++;
// 				System.out.println(c.getConjunto().size());
// 			}
// 			
// 			System.out.println("ERROR: " + ins.fitness());
		}
	}
}