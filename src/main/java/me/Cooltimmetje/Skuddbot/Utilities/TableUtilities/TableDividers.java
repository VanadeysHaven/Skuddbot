package me.Cooltimmetje.Skuddbot.Utilities.TableUtilities;

/**
 * This class is used to store and easily recall table drawing characters.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.62-ALPHA
 * @since v0.4.62-ALPHA
 */
public enum TableDividers {

    HORIZONTAL      ("─"),
    VERTICAL        ("│"),

    DOWN_LEFT       ("┐"),
    DOWN_RIGHT      ("┌"),
    UP_LEFT         ("┘"),
    UP_RIGHT        ("└"),

    VERTICAL_RIGHT  ("├"),
    VERTICAL_LEFT   ("┤"),
    HORIZONTAL_DOWN ("┬"),
    HORIZONTAL_UP   ("┴"),

    CENTRAL         ("┼");

    private String character;

    TableDividers(String character){
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }
}
