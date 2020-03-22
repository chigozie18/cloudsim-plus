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
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;




/**
 * 
 * Shows a CloudSim Scenario with 2 datacenter that have 4 hosts each. Each host has a different set of specifications but all hosts PEs have the same specs. 
 * There are 8 VMs in each datacenter which are allocated using time-shared (so they aren't fixed to any specific datacenter on any machine. Cloudlets are 
 * created according to a random set of parameters defined in CloudGenerator and 200 were created (for each cloudlet list and were assigned to one of the two lists of VMs (each list contains 8 VMs). All 
 *  parameter needed are gotten from parsing a CSV file using utility methods in CloudUtility.
 * 
 * @author chigozieasikaburu
 *
 */
public class SecondMoreComplexScenario {
    
    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList, vmList2, vmList3;
    private List<Cloudlet> cloudletList, cloudletList2;
    private List<Host> hostList, hostList2;
    private Datacenter datacenter0, datacenter1;

    public static void main(String[] args) {
        new SecondMoreComplexScenario();
    }

    private SecondMoreComplexScenario() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();  // Creates the CloudSim simulation and internally creates a CloudInformationService
        
        // Creates a list of host by parsing a local file
        hostList = CloudCreator.createHostsFromFile("Test_Data/Demos (2:28)/Datacenter2VMs(2:28_Demo).csv");
        hostList2 = CloudCreator.createHostsFromFile("Test_Data/Demos (2:28)/Datacenter2Hosts(2:28_Demo).csv");
        
        datacenter0 = CloudCreator.createDatacenter(simulation, hostList);  // creates a datacenter and it's hosts. The number of hosts can be changed by modifying the fields       
        datacenter1 = CloudCreator.createDatacenter(simulation, hostList2);
        
        //Creates a broker that is a software acting on behalf of a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);        	
        vmList = CloudCreator.createVmsFromFile("Test_Data/Demos (2:28)/Datacenter1VMs(2:28_Demo).csv");  // VMs, mips, pes, ram, bandwidth, storage			
        vmList2 = CloudCreator.createVmsFromFile("Test_Data/Demos (2:28)/Datacenter2VMs(2:28_Demo).csv");  // VMs, mips, pes, ram, bandwidth, storage			
        vmList3 = Stream.of(vmList, vmList2)
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());  // creates a combined VM list to be submitted to the broker 
     
        cloudletList = CloudCreator.createCloudletsFromFile("Test_Data/Demos (2:28)/Datacenter1Cloudlets(2:28_Demo).csv");   
        cloudletList2 = CloudCreator.createCloudletsFromFile("Test_Data/Demos (2:28)/Datacenter2Cloudlets(2:28_Demo).csv");
        						
        broker0.submitVmList(vmList3);
        broker0.submitCloudletList(cloudletList);   // sends a list of cloudlet along with VMs which will execute the cloudlet to the broker 
        broker0.submitCloudletList(cloudletList2);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
    }
    
    
}
