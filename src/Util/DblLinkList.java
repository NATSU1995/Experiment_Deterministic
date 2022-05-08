package Util;


import java.io.*;

/**
 * The DblLinkList class defines the double-linked list.
 * Any kind of objects can be linked into the double-linked list
 * by using the ListNode to point to and add into this double-linked list.
 * @version			2.1
 * @author			Atom Yuen
 */
public class DblLinkList implements Serializable
{
	/** @serial */
	protected ListNode	head;
	/** @serial */
	protected ListNode	tail;
	/** @serial */
	protected ListNode	current;

	/**
	 * Constructs a new double-linked list without any ListNode.
	 */
	public DblLinkList()
	{
		head = null;
		tail = null;
		current = null;
	}

	/**
	 * Checks whether this list is empty.
	 * @return		true if the list is empty; false otherwise.
	 */
	public boolean isEmpty()
	{
		return head==null;
	}

	/**
	 * Removes all the ListNode from the list.
	 * @see			#remove
	 */
	public void clear()
	{
		if (isEmpty())
		{
			return;
		}

		current = head;
		head = null;
		while(current!=tail)
		{
			current = current.next;
			current.prev = null;
		}
		current = null;
		tail = null;
		return;
	}

	/**
	 * Adds an object to a new ListNode before the current ListNode in the list
	 * or directly add the object to the empty list.
	 * The new ListNode will become the current ListNode.
	 * @param o		the object to be added
	 * @see			#addAfter
	 * @see			#addFront
	 * @see			#addBack
	 */
	public void addBefore(Object o)
	{
		ListNode temp = new ListNode(o);
		if (isEmpty())
		{
			addEmpty(temp);
			return;
		}
		
		temp.next = current;
		temp.prev = current.prev;
		current.prev = temp;
		if (temp.prev==null)
		{
			head = temp;
		}
		else
		{
			temp.prev.next = temp;
		}
		current = temp;
		return;
	}

	/**
	 * Adds an object to a new ListNode after the current ListNode in the list
	 * or directly add the object to the empty list.
	 * The new ListNode will become the current ListNode.
	 * @param o		the object to be added
	 * @see			#addBefore
	 * @see			#addFront
	 * @see			#addBack
	 */
	public void addAfter(Object o)
	{
		ListNode temp = new ListNode(o);
		if (isEmpty())
		{
			addEmpty(temp);
			return;
		}
		
		temp.prev = current;
		temp.next = current.next;
		current.next = temp;
		if (temp.next==null)
		{
			tail = temp;
		}
		else
		{
			temp.next.prev = temp;
		}
		current = temp;
		return;
	}

	/**
	 * Adds an object to a new ListNode at the beginning of the list
	 * or directly add the object to the empty list.
	 * The new ListNode will become the current ListNode.
	 * It will call addEmpty if the list is empty.
	 * @param o		the object to be added
	 * @see			#addBefore
	 * @see			#addAfter
	 * @see			#addBack
	 */
	public void addFront(Object o)
	{
		ListNode temp = new ListNode(o);
		if (isEmpty())
		{
			addEmpty(temp);
			return;
		}
		
		temp.next = head;
		head.prev = temp;
		head = temp;
		current = temp;
		return;
	}

	/**
	 * Adds an object to a new ListNode at the end of the list
	 * or directly add the object to the empty list.
	 * The new ListNode will become the current ListNode.
	 * It will call addEmpty if the list is empty.
	 * @param o		the object to be added
	 * @see			#addBefore
	 * @see			#addAfter
	 * @see			#addFront
	 */
	public void addBack(Object o)
	{
		ListNode temp = new ListNode(o);
		if (isEmpty())
		{
			addEmpty(temp);
			return;
		}
		
		tail.next = temp;
		temp.prev = tail;
		tail = temp;
		current = temp;
		return;
	}

	/**
	 * Adds an object to a new ListNode in the empty list,
	 * the new ListNode will become the current ListNode.
	 * @param n		the ListNode to be added
	 * @see			#addBefore
	 * @see			#addAfter
	 * @see			#addFront
	 * @see			#addBack
	 */
	private void addEmpty(ListNode n)
	{
		head = n;
		tail = n;
		current = n;
		return;
	}

