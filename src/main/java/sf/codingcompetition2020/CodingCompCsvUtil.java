package sf.codingcompetition2020;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import sf.codingcompetition2020.structures.Agent;
import sf.codingcompetition2020.structures.Claim;
import sf.codingcompetition2020.structures.Customer;
import sf.codingcompetition2020.structures.Dependent;
import sf.codingcompetition2020.structures.Vendor;

public class CodingCompCsvUtil {
	
	/* #1 
	 * readCsvFile() -- Read in a CSV File and return a list of entries in that file.
	 * @param filePath -- Path to file being read in.
	 * @param classType -- Class of entries being read in.
	 * @return -- List of entries being returned.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> readCsvFile(String filePath, Class<T> classType) {
		
		try (Scanner scanner = new Scanner(new File(filePath));) {
			scanner.nextLine();
				
				 String line = scanner.nextLine(); 
				 if(classType == Agent.class) {
					 while (scanner.hasNextLine()) {
					 String[] params = line.split(",");
					 List<Agent> output = new ArrayList<Agent>();
					 output.add(new Agent(Integer.parseInt(params[0]), params[1], params[2],
							  params[3],params[4]));
					 }
					 } 
			
				 
				 else if(classType == Claim.class) {
					 while (scanner.hasNextLine()) {
						 System.out.println(scanner.nextLine());
					 String[] params = line.split(",");
					 for(String line1: params) {
						 System.out.println(line1);
					 }
					 List<Claim> output = new ArrayList<Claim>();
					 output.add(new Claim(Integer.parseInt(params[0]),Integer.parseInt(params[1]),
							 Boolean.parseBoolean(params[2]), Integer.parseInt(params[3])));
					 return (List<T>) output;
					 }
				  }
				 
				 else if(classType == Customer.class) { 
					 while (scanner.hasNextLine()) {
					 List<Customer> output = new ArrayList<Customer>();
					 String[] params = line.split(",");
					 List<Dependent> dependentList = new ArrayList<Dependent>();
					 if(line.indexOf("[") != -1)
					 {
						 String dependentChunk = line.substring(line.indexOf("["), line.indexOf("]"));
						 String[] dependents = dependentChunk.split(",");
						 
						 for(int i = 0; i < dependents.length; i+=2) {
							 dependentList.add( new Dependent(dependents[i].substring(dependents[i].indexOf(":\"") + 3, dependents[i].lastIndexOf("\"")).replaceAll("[^a-zA-Z0-9]", ""),
									 dependents[i +1].substring(dependents[i +1].indexOf(":\"") + 3, 
											 dependents[i +1].lastIndexOf("\"")).replaceAll("[^a-zA-Z0-9]", "")));
							 }
					 }
					// get index of last dependent in params
					 int lastDependent = 8;
					 for(int i = 0; i < params.length -1; i++) {
						 if(params[i].contains("{")) lastDependent = i;
					 }
					 if(params.length == 15) {
						 output.add(new
						 Customer(Integer.parseInt(params[0]),params[1], params[2],
						 Integer.parseInt(params[3]), params[4], Integer.parseInt(params[5]),
						 Short.parseShort(params[6]), params[7], dependentList,
						 Boolean.parseBoolean(params[lastDependent]), Boolean.parseBoolean(params[lastDependent + 1]),
						 Boolean.parseBoolean(params[lastDependent + 2]), params[lastDependent + 3], Short.parseShort(params[lastDependent + 4]),
						 Integer.parseInt(params[lastDependent + 5])));
					 }
					 return (List<T>) output;
					 } 
				 }
				 
		    	
		
		    	
		       //return output;
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}
           return null;
	}



	
	/* #2
	 * getAgentCountInArea() -- Return the number of agents in a given area.
	 * @param filePath -- Path to file being read in.
	 * @param area -- The area from which the agents should be counted.
	 * @return -- The number of agents in a given area
	 */
	public int getAgentCountInArea(String filePath,String area) {
		int agentCount = 0; // Number of agents in the area
		List<Agent> agents = readCsvFile(filePath, Agent.class);
		
		// Loop through list add increment agentCount for every agent
		// that is in the specified area
		for (Agent a : agents) {
			if(a.getArea().equals(area)) {
				agentCount++; // Increment agent
			}
		}
		
		return agentCount;
	}

	
	/* #3
	 * getAgentsInAreaThatSpeakLanguage() -- Return a list of agents from a given area, that speak a certain language.
	 * @param filePath -- Path to file being read in.
	 * @param area -- The area from which the agents should be counted.
	 * @param language -- The language spoken by the agent(s).
	 * @return -- The number of agents in a given area
	 */
	public List<Agent> getAgentsInAreaThatSpeakLanguage(String filePath, String area, String language) {
		List<Agent> list = readCsvFile(filePath, Agent.class); // List of all agents
		List<Agent> agents = new ArrayList<Agent>(); // List that will contain all agents that meet the requirements
		
		// Loop through all of the agents in list and append all agents that meet the area
		// and language requirement into the list agents
		for(Agent a : list) {
			if(a.getArea().equals(area) && a.getLanguage().equals(language)) {
				agents.add(a);
			}
		}
		
		return agents;
	}
	
	
	/* #4
	 * countCustomersFromAreaThatUseAgent() -- Return the number of individuals from an area that use a certain agent.
	 * @param filePath -- Path to file being read in.
	 * @param customerArea -- The area from which the customers should be counted.
	 * @param agentFirstName -- First name of agent.
	 * @param agentLastName -- Last name of agent.
	 * @return -- The number of customers that use a certain agent in a given area.
	 */
	public short countCustomersFromAreaThatUseAgent(Map<String,String> csvFilePaths, String customerArea, String agentFirstName, String agentLastName) {
		short result = 0; // number of customers from specified area that use specified agent
		
		// Breaking the map into two separate ArrayList for iteration
		List<Agent> agents = readCsvFile(csvFilePaths.get("agentList"), Agent.class);
		List<Customer> customers = readCsvFile(csvFilePaths.get("customerList"), Customer.class);
		
		// Iterate through customers and if the customer is in the correct area
		// then iterate through agents and make sure they are the correct agent
		// if they meet the requirements increment the result
		for(Customer c : customers) {
			if(c.getArea().equals(customerArea)) {
				for (Agent a : agents) {
					if(c.getAgentId() == a.getAgentId() && a.getFirstName().equals(agentFirstName) && a.getLastName().equals(a.getLastName())) {
						result++;
					}
				}
			}
		}
		
		return result;
	}

	
	/* #5
	 * getCustomersRetainedForYearsByPlcyCostAsc() -- Return a list of customers retained for a given number of years, in ascending order of their policy cost.
	 * @param filePath -- Path to file being read in.
	 * @param yearsOfServeice -- Number of years the person has been a customer.
	 * @return -- List of customers retained for a given number of years, in ascending order of policy cost.
	 */
	public List<Customer> getCustomersRetainedForYearsByPlcyCostAsc(String customerFilePath, short yearsOfService) {
		// Obtain list of customers and initialize empty list result
		List<Customer> customers = readCsvFile(customerFilePath, Customer.class);
		List<Customer> result = new ArrayList<Customer>();
		
		// Iterate through all of the customers and if they have retained for the 
		// specified number of years add them to the result list
		for (Customer customer : customers) {
			if(customer.getYearsOfService() >= yearsOfService) {
				result.add(customer);
			}
		}
		
		Collections.sort(result); // Sort the result list
		return result; // return the result
	}

	
	/* #6
	 * getLeadsForInsurance() -- Return a list of individuals who’ve made an inquiry for a policy but have not signed up.
	 * *HINT* -- Look for customers that currently have no policies with the insurance company.
	 * @param filePath -- Path to file being read in.
	 * @return -- List of customers who’ve made an inquiry for a policy but have not signed up.
	 */
	public List<Customer> getLeadsForInsurance(String filePath) {
		// Obtain list of customers and initialize result list
		List<Customer> customers = readCsvFile(filePath, Customer.class);
		List<Customer> result = new ArrayList<Customer>();
		
		// For customer if they don;t have an auto policy, home policy, or
		// an renters policy add current customer to the result list
		for (Customer customer : customers) {
			if (!customer.isAutoPolicy() && !customer.isHomePolicy() && !customer.isRentersPolicy()) {
				result.add(customer);
			}
		}
		
		return result;
	}


