import java.util.concurrent.RecursiveTask;

public class FilterHandlerParallel extends RecursiveTask<Double[]> {

	private int sequentialLimit;
	Double[] input;
	Double[] outputParallel;
	int filterSize;
	private int hi;
	private int low;
	boolean beginningBorder = false; //for the border cases.
	boolean endBorder = false;
	
	public FilterHandlerParallel(){
		input = null;
		sequentialLimit = 500;
		outputParallel = null;
		filterSize = 0;
		hi = 0;
		low = 0;
	}

	public FilterHandlerParallel(int filter, Double[] in, Double[] out, int l, int h, int sq ){
		this.input = in;
		this.outputParallel = out;
		this.filterSize = filter;
		this.low = l;
		this.hi = h;	
		sequentialLimit = sq;
	}
	
	@Override
	protected Double[] compute() {
		int border = filterSize/2;
		
		//beginning border values
		if (low < border && beginningBorder == false){
			for (int i = 0; i < border; i++){
				outputParallel[i] = input[i];
			}
			beginningBorder = true;
		}
		
		if (hi > input.length - border && endBorder == false){
			for (int i = input.length - border; i < input.length; i++){
				outputParallel[i] = input[i];
			}
			endBorder = true;
		}
		
		if (( hi - low < sequentialLimit )){
			MedianFilter filterObj;
		
			if ( ( hi < input.length - border ) && ( low >= border ) ) {
				for (int i = low; i < hi; i ++ ) {
					Double[] temp = new Double[filterSize];
					for ( int k = -border; k <= border; k++ ){
						temp[ k + border] = input[i + k];
					}
					filterObj = new MedianFilter(temp);
					outputParallel[i] = filterObj.getMedian();
				}
			}
			
			else if (low < border){
				for (int i = low + border; i < hi; i ++ ) {
					Double[] temp = new Double[filterSize];
					for ( int k = -border; k <= border; k++ ){
						temp[ k + border] = input[i + k];
					}
					filterObj = new MedianFilter(temp);
					outputParallel[i] = filterObj.getMedian();
				}
			}
			
			else if (hi >= input.length - border){
				for (int i = low; i < hi - border; i++){
					Double[] temp = new Double[filterSize];
					for ( int k = -border; k <= border; k++ ){
						temp[ k + border] = input[i + k];
					}
					filterObj = new MedianFilter(temp);
					outputParallel[i] = filterObj.getMedian();
				}
			}
			return outputParallel;
		}
		
		else {
			FilterHandlerParallel left = new FilterHandlerParallel(filterSize, input, outputParallel, low, (low+hi)/2, sequentialLimit );
			FilterHandlerParallel right= new FilterHandlerParallel(filterSize, input, outputParallel, (hi+low)/2,hi, sequentialLimit);
			left.fork();
    	    right.compute();
    	    left.join(); 
    	    return outputParallel;
		}
	}
}