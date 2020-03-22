package org.cloudsimplus.examples.research;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

/**
 * 
 * A class containing methods for creating datacenters, hosts, vms and cloudlets from user-defined parameters or from a CSV file
 * 
 * @author chigozieasikaburu
 *
 */
public class CloudCreator {

	/**
	 * Creates a datacenter. 
	 * 
	 * 
	 * @param simulation the simulation entity that the datacenter is associated with 
	 * @param hostList the list of hosts to be created in the datacenter
	 * 
	 * @return a datacenter 
	 */
	public static Datacenter createDatacenter(CloudSim simulation, List<Host> hostList) {  
		//Uses a VmAllocationPolicySimple by default to allocate VMs
		return new DatacenterSimple(simulation, hostList);  // the simulation paramter represents the instance of the simulation the datacenter (entity) is related to
	}

	/**
	 * Creates a datacenter. This method also defines datacenter characteristics relating to cost. 
	 * 
	 * <p>For example, adding a costperSecond of .5 means it cost 50 cents per second to use the CPU in a datacenter. 
	 * 
	 * @param simulation the simulation entity that the datacenter is associated with 
	 * @param hostList the list of hosts to be created in the datacenter
	 * @param costPerSecond the monetary cost (in $) per second of CPU use in the datacenter
	 * @param costPerMem the monetary cost (in $) to use each megabyte of RAM in the datacenter
	 * @param costPerStorage the monetary cost (in $) to use each megabyte of storage in the datacenter
	 * @param costPerBw the monetary cost (in $) to use each megabit of bandwidth in the datacenter
	 * 
	 * @return a datacenter 
	 */
	public static Datacenter createDatacenter(CloudSim simulation, List<Host> hostList, double costPerSecond, double costPerMem, 
			double costPerStorage, double costPerBw) {  
		//Uses a VmAllocationPolicySimple by default to allocate VMs
		Datacenter datacenter = new DatacenterSimple(simulation, hostList);
		datacenter.getCharacteristics()
		.setCostPerSecond(costPerSecond)
		.setCostPerMem(costPerMem)
		.setCostPerStorage(costPerStorage)
		.setCostPerBw(costPerBw);

		return datacenter;  
	}

	
	/**
	 * Creates a single host. All processor entities (PEs) of the host will have the same mips. 
	 * 
	 * @param pesNum the number of PEs to be created for the host
	 * @param pesMips the mips of all PEs to be created for the host
	 * @param ram the ram of the host (megabytes)
	 * @param bw the bandwidth of the host (megabits/s)
	 * @param storage the storage of the host (megabytes)
	 * 
	 * @return a host
	 */
	public static Host createHost(int pesNum, double pesMips, long ram, long bw, long storage) {
		
		final List<Pe> peList = new ArrayList<>(pesNum);  //List of Host's CPUs (Processing Elements, PEs)

		for (int i = 0; i < pesNum; i++) {
			//Uses a PeProvisionerSimple by default to provision PEs for VMs
			peList.add(new PeSimple(pesMips));
		}

		Host host = new HostSimple(ram, bw, storage, peList);
		return host;
	}
	

	/**
	 * Creates a single host. All processor entities (PEs) of the host will have the same mips. 
	 * 
	 * @param pesNum the number of PEs to be created for the host
	 * @param pesMips the mips of all PEs to be created for the host
	 * @param ram the ram of the host (megabytes)
	 * @param bw the bandwidth of the host (megabits/s)
	 * @param storage the storage of the host (megabytes)
	 * @param vmScheduler the vm scheduler for the host
	 * 
	 * @return a host
	 */
	public static Host createHost(int pesNum, double pesMips, long ram, long bw, long storage, String vmScheduler) {

		final List<Pe> peList = new ArrayList<>(pesNum);  //List of Host's CPUs (Processing Elements, PEs)

		for (int i = 0; i < pesNum; i++) {
			//Uses a PeProvisionerSimple by default to provision PEs for VMs
			peList.add(new PeSimple(pesMips));
		}

		VmScheduler scheduler = getVmScheduler(vmScheduler);

		Host host = new HostSimple(ram, bw, storage, peList);
		host.setVmScheduler(scheduler);

		return host;
	}


