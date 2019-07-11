
public class ConjuntoLog extends Conjunto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 128776907327814548L;
	public Singleton sin = Singleton.getInstance();

	public ConjuntoLog(Conjunto conjuntoCopia) {
		super(conjuntoCopia);
	}

	public ConjuntoLog() {
		super();
	}

	/* (non-Javadoc)
	 * @see Conjunto#remove(int)
	 */
	@Override
	public Punto remove(int index) {
		//if(sin.selogea) System.out.print(".");
		return super.remove(index);
	}

	/* (non-Javadoc)
	 * @see Conjunto#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		//if(sin.selogea) System.out.print(".");
		return super.remove(o);
	}
	
	

}
