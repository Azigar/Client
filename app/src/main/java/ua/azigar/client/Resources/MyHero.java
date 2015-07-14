package ua.azigar.client.Resources;

import android.app.Application;

/**
 * Created by Azigar on 09.07.2015.
 * класс хранит в себе глобильные переменные характеристик героя для постоенние активити локации
 */

public class MyHero extends Application {

    String ID; // ID-аккаунта
    String NAME;
    String LVL;
    String TITLE;
    String MAX_EXP;
    String EXP;
    String MAX_PVP_EXP;
    String PVP_EXP;
    String MAX_HP;
    String HP;
    String MAX_MANA;
    String MANA;
    String MONEY;
    String GOLD;
    String VIP;
    String FIRST_START;

    public String getFIRST_START() {
        return FIRST_START;
    }

    public void setFIRST_START(String FIRST_START) {
        this.FIRST_START = FIRST_START;
    }

    public String getVIP() {

        return VIP;
    }

    public void setVIP(String VIP) {
        this.VIP = VIP;
    }

    public String getGOLD() {

        return GOLD;
    }

    public void setGOLD(String GOLD) {
        this.GOLD = GOLD;
    }

    public String getMONEY() {

        return MONEY;
    }

    public void setMONEY(String MONEY) {
        this.MONEY = MONEY;
    }

    public String getMANA() {

        return MANA;
    }

    public void setMANA(String MANA) {
        this.MANA = MANA;
    }

    public String getMAX_MANA() {

        return MAX_MANA;
    }

    public void setMAX_MANA(String MAX_MANA) {
        this.MAX_MANA = MAX_MANA;
    }

    public String getHP() {

        return HP;
    }

    public void setHP(String HP) {
        this.HP = HP;
    }

    public String getMAX_HP() {

        return MAX_HP;
    }

    public void setMAX_HP(String MAX_HP) {
        this.MAX_HP = MAX_HP;
    }

    public String getPVP_EXP() {

        return PVP_EXP;
    }

    public void setPVP_EXP(String PVP_EXP) {
        this.PVP_EXP = PVP_EXP;
    }

    public String getMAX_PVP_EXP() {

        return MAX_PVP_EXP;
    }

    public void setMAX_PVP_EXP(String MAX_PVP_EXP) {
        this.MAX_PVP_EXP = MAX_PVP_EXP;
    }

    public String getEXP() {

        return EXP;
    }

    public void setEXP(String EXP) {
        this.EXP = EXP;
    }

    public String getMAX_EXP() {

        return MAX_EXP;
    }

    public void setMAX_EXP(String MAX_EXP) {
        this.MAX_EXP = MAX_EXP;
    }

    public String getTITLE() {

        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getLVL() {

        return LVL;
    }

    public void setLVL(String LVL) {
        this.LVL = LVL;
    }

    public String getNAME() {

        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getID() {

        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
