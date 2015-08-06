import java.util.Arrays;

/*
 * This class is the main filtering class to filter the median of each input array
 */

public class MedianFilter {
	
	Double[] toFilter;
	double median;
	
	public MedianFilter(){
		toFilter = null;
		median = 0;
	}
	
	public MedianFilter(Double[] list){
		this.toFilter = list;
		median = filter( toFilter );
		//System.out.println(median);
	}
	
	private double filter(Double[] toFilt) {
		int filterSize = toFilt.length;
		int border = filterSize/2;
		
		//Sort the toSort list here
		Arrays.sort(toFilt);
		
		//get the median value and add it back to the output list at the current position.
		//System.out.println(toFilt[border]);
		return toFilt[border];
	}

	public Double getMedian() {
		//System.out.println(median);
		return this.median;
	}
}