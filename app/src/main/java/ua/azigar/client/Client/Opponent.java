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
 * Created by Azigar on 23.07.2015.
 */
public class Opponent extends Thread {

    BufferedReader in;
    Socket socket;
    Handler h;
    SocketConfig conf;

    public Opponent (Handler h1, SocketConfig con) {
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
                h.sendEmptyMessage(1);  //отсылаю в UI сообщение
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
                while (socket != null && socket.isConnected() && conf.getSOCKET_CONNECTED()==true && !socket.isClosed()) {
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
                    h.sendEmptyMessage(3);
                    conf.setSOCKET_CONNECTED(false);
                    break;
                } else {  //пришло сообщение от сервера
                    ////КОМАНДЫ КЛИЕНТА
                    //клиент уходит от боя
                    if (conf.getSOCKET_OUT() == "END" || cmd.equalsIgnoreCase("END")) {
                        conf.setSOCKET_CONNECTED(false);
                        socket.close(); //закрываю сокет
                        threadOUT.interrupt(); //закрываю второй поток
                        break;
                    }
                    //клиент спрашивает имя игрока
                    if (conf.getSOCKET_OUT() == "NAME") {
                        msg.what = 4; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает уровень героя
                    if (conf.getSOCKET_OUT() == "LVL") {
                        msg.what = 5; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает звание героя
                    if (conf.getSOCKET_OUT() == "TITLE") {
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
                    //клиент спрашивает текущее к-во ОЗ героя
                    if (conf.getSOCKET_OUT() == "HP") {
                        msg.what = 8; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во маны игрока
                    if (conf.getSOCKET_OUT() == "MANA") {
                        msg.what = 9; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает вип-уровень игрока
                    if (conf.getSOCKET_OUT() == "VIP") {
                        msg.what = 10; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает аватар героя
                    if (conf.getSOCKET_OUT() == "AVATAR") {
                        msg.what = 11; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "RATING") {
                        msg.what = 12;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "STR") {
                        msg.what = 13;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "DEX") {
                        msg.what = 14;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "INST") {
                        msg.what = 15;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "DEF") {
                        msg.what = 16;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "MY_URON") {
                        msg.what = 17;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "ENEMY_URON") {
                        msg.what = 18;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "PVP_V") {
                        msg.what = 19;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "PVP_L") {
                        msg.what = 20;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "PVE_V") {
                        msg.what = 21;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "PVE_L") {
                        msg.what = 22;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "ID") {
                        msg.what = 23;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "LAST") {
                        msg.what = 24;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "SEX") {
                        msg.what = 26;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    ////КОМАНДЫ СЕРВЕРА
                    //сервер спрашивает ID игрока
                    if (cmd.equalsIgnoreCase("ID_PLAEYR")){
                        conf.setSOCKET_MESSAGE(conf.getID()); //отправляю ID игрока
                    }
                    //сервер готов авторизировать клиента
                    if (cmd.equalsIgnoreCase("YES_HERO")){
                        conf.setSOCKET_MESSAGE("OPPONENT"); //отправляю запрос на получение полных данных о герое
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("OK")) {
                        conf.setSOCKET_MESSAGE("SELECT"); //запрашиваю всех подходящих оппонентов
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("OK_OK")) {
                        conf.setSOCKET_MESSAGE("SEARCH"); //запрашиваю всех подходящих оппонентов
                    }
                    //сервер нашел опонента
                    if (cmd.equalsIgnoreCase("FOUND")) {
                        conf.setSOCKET_MESSAGE("GET"); //запрашиваю имя оппонента
                    }
                    //сервер не нашел опонента
                    if (cmd.equalsIgnoreCase("NO_FOUND")) {
                        h.sendEmptyMessage(25);
                    }
                    //сервер спрашивает имя оппонента для поиска
                    if (cmd.equalsIgnoreCase("NAME")) {
                        conf.setSOCKET_MESSAGE(conf.getNAME_ENEMY()); //отправляю имя искаемого героя
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("YES")) {
                        conf.setSOCKET_MESSAGE("NAME"); //запрашиваю всех подходящих оппонентов
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("YES_NEXT")) {
                        conf.setSOCKET_MESSAGE("NAME"); //запрашиваю всех подходящих оппонентов
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                conf.setSOCKET_CONNECTED(false);
            }
        }
    }
}
