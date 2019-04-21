package cs2030.simulator;

import java.util.Comparator;

class EventComparator implements Comparator<Event> {
    public int compare(Event e1, Event e2) {
        if (e1.getTime() == e2.getTime()) {
            if (!e1.isServerEvent() && !e2.isServerEvent()) {
                return e1.getCustomer().id - e2.getCustomer().id;
            } 
            if (e1.isServerEvent()) {
                return -1;
            }
            if (e2.isServerEvent()) {
                return 1;
            }
        }
        return Double.compare(e1.getTime(), e2.getTime());  
    }
}