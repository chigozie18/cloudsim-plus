package org.cloudsimplus.examples.research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An implementation of {@link DatacenterBroker} that uses a round robin
 * mapping between submitted cloudlets and vms. It cyclically selects either 
 * datacenter 1/2 or datacenter 3.
 * 
 * <br><br>
 * It then maps an incoming cloudlet to a vm in a round robin fashion (cyclically).
 * This policy also tries to select datacenter 1/2 (to have one of its vms exectue a cloudlet) 
 * first if it's free (has at least one free vm) or selects datacenter 3 afterwards if it's free 
 * before selecting cyclically selecting a datacenter. It will then try to map a 
 * cloudlet to a free vm in that selected datacenter before cyclically finding a vm. 
 * It simply selects the first free vm available when possible (will go in order).
 *
 * @author Chigozie Asikaburu
 * @since CloudSim Plus 4.6.0
 */
public class DatacenterBrokerRoundRobinMap2 extends DatacenterBrokerSimple {

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
    List<List<Long>> lastVmIdListAllDC = new ArrayList<List<Long>>(
            Arrays.asList(lastVmIdListDC1, lastVmIdListDC2, lastVmIdListDC3));

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
     * A list used for storing the mips of subsequently arriving cloudlets. This
     * list is used in order to determine the vm to execute a cloudlet next. This
     * list is cleared when the next cloudlet arrives at a different time than a
     * subsequently arriving cloudlet (which arrived at the same time as previous
     * cloudlets).
     */
    List<Long> lastCloudletMipsListDC3 = new ArrayList<Long>(Arrays.asList((long) -2));

    /**
     * Represents a list of lists containing lists from all datacenter's
     * lastCloudletMipsList.
     */
    List<List<Long>> lastCloudletMipsListAllDC = new ArrayList<List<Long>>(
            Arrays.asList(lastCloudletMipsListDC1, lastCloudletMipsListDC2, lastCloudletMipsListDC3));

    /**
     * Keeps track of the index of the current mapped vm reterieved from an
     * arraylist in datacenter 1. The value is used to cyclically choose the next vm
     * to execute a cloudlet.
     * 
     */
    int currentVmIndexDC1 = 0;

    /**
     * A copied value gotton from currentVmIndexDC1 that is used for printing the
     * actual index (before any incrementation).
     */
    int copyCurrentVmIndexDC1 = 0;

    /**
     * Keeps track of the index of the current mapped vm reterieved from an
     * arraylist in datacenter 2. The value is used to cyclically choose the next vm
     * to execute a cloudlet.
     * 
     */
    int currentVmIndexDC2 = 0;

    /**
     * A copied value gotton from currentVmIndexDC2 that is used for printing the
     * actual index (before any incrementation).
     */
    int copyCurrentVmIndexDC2 = 0;

    /**
     * Keeps track of the index of the current mapped vm reterieved from an
     * arraylist in datacenter 3. The value is used to cyclically choose the next vm
     * to execute a cloudlet.
     * 
     */
    int currentVmIndexDC3 = 0;

    /**
     * A copied value gotton from currentVmIndexDC3 that is used for printing the
     * actual index (before any incrementation).
     */
    int copyCurrentVmIndexDC3 = 0;

    /**
     * Keeps track of the index of the current mapped vm. The list only
     * holds two values. Each value used to cyclically keep track of the index of the vm
     * to be mapped the in the two instances of round robin (datacenter 1 and 3, datacenter
     * 2 and 3). 
     */
    List<Integer> currentVmIndexListAllDC = new ArrayList<>(
            Arrays.asList(0, 0));

    /**
     * A list containing the number of vms in each datacenter. 
     */
    List<Integer> numOfVmsListAllDC;

