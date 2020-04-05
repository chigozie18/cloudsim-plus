package org.cloudsimplus.examples.research.scenariopart1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerBestFit;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerFirstFit;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.research.CloudCreator;
import org.cloudsimplus.examples.research.CloudResults;
import org.cloudsimplus.examples.research.DatacenterBrokerCustomFit;
import org.cloudsimplus.examples.research.DatacenterBrokerBestCustomFit;
import org.cloudsimplus.examples.research.DatacenterBrokerFastFit;
import org.cloudsimplus.examples.research.DatacenterBrokerWorstCustomFit;
import org.cloudsimplus.listeners.EventInfo;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;

/**
 * Creates a simple scenario showing one host running on one datacenter executing cloudlets that arrive at a random time. The cloudlet's arrival is controlled by 
 * delaying the submission of the cloudlets.
 * 
 * @author Chigozie Asikaburu
 *
 */
public class Scenario1 {

	private final CloudSim simulation;
	private DatacenterBroker broker0;
	private List<Vm> vmList, vmList2, vmList3;
	private List<Cloudlet> cloudletList, cloudletList2, cloudletList3;
	private List<Host> hostList, hostList2;
	private Datacenter datacenter0, datacenter1;
	private final List<Cloudlet> finishedCloudlets;
	private int cloudletTracker = 0;  // keeps the index of a cloudlet (in a list) that last arrived dynamically

	public static void main(String[] args) {
		new Scenario1();
	}

	private Scenario1() {

		simulation = new CloudSim();  // Creates the CloudSim simulation and internally creates a CloudInformationService
		simulation.terminateAt(4000);
		// Creates a list of hosts 
		hostList = CloudCreator.createHostsFromFile("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter1Hosts.csv");
		hostList2 = CloudCreator.createHostsFromFile("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter2Hosts.csv");

		datacenter0 = CloudCreator.createDatacenter(simulation, hostList, 1);  // creates a datacenter and it's hosts      
		datacenter1 = CloudCreator.createDatacenter(simulation, hostList2, 1);  // creates a datacenter and it's hosts      

		//Creates a broker that is a software acting on behalf of a cloud customer to manage his/her VMs and Cloudlets
		broker0 = new DatacenterBrokerBestCustomFit(simulation); 

		vmList = CloudCreator.createVmsFromFile("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter1_Vms.csv");		      
		vmList2 = CloudCreator.createVmsFromFile("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter2_Vms.csv");		      

		cloudletList = CloudCreator.createCloudletsFromFile2("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter1_Cloudlets.csv");
		cloudletList2 = CloudCreator.createCloudletsFromFile2("Test_Data/Research Scenarios/Part1/Scenario1/Scenario1_Datacenter2_Cloudlets.csv");

		cloudletList3 = Stream.of(cloudletList, cloudletList2)
				.flatMap(x -> x.stream())
				.collect(Collectors.toList());  // creates a combined VM list to be submitted to the broker 

		Collections.sort(cloudletList3, Comparator.comparingDouble(Cloudlet::getSubmissionDelay));  // sorts cloudlets based on their arrival (from earliest to latestest)
		
		vmList3 = Stream.of(vmList, vmList2)
				.flatMap(x -> x.stream())
				.collect(Collectors.toList());
		
		broker0.submitVmList(vmList3); 

		simulation.addOnClockTickListener(this::clockTickListener);
	
		simulation.start();        

		finishedCloudlets = broker0.getCloudletFinishedList();

		CloudResults.printExtraResults2(finishedCloudlets);       
		new CloudletsTableBuilder(finishedCloudlets).build();  
	}

	/**
	 * Event listener which is called every time the simulation clock advances.
	 * @param info information about the event happened.
	 */
	private void clockTickListener(final EventInfo info) {



		if (cloudletTracker < 600) {

			List<Cloudlet> cloudletList4 = new ArrayList<>();    
			Cloudlet cloudlet = cloudletList3.get(cloudletTracker);  // gets the cloudlet to dynamically arrive
			cloudlet.setSubmissionDelay(0);  // sets the submission delay to 0 as we are simulating the dynamic arrival of cloudlets arriving at the submission delay
			cloudletList4.add(cloudlet);

			System.out.printf("The current simulation time is %f seconds", info.getTime());
			broker0.submitCloudletList(cloudletList4);
			cloudletTracker++;       	
		}
	}
}      