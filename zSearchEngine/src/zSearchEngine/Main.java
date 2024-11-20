package zSearchEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;



public class Main {
	static Scanner scanner = new Scanner(System.in);
	static LinkedList<String> stopWords = new LinkedList<>();
	static LinkedList<LinkedList<String>> docWords = new LinkedList<>();
	static LinkedList<LinkedList<WordFrequency>> invertedIndex = new LinkedList<>();
	static BST<LinkedList<WordFrequency>> bst = new BST<>();

	public static void main(String[] args) {
		String fileCSV = "bin\\dataset.csv"; // csv filePath
		String fileStopWords = "bin\\stop.txt"; // stopwords filePath
   // one time methods
		getStopWords(fileStopWords);
		processDoc(fileCSV);
		invertedIndexRankingBst();
	// ==========================MAIN===================================	
	// printDoc();
		
		
		
		System.out.println(System.currentTimeMillis());
	
		String query0 = " market OR sports"; // <-------| put here querys for boolean Retrieval BST
		LinkedQueue<String> queryQueue0 = queryToQueue(query0);
		LinkedList<WordFrequency> resultDocs0 = SearchEngineCore.evalBoolUnified(queryQueue0,true); //change false to true to use BST
		displayNoIndex(resultDocs0);
		
		
		
		String rank = "business world market"; // <-------|  here querys for Ranked Retrieval 
		LinkedQueue<String> queryQueue2 = queryToQueue(rank);
		LinkedList<WordFrequency> resultDocs2 = SearchEngineCore.evalRankUnified(queryQueue2,true); //change false to true to use BST
		displayRankedResults(resultDocs2);
		
		System.out.println(System.currentTimeMillis());
		
		
		
	}
	
	
	
	
  // ========================================== OPERATIONS ============================================================
	
	

	public static LinkedQueue<String> queryToQueue(String query) {
		LinkedQueue<String> qq = new LinkedQueue<>();

		StringTokenizer tokenizer = new StringTokenizer(query);
		while (tokenizer.hasMoreTokens()) {
			qq.enqueue(tokenizer.nextToken());
		}
		return qq;
	}
	// =====================================================================================================
	public static LinkedList<WordFrequency> searchCopy(String word, boolean useBst) {
	    if (useBst) {
	        return searchCopyBst(word);  // Search using BST
	    } else {
	        return searchCopy(word);  // Search using LinkedList
	    }
	}

	private static LinkedList<WordFrequency> searchCopy(String word) {
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
	
	private static LinkedList<WordFrequency> searchCopyBst(String word){ // searchCopy but for BST
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

	private static void invertedIndexRankingBst() {
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

			
	
	
	
	public static boolean isNumeric(String str) {
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
	private static void processDoc(String filePath) { // reading from csv file
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

	private static LinkedList<String> processTxt(String text) {
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

	private static boolean isStopWord(String word) {
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
  // ======================================DISPLAY METHODS=================================================
	private static void displayFreq(LinkedList<WordFrequency> docs) { // This method displays the invertedIndex <with>  freqeuncy
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
	
	private static void display(LinkedList<WordFrequency> docs) {// This method displays the invertedIndex <without>   freqeuncy
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
	
	private static void printDoc() {  // This method for printing the indexed docs: 0,market, ... 
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
	
	
	
	private static void displayNoIndex(LinkedList<WordFrequency> docs) {// This method displays the invertedIndex <without>   freqeuncy
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