	/**
	 * Checks whether there is another ListNode next to the current ListNode.
	 * @return		true if another ListNode follows; false otherwise.
	 * @see			#hasPrevElement
	 * @see			#isMoveForward
	 * @see			#isMoveBackward
	 */
	public boolean hasNextElement()
	{
		if (current==tail)
		{
			return false;
		}
		return true;
	}

	/**
	 * Checks whether there is another ListNode previous from the current ListNode.
	 * @return		true if another ListNode pirors to; false otherwise.
	 * @see			#hasNextElement
	 * @see			#isMoveForward
	 * @see			#isMoveBackward
	 */
	public boolean hasPrevElement()
	{
		if (current==head)
		{
			return false;
		}
		return true;
	}

	/**
	 * The current position moves to the next ListNode and it returns true
	 * if possible.
	 * Otherwise it returns false.
	 * @return		true if current position can be moved forward; false otherwise.
	 * @see			#hasPrevElement
	 * @see			#hasNextElement
	 * @see			#isMoveBackward
	 */
	public boolean isMoveForward()
	{
		if (hasNextElement())
		{
			current = current.next;
			return true;
		}
		return false;
	}

	/**
	 * The current position moves to the previous ListNode and it returns true
	 * if possible.
	 * Otherwise it returns false.
	 * @return		true if current position can be moved backward; false otherwise.
	 * @see			#hasPrevElement
	 * @see			#hasNextElement
	 * @see			#isMoveForward
	 */
	public boolean isMoveBackward()
	{
		if (hasPrevElement())
		{
			current = current.prev;
			return true;
		}
		return false;
	}

	/**
	 * @deprecated  As of DSI 2.1, replaced by {@link #isMoveForward}
	 */
	public void moveForward()
	{
		if (hasNextElement())
		{
			current = current.next;
		}
		return;
	}

	/**
	 * @deprecated  As of DSI 2.1, replaced by {@link #isMoveBackward}
	 */
	public void moveBackward()
	{
		if (hasPrevElement())
		{
			current = current.prev;
		}
		return;
	}

	/**
	 * Sets the current position to the beginning of the list.
	 * It will also check whether the list is empty.
	 * @return		true if the current position can be set; false if the list is empty.
	 * @see			#isSetLast
	 * @see			#reset
	 */
	public boolean isSetFirst()
	{
		if (isEmpty())
		{
			return false;
		}
		current = head;
		return true;
	}

	/**
	 * Sets the current position to the end of the list.
	 * It will also check whether the list is empty.
	 * @return		true if the current position can be set; false if the list is empty.
	 * @see			#isSetFirst
	 * @see			#reset
	 */
	public boolean isSetLast()
	{
		if (isEmpty())
		{
			return false;
		}
		current = tail;
		return true;
	}

	/**
	 * Sets the current position to the beginning of the list.
	 * @return		true if the current position can be set; false if the list is empty.
	 * @see			#isSetFirst
	 * @see			#isSetLast
	 */
	public void reset()
	{
		current = head;
		return;
	}

	/**
	 * Gets the object pointed by the ListNode at the beginning of the list
	 * or return null if the list is empty.
	 * The object has to be casted explicitly.
	 * @return		the object
	 * @see			#getLastElement
	 * @see			#getElement
	 * @see			#getNextElement
	 * @see			#getPrevElement
	 */
	public Object getFirstElement()
	{
		if (isSetFirst())
		{
			return current.element;
		}
		return null;
	}

	/**
	 * Gets the object pointed by the ListNode at the end of the list
	 * or return null if the list is empty.
	 * The object has to be casted explicitly.
	 * @return		the object
	 * @see			#getFirstElement
	 * @see			#getElement
	 * @see			#getNextElement
	 * @see			#getPrevElement
	 */
	public Object getLastElement()
	{
		if (isSetLast())
		{
			return current.element;
		}
		return null;
	}

