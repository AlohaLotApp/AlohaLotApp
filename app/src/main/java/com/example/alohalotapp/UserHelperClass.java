package com.example.alohalotapp;

public class UserHelperClass {

    String regEmail, regPassword, regConfinrmPass;

    public UserHelperClass() {
    }

    public UserHelperClass(String regEmail, String regPassword, String regConfinrmPass) {
        this.regEmail = regEmail;
        this.regPassword = regPassword;
        this.regConfinrmPass = regConfinrmPass;
    }

    public String getRegEmail() {
        return regEmail;
    }

    public void setRegEmail(String regEmail) {
        this.regEmail = regEmail;
    }

    public String getRegPassword() {
        return regPassword;
    }

    public void setRegPassword(String regPassword) {
        this.regPassword = regPassword;
    }

    public String getRegConfinrmPass() {
        return regConfinrmPass;
    }

    public void setRegConfinrmPass(String regConfinrmPass) {
        this.regConfinrmPass = regConfinrmPass;
    }
}
