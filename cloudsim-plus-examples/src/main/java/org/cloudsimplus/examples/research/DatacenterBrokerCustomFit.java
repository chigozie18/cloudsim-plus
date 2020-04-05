package org.cloudsimplus.examples.research;


import java.util.List;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * A implementation of {@link DatacenterBroker} that uses a Custom Fit
 * mapping between submitted cloudlets and vms where cloudlets are placed into vms using a Round-Robin policy.
 * 
 *
 * @author Chigozie Asikaburu
 * @since CloudSim Plus 4.6.0
 */
public class DatacenterBrokerCustomFit extends DatacenterBrokerSimple {
    
	/**
     * Index of the last datacenter selected to find some host.
     */
	private int lastDCIndex;
	/**
     * Index of the last host selected to find some vm.
     */
	private int lastHostIndex;
	/**
     * A counter that keeps track of the number of cloudlets that have been mapped to a vm.
     */
	private int cloudletCount = 0; 

	/**
	 * Creates a DatacenterBroker object.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     */
    public DatacenterBrokerCustomFit(final CloudSim simulation) {
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
        
        if (cloudletCount < 300) {
        	List<Datacenter> datacenterList = getDatacenterList();
        	Host host = datacenterList.get(lastDCIndex).getHost(lastHostIndex);  
        	int hostListSize = datacenterList.get(lastDCIndex).getHostList().size();  // gets the number of hosts in a datacenter 
        	List<Vm> vmList = host.getVmList();
        	Vm vm = vmList.get(0);  // simply gets the only vm in a host
        	lastHostIndex = ++lastHostIndex % hostListSize;  
        	/* ^^ cycles the host in a round robin manner. The host index can never be more than the 
        	 size of all hosts in a datacenter */
        	++cloudletCount;
        	return vm;
        }
        
        if (cloudletCount == 300) {
        	++lastDCIndex;   // after 300 cloudlets move the next datacenter 
        	lastHostIndex = 0;  // resets the index of the host since we are at a new datacenter
        }
        
        if (cloudletCount >= 300 && cloudletCount < 600) {  
        	List<Datacenter> datacenterList = getDatacenterList();
        	Host host = datacenterList.get(lastDCIndex).getHost(lastHostIndex);
        	int hostListSize = datacenterList.get(lastDCIndex).getHostList().size();  
        	List<Vm> vmList = host.getVmList();
        	Vm vm = vmList.get(0);
        	lastHostIndex = ++lastHostIndex % hostListSize;  
        	++cloudletCount;
        	return vm;    	
        }
        return Vm.NULL;  // no suitable vm found 
    }  
}
