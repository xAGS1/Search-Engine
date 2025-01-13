package zSearchEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
	private static int tokens;
	public static Scanner scanner = new Scanner(System.in);
	public static LinkedList<String> stopWords = new LinkedList<>();
	public static LinkedList<LinkedList<String>> docWords = new LinkedList<>();
	public static LinkedList<LinkedList<WordFrequency>> invertedIndex = new LinkedList<>();
	public static BST<LinkedList<WordFrequency>> bst = new BST<>();
	public static HashMap<String, LinkedList<WordFrequency>> hesh = new HashMap(invertedIndex.getSize()+1);
	
	public static void main(String[] args) {
		String fileCSV = "bin\\dataset.csv"; // csv filePath
		String fileStopWords = "bin\\stop.txt"; // stopwords filePath
   
		
		
		String choice = "";
		
	      while(true) { // this loop let user choose between Datasets
	    	  System.out.println("\n========== Dataset Menu ==========");
				System.out.println("1. Normal Dataset.");
				System.out.println("2. XL Dataset.");
				System.out.println("0. To exit.");
				choice = scanner.nextLine().toLowerCase();
	    	  switch (choice) {
	    	  case "1": fileCSV = "bin\\dataset.csv";
	    		  break;
	    	  case "2": fileCSV = "bin\\datasetXL.csv";
	    		  break;
	    	  }
	    	  if(choice.equals("0") || choice.equals("2") || choice.equals("1")) {
	    		  break;
	    	  }
	      }
		
	    	  // one time methods
		System.out.println("Loading...");
		long start = System.nanoTime();
		getStopWords(fileStopWords);
		processDoc(fileCSV);
		invertedIndexRankingBst();
		hashMaker();
		System.out.println("------------------\nTime taken: " + ((System.nanoTime() - start)/Math.pow(10, 9)) + " seconds to load");
	// ==========================MAIN===================================	
	//printDoc();
	
	      
		
		// MENU
		while(true) {
			System.out.println("\n========== Main Menu ==========");
			System.out.println("1. Boolean Retrieval");
			System.out.println("2. Ranked Retrieval");
			System.out.println("3. View Indexed Documents Count");
			System.out.println("4. View Indexed Tokens and Vocabulary Size");
			System.out.println("0. Exit");
			System.out.println("================================");
			System.out.print("Please enter your choice: ");
			choice = scanner.nextLine().toLowerCase();

			switch (choice) {

			    case "1": // Boolean Retrieval
			        System.out.println("\n===== Boolean Retrieval Menu =====");
			        System.out.println("1. Search using Inverted Index List");
			        System.out.println("2. Search using Binary Search Tree (BST)");
			        System.out.println("3. Search using Indexed Data");
			        System.out.println("4. Search using Hash Table");
			        System.out.println("==================================");
			        System.out.print("Please select a search method: ");
			        choice = scanner.nextLine().toLowerCase();

			        if (choice.equals("1")) {
			            System.out.print("\n(Inverted Index) Enter your Boolean query: ");
			            choice = scanner.nextLine();
			            System.out.print("\033[1;32m");
			            long inti = System.nanoTime();
			            displayNoIndex(boolRetrieve(choice, Use.inverted));
			            System.out.println("------------------\nTime taken: " + ((System.nanoTime() - inti)/Math.pow(10, 9)) + " seconds");
			            System.out.println("\u001B[0m");

			        } else if (choice.equals("2")) {
			            System.out.print("\n(BST) Enter your Boolean query: ");
			            choice = scanner.nextLine();
			            System.out.print("\033[1;32m");
			            long inti = System.nanoTime();
			            displayNoIndex(boolRetrieve(choice, Use.bst));
			            System.out.println("------------------\nTime taken: " + ((System.nanoTime() - inti)/Math.pow(10, 9)) + " seconds");
			            System.out.println("\u001B[0m");

			        } else if (choice.equals("3")) {
			            System.out.print("\n(Indexed) Enter your Boolean query: ");
			            choice = scanner.nextLine();
			            System.out.print("\033[1;32m");
			            long inti = System.nanoTime();
			            displayNoIndex(boolRetrieve(choice, Use.index));
			            System.out.println("------------------\nTime taken: " + ((System.nanoTime() - inti)/Math.pow(10, 9)) + " seconds");
			            System.out.println("\u001B[0m");

			        } else if (choice.equals("4")) {
			            System.out.print("\n(Hash Table) Enter your Boolean query: ");
			            choice = scanner.nextLine();
			            System.out.print("\033[1;32m");
			            long inti = System.nanoTime();
			            displayNoIndex(boolRetrieve(choice, Use.hash));
			            System.out.println("------------------\nTime taken: " + ((System.nanoTime() - inti)/Math.pow(10, 9)) + " seconds");
			            System.out.println("\u001B[0m");

			        } else {
			            System.err.println("Invalid choice. Please select a valid option from the menu.");
			        }
			        break;

			    case "2": // Ranked Retrieval
			        System.out.println("\n===== Ranked Retrieval Menu =====");
			        System.out.println("1. Search using Inverted Index List");
			        System.out.println("2. Search using Binary Search Tree (BST)");
			        System.out.println("3. Search using Indexed Data");
			        System.out.println("4. Search using Hash Table");
			        System.out.println("==================================");
			        System.out.print("Please select a search method: ");
			        choice = scanner.nextLine().toLowerCase();

			        if (choice.equals("1")) {
			            System.out.print("\n(Inverted Index) Enter your query: ");
			            choice = scanner.nextLine();
			            System.out.print("\033[1;32m");
			            long inti = System.nanoTime();
			            displayRankedResults(rankRetrieve(choice, Use.inverted));
			            System.out.println("------------------\nTime taken: " + ((System.nanoTime() - inti)/Math.pow(10, 9)) + " seconds");
			            System.out.println("\u001B[0m");

			        } else if (choice.equals("2")) {
			            System.out.print("\n(BST) Enter your query: ");
			            choice = scanner.nextLine();
			            System.out.print("\033[1;32m");
			            long inti = System.nanoTime();
			            displayRankedResults(rankRetrieve(choice, Use.bst));
			            System.out.println("------------------\nTime taken: " + ((System.nanoTime() - inti)/Math.pow(10, 9)) + " seconds");
			            System.out.println("\u001B[0m");

			        } else if (choice.equals("3")) {
			            System.out.print("\n(Indexed) Enter your query: ");
			            choice = scanner.nextLine();
			            System.out.print("\033[1;32m");
			            long inti = System.nanoTime();
			            displayRankedResults(rankRetrieve(choice, Use.index));
			            System.out.println("------------------\nTime taken: " + ((System.nanoTime() - inti)/Math.pow(10, 9)) + " seconds");
			            System.out.println("\u001B[0m");

			        } else if (choice.equals("4")) {
			            System.out.print("\n(Hash Table) Enter your query: ");
			            choice = scanner.nextLine();
			            System.out.print("\033[1;32m");
			            long inti = System.nanoTime();
			            displayRankedResults(rankRetrieve(choice, Use.hash));
			            System.out.println("------------------\nTime taken: " + ((System.nanoTime() - inti)/Math.pow(10, 9)) + " seconds");
			            System.out.println("\u001B[0m");

			        } else {
			            System.err.println("Invalid choice. Please select a valid option from the menu.");
			        }
			        break;

			    case "3": // Indexed Document Count
			    	System.out.print("\033[1;94m");
			        System.out.printf("Total number of indexed documents: %d\n", docWords.getSize());
			        System.out.println("\u001B[0m");
			        break;

			    case "4": // Indexed Tokens and Vocabulary
			    	System.out.print("\033[1;94m");
			        System.out.printf("Total number of indexed tokens: %d\n", tokens);
			        System.out.printf("Vocabulary size: %d\n",invertedIndex.getSize());
			        System.out.println("\u001B[0m");
			        break;

			    case "0": // Exit
			    	 System.out.println("Exiting the application. Goodbye!");
					    return;
			    default:
			        System.err.println("Invalid choice. Please select a valid option from the menu.");
			}

			

		}
	
		
		
		
		
		
		
		
	}
	
	 
	
	
  // ========================================== OPERATIONS ============================================================
	
	public static LinkedList<WordFrequency> boolRetrieve(String word, Use use){ // main method for bool retrieve
		LinkedQueue<String> queryQueue = queryToQueue(word);
		LinkedList<WordFrequency> resultDocs = SearchEngineCore.evalBoolUnified(queryQueue,use); //change Use. to change search mode
		return resultDocs;
	}
	
	public static LinkedList<WordFrequency> rankRetrieve(String word, Use use){ // main method for rank retrieve
		LinkedQueue<String> queryQueue = queryToQueue(word);
		LinkedList<WordFrequency> resultDocs = SearchEngineCore.evalRankUnified(queryQueue,use);//change Use. to change search mode
		return resultDocs;
	}
	
	
	

	public static LinkedQueue<String> queryToQueue(String query) { //tokenize query string using StringTokenizer Class and convert it to Queue  
		LinkedQueue<String> qq = new LinkedQueue<>();

		StringTokenizer tokenizer = new StringTokenizer(query);
		while (tokenizer.hasMoreTokens()) {
			qq.enqueue(tokenizer.nextToken());
		}
		return qq;
	}
	// =======================================Search Mehtods=========================================================
	public static LinkedList<WordFrequency> searchCopy(String word, Use use) { // Main method for searching
	    if (use == Use.bst) {
	        return searchCopyBst(word);  // Search using BST
	    } else if(use == Use.inverted) {
	        return searchCopyInverted(word);  // Search using Inverted list
	    }else if(use == Use.hash) {
	    	return  searchCopyHash(word);
	    }else {
	    	return  searchCopyIndex(word);
	    }
	}

	private static LinkedList<WordFrequency> searchCopyHash(String word) {  // search  for hash
		LinkedList<WordFrequency> resultCopy =  hesh.get(word.toLowerCase());
		return resultCopy == null ? new LinkedList<WordFrequency>(): resultCopy;
	}
	
	private static LinkedList<WordFrequency> searchCopyIndex(String word) { // search  for index list  O(n * m + n)
	    LinkedList<WordFrequency> resultCopy = new LinkedList<>();

	    // Iterate over all documents in docWords
	    docWords.findFirst();
	    for (int i = 0; i < docWords.getSize(); i++) {
	        LinkedList<String> currentEntry = docWords.retrieve(); // Get the current document's words

	        // Search for the word in the current document
	        currentEntry.findFirst();
	        for (int j = 0; j < currentEntry.getSize(); j++) {
	            if (currentEntry.retrieve().equalsIgnoreCase(word)&& !isNumeric(word) ) {
	                // If word matches, add to resultCopy with its document ID
	                WordFrequency wf = new WordFrequency(Integer.toString(i), 1);
	                resultCopy.insert(wf);
	            }
	            if (!currentEntry.last()) {
	                currentEntry.findNext(); // Move to the next word
	            }
	        }
	        if (!docWords.last()) {
	            docWords.findNext(); // Move to the next document
	        }
	    }
	    // Check for duplicates in resultCopy and merge frequencies
	    resultCopy.findFirst();
	    while (!resultCopy.empty() && !resultCopy.last()) {
	        WordFrequency current = resultCopy.retrieve();
	        resultCopy.findNext();
	        while (!resultCopy.last() && resultCopy.retrieve().getDocId().equals(current.getDocId())) {
	            current.incrementFrequency(); // Merge frequencies
	            resultCopy.remove(); // Remove duplicate
	        }
	    }

	    return resultCopy;
	}

	
	
	private static LinkedList<WordFrequency> searchCopyInverted(String word) {// search  for inverted index list
	    LinkedList<WordFrequency> resultCopy = new LinkedList<>();
	    LinkedList<WordFrequency> currentEntry = null;
	    boolean found = false;
	    // Iterate through the entire inverted index
	    invertedIndex.findFirst();
	    for (int i = 0; i < invertedIndex.getSize(); i++) {
	        currentEntry = invertedIndex.retrieve(); // Retrieve current entry
	        // Check if the first WordFrequency  matches the search word
	        currentEntry.findFirst(); // Move to the first element of the current linked list
	        if (currentEntry.retrieve().getDocId().equals(word.toLowerCase())) {
	            found = true;
	            break; // Word found, we stop searching
	        }

	        invertedIndex.findNext(); // Move to the next list in the inverted index
	    }
	    // If the word is found, copy its list of document frequencies
	    if (found) {
	        currentEntry.findFirst(); // Start copying from the first WordFrequency

	        // Iterate through currentEntry and copy each WordFrequency to resultCopy
	        for (int i = 0; i < currentEntry.getSize(); i++) {
	            resultCopy.insert(currentEntry.retrieve());
	            currentEntry.findNext(); // Move to the next WordFrequency
	        }
	    }
	    return resultCopy;
	}
	
	
	private static LinkedList<WordFrequency> searchCopyBst(String word){ // searchCopy but for BST  | O(log n + k) or O(n + k)
		 LinkedList<WordFrequency> resultCopy = new LinkedList<>(); 
		 LinkedList<WordFrequency> currentEntry =  bst.search(word.toLowerCase());
		 if(currentEntry != null) {
			 currentEntry.findFirst();
			 for(int i=0; i< currentEntry.getSize(); i++) {
				 resultCopy.insert(currentEntry.retrieve());
				 currentEntry.findNext();
			 }
			
		 }else {
			 return resultCopy;
		 }
				
		 return resultCopy;
	}
	
	
