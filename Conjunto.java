import java.util.ArrayList;

public class Conjunto extends ArrayList<Punto>
{
	public boolean isChange = true;
	private float clique;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5014696924540648765L;
	
	public Conjunto(Conjunto conjuntoCopia) {
		super(conjuntoCopia);
	}

	public Conjunto() {
		super();
	}

	public float getClique() {
		return clique;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Punto p : this){
			sb.append(p.toString()).append("\n");
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	@Override
	public boolean add(Punto e) {
		isChange = true;
		return super.add(e);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#remove(int)
	 */
	@Override
	public Punto remove(int index) {
		isChange = true;
		return super.remove(index);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		isChange = true;
		return super.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isChange ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Conjunto other = (Conjunto) obj;
		if (isChange != other.isChange)
			return false;
		return true;
	}
	
	
	
	
}