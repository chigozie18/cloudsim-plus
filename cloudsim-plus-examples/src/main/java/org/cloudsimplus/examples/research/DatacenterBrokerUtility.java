package org.cloudsimplus.examples.research;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * A utility class for a DatacenterBroker policy. It provides useful methods like ones for getting all cloudlets in a datacenter, getting the 
 * total number of executing cloudlets in a vm and more. 
 * 
 * @author chigozieasikaburu
 *
 */
public class DatacenterBrokerUtility {


	/**
	 * Gets the list of all vms in a given datacenter.
	 * 
	 * @param datacenter the datacenter to retrieve the list of vms 
	 * 
	 * @return a list of all vms in a datacenter
	 */
	public static List<Vm> getVmList(Datacenter datacenter) {  	
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
	 * 
	 * @return a list of all waiting or executing cloudlets on all vms in a datacenter
	 */
	public static List<Cloudlet> getAllVmCloudletLists(Datacenter datacenter) {  	

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
	 * @param vm the vm to get the total mips of all waiting cloudlets and remaining mips of executing cloudlets
	 * 
	 * @return the total mips of all waiting and remaining mips of executing cloudlets in a vm
	 */
	public static long getTotalCloudletMips(Vm vm) {  	

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

	/**
	 * Gets the total mips of all waiting cloudlets and mips remaining so far from currently executing cloudlets in a given vm. This method also 
	 * considers that previous cloudlets that arrived at the same time, have been assigned to vms but not yet "offically" mapped by CloudSim. 
	 * It calculates the mips to execute from those "yet to be mapped" cloudlets and adds them to the total. 
	 * 
	 * @param vm the vm to get the total mips of all waiting cloudlets and remaining mips of executing cloudlets
	 * @param lastVmId the id of the last vm that mapped a cloudlet
	 * @param lastCloudletMips the mips of the last cloudlet that arrived
	 * 
	 * @return the total mips of all waiting and remaining mips of executing cloudlets in a vm
	 */
	public static long getTotalCloudletMips2 (Vm vm, List<Long> lastVmIdList, List<Long> lastCloudletMipsList) {

		long totalCloudletMips = 0;
		for (int i = 0; i < lastVmIdList.size(); i++) {  /*  Loop for every vm id recorded. Note that the size of the list containing the last vm's id will be the same 
		 												   size as the list containing the last cloudlet's mips and there is a one-to-one correspondence between them */
			if (vm.getId() == lastVmIdList.get(i)) {	
				totalCloudletMips = lastCloudletMipsList.get(i) + totalCloudletMips;
			}
		}

		return getTotalCloudletMips(vm) + totalCloudletMips;
	}

	/**
	 * Gets the total mips of all waiting cloudlets in a vm. 
	 * 
	 * @param vm the vm to get the total mips of all waiting cloudlets
	 * 
	 * @return the total mips of all waiting cloudlets in a vm
	 */
	public static long getTotalWaitingCloudletMips(Vm vm) {  	

		long totalWaitingCloudletMips = 0;
		List<CloudletExecution> cloudletWaitList = vm.getCloudletScheduler().getCloudletWaitingList();  // gets all cloudlets which are executing in a vm

		// get the remaining mips of currently waiting cloudlets 
		for (int i = 0; i < cloudletWaitList.size(); i++) {
			// the remaining length of all waiting cloudlets will be the same as the cloudlet length before execution (cloudlet.getLength())
			totalWaitingCloudletMips = totalWaitingCloudletMips + cloudletWaitList.get(i).getRemainingCloudletLength();
		}

		return totalWaitingCloudletMips; 	
	}

	/**
	 * Gets the total mips of all executing cloudlets in a vm. 
	 * 
	 * @param vm the vm to get the total mips of all executing cloudlets
	 * 
	 * @return the total mips of all executing cloudlets in a vm
	 */
	public static long getTotalExecutingCloudletMips(Vm vm) {  	

		long totalExecutingCloudletMips = 0;
		List<CloudletExecution> cloudletExecList = vm.getCloudletScheduler().getCloudletExecList();  // gets all cloudlets which are executing in a vm

		// get the remaining mips of currently executing cloudlets 
		for (int i = 0; i < cloudletExecList.size(); i++) {
			totalExecutingCloudletMips = totalExecutingCloudletMips + cloudletExecList.get(i).getRemainingCloudletLength();
		}

		return totalExecutingCloudletMips; 	
	}

	/**
	 * Gets the number of cloudlets waiting in a vm.
	 * 
	 * @param vm the vm to get the number of waiting cloudlets
	 * 
	 * @return the number of cloudlets waiting in a vm
	 */
	public static long getNumOfWaitingCloudlets(Vm vm) {  	
		int cloudletWaitListSize = vm.getCloudletScheduler().getCloudletWaitingList().size();  // gets all cloudlets which are executing in a vm

		return cloudletWaitListSize; 	
	}


	/**
	 * Gets the number of cloudlets executing in a vm.
	 * 
	 * @param vm the vm to get the number of executing cloudlets
	 * 
	 * @return the number of cloudlets executing in a vm
	 */
	public static long getNumOfExecutingCloudlets(Vm vm) {  	
		int cloudletExecListSize = vm.getCloudletScheduler().getCloudletExecList().size();  // gets all cloudlets which are executing in a vm

		return cloudletExecListSize; 	
	}

}
