package io.github.psychesworld.mappergenerator.model;

import java.util.List;

import io.github.psychesworld.mappergenerator.TV;
import io.github.psychesworld.mappergenerator.annotations.MapName;
import io.github.psychesworld.mappergenerator.annotations.MapTo;
import io.github.psychesworld.mappergenerator.entity.PersonEntity;

@MapTo(PersonEntity.class)
public class Person {
    @MapName("theName")
    private String name;
    @MapName("age")
    private int age;
    private List<Car> cars;
    private Wife wife;
    private TV tv;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public Wife getWife() {
        return wife;
    }

    public void setWife(Wife wife) {
        this.wife = wife;
    }

    public TV getTv() {
        return tv;
    }

    public void setTv(TV tv) {
        this.tv = tv;
    }
}
