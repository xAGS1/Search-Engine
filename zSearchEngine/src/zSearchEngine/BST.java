package zSearchEngine;



public class BST<T> {

	private BSTNode<T> root, current;

	public BST() {
		root = current = null;
	}

	public boolean insert(String word, T data) {
		 int key = hash(word);
		 
		BSTNode<T> p = root;
		BSTNode<T> q = current;
		if (empty()) {
			root = current = new BSTNode<T>(key, data);
			return true;
		}
		p = new BSTNode<T>(key, data);

		if (findKey(key)) {
			current = q;
			return false;
		} else {

			if (key < current.key)
				current.left = p;
			else
				current.right = p;
			current = p;
			return true;
		}

	}
	
	
	public int hash(String s) {
		int index = Math.abs(s.toLowerCase().hashCode());
		return index;
	}

	public boolean findKey(int key) {

		BSTNode<T> p = root;
		BSTNode<T> q = current;
		if (empty())
			return false;

		while (p != null) {
			q = p;
			if (key == current.key) {
				current = p;
				return true;
			} else if (key < p.key) {
				p = p.left;

			} else
				p = p.right;

		}

		current = q;
		return false;
	}

	public int getKey() {
		return current.key;
		
	}
	
	
	
	public boolean remove(String word) {
		int key =  hash(word);
		
		if (empty())return false;
		RemovalStatus flag = new RemovalStatus();
		root = removeHelper(key, root, flag);
		return flag.isRemoved();
	}

	private BSTNode<T> removeHelper(int key, BSTNode<T> node, RemovalStatus flag) {
		BSTNode<T> q, child = null;
		if (node == null)
			return null;
		if (key < node.key) {
			node.left = removeHelper(key, node.left, flag);
		} else if (key > node.key) {
			node.right = removeHelper(key, node.right, flag);
		} else { // ==
			flag.setRemoved(true);

			// case 3: Node with only one child or no child
			if (node.left != null && node.right != null) {
				q = findMin(node.right);
				node.key = q.key;
				node.data = q.data;
				node.right = removeHelper(q.key, node.right, flag);
			} else { //CASE 1+2
				if (node.right == null) // one child
					child = node.left;
				else if (node.left == null) // one child
					child = node.right;
				return child;
			}

		}
		return node;
	}
	
	
	public T findMin() {
		if (empty())
			return null;
		BSTNode<T> minNode = findMin(root);
		current = minNode;
		return minNode.data;
	}

	private BSTNode<T> findMin(BSTNode<T> node) { // helper
		while (node.left != null)
			node = node.left;
		return node;
	}

	public boolean empty() {
		return root == null;
	}

	public boolean full() {
		return false;
	}

	public T retrieve() {
		return current.data;
	}

	public void display() {
		displayHelper(root);
	}

	private void displayHelper(BSTNode<T> node) {
		if (node == null)
			return;
		// right subtree
		displayHelper(node.right);
		System.out.println(node.data + "-> " + node.key);
		// left subtree
		displayHelper(node.left);
	}

	public void rootInfo() {
		System.out.println("Root Data: " + root.data + " | Root Key: " + root.key);
	}
	
	
	public int size() {
		return sizeHelper(root);
	}
	
	private int sizeHelper(BSTNode node) {
		if(node == null) return 0;
		
		return 1+ sizeHelper(node.right) + sizeHelper(node.left);
	
		
	}
	
	 public T search(String word) {
	        int key = hash(word);
	        BSTNode<T> result = searchNode(this.root, key);
	        
	        if (result == null) {
	        	
	        	return null;
	           
	        }
	        return result.data;
	    }

	    private BSTNode<T> searchNode(BSTNode<T> node, int key) {
	        if (node == null || node.key == key) {
	            return node;
	        }
	        if (key < node.key) {
	            return searchNode(node.left, key);
	        } else {
	            return searchNode(node.right, key);
	        }
	    }

	// ============= BSTNODE CLASS ============= //
	private class RemovalStatus {
		private boolean removed;

		public void setRemoved(boolean flag) {
			removed = flag;
		}

		public boolean isRemoved() {
			return removed;
		}
	}

	// ============= BSTNODE CLASS ============= //
	private class BSTNode<T> {
		public int key;
		public T data;
		public BSTNode<T> left, right;

		public BSTNode(int key, T data) {
			this.key = key;
			this.data = data;
			left = right = null;
		}

		public BSTNode(int key, T data, BSTNode<T> left, BSTNode<T> right) {
			this.key = key;
			this.data = data;
			this.left = left;
			this.right = right;
		}
	}
}
