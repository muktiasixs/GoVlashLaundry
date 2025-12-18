package models;

public class Service {
    private int serviceId;
    private String serviceName;
    private int servicePrice;
    private int serviceDuration;

    public Service(int serviceId, String serviceName, int servicePrice, int serviceDuration) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.serviceDuration = serviceDuration;
    }
    
    // Getter methods
    public int getServiceId() { return serviceId; }
    public String getServiceName() { return serviceName; }
    public int getServicePrice() { return servicePrice; }
    public int getServiceDuration() { return serviceDuration; }
}