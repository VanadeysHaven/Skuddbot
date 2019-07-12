package me.Cooltimmetje.Skuddbot.Profiles.Currencies;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a currency.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA
 * @since v0.5-ALPHA
 */
@Getter
@Setter
public class Currency {

    private Currencies type;
    private int amount;

    public Currency (Currencies type, int amount){
        this.type = type;
        this.amount = amount;
    }



}
