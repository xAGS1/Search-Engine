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

	public static void main(String[] args) {
		String fileCSV = "bin\\dataset.csv";
		String fileStopWords = "bin\\stop.txt";
   // one time methods
		getStopWords(fileStopWords);
		processDoc(fileCSV);
		invertedIndexRanking();
	// ==========================MAIN===================================	
		//displayFreq(search("weather"));
		//displayFreq(search("warming"));
		
		
		
		String query = "market AND sports"; // <-------| put here querys

		LinkedQueue<String> queryQueue = queryToQueue(query);
		LinkedList<WordFrequency> resultDocs1 = evalQuery(queryQueue);
		display(resultDocs1);
	
				

	}
	

	public static LinkedList<WordFrequency> evalQuery(LinkedQueue<String> qq) {
		// Stacks for operands and operators
		LinkedStack<LinkedList<WordFrequency>> operandStack = new LinkedStack<>();
		LinkedStack<String> operatorStack = new LinkedStack<>();
		String previousWord = ""; // keeping track of pervious word

		// Process the query queue
		while (!qq.empty()) {
			String word = qq.serve().trim();

			if (word.isEmpty())
				continue; // Skip empty strings

			boolean isOperator = word.equalsIgnoreCase("AND") || word.equalsIgnoreCase("OR");

			if (isOperator && (previousWord.isEmpty() || previousWord.equalsIgnoreCase("AND")
					|| previousWord.equalsIgnoreCase("OR"))) {
				System.err.print("Invalid query: duplicated operators. Skipping this part.\n");
				continue; // Skip duplicate operators
			}

			if (isOperator) {
				String top = "";
				if (!operatorStack.empty()) {
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
	// Instead of just getting a list of strings, retrieve WordFrequency objects
				LinkedList<WordFrequency> docList = search(word); // Now returns a list of WordFrequency objects
				operandStack.push(docList);
			}
			previousWord = word;
		}
		while (!operatorStack.empty()) {
			processOperation(operandStack, operatorStack);
		}

		return operandStack.empty() ? new LinkedList<>() : operandStack.pop();
	}
	

	private static int precedence(String operator) { // Helper method for checking precedence where AND > OR
		if (operator.equalsIgnoreCase("AND")) {
			return 2;
		} else if (operator.equalsIgnoreCase("OR")) {
			return 1;
		}
		return 0;
	}

	private static LinkedList<WordFrequency> doIntersection(LinkedList<WordFrequency> list1,
			LinkedList<WordFrequency> list2) {
		LinkedList<WordFrequency> intersection = new LinkedList<>();

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
	

	private static LinkedList<WordFrequency> doUnion(LinkedList<WordFrequency> list1, LinkedList<WordFrequency> list2) {
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

	public static LinkedQueue<String> queryToQueue(String query) {
		LinkedQueue<String> qq = new LinkedQueue<>();

		StringTokenizer tokenizer = new StringTokenizer(query);
		while (tokenizer.hasMoreTokens()) {
			qq.enqueue(tokenizer.nextToken());
		}
		return qq;
	}

	private static LinkedList<WordFrequency> search(String word) { // giving a word it returns a list of reversed indecies
		LinkedList<WordFrequency> resultDocs = new LinkedList<>();

		// Iterate through the inverted index to find the word
		invertedIndex.findFirst();
		while (!invertedIndex.last()) {
			LinkedList<WordFrequency> currentEntry = invertedIndex.retrieve(); // takes LinkedList from invertedIndex list

			// Check if the first entry (the word) matches
			if (currentEntry.get(0).getDocId().equals(word)) {
				resultDocs = currentEntry;
				break;
			}

			invertedIndex.findNext();
		}

		// Check if the last element matches the word (for the last word in the list)
		LinkedList<WordFrequency> lastEntry = invertedIndex.retrieve();
		if (lastEntry.get(0).getDocId().equals(word)) {
			resultDocs = lastEntry;
		}

		return resultDocs;
	}

	private static void invertedIndexRanking() {
		for (int j = 0; j < docWords.getSize(); j++) {
			LinkedList<String> currentDoc = docWords.get(j);
			String docId = "" + (j ); // The docId should be "0", "1", "2", for  etc.

			for (int i = 0; i < currentDoc.getSize(); i++) {
				String word = currentDoc.get(i);
				WordFrequency wordNode = new WordFrequency(word, -1); // example: ("market",-1) => -1 is freq means that docId is word
				WordFrequency wordId = new WordFrequency(docId, 1); // First frequency for this word in this doc
				boolean wordExists = false;

				// Check if the word exists in the inverted index
				if (!invertedIndex.empty()) {
					invertedIndex.findFirst();
					while (!invertedIndex.last()) {
						LinkedList<WordFrequency> entryDoc = invertedIndex.retrieve();

						// If word already exists in inverted index
						if (entryDoc.get(0).equalsS(wordNode)) {
							wordExists = true;
							boolean docExists = false;

							// Check if the word is already present in the document's entry
							for (int k = 1; k < entryDoc.getSize(); k++) {
								if (entryDoc.get(k).getDocId().equals(docId)) {
									entryDoc.get(k).incrementFrequency(); // Increment frequency for this document
									docExists = true;
									break;
								}
							}
							if (!docExists) {
								entryDoc.insert(wordId); // Insert new entry for the doc if not found
							}
							break;
						}

						invertedIndex.findNext();
					}
					if (!wordExists) {
						LinkedList<WordFrequency> newEntry = new LinkedList<>();
						newEntry.insert(wordNode); // Insert wordNode 
						newEntry.insert(wordId); // Insert (first occurrence) for this document
						invertedIndex.insert(newEntry);
					}
				} else {
					LinkedList<WordFrequency> newEntry = new LinkedList<>();
					newEntry.insert(wordNode); // Insert  wordNode
					newEntry.insert(wordId); // Insert (first occurrence)for this document
					invertedIndex.insert(newEntry);
				}
			}
		}
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
		String processedText = text.toLowerCase().replaceAll("[^a-z0-9\\s']", " ");
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
		for(int i=0; i < docWords.getSize(); i++) {
			for(int j=0 ; j < docWords.get(i).getSize(); j++) {
				System.out.print(docWords.get(i).get(j)+ ",");
			}
			System.out.println();
		}
	}
	
	
	
	
	
	
}
