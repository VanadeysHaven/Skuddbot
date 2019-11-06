package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * This is to easily recall emoji's without going out and copying them.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4-ALPHA-DEV
 */
@Getter
public enum EmojiEnum {

    WHITE_CHECK_MARK        ("✅", "white_check_mark"),
    ARROW_UP                ("⬆", "arrow_up"),
    WARNING                 ("⚠", "warning"),
    X                       ("❌", "x"),
    HOURGLASS_FLOWING_SAND  ("⏳", "hourglass_flowing_sand"),
    CROSSED_SWORDS          ("⚔", "crossed_swords"),
    EYES                    ("\uD83D\uDC40", "eyes"),
    MAILBOX_WITH_MAIL       ("\uD83D\uDCEC", "mailbox_with_mail"),
    NOTEPAD_SPIRAL          ("\uD83D\uDDD2", "spiral_note_pad"),
    SPADES                  ("♠", "spades"),
    CLUBS                   ("♣", "clubs"),
    DIAMONDS                ("♦", "diamonds"),
    HEARTS                  ("♥", "hearts"),
    QUESTION                ("❓", "question"),
    TWO                     ("2⃣", "two"),
    THREE                   ("3⃣", "three"),
    FOUR                    ("4⃣", "four"),
    FIVE                    ("5⃣", "five"),
    SIX                     ("6⃣", "six"),
    SEVEN                   ("7⃣", "seven"),
    EIGHT                   ("8⃣", "eight"),
    NINE                    ("9⃣", "nine"),
    TEN                     ("\uD83D\uDD1F", "keycap_ten"),
    A                       ("\uD83C\uDDE6", "regional_indicator_symbol_a"),
    H                       ("\uD83C\uDDED", "regional_indicator_symbol_h"),
    J                       ("\uD83C\uDDEF", "regional_indicator_symbol_j"),
    K                       ("\uD83C\uDDF0", "regional_indicator_symbol_k"),
    Q                       ("\uD83C\uDDF6", "regional_indicator_symbol_q"),
    S                       ("\uD83C\uDDF8", "regional_indicator_symbol_s");

    private String unicode;
    private String alias;

    EmojiEnum(String unicode, String alias){
        this.unicode = unicode;
        this.alias = alias;
    }

    public String getString(){
        return ":" + alias + ":";
    }

    public static EmojiEnum getByUnicode(String unicodeEmoji){
        for(EmojiEnum emoji : EmojiEnum.values()){
            if(emoji.getUnicode().equals(unicodeEmoji)){
                return emoji;
            }
        }
        return null;
    }

}
