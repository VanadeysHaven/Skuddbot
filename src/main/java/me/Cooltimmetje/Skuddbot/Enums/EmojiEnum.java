package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * This is to easily recall emoji's without going out and copying them.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4-ALPHA-DEV
 * @since v0.4-ALPHA-DEV
 */
@Getter
public enum EmojiEnum {

    WHITE_CHECK_MARK("✅"),
    ARROW_UP("⬆"),
    WARNING("⚠"),
    X("❌"),
    HOURGLASS_FLOWING_SAND("⏳"),
    CROSSED_SWORDS("⚔");


    private String emoji;

    EmojiEnum(String s){
        this.emoji = s;
    }

}
