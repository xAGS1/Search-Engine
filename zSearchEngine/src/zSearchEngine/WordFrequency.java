package zSearchEngine;

public class WordFrequency {
	private String docId;
	private int frequency;
	
	  public WordFrequency(String docId, int frequency) {
	        this.docId = docId;
	        this.frequency = frequency;
	    }

	    public String getDocId() {
	        return docId;
	    }

	    public int getFrequency() {
	        return frequency;
	    }

	    public void incrementFrequency() {
	        this.frequency++;
	    }
	
	    public String toString() {
	        return "(" + docId + ", " + frequency + ")";
	    }
	    
	    public boolean equalsS(WordFrequency wf) {
	    	return this.docId.equals(wf.docId);
	    }

		public void setFrequency(int i) {
			frequency = i;
			
		}
	    
		
		
}
	