package org.cloudsimplus.examples.research;

/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;

import org.cloudbus.cloudsim.resources.Pe;

import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.text.DecimalFormat;
import java.util.List;


/**
 * Creates a simple scenario showing one host running on one datacenter executing cloudlets that arrive at a random time. The cloudlets arrive is controlled by 
 * delaying the submission of cloudlets.
 * 
 * @author chigozieasikaburu
 *
 */
public class CloudletExecutionTimeScenario {
	
	private static final int HOSTS = 1;  // number of hosts for each datacenter 
								
    private static final int VMS = 2;  // number of VMs for each host
    
    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private List<Host> hostList;
    private Datacenter datacenter0;
    private final List<Cloudlet> finishedCloudlets;

    public static void main(String[] args) {
        new CloudletExecutionTimeScenario();
    }
    
    private CloudletExecutionTimeScenario() {
    
        simulation = new CloudSim();  // Creates the CloudSim simulation and internally creates a CloudInformationService
        
        // Creates a list of hosts 
        hostList = CloudCreator.createHosts(1, 6, 2000, 2048, 1000, 25000, "space-shared");
        
        datacenter0 = CloudCreator.createDatacenter(simulation, hostList);  // creates a datacenter and it's hosts. The number of hosts can be changed by modifying the fields       
        
        //Creates a broker that is a software acting on behalf of a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation); 
        
        vmList = CloudCreator.createVms(1, 4, 1400, 1024, 500, 5000, "time-shared");		
        
        cloudletList = CloudCreator.createCloudletsFromFile("Test_Data/Demos (3:6)/DatacenterCloudlets(3:6_Demo).csv");   
        						
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);   

        simulation.start();
        
        finishedCloudlets = broker0.getCloudletFinishedList();
        
        printExtraResults();
       
        new CloudletsTableBuilder(finishedCloudlets).build();
    }
    
    public void printExtraResults () {
    	
		DecimalFormat df = new DecimalFormat("0.00");	

    	System.out.println("\n");

    	System.out.println("************* Average Finish Time *************");

    	System.out.println("\nThe average finish time was: " + TimeCalculator.getAverageFinishTime(finishedCloudlets));

    	System.out.println("\n");


    	System.out.println("************* Average Execution Time *************");

    	System.out.println("\nThe average execution time was: " + TimeCalculator.getAverageExecutionTime(finishedCloudlets));

    	System.out.println("\n");


    	System.out.println("Arrival Times: \n");

    	for (int i = 0; i < finishedCloudlets.size(); i++) {
    		// gets the sum of all cloudlet finish times
    		System.out.println("Cloudlet " + finishedCloudlets.get(i).getId() + " arrived at : " + finishedCloudlets.get(i).getArrivalTime(datacenter0) + " seconds");

    	}
    	
    	System.out.println("\n");


    	System.out.println("************* Average Arrival Time *************");


    	System.out.println("\nThe average arrival time was: " + TimeCalculator.getAverageArrivalTime(finishedCloudlets, datacenter0));

    	System.out.println("\n");


    	for (int i = 0; i < finishedCloudlets.size(); i++) {
    		// gets the sum of all cloudlet finish times
    		System.out.println("Cloudlet " + finishedCloudlets.get(i).getId() + " had a submission delay of: " + finishedCloudlets.get(i).getSubmissionDelay() + " seconds");

    	}
    }




}
