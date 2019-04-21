package cs2030.simulator;

/**Encapsulates the customer as a class with overrided toString method. */
public class Customer {
    /**noOfCustomers is a static variable that keeps track of number 
     * of customers created for the id.*/
    public static int noOfCustomers;
    public int id;
    private boolean greedy;
    
    /**A customer is created with type and is
     *given a chronological id.
     * @param greedy is a boolean value of whether it is greedy.
     */
    public Customer(boolean greedy) {
        noOfCustomers++;
        this.id = noOfCustomers;
        this.greedy = greedy;
    }

    public boolean isGreedy() {
        return greedy;
    }

    /**toString overrided to return the correct format.*/
    @Override
    public String toString() {
        return id + (greedy ? "(greedy)" : "");
    }
}