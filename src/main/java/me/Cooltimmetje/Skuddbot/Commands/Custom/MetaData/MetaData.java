package me.Cooltimmetje.Skuddbot.Commands.Custom.MetaData;

import lombok.Getter;

/**
 * Commands MetaData types.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA
 * @since v0.5-ALPHA
 */
@Getter
public enum MetaData {

    TIME_CREATED    ("time_created",    "Long",    "0"),
    LAST_UPDATED    ("last_updated",    "Long",    "0"),
    TIMES_USED      ("times_used",      "Integer", "0"),
    CREATED_BY      ("created_by",      "Long",    "0"),
    LAST_UPDATED_BY ("last_updated_by", "Long",    "0");

    private String jsonReference;
    private String type;
    private String defaultValue;

    MetaData(String jsonReference, String type, String defaultValue){
        this.jsonReference = jsonReference;
        this.type = type;
        this.defaultValue = defaultValue;
    }



}
