package org.cloudsimplus.examples.research;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.Random;


/**
 * 
 * A class containing methods for generating parameters for cloudlets. The generated parameters are stored 
 * in a CSV file with a vertical bar (|) to denote delimiters.
 * 
 * @author Chigozie Asikaburu
 *
 */
public class CloudParameterGenerator {

	private static String localDirectory = System.getProperty("user.dir");

	public static void main(String[] args) {

		String filePath = localDirectory + "/cloudsim-plus-examples/Test_Data/Research Scenarios/Workload2/Part2/Scenario8/Datacenter1_Cloudlets.csv";
		CloudParameterGenerator.generateRandomCloudletParameters(filePath, 450, 1, 5000, 15000, 1000, 0, 600, 1);
		// For full utilization model cloudlets the last two parameters can ignored and can be any value hence the two zeros	
	}

	/**
	 * Generates random cloudlet parameters within a user-defined range in a CSV file. The minimum cloudlet processor entities (pes), lengths and sizes generated will be 
	 * at least 1. Utilization percentage parameters for this method take a double value ranging from 0 (0%) to 1 (100%). The utilization percentage parameters 
	 * are ignored when a cloudlet's model is set to full as it's always 1 (100%). Therefore, the parameter is only used when cloudlets with a dynamic utilization model are generated. 
	 * 
	 * @param filePath the file path containing the CSV file to write data to 
	 * @param cloudletNum the number of random cloudlet parameters to be generated  
	 * @param pes the maximum number of pes a cloudlet can have
	 * @param length the maximum length (in mips) a cloudlet can have 
	 * @param size the maximum size (in bytes) a cloudlet can be 
	 * @param submissionDelayMin the minimum submission delay (in seconds) a cloudlet can have 
	 * @param submissionDelayMax the maximum submission delay (in seconds)a cloudlet can have 
	 * @param utilizationModels the set of allowable utilization models (either "dynamic" or "full")
	 * @param utilizationPercentageMin the minimum utilization a cloudlet with a dynamic utilization model can have (this parameter is ignored for cloudlet with a full utilization model) 
	 * @param utilizationPercentageMax the maximum utilization a cloudlet with a dynamic utilization model can have (this parameter is ignored for cloudlet with a full utilization model) 
	 */
	public static void generateRandomCloudletParameters(String filePath, int cloudletNum, int pes, int length, int size, double submissionDelayMin, 
			double submissionDelayMax, String[] utilizationModels, double utilizationPercentageMin, double utilizationPercentageMax) {

		Random rand = new Random();
		int utilModelLength = utilizationModels.length;	
		DecimalFormat df = new DecimalFormat("0.00");	

		try {
			File file = new File(filePath);
			FileWriter fileReader = new FileWriter(file); // A stream that connects to the text file
			BufferedWriter bufferedWriter = new BufferedWriter(fileReader); // Connect the FileWriter to the BufferedWriter

			for (int i = 0; i < cloudletNum; i++) {

				int pesRandom = rand.nextInt(pes) + 1;
				int lengthRandom = rand.nextInt(length) + 1;
				int sizeRandom = rand.nextInt(size) + 1;
				double submissionDelayRandom = randomDoubleRange(submissionDelayMin, submissionDelayMax);  // The minimum submission delay is allowed to be zero hence no delay 
				String utilModelRandom = utilizationModels[rand.nextInt(utilModelLength)];  // randomly chooses one of the two allowable utilization models 

				StringBuilder sb = new StringBuilder();
				sb.append(pesRandom + "|" + lengthRandom + "|" + sizeRandom + "|" + df.format(submissionDelayRandom) + "|" + utilModelRandom);
				
				if (utilModelRandom.equals("dynamic")) {	
					double utilRandom = randomDoubleRange(utilizationPercentageMin, utilizationPercentageMax);  // creates a range of random doubles
																		
					sb.append("|" + df.format(utilRandom));
				}

				if (utilModelRandom.equals("full")) {
					sb.append("|" + 1.0);   // will never be used but done to keep the CSV file format consistent with cloudlets using dynamic and full utilization models
				}

				bufferedWriter.write(sb + "\n");		
			}

			bufferedWriter.close(); // Close the stream	
			System.out.println("Successfully wrote to a file!");
		} 

		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Generates random cloudlet parameters within a user-defined range in a CSV file. This method always generates cloudlets with a utilization model of full. 
	 * The PEs and the size of the cloudlets are always fixed (defined by the parameters) and will not be randomized. This method only takes an integer for 
	 * a submission delay range and generates cloudlet submission delays containing decimal integers (ex. 1.0, 3.0, 7.0).
	 * 
	 * @param filePath the file path containing the CSV file to write data to 
	 * @param cloudletNum the number of random cloudlet parameters to be generated  
	 * @param pes the number of pes all cloudlets 
	 * @param lengthMin the minimum length (in mips) a cloudlet can have 
	 * @param lengthMax the maximum length (in mips) a cloudlet can have 
	 * @param size the size (in bytes) of all cloudlets
	 * @param submissionDelayMin the minimum submission delay (in seconds) a cloudlet can have 
	 * @param submissionDelayMax the maximum submission delay (in seconds) a cloudlet can have 
	 * @param jobId the type of job this cloudlet belongs to
	 */
	public static void generateRandomCloudletParameters(String filePath, int cloudletNum, int pes, int lengthMin, int lengthMax, int size, int submissionDelayMin, 
			int submissionDelayMax, int jobId) {

		DecimalFormat df = new DecimalFormat("0.00");	

		try {
			File file = new File(filePath);
			FileWriter fileReader = new FileWriter(file); // A stream that connects to the text file
			BufferedWriter bufferedWriter = new BufferedWriter(fileReader); // Connect the FileWriter to the BufferedWriter

			for (int i = 0; i < cloudletNum; i++) {

				int lengthRandom = randomIntegerRange(lengthMin, lengthMax);
				double submissionDelayRandom = randomIntegerRange(submissionDelayMin, submissionDelayMax);  // The minimum submission delay is allowed to be zero hence no delay 

				StringBuilder sb = new StringBuilder();
				sb.append(pes + "|" + lengthRandom + "|" + size + "|" + df.format(submissionDelayRandom) + "|" + "full");

				sb.append("|" + 1.0);     // will never be used but done to keep the CSV file format consistent with cloudlets using dynamic and full utilization models
				sb.append("|" + jobId);
				
				bufferedWriter.write(sb + "\n");		
			}
			
			bufferedWriter.close(); // Close the stream
			System.out.println("Successfully wrote to a file!");
		}           

		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Generates a random integer between a min (inclusive) and max (inclusive). 
	 * 
	 * @param min the minimum integer (inclusive) to be generated
	 * @param max the maximum integer (exclusive) to be generated 
	 * @return
	 */
	private static int randomIntegerRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	/**
	 * Generates a random double between a min (inclusive) and max (inclusive). 
	 * 
	 * @param min the minimum double value in the range
	 * @param max the maximum double value in the range 
	 * 
	 * @return a random double value 
	 */
	private static double randomDoubleRange(double min, double max) {
		
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		
		double random = new Random().nextDouble();
		double randomdouble = min + (random * (max - min));  // creates a range of random doubles from 0.05 to 1.0		            	            		
		
		return randomdouble;
	}
}


