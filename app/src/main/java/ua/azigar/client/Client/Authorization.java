package ua.azigar.client.Client;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import ua.azigar.client.Resources.SocketConfig;

/**
 * Created by Azigar on 06.07.2015.
 */
public class Authorization extends Thread {

    BufferedReader in;
    public Socket socket;
    Handler h;
    SocketConfig conf;
    private boolean stopped = false;

    public Authorization(Handler h1, SocketConfig con) {  //главный
        this.h = h1;
        this.conf = con;
        conf.setSOCKET_CONNECTED(false);
        start();
    }

    public void run() {
        if (conf.getSOCKET_CONNECTED()==false) {
            try {
                InetAddress ipAddress = InetAddress.getByName(conf.getSERVER_ADDR()); // создаем объект который отображает вышеописанный IP-адрес.
                socket = new Socket(ipAddress, conf.getSERVER_PORT()); // создаем сокет используя IP-адрес и порт сервера.
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                h.sendEmptyMessage(1);  //отсылаю в UI сообщение о успешном подключнении
                conf.setSOCKET_CONNECTED(true);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                h.sendEmptyMessage(2);  //отсылаю в UI сообщение
                conf.setSOCKET_CONNECTED(false);
            } catch (IOException e) {
                e.printStackTrace();
                h.sendEmptyMessage(2);  //отсылаю в UI сообщение
                conf.setSOCKET_CONNECTED(false);
            }
        }

        //запускаю второй поток для отправки сообщения
        Thread threadOUT = new Thread(new Runnable() {
            @Override
            public void run() {
                //вечный цыкл, пока есть конект
                while (socket != null && socket.isConnected() && conf.getSOCKET_CONNECTED()==true &&
                        !socket.isClosed() && !isStopped()) {
                    //если переменная для отправки имеет значение
                    if (conf.getSOCKET_MESSAGE() != null) {
                        try {
                            PrintWriter out = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8")),
                                    true); //подключаю отправитель в переменную
                            out.println(conf.getSOCKET_MESSAGE()); //отправляю сообщение на сервер
                            conf.setSOCKET_OUT(conf.getSOCKET_MESSAGE());  //сохраняю значение отправленого в переменую
                        } catch (Exception e) {
                            conf.setSOCKET_CONNECTED(false);  //нет соединение
                            return;
                        }
                        conf.setSOCKET_MESSAGE(null);  //очищаю переменную для отправки
                    }
                }
            }
        });
        threadOUT.start(); // запускаем на отправку