	/**
	 * Creates a list of hosts with the same attributes. All processor entities (PEs) of each host will have the same mips.
	 * 
	 * @param hostNum the number of hosts to be created in the datacenter
	 * @param pesNum the number of PEs to be created for each host
	 * @param pesMips the mips of all PEs to be created for each host
	 * @param ram the ram of the each host (megabytes)
	 * @param bw the bandwidth of each host (megabits/s)
	 * @param storage the storage of each host (megabytes)
	 * 
	 * @return a list of hosts
	 */
	public static List<Host> createHosts(int hostNum, int pesNum, double pesMips, long ram, long bw, long storage) {

		final List<Host> list = new ArrayList<>(hostNum);

		for (int i = 0; i < hostNum; i++) {

			final List<Pe> peList = new ArrayList<>(pesNum);  //List of Host's CPUs (Processing Elements, PEs)

			for (int j = 0; j < pesNum; j++) {
				//Uses a PeProvisionerSimple by default to provision PEs for VMs
				peList.add(new PeSimple(pesMips));
			}

			Host host = new HostSimple(ram, bw, storage, peList);
			list.add(host);
		}

		return list;
	}


	/**
	 * Creates a list of hosts with the same attributes. All processor entities (PEs) of each host will have the same mips.
	 * 
	 * @param hostNum the number of hosts to be created in the datacenter
	 * @param pesNum the number of PEs to be created for each host
	 * @param pesMips the mips of all PEs to be created for each host
	 * @param ram the ram of the each host (megabytes)
	 * @param bw the bandwidth of each host (megabits/s)
	 * @param storage the storage of each host (megabytes)
	 * @param vmScheduler the vm scheduler for each host
	 * 
	 * @return a list of hosts
	 */
	public static List<Host> createHosts(int hostNum, int pesNum, double pesMips, long ram, long bw, long storage, String vmScheduler) {

		final List<Host> list = new ArrayList<>(hostNum);

		for (int i = 0; i < hostNum; i++) {

			final List<Pe> peList = new ArrayList<>(pesNum);  //List of Host's CPUs (Processing Elements, PEs)

			for (int j = 0; j < pesNum; j++) {
				//Uses a PeProvisionerSimple by default to provision PEs for VMs
				peList.add(new PeSimple(pesMips));
			}

			VmScheduler scheduler = getVmScheduler(vmScheduler);

			Host host = new HostSimple(ram, bw, storage, peList);
			host.setVmScheduler(scheduler);
			list.add(host);
		}

		return list;
	}


