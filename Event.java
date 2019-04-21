package cs2030.simulator;

/**Encapsulates the events in the simulator. 
 * It handles logic with the variables, server, customer and time.
 */
public class Event {

    public Server server;
    private double time;
    private State state;
    private Customer id;
    private int eventTrack;
    
    /** Constructor for SERVER_REST and SERVER_LEFT events.*/
    public Event(double time, Server s) {
        this.time = time;
        this.state = State.SERVER_REST;
        this.server = s;
    }

    /** Overloaded constructor for ARRIVE events. */
    public Event(double time, Customer c, State state) { 
        this.time = time;
        this.id = c;
        this.state = state;
        this.eventTrack = 0;
    }

    /**Overloaded constructor for events with servers. 
     * @param time is the time the event is set to occur.
     * @param c is the customer in the event.
     * @param state is the type of event occuring.
     * @param s is the server handling the customer.
     */
    public Event(double time, Customer c, State state, Server s) { 
        this.time = time;
        this.id = c;
        this.state = state;
        this.eventTrack = 1;
        this.server = s;
    }

    public State getState() {
        return state;
    }

    public boolean isServerEvent() {
        return state == State.SERVER_REST || state == State.SERVER_BACK;
    }

    /**Changes the state of the event.
     * Will change the event track of the event accordingly to,
     * understand what path of events will occur.
     */
    public void transitionState(State state) {
        if (state == State.WAITS) {
            eventTrack = 1;
        }
        if (state == State.LEAVES) {
            eventTrack = 2;
        }

        this.state = state;
    }
    
    public double getTime() {
        return time;
    }

    public Customer getCustomer() {
        return id;
    }

    public Server getServer() {
        return server;
    }     

    void arrivesToServed(Server s) {
        this.server = s;
        s.setBusy();
        transitionState(State.SERVED);
    }

    void arrivesToWaits(Server s) {
        this.server = s;
        s.setWaitingCustomer(id);
        transitionState(State.WAITS);
    }

    void servedToDone() {
        transitionState(State.DONE);
        time = server.serve(time);
        server.setBusy();
        if (eventTrack == 1) {
            server.getWaitingCustomer();    
        }       
    }

    void waitsToServed() {  
        transitionState(State.SERVED);
        time = server.timeNextIdle(time);
    }

    void serverRest(double restPeriod) {
        state = State.SERVER_BACK;
        server.rest(restPeriod);
        time += restPeriod;
    }

    void done() {
        server.setFinish();
    }
    
    //to check if the event is early and delays the event accordingly.
    boolean check() {
        if (state == State.SERVED && eventTrack == 1) {
            double timeNextIdle = server.timeNextIdle(time);
            if (time == timeNextIdle) {
                return false;
            } else {
                time = timeNextIdle;
                return true;
            }
        }
        return false;
    }

    //helper method for the toString method.
    private String state() {
        switch (state) {
            case ARRIVES:
                return id + " " + state.toString();
            case SERVED:
                return id + " served by " + server;
            case WAITS:
                return id + " waits to be served by " + server;
            case DONE:
                return id + " done serving by " + server;
            case LEAVES:
                return id + " " + state.toString();
            case SERVER_REST:
                return server + " rest";
            case SERVER_BACK:
                return server + " back";
            default:
                return "";            
        }
    }   

    /**toString overrided to return the correct format.*/
    @Override
    public String toString() {
        return String.format("%.3f ", time) + state();
    }
}