package org.cloudsimplus.examples.research;

import static org.cloudbus.cloudsim.brokers.DatacenterBroker.LOGGER;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An implementation of {@link DatacenterBroker} that uses a custom and optimal
 * mapping between submitted cloudlets and vms. It maps an incoming cloudlet to the vm that can execute the 
 * cloudlet the fastest (vm with the least work). The vm with the least work is the one with the smallest amount of cloudlets mips (running or waiting) on the 
 * vm. This policy also tries to map incoming cloudlets to any free vms first before finding the vm with the least work. It simply selects the first free vm available when possible (will go in order). 
 *
 * @author Chigozie Asikaburu
 * @since CloudSim Plus 4.6.0
 */
public class DatacenterBrokerBestCustomFit extends DatacenterBrokerSimple {

	/**
	 * Creates a DatacenterBroker object.
	 *
	 * @param simulation The CloudSim instance that represents the simulation the Entity is related to
	 */
	public DatacenterBrokerBestCustomFit(final CloudSim simulation) {
		super(simulation);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p><b>It applies a Round-Robin policy to cyclically select
	 * the next Vm from the {@link #getVmWaitingList() list of waiting VMs}.</p>
	 *
	 * @param cloudlet {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	protected Vm defaultVmMapper(final Cloudlet cloudlet) {
		if (cloudlet.isBoundToVm()) {
			return cloudlet.getVm();
		}

		if (getVmExecList().isEmpty()) {
			return Vm.NULL;
		}

		Vm mappedVm = Vm.NULL;

		System.out.println("*********CLOUDLET JOB IS: " + cloudlet.getJobId());
		
		if (cloudlet.getJobId() == 1) {   // if the cloudlet's job id is 1 it will be executed in a vm in datacenter 1
			Datacenter datacenter = getDatacenterList().get(0);

			List<Vm> datacenterVmList = getVmList(datacenter);

			// if a vm in this datacenter is free then assign the cloudlet to that vm       	
			mappedVm = datacenterVmList
				.stream()
				.filter(vm -> vm.getFreePesNumber() > 0)  // finds vms that have at least one free PE (in our scenario this is a free/idle vm)
				.min(Comparator.comparingLong(Vm::getExpectedFreePesNumber))  // this attribute doesn't matter and is used to pick any free vm (change this) 
				.orElse(Vm.NULL);			

			if (mappedVm == Vm.NULL) {  // if there is no free vm 

				mappedVm = datacenterVmList
					.stream()
					.min(Comparator.comparingLong(vm -> getTotalCloudletMips(vm)))  // select the vm with the shortest total cloudlet mips 
					.orElse(Vm.NULL);	
			} 
		}

		if (cloudlet.getJobId() == 2) {   // if the cloudlet's job id is 2 it will be executed in a vm in datacenter 2
			Datacenter datacenter = getDatacenterList().get(1);

			List<Vm> datacenterVmList = getVmList(datacenter);

			mappedVm = datacenterVmList
				.stream()
				.filter(vm -> vm.getFreePesNumber() > 0)  // finds vms that have at least one free PE (in our scenario this is a free/idle vm)
				.min(Comparator.comparingLong(Vm::getExpectedFreePesNumber))  // this attribute doesn't matter and just returns some vm (change this) 
				.orElse(Vm.NULL);

			if (mappedVm == Vm.NULL) {  // if there is no free vm 

				mappedVm = datacenterVmList
					.stream()
					.min(Comparator.comparingLong(vm -> getTotalCloudletMips(vm)))  // gets the vm with the shortest total cloudlet mips 
					.orElse(Vm.NULL);	
			}
		}

		return mappedVm;  // either returns a vm that's free or the vm that will execute the cloudlet the fastest (has the least instructions to execute)
	}

	/**
	 * Gets the list of all vms in a given datacenter.
	 * 
	 * @param datacenter the datacenter to retrieve the list of vms 
	 */
	private List<Vm> getVmList(Datacenter datacenter) {  	
		List<Host> hostList = datacenter.getHostList();
		List<Vm> vmList = new ArrayList<>();  // the list of all vms in a given datacenter 
		Host host = null;

		for (int i = 0; i < hostList.size(); i++) {  // iterate through every host
			host = hostList.get(i);

			for (int j = 0; j < host.getVmList().size(); j++) {  // iterate through a host's vms
				vmList.add(host.getVmList().get(j));  // add the vm of a particular host to a list 
			}
		}

		return vmList;   	
	}

	/**
	 * Gets the list of all cloudlets that are either waiting or executing on all vms in a given datacenter.
	 * 
	 * @param datacenter the datacenter to retrieve the list of vms 
	 */
	private List<Cloudlet> getAllVmCloudletLists(Datacenter datacenter) {  	

		List<Vm> vmList = getVmList(datacenter);
		Vm vm = null;
		List<Cloudlet> vmCloudletLists = new ArrayList<>();

		// iterates over every vm and gets all of it's waiting and executing cloudlets and adds them to a list
		for (int i = 0; i < vmList.size(); i++) {
			List<Cloudlet> cloudletList = vmList.get(i).getCloudletScheduler().getCloudletList();  
			// adds the current list of cloudlets on a vm to a final cloudlet list that will contain the list of all cloudlets in all vms 
			vmCloudletLists = Stream.of(vmCloudletLists, cloudletList)  
					.flatMap(x -> x.stream())
					.collect(Collectors.toList());    		
		}

		return vmCloudletLists; 	
	}

	/**
	 * Gets the total mips of all waiting cloudlets and mips remaining so far from currently executing cloudlets in a given vm.
	 * 
	 * @param datacenter the datacenter to retrieve the list of vms 
	 */
	private long getTotalCloudletMips(Vm vm) {  	

		long totalExecutingCloudletMips = 0;
		long totalWaitingCloudletMips = 0;

		List<CloudletExecution> cloudletExecList = vm.getCloudletScheduler().getCloudletExecList();  // gets all cloudlets which are executing in a vm
		// Ask the CloudSim Google Group for how to retrieve cloudletExecList (or just figure it out);
		List<CloudletExecution> cloudletWaitingList = vm.getCloudletScheduler().getCloudletWaitingList();  // gets all cloudlets which are executing in a vm

		// get the remaining mips of currently executing cloudlets 
		for (int i = 0; i < cloudletExecList.size(); i++) {
			totalExecutingCloudletMips = totalExecutingCloudletMips + cloudletExecList.get(i).getRemainingCloudletLength();
		}

		// get the remaining mips of currently waiting cloudlets 
		for (int i = 0; i < cloudletWaitingList.size(); i++) {
			// the remaining length of all waiting cloudlets will be the same as the cloudlet length before execution (cloudlet.getLength())
			totalWaitingCloudletMips = totalWaitingCloudletMips + cloudletWaitingList.get(i).getRemainingCloudletLength();
		}

		long totalCloudletMips = totalExecutingCloudletMips + totalWaitingCloudletMips;

		return totalCloudletMips; 	
	}

}
