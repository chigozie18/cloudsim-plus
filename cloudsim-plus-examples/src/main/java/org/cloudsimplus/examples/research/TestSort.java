package org.cloudsimplus.examples.research;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

public class TestSort {

	
	
    public static void main(String[] args) {
    	List<Cloudlet> cloudletList = CloudCreator.createCloudletsFromFile("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter2_Cloudlets.csv"); 
        Collections.sort(cloudletList, Comparator.comparing(Cloudlet::getSubmissionDelay));  // sorts Cloudlets for the ones
    	
        
        for (int i = 0; i < cloudletList.size(); i++) { 
        	
        	System.out.println("Cloudlet " + i + "has a submission delay of: " + cloudletList.get(i).getSubmissionDelay() + " seconds.");
        }
	}
}
