package com.epam.edp.demo.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String regex =  "^(?!\\d+$)[A-Za-z\\d+_.-]+@[A-Za-z.-]+\\.[A-Za-z]{2,6}$";
    ;
    private static final Pattern pattern = Pattern.compile(regex);
    public static boolean validateEmail(String email){
        if (email == null){
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}