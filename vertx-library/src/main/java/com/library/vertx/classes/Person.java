package com.library.vertx.classes;

public class Person{
    private String firstname;
    private String lastname;
    private String rights;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }


    public Person(String firstname, String lastname, String rights){
        this.firstname = firstname;
        this.lastname = lastname;
        this.rights = rights;
    }

}
