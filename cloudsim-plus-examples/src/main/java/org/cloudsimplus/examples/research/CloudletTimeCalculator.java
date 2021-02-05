package org.cloudsimplus.examples.research;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

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
  public static BigDecimal getAverageFinishTime(List<Cloudlet> finishedCloudlets) {
    BigDecimal averageFinishTime = BigDecimal.valueOf(0);

    int finishedCloudletLength = finishedCloudlets.size();

    for (int i = 0; i < finishedCloudletLength; i++) {
      // gets the sum of all cloudlet finish times
      averageFinishTime = averageFinishTime.add(BigDecimal.valueOf(finishedCloudlets.get(i).getFinishTime()));
    }

    averageFinishTime = averageFinishTime.divide(BigDecimal.valueOf(finishedCloudletLength), 2, RoundingMode.HALF_UP); // gets the average by dividing by number of finshed
                                                                    // cloudlets in the list
    return averageFinishTime;
  }

  /**
   * Gets the average execution (CPU) time of cloudlets. The execution time
   * represents the time a cloudlet spent running or {@link #NOT_ASSIGNED} if it
   * hasn't finished yet.
   * 
   * 
   * @param finishedCloudlets the list of cloudlets that finished execution
   * @return the average execution time of the given cloudlets
   */
  public static BigDecimal getAverageExecutionTime(List<Cloudlet> finishedCloudlets) {
    BigDecimal averageExecutuionTime = BigDecimal.valueOf(0);

    int finishedCloudletLength = finishedCloudlets.size();

    for (int i = 0; i < finishedCloudletLength; i++) {
      // gets the sum of all cloudlet execution times
      averageExecutuionTime = averageExecutuionTime.add(BigDecimal.valueOf(finishedCloudlets.get(i).getActualCpuTime()));
    }

    averageExecutuionTime = averageExecutuionTime.divide(BigDecimal.valueOf(finishedCloudletLength), 2, RoundingMode.HALF_UP);  // gets the average by dividing by number of
                                                                            // finished cloudlets in the list
    return averageExecutuionTime;
  }

  /**
   * Gets the average arrival time of cloudlets.
   * 
   * 
   * @param finishedCloudlets the list of cloudlets that finished execution
   * @return the average arrival time of the given cloudlets
   */
  public static BigDecimal getAverageArrivalTime(List<Cloudlet> finishedCloudlets) {
    BigDecimal averageArrivalTime = BigDecimal.valueOf(0);

    int finishedCloudletLength = finishedCloudlets.size();

    for (int i = 0; i < finishedCloudletLength; i++) {
      // gets the sum of all cloudlet arrival times
      averageArrivalTime = averageArrivalTime.add(BigDecimal.valueOf(finishedCloudlets.get(i).getLastDatacenterArrivalTime()));
    }

    averageArrivalTime = averageArrivalTime.divide(BigDecimal.valueOf(finishedCloudletLength), 2, RoundingMode.HALF_UP);  // gets the average by dividing by number of
                                                                      // finshed cloudlets in the list
    return averageArrivalTime;
  }

  /**
   * Gets the average turnaround time of cloudlets.
   * 
   * @param finishedCloudlets the list of cloudlets that finished execution
   * @return the average turnaround time of the given cloudlets
   */
  public static BigDecimal getAverageTurnaroundTime(List<Cloudlet> finishedCloudlets) {
    BigDecimal averageTurnaroundTime = BigDecimal.valueOf(0);

    int finishedCloudletLength = finishedCloudlets.size();

    for (int i = 0; i < finishedCloudletLength; i++) {
      averageTurnaroundTime = averageTurnaroundTime.add((BigDecimal.valueOf(finishedCloudlets.get(i).getFinishTime())
      .subtract(BigDecimal.valueOf(finishedCloudlets.get(i).getLastDatacenterArrivalTime()))));
    }

    averageTurnaroundTime = averageTurnaroundTime.divide(BigDecimal.valueOf(finishedCloudletLength), 2, RoundingMode.HALF_UP);  // gets the average by dividing by number of
                                                                            // finshed cloudlets in the list
    return averageTurnaroundTime; 
  }

  /**
   * Gets the average turnaround time of cloudlets that match a job id. 
   * 
   * @param jobId the id of the cloudlets to find the average turnaround time 
   * @param jobIdList the list of cloudlets that finished execution
   * @return the average turnaround time of the given cloudlets
   */
  public static BigDecimal getAverageTurnaroundTimeForJobId(long jobId, List<Cloudlet> finishedCloudlets) {
    BigDecimal averageTurnaroundTime = BigDecimal.valueOf(0);

    List<Cloudlet> jobIdList = finishedCloudlets.stream().filter(cloudlet -> cloudlet.getJobId() == jobId).collect(Collectors.toList());

    int jobIdCloudletLength = jobIdList.size();

    System.out.println("\nNumber of cloudlets with job id " + jobId + ": "  + jobIdCloudletLength);

    for (int i = 0; i < jobIdCloudletLength; i++) {
      averageTurnaroundTime = averageTurnaroundTime.add((BigDecimal.valueOf(jobIdList.get(i).getFinishTime())
      .subtract(BigDecimal.valueOf(jobIdList.get(i).getLastDatacenterArrivalTime()))));
    }

    averageTurnaroundTime = averageTurnaroundTime.divide(BigDecimal.valueOf(jobIdCloudletLength), 2, RoundingMode.HALF_UP); // gets the average by dividing by number of
                                                                          // finshed cloudlets in the list
    return averageTurnaroundTime;
  }
}
