package me.Cooltimmetje.Skuddbot.Commands.Custom.MetaData;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Container for holding metadata.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.5-ALPHA
 * @since v0.4.5-ALPHA
 */
public class MetaDataContainer {

    private static final JSONParser PARSER = new JSONParser();

    @Getter @Setter private long timeCreated;
    @Getter @Setter private long lastUpdated;
    @Getter @Setter private int timesUsed;
    @Getter @Setter private long createdBy;
    @Getter @Setter private long lastUpdatedBy;

    public MetaDataContainer(String json) throws ParseException {
        JSONObject obj = (JSONObject) PARSER.parse(json);

        for(MetaData md : MetaData.values()){
            if(obj.containsKey(md.getJsonReference())){
                setMetaData(md, String.valueOf(obj.get(md.getJsonReference())));
            } else {
                setMetaData(md, md.getDefaultValue());
            }
        }
    }

    private String setMetaData(MetaData md, String value){
        double doubleValue = 0;
        long longValue = 0;
        boolean booleanValue = false;
        int intValue = 0;
        boolean intUsed = false;

        switch (md.getType().toLowerCase()){
            case "double":
                try {
                    doubleValue = Double.parseDouble(value);
                } catch (NumberFormatException e){
                    return "Value is not a Double.";
                }
                break;
            case "integer":
                try {
                    intValue = Integer.parseInt(value);
                    intUsed = true;
                } catch (NumberFormatException e){
                    return "Value is not a Integer.";
                }
                break;
            case "boolean":
                booleanValue = Boolean.parseBoolean(value);
                if (!booleanValue) {
                    if(!value.equalsIgnoreCase("false")){
                        return "Value is not a boolean.";
                    }
                }
                break;
            case "long":
                try {
                    longValue = Long.parseLong(value);
                } catch (NumberFormatException e){
                    return "Value is not a Long.";
                }
            default:
                if(value.equalsIgnoreCase("null")){
                    value = null;
                }
                break;
        }

        switch (md){
            case TIME_CREATED:
                this.timeCreated = longValue;
                return null;
            case LAST_UPDATED:
                this.lastUpdated = longValue;
                return null;
            case TIMES_USED:
                this.timesUsed = intValue;
                return null;
            case CREATED_BY:
                this.createdBy = longValue;
                return null;
            case LAST_UPDATED_BY:
                this.lastUpdatedBy = longValue;
            default:
                return null;
        }
    }

    private String getMetaData(MetaData md){
        switch (md){
            case TIME_CREATED:
                return this.timeCreated+"";
            case LAST_UPDATED:
                return this.lastUpdated+"";
            case TIMES_USED:
                return this.timesUsed+"";
            case CREATED_BY:
                return this.createdBy+"";
            case LAST_UPDATED_BY:
                return this.lastUpdatedBy+"";
            default:
                return null;
        }
    }

    public void incrementCount(){
        timesUsed += 1;
    }

    public String getJSON() {
        JSONObject obj = new JSONObject();

        for(MetaData md : MetaData.values()){
            if(getMetaData(md) != null) {
                if(!getMetaData(md).equals(md.getDefaultValue())){
                    obj.put(md.getJsonReference(), getMetaData(md));
                }
            }
        }

        return obj.toString();
    }

}
