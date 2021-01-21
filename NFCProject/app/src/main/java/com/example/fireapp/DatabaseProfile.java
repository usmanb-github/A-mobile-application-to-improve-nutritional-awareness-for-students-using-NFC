package com.example.fireapp;

public class DatabaseProfile {

    public String name, surname, emailAddress, gender, age;

    public DatabaseProfile() {

    }

    public DatabaseProfile(String name,String surname, String emailAddress, String gender, String age) {
        this.age = age;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.emailAddress = emailAddress;
    }

    public String getAge() {
        return age;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
