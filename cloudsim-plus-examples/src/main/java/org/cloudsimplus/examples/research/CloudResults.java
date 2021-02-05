package org.cloudsimplus.examples.research;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

/**
 * A class for printing out various times (finish time, execution time, average
 * time).
 * 
 * @author Chigozie Asikaburu
 *
 */
public class CloudResults {
	
	private static DecimalFormat df = new DecimalFormat("0.00");

	/**
	 * Prints out extra results for cloudlets that have finished executing (average
	 * finish time, average execution time, average arrival time). It also prints
	 * out the arrival times and submission delays for all cloudlets. It also prints
	 * out the arrival times and submission delays of all cloudlets.
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printExtraResults(List<Cloudlet> finishedCloudlets) {
		printNumOfFinishedCloudlets(finishedCloudlets);
		printAverageFinishTime(finishedCloudlets);
		printAverageExecutionTime(finishedCloudlets);
		printAverageArrivalTime(finishedCloudlets);
		printAllArrivalTimes(finishedCloudlets);
		printAllSubmissionDelays(finishedCloudlets);
	}

	/**
	 * Prints out extra results for cloudlets that have finished executing (average
	 * finish time, average execution time, average arrival time, average turnaround
	 * time).
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printExtraResults2(List<Cloudlet> finishedCloudlets) {
		printNumOfFinishedCloudlets(finishedCloudlets);
		printAverageFinishTime(finishedCloudlets);
		printAverageExecutionTime(finishedCloudlets);
		printAverageArrivalTime(finishedCloudlets);
		printAverageTurnaroundTime(finishedCloudlets);
	}

	/**
	 * Prints out the average finish time of all cloudlets that have finished
	 * executing.
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAverageFinishTime(List<Cloudlet> finishedCloudlets) {	
		BigDecimal averageFinishTime = CloudletTimeCalculator.getAverageFinishTime(finishedCloudlets);
		averageFinishTime.setScale(2, RoundingMode.HALF_UP);
		System.out.println("\n");
		System.out.println("************* Average Finish Time *************");
		System.out.println("\nThe average finish time was: " + averageFinishTime);
		System.out.println("\n");
	}

	/**
	 * Prints out the average execution time of all cloudlets that have finished
	 * executing.
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAverageExecutionTime(List<Cloudlet> finishedCloudlets) {
		BigDecimal averageExecutionTime = CloudletTimeCalculator.getAverageExecutionTime(finishedCloudlets);
		averageExecutionTime.setScale(2, RoundingMode.HALF_UP);
		System.out.println("\n");
		System.out.println("************* Average Execution Time *************");
		System.out.println("\nThe average execution time was: " + averageExecutionTime);
		System.out.println("\n");
	}

	/**
	 * Prints out the average arrival time of all cloudlets that have finished
	 * executing.
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAverageArrivalTime(List<Cloudlet> finishedCloudlets) {
		BigDecimal averageArrivalTime = CloudletTimeCalculator.getAverageArrivalTime(finishedCloudlets);
		averageArrivalTime.setScale(2, RoundingMode.HALF_UP);
		System.out.println("\n");
		System.out.println("************* Average Arrival Time *************");
		System.out.println("\nThe average arrival time was: " + averageArrivalTime);
		System.out.println("\n");
	}

	/**
	 * Prints out the average arrival time of all cloudlets that have finished
	 * executing.
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAverageTurnaroundTime(List<Cloudlet> finishedCloudlets) {
		BigDecimal averageTurnaroundTime = CloudletTimeCalculator.getAverageTurnaroundTime(finishedCloudlets);
		averageTurnaroundTime.setScale(2, RoundingMode.HALF_UP);
		System.out.println("\n");
		System.out.println("************* Average Turnaround Time *************");
		System.out.println("\nThe average turnaround time was: " + averageTurnaroundTime);
		System.out.println("\n");
	}

	/**
	 * Prints out the average turnaround time of all cloudlets with a job id of 1 or 2.
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAverageTurnaroundTimeJobId(List<Cloudlet> finishedCloudlets) {
		BigDecimal averageTurnaroundTimeJobId1 = CloudletTimeCalculator.getAverageTurnaroundTimeForJobId(1,finishedCloudlets);
		averageTurnaroundTimeJobId1.setScale(2, RoundingMode.HALF_UP);
		BigDecimal averageTurnaroundTimeJobId2 = CloudletTimeCalculator.getAverageTurnaroundTimeForJobId(2, finishedCloudlets);
		averageTurnaroundTimeJobId2.setScale(2, RoundingMode.HALF_UP);
		System.out.println("\n");
		System.out.println("************* Average Turnaround Time (Job ID 1) *************");

		System.out.println("\nThe average turnaround time was: " + averageTurnaroundTimeJobId1);
		System.out.println("\n");

		System.out.println("\n");
		System.out.println("************* Average Turnaround Time (Job ID 2) *************");
		System.out.println("\nThe average turnaround time was: " + averageTurnaroundTimeJobId2);
		System.out.println("\n");
	}

	/**
	 * Prints out the average turnaround time of all cloudlets with a job id of 1, 2 or 3.
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAverageTurnaroundTimeAllJobId(List<Cloudlet> finishedCloudlets) {
		BigDecimal averageTurnaroundTimeJobId1 = CloudletTimeCalculator.getAverageTurnaroundTimeForJobId(1, finishedCloudlets);
		//averageTurnaroundTimeJobId1.setScale(2, RoundingMode.HALF_UP);
		BigDecimal averageTurnaroundTimeJobId2 = CloudletTimeCalculator.getAverageTurnaroundTimeForJobId(2, finishedCloudlets);
		//averageTurnaroundTimeJobId2.setScale(2, RoundingMode.HALF_UP);
		BigDecimal averageTurnaroundTimeJobId3 = CloudletTimeCalculator.getAverageTurnaroundTimeForJobId(3, finishedCloudlets);
		//averageTurnaroundTimeJobId3.setScale(2, RoundingMode.HALF_UP);
		System.out.println("\n");
		System.out.println("************* Average Turnaround Time (Job ID 1) *************");

		System.out.println("\nThe average turnaround time was: " + averageTurnaroundTimeJobId1);
		System.out.println("\n");

		System.out.println("\n");
		System.out.println("************* Average Turnaround Time (Job ID 2) *************");
		System.out.println("\nThe average turnaround time was: " + averageTurnaroundTimeJobId2);
		System.out.println("\n");

		System.out.println("\n");
		System.out.println("************* Average Turnaround Time (Job ID 3) *************");
		System.out.println("\nThe average turnaround time was: " + averageTurnaroundTimeJobId3);
		System.out.println("\n");
	}

	/**
	 * Prints out all arrival times of cloudlets that have finished executing.
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAllArrivalTimes(List<Cloudlet> finishedCloudlets) {
		// sorts cloudlets based on their arrival (from earliest to latestest)
		List<Cloudlet> tempFinishedCloudlets = finishedCloudlets.stream()
				.sorted(Comparator.comparingDouble(Cloudlet::getLastDatacenterArrivalTime))
				.collect(Collectors.toList());

		System.out.println("Arrival Times: \n");
		for (int i = 0; i < tempFinishedCloudlets.size(); i++) {
			// gets the sum of all cloudlet finish times
			System.out.println("Cloudlet " + tempFinishedCloudlets.get(i).getId() + " arrived at : "
					+ df.format(tempFinishedCloudlets.get(i).getLastDatacenterArrivalTime()) + " seconds");
		}
		System.out.println("\n");
	}

	/**
	 * Prints out all arrival times of cloudlets along with their job ids that have
	 * finished executing.
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAllArrivalTimesWithJobID(List<Cloudlet> finishedCloudlets) {
		// sorts cloudlets based on their arrival (from earliest to latestest)
		List<Cloudlet> tempFinishedCloudlets = finishedCloudlets.stream()
		.sorted(Comparator.comparingDouble(Cloudlet::getLastDatacenterArrivalTime))
		.collect(Collectors.toList());

		System.out.println("Arrival Times: \n");
		for (int i = 0; i < tempFinishedCloudlets.size(); i++) {
			// gets the sum of all cloudlet finish times
			System.out.println("Cloudlet " + tempFinishedCloudlets.get(i).getId() + " with a job id of: "
					+ tempFinishedCloudlets.get(i).getJobId() + " arrived at : "
					+ df.format(tempFinishedCloudlets.get(i).getLastDatacenterArrivalTime()) + " seconds");
		}
		System.out.println("\n");
	}

	/**
	 * Prints out all submission delays of cloudlets that have finished executing.
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printAllSubmissionDelays(List<Cloudlet> finishedCloudlets) {
		System.out.println("\n");
		for (int i = 0; i < finishedCloudlets.size(); i++) {
			// gets the sum of all cloudlet finish times
			System.out.println("Cloudlet " + finishedCloudlets.get(i).getId() + " had a submission delay of: "
					+ df.format(finishedCloudlets.get(i).getSubmissionDelay()) + " seconds");
		}
		System.out.println("\n");
	}

	/**
	 * Prints out the number of cloudlets that have finished executing.
	 * 
	 * @param finishedCloudlets the list of all finished cloudlets
	 */
	public static void printNumOfFinishedCloudlets(List<Cloudlet> finishedCloudlets) {
		System.out.println("\n\nAmount of Cloudlets that finished executing: " + finishedCloudlets.size() + "\n");
	}
}
