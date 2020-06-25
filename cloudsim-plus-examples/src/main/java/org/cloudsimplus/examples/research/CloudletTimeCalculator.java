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
   * Gets the average turnaround time of cloudlets with a job id of 1.
   * 
   * @param jobIdList the list of cloudlets that finished execution
   * @return the average turnaround time of the given cloudlets
   */
  public static BigDecimal getAverageTurnaroundTimeForJobId1(List<Cloudlet> finishedCloudlets) {
    BigDecimal averageTurnaroundTime = BigDecimal.valueOf(0);

    List<Cloudlet> jobId1List = finishedCloudlets.stream().filter(cloudlet -> cloudlet.getJobId() == 1).collect(Collectors.toList());

    int jobId1CloudletLength = jobId1List.size();

    for (int i = 0; i < jobId1CloudletLength; i++) {
      averageTurnaroundTime = averageTurnaroundTime.add((BigDecimal.valueOf(jobId1List.get(i).getFinishTime())
      .subtract(BigDecimal.valueOf(jobId1List.get(i).getLastDatacenterArrivalTime()))));
    }

    averageTurnaroundTime = averageTurnaroundTime.divide(BigDecimal.valueOf(jobId1CloudletLength), 2, RoundingMode.HALF_UP); // gets the average by dividing by number of
                                                                          // finshed cloudlets in the list
    return averageTurnaroundTime;
  }

   /**
   * Gets the average turnaround time of cloudlets with a job id of 2.
   * 
   * @param jobIdList the list of cloudlets that finished execution
   * @return the average turnaround time of the given cloudlets
   */
  public static BigDecimal getAverageTurnaroundTimeForJobId2(List<Cloudlet> finishedCloudlets) {
    BigDecimal averageTurnaroundTime = BigDecimal.valueOf(0);

    List<Cloudlet> jobId2List = finishedCloudlets.stream().filter(cloudlet -> cloudlet.getJobId() == 2).collect(Collectors.toList());

    int jobId2CloudletLength = jobId2List.size();

    for (int i = 0; i < jobId2CloudletLength; i++) {
      averageTurnaroundTime = averageTurnaroundTime.add((BigDecimal.valueOf(jobId2List.get(i).getFinishTime())
      .subtract(BigDecimal.valueOf(jobId2List.get(i).getLastDatacenterArrivalTime()))));
    }

    averageTurnaroundTime = averageTurnaroundTime.divide(BigDecimal.valueOf(jobId2CloudletLength), 2, RoundingMode.HALF_UP);  // gets the average by dividing by number of
                                                                          // finshed cloudlets in the list
    return averageTurnaroundTime;
  }
}
