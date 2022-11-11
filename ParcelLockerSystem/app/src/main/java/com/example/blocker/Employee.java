package com.example.blocker;

public class Employee extends ProfileAccount {

    protected String system_Id;
    protected String company;
    protected String position;

    public String getSystem_Id() {
        return system_Id;
    }

    public void setSystem_Id(String system_Id) {
        this.system_Id = system_Id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }

}
