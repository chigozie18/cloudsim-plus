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

		return getTotalWaitingCloudletMips(vm) + getTotalExecutingCloudletMips(vm);
	}

	/**
	 * Gets the total mips of all waiting cloudlets and mips remaining so far from currently executing cloudlets in a given datacenter.
	 * 
	 * @param datacenter the datacenter to get the total mips of all waiting cloudlets and remaining mips of executing cloudlets
	 * 
	 * @return the total mips of all waiting and remaining mips of executing cloudlets in a datacenter
	 */
	public static long getTotalCloudletMipsInDC(Datacenter datacenter) {  	

		List<Vm> vmList = getVmList(datacenter);
		long totalCloudletMips = 0;
		for (int i = 0; i < vmList.size(); i++) {
			totalCloudletMips = totalCloudletMips + getTotalCloudletMips(vmList.get(i));
		}

		return totalCloudletMips;
	}

	/**
	 * Gets the total mips of all waiting cloudlets and mips remaining so far from currently executing cloudlets in a given vm. This method also 
	 * considers that previous cloudlets that arrived at the same time, have been assigned to vms but not yet "offically" mapped by CloudSim. 
	 * It calculates the mips to execute from those "yet to be mapped" cloudlets and adds them to the total. 
	 * 
	 * @param vm the vm to get the total mips of all waiting cloudlets and remaining mips of executing cloudlets
	 * @param lastVmIdList the list of last vm ids that mapped a cloudlet
	 * @param lastCloudletMips the mips of the last cloudlet that arrived
	 * 
	 * @see #getTotalCloudletMips(Vm)
	 * 
	 * @return the total mips of all waiting and remaining mips of executing cloudlets in a vm
	 */
	public static long getTotalCloudletMips2 (Vm vm, List<Long> lastVmIdList, List<Long> lastCloudletMipsList) {

		long totalCloudletMips = 0;
		for (int i = 0; i < lastVmIdList.size(); i++) {  /*  Loop for every vm id recorded. Note that the size of the list containing the last vm's id will be the same 
													   size as the list containing the last cloudlet's mips and there is a one-to-one correspondence between them */
			if (vm.getId() == lastVmIdList.get(i)) {	
				totalCloudletMips = totalCloudletMips + lastCloudletMipsList.get(i);
			}
		}

		return getTotalCloudletMips(vm) + totalCloudletMips;
	}

	/**
	 * Gets the total mips of all waiting cloudlets and mips remaining so far from currently executing cloudlets in a given datacenter. This method also 
	 * considers that previous cloudlets that arrived at the same time, have been assigned to vms but not yet "offically" mapped by CloudSim. 
	 * It calculates the mips to execute from those "yet to be mapped" cloudlets and adds them to the total. The "2" in this method name doesn't refer to
	 * datacenter 2 but that it's a variation of {@link #getTotalCloudletMipsInDC(Datacenter)}. 
	 * 
	 * @param datacenter the datacenter to get the total mips of all waiting cloudlets and remaining mips of executing cloudlets
	 * @param lastVmIdList the list of last vm ids that mapped a cloudlet
	 * @param lastCloudletMips the mips of the last cloudlet that arrived
	 * 
	 * @see #getTotalCloudletMipsInDC(Datacenter)
	 * 
	 * @return the total mips of all waiting and remaining mips of executing cloudlets in a datacenter
	 */
	public static long getTotalCloudletMipsInDC2 (Datacenter datacenter, List<Long> lastVmIdList, List<Long> lastCloudletMipsList) {

		long totalCloudletMips = 0;
		List<Vm> vmList = getVmList(datacenter);
		for (int i = 0; i < vmList.size(); i++) {
			totalCloudletMips = totalCloudletMips + getTotalCloudletMips2(vmList.get(i), lastVmIdList, lastCloudletMipsList);
		}
		
		return totalCloudletMips;
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
	 * @see #getTotalWaitingCloudletMips(Vm)
	 * @see #getTotalExecutingCloudletMips(Vm)
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

	/**
	 * Gets the number of cloudlets executing in a datacenter.
	 * 
	 * @param datacenter the datacenter to get the number of executing cloudlets
	 * 
	 * @return the number of cloudlets executing in a datacenter
	 */
	public static long getNumOfExecutingCloudletsInDC(Datacenter datacenter) {  	
		
		List<Vm> vmList = getVmList(datacenter);

		long cloudletExecListSize = 0;
		for (int i = 0; i < vmList.size(); i++) {
			Vm vm = vmList.get(i);
			cloudletExecListSize = cloudletExecListSize + getNumOfExecutingCloudlets(vm);  // gets all cloudlets which are executing in a vm
		}

		return cloudletExecListSize; 	
	}

	/**
	 * Determines if a datacenter is free (has at least one free vm). This method takes into account subsequent cloudlets 
	 * arriving at the same time. 
	 * 
	 * @param datacenter the datacenter to check if it's free
	 * @param lastVmIdList the last vm id list to use to check if a datacenter is free
	 * 
	 * @return true if a datacenter is free and false otherwise
	 */
	public static boolean isDatacenterFree(Datacenter datacenter, List<Long> lastVmIdList) {  	
		List<Vm> datacenterVmList = getVmList(datacenter);
		Vm mappedVm = datacenterVmList.stream()
						.filter(vm -> getNumOfExecutingCloudlets(vm) == 0
								&& !lastVmIdList.contains(vm.getId()))
						.findFirst() 
						.orElse(Vm.NULL);

		boolean datacenterFree;
		if (mappedVm != Vm.NULL) {
			datacenterFree = true;
		}
		else {
			datacenterFree = false;
		}
		return datacenterFree;
	}

	/**
	 * Determines if a datacenter is free (has at least one free vm). 
	 * 
	 * @param datacenter the datacenter to check if it's free
	 * 
	 * @return true if a datacenter is free and false otherwise
	 */
	public static boolean isDatacenterFree2(Datacenter datacenter) {  	
		List<Vm> datacenterVmList = getVmList(datacenter);
		Vm mappedVm = datacenterVmList.stream()
						.filter(vm -> getNumOfExecutingCloudlets(vm) == 0)
						.findFirst() 
						.orElse(Vm.NULL);

		/*
		 * Note that the lastVmIdList and lastCloudletMipsList don't need to be cleared in this method because they will be cleared 
		 * in the DatacenterBroker's defaultVmMapper() method. 
		 */ 
		boolean datacenterFree;
		if (mappedVm != Vm.NULL) {
			datacenterFree = true;
		}
		else {
			datacenterFree = false;
		}
		return datacenterFree;
	}

	/**
	 * Determines if datacenter 3 is free (has at least one free vm). This method takes into account subsequent cloudlets 
	 * arriving at the same time. 
	 * 
	 * @param datacenter datacenter 3 to check if it's free
	 * @param lastVmIdList the last vm id list to use to check if datacenter 3 is free
	 * @param currentSimulationTime the current simulation time (when the cloudlet arrived)
	 * @param lastCloudletArrivalTime the last cloudlet arrival time in datacenter 3
	 * 
	 * @return true if datacenter 3 is free and false otherwise
	 */
	public static boolean isDatacenter3Free(Datacenter datacenter, List<Long> lastVmIdList, double currentSimulationTime, double lastCloudletArrivalTime) {  	
		
		boolean datacenterFree;

		if (lastCloudletArrivalTime == currentSimulationTime) { 
			datacenterFree = isDatacenterFree(datacenter, lastVmIdList);
		}

		else {
			datacenterFree = isDatacenterFree2(datacenter);
		}

		return datacenterFree;
	}

	/**
	 * Determines if a cloudlet will go to datacenter 3. This method is optimal and will only assign 
	 * a cloudlet to datacenter 3 if datacenter 3 is free and datacenter 1 or 2 aren't. If neither
	 * datacenter 1 or 2 or datacenter 3 is free, the method will assign a cloudlet to datacenter 3 if 
	 * it has the least remaining work compared to datacenter 1 or 2.
	 * 
	 * @param currentSimulationTime the current simulation time (when the cloudlet arrived)
	 * @param cloudlet the cloudlet to determine if it will go to datacenter 3 
	 * @param lastCloudletArrivalTimeAllDC the list of the last cloudlet arrival times in all datacenters 
	 * @param lastCloudletMipsListAllDC the list of the last cloudlet mips in all datacenters 
	 * @param lastVmIdListAllDC the list of the last vm ids in all datacenters 
	 * @param datacenterList the list of all datacenters 
	 * 
	 */
	public static void determineIfCloudletGoesToDC3BestFit(double currentSimulationTime, Cloudlet cloudlet, List<Double> lastCloudletArrivalTimeAllDC,
	List<List<Long>> lastCloudletMipsListAllDC, List<List<Long>> lastVmIdListAllDC, List<Datacenter> datacenterList) {

		long cloudletJobId = cloudlet.getJobId();
		Datacenter datacenter = datacenterList.get((int) cloudletJobId - 1); // could be datacenter 1 or 2
		Datacenter datacenter3 = datacenterList.get(2);
		double lastCloudletArrivalTime = lastCloudletArrivalTimeAllDC.get((int) cloudletJobId - 1);
		double lastCloudletArrivalTimeDC3 = lastCloudletArrivalTimeAllDC.get(2);
		List<Long> lastCloudletMipsList = lastCloudletMipsListAllDC.get((int) cloudletJobId - 1);
		List<Long> lastCloudletMipsListDC3 = lastCloudletMipsListAllDC.get(2);
		List<Long> lastVmIdList = lastVmIdListAllDC.get((int) cloudletJobId - 1);
		List<Long> lastVmIdListDC3 = lastVmIdListAllDC.get(2);

		if (lastCloudletArrivalTime == currentSimulationTime) { // if the next cloudlet arrives at the same time as the last cloudlet
			boolean datacenterFree = isDatacenterFree(datacenter, lastVmIdList);
			boolean datacenter3Free = isDatacenter3Free(datacenter3, lastVmIdListDC3, currentSimulationTime, lastCloudletArrivalTimeDC3);

			if (datacenterFree == false && datacenter3Free == true) {
				cloudlet.setJobId(3);  // if datacenter 1 or 2 isn't free but datacenter 3 is then execute the cloudlet in datacenter 3 
			}

			// if datacenter 1 or 2 and datacenter 3 is busy pick the least busy of the datacenters 
			else if (datacenterFree == false && datacenter3Free == false) { 
				long totalCloudletMips = getTotalCloudletMipsInDC2(datacenter, lastVmIdList, lastCloudletMipsList);
				long totalCloudletMipsDC3 = getTotalCloudletMipsForDC3(datacenter3, currentSimulationTime, lastVmIdListDC3,
				lastCloudletArrivalTimeDC3, lastCloudletMipsListDC3);

				System.out.println("The total cloudlet mips of DC #" + cloudletJobId + " is: " + totalCloudletMips);
				System.out.println("The total cloudlet mips of DC #3 is: " + totalCloudletMipsDC3);

				if (totalCloudletMipsDC3 < totalCloudletMips) {  // if the two are equal (rare case) the cloudlet is sent to datacenter 1 or 2 instead of datacenter 3
					cloudlet.setJobId(3);  // if neither datacenter 1 or 2 or datacenter 3 is free then execute the cloudlet in datacenter 3 if it has the least remaining work 
				}
			}
		}

		else { // if the next cloudlet arrives at a different time than the last cloudlet
			boolean datacenterFree  = isDatacenterFree2(datacenter);
			boolean datacenter3Free = isDatacenter3Free(datacenter3, lastVmIdListDC3, currentSimulationTime, lastCloudletArrivalTimeDC3);

			if (datacenterFree == false && datacenter3Free == true) {
				cloudlet.setJobId(3);  
			}

			else if (datacenterFree == false && datacenter3Free == false) { 				
				long totalCloudletMips = getTotalCloudletMipsInDC(datacenter);
				long totalCloudletMipsDC3 = getTotalCloudletMipsForDC3(datacenter3, currentSimulationTime, lastVmIdListDC3,
				lastCloudletArrivalTimeDC3, lastCloudletMipsListDC3);
				
				System.out.println("The total cloudlet mips of DC #" + cloudletJobId + " is: " + totalCloudletMips);
				System.out.println("The total cloudlet mips of DC #3 is: " + totalCloudletMipsDC3);

				if (totalCloudletMipsDC3 < totalCloudletMips) {  
					cloudlet.setJobId(3);
				}
			}
		}
	}

	/**
	 * Gets the total mips for datacenter 3. This method may consider that previous cloudlets that arrived at the same time,
	 * have been assigned to vms but not yet "offically" mapped by CloudSim. It calculates the mips to execute from those 
	 * "yet to be mapped" cloudlets and adds them to the total. 
	 * 
	 * @param datacenter datacenter 3 to get the total mips of all waiting cloudlets and remaining mips of executing cloudlets
	 * @param currentSimulationTime the current simulation time (when the cloudlet arrived)
	 * @param lastVmIdList the list of last vm ids that mapped a cloudlet
	 * @param lastCloudletArrivalTime the list of the last cloudlet arrival times to get the cloudlet mips in a datacenter 
	 * @param lastCloudletMipsList the list of the last cloudlet mips to help print the cloudlet mips in every vm in a datacenter 
	 * 
	 * @see #getTotalCloudletMipsInDC(Datacenter)
	 * 
	 * @return the total mips of all waiting and remaining mips of executing cloudlets in datacenter 3
	 */
	public static long getTotalCloudletMipsForDC3 (Datacenter datacenter, double currentSimulationTime,  List<Long> lastVmIdList, 
	double lastCloudletArrivalTime, List<Long> lastCloudletMipsList) {
	
		long totalCloudletMips = 0;

		if (lastCloudletArrivalTime == currentSimulationTime) { // if the next cloudlet arrives at the same time as the last cloudlet
			totalCloudletMips = getTotalCloudletMipsInDC2(datacenter, lastVmIdList, lastCloudletMipsList);
		}

		else {
			totalCloudletMips = getTotalCloudletMipsInDC(datacenter);
		}	

		return totalCloudletMips;
	}

	/**
	 * Prints the number of cloudlets executing in all datacenters. 
	 * 
	 * @param datacenterList the list of all datacenters that will have their number of cloudlets executing printed out
	 * 
	 */
	public static void printNumOfExecutingCloudletsInAllDC(List<Datacenter> datacenterList) {
		
		System.out.println();
		for (int i = 0; i < datacenterList.size(); i++) {
			Datacenter datacenter = datacenterList.get(i);
			System.out.println("The number of cloudlets executing in DC #" + datacenter.getId() + 
			" is: " + getNumOfExecutingCloudletsInDC(datacenter));
		}
	}

	/**
	 * Prints the total cloudlet mips in every vm in a datacenter. This method considers that subsequent cloudlets may be arriving at the 
	 * same time. 
	 * 
	 * @param datacenter the datacenter that will have all of its vm's cloudlet mips printed out 
	 * @param lastVmIdList the list of the last vm ids to help print the cloudlet mips in every vm in a datacenter
	 * @param lastCloudletMipsList the list of the last cloudlet mips to help print the cloudlet mips in every vm in a datacenter 
	 */
	public static void printTotalCloudletMipsInAllVmsInDCSubsequent(Datacenter datacenter, List<Long> lastVmIdList, 
	List<Long> lastCloudletMipsList) {

		List<Vm> vmList = getVmList(datacenter);

		for (int i = 0; i < vmList.size(); i++) {
			Vm vm = vmList.get(i);
			System.out.println("The total number of mips to execute for Vm #" + vm.getId()
					+ " is: " + DatacenterBrokerUtility.getTotalCloudletMips2(vm, lastVmIdList, lastCloudletMipsList));
		}
	}

	/**
	 * Prints the total cloudlet mips in a datacenter. This method considers that subsequent cloudlets may be arriving at the 
	 * same time. 
	 * 
	 * @param datacenter the datacenter that will have its cloudlet mips printed out 
	 * @param lastVmIdList the list of the last vm ids to help print the cloudlet mips in a datacenter 
	 * @param lastCloudletMipsList the list of the last cloudlet mips to help print the cloudlet mips in a datacenter 
	 */
	public static void printTotalCloudletMipsInDCSubsequent(Datacenter datacenter, List<Long> lastVmIdList, 
	List<Long> lastCloudletMipsList) {

		System.out.println("The total number of mips in DC #" + datacenter.getId() + " is: "
		+ DatacenterBrokerUtility.getTotalCloudletMipsInDC2(datacenter, lastVmIdList, lastCloudletMipsList));
	}

	/**
	 * Prints the total cloudlet mips in every vm in a datacenter. 
	 * 
	 * @param datacenter the datacenter that will have all of its vm's cloudlet mips printed out 
	 */
	public static void printTotalCloudletMipsInAllVmsInDCNonSubsequent(Datacenter datacenter) {
		
		List<Vm> vmList = getVmList(datacenter);

		for (int i = 0; i < vmList.size(); i++) {
			Vm vm = vmList.get(i);
			System.out.println("The total number of mips to execute for Vm #" + vm.getId()
					+ " is: " + DatacenterBrokerUtility.getTotalCloudletMips(vm));
		}
	}

	/**
	 * Prints the total cloudlet mips in a datacenter. 
	 * 
	 * @param datacenter the datacenter that will have its cloudlet mips printed out 
	 */
	public static void printTotalCloudletMipsInDCNonSubsequent(Datacenter datacenter) {
		
		System.out.println("The total number of mips in DC #" + datacenter.getId() + " is: "
		+ DatacenterBrokerUtility.getTotalCloudletMipsInDC(datacenter));
	}

	/**
	 * Prints the total cloudlet mips in every vm in a datacenter. This method considers that subsequent cloudlets may be arriving at the 
	 * same time. 
	 * 
	 * @param currentSimulationTime the current simulation time (when the cloudlet arrived)
	 * @param lastCloudletArrivalTimeAllDC the list of the last cloudlet arrival times to help print the cloudlet mips in every vm in a datacenter 
	 * @param lastCloudletMipsListAllDC the list of the last cloudlet mips to help print the cloudlet mips in every vm in a datacenter 
	 * @param lastVmIdListAllDC the list of the last vm ids to help print the cloudlet mips in every vm in a datacenter
	 * @param datacenterList the list of datacenters that will have all of their vm's cloudlet mips printed out 
	 */
	public static void printTotalCloudletMipsInAllVmsInAllDC(double currentSimulationTime, List<Double> lastCloudletArrivalTimeAllDC,
	List<List<Long>> lastCloudletMipsListAllDC, List<List<Long>> lastVmIdListAllDC, List<Datacenter> datacenterList) {
		
		System.out.println();

		for (int i = 0; i < datacenterList.size(); i++) {
			Datacenter datacenter = datacenterList.get(i); 
			double lastCloudletArrivalTime = lastCloudletArrivalTimeAllDC.get(i);
			List<Long> lastVmIdList = lastVmIdListAllDC.get(i);
			List<Long> lastCloudletMipsList = lastCloudletMipsListAllDC.get(i); 

			System.out.println("DC #" + datacenter.getId() + ":");

			if (lastCloudletArrivalTime == currentSimulationTime) { // if the next cloudlet arrives at the same time as the last cloudlet
				printTotalCloudletMipsInAllVmsInDCSubsequent(datacenter, lastVmIdList, lastCloudletMipsList);
			}
	
			else {
				printTotalCloudletMipsInAllVmsInDCNonSubsequent(datacenter);
			}
		}	
	}

	/**
	 * Prints the total cloudlet mips in all datacenters. This method considers that subsequent cloudlets may be arriving at the 
	 * same time. 
	 * 
	 * 
 	 * @param currentSimulationTime the current simulation time (when the cloudlet arrived)
	 * @param lastCloudletArrivalTimeAllDC the list of the last cloudlet arrival times to help calculate the cloudlet mips in all datacenters 
	 * @param lastCloudletMipsListAllDC the list of the last cloudlet mips to help calculate the cloudlet mips in all datacenters  
	 * @param lastVmIdListAllDC the list of the last vm ids to help calculate the cloudlet mips in all datacenters 
	 * @param datacenterList the list of all datacenters 
	 */
	public static void printTotalCloudletMipsInAllDC(double currentSimulationTime, List<Double> lastCloudletArrivalTimeAllDC,
	List<List<Long>> lastCloudletMipsListAllDC, List<List<Long>> lastVmIdListAllDC, List<Datacenter> datacenterList) {
		
		System.out.println();

		for (int i = 0; i < datacenterList.size(); i++) {
			Datacenter datacenter = datacenterList.get(i); 
			double lastCloudletArrivalTime = lastCloudletArrivalTimeAllDC.get(i);
			List<Long> lastVmIdList = lastVmIdListAllDC.get(i);
			List<Long> lastCloudletMipsList = lastCloudletMipsListAllDC.get(i); 

			if (lastCloudletArrivalTime == currentSimulationTime) { // if the next cloudlet arrives at the same time as the last cloudlet
				printTotalCloudletMipsInDCSubsequent(datacenter, lastVmIdList, lastCloudletMipsList);
			}

			else {
				printTotalCloudletMipsInDCNonSubsequent(datacenter);
			}
		}	
	}

	/**
	 * Prints the datacenter that was selected (datacenter with the least/most remaining work or selected as apart of a cycle)
	 * to have one of its vms execute a cloudlet. This method must be called after 
	 * {@link #determineIfCloudletGoesToDC3BestFit(double, Cloudlet, List, List, List, List)}. 
	 * 
	 * @param cloudletJobId the job id of the cloudlet to determine which datacenter was selected
	 * 
	 * @see #determineIfCloudletGoesToDC3BestFit(double, Cloudlet, List, List, List, List)
	 */
	public static void printSelectedDC(long cloudletJobId) {
		System.out.println("Datacenter #" + cloudletJobId + " was selected to have one of its vms " + 
			"execute a cloudlet.\n");  // the cloudlet job id is the same as the datacenter id 	
	}

	/**
	 * Prints the cloudlet job ID message. 
	 * 
	 * @param oldCloudletJobId the old job id of the cloudlet 
	 * @param newcloudletJobId the new job id of the cloudlet 
	 */
	public static void printCloudletJobIdMessage(long oldCloudletJobId, long newCloudletJobId) {

		if (newCloudletJobId == 3) {
			System.out.println("*********CLOUDLET JOB IS: " + newCloudletJobId + " (Was originally: " + oldCloudletJobId + ")");
		}

		else {
			System.out.println("*********CLOUDLET JOB IS: " + newCloudletJobId);
		}
	}
}