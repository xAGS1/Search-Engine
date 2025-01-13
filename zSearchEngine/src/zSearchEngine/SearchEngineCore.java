package zSearchEngine;

public class SearchEngineCore {

	
	
	public static LinkedList<WordFrequency> evalBoolUnified(LinkedQueue<String> qq, Use use) { // evaluates boolean queries.
	    // Stacks for operands and operators
	    LinkedStack<LinkedList<WordFrequency>> operandStack = new LinkedStack<>();
	    LinkedStack<String> operatorStack = new LinkedStack<>();
	    String previousWord = ""; // Track the previous word for validation

	    // Process the query queue
	    while (!qq.empty()) {
	        String word = qq.serve().trim();

	        if (word.isEmpty())
	            continue; // Skip empty strings
	        
	        boolean isOperator = word.equalsIgnoreCase("AND") || word.equalsIgnoreCase("OR");
	        
	        if(isOperator && previousWord.isEmpty()) {
	        	System.err.println("Invalid query: worng operator use. Skipping this part.");
	        	 continue;
	        }
	        
	        // Validate operators
	        if (isOperator && (previousWord.equalsIgnoreCase("AND")
	                || previousWord.equalsIgnoreCase("OR"))) {
	            System.err.println("Invalid query: duplicated operators. Skipping this part.");
	            continue; // Skip duplicate operators
	        }

	        if (isOperator) {
	            String top = "";
	            if (!operatorStack.empty()) { // Check the top of the operator stack
	                top = operatorStack.pop();
	                operatorStack.push(top);
	            }
	            while (!operatorStack.empty() && precedence(top) >= precedence(word)) {
	                processOperation(operandStack, operatorStack);
	                if (!operatorStack.empty()) {
	                    top = operatorStack.pop();
	                    operatorStack.push(top);
	                }
	            }
	            operatorStack.push(word);
	        } else {
	            // Use the appropriate search method based on the flag
	        	LinkedList<WordFrequency> docList = Main.searchCopy(word,use);
	        	
	          //  = use ? Main.searchCopy(word,true) : 	
	           operandStack.push(docList);
	        }
	        previousWord = word;
	    }
	    // Process remaining operations in the stack
	    while (!operatorStack.empty()) {
	        processOperation(operandStack, operatorStack);
	        if (operatorStack.getSize() == 1 && operandStack.getSize() == 1) break;
	    }
	  
	    return operandStack.empty() ? new LinkedList<>() : operandStack.pop();
	}
	
	
	private static int precedence(String operator) { // Helper method for checking precedence where AND > OR  { O(1) } 
		if (operator.equalsIgnoreCase("AND")) {
			return 2;
		} else if (operator.equalsIgnoreCase("OR")) {
			return 1;
		}
		return 0;
	}

	
	private static LinkedList<WordFrequency> doIntersection(LinkedList<WordFrequency> list1, // do intersection for two lists
			LinkedList<WordFrequency> list2) {
		LinkedList<WordFrequency> intersection = new LinkedList<>();
		
		if(list1.empty() || list2.empty()) {
			return intersection;
		}
		
		for (int i = 0; i < list1.getSize(); i++) {
				// Skip entries with frequency -1 as they words
			if (list1.get(i).getFrequency() == -1)
				continue;

			for (int j = 0; j < list2.getSize(); j++) {
				// Skip entries with frequency -1 as they words
				if (list2.get(j).getFrequency() == -1)
					continue;

				if (list1.get(i).getDocId().equals(list2.get(j).getDocId())) {
					intersection.insert(list1.get(i)); // Insert WordFrequency
				}
			}
		}
		return intersection;
	}
	

	private static LinkedList<WordFrequency> doUnion(LinkedList<WordFrequency> list1, LinkedList<WordFrequency> list2) { // O(n1 + n2 * n1),
		LinkedList<WordFrequency> union = new LinkedList<>();
		// Add list1 to union  ,Skip entries with frequency -1 as they words
		for (int i = 0; i < list1.getSize(); i++) {
			if (list1.get(i).getFrequency() != -1) {
				union.insert(list1.get(i));
			}
		}
// Add list2 to union,Skip entries with frequency -1 as they words
		for (int i = 0; i < list2.getSize(); i++) {
			if (list2.get(i).getFrequency() == -1)
				continue;

			boolean exists = false;
			for (int j = 0; j < list1.getSize(); j++) {
				if (list1.get(j).getDocId().equals(list2.get(i).getDocId())) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				union.insert(list2.get(i)); // Insert the WordFrequency from list2
			}
		}
		return union;
	}
	

	private static void processOperation(LinkedStack<LinkedList<WordFrequency>> operandStack,
			LinkedStack<String> operatorStack) {
		if (operandStack.getSize() < 2)
			return; // ensures that we have at least two operands

		String operator = operatorStack.pop();
		LinkedList<WordFrequency> Operand1 = operandStack.pop();
		LinkedList<WordFrequency> Operand2 = operandStack.pop();

		if (operator.equalsIgnoreCase("AND")) {
			operandStack.push(doIntersection(Operand1, Operand2));
		} else { // OR operation
			operandStack.push(doUnion(Operand1, Operand2));
		}
	}
	
	
	
	
	
