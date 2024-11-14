package zSearchEngine;

public class LinkedStack<T> {
	private Node<T> top;
private int size;
	public LinkedStack() {
		top = null;
		size =0;
	}
	
	public int getSize() {
		return size;
	}

	public void push(T e) {
		Node<T> tmp = new Node<T>(e);
		tmp.next = top;
		top = tmp;
		size++;
	}

	public boolean empty() {
		return top == null;
	}

	public boolean full() {
		return false;
	}

	public T pop() {
		T e = top.data;
		top = top.next;
		size--;
		return e;
	}

	private class Node<T> {
		public T data;
		public Node<T> next;

		public Node() {
			data = null;
			next = null;
		}

		public Node(T data) {
			this.data = data;
			next = null;
		}
	}

}
