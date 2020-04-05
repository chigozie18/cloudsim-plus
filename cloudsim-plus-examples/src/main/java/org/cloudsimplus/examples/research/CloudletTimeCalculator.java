package org.cloudsimplus.examples.research;

import java.util.List;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;



/**
 * A class for calculating time relating to cloudlets. 
 * 
 * 
 * @author Chigozie Asikaburu
 *
 */
public class CloudletTimeCalculator {

	
	/**
     * Gets the average finish time of cloudlets.
     * 
     * 
     * @param finishedCloudlets the list of cloudlets that finished execution
     * @return the average finish time of the given cloudlets
     */
    public static double getAverageFinishTime (List<Cloudlet> finishedCloudlets) {
    	double averageFinishTime = 0;
    	
    	int finishedCloudletLength = finishedCloudlets.size();
    	
    	for (int i = 0; i < finishedCloudletLength; i++) {
    		// gets the sum of all cloudlet finish times
    		averageFinishTime = averageFinishTime + finishedCloudlets.get(i).getFinishTime();
    	}
    	
    	averageFinishTime = averageFinishTime/finishedCloudletLength;  // gets the average by dividing by number of finshed cloudlets in the list

    	return averageFinishTime;

    }
    
    /**
     * Gets the average execution (CPU) time of cloudlets. The execution time represents the time a cloudlet spent running 
     * or {@link #NOT_ASSIGNED} if it hasn't finished yet.
     * 
     * 
     * @param finishedCloudlets the list of cloudlets that finished execution
     * @return the average execution time of the given cloudlets
     */
    public static double getAverageExecutionTime (List<Cloudlet> finishedCloudlets) {
    	double averageExecutuionTime = 0;
    	
    	int finishedCloudletLength = finishedCloudlets.size();
    	
    	for (int i = 0; i < finishedCloudletLength; i++) {
    		// gets the sum of all cloudlet execution times
    		averageExecutuionTime = averageExecutuionTime + finishedCloudlets.get(i).getActualCpuTime();
    	}
    	
    	averageExecutuionTime = averageExecutuionTime/finishedCloudletLength;  // gets the average by dividing by number of finished cloudlets in the list

    	return averageExecutuionTime;

    }
    
    
    /**
     * Gets the average arrival time of cloudlets.
     * 
     * 
     * @param finishedCloudlets the list of cloudlets that finished execution
     * @return the average arrival time of the given cloudlets
     */
    public static double getAverageArrivalTime (List<Cloudlet> finishedCloudlets) {
    	double averageArrivalTime = 0;
    	
    	int finishedCloudletLength = finishedCloudlets.size();
    	
    	for (int i = 0; i < finishedCloudletLength; i++) {
    		// gets the sum of all cloudlet arrival times
    		averageArrivalTime = averageArrivalTime + finishedCloudlets.get(i).getLastDatacenterArrivalTime();
    	}
    	
    	averageArrivalTime = averageArrivalTime/finishedCloudletLength;  // gets the average by dividing by number of finshed cloudlets in the list

    	return averageArrivalTime;

    }
    
	/**
     * Gets the average turnaround time of cloudlets.
     * 
     * @param finishedCloudlets the list of cloudlets that finished execution
     * @return the average turnaround time of the given cloudlets
     */
    public static double getAverageTurnaroundTime (List<Cloudlet> finishedCloudlets) {
		double averageTurnaroundTime = 0;
    	
    	int finishedCloudletLength = finishedCloudlets.size();
    	
    	for (int i = 0; i < finishedCloudletLength; i++) {
    		Datacenter cloudletDatacenter = finishedCloudlets.get(i).getVm().getHost().getDatacenter();
    		averageTurnaroundTime =  averageTurnaroundTime + finishedCloudlets.get(i).getFinishTime() - finishedCloudlets.get(i).getArrivalTime(cloudletDatacenter);
    	}
    	
    	averageTurnaroundTime = averageTurnaroundTime/finishedCloudletLength;  // gets the average by dividing by number of finshed cloudlets in the list

    	return averageTurnaroundTime;

    }
	
	
}
