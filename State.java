package cs2030.simulator;

/**enumeration constants used for event states.*/
public enum State {
    ARRIVES, SERVED, WAITS, DONE, LEAVES, SERVER_REST, SERVER_BACK;
    
    public String toString() {
        return super.toString().toLowerCase();
    }
}