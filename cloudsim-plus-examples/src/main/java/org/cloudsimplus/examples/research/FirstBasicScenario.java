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
package org.cloudsimplus.examples.research;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A minimal but organized, structured and re-usable CloudSim Plus example
 * which shows good coding practices for creating simulation scenarios.
 *
 * <p>It defines a set of constants that enables a developer
 * to change the number of Hosts, VMs and Cloudlets to create
 * and the number of {@link Pe}s for Hosts, VMs and Cloudlets.</p>
 *
 * @author chigozieasikaburu
 */
public class FirstBasicScenario {
	
	private static final int HOSTS = 1;  // number of hosts for each datacenter 
	
	private static final int HOST1_PES = 2;
	private static final int HOST1_RAM = 4096;
	private static final int HOST1_BW = 1000;
	private static final int HOST1_STORAGE = 4096;
	private static final int HOST1_MIPS = 10000;
	
	private static final int HOST2_PES = 4;
	private static final int HOST2_RAM = 2048;
	private static final int HOST2_BW = 1000;
	private static final int HOST2_STORAGE = 4096;
	private static final int HOST2_MIPS = 1000;
								
    private static final int VMS = 1;  // number of VMs for each host
    
    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList, cloudletList2;
    private List<Host> hostList, hostList2;
    private Datacenter datacenter0, datacenter1;

    public static void main(String[] args) {
        new FirstBasicScenario();
    }

    private FirstBasicScenario() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();  // Creates the CloudSim simulation and internally creates a CloudInformationService
        
        hostList = CloudCreator.createHosts(HOSTS, HOST1_PES, HOST1_MIPS, HOST1_RAM, HOST1_BW, HOST1_STORAGE);     
        hostList2 = CloudCreator.createHosts(HOSTS, HOST2_PES, HOST2_MIPS, HOST2_RAM, HOST2_BW, HOST2_STORAGE);     
        
        datacenter0 = CloudCreator.createDatacenter(simulation, hostList);  // creates a datacenter and it's hosts. The number of hosts can be changed by modifying the fields       
        datacenter0 = CloudCreator.createDatacenter(simulation, hostList2);  
        
        //Creates a broker that is a software acting on behalf of a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);        	
        vmList = CloudCreator.createVms(VMS, 2, 200, 512, 100, 1024);  // VMs, mips, pes, ram, bandwidth, storage			
        final Vm vm2 = new VmSimple(100, 2);
        vm2.setRam(512).setBw(100).setSize(1024).setHost(hostList2.get(0));
        vmList.add(vm2);
        
        //UtilizationModelFull - Cloudlets use the full resources available to them all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(.1);
        
        cloudletList = CloudCreator.createCloudlets(2, 1, 30, 10, utilizationModel);   
        cloudletList2 = CloudCreator.createCloudlets(2, 1, 30, 10, utilizationModel);   
        						
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList, vmList.get(0));   // sends a list of cloudlet along with VMs which will execute the cloudlet to the broker 
        broker0.submitCloudletList(cloudletList2, vmList.get(1));
        			
        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
    }
}
    
  