	/* #7
	 * getVendorsWithGivenRatingThatAreInScope() -- Return a list of vendors within an area and include options to narrow it down by: 
			a.	Vendor rating
			b.	Whether that vendor is in scope of the insurance (if inScope == false, return all vendors in OR out of scope, if inScope == true, return ONLY vendors in scope)
	 * @param filePath -- Path to file being read in.
	 * @param area -- Area of the vendor.
	 * @param inScope -- Whether or not the vendor is in scope of the insurance.
	 * @param vendorRating -- The rating of the vendor.
	 * @return -- List of vendors within a given area, filtered by scope and vendor rating.
	 */
	public List<Vendor> getVendorsWithGivenRatingThatAreInScope(String filePath, String area, boolean inScope, int vendorRating) {
		// Obtain list of all vendors and initialize the result list
		List<Vendor> vendors = readCsvFile(filePath, Vendor.class);
		List<Vendor> result = new ArrayList<Vendor>();
		
		// For vendor in the vendors list, if the vendor is inScope and
		// the vendors rating is == to the vendorRating add current vendor
		// to the result list
		for (Vendor vendor : vendors) {
			if (vendor.isInScope() == inScope && vendor.getVendorRating() == vendorRating) {
				result.add(vendor);
			}
		}
		
		return result; //MAY NOT BE CORRECT
	}


