package com.rykj.hmccp.rybarcode;

import java.util.List;

class Model_AreaInfo {
    private String ID;
    private String Area;
    private String CreateTime;
    private String ModifyTime;
    private List<Model_BarCodeInfo> list;

    public Model_AreaInfo(String area) {
        String id = AU.CAS();
        String time = AU.getCurTime();
        setAddressInfo(id, area, time, time);
    }

    public Model_AreaInfo(String id, String area, String createTime, String modifyTime) {
        setAddressInfo(id, area, createTime, modifyTime);
    }

    private void setAddressInfo(String id, String area, String createTime, String modifyTime) {
        ID = id;
        Area = area;
        CreateTime = createTime;
        ModifyTime = modifyTime;
        list = null;
    }

    public String getID() {
        return ID;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
        updateModifyTime();
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public String getModifyTime() {
        return ModifyTime;
    }

    public void updateModifyTime() {
        ModifyTime = AU.getCurTime();
    }

    public List<Model_BarCodeInfo> getList() {
        return list;
    }

    public void setList(List<Model_BarCodeInfo> list) {
        this.list = list;
    }
}

class Model_BarCodeInfo {
    private String ID;
    private String Room;
    private String Meter;
    private String Valve;

    public Model_BarCodeInfo(String room) {
        init(room, "", "");
    }

    public Model_BarCodeInfo(String room, String meter, String valve) {
        init(room, meter, valve);
    }

    private void init(String room, String meter, String valve) {
        ID = AU.CAS();
        Room = room;
        Meter = meter;
        Valve = valve;
    }

    public String getID() {
        return ID;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getMeter() {
        return Meter;
    }

    public void setMeter(String meter) {
        Meter = meter;
    }

    public String getValve() {
        return Valve;
    }

    public void setValve(String valve) {
        Valve = valve;
    }
}