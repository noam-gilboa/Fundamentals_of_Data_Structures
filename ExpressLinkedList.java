/* ExpressLinkedList
   @author David Badiei
   @author Noam Gilba
 */
public class ExpressLinkedList<E>
{
    //Class that stores node structure
    public class Node
    {
        //Reference to previous node
        Node previous;

        //Reference to node 10 positions ahead
        Node next;

        //Stored element
        E element;
    }

    //Reference to tail node
    private Node tail;

    //Tenth item (used in retrival)
    private Node tenthItem;

    //Size of the list
    private int size;
    
    ExpressLinkedList()
    {
        //Set reference pointers and size to default values
        this.tail = null;
        this.tenthItem = null;
        size = 0;
    }

    
    /** 
     * Adds element to the end of the list
     * 
     * @param e Element to add
     * @return boolean Always true
     */
    public boolean add(E e)
    {   
        //Shim the request to the indexed add
        add(size,e);

        //Always true
        return true;
    }

    
    /** 
     * Adds element at specific index
     * 
     * @param index Position to add new element
     * @param e Element to add
     */
    public void add(int index, E e)
    {
        //If index is out of bounds throw exception
        if (index < 0 && index > size)
            throw new IndexOutOfBoundsException(index);

        //Reference to new tenth item
        Node newTenthItem = tenthItem;

        //Create new node object
        Node newNode = new Node();
        
        //Check if node is being added at the end, if so update the tail reference
        if (index == size)
        {
            newNode.previous = tail;
            tail = newNode;
        }

        //Start updating next references at the new node
        Node startingUpdateNext = newNode;

        //Get the new tenth value if necessary
        if (index < 9 && size+1 >= 10)
            newTenthItem = findNode(size > 10 ? tenthItem : tail,8);
        else if (index == 9 && size+1 >= 10)
            newTenthItem = newNode;
        
        //Get the node immediately after the new node
        Node nodeImmediateAfter = findNode(size > 10 ? tenthItem : tail,index);
        if (nodeImmediateAfter != null)
        {
            //Insert the new node using the previous references
            newNode.previous = nodeImmediateAfter.previous;
            nodeImmediateAfter.previous = newNode;
            
            //If the node immediately after the new node has a node 10 positions ahead, use its previous for updating the next values
            if (nodeImmediateAfter.next != null)
                startingUpdateNext = nodeImmediateAfter.next.previous;
        }

        //Update the next values in the list when necessary
        Node startingUpdatePrev = tail.equals(newNode) ? findNode(size > 10 ? tenthItem : tail, index-10) : newNode;
        
        for (int i = 0; i <= 10; i++)
        {
            if (startingUpdatePrev != null)
            {
                startingUpdatePrev.next = startingUpdateNext;
                startingUpdateNext = startingUpdateNext == null ? null : startingUpdateNext.previous;
                startingUpdatePrev = startingUpdatePrev.previous;
            }
        }

        //Increment size
        size++;

        //Update tenth item
        tenthItem = newTenthItem;

        //Save element
        newNode.element = e;
    }

    
    /** 
     * Removes an element from the list
     * 
     * @param index Index of element to remvoe
     * @return E Removed element
     */
    public E remove(int index)
    {
        //If remove is out of bounds, return null
        if (index < 0 || index >= size)
            return (E)null;

        //Reference to new tenth item
        Node newTenthItem = tenthItem;

        //Get the new tenth value if necessary
        if (index <= 9 && size > 10)
            newTenthItem = findNode(size > 10 ? tenthItem : tail,10);
        
        //Find the node to remove and the node immediately after that
        Node removeIndex = findNode(size > 10 ? tenthItem : tail,index);
        Node nodeImmediateAfter = findNode(size > 10 ? tenthItem : tail,index+1);
        
        //Update the tail if necessary
        if (tail.equals(removeIndex))
            tail = removeIndex.previous;

        //Update previous reference of the node immediately after
        if (nodeImmediateAfter != null)
            nodeImmediateAfter.previous = removeIndex.previous;
        
        //Update next values in the list when necessary
        Node startingUpdateNext = removeIndex.next;
        Node startingUpdatePrev = removeIndex.previous;

        for (int i = 0; i < 10; i++)
        {
            if (startingUpdatePrev != null)
            {
                startingUpdatePrev.next = startingUpdateNext;
                startingUpdateNext = startingUpdateNext == null ? null : startingUpdateNext.previous;
                startingUpdatePrev = startingUpdatePrev.previous;
            }
        }
            
        //Decrement size
        size--;

        //Update tenth item
        tenthItem = newTenthItem;
        
        //Return deleted element
        return removeIndex.element;
    }

    
    /** 
     * Find node in linked list
     * 
     * @param currentIndex Starting node reference, Either the tail or tenth node
     * @param index Index of item to find
     * @return Node Reference to found node
     */
    private Node findNode(Node currentIndex, int index)
    {  
        //If index is out of bounds, return null
        if (index < 0 || index >= size)
            return null;
        
        //Count backwards from tenth node or tail until the ones place matches index
        for (int i = (size > 10 ? 9 : size-1)-(index % 10); i > 0; i--)
            currentIndex = currentIndex.previous;
        
        //Remove ones place from index
        index = index - (index % 10);

        //Keep jumping forward by 10 until the final destination is reached
        for (int i = 0; i*10 < index; i++)
            currentIndex = currentIndex.next;

        //Return found node
        return currentIndex;
    }

    
    /** 
     * Get element at index
     * 
     * @param index Index of element
     * @return E Found element
     */
    public E get(int index)
    {
        //If index is out of bounds, return null
        if (index < 0 || index >= size)
            return (E)null;
        
        //Find element with given index
        Node nodeToGet = findNode(size > 10 ? tenthItem : tail, index);

        //Return found element
        return nodeToGet.element;
    }

    
    /** 
     * Returns size of linked list
     * 
     * @return int Size of list
     */
    public int size()
    {
        //Return size variable
        return size;
    }

    
    /** 
     * Clears contents of linked list
     */
    public void clear()
    {
        //Remove elements starting from head
        for (int i = size; i >= 0; i--)
            remove(i);
        
        //Set reference pointers and size to default values
        this.tail = null;
        this.tenthItem = null;
        size = 0;
    }

    
    /** 
     * Returns string representation of linked list
     * 
     * @return String String representation of linked list
     */
    @Override
    public String toString()
    {
        //Assemble string
        String outputString = "[";
        
        for (int i = 0; i < size; i++)
        {
            outputString = outputString + get(i);
            if (i < size-1)
                outputString = outputString + ", ";
        }

        outputString = outputString + "]";

        //Return assembled string representation of list
        return outputString;
    }
}