/*
 * Ali Hoernle HRNALI002
 * 30 July 2015
 * This is the sequential program for median filtering a set of data
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;


public class Driver {

	static Double[] dataList; //create the array to be filtered
	static Double[] result; //the filtered array.
	static Double[] resultParallel; //the filtered array.
	static String inputFile, outputFile;
	static int filterSize, fileSize;
	static final ForkJoinPool fjPool = new ForkJoinPool();
	int sq; //variable for sequential limit
	static float serialAveTime, parallelAveTime;
	static long startTime;
	static float time;
	
	//Create forkJoin Pool
	static Double[] getMedian(Double[] arr, Double[] arr2, int sq){
		return fjPool.invoke(new FilterHandlerParallel(filterSize, arr, arr2, 0, arr.length, sq));
	}
	
	public static void main(String args[]) throws FileNotFoundException{
		PrintWriter writer = new PrintWriter("dataOut.txt"); //going to write results to file for report to compare serial and parallel versions.
		startTime = 0;
		
		/*inputFile = args[0];
		filterSize = Integer.parseInt(args[1]);
		outputFile = args[2];
		*/
		filterSize = 5;
		outputFile = "dataOut.txt";
		
		writer.print("Sequential Cutoff");
		String[] inputFiles = {"inp2.txt","inp3.txt","inp4.txt"};
		ArrayList<Float> parallelTimesFinal;
		ArrayList<Float> serialTimesFinal;

		//get correct number of processors running on the machine
		Runtime runtime = Runtime.getRuntime();
		int nrOfProcessors = runtime.availableProcessors();
		System.out.println("Number of processors available: " + nrOfProcessors);
			
		startTime = tick();
		time = toc( startTime ); //Time to load the file (serial operation)
		System.out.println("The time to load the data from file was " + time + " milliseconds");
			
		for (int sq = 1000; sq <= 100000 ; sq = sq + 1000) {
			parallelTimesFinal = new ArrayList<Float>();
			serialTimesFinal = new ArrayList<Float>();
			
			for ( String inputFile : inputFiles ) {  
				load(inputFile); //Load the input file
				if ( sq == 500 ) 
					writer.print("	Serial Time " + inputFile + "	Parallel Time" + inputFile);

				float[] serialTimes = new float[5]; //list for all the serial runs
				float[] parallelTimes = new float[5]; //list for all the parallel runs
				
				FilterHandler object;
				
				//Run the process 5 times to account for any warming up of the system and to therefore get more accurate results.
				for (int i = 0; i < 5; i++){
					startTime = tick(); //start the timer
					object = new FilterHandler(filterSize, dataList);
					object.filterTheInputSerial();
					result = object.getOuputSerial();
					time = toc( startTime ); //get the total run time of the filtering process
					serialTimes[i] = time; //put all the readings into an array
				}
				
				Arrays.sort(serialTimes);
				float minSerial = serialTimes[0];
				float secondMinSerial = serialTimes[1];
			
				serialAveTime = (minSerial + secondMinSerial)/2f;
				
				serialTimesFinal.add(serialAveTime);
				
				//Run the process 5 times to account for any warming up of the system and to therefore get more accurate results.
				for (int i = 0; i < 5; i++){
					startTime = tick(); //start the timer
					resultParallel = new Double[dataList.length];
					resultParallel = getMedian( dataList, resultParallel, sq );
					time = toc( startTime ); //get the total run time of the filtering process
					parallelTimes[i] = time;
				}
				
				Arrays.sort(parallelTimes);
				float minParallel = parallelTimes[0];
				float secondMinParallel = parallelTimes[1];
				
				parallelAveTime = (minParallel + secondMinParallel)/2f;		
				parallelTimesFinal.add(parallelAveTime);

			}
			if (sq == 500 )
				writer.println();
			writer.print( sq );
			for ( int i = 0; i < parallelTimesFinal.size() ; i ++ ){
				writer.print( "	" + serialTimesFinal.get(i) + "	" + parallelTimesFinal.get(i) );
			}
			writer.println();
		}
		startTime = tick();
		//output(outputFile); //print the output to file;
		time = toc(startTime);
		System.out.println("The time to print the result to file was " + time + " milliseconds");
		writer.close(); //close the file
	}

	private static long tick(){
		return System.nanoTime();
	}
	private static float toc(long startTime){
		return (System.nanoTime() - startTime)/1000000f; 
	}
	
	/*
	 * This method reads in the data from the file and inserts it into an array
	 */
	public static void load(String filename) throws FileNotFoundException{
		try{
			File file = new File(filename);
			Scanner scanner = new Scanner(file);
			fileSize = Integer.parseInt(scanner.nextLine()); //take away the number of lines in the file
			
			String currentLine;
			dataList = new Double[fileSize];
			int pos = 0;
			while (scanner.hasNext()){
				currentLine = scanner.nextLine();
				String[] parts = currentLine.split(" ");
				Double value = Double.parseDouble(parts[1]);
				dataList[pos] = value;
				pos++;
			}
			
			scanner.close();
		}
		catch (IOException e) {
		    System.out.println("Error during reading file");
		}
		
	}
	
	/*
	 * This method outputs the returned median filtered list to the output file.
	 */
	public static void output(String outputFile){
		try {
			//create an print writer for writing to a file
			PrintWriter writer = new PrintWriter(outputFile);
			writer.println(fileSize);
			
		    //output to the file the line number plus the actual line element.
		    for (int i = 0; i < fileSize; i++){
		    	boolean writtenToFile = false;
		    	//Check that the serial and parallel gives the same result and then print it to the output file
		    	Double valueSerial = result[i];
			    Double valueParallel = resultParallel[i];
			    if (valueParallel != null){ 
			    	if (valueSerial.compareTo(valueParallel) == 0){
			    		writer.println(i+1 + " " + valueParallel);
			    		writtenToFile = true;
			    	}
			    }
			    
			    if (writtenToFile == false){
				    System.out.println("Serial and parallel results are not the same");
				    break;
			    }
		    }
		    
		    writer.close(); //close the file
		}
		catch(IOException e) {
		    System.out.println("Error during writing to file");
		}
	}
}