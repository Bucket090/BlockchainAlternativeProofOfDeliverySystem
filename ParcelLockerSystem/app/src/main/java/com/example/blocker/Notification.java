package com.example.blocker;

public class Notification {

    private String delivery_id;
    private String delivery_device;
    private String delivery_msg;
    private String delivery_timestamp;

    public String getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }

    public String getDelivery_device() {
        return delivery_device;
    }

    public void setDelivery_device(String delivery_device) {
        this.delivery_device = delivery_device;
    }

    public String getDelivery_msg() {
        return delivery_msg;
    }

    public void setDelivery_msg(String delivery_msg) {
        this.delivery_msg = delivery_msg;
    }

    public String getDelivery_timestamp() {
        return delivery_timestamp;
    }

    public void setDelivery_timestamp(String delivery_timestamp) {
        this.delivery_timestamp = delivery_timestamp;
    }

    public Notification(String delivery_id,String delivery_device, String delivery_msg, String delivery_timestamp) {
        this.delivery_id = delivery_id;
        this.delivery_device = delivery_device;
        this.delivery_msg = delivery_msg;
        this.delivery_timestamp = delivery_timestamp;
    }
}
