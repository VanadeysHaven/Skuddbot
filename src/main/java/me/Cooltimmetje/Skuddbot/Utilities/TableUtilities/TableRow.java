package me.Cooltimmetje.Skuddbot.Utilities.TableUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * This class represents a row in a table.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.62-ALPHA
 * @since v0.4.62-ALPHA
 */
public class TableRow {

    private ArrayList<String> strings;

    public TableRow(){
        this.strings = new ArrayList<String>();
    }

    public TableRow(String... strings){
        this.strings = new ArrayList<String>(Arrays.asList(strings));
    }

    public void addString(String string){
        this.strings.add(string);
    }

    public Iterator<String> getIterator(){
        return strings.iterator();
    }

    public int getLength(){
        return strings.size();
    }

}
