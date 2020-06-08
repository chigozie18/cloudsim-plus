package org.cloudsimplus.examples.research;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

import com.sun.tools.sjavac.comp.dependencies.PublicApiCollector;

public class CloudletTest2 {





	public static void main(String[] args) {

		cloudletSubmissionDelaySortTest3();


	}

	public static void cloudletSubmissionDelaySortTest() {

		List<Cloudlet> cloudletList = CloudCreator.createCloudletsFromFile2("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter1_Cloudlets.csv");
		List<Cloudlet> cloudletList2 = CloudCreator.createCloudletsFromFile2("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter2_Cloudlets.csv");

		List<Cloudlet> cloudletList3 = Stream.of(cloudletList, cloudletList2)
				.flatMap(x -> x.stream())
				.collect(Collectors.toList());  // creates a combined VM list to be submitted to the broker 

		Collections.sort(cloudletList3, Comparator.comparingDouble(Cloudlet::getSubmissionDelay));  // sorts cloudlets based on their arrival (from earliest to latestest)


		for (int i = 0; i < cloudletList3.size(); i++) {
			System.out.println("Cloudlet #" + i + " had a delay of " + cloudletList3.get(i).getSubmissionDelay() + " seconds.");
		}
	}


	public static void cloudletSubmissionDelaySortTest2() {

		List<Cloudlet> cloudletList = CloudCreator.createCloudletsFromFile2("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter1_Cloudlets.csv");
		List<Cloudlet> cloudletList2 = CloudCreator.createCloudletsFromFile2("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter2_Cloudlets.csv");

		List<Cloudlet> cloudletList3 = Stream.of(cloudletList, cloudletList2)
				.flatMap(x -> x.stream())
				.collect(Collectors.toList());  // creates a combined VM list to be submitted to the broker 

		// sorts cloudlets based on their arrival (from earliest to latestest) then by their job id
		Collections.sort(cloudletList3, Comparator.comparingDouble(Cloudlet::getSubmissionDelay));  


		for (int i = 0; i < cloudletList3.size(); i++) {
			System.out.println("---------------------------------");
			System.out.println("Entry #" + (i + 1) );
			System.out.println("---------------------------------");
			System.out.println("Cloudlet #" + i);
			System.out.println("Submission Delay: " + cloudletList3.get(i).getSubmissionDelay());
			System.out.println("Job ID: " + cloudletList3.get(i).getJobId());
			System.out.println("\n");
		}	



	}
	
	public static void cloudletSubmissionDelaySortTest3() {

		List<Cloudlet> cloudletList = CloudCreator.createCloudletsFromFile2("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter1_Cloudlets.csv");
		List<Cloudlet> cloudletList2 = CloudCreator.createCloudletsFromFile2("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter2_Cloudlets.csv");

		List<Cloudlet> cloudletList3 = Stream.of(cloudletList, cloudletList2)
				.flatMap(x -> x.stream())
				.collect(Collectors.toList());  // creates a combined VM list to be submitted to the broker 

		// sorts cloudlets based on their arrival (from earliest to latestest) then by their job id
		Collections.sort(cloudletList3, Comparator.comparingDouble(Cloudlet::getSubmissionDelay));  


		for (int i = 0; i < cloudletList3.size(); i++) {
			System.out.println("---------------------------------");
			System.out.println("Cloudlet #" + i);
			System.out.println("---------------------------------");
			System.out.println("Cloudlet Mips: " + cloudletList3.get(i).getLength());
			System.out.println("Submission Delay: " + cloudletList3.get(i).getSubmissionDelay());
			System.out.println("Job ID: " + cloudletList3.get(i).getJobId());
			System.out.println("\n");
		}	



	}

}
