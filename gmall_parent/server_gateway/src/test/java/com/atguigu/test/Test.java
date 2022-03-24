package com.atguigu.test;

/**
 * Person
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/21 18:38
 **/
public class Test {
    public static void main(String[] args) {

        Person person = new Man();

        Man man = new Man();
        System.out.println("man.a = " + man.a);
        System.out.println("person.a = " + person.a);
    }
}

class Person{
    int a=5;
    private void eat(){
        System.out.println("人吃饭");
    }
}

class Man extends Person{
    int a=6;
    private void eat(){
        System.out.println("男人吃饭");
    }
}

