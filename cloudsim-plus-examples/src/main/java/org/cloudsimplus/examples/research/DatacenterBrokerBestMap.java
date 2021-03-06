package org.cloudsimplus.examples.research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An implementation of {@link DatacenterBroker} that uses a best
 * mapping between submitted cloudlets and vms. It maps an incoming cloudlet to
 * the vm that can execute the cloudlet the fastest (vm with the least remaining
 * work). The vm with the least remaining work, is the one with smallest amount
 * of cloudlet mips (remaining length of running cloudlets + length of waiting
 * cloudlets). This policy also tries to map incoming cloudlets to any
 * free vms first before finding the vm with the least remaining work. It simply
 * selects the first free vm available when possible (will go in order).
 *
 * @author Chigozie Asikaburu
 * @since CloudSim Plus 4.6.0
 */
public class DatacenterBrokerBestMap extends DatacenterBrokerSimple {

	/**
	 * Stores the last cloudlet's arrival time in datacenter 1. This value is gotten
	 * from the current simulation time when the last cloudlet arrived.
	 */
	double lastCloudletArrivalTimeDC1 = -2;

	/**
	 * Stores the last cloudlet's arrival time in datacenter 2. This value is gotten
	 * from the current simulation time when the last cloudlet arrived.
	 */
	double lastCloudletArrivalTimeDC2 = -2;

	/**
	 * Stores the last vm's id used to map a cloudlet in datacenter 1. It's
	 * initially set to -2 instead of 0 to make sure that the first cloudlet is
	 * mapped normally. In this way this attribute is only considered after the
	 * first cloudlet. It's not -1 because -1 is used as the id for null vms in
	 * CloudSim.
	 */
	long lastVmIdDC1 = -2;

	/**
	 * Stores the last vm's id used to map a cloudlet in datacenter 2. It's
	 * initially set to -2 instead of 0 to make sure that the first cloudlet is
	 * mapped normally. In this way this attribute is only considered after the
	 * first cloudlet. It's not -1 because -1 is used as the id for null vms in
	 * CloudSim.
	 */
	long lastVmIdDC2 = -2;

	/**
	 * Stores the last vm's id in a list in the event that subsequent cloudlets
	 * arrive at the same time. It will store them until a subsequent cloudlet
	 * arrives at a different time. This is done to make sure that many subsequent
	 * cloudlets arriving at the same time don't get mapped to the same vm. If a
	 * variable was used to remember the last vm id it may eventaully cycle back to
	 * a vm which it thought was free (because the variable was changed) but is no
	 * longer free since it has work. So a list of vm ids is used instead of just a
	 * variable. The list is cleared once a subsequent cloudlet arrives at a
	 * different time.
	 */
	List<Long> lastVmIdListDC1 = new ArrayList<Long>(Arrays.asList((long) -2));

	/**
	 * Stores the last vm's id in a list in the event that subsequent cloudlets
	 * arrive at the same time. It will store them until a subsequent cloudlet
	 * arrives at a different time. This is done to make sure that many subsequent
	 * cloudlets arriving at the same time don't get mapped to the same vm. If a
	 * variable was used to remember the last vm id it may eventaully cycle back to
	 * a vm which it thought was free (because the variable was changed) but is no
	 * longer free since it has work. So a list of vm ids is used instead of just a
	 * variable. The list is cleared once a subsequent cloudlet arrives at a
	 * different time.
	 */
	List<Long> lastVmIdListDC2 = new ArrayList<Long>(Arrays.asList((long) -2));

    /**
     * A list of all datacenter's lastVmIdList.
     */
    List<List<Long>> lastVmIdListAllDC = new ArrayList<List<Long>>(
        Arrays.asList(lastVmIdListDC1, lastVmIdListDC2));

	/**
	 * Stores the mips of the last cloudlet that arrived in datacenter 1.
	 */
	long lastCloudletMipsDC1 = -2;

	/**
	 * Stores the mips of the last cloudlet that arrived in datacenter 2.
	 */
	long lastCloudletMipsDC2 = -2;

