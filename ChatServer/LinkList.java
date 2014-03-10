import java.util.*;
public class LinkList 
  {
  public Link first;
  public LinkList()
    {
	first = null;
    }

  public boolean isEmpty() 
  {
    return (first == null);
  } 
  
  public Link get(String s) {
  	
  	Link current = first;
  while(current.next!= null) {
  	if (current.data.memberName.equals(s))
  	return current;
  	current = current.next;	
  	}
  	return null;
  }
  
  public void insertLast(Communication client)
{
	Link current;
	if(first == null)
	{first = new Link(client);
		current=first;
	}
	else
{
		current=first;
	while(current.next != null)
	current = current.next;
	current.next = new Link(client);
}


}

public void deleteLink(String client)
{	
	Link current=first;
	
	if(first != null && first.next==null && first.data.memberName.equals(client))
		first=null;
	
	if(current != null && current.next!=null)
	while(!(current.next.data.memberName.equals(client)))
	{ 
		current=current.next;
		
	}
	if(current != null && current.next!=null)
	{
	Link temp=current.next.next;
	current.next=temp;
	}
	else{
		if(current != null)
		current.next=null;
	}

}

public String returnList() 
  { 
  String members="";
  Link current = first;
  	while(current.next != null)
    {
 	members = members + current.data.memberName + " ";
    current = current.next;
    }
  members = members + current.data.memberName + " ";
  
  return members;
  }

  
		public int getSize() {
		int size = 1;
		if (this.isEmpty()) {
			return 0;
		}
		Link current = this.first;
		while(current.next!=null) {
			size++;
			current = current.next;
		}
		return size;
	}
	
	public boolean contains(String s) {
		return this.returnList().contains(s);
	}

}


