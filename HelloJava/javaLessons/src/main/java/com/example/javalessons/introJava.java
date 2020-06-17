package com.example.javalessons;

public class introJava {

    public static void main(String[] args) {
        Car car = new Car("Honda", "civic", 2012);
        Motorcycle bajaj = new Motorcycle("Jincheng", "fire", 2014);
        Truck ford = new Truck("Ford", "F150", 2010);

        car.changeGear("d");
        car.changeGear(1);
        car.changeGear(2);
        car.changeGear(3);
        car.changeGear(4);
        car.changeGear(5);
        car.honk();
        car.changeGear(6);
        car.changeGear("p");

        System.out.println("\n");

        ford.changeGear("d");
        ford.honk();
        ford.changeGear(1);
        ford.changeGear(2);
        ford.changeGear(3);
        ford.changeGear(4);
        ford.changeGear(5);
    }
}