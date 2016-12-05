package me.Cooltimmetje.Skuddbot.Enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Tim on 8/2/2016.
 */
public enum ErrorMessages {

    A01("lol no..."),
    A02("You little..."),
    A03("And you think it's gonna work like this?"),
    A04("Ya dun goofed..."),
    A05("You know, that's not gonna work?"),
    A06("(╯°□°）╯︵ ┻━┻"),
    A07("Ayy, you broke it!"),
    A08(":facepalm:"),
    A09("**NUKES DIDN'T LAUNCH**"),
    A10("R A T E L I M I T E D"),
    A11("Oh hey, it's you again."),
    A12("#BlameTimmy"),
    A13("#BlameRay"),
    A14("#BlameGameslinx"),
    A15("I told you to not press the red button."),
//    A16("While we're at it: Follow Ray on Twitch: https://www.twitch.tv/rayskudda"),
    A17("kden"),
    A18("How rude of you."),
    A19("Error: Not dank enough."),
    A20("Dun dun duuuuuuuun..."),
    A21("#Baron4Ban"),
    A22("( ͡° ͜ʖ ͡°)"),
    A23(":eyes:"),
    A24("Needs more Razzberries."),
    A25("A wild 'you fucked up' appeared...");

    private String error;

    ErrorMessages(String error){
        this.error = error;
    }

    public String getError(){
        return error;
    }

    private static final List<ErrorMessages> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static ErrorMessages random()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }


}