        //пока есть конект с сервером, цикл ждет приема сообщений
        while (socket != null && socket.isConnected() && conf.getSOCKET_CONNECTED()==true && !socket.isClosed()) {
            Message msg = new Message();  //создаю меседж-обьект
            try {
                //переменная для получение данных
                String cmd = new String(in.readLine());

                if (cmd.equalsIgnoreCase(null)) { //если нет сообщения от сервера
                    socket.close(); //закрываю сокет
                    threadOUT.interrupt(); //закрываю второй поток
                    h.sendEmptyMessage(21);
                    conf.setSOCKET_CONNECTED(false);
                    break;
                } else {  //пришло сообщение от сервера
                    ////КОМАНДЫ КЛИЕНТА
                    //клиент уходит от боя
                    if (conf.getSOCKET_OUT() == "END") {
                        conf.setSOCKET_CONNECTED(false); //закрываю второй поток, первый метод
                        MyStop(); //закрываю второй поток, второй метод
                        threadOUT.interrupt(); //закрываю второй поток, третий метод
                        socket.close(); //закрываю сокет, а также второй поток, четвертый метод
                        break; //закрываю этот поток
                    }
                    //клиент спрашивает какой подарок на праздник
                    if (conf.getSOCKET_OUT() == "WHAT_BOUNTY") {
                        msg.what = 22; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает какой ежедневный подарок подарок
                    if (conf.getSOCKET_OUT() == "WHAT_HOLIDAY") {
                        msg.what = 25; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает какой ежедневный подарок подарок
                    if (conf.getSOCKET_OUT() == "WHAT_BIRTHDAY") {
                        msg.what = 26; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает имя игрока
                    if (conf.getSOCKET_OUT() == "NAME") {
                        msg.what = 15; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает уровень героя
                    if (conf.getSOCKET_OUT() == "LVL") {
                        msg.what = 16; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает пвп-уровень героя
                    if (conf.getSOCKET_OUT() == "PVP_LVL") {
                        msg.what = 37; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает звание героя
                    if (conf.getSOCKET_OUT() == "TITLE") {
                        msg.what = 17; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает мак. к-во опыта героя
                    if (conf.getSOCKET_OUT() == "MAX_EXP") {
                        msg.what = 4; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает текущее к-во опыта героя
                    if (conf.getSOCKET_OUT() == "EXP") {
                        msg.what = 5; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает  мак. к-во pvp-опыта героя
                    if (conf.getSOCKET_OUT() == "MAX_PVP_EXP") {
                        msg.what = 6; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает текущее к-во pvp-опыта героя
                    if (conf.getSOCKET_OUT() == "PVP_EXP") {
                        msg.what = 7; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает мак. к-во ОЗ героя
                    if (conf.getSOCKET_OUT() == "MAX_HP") {
                        msg.what = 8; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает текущее к-во ОЗ героя
                    if (conf.getSOCKET_OUT() == "HP") {
                        msg.what = 9; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает мак. к-во маны игрока
                    if (conf.getSOCKET_OUT() == "MAX_MANA") {
                        msg.what = 20; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во маны игрока
                    if (conf.getSOCKET_OUT() == "MANA") {
                        msg.what = 10; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во монет игрока
                    if (conf.getSOCKET_OUT() == "MONEY") {
                        msg.what = 14; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "GOLD") {
                        msg.what = 18; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает вип-уровень игрока
                    if (conf.getSOCKET_OUT() == "VIP") {
                        msg.what = 19; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает пол героя
                    if (conf.getSOCKET_OUT() == "SEX") {
                        msg.what = 23; //пришел ответ от сервера
                        msg.obj = cmd;
                         h.sendMessage(msg);
                    }
                    //клиент спрашивает аватар героя
                    if (conf.getSOCKET_OUT() == "AVATAR") {
                        msg.what = 24; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает ID-квеста
                    if (conf.getSOCKET_OUT() == "GET_ID_QUEST") {
                        msg.what = 39; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_EXECUTE") {
                        msg.what = 40; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "LAST_EXECUTE") {
                        msg.what = 41; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает новый пароль
                    if (conf.getSOCKET_OUT() == "WHAT_NEW_PASS") {
                        msg.what = 33; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //сервер говорит, что есть ежедневный подарка
                    if (conf.getSOCKET_OUT() == "BOUNTY" && cmd.equalsIgnoreCase("YES_BOUNTY")) {
                        conf.setSOCKET_MESSAGE("WHAT_BOUNTY");
                    }
                    //сервер говорит, что нет ежедневного подарка
                    if (conf.getSOCKET_OUT() == "BOUNTY" && cmd.equalsIgnoreCase("NO_BOUNTY")) {
                        conf.setSOCKET_MESSAGE("HOLIDAY");
                    }
                    //сервер говорит, что есть подарок на праздник
                    if (conf.getSOCKET_OUT() == "HOLIDAY" && cmd.equalsIgnoreCase("YES_HOLIDAY")) {
                        conf.setSOCKET_MESSAGE("WHAT_HOLIDAY");
                    }
                    //сервер говорит, что нет праздника
                    if (conf.getSOCKET_OUT() == "HOLIDAY" && cmd.equalsIgnoreCase("NO_HOLIDAY")) {
                        conf.setSOCKET_MESSAGE("BIRTHDAY");
                    }
                    //сервер говорит, что у игрока день рождения
                    if (conf.getSOCKET_OUT() == "BIRTHDAY" && cmd.equalsIgnoreCase("YES_BIRTHDAY")) {
                        conf.setSOCKET_MESSAGE("WHAT_BIRTHDAY");  //спрашиваю какой подарок
                    }
                    //сервер говорит, что нет праздника
                    if (conf.getSOCKET_OUT() == "BIRTHDAY" && cmd.equalsIgnoreCase("NO_BIRTHDAY")) {
                        conf.setSOCKET_MESSAGE("GET");
                    }
                    ////КОМАНДЫ СЕРВЕРА
                    //сервер оповещает об ошибке запроса
                    if (cmd.equalsIgnoreCase("ERROR")) {
                        h.sendEmptyMessage(38);
                    }
                    //сервер запрашивает ID игрока
                    if (cmd.equalsIgnoreCase("ID_PLAEYR")) {
                        conf.setSOCKET_MESSAGE(conf.getID());
                    }
                    //сервер говорит, что есть такой акк
                    if (cmd.equalsIgnoreCase("YES_HERO")) {
                        h.sendEmptyMessage(3); //сохраняю ID акк в Preferences.
                    }
                    //сервер говорит, что нет такого акк
                    if (cmd.equalsIgnoreCase("NO_HERO")) {
                        h.sendEmptyMessage(11);
                    }
                    //сервер говорит, что такое имя существует
                    if (cmd.equalsIgnoreCase("NAME_EXISTS")) {
                        h.sendEmptyMessage(12);
                    }
                    //сервер запрашивает дату рождения
                    if (cmd.equalsIgnoreCase("DATE_BIRTH")) {
                        conf.setSOCKET_MESSAGE(conf.getDATE_BIRTH());
                    }
                    //сервер запрашивает пол нового героя
                    if (cmd.equalsIgnoreCase("SEX")) {
                        conf.setSOCKET_MESSAGE(conf.getSEX());
                    }
                    //сервер запрашивает аватар нового героя
                    if (cmd.equalsIgnoreCase("AVATAR")) {
                        conf.setSOCKET_MESSAGE(conf.getAVATAR());
                    }
                    //сервер запрашивает аватар нового героя
                    if (cmd.equalsIgnoreCase("PASS")) {
                        conf.setSOCKET_MESSAGE(conf.getPASS());
                    }
                    //сервер говорит, что регестрация прошла успешно
                    if (cmd.equalsIgnoreCase("REGISTERED")) {
                        h.sendEmptyMessage(13);
                    }
                    //сервер говорит, что можно входить без пароля
                    if (cmd.equalsIgnoreCase("NO_PASS")) {
                        conf.setSOCKET_MESSAGE("END"); //закрываю сокет-поток
                        h.sendEmptyMessage(27);
                    }
                    //сервер спрашивает пароль
                    if (cmd.equalsIgnoreCase("YES_PASS")) {
                        h.sendEmptyMessage(28);
                    }
                    //сервер спрашивает имя героя для востановления пароля
                    if (cmd.equalsIgnoreCase("NAME_HERO")) {
                        h.sendEmptyMessage(29);
                    }
                    //сервер спрашивает лвл героя для востановления пароля
                    if (cmd.equalsIgnoreCase("LVL_HERO")) {
                        h.sendEmptyMessage(30);
                    }
                    //сервер спрашивает лвл героя для востановления пароля
                    if (cmd.equalsIgnoreCase("DATE_BIRTH_PASS")) {
                        h.sendEmptyMessage(31);
                    }
                    //сервер спрашивает лвл героя для востановления пароля
                    if (cmd.equalsIgnoreCase("DATE_BEGIN_PASS")) {
                        h.sendEmptyMessage(32);
                    }
                    //сервер передает вопросы клиенту
                    if (cmd.equalsIgnoreCase("CHANGE_PASS")) {
                        conf.setSOCKET_MESSAGE("WHAT_NEW_PASS");
                    }
                    //сервер передает вопросы клиенту
                    if (cmd.equalsIgnoreCase("OK")) {
                        conf.setSOCKET_MESSAGE("NAME");
                    }
                    /*
                    //сервер передает вопросы клиенту
                    if (cmd.equalsIgnoreCase("DID_NIT_GUESS")) {
                        h.sendEmptyMessage(34);
                    }
                    */
                    //сервер говорит, что пароль не верный
                    if (cmd.equalsIgnoreCase("ERROR_PASS")) {
                        h.sendEmptyMessage(34);
                    }
                    //сервер говорит, что имя не верно
                    if (cmd.equalsIgnoreCase("ERROR_NAME_HERO")) {
                        h.sendEmptyMessage(30);
                    }
                    //сервер говорит, дата рождения не верна
                    if (cmd.equalsIgnoreCase("ERROR_DATE_BIRTH_PASS")) {
                        h.sendEmptyMessage(35);
                    }
                    //сервер говорит, дата рождения не верна
                    if (cmd.equalsIgnoreCase("ERROR_DATE_BEGIN_PASS")) {
                        h.sendEmptyMessage(36);
                    }
                    if (cmd.equalsIgnoreCase("YES_EXECUTE")) {
                        conf.setSOCKET_MESSAGE("GET_ID_QUEST");
                    }
                    if (cmd.equalsIgnoreCase("YES_NEXT_EXECUTE")) {
                        conf.setSOCKET_MESSAGE("GET_ID_QUEST");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                conf.setSOCKET_CONNECTED(false);
            }
        }
    }

    public void MyStop () {
        setStopped(true);
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public boolean isStopped() {
        return stopped;
    }
}