//=========================================================================================================================	

	private static void invertedIndexRankingBst() { // invert indexing for lists and bst, and ranking | O(n*m *k)
		tokens = 0;
	    if (docWords.empty()) return;

	    docWords.findFirst();
	    for (int j = 0; j < docWords.getSize(); j++) {
	        LinkedList<String> currentDoc = docWords.retrieve();
	        String docId = "" + j; // Generate docId as a string

	        currentDoc.findFirst();
	        for (int i = 0; i < currentDoc.getSize(); i++) {
	            String word = currentDoc.get(i);

	            // Skip numeric words
	            if (isNumeric(word)) continue;
	            tokens++;
	            WordFrequency wordNode = new WordFrequency(word, -1); // Word itself
	            WordFrequency wordId = new WordFrequency(docId, 1);  // First frequency for this document

	            // Check if the word exists in the inverted index
	            LinkedList<WordFrequency> entryDoc = null;
	            boolean wordExists = false;

	            if (!invertedIndex.empty()) {
	                invertedIndex.findFirst();
	                while (!invertedIndex.last()) {
	                    entryDoc = invertedIndex.retrieve();
	                    if (entryDoc.get(0).equalsS(wordNode)) {
	                        wordExists = true;
	                        break;
	                    }
	                    invertedIndex.findNext();
	                }

	                // Check the last element
	                if (!wordExists) {
	                    entryDoc = invertedIndex.retrieve();
	                    if (entryDoc.get(0).equalsS(wordNode)) {
	                        wordExists = true;
	                    }
	                }
	            }

	            // Update inverted index
	            if (wordExists) {
	                boolean docExists = false;

	                // Check if the document already exists in the entry
	                for (int k = 1; k < entryDoc.getSize(); k++) {
	                    if (entryDoc.get(k).getDocId().equals(docId)) {
	                        entryDoc.get(k).incrementFrequency(); // Increment frequency
	                        docExists = true;
	                        break;
	                    }
	                }
	                if (!docExists) {
	                    entryDoc.insert(wordId); // Add new document entry
	                }
	            } else {
	                // Add a new word entry
	                LinkedList<WordFrequency> newEntry = new LinkedList<>();
	                newEntry.insert(wordNode);
	                newEntry.insert(wordId);
	                invertedIndex.insert(newEntry);
	               
	                //  insert into the BST
	                bst.insert(wordNode.getDocId(), newEntry);
	            }
	        }

	        docWords.findNext();
	    }

	    //  if BST size matches the inverted index size we return true to make sure.
	    if (bst.size() == invertedIndex.getSize()) {
	        System.out.println("BST and inverted index matches, with size: "+ invertedIndex.getSize());
	    } else {
	        System.err.println("BST size mismatch with inverted index.");
	    }
	}

			
	
	
	
	public static boolean isNumeric(String str) { // checks if string is a number.
		if(str != null) {
			char c = str.charAt(0);
		 if(c < 48 || c > 57) {
			return false;
		}else
			return true;	
	}else
	return false;	
	}	
	
	
	
  // ============================READING STUFF / Proccssing stuff============================================ [1]
	private static void processDoc(String filePath) { // reading from csv file | O(n * m)
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String line;

			// int docId = 0;
			line = br.readLine();
			line = null; // for skipping first line in csv doc
			while ((line = br.readLine()) != null) {
				if (line.trim().equals(",,") || line.trim().isEmpty()) {
					break;
				}

				LinkedList<String> wordsList = processTxt(line);
				docWords.insert(wordsList);
				// docId++;
			}
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static LinkedList<String> processTxt(String text) { // removes 
		LinkedList<String> wordsList = new LinkedList<>();
		String processedText = text.toLowerCase().replaceAll("[^a-z0-9\\s'-]", " ");
		String[] wordsArray = processedText.split("\\s+");

		for (String word : wordsArray) {
			
			if (!word.isEmpty() && !isStopWord(word)) {
				word = word.replaceAll("'", ""); // Replaces every '  with nothing  
				wordsList.insert(word);
				
			}
		}
		return wordsList;
	}

	private static boolean isStopWord(String word) { // checks if a string contains stop words
		stopWords.findFirst();
		while (!stopWords.last()) {
			if (stopWords.retrieve().equals(word)) {
				return true;
			}
			stopWords.findNext();
		}
		return stopWords.retrieve().equals(word);
	}

	private static void getStopWords(String stopWordPath) { // reads stopwords given and save them to stopWords.
		BufferedReader br = null;
		try  {
			br = new BufferedReader(new FileReader(stopWordPath));
			String line;
			while ((line = br.readLine()) != null) {
				stopWords.insert(line.trim().toLowerCase());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	
	
	public static void hashMaker() { // hashs the invertedIndex
		if(invertedIndex == null) {
			System.err.println("Initialize the invertedIndex first.");
			return;
		}
		invertedIndex.findFirst();
		for(int i=0 ; i< invertedIndex.getSize(); i++) {
			invertedIndex.retrieve().findFirst();
			hesh.put(invertedIndex.retrieve().retrieve().getDocId(), invertedIndex.retrieve());
			invertedIndex.findNext();
		}
	}
	
	
	
  // ======================================DISPLAY METHODS======== for debugging purposes=========================================
	private static void displayFreq(LinkedList<WordFrequency> docs) { // This method displays the invertedIndex <with>  freqeuncy   [DEBUGGING]
		if (!docs.empty()) {
			docs.findFirst();
			System.out.print("Result doc IDs: {");
			while (!docs.last()) {
				WordFrequency wf = docs.retrieve();
				System.out.print(wf.getDocId() + "(" + wf.getFrequency() + ") ");
				docs.findNext();
			}// Last one
			WordFrequency wf = docs.retrieve();
			System.out.println(wf.getDocId() + "(" + wf.getFrequency() + ")}");
		} else {
			System.out.println("No documents found for the given query.");
		}
	}
	
	private static void display(LinkedList<WordFrequency> docs) {// This method displays the invertedIndex <without>   freqeuncy  [DEBUGGING]
		if (!docs.empty()) {
			docs.findFirst();
			System.out.print("Result doc IDs: {");
			while (!docs.last()) {
				WordFrequency wf = docs.retrieve();
				System.out.print(wf.getDocId() +", ");
				docs.findNext();
			}// Last one
			WordFrequency wf = docs.retrieve();
			System.out.println(wf.getDocId() +"}");
		} else {
			System.out.println("No documents found for the given query.");
		}
	}
	
	private static void printDoc() {  // This method for printing the indexed docs: 0,market, ...   [DEBUGGING]
		if(docWords.empty())return;
		docWords.findFirst();
		for(int i=0; i < docWords.getSize(); i++) {
		   LinkedList<String> wf = 	docWords.retrieve();
		   wf.findFirst();
			for(int j=0 ; j < wf.getSize(); j++) {
				System.out.print(wf.retrieve()+ ",");
				wf.findNext();
			}
			docWords.findNext();
			System.out.println();
		}
	}
	
	
	
	public static void displayNoIndex(LinkedList<WordFrequency> docs) {// This method displays the invertedIndex <without>   freqeuncy
		if (!docs.empty()) {
			docs.findFirst();
			System.out.print("Result doc IDs: {");
			while (!docs.last()) {
				WordFrequency wf = docs.retrieve();
				if(wf.getFrequency() != -1)  // skips freq with -1
				System.out.print(wf.getDocId() +", ");
				docs.findNext();
			}// Last one
			WordFrequency wf = docs.retrieve();
			if(wf.getFrequency() != -1)  // skips freq with -1
			System.out.println(wf.getDocId() +"}");
		} else {
			System.out.println("No documents found for the given query.");
		}
	}
	

	public static void displayRankedResults(LinkedList<WordFrequency> resultDocs) {
	    if (resultDocs.empty()) {
	        System.out.println("No documents found for the given query.");
	        return;
	    }

	    // Convert LinkedList to array for sorting
	    WordFrequency[] docArray = new WordFrequency[resultDocs.getSize()];
	    resultDocs.toArray(docArray);

	    // Sort using merge sort
	    WordFrequency[] sortedDocs = SearchEngineCore.mergeSortRanked(docArray);
	    
	    // Display sorted results
	    System.out.println("DocID\tScore");
	    for (WordFrequency wf : sortedDocs) {
	        if (wf.getFrequency() != -1) { // Only display valid entries
	            System.out.printf("%s\t%d\n", wf.getDocId(), wf.getFrequency());
	        }
	    }
	}

	
	
	// ================================================================================
	
}
