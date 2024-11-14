package zSearchEngine;

public class LinkedQueue<T> {
	private Node<T> head,tail;
	private int size;
	
	public LinkedQueue() {
		head = tail = null;
		size =0;
	}
	
	public boolean full() {
		return false;
	}
	
	public int length (){
		return size;
	}
	
	public void enqueue(T data) {
		if(size ==0) {
			head = tail = new Node<T> (data);
		}else {
			tail.next = new Node<T>(data);
			tail = tail.next;
		}
		size++;
	}

	public T serve() {
		T data = head.data;
		head = head.next;
		size--;
		if(size == 0)
			tail = null;
		return data;
	}

	public boolean empty() {
		
		return size == 0;
	}

	

}
