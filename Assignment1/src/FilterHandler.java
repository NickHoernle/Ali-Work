public class FilterHandler {
	
	Double[] input;
	Double[] outputSerial;
	int filterSize;
	
	public FilterHandler(){
		input = null;
		outputSerial = null;
		filterSize = 0;	
	}

	public FilterHandler(int filter, Double[] in ){
		this.input = in;
		this.outputSerial = new Double[input.length];
		this.filterSize = filter;
	}
		
	public void filterTheInputSerial() {
		MedianFilter filterObj;
		int border = filterSize/2;
		// insert border into output list
		for (int i = 0; i < border; i++){
			outputSerial[i] = input[i];
		}
		
		for ( int i = border ; i < outputSerial.length - border; i ++ ) {
			Double[] temp = new Double[filterSize];
			int tempPos = 0;
			int tempPosEnd = i - border + filterSize;
			for (int x = i-border; x < tempPosEnd; x++){
				temp[tempPos] = input[x];
				tempPos ++;
			}
			filterObj = new MedianFilter(temp);
			outputSerial[i] = filterObj.getMedian();
		}
			
		//now add the final elements to the end of the list (the unchanged border)
		for (int i = outputSerial.length -border; i < outputSerial.length; i++){
			outputSerial[i] = input[i];
		}
	}
	
	public Double[] getOuputSerial() {
		return this.outputSerial;
	}
}