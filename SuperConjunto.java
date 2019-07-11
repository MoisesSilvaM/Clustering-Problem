import java.util.ArrayList;

class SuperConjunto extends ArrayList<Conjunto>{
	public boolean isChange = true;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3944066636104704591L;

	public SuperConjunto(SuperConjunto superConjunto) {
		super(superConjunto);
	}

	public SuperConjunto() {
		super();
	}

	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(Conjunto c : this){
			sb.append("C").append(count).append("\n");
			sb.append(c.toString()).append("\n");
			count++;
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	@Override
	public boolean add(Conjunto e) {
		isChange = true;
		return super.add(e);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#remove(int)
	 */
	@Override
	public Conjunto remove(int index) {
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
		SuperConjunto other = (SuperConjunto) obj;
		if (isChange != other.isChange)
			return false;
		return true;
	}
	
	
	
	
}