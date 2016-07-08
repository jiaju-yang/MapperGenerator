package io.github.psychesworld.mappergenerator.entity;


import java.util.List;

import io.github.psychesworld.mappergenerator.TV;

public class PersonEntity {
    private String theName;
    private int age;
    private List<CarEntity> cars;
    private WifeEntity wife;
    private TV tv;

    public TV getTv() {
        return tv;
    }

    public void setTv(TV tv) {
        this.tv = tv;
    }

    public String getTheName() {
        return theName;
    }

    public void setTheName(String theName) {
        this.theName = theName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<CarEntity> getCars() {
        return cars;
    }

    public void setCars(List<CarEntity> cars) {
        this.cars = cars;
    }

    public WifeEntity getWife() {
        return wife;
    }

    public void setWife(WifeEntity wife) {
        this.wife = wife;
    }
}
