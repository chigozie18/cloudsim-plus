package org.cloudsimplus.examples.research;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;

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
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;

import java.text.DecimalFormat;
import java.util.List;


/**
 * Creates a simple scenario showing one host running on one datacenter executing cloudlets that arrive at a random time. The cloudlet's arrival is controlled by 
 * delaying the submission of the cloudlets.
 * 
 * @author chigozieasikaburu
 *
 */
public class DatacenterBrokerFastFitDemo {
    
    private final CloudSim simulation;
    private DatacenterBroker broker1;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private List<Host> hostList1, hostList2, hostList3;
    private Datacenter datacenter1, datacenter2, datacenter3;
    private final List<Cloudlet> finishedCloudlets;

    public static void main(String[] args) {
        new DatacenterBrokerFastFitDemo();
    }
    
    private DatacenterBrokerFastFitDemo() {
    
    	hostList1 = CloudCreator.createHostsFromFile("Test_Data/Demos 3:20/Datacenter1Hosts(3:20_Demo).csv");
    	hostList2 = CloudCreator.createHostsFromFile("Test_Data/Demos 3:20/Datacenter2Hosts(3:20_Demo).csv");
    	hostList3 = CloudCreator.createHostsFromFile("Test_Data/Demos 3:20/Datacenter3Hosts(3:20_Demo).csv");

        simulation = new CloudSim();  // Creates the CloudSim simulation and internally creates a CloudInformationService        
        
     // creates a datacenter and costs (in $) for using resources on the datacenter     
        datacenter1 = CloudCreator.createDatacenter(simulation, hostList1, .02, .05, .1, .1);  
        datacenter2 = CloudCreator.createDatacenter(simulation, hostList2, .01, .05, .1, .1);
        datacenter3 = CloudCreator.createDatacenter(simulation, hostList3, .04, .05, .1, .1);
        
        
        //Creates a broker that is a software acting on behalf of a cloud customer to manage his/her VMs and Cloudlets
        broker1 = new DatacenterBrokerFastFit(simulation); 
        
        vmList = CloudCreator.createVmsFromFile("/Users/chigozieasikaburu/git/cloudsim-plus/cloudsim-plus-examples/Test_Data/Demos 3:20/Datacenter1Vms(3:20_Demo).csv");
        
        // Creates a list of hosts from a file 
        cloudletList = CloudCreator.createCloudletsFromFile("Test_Data/Demos 3:20/DatacenterCloudlets(3:20_Demo).csv");   
        
        
        broker1.submitVmList(vmList);        
        broker1.submitCloudletList(cloudletList);
        
		
        simulation.start();        
        
        finishedCloudlets = broker1.getCloudletFinishedList();
        
        CloudResults.printExtraResults(finishedCloudlets);
        new CloudletsTableBuilder(finishedCloudlets).build();        
    }
     
    //We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
    //to the specific rules of the simulated scenario

}
