package com.mdh.devtable.global.util;

public class RegularExpression {

    public static final String EMAIL = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

    public static final String PASSWORD = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$";

    public static final String PHONE_NUMBER = "^[0-9]{10,11}$";
}