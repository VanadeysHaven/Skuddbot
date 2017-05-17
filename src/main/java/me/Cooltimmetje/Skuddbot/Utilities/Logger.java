package me.Cooltimmetje.Skuddbot.Utilities;

import org.slf4j.LoggerFactory;

/**
 * Created by Tim on 8/2/2016.
 */
public class Logger {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Logger.class);


    public static void info(String string){
        log.info(string);
    }

    public static void warn(String string, Exception e){
        log.warn(string,e);
    }

}
