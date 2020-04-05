package org.cloudsimplus.examples.research;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

public class CloudletTest2 {
	
	
	
	
	
	public static void main(String[] args) {
		
		
		
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

}
