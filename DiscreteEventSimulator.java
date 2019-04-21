package cs2030.simulator;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Arrays;

/**Handles the simulation with a list of Events.
 * Adding Events with their corresponding state are manipulated and reordered by time.
 * Has output method.
 */
public class DiscreteEventSimulator {
    
    PriorityQueue<Event> eventQueue = new PriorityQueue<>(new EventComparator());
    LinkedList<Server> servers = new LinkedList<>();
    private RandomGenerator rng;
    private boolean isCustomerWaiting = false;
    private int noOfCustomersServed;
    private int noLeft;
    private double probabilityOfResting;
    private double probabilityOfGreedy;

    /**Constructor creates a private RandomGenerator and queue of Events for the simulator.
     *  @param seed is the RandomGenerator seed, 
     *  @param noOfServers number of servers,
     * @param scCounters an int value representing the number of self-checkout counters, 
     *  @param noOfCustomers is the number customers,
     *  @param maxQ is the maximum size of the server queue,
     *  @param lambda is arrival rate for RandomGenerator, 
     * @param mu service rate for RandomGenerator, 
     * @param rho positive double parameter for the resting rate,
     * @param pR a double parameter for the probability of resting,
     * @param pG a double parameter for the probability of a greedy customer occurring.
    */
    public DiscreteEventSimulator(
            int seed, int noOfServers,
            int scCounters, 
            int noOfCustomers, int maxQ,
            double lambda, double mu,
            double rho, double pR,
            double pG) {

        this.rng = new RandomGenerator(seed, lambda, mu, rho);
        this.probabilityOfResting = pR;
        this.probabilityOfGreedy = pG;

        //Initializes servers
        for (int i = 1; i <= noOfServers; i++) {
            servers.add(new Server(i, maxQ, rng, "server"));
        } 

        //Initializes self-checkout counters
        for (int i = noOfServers + 1; i < noOfServers + 1 + scCounters; i++) {
            servers.add(new Server(i, maxQ, rng, "self-check"));
        }

        //Begins the events of customer arrivals at the respective time.
        double timestamp = 0;
        addCustomerArrivals(timestamp);
        for (int j = 1; j < noOfCustomers; j++) {
            timestamp += rng.genInterArrivalTime();
            addCustomerArrivals(timestamp);  
        }      
    }

    /**Add a customer arrival event by time into the event queue.
     * Will sort out if customer is greedy or normal.
     * @param arrivalTime is the arrival time of the individual customer,
     */
    private void addCustomerArrivals(double arrivalTime) {
        if (rng.genCustomerType() < probabilityOfGreedy) {
            boolean greedy = true;
            eventQueue.offer(new Event(arrivalTime, new Customer(greedy), State.ARRIVES));
        } else {
            eventQueue.offer(new Event(arrivalTime, new Customer(false), State.ARRIVES));
        }
    }

    /**handles the event and adds them to the queue in the correct position by time.
     * @param e is the event being handled.
     */
    void handleEvent(Event e) {
        switch (e.getState()) {
            case DONE:
                //checks if the server is normal and finds out if they will rest
                if (!e.server.isSelfCheck() && rng.genRandomRest() < probabilityOfResting) {
                    eventQueue.offer(new Event(e.getTime(), e.getServer())); 
                    //offer server rest event
                } else {
                    e.done();
                }
                break;
            case LEAVES:
                //increment the number of customers who left.
                noLeft++;
                break;
            case SERVED:
                e.servedToDone(); 
                eventQueue.offer(e);
                //increment the number who was served.
                noOfCustomersServed++;
                break;
            case WAITS:
                e.waitsToServed();    
                eventQueue.offer(e);
                break;
            case ARRIVES:
                //method to handle server checking.
                customerArrival(e);
                break;
            case SERVER_REST:
                e.serverRest(rng.genRestPeriod()); //gets duration of rest period.
                eventQueue.offer(e);
                break;
            case SERVER_BACK:
                e.done();
                break;
            default:
                break;
        } 
    }

    /**checks for the customer arriving, for any idle servers.
     * If nobody, checks for servers that have no waiting customers.
     * If customer is greedy, find server with min queue.
     * If still nobody, customer is set to leave.
     * @param e is the customer arrival event.
    */
    void customerArrival(Event e) {
        //Checks for any free servers
        for (Server s: servers) { 
            if (s.isFree()) {
                e.arrivesToServed(s);
                eventQueue.offer(e);
                return;
            } 
        }

        boolean greedy = e.getCustomer().isGreedy();
        if (greedy) {
            Server serverWithMinQueueSize = null;
            int minSize = 1000000000;
            for (Server s: servers) {
                if (s.canQueue() && s.queueSize() < minSize) {
                    serverWithMinQueueSize = s;
                    minSize = s.queueSize();
                }
            }

            if (serverWithMinQueueSize != null) {
                e.arrivesToWaits(serverWithMinQueueSize);
                eventQueue.offer(e);
                return;
            }
        } else {
            for (Server s: servers) {
                if (s.canQueue()) {
                    e.arrivesToWaits(s);
                    eventQueue.offer(e);
                    return;
                }
            }
        }
        //If all servers are empty and queues are full, event becomes customer leaving.
        e.transitionState(State.LEAVES);
        eventQueue.offer(e);
    }
    
    /**Produces the simulated output in the system, 
     * by polling the eventQueue and handling it.
     * The simulation ends when the queue is empty.
    */
    public void output() {
        while (eventQueue.size() != 0) {
            Event e = eventQueue.poll();    
            if (e.check()) {
                //check for cases when the event is early.
                eventQueue.offer(e);
            } else {  
                System.out.println(e);           
                //changes time or state, and then adds it into the queue.
                handleEvent(e);
            }
        }
        String avgWaitingTime = String.format("%.3f", noOfCustomersServed == 0 ? 0 
                                    : Server.waitingTime / (double) noOfCustomersServed);
        System.out.println("[" +  avgWaitingTime + " " + noOfCustomersServed + " " + noLeft + "]");
    }

}
