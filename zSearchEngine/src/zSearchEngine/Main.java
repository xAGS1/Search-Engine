package zSearchEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
	static LinkedList<String> stopWords = new LinkedList<>();
	static LinkedList<LinkedList<String>> docWords = new LinkedList<>();
	// static BST<String> bst = new BST<>();

	public static void main(String[] args) {
		String fileCSV = "bin\\dataset.csv"; // <-- here File Path of CSV
		String fileStopWords = "bin\\stop.txt";// <-- here File Path of stopWords  (in bin folder)

		getStopWords(fileStopWords);
		processDoc(fileCSV);
		

		printDocWords();
	System.out.println(docWords.getSize());
	
	
	}
	
	
	
	private static void processDoc(String filePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String line;
			
			int docId = 0;
			while ((line = br.readLine()) != null) {
				LinkedList<String> wordsList = processTxt(line);
				docWords.insert(wordsList);
				docId++;
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
	
	public static LinkedList<String> processTxt(String text) {
        LinkedList<String> wordsList = new LinkedList<>();
        String processedText = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " "); // removes all non alphaumric
        StringBuilder word = new StringBuilder();  // for O(n)  haha

 
        for (char ch : processedText.toCharArray()) {
            if (ch == ' ') {
                String w = word.toString();
                if (!stopWords.contains(w) && !w.isEmpty()) {
                    wordsList.insert(w);
                }
                word.setLength(0); // Reset word
            } else {
                word.append(ch);
            }
        }
        return wordsList;
    }
	
	
	

	private static void getStopWords(String stopWordPath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(stopWordPath));
			String line;
			while ((line = br.readLine()) != null) {

				String stopWord = line.trim().toLowerCase();
				stopWords.insert(stopWord); // Insert into LinkedList of stop words
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	// Method to print the contents of docWords
	private static void printDocWords() {
	
	    for (int i = 0; i < docWords.getSize(); i++) {
	        LinkedList<String> wordsList = docWords.get(i);
	        System.out.print("Document " );
	      
	        for (int j = 0; j < wordsList.getSize(); j++) {
	            System.out.print(wordsList.get(j));
	            if (j < wordsList.getSize() - 1) {
	                System.out.print(", ");
	            }
	        }
	        System.out.println(); 
	    }
	}

}
