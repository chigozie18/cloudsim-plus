package org.cloudsimplus.examples.research;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.Random;


/**
 * 
 * A class containing methods for generating parameters for datacenters, hosts, vms and cloudlets. The generated parameters are stored 
 * in a CSV file with a vertical bar (|) to denote delimiters.
 * 
 * @author chigozieasikaburu
 *
 */
public class CloudParameterGenerator {

	
	public static void main(String[] args) {
		String[] utilizationModels = {"full"};  // the set of utilization models a cloudlet is allowed to have

		// The maximum amount of delay a cloudlet can have is 10 seconds
		CloudParameterGenerator.generateRandomCloudletParameters(10, 2, 500, 5000, 0, 10, utilizationModels, 0, 0);  
		// For full utilization model cloudlets the last two parameters can ignored and can be any value hence the two zeros	
	
	}

	/**
	 * Generates random cloudlet parameters within a user-defined range in a CSV file. The minimum cloudlet processor entities (pes), lengths, and sizes are at least 1.
	 * 
	 * 
	 * @param cloudletNum the number of random cloudlet parameters to be generated  
	 * @param pes the maximum number of pes a cloudlet can have
	 * @param length the maximum length of a cloudlet can have 
	 * @param size the maximum size a cloudlet can be 
	 * @param submissionDelayMin the minimum submission delay a cloudlet can have 
	 * @param submissionDelayMax the maximum submission delay a cloudlet can have 
	 * @param utilizationModels the set of allowable utilization models (either "dynamic" or "full")
	 * @param utilizationPercentageMin the minimum utilization a cloudlet with a dynamic utilization model can have (this parameter is ignored for cloudlet with a full utilization model) 
	 * @param utilizationPercentageMax the maximum utilization a cloudlet with a dynamic utilization model can have (this parameter is ignored for cloudlet with a full utilization model) 
	 */
	public static void generateRandomCloudletParameters(int cloudletNum, int pes, int length, int size, double submissionDelayMin, 
			double submissionDelayMax, String[] utilizationModels, double utilizationPercentageMin, double utilizationPercentageMax) {

		Random rand = new Random();
		int utilModelLength = utilizationModels.length;	
		DecimalFormat df = new DecimalFormat("0.00");	

		try {
			File file = new File("Test_Data/Demos (3:6)/DatacenterCloudlets(3:6_Demo).csv");
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
		} 

		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Generates a random double value from a user-defined range.
	 * 
	 * @param start the minimum double value in the range
	 * @param end the maximum double value in the range 
	 * 
	 * @return a random double value 
	 */
	public static double randomDoubleRange(double start, double end) {
		
		double random = new Random().nextDouble();
		double randomdouble = start + (random * (end - start));  // creates a range of random doubles from 0.05 to 1.0		            	            		
		
		return randomdouble;
	}
}


