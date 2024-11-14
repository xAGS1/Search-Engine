package zSearchEngine;

public class LinkedList<T> {

	public Node<T> head, current;
	private int size;

	public LinkedList() {
		head = current = null;
		size = 0;
	}

	public void findFirst() {
		current = head;
	}

	public void findNext() {
		current = current.next;
	}

	public T retrieve() {
		return current.data;
	}

	public void update(T e) {
		current.data = e;
	}

	public boolean contains(T data) {
		Node<T> tmp = head;
		while (tmp != null) {
			if (tmp.data.equals(data)) {
				return true;
			}
			tmp = tmp.next;
		}
		return false;
	}

	public T get(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}

		Node<T> tmp = head;
		for (int i = 0; i < index ; i++) {
			tmp = tmp.next;
		}
		return tmp.data;
	}

	public void clear() {
		head = current = null;
		size = 0;
	}

	public void insert(T data) {
		Node<T> t;
		if (empty()) {
			head = current = new Node<T>(data);
			size++;

		} else {

			t = current.next;
			current.next = new Node<T>(data);
			current = current.next;
			current.next = t;
			size++;
		}
	}
	

	public int getSize() {
		return size;
	}

	public boolean empty() {
		return head == null;
	}

	public void remove() {
		if (empty())
			return;

		if (current == head) {
			head = head.next;
			size--;
		} else {
			Node<T> tmp = head;

			while (tmp.next != current)
				tmp = tmp.next;

			tmp.next = current.next;
			size--;
		}

		if (current.next == null)
			current = head;
		else
			current = current.next;
	}
	public boolean last () {
		return current.next == null;
	}
	
	
	
	

	public void display() {
		Node<T> tmp = head;

		while (tmp != null) {
			if (tmp.next != null)
				System.out.print(tmp.data + " -> ");
			else
				System.out.print(tmp.data);
			tmp = tmp.next;
		}
		System.out.println();
	}

	
	

}