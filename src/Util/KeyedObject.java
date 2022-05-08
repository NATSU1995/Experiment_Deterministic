package Util;


/**
 * The KeyedObject defines an interface for the keyed classes.
 * Those classes should have the <code>key</code> attribute (as
 * String) and this attribute can be retrieved by using
 * <code><i>object.getKey()</i></code> function.
 * @version			2.1
 * @author			Atom Yuen
 */
public interface KeyedObject
{
	/**
	 * Returns the key of the Object.
	 * @return			the key of the Object as String
	 */
	public String getKey();
}
