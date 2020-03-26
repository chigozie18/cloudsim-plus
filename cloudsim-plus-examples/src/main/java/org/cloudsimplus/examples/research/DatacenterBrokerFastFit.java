package org.cloudsimplus.examples.research;


import java.util.Comparator;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * A implementation of {@link DatacenterBroker} that uses a Fast Fit
 * mapping between submitted cloudlets and vms, trying to place a cloudlet
 * at the vm which can execute the cloudlet the fastest. 
 * The Broker then places the submitted vms at the first datacenter found.
 * If there isn't capacity in that one, it will try the other ones.
 *
 * @author Chigozie Asikaburu
 * @since CloudSim Plus 4.6.0
 */
public class DatacenterBrokerFastFit extends DatacenterBrokerSimple {

    /**
     * Creates a DatacenterBroker object.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     */
    public DatacenterBrokerFastFit(final CloudSim simulation) {
        super(simulation);
    }
    
    /**
     * Selects the VM which can execute the Cloudlet the fastest. It does this by selecting the Vm with the most Mips. This will allow for the fastest expected completion of a   
     * Cloudlet as given by the equation below. 
     * 
     *  <p> Fastest Exepected Completion Time (Cloudlet) = Cloudlet Length / Fastest Vm Mips <p>
     * 
     * The mapper checks to make sure a Vm is suitable to execute a cloudlet. In case the algorithm can't find such a VM, it uses the default DatacenterBroker VM mapper as a fallback.
     *
     * @param cloudlet the Cloudlet to find a VM to run it
     * @return the VM selected for the Cloudlet or {@link Vm#NULL} if no suitable VM was found
     */
    @Override
    public Vm defaultVmMapper(final Cloudlet cloudlet) {
        if (cloudlet.isBoundToVm()) {
            return cloudlet.getVm();
        }

        final Vm mappedVm = getVmCreatedList()
            .stream()
            .filter(vm -> vm.getExpectedFreePesNumber() >= cloudlet.getNumberOfPes())  // checks if the Vm is suitable for the cloutlet
            .max(Comparator.comparingDouble(Vm::getMips))  // gets the Vm with the most mips
            .orElse(Vm.NULL);

        if (mappedVm == Vm.NULL) {
            LOGGER.warn("{}: {}: {} (PEs: {}) couldn't be mapped to any suitable VM.",
                getSimulation().clockStr(), getName(), cloudlet, cloudlet.getNumberOfPes());
        } else {
            LOGGER.trace("{}: {}: {} (PEs: {}) mapped to {} (available PEs: {}, tot PEs: {})",
                getSimulation().clockStr(), getName(), cloudlet, cloudlet.getNumberOfPes(), mappedVm,
                mappedVm.getExpectedFreePesNumber(), mappedVm.getFreePesNumber());
        }

        return mappedVm;
    }
    
}
