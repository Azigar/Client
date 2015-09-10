package ua.azigar.client.Resources;

/**
 * Created by Azigar on 26.07.2015.
 * класс проверяет полученый текст, что бы не совпадал с командами для обмена данных
 */
public class NoName {

    private String name;

    public boolean NoName(String text) {
        this.name = text;
        boolean yes = true;
        if (name.equalsIgnoreCase("ENTER") ||
                name.equalsIgnoreCase("END") ||
                name.equalsIgnoreCase("") ||
                name.equalsIgnoreCase("null") ||
                name.equalsIgnoreCase("ID_PLAEYR") ||
                name.equalsIgnoreCase("YES_HERO") ||
                name.equalsIgnoreCase("GET_FULL") ||
                name.equalsIgnoreCase("OK") ||
                name.equalsIgnoreCase("RATING") ||
                name.equalsIgnoreCase("STR") ||
                name.equalsIgnoreCase("DEX") ||
                name.equalsIgnoreCase("INST") ||
                name.equalsIgnoreCase("DEF") ||
                name.equalsIgnoreCase("MY_URON") ||
                name.equalsIgnoreCase("ENEMY_URON") ||
                name.equalsIgnoreCase("PVP_V") ||
                name.equalsIgnoreCase("PVP_L") ||
                name.equalsIgnoreCase("PVE_V") ||
                name.equalsIgnoreCase("PVE_L") ||
                name.equalsIgnoreCase("GET") ||
                name.equalsIgnoreCase("NAME") ||
                name.equalsIgnoreCase("LVL") ||
                name.equalsIgnoreCase("TITLE") ||
                name.equalsIgnoreCase("MAX_EXP") ||
                name.equalsIgnoreCase("EXP") ||
                name.equalsIgnoreCase("MAX_PVP_EXP") ||
                name.equalsIgnoreCase("PVP_EXP") ||
                name.equalsIgnoreCase("MAX_HP") ||
                name.equalsIgnoreCase("HP") ||
                name.equalsIgnoreCase("MAX_MANA") ||
                name.equalsIgnoreCase("MANA") ||
                name.equalsIgnoreCase("MONEY") ||
                name.equalsIgnoreCase("GOLD") ||
                name.equalsIgnoreCase("VIP") ||
                name.equalsIgnoreCase("SEX") ||
                name.equalsIgnoreCase("AVATAR") ||
                name.equalsIgnoreCase("BOUNTY") ||
                name.equalsIgnoreCase("YES_BOUNTY") ||
                name.equalsIgnoreCase("WHAT_BOUNTY") ||
                name.equalsIgnoreCase("HOLIDAY") ||
                name.equalsIgnoreCase("YES_HOLIDAY") ||
                name.equalsIgnoreCase("WHAT_HOLIDAY") ||
                name.equalsIgnoreCase("BIRTHDAY") ||
                name.equalsIgnoreCase("YES_BIRTHDAY") ||
                name.equalsIgnoreCase("WHAT_BIRTHDAY") ||
                name.equalsIgnoreCase("SAVE_GAME") ||
                name.equalsIgnoreCase("SAVE") ||
                name.equalsIgnoreCase("REGEN") ||
                name.equalsIgnoreCase("NO_REGEN") ||
                name.equalsIgnoreCase("PRICE") ||
                name.equalsIgnoreCase("YES") ||
                name.equalsIgnoreCase("GOOD") ||
                name.equalsIgnoreCase("NO_MONEY") ||
                name.equalsIgnoreCase("OPPONENT") ||
                name.equalsIgnoreCase("SEARCH") ||
                name.equalsIgnoreCase("OK_OK") ||
                name.equalsIgnoreCase("SELECT") ||
                name.equalsIgnoreCase("FOUND") ||
                name.equalsIgnoreCase("NO_FOUND") ||
                name.equalsIgnoreCase("NEXT") ||
                name.equalsIgnoreCase("YES_NEXT") ||
                name.equalsIgnoreCase("LAST") ||
                name.equalsIgnoreCase("YES_LAST") ||
                name.equalsIgnoreCase("NO_LAST") ||
                name.equalsIgnoreCase("ID") ||
                name.equalsIgnoreCase("FIGHT_ACTIVITY") ||
                name.equalsIgnoreCase("YOUR_QUESTIONS") ||
                name.equalsIgnoreCase("WHO_FIRST") ||
                name.equalsIgnoreCase("FIRST_PLAYER") ||
                name.equalsIgnoreCase("FIRST_ENEMY") ||
                name.equalsIgnoreCase("ATK_1") ||
                name.equalsIgnoreCase("ATK_2") ||
                name.equalsIgnoreCase("ATK_3") ||
                name.equalsIgnoreCase("DEF_1") ||
                name.equalsIgnoreCase("DEF_2") ||
                name.equalsIgnoreCase("DEF_3") ||
                name.equalsIgnoreCase("LOSE") ||
                name.equalsIgnoreCase("VICTORY") ||
                name.equalsIgnoreCase("UP_LVL") ||
                name.equalsIgnoreCase("NO_LVL_UP") ||
                name.equalsIgnoreCase("UP_PVP") ||
                name.equalsIgnoreCase("NO_PVP_UP") ||
                name.equalsIgnoreCase("COMMENT_ATK") ||
                name.equalsIgnoreCase("COMMENT_DEF") ||
                name.equalsIgnoreCase("NO_HERO") ||
                name.equalsIgnoreCase("DATE_BIRTH") ||
                name.equalsIgnoreCase("REGISTERED") ||
                name.equalsIgnoreCase("NAME_EXISTS") ||
                name.equalsIgnoreCase("NO_BIRTHDAY") ||
                name.equalsIgnoreCase("NO_HOLIDAY") ||
                name.equalsIgnoreCase("NO_BOUNTY")) {
            yes = false;
        }
        return yes;
    }
}
