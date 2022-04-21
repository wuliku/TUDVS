package com.com.serializable;

import java.io.Serializable;

/**
 * @author admin
 * @create 2022-04-15 15:02
 * @
 */
public class Student implements Serializable {

    private int number;
    private int age;
    private String name;
    private String address;

    public Student() {

    }

    public Student(int number, int age, String name, String address) {
        this.number = number;
        this.age = age;
        this.name = name;
        this.address = address;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Student{" +
                "number=" + number +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
