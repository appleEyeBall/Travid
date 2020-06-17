package com.example.javalessons;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Vehicle {
    String color;
    String make;
    String model;
    int year;
    double maxSpeed;
    private double hornFrequency;
    double speed;
    int gear;
    String gearStatus ="p";

    public Vehicle(String make, String model, int year){
        this.make = make;
        this.model = model;
        this.year = year;
        this.gear = 0;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getHornFrequency() {
        return hornFrequency;
    }

    public void setHornFrequency(double hornFrequency) {
        this.hornFrequency = hornFrequency;
    }
    public void move(){
        switch (this.gear){
            case 0:
                this.speed = 0.0*(maxSpeed/10);
                break;
            case 1:
                this.speed = 1.0*(maxSpeed/10);
                break;
            case 2:
                this.speed = 2.0*(maxSpeed/10);
                break;
            case 3:
                this.speed = 3.0*(maxSpeed/10);
                break;
            case 4:
                this.speed = 4.0*(maxSpeed/10);
                break;
            case 5:
                this.speed = this.maxSpeed;
                break;
            default:
                this.speed = 0.0;
                break;

        }
        System.out.println(make+" "+model+" is moving at "+ String.valueOf(this.speed)+" MpH");
    }

    public void changeGear(String gear){
        if (this.gearStatus.equals(gear)){
            move();
        }
        else if (gear.equals("p") || gear.equals("d")){
            this.gearStatus = gear;
            this.speed = 0.0;

        }
        else if (gear.equals("r")){
            this.speed = -8.0;
        }
        move();
    }

    public void changeGear(int gear){
        if (gear<0 || gear>5){
            System.out.println("Error changing gear, are you a learner?? ");
        }
        if ((gear-this.gear == 1 || gear-this.gear==-1)){
            this.gear = gear;
        }
        move();
    }

    public void honk(){
        System.out.println("honk");
    }

}

class Car extends Vehicle{

    public Car(String make, String model, int year) {
        super(make, model, year);
        this.maxSpeed = 120.0;

    }


    @Override
    public void honk() {
        System.out.println("honk honk");
    }
}

class Truck extends Vehicle{

    public Truck(String make, String model, int year) {
        super(make, model, year);
        this.maxSpeed = 80.0;
    }

}

class Motorcycle extends Vehicle{

    public Motorcycle(String make, String model, int year) {
        super(make, model, year);
        this.maxSpeed = 60.0;
    }

    @Override
    public void honk() {
        System.out.println("pim pim");
    }


}
