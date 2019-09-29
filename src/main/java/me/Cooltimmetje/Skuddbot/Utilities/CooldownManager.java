package me.Cooltimmetje.Skuddbot.Utilities;

import java.util.HashMap;

/**
 * Class to manage cooldowns
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.7-ALPHA
 * @since v0.4.7-ALPHA
 */
public class CooldownManager {

    private int delay; //in seconds
    private HashMap<String,Long> lastUsed;

    public CooldownManager(int delay) {
        this.delay = delay;
        this.lastUsed = new HashMap<>();
    }

    public void applyCooldown(String identifier){
        applyCooldown(identifier, System.currentTimeMillis());
    }

    public void applyCooldown(String identifier, Long lastUsed){
        this.lastUsed.put(identifier, lastUsed);
    }

    public boolean isOnCooldown(String identifier) {
        if(!lastUsed.containsKey(identifier)) return false;
        return ((System.currentTimeMillis() - lastUsed.get(identifier))/1000) < delay;
    }

    public boolean isCooldownExpired(String identifier){
        return !isOnCooldown(identifier);
    }

    public void clearAll(){
        lastUsed.clear();
    }

}
