package ua.azigar.client.Client;

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
    String ID, HP, MAX_HP, MANA, MAX_MANA, VIP;
    String ID_ENEMY, HP_ENEMY;
    String LAST_LOCAL;


    public String getLAST_LOCAL() {
        return LAST_LOCAL;
    }

    public void setLAST_LOCAL(String LAST_LOCAL) {
        this.LAST_LOCAL = LAST_LOCAL;
    }

    public String getVIP() {

        return VIP;
    }

    public void setVIP(String VIP) {
        this.VIP = VIP;
    }

    public String getMAX_MANA() {

        return MAX_MANA;
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
