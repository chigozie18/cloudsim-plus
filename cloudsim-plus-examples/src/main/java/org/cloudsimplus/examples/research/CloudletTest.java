package org.cloudsimplus.examples.research;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
/**
 * A class for testing if parsing cloudlet parameters from a CSV file works correctly. 
 * 
 * @author Chigozie Asikaburu
 *
 */
public class CloudletTest {

	public static void main(String[] args) {
		testCloudletsFromFile();
	}

	public static void testCloudletsFromFile () {
		BufferedReader br = null;

		try {

			String line;

			br = new BufferedReader(new FileReader("Test_Data/Demos (3:6)/DatacenterCloudlets(3:6_Demo).csv"));

			int entryNum = 1;
			
			System.out.println("---------------------------------");
			System.out.println("CLOUDLET CREATION TEST");
			System.out.println("---------------------------------");

			while ((line = br.readLine()) != null) {
				
				System.out.println("---------------------------------");
				System.out.println("Entry #" + entryNum);
				System.out.println("---------------------------------");
				System.out.println(line);

				StringTokenizer stringTokenizer = new StringTokenizer(line, " |");
				
				while (stringTokenizer.hasMoreElements()) {

					int pes = Integer.parseInt(stringTokenizer.nextElement().toString());
					int length = Integer.parseInt(stringTokenizer.nextElement().toString());
					int size = Integer.parseInt(stringTokenizer.nextElement().toString());
					double submissionDelay = Double.parseDouble(stringTokenizer.nextElement().toString());
					String utilizationModel = stringTokenizer.nextElement().toString();
					double utilizationPercentage = Double.parseDouble(stringTokenizer.nextElement().toString());

					StringBuilder sb = new StringBuilder();
					sb.append("PEs: " + pes);
					sb.append("\nLength (MIPS): " + length);
					sb.append("\nSize: " + size);
					sb.append("\nSubmission Delay: " + submissionDelay);
					sb.append("\nUtilization Model: " + utilizationModel);

					if (utilizationModel.equals("dynamic")) {
						sb.append("\nUtilization Percentage: " + utilizationPercentage);
					}

					if (utilizationModel.equals("full")) {
						sb.append("\nUtilization Percentage: " + 1.0);
			
					}

					sb.append("\n*******************\n");

					System.out.println(sb.toString());
				}

				entryNum++;
			}

			System.out.println("Done");

		} catch (IOException e) {
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
	}
	
}

