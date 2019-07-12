package me.Cooltimmetje.Skuddbot.Profiles.Currencies;

import lombok.Getter;

/**
 * This enumerator holds all types of currencies.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA
 * @since v0.5-ALPHA
 */
@Getter
public enum Currencies {

    MAIN ("main", "SkuddBux");

    private String jsonReference;
    private String name;

    Currencies(String jsonReference, String name){
        this.jsonReference = jsonReference;
        this.name = name;
    }

}
