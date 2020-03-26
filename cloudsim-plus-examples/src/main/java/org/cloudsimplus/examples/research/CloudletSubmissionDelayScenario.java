package org.cloudsimplus.examples.research;

import java.util.List;

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
import org.cloudbus.cloudsim.brokers.DatacenterBrokerBestFit;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;


/**
 * Creates a simple scenario showing one host running on one datacenter executing cloudlets that arrive at a random time. The cloudlet's arrival is controlled by 
 * delaying the submission of the cloudlets.
 * 
 * @author Chigozie Asikaburu
 *
 */
public class CloudletSubmissionDelayScenario {
    
    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private List<Host> hostList;
    private Datacenter datacenter0;
    private final List<Cloudlet> finishedCloudlets;

    public static void main(String[] args) {
        new CloudletSubmissionDelayScenario();
    }
    
    private CloudletSubmissionDelayScenario() {
    
        simulation = new CloudSim();  // Creates the CloudSim simulation and internally creates a CloudInformationService
        
        // Creates a list of hosts 
        hostList = CloudCreator.createHosts(1, 6, 2000, 2048, 1000, 25000, "space-shared");
        
        datacenter0 = CloudCreator.createDatacenter(simulation, hostList);  // creates a datacenter and it's hosts. The number of hosts can be changed by modifying the fields       
        
        //Creates a broker that is a software acting on behalf of a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation); 
        
        vmList = CloudCreator.createVms(1, 4, 1400, 1024, 500, 5000, "time-shared");		      
        	

        cloudletList = CloudCreator.createCloudletsFromFile("Test_Data/Demos (3:6)/DatacenterCloudlets(3:6_Demo).csv");   

        broker0.submitCloudletList(cloudletList);
        broker0.submitVmList(vmList);      
        
        Vm vm = vmList.get(vmList.size() -1);  // gets the last VM to track to listen when it's been notified
		
        simulation.start();        
        
        finishedCloudlets = broker0.getCloudletFinishedList();
        
        CloudResults.printExtraResults(finishedCloudlets);       
        new CloudletsTableBuilder(finishedCloudlets).build();  
    }
     
    // takes a list of all VMs to set up a listener to wait for the last VM to be allocated and then 
    // we change the submission delay of all cloudlets to make them non relative to the current simulation time 
    public void setNonRelativeSubmissionDelay (List<Cloudlet> cloudletList, CloudSim simulation) {
    	
    	double currentSimulationTime = simulation.clock();   
    	    	
    	System.out.printf("The current simulation time is %f seconds", currentSimulationTime);
    	
    	for (int i = 0; i < cloudletList.size(); i++) { 
    		
    		/* Sets the Listener to intercept allocation of a Host to the Vm.
            * The Listener is created using Java 8 Lambda Expressions.
            */
    		
    		double currentDelay = cloudletList.get(i).getSubmissionDelay();  // represents the current simulation time
    		
    		cloudletList.get(i).setSubmissionDelay(currentDelay - currentSimulationTime); 
        
    	}	
    }
    
    /**
     * NOTE THIS METHOD DOESN'T WORK AT ALL. The submission times changes but the arrival times don't because this listener is notified after 
     * all cloudlets have already been submitted. 
     * 
     * Event listener which is called every time the simulation clock advances.
     * @param info information about the event happened.
    */
    private void clockTickListener(final EventInfo info) {
        //at the desired time, submit new cloudlets
        if(info.getTime() == .10) {

        	System.out.printf("The current simulation time is %f seconds", info.getTime());
        	
            cloudletList = CloudCreator.createCloudletsFromFile("Test_Data/Demos (3:6)/DatacenterCloudlets(3:6_Demo).csv");   
        	
        	for (int i = 0; i < cloudletList.size(); i++) { 
        		
        		/* Sets the Listener to intercept allocation of a Host to the Vm.
                * The Listener is created using Java 8 Lambda Expressions.
                */
        		
        		double currentDelay = cloudletList.get(i).getSubmissionDelay();  // represents the current simulation time
        		
        		cloudletList.get(i).setSubmissionDelay(currentDelay); 
        	}
        	
            broker0.submitCloudletList(cloudletList);
        }
    }
    
    



}
