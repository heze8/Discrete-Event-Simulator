import java.util.Scanner;
import cs2030.simulator.DiscreteEventSimulator;

/**Handles the input while creating the DiscreteEventSimulator class to handle the simulation.*/
public class Main {
    
    /**Main method handles input of client to create a Discrete Event Simulator object. 
     * Outputs the simulation given by the variables.
    */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int seed = sc.nextInt(); 
        //an int value denoting the base seed for the RandomGenerator object
        int noOfServers = sc.nextInt(); 
        //an int value representing the number of servers,
        int scCounters = sc.nextInt(); 
        //an int value representing the number of self-checkout counters, Nself
        int maxQ = sc.nextInt(); 
        //an int value for the maximum queue length, Qmax
        int noOfCustomers = sc.nextInt();
        //an int representing the number of customers (or the number of arrival events) to simulate
        double lambda = sc.nextDouble();
        //a positive double parameter for the arrival rate, λ
        double mu = sc.nextDouble();
        //a positive double parameter for the service rate, μ
        double rho = sc.nextDouble(); 
        //a positive double parameter for the resting rate
        double pr = sc.nextDouble();
        //a double parameter for the probability of resting, Pr
        double pg = sc.nextDouble();
        //a double parameter for the probability of a greedy customer occurring, Pg

        DiscreteEventSimulator dec = 
            new DiscreteEventSimulator(
                seed, noOfServers, scCounters, noOfCustomers, maxQ, lambda, mu, rho, pr, pg);
            
        dec.output();
    }
}