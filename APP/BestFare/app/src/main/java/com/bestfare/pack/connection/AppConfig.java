package com.bestfare.pack.connection;

/**
 * Created by Rishabhkokra on 4/10/2016.
 */
public class AppConfig {
    public static String URL_REGISTER ="http://mystuffsite.coolpage.biz/register_user.php";
    public static String URL_CHECK_EMAIL_CONTACT=  "http://mystuffsite.coolpage.biz/contactNemail_check.php";
public static String JSON_URL="http://mystuffsite.coolpage.biz/game_stats.json";
    public static final String SMS_ORIGIN = "SMSZON";
    public static final String OTP_VERIFICATION_URL="http://mystuffsite.coolpage.biz/otp_verification.php";
    public static final String LOGIN_URL="http://mystuffsite.coolpage.biz/login.php";
    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";
}