	/**
	 * Creates a list of hosts from a file.
	 * 
	 * @param filePath the path that contains the list of hosts in a CSV file format (pes|pesMips|ram|bw|storage|vmScheduler)
	 * 
	 * @return a list of hosts
	 */
	public static List<Host> createHostsFromFile(String filePath) {

		BufferedReader br = null;
		final List<Host> hostList = new ArrayList<>();

		try {
			String line;
			br = new BufferedReader(new FileReader(filePath));

			while ((line = br.readLine()) != null) {

				StringTokenizer stringTokenizer = new StringTokenizer(line, "|");

				while (stringTokenizer.hasMoreElements()) {

					int pes = Integer.parseInt(stringTokenizer.nextElement().toString());
					int pesMips = Integer.parseInt(stringTokenizer.nextElement().toString());
					int ram = Integer.parseInt(stringTokenizer.nextElement().toString());
					int bw = Integer.parseInt(stringTokenizer.nextElement().toString());
					int storage = Integer.parseInt(stringTokenizer.nextElement().toString());
					String vmScheduler = stringTokenizer.nextElement().toString();

					final List<Pe> peList = new ArrayList<>(pes);  // list of Host's CPUs (Processing Elements, PEs)

					for (int i = 0; i < pes; i++) {
						//Uses a PeProvisionerSimple by default to provision PEs for VMs
						peList.add(new PeSimple(pesMips, new PeProvisionerSimple()));   // creates all the PEs for one host
					}

					ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
					ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
					VmScheduler scheduler = getVmScheduler(vmScheduler);

					Host host = new HostSimple(ram, bw, storage, peList);
					host.setRamProvisioner(ramProvisioner)
					.setBwProvisioner(bwProvisioner).setVmScheduler(scheduler);
					hostList.add(host);    	        
				}  
			}
		} 

		catch (IOException e) {
			e.printStackTrace();
		} 

		finally {

			try {
				if (br != null)
					br.close();
			} 
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return hostList;
	}


	/**
	 * 
	 * Creates a list of vms with the same attributes.
	 * 
	 * @param vmNum the number of vms to be created
	 * @param pesNum the number of PEs required and used by each vm
	 * @param mips the mips of the each vm
	 * @param ram the ram of each vm
	 * @param bw the bandwidth of each vm
	 * @param storage the storage of each vm
	 * 
	 * @return a list of vms
	 *
	 */
	public static List<Vm> createVms(int vmNum, int pesNum, double mips, int ram, int bw, int storage) {

		final List<Vm> list = new ArrayList<>(vmNum);

		for (int i = 0; i < vmNum; i++) {
			final Vm vm = new VmSimple(mips, pesNum);
			vm.setRam(ram).setBw(bw).setSize(storage);  
			list.add(vm);
		}

		return list;
	}

	/**
	 * 
	 * Creates a list of vms with the same attributes.
	 * 
	 * @param vmNum the number of vms to be created
	 * @param pesNum the number of PEs required and used by each vm
	 * @param mips the mips of the each vm
	 * @param ram the ram of each vm
	 * @param bw the bandwidth of each vm
	 * @param storage the storage of each vm
	 * @param cloudletScheduler the cloudlet scheduler for each host
	 * 
	 * @return a list of vms
	 *
	 */
	public static List<Vm> createVms(int vmNum, int pesNum, double mips, int ram, int bw, int storage, String cloudletScheduler) {

		final List<Vm> list = new ArrayList<>(vmNum);

		CloudletScheduler scheduler = getCloudletScheduler(cloudletScheduler);

		for (int i = 0; i < vmNum; i++) {
			final Vm vm = new VmSimple(mips, pesNum);
			vm.setRam(ram).setBw(bw).setSize(storage).setCloudletScheduler(scheduler);  
			list.add(vm);
		}

		return list;
	}  


	/**
	 * Creates a list of vms from a CSV file.
	 * 
	 * @param filePath the path that contains the list of vms in a CSV file format (pes|pesMips|ram|bw|storage|cloudletScheduler)
	 * 
	 * @return a list of hosts
	 */
	public static List<Vm> createVmsFromFile(String filePath) {

		BufferedReader br = null;
		final List<Vm> vmList = new ArrayList<>();

		try {
			String line;
			br = new BufferedReader(new FileReader(filePath));

			while ((line = br.readLine()) != null) {

				StringTokenizer stringTokenizer = new StringTokenizer(line, "|");

				while (stringTokenizer.hasMoreElements()) {

					int pes = Integer.parseInt(stringTokenizer.nextElement().toString());
					int mips = Integer.parseInt(stringTokenizer.nextElement().toString());
					int ram = Integer.parseInt(stringTokenizer.nextElement().toString());
					int bw = Integer.parseInt(stringTokenizer.nextElement().toString());
					int storage = Integer.parseInt(stringTokenizer.nextElement().toString());
					String cloudletScheduler = stringTokenizer.nextElement().toString();

					CloudletScheduler scheduler = getCloudletScheduler(cloudletScheduler);

					final Vm vm = new VmSimple(mips, pes);
					vm.setRam(ram).setBw(bw).setSize(storage).setCloudletScheduler(scheduler); // Makes all VMs 
					vmList.add(vm);	            
				}  
			}
		} 

		catch (IOException e) {
			e.printStackTrace();
		} 

		finally {

			try {
				if (br != null)
					br.close();
			} 
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return vmList;
	}

	/**
	 * Creates a list of cloudlets with the same attributes.
	 * 
	 * @param cloudletNum the number of cloudlets to be created
	 * @param pes the number of processor entities required by each cloudlet
	 * @param length the length or size (in MI) of each cloudlet 
	 * @param size the input and output size of the cloudlet in bytes 
	 * @param utlizationModel the utilization model to be used by each cloudlet
	 */
	public static List<Cloudlet> createCloudlets(int cloudletNum, int pes, int length, int size, UtilizationModel utilizationModel) {
		final List<Cloudlet> list = new ArrayList<>(cloudletNum);

		for (int i = 0; i < cloudletNum; i++) {
			final Cloudlet cloudlet = new CloudletSimple(length, pes, utilizationModel);
			cloudlet.setSizes(size);
			list.add(cloudlet);
		}

		return list;

	}

	/**
	 * Creates a list of cloudlets from a CSV file. 
	 * 
	 * @param filePath the file path to be read
	 * @return
	 */
	public static List<Cloudlet> createCloudletsFromFile(String filePath) {

		BufferedReader br = null;
		final List<Cloudlet> cloudletList = new ArrayList<>();

		try {
			String line;
			br = new BufferedReader(new FileReader(filePath));

			while ((line = br.readLine()) != null) {

				StringTokenizer stringTokenizer = new StringTokenizer(line, "|");

				while (stringTokenizer.hasMoreElements()) {

					int pes = Integer.parseInt(stringTokenizer.nextElement().toString());
					int length = Integer.parseInt(stringTokenizer.nextElement().toString());
					int size = Integer.parseInt(stringTokenizer.nextElement().toString());
					double submissionDelay = Double.parseDouble(stringTokenizer.nextElement().toString());
					String utilizationModel = stringTokenizer.nextElement().toString();
					double utilizationPercentage = Double.parseDouble(stringTokenizer.nextElement().toString());

					UtilizationModel model = getUtilizationModel(utilizationModel, utilizationPercentage);

					Cloudlet cloudlet = new CloudletSimple(length, pes, model);
					cloudlet.setSizes(size);
					cloudlet.setSubmissionDelay(submissionDelay);
					cloudletList.add(cloudlet);				
				}  
			}
		} 

		catch (IOException e) {
			e.printStackTrace();
		} 

		finally {

			try {
				if (br != null)
					br.close();
			} 
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return cloudletList;
	}

	/**
	 * Takes a cloudlet scheduler type (either "time-shared" or "space-shared") as a string and returns the appropriate cloudlet scheduler object.
	 * 
	 * @param cloudletScheduler the type of cloudlet scheduler to be created 
	 * @return a cloudlet scheduler
	 */
	public static CloudletScheduler getCloudletScheduler(String cloudletScheduler) {

		CloudletScheduler scheduler = null;
		if (cloudletScheduler.equals("time-shared")) {
			scheduler = new CloudletSchedulerTimeShared();      
		}

		if (cloudletScheduler.equals("space-shared")) {
			scheduler = new CloudletSchedulerSpaceShared();    
		}

		return scheduler;
	}


	/**
	 * Takes a vm scheduler type (either "time-shared" or "space-shared") as a string and returns the appropriate vm scheduler object.
	 * 
	 * @param vmScheduler the type of vm scheduler to be created
	 * @return a vm scheduler
	 */
	public static VmScheduler getVmScheduler(String vmScheduler) {

		VmScheduler scheduler = null;
		if (vmScheduler.equals("time-shared")) {
			scheduler = new VmSchedulerTimeShared();      
		}

		if (vmScheduler.equals("space-shared")) {
			scheduler = new VmSchedulerSpaceShared();     
		}

		return scheduler;
	}


	/**
	 * Takes a utilization model type (either "dynamic" or "full") as a string and returns the appropriate utilization model object.
	 * 
	 * @param utilizationModel the type of utilization model to be created 
	 * @return a cloudlet scheduler 
	 */
	public static UtilizationModel getUtilizationModel(String utilizationModel, double utilizationPercentage) {

		UtilizationModel model = null;
		if (utilizationModel.equals("dynamic")) {
			model = new UtilizationModelDynamic(utilizationPercentage);      
		}

		if (utilizationModel.equals("full")) {
			model = new UtilizationModelFull();  // utilization percentage not needed here (always at 100% utilization)    
		}

		return model;
	}

}
