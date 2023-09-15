package com.mdh.devtable.global.util;

public class RegularExpression {

    public static final String EMAIL = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

    public static final String PASSWORD = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$";

    public static final String PHONE_NUMBER = "^01([0|1|6|7|8|9]{1})[0-9]{3,4}[0-9]{4}$";

    public static final String SHOP_TEL_NUMBER = "^[0-9]{10,11}$";

    public static final String URL = "^(http|https)://.*";


}