	/**
	 * A list used for storing the mips of subsequently arriving cloudlets. This
	 * list is used in order to determine the vm to execute a cloudlet next. This
	 * list is cleared when the next cloudlet arrives at a different time than a
	 * subsequently arriving cloudlet (which arrived at the same time as previous
	 * cloudlets).
	 */
	List<Long> lastCloudletMipsListDC1 = new ArrayList<Long>(Arrays.asList((long) -2));

	/**
	 * A list used for storing the mips of subsequently arriving cloudlets. This
	 * list is used in order to determine the vm to execute a cloudlet next. This
	 * list is cleared when the next cloudlet arrives at a different time than a
	 * subsequently arriving cloudlet (which arrived at the same time as previous
	 * cloudlets).
	 */
	List<Long> lastCloudletMipsListDC2 = new ArrayList<Long>(Arrays.asList((long) -2));

    /**
     * A list of all datacenter's lastCloudletMipsList.
     */
    List<List<Long>> lastCloudletMipsListAllDC = new ArrayList<List<Long>>(
        Arrays.asList(lastCloudletMipsListDC1, lastCloudletMipsListDC2));

	/**
	 * Creates a DatacenterBroker object.
	 *
	 * @param simulation The CloudSim instance that represents the simulation the
	 *                   Entity is related to
	 */
	public DatacenterBrokerBestMap(final CloudSim simulation) {
		super(simulation);
	}

	/**
	 * <p>
	 * <b>It applies a best mapping policy to select the next Vm from the
	 * {@link #getVmWaitingList() list of waiting VMs}.
	 * </p>
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

		double simulationTime = getSimulation().clock();
		List<Datacenter> datacenterList = getDatacenterList();
		long cloudletJobId = cloudlet.getJobId();
		
		/*
     	* Stores the last cloudlet's arrival time in all datacenters. This value is gotten
     	* from the current simulation time when the last cloudlet arrived.
     	*/
		 List<Double> lastCloudletArrivalTimeAllDC = new ArrayList<Double>(Arrays.asList(lastCloudletArrivalTimeDC1,
		 lastCloudletArrivalTimeDC2));

		//DatacenterBrokerUtility.printCloudletJobIdMessage(cloudletJobId);

		//DatacenterBrokerUtility.printTotalCloudletMipsInAllVmsInAllDC(simulationTime,
		//lastCloudletArrivalTimeAllDC, lastCloudletMipsListAllDC, lastVmIdListAllDC, datacenterList);

		//DatacenterBrokerUtility.printNumOfExecutingCloudletsInAllDC(datacenterList);

		if (cloudletJobId == 1) { // if the cloudlet's job id is 1 it will be executed in a vm in datacenter 1
			Datacenter datacenter = datacenterList.get(0);

			List<Vm> datacenterVmList = DatacenterBrokerUtility.getVmList(datacenter);

			/*
			 * If subsequent cloudlets arrive at the same time then make sure it doesn't get
			 * mapped to the last vm "free vm" used. This is done so we don't assign
			 * subsequent cloudlets to a vm which was deemed to be "free" but is actually
			 * not. The current simulation time is used to check when cloudlets arrive.
			 */
			if (lastCloudletArrivalTimeDC1 == simulationTime) {
				// if a vm in this datacenter is free then assign the cloudlet to that vm
				//System.out.println("Subsequent cloudlet arriving at the same time");
				mappedVm = datacenterVmList.stream()
						.filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0
						&& DatacenterBrokerUtility.getNumOfWaitingCloudlets(vm) == 0 && !lastVmIdListDC1.contains(vm.getId()))
						.findFirst() // return the first free vm if it exists
						.orElse(Vm.NULL);
			}

