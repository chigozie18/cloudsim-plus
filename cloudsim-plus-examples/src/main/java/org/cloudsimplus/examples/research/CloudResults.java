package org.cloudsimplus.examples.research;

import java.text.DecimalFormat;
import java.util.List;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

public class CloudResults {


	/**
	 * Prints out extra results for Cloudlets that have finished executing (average finish time, average execution time, average arrival time). It also prints out the arrival times and submission delays  
	 * for all Cloudlets. 
	 * 
	 * @param datacenter the datacenter that the finished cloudlets ran on 
	 * @param finishedCloudlets the list of all finished c
	 */
	public static void printExtraResults (List<Cloudlet> finishedCloudlets) {

		DecimalFormat df = new DecimalFormat("0.00");	

		System.out.println("\n");

		System.out.println("************* Average Finish Time *************");

		System.out.println("\nThe average finish time was: " + CloudletTimeCalculator.getAverageFinishTime(finishedCloudlets));

		System.out.println("\n");

		System.out.println("************* Average Execution Time *************");

		System.out.println("\nThe average execution time was: " + CloudletTimeCalculator.getAverageExecutionTime(finishedCloudlets));

		System.out.println("\n");


		System.out.println("Arrival Times: \n");

		for (int i = 0; i < finishedCloudlets.size(); i++) {
			// gets the sum of all cloudlet finish times
			System.out.println("Cloudlet " + finishedCloudlets.get(i).getId() + " arrived at : " + df.format(finishedCloudlets.get(i).getLastDatacenterArrivalTime()) + " seconds");

		}

		System.out.println("\n");


		System.out.println("************* Average Arrival Time *************");


		System.out.println("\nThe average arrival time was: " + CloudletTimeCalculator.getAverageArrivalTime(finishedCloudlets));

		System.out.println("\n");

		for (int i = 0; i < finishedCloudlets.size(); i++) {
			// gets the sum of all cloudlet finish times
			System.out.println("Cloudlet " + finishedCloudlets.get(i).getId() + " had a submission delay of: " + df.format(finishedCloudlets.get(i).getSubmissionDelay()) + " seconds");

		}
	}
}
