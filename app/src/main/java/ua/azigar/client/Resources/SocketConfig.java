package ua.azigar.client.Resources;

/**
 * Created by Azigar on 10.06.2015.
 * класс для хранение и передачи синхронизированных данных по соекету
 */

public class SocketConfig {

    boolean SOCKET_CONNECTED = false;
    String SERVER_ADDR = "192.168.1.23";
    int SERVER_PORT = 8888;
    String SOCKET_MESSAGE = null;
    String SOCKET_OUT;
    String ID, NAME, HP, MANA, MAX_HP, MAX_MANA, AVATAR, DATE_BIRTH, SEX, PASS;
    String ID_ENEMY, HP_ENEMY, NAME_ENEMY, SHOP, ID_OBJ;
    String LAST_LOCAL, ID_QUEST, EXE_QUEST, IS_MANA;


    public String getIS_MANA() {
        return IS_MANA;
    }

    public void setIS_MANA(String IS_MANA) {
        this.IS_MANA = IS_MANA;
    }

    public String getEXE_QUEST() {
        return EXE_QUEST;
    }

    public void setEXE_QUEST(String EXE_QUEST) {
        this.EXE_QUEST = EXE_QUEST;
    }

    public String getID_QUEST() {
        return ID_QUEST;
    }

    public void setID_QUEST(String ID_QUEST) {
        this.ID_QUEST = ID_QUEST;
    }

    public String getID_OBJ() {
        return ID_OBJ;
    }

    public void setID_OBJ(String ID_OBJ) {
        this.ID_OBJ = ID_OBJ;
    }

    public String getSHOP() {
        return SHOP;
    }

    public void setSHOP(String SHOP) {
        this.SHOP = SHOP;
    }

    public String getSEX() {
        return SEX;
    }

    public String getPASS() {
        return PASS;
    }

    public void setPASS(String PASS) {
        this.PASS = PASS;
    }

    public String getNAME_ENEMY() {
        return NAME_ENEMY;
    }

    public void setNAME_ENEMY(String NAME_ENEMY) {
        this.NAME_ENEMY = NAME_ENEMY;
    }

    public void setSEX(String SEX) {
        this.SEX = SEX;
    }

    public String getMAX_MANA() {
        return MAX_MANA;
    }

    public String getDATE_BIRTH() {
        return DATE_BIRTH;
    }

    public void setDATE_BIRTH(String DATE_BIRTH) {
        this.DATE_BIRTH = DATE_BIRTH;
    }

    public String getAVATAR() {
        return AVATAR;
    }

    public void setAVATAR(String AVATAR) {
        this.AVATAR = AVATAR;
    }

    public void setMAX_MANA(String MAX_MANA) {
        this.MAX_MANA = MAX_MANA;
    }

    public String getMAX_HP() {

        return MAX_HP;
    }

    public void setMAX_HP(String MAX_HP) {
        this.MAX_HP = MAX_HP;
    }

    public String getLAST_LOCAL() {
        return LAST_LOCAL;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public void setLAST_LOCAL(String LAST_LOCAL) {
        this.LAST_LOCAL = LAST_LOCAL;
    }

    public String getHP_ENEMY() {
        return HP_ENEMY;
    }

    public void setHP_ENEMY(String HP_ENEMY) {
        this.HP_ENEMY = HP_ENEMY;
    }

    public String getID_ENEMY() {
        return ID_ENEMY;
    }

    public void setID_ENEMY(String ID_ENEMY) {
        this.ID_ENEMY = ID_ENEMY;
    }

    public String getMANA() {
        return MANA;
    }

    public void setMANA(String MANA) {
        this.MANA = MANA;
    }

    public String getHP() {

        return HP;
    }

    public void setHP(String HP) {
        this.HP = HP;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public synchronized String getSOCKET_MESSAGE() {
        return this.SOCKET_MESSAGE;
    }

    public synchronized void setSOCKET_MESSAGE(String SOCKET_MESSAGE) {
        this.SOCKET_MESSAGE = SOCKET_MESSAGE;
    }

    public String getSOCKET_OUT() {
        return SOCKET_OUT;
    }

    public void setSOCKET_OUT(String SOCKET_OUT) {
        this.SOCKET_OUT = SOCKET_OUT;
    }

    public synchronized boolean getSOCKET_CONNECTED() {
        return this.SOCKET_CONNECTED;
    }

    public synchronized void setSOCKET_CONNECTED(boolean SOCKET_CONNECTED) {
        this.SOCKET_CONNECTED = SOCKET_CONNECTED;
    }

    public synchronized String getSERVER_ADDR() {
        return this.SERVER_ADDR;
    }

    public synchronized int getSERVER_PORT() {
        return this.SERVER_PORT;
    }
}
