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
 * Created by Azigar on 03.09.2015.
 */
public class Quest extends Thread {

    BufferedReader in;
    Socket socket;
    SocketConfig conf;
    Handler h;

    public Quest(Handler h1, SocketConfig con) {  //главный
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
                    h.sendEmptyMessage(2);
                    conf.setSOCKET_CONNECTED(false);
                    break;
                } else {  //пришло сообщение от сервера
                    ////КОМАНДЫ КЛИЕНТА
                    if (conf.getSOCKET_OUT() == "END") {
                        conf.setSOCKET_CONNECTED(false);
                        threadOUT.interrupt(); //закрываю второй поток
                        socket.close(); //закрываю сокет
                        break;
                    }
                    //клиент спрашивает уровень героя
                    if (conf.getSOCKET_OUT() == "LVL") {
                        msg.what = 4; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает пвп-уровень героя
                    if (conf.getSOCKET_OUT() == "PVP_LVL") {
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
                    //клиент спрашивает мак. к-во опыта героя
                    if (conf.getSOCKET_OUT() == "MAX_EXP") {
                        msg.what = 7; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает текущее к-во опыта героя
                    if (conf.getSOCKET_OUT() == "EXP") {
                        msg.what = 8; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает  мак. к-во pvp-опыта героя
                    if (conf.getSOCKET_OUT() == "MAX_PVP_EXP") {
                        msg.what = 9; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает текущее к-во pvp-опыта героя
                    if (conf.getSOCKET_OUT() == "PVP_EXP") {
                        msg.what = 10; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает мак. к-во ОЗ героя
                    if (conf.getSOCKET_OUT() == "MAX_HP") {
                        msg.what = 11; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает текущее к-во ОЗ героя
                    if (conf.getSOCKET_OUT() == "HP") {
                        msg.what = 12; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает мак. к-во маны игрока
                    if (conf.getSOCKET_OUT() == "MAX_MANA") {
                        msg.what = 13; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во маны игрока
                    if (conf.getSOCKET_OUT() == "MANA") {
                        msg.what = 14; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во монет игрока
                    if (conf.getSOCKET_OUT() == "MONEY") {
                        msg.what = 15; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "GOLD") {
                        msg.what = 16; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает ID-квеста
                    if (conf.getSOCKET_OUT() == "GET_ID_QUEST") {
                        msg.what = 17; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_EXECUTE") {
                        msg.what = 18; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "LAST_EXECUTE") {
                        msg.what = 19; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //повысил ли клиент уровень
                    if (conf.getSOCKET_OUT() == "UP_LVL") {
                        msg.what = 20; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //повысил ли клиент звание
                    if (conf.getSOCKET_OUT() == "UP_PVP") {
                        msg.what = 21; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    ////КОМАНДЫ СЕРВЕРА
                    //сервер запрашивает ID игрока
                    if (cmd.equalsIgnoreCase("ID_PLAEYR")) {
                        conf.setSOCKET_MESSAGE(conf.getID());
                    }
                    //сервер распознал ID игрока и ждет команды
                    if (cmd.equalsIgnoreCase("YES_HERO")) {
                        conf.setSOCKET_MESSAGE("QUEST");
                    }
                    if (cmd.equalsIgnoreCase("ID_QUEST")) {
                        conf.setSOCKET_MESSAGE(conf.getID_QUEST());
                    }
                    //сервер оповещает об ошибке запроса
                    if (cmd.equalsIgnoreCase("ERROR")) {
                        h.sendEmptyMessage(3);
                    }
                    if (cmd.equalsIgnoreCase("YES_GET_QUEST") || cmd.equalsIgnoreCase("YES_DELETE_QUEST")) {
                        conf.setSOCKET_MESSAGE("GET");  //спрашиваю новые хар-ки героя
                    }
                    if (cmd.equalsIgnoreCase("OK_OK_GET")) {
                        conf.setSOCKET_MESSAGE("GET");  //спрашиваю новые хар-ки героя
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("OK")) {
                        conf.setSOCKET_MESSAGE("LVL");
                    }
                    if (cmd.equalsIgnoreCase("YES_EXECUTE")) {
                        conf.setSOCKET_MESSAGE("GET_ID_QUEST");
                    }
                    if (cmd.equalsIgnoreCase("YES_NEXT_EXECUTE")) {
                        conf.setSOCKET_MESSAGE("GET_ID_QUEST");
                    }
                    //квест взят на выполнение и сервер ждет команды
                    if (cmd.equalsIgnoreCase("YES_EXIST_QUEST")) {
                        conf.setSOCKET_MESSAGE(conf.getEXE_QUEST());
                    }
                    //успешное завершение квеста и стпрашиваю что случилось с героем после этого
                    if (cmd.equalsIgnoreCase("YES_TAKEN")) {
                        conf.setSOCKET_MESSAGE("UP_LVL");
                    }
                    if (cmd.equalsIgnoreCase("NO_TAKEN")) {
                        h.sendEmptyMessage(23);
                    }
                    if (cmd.equalsIgnoreCase("OK_OK_CANCEL_QUEST")) {
                        conf.setSOCKET_MESSAGE("CANCEL_QUEST");
                    }
                    if (cmd.equalsIgnoreCase("FULL_INVENTAR")) {
                        h.sendEmptyMessage(24);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                conf.setSOCKET_CONNECTED(false);
            }
        }
    }
}
