package Util;


import java.io.*;

/**
 * The ListNode class defines the node in a double-linked list.
 * Any kind of objects can be linked into the double-linked list
 * by using this ListNode to point to.
 * @version			2.0
 * @author			Atom Yuen
 */
public class ListNode implements Serializable
{
	/** @serial */
	protected Object		element;
	/** @serial */
	protected ListNode		prev;
	/** @serial */
	protected ListNode		next;

	/**
	 * Constructs a new ListNode pointing to a null object.
	 */
	public ListNode()
	{
		element = null;
		prev = null;
		next = null;
	}

	/**
	 * Constructs a new ListNode pointing to object o.
	 * @param o		the object
	 */
	public ListNode(Object o)
	{
		this();
		element = o;
	}

	/**
	 * Gets the object pointed by this ListNode.
	 * The object has to be casted explicitly.
	 * @return		the object
	 * @see			#getNextElement
	 * @see			#getPrevElement
	 */
	public Object getElement()
	{
		return element;
	}

	/**
	 * Gets the object pointed by the ListNode next to this ListNode
	 * or return null if no ListNode next to it.
	 * The object has to be casted explicitly.
	 * @return		the object
	 * @see			#getElement
	 * @see			#getPrevElement
	 */
	public Object getNextElement()
	{
		if (next==null)
		{
			return null;
		}
		return next.element;
	}

	/**
	 * Get the object pointed by the ListNode previous from this ListNode
	 * or return null if no ListNode previous from it.
	 * The object has to be casted explicitly.
	 * @return		the object
	 * @see			#getElement
	 * @see			#getNextElement
	 */
	public Object getPrevElement()
	{
		if (prev==null)
		{
			return null;
		}
		return prev.element;
	}
}