	public static LinkedList<WordFrequency> evalRankUnified(LinkedQueue<String> qq, Use use) { // evaluates the Rank
	    LinkedList<WordFrequency> result = new LinkedList<>();

	   
	    //  loop for processing query terms
	    while (!qq.empty()) {
	        String word = qq.serve().trim();
	        LinkedList<WordFrequency> currentList = Main.searchCopy(word, use);

	        // Skip words not indexed
	        if (currentList.empty()) {
	       //     System.err.println("Word '" + word + "' not indexed.");
	            continue;
	        }      

	        // Merge current list into result
	        mergeRankedLists(result, currentList);
	    }

	    return result;
	}


	private static void mergeRankedLists(LinkedList<WordFrequency> result, LinkedList<WordFrequency> currentList) { // merges the result with the currentList
		
		if(currentList.empty()) return;
		
	    currentList.findFirst();
	    for(int i=0; i < currentList.getSize(); i++) {
	        WordFrequency current = currentList.retrieve();

	        // Skip invalid frequencies
	        if (current.getFrequency() == -1) {
	            if (currentList.last()) break;
	            currentList.findNext();
	            continue;
	        }

	        // Merge into result
	        boolean found = false;
	        result.findFirst();
	        for(int j=0; j <result.getSize(); j++) {
	            WordFrequency existing = result.retrieve();
	            if (existing.getDocId().equals(current.getDocId())) {
	                // Adding Scores (frequencies) for matches
	                existing.setFrequency(existing.getFrequency() + current.getFrequency());
	                found = true;
	                break;
	            }
	            if (result.last()) break;
	            result.findNext();
	        }

	        // If no match found, insert the current WordFrequency into the result list
	        if (!found) {
	            result.insert(new WordFrequency(current.getDocId(), current.getFrequency()));
	        }

	        if (currentList.last()) break;
	        currentList.findNext();
	    }
	}


	
	
	

	
	public static WordFrequency[] mergeSortRanked(WordFrequency[] arr) {
	    if (arr.length <= 1) {
	        return arr;
	    }

	    // Splitting the array into two halves
	    int mid = arr.length / 2;
	    WordFrequency[] left = new WordFrequency[mid];
	    WordFrequency[] right = new WordFrequency[arr.length - mid];

	    System.arraycopy(arr, 0, left, 0, mid);
	    System.arraycopy(arr, mid, right, 0, arr.length - mid);

	    // Recursively sort the two halves
	    left = mergeSortRanked(left);
	    right = mergeSortRanked(right);

	    // Merging sorted halves
	    return mergeRanked(left, right);
	}

	// Helper function to merge two sorted arrays based on ranking criteria
	private static WordFrequency[] mergeRanked(WordFrequency[] left, WordFrequency[] right) {
	    WordFrequency[] result = new WordFrequency[left.length + right.length];
	    int i = 0, j = 0, k = 0;

	    while (i < left.length && j < right.length) {
	        // Compare based on frequency (score), descending order
	        if (left[i].getFrequency() > right[j].getFrequency()) {
	            result[k++] = left[i++];
	        } else if (left[i].getFrequency() < right[j].getFrequency()) {
	            result[k++] = right[j++];
	        } else {
	            // If frequencies are equal, compare based on docId (ascending order)
	            int leftDocId = Integer.parseInt(left[i].getDocId());
	            int rightDocId = Integer.parseInt(right[j].getDocId());

	            if (leftDocId <= rightDocId) {
	                result[k++] = left[i++];
	            } else {
	                result[k++] = right[j++];
	            }
	        }
	    }

	    //  remaining elements
	    while (i < left.length) {
	        result[k++] = left[i++];
	    }
	    while (j < right.length) {
	        result[k++] = right[j++];
	    }

	    return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// =========================== NOT USED ====================================//
	
	
	
	private static WordFrequency[] sortRanked(LinkedList<WordFrequency> docs) {
	    // Initializing  the array with the size of the linked list
	    WordFrequency[] arr = new WordFrequency[docs.getSize()];

	    // Handle empty list case
	    if (docs.empty()) {
	        return arr;
	    }
	    // Convert linked list to array
	   docs.toArray(arr);

	    // Selection-Sort Algorithm
	    for (int i = 0; i < arr.length - 1; i++) {
	        int maxIndex = i;  
	        for (int j = i + 1; j < arr.length; j++) {
	            // Sort primarily by `frequency` (descending), then by `docId` (Ascending)
	            int currentDocId = Integer.parseInt(arr[j].getDocId());
	            int maxDocId = Integer.parseInt(arr[maxIndex].getDocId());

	            if (arr[j].getFrequency() > arr[maxIndex].getFrequency() ||
	                (arr[j].getFrequency() == arr[maxIndex].getFrequency() && currentDocId < maxDocId)) {
	                maxIndex = j;
	            }
	        }      
	        if (maxIndex != i) {
	            WordFrequency temp = arr[i];
	            arr[i] = arr[maxIndex];
	            arr[maxIndex] = temp;
	        }
	    }
	    return arr;
	}
	
	private static void displayRank(LinkedList<WordFrequency> docs) { // This method displays the invertedIndex <with>  freqeuncy
		if (!docs.empty()) {
			WordFrequency arr[] = new WordFrequency[docs.getSize()];
	arr = sortRanked(docs);
	
			displayRankHelper(arr);
			
		} else {
			System.out.println("No documents found for the given query.");
		}
	}
	private static void displayRankHelper(WordFrequency wf[]) {
		if (wf.length == 0) return;
		System.out.println("DocID    Score");
		for(int i =0; i < wf.length; i++) {
		System.out.println(	wf[i].getDocId() + "\t  " +  wf[i].getFrequency());
		}
		
	}
	
}