	/* #8
	 * getUndisclosedDrivers() -- Return a list of customers between the age of 40 and 50 years (inclusive), who have:
			a.	More than X cars
			b.	less than or equal to X number of dependents.
	 * @param filePath -- Path to file being read in.
	 * @param vehiclesInsured -- The number of vehicles insured.
	 * @param dependents -- The number of dependents on the insurance policy.
	 * @return -- List of customers filtered by age, number of vehicles insured and the number of dependents.
	 */
	public List<Customer> getUndisclosedDrivers(String filePath, int vehiclesInsured, int dependents) {
		// List of all customers and initialize the empty array list which will
		// be returned
		List<Customer> customers = readCsvFile(filePath, Customer.class);
		List<Customer> result = new ArrayList<Customer>();
		
		// Loop through customers and if customer has more than X vehicles insured
		// and less than or equal to X dependents than add current customer to 
		// result list
		for (Customer c : customers) {
			if(c.getVehiclesInsured() > vehiclesInsured && c.getDependents().size() <= dependents) {
				result.add(c);
			}
		}
		
		return result;
	}	


	/* #9
	 * getAgentIdGivenRank() -- Return the agent with the given rank based on average customer satisfaction rating. 
	 * *HINT* -- Rating is calculated by taking all the agent rating by customers (1-5 scale) and dividing by the total number 
	 * of reviews for the agent.
	 * @param filePath -- Path to file being read in.
	 * @param agentRank -- The rank of the agent being requested.
	 * @return -- Agent ID of agent with the given rank.
	 */
	public int getAgentIdGivenRank(String filePath, int agentRank) {
		// List of Agents and Customers
		List<Agent> agents = readCsvFile(filePath, Agent.class);
		List<Customer> customers = readCsvFile("src/main/resources/DataFiles/customers.csv", Customer.class);
		int agentId = -1; // Current agent id to be returned if actual agent not found
		
		// Loop through agents and customers, if customers agent is the current agent
		// increment totalCustomers and add the customers rating of their agent to
		// customer ratings
		for(Agent agent : agents) {
			int totalCustomers = 0;
			int customerRatings = 0;
			
			for(Customer customer : customers) {
				if(customer.getAgentId() == agent.getAgentId()) {
					totalCustomers++;
					customerRatings += customer.getAgentRating();
				}
			}
			
			// IF customer ratings / totalCustomers == agentRank
			// set agentId == to the current agents ID and break;
			if(customerRatings / totalCustomers == agentRank) {
				agentId = agent.getAgentId();
				break;
			}
		}
		
		return agentId; // return the agentId
	}	

	
	/* #10
	 * getCustomersWithClaims() -- Return a list of customers who’ve filed a claim within the last <numberOfMonths> (inclusive). 
	 * @param filePath -- Path to file being read in.
	 * @param monthsOpen -- Number of months a policy has been open.
	 * @return -- List of customers who’ve filed a claim within the last <numberOfMonths>.
	 */
	public List<Customer> getCustomersWithClaims(Map<String,String> csvFilePaths, short monthsOpen) {
		List<Customer> customers = readCsvFile(csvFilePaths.get("customerList"), Customer.class);
		List<Claim> claims = readCsvFile(csvFilePaths.get("claimList"), Claim.class);
		List<Customer> result = new ArrayList<Customer>();
		
		// Loop through list of claims, if claim was opened within monthsOpen
		// The loop through customers and if the claim customer ID == the
		// customer customer ID add that customer to the result list.
		for (Claim claim : claims) {
			if (claim.getMonthsOpen() < monthsOpen) {
				for (Customer customer : customers) {
					if (claim.getCustomerId() == customer.getCustomerId()) {
						result.add(customer); // Add customer to result list
					}
				}
			}
		}
		
		return result;
	}	

}


