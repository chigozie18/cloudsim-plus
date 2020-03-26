package org.cloudsimplus.examples.research.scenariopart1;

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
import org.cloudsimplus.examples.research.DatacenterBrokerCustomFit;

/**
 * Creates a simple scenario showing one host running on one datacenter executing cloudlets that arrive at a random time. The cloudlet's arrival is controlled by 
 * delaying the submission of the cloudlets.
 * 
 * @author chigozieasikaburu
 *
 */
public class Scenario2 {
    
    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList, vmList2;
    private List<Cloudlet> cloudletList, cloudletList2, cloudletList3;
    private List<Host> hostList, hostList2;
    private Datacenter datacenter0, datacenter1;
    private final List<Cloudlet> finishedCloudlets;

    public static void main(String[] args) {
        new Scenario2();
    }
    
    private Scenario2() {
    
        simulation = new CloudSim();  // Creates the CloudSim simulation and internally creates a CloudInformationService
        
        // Creates a list of hosts 
        hostList = CloudCreator.createHostsFromFile("Test_Data/Research Scenarios/Part1/Scenario2/Scenario2_Datacenter1Hosts.csv");
        hostList2 = CloudCreator.createHostsFromFile("Test_Data/Research Scenarios/Part1/Scenario2/Scenario2_Datacenter2Hosts.csv");

        datacenter0 = CloudCreator.createDatacenter(simulation, hostList);  // creates a datacenter and it's hosts      
        datacenter1 = CloudCreator.createDatacenter(simulation, hostList2);  // creates a datacenter and it's hosts      
        
        //Creates a broker that is a software acting on behalf of a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerCustomFit(simulation); 
        
        vmList = CloudCreator.createVmsFromFile("Test_Data/Research Scenarios/Part1/Scenario2/Scenario2_Datacenter1_Vms.csv");		      
        vmList2 = CloudCreator.createVmsFromFile("Test_Data/Research Scenarios/Part1/Scenario2/Scenario2_Datacenter2_Vms.csv");		      

        cloudletList = CloudCreator.createCloudletsFromFile("Test_Data/Research Scenarios/Part1/Scenario2/Scenario2_Datacenter1_Cloudlets.csv");
        cloudletList2 = CloudCreator.createCloudletsFromFile("Test_Data/Research Scenarios/Part1/Scenario2/Scenario2_Datacenter2_Cloudlets.csv");

        cloudletList3 = Stream.of(cloudletList, cloudletList2)
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());  // creates a combined VM list to be submitted to the broker 
        
        broker0.submitCloudletList(cloudletList3);   // sends a list of cloudlet along with VMs which will execute the cloudlet to the broker 
        broker0.submitVmList(vmList); 
        broker0.submitVmList(vmList2);      
        		
        simulation.start();        
        
        finishedCloudlets = broker0.getCloudletFinishedList();
                
        CloudResults.printExtraResults2(finishedCloudlets);       
        new CloudletsTableBuilder(finishedCloudlets).build();  
    }
}