    /**
     * Creates a DatacenterBroker object. (REMOVE THIS METHOD LATER)
     *
     * @param simulation The CloudSim instance that represents the simulation the
     *                   Entity is related to
     */
    public DatacenterBrokerRoundRobinMap2(final CloudSim simulation) {
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
	
		double simulationTime = getSimulation().clock();
		List<Datacenter> datacenterList = getDatacenterList();
        long oldCloudletJobId = cloudlet.getJobId();

        /* Initializes the numOfVmsListAllDC. This can't be done in the constructor since the 
           simulation needs to have started to place vms in the datacenters. */
        
        if (numOfVmsListAllDC == null) {  
            numOfVmsListAllDC = new ArrayList<>();
            numOfVmsListAllDC.add(DatacenterBrokerUtility.getVmList(datacenterList.get(0)).size());
            numOfVmsListAllDC.add(DatacenterBrokerUtility.getVmList(datacenterList.get(1)).size());
            numOfVmsListAllDC.add(DatacenterBrokerUtility.getVmList(datacenterList.get(2)).size());
        }
        
        /*
     	* Stores the last cloudlet's arrival time in all datacenters. This value is gotten
     	* from the current simulation time when the last cloudlet arrived.
     	*/
		List<Double> lastCloudletArrivalTimeAllDC = new ArrayList<Double>(Arrays.asList(lastCloudletArrivalTimeDC1,
		lastCloudletArrivalTimeDC2, lastCloudletArrivalTimeDC3));

        DatacenterBrokerUtility.determineIfCloudletGoesToDC3RoundRobinMap(simulationTime, cloudlet, lastCloudletArrivalTimeAllDC,
         lastVmIdListAllDC, datacenterList, currentVmIndexListAllDC, numOfVmsListAllDC);

		long newCloudletJobId = cloudlet.getJobId();

		DatacenterBrokerUtility.printCloudletJobIdMessage(oldCloudletJobId, newCloudletJobId);
		
		DatacenterBrokerUtility.printTotalCloudletMipsInAllVmsInAllDC(simulationTime,
		lastCloudletArrivalTimeAllDC, lastCloudletMipsListAllDC, lastVmIdListAllDC, datacenterList);

		DatacenterBrokerUtility.printNumOfExecutingCloudletsInAllDC(datacenterList);

		DatacenterBrokerUtility.printSelectedDC(newCloudletJobId);

        if (newCloudletJobId == 1) { // if the cloudlet's job id is 1 it will be executed in a vm in datacenter 1
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
                System.out.println("Subsequent cloudlet arriving at the same time");
                mappedVm = datacenterVmList.stream()
                        .filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0
                        && DatacenterBrokerUtility.getNumOfWaitingCloudlets(vm) == 0 && !lastVmIdListDC1.contains(vm.getId()))
                        .findFirst() // return the first free vm if it exists
                        .orElse(Vm.NULL);
            }
            else {
                mappedVm = datacenterVmList.stream()
                        .filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0 
                        && DatacenterBrokerUtility.getNumOfWaitingCloudlets(vm) == 0)
                        .findFirst()
                        .orElse(Vm.NULL);
                // a subsequent cloudlet arrived at a different time so clear the vm list
                lastVmIdListDC1.clear();
                lastCloudletMipsListDC1.clear(); 
            }
            if (mappedVm != Vm.NULL) { // if there is a free vm
                lastVmIdDC1 = mappedVm.getId(); // keep track of the id of a mapped vm
                System.out.println("A free Vm was found");
            }
            else { // if there is no free vm
                System.out.println("No free vm was found so round robin is done.");

                copyCurrentVmIndexDC1 = currentVmIndexDC1;
                mappedVm = datacenterVmList.get(currentVmIndexDC1);
                currentVmIndexDC1 = (currentVmIndexDC1 + 1)  % datacenterVmList.size(); // increment the counter if no free vm was found
                lastVmIdDC1 = mappedVm.getId();
            }

            /* the line below is just for printing to the console and makes sure that the index that's printed correspondes to the actual 
            index of an arraylist (index starts at 0 and ends at arrayListSize - 1) */
            System.out.println("The index of the current vm mapped in round robin is: " + copyCurrentVmIndexDC1);
            lastCloudletArrivalTimeDC1 = simulationTime;
            lastCloudletMipsListDC1.add(cloudlet.getLength());
            lastVmIdListDC1.add(lastVmIdDC1);
        }

        if (newCloudletJobId == 2) { // if the cloudlet's job id is 2 it will be executed in a vm in datacenter 2
            Datacenter datacenter = datacenterList.get(1);

            List<Vm> datacenterVmList = DatacenterBrokerUtility.getVmList(datacenter);

            /*
            * If subsequent cloudlets arrive at the same time then make sure it doesn't get
            * mapped to the last vm "free vm" used. This is done so we don't assign
            * subsequent cloudlets to a vm which was deemed to be "free" but is actually
            * not. The current simulation time is used to check when cloudlets arrive.
            */
            if (lastCloudletArrivalTimeDC2 == simulationTime) {
                // if a vm in this datacenter is free then assign the cloudlet to that vm
                System.out.println("Subsequent cloudlet arriving at the same time");
                mappedVm = datacenterVmList.stream()
                        .filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0
                                && DatacenterBrokerUtility.getNumOfWaitingCloudlets(vm) == 0 && !lastVmIdListDC2.contains(vm.getId()))
                        .findFirst() // return the first free vm if it exists
                        .orElse(Vm.NULL);
            }
            else {
                mappedVm = datacenterVmList.stream()
                        .filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0 
                        && DatacenterBrokerUtility.getNumOfWaitingCloudlets(vm) == 0)
                        .findFirst()
                        .orElse(Vm.NULL);
                // a subsequent cloudlet arrived at a different time so clear the vm list
                lastVmIdListDC2.clear();
                lastCloudletMipsListDC2.clear(); 
            }
            if (mappedVm != Vm.NULL) { // if there is a free vm
                lastVmIdDC2 = mappedVm.getId(); // keep track of the id of a mapped vm
                System.out.println("A free Vm was found");
            }
            else { // if there is no free vm
                System.out.println("No free vm was found so round robin is done.");

                copyCurrentVmIndexDC2 = currentVmIndexDC2;
                mappedVm = datacenterVmList.get(currentVmIndexDC2);
                currentVmIndexDC2 = (currentVmIndexDC2 + 1)  % datacenterVmList.size(); // increment the counter if no free vm was found
                lastVmIdDC2 = mappedVm.getId();
            }
            /* the line below is just for printing to the console and makes sure that the index that's printed correspondes to the actual 
            index of an arraylist (index starts at 0 and ends at arrayListSize - 1) */
            System.out.println("The index of the current vm mapped in round robin is: " + copyCurrentVmIndexDC2);
            lastCloudletArrivalTimeDC2 = simulationTime;
            lastCloudletMipsListDC2.add(cloudlet.getLength());
            lastVmIdListDC2.add(lastVmIdDC2);
		}
		
		if (newCloudletJobId == 3) { // if the cloudlet's job id is 3 it will be executed in a vm in datacenter 3
            Datacenter datacenter = datacenterList.get(2);

            List<Vm> datacenterVmList = DatacenterBrokerUtility.getVmList(datacenter);

            /*
            * If subsequent cloudlets arrive at the same time then make sure it doesn't get
            * mapped to the last vm "free vm" used. This is done so we don't assign
            * subsequent cloudlets to a vm which was deemed to be "free" but is actually
            * not. The current simulation time is used to check when cloudlets arrive.
            */
            if (lastCloudletArrivalTimeDC3 == simulationTime) {
                // if a vm in this datacenter is free then assign the cloudlet to that vm
                System.out.println("Subsequent cloudlet arriving at the same time");
                mappedVm = datacenterVmList.stream()
                        .filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0
                                && DatacenterBrokerUtility.getNumOfWaitingCloudlets(vm) == 0 && !lastVmIdListDC3.contains(vm.getId()))
                        .findFirst() // return the first free vm if it exists
                        .orElse(Vm.NULL);
            }
            else {
                mappedVm = datacenterVmList.stream()
                        .filter(vm -> DatacenterBrokerUtility.getNumOfExecutingCloudlets(vm) == 0 
                        && DatacenterBrokerUtility.getNumOfWaitingCloudlets(vm) == 0)
                        .findFirst()
                        .orElse(Vm.NULL);
                // a subsequent cloudlet arrived at a different time so clear the vm list
                lastVmIdListDC3.clear();
                lastCloudletMipsListDC3.clear(); 
            }
            if (mappedVm != Vm.NULL) { // if there is a free vm
                lastVmIdDC3 = mappedVm.getId(); // keep track of the id of a mapped vm
                System.out.println("A free Vm was found");
            }
            else { // if there is no free vm
                System.out.println("No free vm was found so round robin is done.");

                copyCurrentVmIndexDC3 = currentVmIndexDC3;
                mappedVm = datacenterVmList.get(currentVmIndexDC3);
                currentVmIndexDC3 = (currentVmIndexDC3 + 1)  % datacenterVmList.size(); // increment the counter if no free vm was found
                lastVmIdDC3 = mappedVm.getId();
            }
            /* the line below is just for printing to the console and makes sure that the index that's printed correspondes to the actual 
            index of an arraylist (index starts at 0 and ends at arrayListSize - 1) */
            System.out.println("The index of the current vm mapped in round robin is: " + copyCurrentVmIndexDC3);
            lastCloudletArrivalTimeDC3 = simulationTime;
            lastCloudletMipsListDC3.add(cloudlet.getLength());
            lastVmIdListDC3.add(lastVmIdDC3);
        }

        /*
         * either returns a vm that's free or the vm that was chosen by round robin
         * scheduling
         */
        return mappedVm;
    }
}
