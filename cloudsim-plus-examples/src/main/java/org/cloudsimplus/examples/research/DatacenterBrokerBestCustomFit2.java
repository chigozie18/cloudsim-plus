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
 * An implementation of {@link DatacenterBroker} that uses a custom and optimal
 * mapping between submitted cloudlets and vms. First, it selects the datacenter 
 * with the least remaining work between either datacenter 1/2 or datacenter 3.
 * The datacenter with the least remaining work is the datacenter with the smallest amount 
 * of cloudlet mips (remaining length of running cloudlets + length of waiting
 * cloudlets), which is the sum of all cloudlet mips from all the vms in the datacenter. 
 * 
 * <br><br>
 * It then maps an incoming cloudlet to the vm in the selected datacenter that can execute 
 * the cloudlet the fastest (vm with the least remaining work). The vm with the least 
 * remaining work, is the one with the smallest amount of cloudlet mips 
 * (remaining length of running cloudlets + length of waiting cloudlets). 
 * This policy also tries to select datacenter 1/2 (to have one of it's vms exectue a cloudlet) 
 * first if it's free (has at least one free vm) or selects datacenter 3 afterwards if it's free 
 * before selecting the datacenter with the least remaining work. It will then try to map a 
 * cloudlet to a free vm in that selected datacenter before finding the vm with the least remaining work. 
 * It simply selects the first free vm available when possible (will go in order).
 *
 * @author Chigozie Asikaburu
 * @since CloudSim Plus 4.6.0
 */
public class DatacenterBrokerBestCustomFit2 extends DatacenterBrokerSimple {

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
     * Stores the last cloudlet's arrival time in datacenter 3. This value is gotten
     * from the current simulation time when the last cloudlet arrived.
     */
    double lastCloudletArrivalTimeDC3 = -2;

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
     * Stores the last vm's id used to map a cloudlet in datacenter 3. It's
     * initially set to -2 instead of 0 to make sure that the first cloudlet is
     * mapped normally. In this way this attribute is only considered after the
     * first cloudlet. It's not -1 because -1 is used as the id for null vms in
     * CloudSim.
     */
    long lastVmIdDC3 = -2;

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
    List<Long> lastVmIdListDC3 = new ArrayList<Long>(Arrays.asList((long) -2));

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
    List<List<Long>> lastVmIdListAllDC = new ArrayList<List<Long>>
    (Arrays.asList(lastVmIdListDC1,lastVmIdListDC2, lastVmIdListDC3));

    /**
     * Stores the mips of the last cloudlet that arrived in datacenter 1.
     */
    long lastCloudletMipsDC1 = -2;

    /**
     * Stores the mips of the last cloudlet that arrived in datacenter 2.
     */
    long lastCloudletMipsDC2 = -2;

    /**
     * Stores the mips of the last cloudlet that arrived in datacenter 3.
     */
    long lastCloudletMipsDC3 = -2;

    /**
     * A list used for storing the mips of subsequently arriving cloudlets. This list is 
     * used in order to determine the vm to execute a cloudlet next. This list is cleared when the
     * next cloudlet arrives at a different time than a subsequently arriving cloudlet (which arrived at
     * the same time as previous cloudlets).
     */
    List<Long> lastCloudletMipsListDC1 = new ArrayList<Long>(Arrays.asList((long) -2));

    /**
     * A list used for storing the mips of subsequently arriving cloudlets. This list is 
     * used in order to determine the vm to execute a cloudlet next. This list is cleared when the
     * next cloudlet arrives at a different time than a subsequently arriving cloudlet (which arrived at
     * the same time as previous cloudlets).
     */
    List<Long> lastCloudletMipsListDC2 = new ArrayList<Long>(Arrays.asList((long) -2));

    /**
     * A list used for storing the mips of subsequently arriving cloudlets. This list is 
     * used in order to determine the vm to execute a cloudlet next. This list is cleared when the
     * next cloudlet arrives at a different time than a subsequently arriving cloudlet (which arrived at
     * the same time as previous cloudlets).
     */
    List<Long> lastCloudletMipsListDC3 = new ArrayList<Long>(Arrays.asList((long) -2));

     /**
     * Represents a list of lists containing lists from all datacenter's lastCloudletMipsList. 
     */
    List<List<Long>> lastCloudletMipsListAllDC = new ArrayList<List<Long>>
    (Arrays.asList(lastCloudletMipsListDC1, lastCloudletMipsListDC2, lastCloudletMipsListDC3));

    /**
     * Creates a DatacenterBroker object.
     *
     * @param simulation The CloudSim instance that represents the simulation the
     *                   Entity is related to
     */
    public DatacenterBrokerBestCustomFit2(final CloudSim simulation) {
		super(simulation);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * <b>It applies a Round-Robin policy to cyclically select the next Vm from the
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

		/*
     	* Stores the last cloudlet's arrival time in all datacenters. This value is gotten
     	* from the current simulation time when the last cloudlet arrived.
     	*/
		List<Double> lastCloudletArrivalTimeAllDC = new ArrayList<Double>(Arrays.asList(lastCloudletArrivalTimeDC1,
		lastCloudletArrivalTimeDC2, lastCloudletArrivalTimeDC3));
	
		long oldCloudletJobId = cloudlet.getJobId();

		DatacenterBrokerUtility.determineIfCloudletGoesToDC3BestFit(Double.parseDouble(getSimulation().clockStr()), 
		cloudlet, lastCloudletArrivalTimeAllDC, lastCloudletMipsListAllDC, lastVmIdListAllDC, getDatacenterList());
		
		DatacenterBrokerUtility.printCloudletJobIdMessage(oldCloudletJobId, cloudlet.getJobId());

		DatacenterBrokerUtility.printTotalCloudletMipsInAllDC(Double.parseDouble(getSimulation().clockStr()),
		lastCloudletArrivalTimeAllDC, lastCloudletMipsListAllDC, lastVmIdListAllDC, getDatacenterList());
		
		DatacenterBrokerUtility.printTotalCloudletMipsInAllVmsInAllDC(Double.parseDouble(getSimulation().clockStr()),
		lastCloudletArrivalTimeAllDC, lastCloudletMipsListAllDC, lastVmIdListAllDC, getDatacenterList());

		DatacenterBrokerUtility.printNumOfExecutingCloudletsInAllDC(getDatacenterList());

		DatacenterBrokerUtility.printSelectedDC(cloudlet.getJobId());

		if (cloudlet.getJobId() == 1) { // if the cloudlet's job id is 1 it will be executed in a vm in datacenter 1
			Datacenter datacenter = getDatacenterList().get(0);

			List<Vm> datacenterVmList = DatacenterBrokerUtility.getVmList(datacenter);
			
			/*
			 * If subsequent cloudlets arrive at the same time then make sure it doesn't get
			 * mapped to the last vm "free vm" used. This is done so we don't assign
			 * subsequent cloudlets to a vm which was deemed to be "free" but is actually
			 * not. The current simulation time is used to check when cloudlets arrive.
			 */
			if (lastCloudletArrivalTimeDC1 == Double.parseDouble(getSimulation().clockStr())) {
				// if a vm in this datacenter is free then assign the cloudlet to that vm
				System.out.println("Subsequent cloudlet arriving at the same time");
				mappedVm = datacenterVmList.stream()
						.filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0
								&& !lastVmIdListDC1.contains(vm.getId()))
						.findFirst() // return the first free vm if it exists
						.orElse(Vm.NULL);
			}

			else {
				mappedVm = datacenterVmList.stream()
						.filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0).findFirst()
						.orElse(Vm.NULL);
				lastVmIdListDC1.clear(); // a subsequent cloudlet arrived at a different time so clear the vm list
				// a subsequent cloudlet arrived at a different time so clear the cloudlet mips list
				lastCloudletMipsListDC1.clear(); 
			}

			if (mappedVm != Vm.NULL) { // if there is a free vm
				// keep track of the id of a mapped vm (a vm with the least remaining work in this case)
				lastVmIdDC1 = mappedVm.getId(); 
				System.out.println("A free Vm was found");
				System.out.println("The total waiting cloudlet mips is: "
						+ DatacenterBrokerUtility.getTotalWaitingCloudletMips(mappedVm)); 
				System.out.println("The total executing cloudlet mips is: "
						+ DatacenterBrokerUtility.getTotalExecutingCloudletMips(mappedVm));
				System.out.println("The number of cloudlets executing is: "
						+ DatacenterBrokerUtility.getNumOfExecutingCloudlets(mappedVm));
			}

			else { // if there is no free vm

				/*
				 * For subsequent cloudlets make sure to find the vm with the least remaining
				 * work considering that previous cloudlets arriving at the same time have been
				 * assigned to vms but not yet mapped. This makes sure that those
				 * "yet to be mapped" cloudlets are considered when finding the vm with the
				 * least remaining work.
				 */
				if (lastCloudletArrivalTimeDC1 == Double.parseDouble(getSimulation().clockStr())) {
					System.out.println("No free vm was found so one with the least remaining work was chosen.");

					for (int i = 0; i < datacenterVmList.size(); i++) {
						System.out.println("The total number of mips to execute for Vm #" + datacenterVmList.get(i).getId()
						+ " is: " + DatacenterBrokerUtility.getTotalCloudletMips2(datacenterVmList.get(i), lastVmIdListDC1, lastCloudletMipsListDC1));
					}

					mappedVm = datacenterVmList
						.stream()
						.min(Comparator.comparingLong(vm -> DatacenterBrokerUtility.getTotalCloudletMips2(vm,
							lastVmIdListDC1, lastCloudletMipsListDC1))) // select the vm with the smallest total cloudlet mips
						.orElse(Vm.NULL);
				}

				else {
					System.out.println("No free vm was found so one with the least remaining work was chosen.");

					for (int i = 0; i < datacenterVmList.size(); i++) {
						System.out.println("The total number of mips to execute for Vm #"
								+ datacenterVmList.get(i).getId() + " is: "
								+ DatacenterBrokerUtility.getTotalCloudletMips(datacenterVmList.get(i)));
					}

					mappedVm = datacenterVmList.stream()
							.min(Comparator.comparingLong(vm -> DatacenterBrokerUtility.getTotalCloudletMips(vm))) 
							.orElse(Vm.NULL);
				}
				// keep track of the id of a mapped vm (a vm with the least remaining work in this case)
				lastVmIdDC1 = mappedVm.getId(); 
			}
			lastCloudletArrivalTimeDC1 = Double.parseDouble(getSimulation().clockStr());
			lastCloudletMipsListDC1.add(cloudlet.getLength());
			lastVmIdListDC1.add(lastVmIdDC1);
		}

		if (cloudlet.getJobId() == 2) { // if the cloudlet's job id is 2 it will be executed in a vm in datacenter 2
			Datacenter datacenter = getDatacenterList().get(1);

			List<Vm> datacenterVmList = DatacenterBrokerUtility.getVmList(datacenter);

			/* if subsequent cloudlets arrive at the same time then make sure it doesn't get
			   mapped to the last vm used */
			if (lastCloudletArrivalTimeDC2 == Double.parseDouble(getSimulation().clockStr())) {
				// if a vm in this datacenter is free then assign the cloudlet to that vm
				System.out.println("Subsequent cloudlet arriving at the same time");

				System.out.println("The ids in the last vm id list are ");
				for (int i = 0; i < lastVmIdListDC2.size(); i++) {
					System.out.println(lastVmIdListDC2.get(i));
				}

				mappedVm = datacenterVmList.stream()
						.filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0
								&& !lastVmIdListDC2.contains(vm.getId())) 
						.findFirst()
						.orElse(Vm.NULL);
			}

			else {
				mappedVm = datacenterVmList.stream()
						.filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0) 
						.findFirst() 
						.orElse(Vm.NULL);
				lastVmIdListDC2.clear();
				// VV a subsequent cloudlet arrived at a different time so clear the cloudlet mips list
				lastCloudletMipsListDC2.clear(); 
			}

			if (mappedVm != Vm.NULL) { // if there is a free vm
				lastVmIdDC2 = mappedVm.getId();
				System.out.println("A free Vm was found");
				System.out.println("The total waiting cloudlet mips is: "
						+ DatacenterBrokerUtility.getTotalWaitingCloudletMips(mappedVm));
				System.out.println("The total executing cloudlet mips is: "
						+ DatacenterBrokerUtility.getTotalExecutingCloudletMips(mappedVm));
				System.out.println("The number of cloudlets executing is: "
						+ DatacenterBrokerUtility.getNumOfExecutingCloudlets(mappedVm));
				System.out.println("The number of waiting cloudlets is: "
						+ DatacenterBrokerUtility.getNumOfWaitingCloudlets(mappedVm));
			}

			else { // if there is no free vm

				if (lastCloudletArrivalTimeDC2 == Double.parseDouble(getSimulation().clockStr())) {

					System.out.println("No free vm was found so one with the least remaining work was chosen.");

					for (int i = 0; i < datacenterVmList.size(); i++) {
						System.out.println(
								"The total number of mips to execute for Vm #" + datacenterVmList.get(i).getId()
								+ " is: " + DatacenterBrokerUtility.getTotalCloudletMips2(
								datacenterVmList.get(i), lastVmIdListDC2, lastCloudletMipsListDC2));
					}
					mappedVm = datacenterVmList.stream()
							.min(Comparator.comparingLong(vm -> DatacenterBrokerUtility.getTotalCloudletMips2(vm,
									lastVmIdListDC2, lastCloudletMipsListDC2))) /* select the vm with the shortest total
																				 cloudlet mips */
							.orElse(Vm.NULL);
					System.out.println("The number of waiting cloudlets of the mapped vm is: "
							+ DatacenterBrokerUtility.getNumOfWaitingCloudlets(mappedVm));
					System.out.println("The number of executing cloudlets of the mapped vm is: "
							+ DatacenterBrokerUtility.getNumOfExecutingCloudlets(mappedVm));
					System.out.println("The total waiting cloudlet mips of the mapped vm is: "
							+ DatacenterBrokerUtility.getTotalWaitingCloudletMips(mappedVm));
					System.out.println("The total executing cloudlet mips of the mapped vm is: "
							+ DatacenterBrokerUtility.getTotalExecutingCloudletMips(mappedVm));

				}

				else {

					System.out.println("No free vm was found so one with the least remaining work was chosen.");

					for (int i = 0; i < datacenterVmList.size(); i++) {
						System.out.println("The total number of mips to execute for Vm #"
								+ datacenterVmList.get(i).getId() + " is: "
								+ DatacenterBrokerUtility.getTotalCloudletMips(datacenterVmList.get(i)));
					}
					mappedVm = datacenterVmList.stream()
							.min(Comparator.comparingLong(vm -> DatacenterBrokerUtility.getTotalCloudletMips(vm))) 
							.orElse(Vm.NULL);
					System.out.println("The number of waiting cloudlets of the mapped vm is: "
							+ DatacenterBrokerUtility.getNumOfWaitingCloudlets(mappedVm));
					System.out.println("The number of executing cloudlets of the mapped vm is: "
							+ DatacenterBrokerUtility.getNumOfExecutingCloudlets(mappedVm));
					System.out.println("The total waiting cloudlet mips of the mapped vm is: "
							+ DatacenterBrokerUtility.getTotalWaitingCloudletMips(mappedVm));
					System.out.println("The total executing cloudlet mips of the mapped vm is: "
							+ DatacenterBrokerUtility.getTotalExecutingCloudletMips(mappedVm));
				}
				lastVmIdDC2 = mappedVm.getId();
			}
			lastCloudletArrivalTimeDC2 = Double.parseDouble(getSimulation().clockStr());
			lastCloudletMipsListDC2.add(cloudlet.getLength());
			lastVmIdListDC2.add(lastVmIdDC2);
        }
        
         // a cloudlet will be executed in datacenter 3 if it can be executed faster in datacenter 3 than in datacenter 1/2.
        if (cloudlet.getJobId() == 3) {
			Datacenter datacenter = getDatacenterList().get(2);

			List<Vm> datacenterVmList = DatacenterBrokerUtility.getVmList(datacenter);

			/* if subsequent cloudlets arrive at the same time then make sure it doesn't get
			   mapped to the last vm used */
			if (lastCloudletArrivalTimeDC3 == Double.parseDouble(getSimulation().clockStr())) {
				// if a vm in this datacenter is free then assign the cloudlet to that vm
				System.out.println("Subsequent cloudlet arriving at the same time");

				System.out.println("The ids in the last vm id list are ");
				for (int i = 0; i < lastVmIdListDC3.size(); i++) {
					System.out.println(lastVmIdListDC3.get(i));
				}

				mappedVm = datacenterVmList.stream()
						.filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0
								&& !lastVmIdListDC3.contains(vm.getId())) 
						.findFirst()
						.orElse(Vm.NULL);
			}

			else {
				mappedVm = datacenterVmList.stream()
						.filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0) 
						.findFirst() 
						.orElse(Vm.NULL);
				lastVmIdListDC3.clear();
				// VV a subsequent cloudlet arrived at a different time so clear the cloudlet mips list
				lastCloudletMipsListDC3.clear(); 
			}

			if (mappedVm != Vm.NULL) { // if there is a free vm
				lastVmIdDC3 = mappedVm.getId();
				System.out.println("A free Vm was found");
				System.out.println("The total waiting cloudlet mips is: "
						+ DatacenterBrokerUtility.getTotalWaitingCloudletMips(mappedVm));
				System.out.println("The total executing cloudlet mips is: "
						+ DatacenterBrokerUtility.getTotalExecutingCloudletMips(mappedVm));
				System.out.println("The number of cloudlets executing is: "
						+ DatacenterBrokerUtility.getNumOfExecutingCloudlets(mappedVm));
				System.out.println("The number of waiting cloudlets is: "
						+ DatacenterBrokerUtility.getNumOfWaitingCloudlets(mappedVm));
			}

			else { // if there is no free vm

				if (lastCloudletArrivalTimeDC3 == Double.parseDouble(getSimulation().clockStr())) {

					System.out.println("No free vm was found so one with the least remaining work was chosen.");

					for (int i = 0; i < datacenterVmList.size(); i++) {
						System.out.println(
								"The total number of mips to execute for Vm #" + datacenterVmList.get(i).getId()
								+ " is: " + DatacenterBrokerUtility.getTotalCloudletMips2(
								datacenterVmList.get(i), lastVmIdListDC3, lastCloudletMipsListDC3));
					}
					mappedVm = datacenterVmList.stream()
							.min(Comparator.comparingLong(vm -> DatacenterBrokerUtility.getTotalCloudletMips2(vm,
									lastVmIdListDC3, lastCloudletMipsListDC3))) /* select the vm with the shortest total
																				 cloudlet mips */
							.orElse(Vm.NULL);
					System.out.println("The number of waiting cloudlets of the mapped vm is: "
							+ DatacenterBrokerUtility.getNumOfWaitingCloudlets(mappedVm));
					System.out.println("The number of executing cloudlets of the mapped vm is: "
							+ DatacenterBrokerUtility.getNumOfExecutingCloudlets(mappedVm));
					System.out.println("The total waiting cloudlet mips of the mapped vm is: "
							+ DatacenterBrokerUtility.getTotalWaitingCloudletMips(mappedVm));
					System.out.println("The total executing cloudlet mips of the mapped vm is: "
							+ DatacenterBrokerUtility.getTotalExecutingCloudletMips(mappedVm));

				}

				else {

					System.out.println("No free vm was found so one with the least remaining work was chosen.");

					for (int i = 0; i < datacenterVmList.size(); i++) {
						System.out.println("The total number of mips to execute for Vm #"
								+ datacenterVmList.get(i).getId() + " is: "
								+ DatacenterBrokerUtility.getTotalCloudletMips(datacenterVmList.get(i)));
					}
					mappedVm = datacenterVmList.stream()
							.min(Comparator.comparingLong(vm -> DatacenterBrokerUtility.getTotalCloudletMips(vm))) 
							.orElse(Vm.NULL);
					System.out.println("The number of waiting cloudlets of the mapped vm is: "
							+ DatacenterBrokerUtility.getNumOfWaitingCloudlets(mappedVm));
					System.out.println("The number of executing cloudlets of the mapped vm is: "
							+ DatacenterBrokerUtility.getNumOfExecutingCloudlets(mappedVm));
					System.out.println("The total waiting cloudlet mips of the mapped vm is: "
							+ DatacenterBrokerUtility.getTotalWaitingCloudletMips(mappedVm));
					System.out.println("The total executing cloudlet mips of the mapped vm is: "
							+ DatacenterBrokerUtility.getTotalExecutingCloudletMips(mappedVm));
				}
				lastVmIdDC3 = mappedVm.getId();
			}
			lastCloudletArrivalTimeDC3 = Double.parseDouble(getSimulation().clockStr());
			lastCloudletMipsListDC3.add(cloudlet.getLength());
			lastVmIdListDC3.add(lastVmIdDC3);
		}
		/* either returns a vm that's free or the vm that will execute the cloudlet the 
		fastest (has the least instructions to execute) */
		return mappedVm; 
	}
}
