import java.util.ArrayList;


public class ConjuntoInstancia extends ArrayList<Instancia> implements Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1143636577223102809L;

	/* (non-Javadoc)
	 * @see java.util.ArrayList#clone()
	 */
	@Override
	public ConjuntoInstancia clone() {
		ConjuntoInstancia copia = null;
//		copia = (ConjuntoInstancia)super.clone();
		for(Instancia i : this){
			if(copia == null) copia = new ConjuntoInstancia();
			copia.add(i.clone());
		}
		return copia;
	}
	
	public ConjuntoInstancia(ConjuntoInstancia original){
		if(original != null){
			for(Instancia i : original){
				this.add(i.clone());
			}
		}
	}

	public ConjuntoInstancia() {
	}

	
}