	/**
	 * Gets the object pointed by the ListNode at the current position
	 * or return null if the list is empty.
	 * The object has to be casted explicitly.
	 * @return		the object
	 * @see			#getFirstElement
	 * @see			#getLastElement
	 * @see			#getNextElement
	 * @see			#getPrevElement
	 */
	public Object getElement()
	{
		if (isEmpty())
		{
			return null;
		}
		return current.element;
	}

	/**
	 * Gets the object pointed by the ListNode next to the current ListNode.
	 * It returns null if the current ListNode is at the end
	 * or the list is empty.
	 * The object has to be casted explicitly.
	 * @return		the object
	 * @see			#getFirstElement
	 * @see			#getLastElement
	 * @see			#getElement
	 * @see			#getPrevElement
	 */
	public Object getNextElement()
	{
		if (isMoveForward())
		{
			return current.element;
		}
		return null;
	}

	/**
	 * Gets the object pointed by the ListNode previous from the current ListNode.
	 * It returns null if the current ListNode is at the beginning
	 * or the list is empty.
	 * The object has to be casted explicitly.
	 * @return		the object
	 * @see			#getFirstElement
	 * @see			#getLastElement
	 * @see			#getElement
	 * @see			#getNextElement
	 */
	public Object getPrevElement()
	{
		if (isMoveBackward())
		{
			return current.element;
		}
		return null;
	}

	/**
	 * Gets the ListNode at the current position in the list.
	 * or return null if the list is empty.
	 * This function should be used carefully otherwise it may
	 * demage the double-linked list.
	 * @return		the ListNode 
	 */
	public ListNode getListNode()
	{
		if (isEmpty())
		{
			return null;
		}
		return current;
	}

	/**
	 * Checks whether the object is contained in the list.
	 * @param o		the object
	 * @return		true if the object is contained in the list; false otherwise.
	 * @see			#equals
	 */
	public boolean isExist(Object o)
	{
		if(!isSetFirst())
		{
			return false;
		}

		while (current!=tail)
		{
			if (current.element==o)
			{
				return true;
			}
			current = current.next;
		}

		if (tail.element==o)
		{
			return true;
		}
		return false;
	}

	/**
	 * Checks whether a similar object is contained in the list.
	 * @param o		the object
	 * @return		true if a similar object is contained in the list; false otherwise.
	 * @see			#isExist
	 */
	public boolean equals(Object o)
	{
		if(!isSetFirst())
		{
			return false;
		}

		while (current!=tail)
		{
			if (current.element.equals(o))
			{
				return true;
			}
			current = current.next;
		}

		if (tail.element.equals(o))
		{
			return true;
		}
		return false;
	}

	/**
	 * Removes the ListNode n from the list.
	 * The current position shifts to the next ListNode if possible
	 * or sets to the end of the list
	 * @param n		the ListNode
	 * @see			#remove
	 */
	public void removeListNode(ListNode n)
	{
		current = n;
		remove();
	}

	/**
	 * Removes the ListNode at the current position from the list.
	 * The current position shifts to the next ListNode if possible
	 * or sets to the end of the list
	 * @see			#removeListNode
	 */
	public void remove()
	{
		if (isEmpty())
		{
			return;
		}

		if (current==head)
		{
			if (current.next!=null)
			{
				current = current.next;
				current.prev = null;
				head = current;
				return;
			}
		}

		if (current==tail)
		{
			if (current.prev!=null)
			{
				current = current.prev;
				current.next = null;
				tail = current;
				return;
			}
			head = null;
			tail = null;
			current = null;
			return;
		}

		current.prev.next = current.next;
		current.next.prev = current.prev;
		current = current.next;
		return;
	}

	/**
	 * Gets the number of objects contained in the list.
	 * @return		the number of objects as int
	 */
	public int size()
	{
		ListNode tempNode = current;
		if (!isSetFirst())
		{
			return 0;
		}
		int s = 1;
		while (isMoveForward())
		{
			s++;
		}
		current = tempNode;
		return s;
	}
}
