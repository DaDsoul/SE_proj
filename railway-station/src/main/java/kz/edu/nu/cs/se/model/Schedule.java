package kz.edu.nu.cs.se.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Schedule {
    private Integer id;
    private ArrayList<Route> routes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Train train;

    public Schedule(Integer id) {
        this.setId(id);
        this.setTrain(train);
        this.setRoutes(new ArrayList<>());
    }

    public ArrayList<Route> getRoutes() { return routes; }

    public void AddRoute(Route route) {
        getRoutes().add(route);
        if (this.getStartTime() == null) this.setStartTime(route.getStartDate());
        if (this.getEndTime() == null) this.setEndTime(route.getEndDate());

        if (this.getStartTime().compareTo(route.getStartDate()) > 0) {
            setStartTime(route.getStartDate());
        }

        if (this.getEndTime().compareTo(route.getEndDate()) < 0) {
            setEndTime(route.getEndDate());
        }
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public void sortRoutes() {
        Collections.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
    }


}
