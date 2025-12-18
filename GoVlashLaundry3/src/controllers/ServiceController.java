package controllers;

import java.sql.ResultSet;
import java.util.Vector;
import models.Service;
import utils.Connect;

public class ServiceController {
    private static ServiceController instance;
    private Connect con = Connect.getInstance();

    public static ServiceController getInstance() {
        if(instance == null) instance = new ServiceController();
        return instance;
    }

    public Vector<Service> getAllServices() {
        Vector<Service> list = new Vector<>();
        ResultSet rs = con.execQuery("SELECT * FROM services");
        try {
            while(rs.next()) {
                list.add(new Service(
                    rs.getInt("service_id"), rs.getString("service_name"), 
                    rs.getInt("service_price"), rs.getInt("service_duration")
                ));
            }
        } catch(Exception e) { e.printStackTrace(); }
        return list;
    }

    public String addService(String name, int price, int duration) {
        if(name.isEmpty()) return "Name cannot be empty";
        if(price <= 0) return "Price must be > 0";
        if(duration < 1 || duration > 30) return "Duration must be 1-30 days";
        
        String query = String.format("INSERT INTO services (service_name, service_price, service_duration) VALUES ('%s', %d, %d)", name, price, duration);
        con.execUpdate(query);
        return "Service Added Successfully";
    }
    
    public String updateService(int id, String name, int price, int duration) {
        if(name.isEmpty()) return "Name cannot be empty";
        if(price <= 0) return "Price must be > 0";
        if(duration < 1 || duration > 30) return "Duration must be 1-30 days";
        
        String query = String.format("UPDATE services SET service_name='%s', service_price=%d, service_duration=%d WHERE service_id=%d", 
                name, price, duration, id);
        con.execUpdate(query);
        return "Service Updated Successfully";
    }
    
    public void deleteService(int id) {
        con.execUpdate("DELETE FROM services WHERE service_id = " + id);
    }
}