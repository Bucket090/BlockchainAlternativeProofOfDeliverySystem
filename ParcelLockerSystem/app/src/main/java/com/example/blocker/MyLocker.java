package com.example.blocker;

public class MyLocker {

    private String locker_Id;
    private String locker_Password;
    public int one_Time_Code;
    private boolean allowAccess;
    private boolean isLocked;
    private int parcelsReceived;

    public MyLocker(){

    }

    public String getLocker_Id() {
        return locker_Id;
    }

    public void setLocker_Id(String locker_Id) {
        this.locker_Id = locker_Id;
    }

    public String getLocker_Password() {
        return locker_Password;
    }

    public void setLocker_Password(String locker_Password) {
        this.locker_Password = locker_Password;
    }

    public int getOne_Time_Code() {
        return one_Time_Code;
    }

    public void setOne_Time_Code(int one_Time_Code) {
        this.one_Time_Code = one_Time_Code;
    }

    public boolean isAllowAccess() {
        return allowAccess;
    }

    public void setAllowAccess(boolean allowAccess) {
        this.allowAccess = allowAccess;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getParcelsReceived() {
        return parcelsReceived;
    }

    public void setParcelsReceived(int parcelsReceived) {
        this.parcelsReceived = parcelsReceived;
    }


}
