package cs2030.simulator;

import java.util.Queue;
import java.util.LinkedList;

/**Server class manages the server class with it's queue, it's own timing and it's own queue.
*/
public class Server {
    public static double waitingTime;
    private boolean busy;
    private String type;
    private int index;
    private double timeNextIdle;
    private Queue<Customer> queue = new LinkedList<Customer>();
    private int maxQ;
    private RandomGenerator rng;

    /**Constructor creates server with index, max queue size and type of server.
     * @param i index of Server.
     * @param maxQ is the max size of the customer queue.
     * @param rng is the random generator of the simulator to help in generating service time.
     * @param type is the type of server it is.
    */
    public Server(int i, int maxQ, RandomGenerator rng, String type) {
        this.index = i;
        this.maxQ = maxQ;
        this.rng = rng;
        this.type = type;
    }

    void setBusy() {
        busy = true;
    }

    /**sets the arriving customer to be served by this server.
     * @param c is the customer to be served.
    */
    void setWaitingCustomer(Customer c) {
        queue.add(c);
    }
    
    /**sets the server as finishing serving the customer.*/
    void setFinish() {
        busy = false;
    }

    boolean isSelfCheck() {
        return type.equals("self-check");
    }

    Customer getWaitingCustomer() {
        return queue.poll();
    }

    /**sets customer to be served and generates the time it is finish.
     * @param time is the time the customer is served.
    */
    double serve(double time) {
        timeNextIdle = time + rng.genServiceTime();
        return timeNextIdle;
    }

    void rest(double restPeriod) {
        timeNextIdle += restPeriod;
    }

    /**checks for when the server is next idle while maintaining the waiting time,
     * by retrieving the time of the customer waiting.
     * @param time is the time the customer is waiting to be served.
     * @return timeNextIdle of the server.
    */
    double timeNextIdle(double time) {
        waitingTime += timeNextIdle - time;
        return timeNextIdle;
    }

    /**checks whether the server is free of customers.*/
    boolean isFree() {
        return !busy && queue.isEmpty();
    }

    /**checks whether the server has any waiting customers.*/
    boolean hasWaitingCustomer() {
        return !queue.isEmpty();
    }

    //checks if max queue size has been reached.
    boolean canQueue() {
        return queue.size() < maxQ;
    }

    int queueSize() {
        return queue.size();
    }

    @Override
    public String toString() {
        return type + " " + index;
    }
}