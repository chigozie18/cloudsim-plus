package org.cloudsimplus.examples.research.scenariopart2;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.research.CloudCreator;
import org.cloudsimplus.examples.research.CloudResults;
import org.cloudsimplus.examples.research.DatacenterBrokerBestMap2;
import org.cloudsimplus.examples.research.DatacenterBrokerRoundRobinMap2;
import org.cloudsimplus.listeners.EventInfo;

/**
 * 
 * 
 * @author Chigozie Asikaburu
 *
 */
public class Scenario5 {

	private final CloudSim simulation;
	private DatacenterBroker broker0;
	private List<Vm> vmList, vmList2, vmList3, vmList4;
	private List<Cloudlet> cloudletList, cloudletList2, cloudletList3;
	private List<Host> hostList, hostList2, hostList3;
	private Datacenter datacenter0, datacenter1, datacenter2;
	private final List<Cloudlet> finishedCloudlets;
	private int cloudletTracker = 0; // keeps the index of a cloudlet (in a list) that last arrived dynamically
	private String workingDirectory = System.getProperty("user.dir");

	public static void main(String[] args) {
		new Scenario5();
	}

	private Scenario5() {

		simulation = new CloudSim();  // Creates the CloudSim simulation and internally creates a CloudInformationService
		simulation.terminateAt(1000);
		// Creates a list of hosts 
		hostList = CloudCreator.createHostsFromFile(workingDirectory + "/cloudsim-plus-examples/Test_Data/Research Scenarios/Part2/Scenario5/Datacenter1_Hosts.csv");
		hostList2 = CloudCreator.createHostsFromFile(workingDirectory + "/cloudsim-plus-examples/Test_Data/Research Scenarios/Part2/Scenario5/Datacenter2_Hosts.csv");
		hostList3 = CloudCreator.createHostsFromFile(workingDirectory + "/cloudsim-plus-examples/Test_Data/Research Scenarios/Part2/Scenario5/Datacenter3_Hosts.csv");

		datacenter0 = CloudCreator.createDatacenter(simulation, hostList, 1);  // creates a datacenter and it's hosts      
		datacenter1 = CloudCreator.createDatacenter(simulation, hostList2, 1);  // creates a datacenter and it's hosts  
		datacenter2 = CloudCreator.createDatacenter(simulation, hostList3, 1);  // creates a datacenter and it's hosts      

		// Creates a broker that is a software acting on behalf of a cloud customer to manage his/her VMs and Cloudlets
		broker0 = new DatacenterBrokerRoundRobinMap2(simulation); 

		vmList = CloudCreator.createVmsFromFile(workingDirectory + "/cloudsim-plus-examples/Test_Data/Research Scenarios/Part2/Scenario5/Datacenter1_Vms.csv");		      
		vmList2 = CloudCreator.createVmsFromFile(workingDirectory + "/cloudsim-plus-examples/Test_Data/Research Scenarios/Part2/Scenario5/Datacenter2_Vms.csv");		
		vmList3 = CloudCreator.createVmsFromFile(workingDirectory + "/cloudsim-plus-examples/Test_Data/Research Scenarios/Part2/Scenario5/Datacenter3_Vms.csv");		      

		cloudletList = CloudCreator.createCloudletsFromFile2(workingDirectory + "/cloudsim-plus-examples/Test_Data/Research Scenarios/Part2/Scenario5/Datacenter1_Cloudlets.csv");
		cloudletList2 = CloudCreator.createCloudletsFromFile2(workingDirectory + "/cloudsim-plus-examples/Test_Data/Research Scenarios/Part2/Scenario5/Datacenter2_Cloudlets.csv");
		
		cloudletList3 = Stream.of(cloudletList, cloudletList2)
				.flatMap(x -> x.stream())
				.collect(Collectors.toList());  // creates a combined VM list to be submitted to the broker 

		Collections.sort(cloudletList3, Comparator.comparingDouble(Cloudlet::getSubmissionDelay));  // sorts cloudlets based on their arrival (from earliest to latestest)

		vmList4 = Stream.of(vmList, vmList2, vmList3)
				.flatMap(x -> x.stream())
				.collect(Collectors.toList());

		broker0.submitVmList(vmList4); 

		simulation.addOnClockTickListener(this::clockTickListener);

		simulation.start();        

		finishedCloudlets = broker0.getCloudletFinishedList();

		CloudResults.printExtraResults2(finishedCloudlets); 
		CloudResults.printAverageTurnaroundTimeAllJobId(finishedCloudlets);
		CloudResults.printAllArrivalTimesWithJobID(finishedCloudlets);
		new CloudletsTableBuilder(finishedCloudlets).build();  
	}

	/**
	 * Event listener which is called every time the simulation clock advances.
	 * @param info information about the event happened.
	 */
	private void clockTickListener(final EventInfo info) {

		if (cloudletTracker < cloudletList3.size()) {
			int currentCloudletArrival = (int) cloudletList3.get(cloudletTracker).getSubmissionDelay();  // keeps track of the arrival time of the current dynamically arriving cloudlet 

			if ((int) info.getTime() == currentCloudletArrival) {

				// filter the list to find all cloudlets arriving at the same time
				List<Cloudlet> cloudletList4 = cloudletList3.stream()
						.filter(cloudlet -> cloudlet.getSubmissionDelay() == currentCloudletArrival).collect(Collectors.toList());

				// sets the submission delay to 0 as we are simulating the dynamic arrival of cloudlets arriving at the submission delay
				cloudletList4.forEach((cloudlet) -> cloudlet.setSubmissionDelay(0));  

				broker0.submitCloudletList(cloudletList4); 
				cloudletTracker = cloudletTracker + cloudletList4.size();  // increments the counter by the amount of cloudlets submitted      	
			}
		}
	}
}       