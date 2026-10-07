/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 *
 * @author hoanh
 */
public class Appliance {
    
    private int appliance_id;
    private String appliance_name;
    private int household_id;
    private float rated_w;
    private String room;
    private Timestamp createdAt;

    public Appliance() {
    }

    public Appliance(int appliance_id, String appliance_name, int household_id, float rated_w, String room, Timestamp createdAt) {
        this.appliance_id = appliance_id;
        this.appliance_name = appliance_name;
        this.household_id = household_id;
        this.rated_w = rated_w;
        this.room = room;
        this.createdAt = createdAt;
    }

    public int getAppliance_id() {
        return appliance_id;
    }

    public void setAppliance_id(int appliance_id) {
        this.appliance_id = appliance_id;
    }

    public String getAppliance_name() {
        return appliance_name;
    }

    public void setAppliance_name(String appliance_name) {
        this.appliance_name = appliance_name;
    }

    public int getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(int household_id) {
        this.household_id = household_id;
    }

    public float getRated_w() {
        return rated_w;
    }

    public void setRated_w(float rated_w) {
        this.rated_w = rated_w;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    
}