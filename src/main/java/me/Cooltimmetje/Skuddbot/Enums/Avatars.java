package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * Contains all the avatar variations for Skuddbot.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.1-ALPHA-DEV
 * @since v0.4.1-ALPHA-DEV
 */
@Getter
public enum Avatars {

    DEFAULT("https://i.imgur.com/v1vlVru.png"),
    CHRISTMAS("https://i.imgur.com/fc0ORQx.png"),
    WIP("http://i.imgur.com/HTZy6Ve.png");

    String url;

    Avatars(String s) {
        this.url = s;
    }
}
