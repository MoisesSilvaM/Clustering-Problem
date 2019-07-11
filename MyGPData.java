import ec.gp.*;

public class MyGPData extends GPData
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1564500534605725349L;
	public Punto punto;
	public boolean condicion;
	
	public void copyTo(final GPData gpd)   // copy my stuff to another DoubleData
    {
		((MyGPData)gpd).punto = punto;
		((MyGPData)gpd).condicion = condicion;
    }
}