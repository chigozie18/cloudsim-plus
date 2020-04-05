package org.cloudsimplus.examples.research;

import java.text.DecimalFormat;
import java.util.List;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;


/**
 * A class for printing out various times (finish time, execution time, average time). 
 * 
 * @author Chigozie Asikaburu
 *
 */
public class CloudResults {

	/**
	 * Prints out extra results for cloudlets that have finished executing (average finish time, average execution time, average arrival time). It also prints out the arrival times and submission delays  
	 * for all cloudlets. It also prints out the arrival times and submission delays of all cloudlets. 
	 * 
	 * @param datacenter the datacenter that the finished cloudlets ran on 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printExtraResults (List<Cloudlet> finishedCloudlets) {
		printNumOfFinishedCloudlets(finishedCloudlets);
		printAverageFinishTime(finishedCloudlets);
		printAverageExecutionTime(finishedCloudlets);
		printAverageArrivalTime(finishedCloudlets);
		printAllArrivalTimes(finishedCloudlets);
		printAllSubmissionDelays(finishedCloudlets);
	}
	
	/**
	 * Prints out extra results for cloudlets that have finished executing (average finish time, average execution time, average arrival time). It also prints out the arrival times and submission delays  
	 * for all cloudlets. 
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printExtraResults2 (List<Cloudlet> finishedCloudlets) {
		printNumOfFinishedCloudlets(finishedCloudlets);
		printAverageFinishTime(finishedCloudlets);
		printAverageExecutionTime(finishedCloudlets);
		printAverageArrivalTime(finishedCloudlets);
		printAverageTurnaroundTime(finishedCloudlets);

	}
	
	/**
	 * Prints out the average finish time of all cloudlets that have finished executing.  
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAverageFinishTime (List<Cloudlet> finishedCloudlets) { 
		DecimalFormat df = new DecimalFormat("0.00");	

		System.out.println("\n");
		System.out.println("************* Average Finish Time *************");
		System.out.println("\nThe average finish time was: " + df.format(CloudletTimeCalculator.getAverageFinishTime(finishedCloudlets)));
		System.out.println("\n");
	}
	
	/**
	 * Prints out the average execution time of all cloudlets that have finished executing.  
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAverageExecutionTime (List<Cloudlet> finishedCloudlets) { 
		DecimalFormat df = new DecimalFormat("0.00");	

		System.out.println("\n");
		System.out.println("************* Average Execution Time *************");
		System.out.println("\nThe average execution time was: " + df.format(CloudletTimeCalculator.getAverageExecutionTime(finishedCloudlets)));
		System.out.println("\n");
	}
	
	/**
	 * Prints out the average arrival time of all cloudlets that have finished executing.  
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAverageArrivalTime (List<Cloudlet> finishedCloudlets) { 
		DecimalFormat df = new DecimalFormat("0.00");	

		System.out.println("\n");
		System.out.println("************* Average Arrival Time *************");
		System.out.println("\nThe average arrival time was: " + df.format(CloudletTimeCalculator.getAverageArrivalTime(finishedCloudlets)));
		System.out.println("\n");
	}
	
	/**
	 * Prints out the average arrival time of all cloudlets that have finished executing.  
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAverageTurnaroundTime (List<Cloudlet> finishedCloudlets) { 
		DecimalFormat df = new DecimalFormat("0.00");	

		System.out.println("\n");
		System.out.println("************* Average Turnaround Time *************");
		System.out.println("\nThe average turnaround time was: " + df.format(CloudletTimeCalculator.getAverageTurnaroundTime(finishedCloudlets)));
		System.out.println("\n");
	}
	
	/**
	 * Prints out all arrival times of cloudlets that have finished executing.  
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAllArrivalTimes (List<Cloudlet> finishedCloudlets) { 
		DecimalFormat df = new DecimalFormat("0.00");	

		System.out.println("Arrival Times: \n");
		for (int i = 0; i < finishedCloudlets.size(); i++) {
			// gets the sum of all cloudlet finish times
			System.out.println("Cloudlet " + finishedCloudlets.get(i).getId() + " arrived at : " + df.format(finishedCloudlets.get(i).getLastDatacenterArrivalTime()) + " seconds");
		}
		System.out.println("\n");
	}
	
	/**
	 * Prints out all submission delays of cloudlets that have finished executing.  
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAllSubmissionDelays (List<Cloudlet> finishedCloudlets) { 
		DecimalFormat df = new DecimalFormat("0.00");	

		System.out.println("\n");
		for (int i = 0; i < finishedCloudlets.size(); i++) {
			// gets the sum of all cloudlet finish times
			System.out.println("Cloudlet " + finishedCloudlets.get(i).getId() + " had a submission delay of: " + df.format(finishedCloudlets.get(i).getSubmissionDelay()) + " seconds");
		}
		System.out.println("\n");
	}
	
	/**
	 * Prints out the number of cloudlets that have finished executing.  
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printNumOfFinishedCloudlets (List<Cloudlet> finishedCloudlets) { 
        System.out.println("\n\nAmount of Cloudlets that finished executing: " + finishedCloudlets.size() + "\n");
	}
}