			else {
				mappedVm = datacenterVmList.stream()
						.filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0
						&& DatacenterBrokerUtility.getNumOfWaitingCloudlets(vm) == 0).findFirst()
						.orElse(Vm.NULL);
				// a subsequent cloudlet arrived at a different time so clear the cloudlet mips list
				lastVmIdListDC1.clear();
				lastCloudletMipsListDC1.clear(); 
			}

			if (mappedVm != Vm.NULL) { // if there is a free vm
				// keep track of the id of a mapped vm (a vm with the least remaining work in this case)
				lastVmIdDC1 = mappedVm.getId(); 
				//System.out.println("A free Vm was found");
			}

			else { // if there is no free vm

				/*
				 * For subsequent cloudlets make sure to find the vm with the least remaining
				 * work considering that previous cloudlets arriving at the same time have been
				 * assigned to vms but not yet mapped. This makes sure that those
				 * "yet to be mapped" cloudlets are considered when finding the vm with the
				 * least remaining work.
				 */
				if (lastCloudletArrivalTimeDC1 == simulationTime) {
					//System.out.println("No free vm was found so one with the least remaining work was chosen.");

					mappedVm = datacenterVmList
						.stream()
						.min(Comparator.comparingLong(vm -> DatacenterBrokerUtility.getTotalCloudletMips2(vm,
							simulationTime, lastVmIdListDC1, lastCloudletMipsListDC1))) // select the vm with the smallest total cloudlet mips
						.orElse(Vm.NULL);
				}

				else {
					//System.out.println("No free vm was found so one with the least remaining work was chosen.");

					mappedVm = datacenterVmList.stream()
							.min(Comparator.comparingLong(vm -> DatacenterBrokerUtility.getTotalCloudletMips(vm, simulationTime))) 
							.orElse(Vm.NULL);
				}
				// keep track of the id of a mapped vm (a vm with the least remaining work in this case)
				lastVmIdDC1 = mappedVm.getId(); 
			}
			lastCloudletArrivalTimeDC1 = simulationTime;
			lastCloudletMipsListDC1.add(cloudlet.getLength());
			lastVmIdListDC1.add(lastVmIdDC1);
		}

		else if (cloudletJobId == 2) { // if the cloudlet's job id is 2 it will be executed in a vm in datacenter 2
			Datacenter datacenter = datacenterList.get(1);

			List<Vm> datacenterVmList = DatacenterBrokerUtility.getVmList(datacenter);

			/* if subsequent cloudlets arrive at the same time then make sure it doesn't get
			   mapped to the last vm used */
			if (lastCloudletArrivalTimeDC2 == simulationTime) {
				// if a vm in this datacenter is free then assign the cloudlet to that vm
				//System.out.println("Subsequent cloudlet arriving at the same time");

				mappedVm = datacenterVmList.stream()
						.filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0
						&& DatacenterBrokerUtility.getNumOfWaitingCloudlets(vm) == 0 && !lastVmIdListDC2.contains(vm.getId())) 
						.findFirst()
						.orElse(Vm.NULL);
			}

			else {
				mappedVm = datacenterVmList.stream()
						.filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0
						&& DatacenterBrokerUtility.getNumOfWaitingCloudlets(vm) == 0) 
						.findFirst() 
						.orElse(Vm.NULL);
				lastVmIdListDC2.clear();
				// VV a subsequent cloudlet arrived at a different time so clear the cloudlet mips list
				lastCloudletMipsListDC2.clear(); 
			}

			if (mappedVm != Vm.NULL) { // if there is a free vm
				lastVmIdDC2 = mappedVm.getId();
				//System.out.println("A free Vm was found");
			}

			else { // if there is no free vm

				if (lastCloudletArrivalTimeDC2 == simulationTime) {

					//System.out.println("No free vm was found so one with the least remaining work was chosen.");

					mappedVm = datacenterVmList.stream()
							.min(Comparator.comparingLong(vm -> DatacenterBrokerUtility.getTotalCloudletMips2(vm,
									simulationTime, lastVmIdListDC2, lastCloudletMipsListDC2))) /* select the vm with the shortest total
																				 cloudlet mips */
							.orElse(Vm.NULL);
				}

				else {

					//System.out.println("No free vm was found so one with the least remaining work was chosen.");

					mappedVm = datacenterVmList.stream()
							.min(Comparator.comparingLong(vm -> DatacenterBrokerUtility.getTotalCloudletMips(vm, simulationTime))) 
							.orElse(Vm.NULL);
				}
				lastVmIdDC2 = mappedVm.getId();
			}
			lastCloudletArrivalTimeDC2 = simulationTime;
			lastCloudletMipsListDC2.add(cloudlet.getLength());
			lastVmIdListDC2.add(lastVmIdDC2);
		}
		/* either returns a vm that's free or the vm that will execute the cloudlet the 
		fastest (has the least instructions to execute) */
		return mappedVm; 
	}
}
