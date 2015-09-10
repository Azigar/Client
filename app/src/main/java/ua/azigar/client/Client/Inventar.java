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
 * Created by Azigar on 05.08.2015.
 */
public class Inventar extends Thread {

    BufferedReader in;
    Socket socket;
    SocketConfig conf;
    Handler h;

    public Inventar(Handler h1, SocketConfig con) {  //главный
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
                        //h.sendEmptyMessage(13);
                        break;
                    }
                    //клиент спрашивает имя игрока
                    if (conf.getSOCKET_OUT() == "HELMET") {
                        msg.what = 4; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "RIGHT_HAND") {
                        msg.what = 5; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "LEFT_HAND") {
                        msg.what = 6; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "ARMOR") {
                        msg.what = 7; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "BOOTS") {
                        msg.what = 8; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "GLOVES") {
                        msg.what = 9; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "AMULET") {
                        msg.what = 10; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "RIGHT_RING") {
                        msg.what = 11; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "LEFT_RING") {
                        msg.what = 12; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "FREE_INVENTAR") {
                        msg.what = 27; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "SIZE_INVENTAR") {
                        msg.what = 28; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент инфу о предмете на герое
                    if (conf.getSOCKET_OUT() == "GET_INFO_OBJ_HERO") {
                        msg.what = 15; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент инфу о предмете на герое
                    if (conf.getSOCKET_OUT() == "GET_LVL_OBJ_HERO") {
                        msg.what = 33; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент инфу о предмете на герое
                    if (conf.getSOCKET_OUT() == "GET_PIC_OBJ_HERO") {
                        msg.what = 34; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент инфу о предмете на герое
                    if (conf.getSOCKET_OUT() == "GET_TWO_OBJ_HERO") {
                        msg.what = 22; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает, не последняя это вещь?
                    if (conf.getSOCKET_OUT() == "LAST_INFO_OBJ_HERO") {
                        msg.what = 16; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_ID_OBJ_INVENTAR") {
                        msg.what = 17; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_WEAR_OBJ_INVENTAR") {
                        msg.what = 13; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_LVL_HERO_INVENTAR") {
                        msg.what = 20; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_QUESTION_OBJ_INVENTAR") {
                        msg.what = 29; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_QVEST_OBJ_INVENTAR") {
                        msg.what = 32; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает, уровень предмета
                    if (conf.getSOCKET_OUT() == "GET_LVL_OBJ_INVENTAR") {
                        msg.what = 35; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает, не последняя это вещь?
                    if (conf.getSOCKET_OUT() == "GET_PIC_OBJ_INVENTAR") {
                        msg.what = 36; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает, не последняя это вещь?
                    if (conf.getSOCKET_OUT() == "GET_INFO_OBJ_INVENTAR") {
                        msg.what = 18; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает, не последняя это вещь?
                    if (conf.getSOCKET_OUT() == "LAST_INFO_OBJ_INVENTAR") {
                        msg.what = 19; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое мак. значение здоровья
                    if (conf.getSOCKET_OUT() == "MAX_HP") {
                        msg.what = 25;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое мак. значение мани
                    if (conf.getSOCKET_OUT() == "MAX_MANA") {
                        msg.what = 26;//пришел ответ от сервера
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
                        conf.setSOCKET_MESSAGE("INVENTAR");
                    }
                    //сервер оповещает об ошибке запроса
                    if (cmd.equalsIgnoreCase("ERROR")) {
                        h.sendEmptyMessage(3);
                    }
                    //сервер ждет первой команды в инвентаре
                    if (cmd.equalsIgnoreCase("OK_INVENTAR")) {
                        conf.setSOCKET_MESSAGE("GET_OBJ_HERO"); //получить вещи которые одеты
                    }
                    //сервер готов отсылать данные о вещах на герое
                    if (cmd.equalsIgnoreCase("OK_GET_OBJ_HERO")) {
                        conf.setSOCKET_MESSAGE("HELMET");  //получаю новые данные о герое
                    }
                    //сервер записал всю инфу в массив и готов её передать
                    if (cmd.equalsIgnoreCase("YES_INFO_OBJ_HERO")) {
                        conf.setSOCKET_MESSAGE("GET_INFO_OBJ_HERO"); //получить вещи которые одеты
                    }
                    //сервер записал всю инфу в массив и готов её передать
                    if (cmd.equalsIgnoreCase("YES_NEXT_INFO_OBJ_HERO")) {
                        conf.setSOCKET_MESSAGE("GET_INFO_OBJ_HERO"); //получить вещи которые одеты
                    }
                    //сервер записал всю инфу в массив и готов её передать
                    if (cmd.equalsIgnoreCase("OK_OK_OK")) {
                        conf.setSOCKET_MESSAGE("GET_INVENTAR"); //получить вещи которые одеты
                    }
                    //сервер не нашел предметов в рюкзаке
                    if (cmd.equalsIgnoreCase("NO_OBJECTS")) {
                        h.sendEmptyMessage(14);
                    }
                    //сервер нашел предметы в инвентаре
                    if (cmd.equalsIgnoreCase("YES_OBJECTS")) {
                        conf.setSOCKET_MESSAGE("GET_ID_OBJ_INVENTAR");
                    }
                    //сервер записал всю инфу в массив и готов её передать
                    if (cmd.equalsIgnoreCase("YES_NEXT_INFO_OBJ_INVENTAR")) {
                        conf.setSOCKET_MESSAGE("GET_ID_OBJ_INVENTAR"); //получить вещи которые одеты
                    }
                    //сервер говорит, что есть свободное место в рюкзаке
                    if (cmd.equalsIgnoreCase("YES_FREE")) {
                        h.sendEmptyMessage(21);
                    }
                    //сервер говорит, что нет свободного места в рюкзаке
                    if (cmd.equalsIgnoreCase("NO_FREE")) {
                        h.sendEmptyMessage(23);
                    }
                    //сервер спрашивает, откуда снимать вещь
                    if (cmd.equalsIgnoreCase("WHO_SHOOT")) {
                        h.sendEmptyMessage(24);
                    }
                    //получилось снять вещь
                    if (cmd.equalsIgnoreCase("YES_SHOOT")) {
                        conf.setSOCKET_MESSAGE("GET");  //спрашиваю новые хар-ки героя
                    }
                    //получилось одеть вещь
                    if (cmd.equalsIgnoreCase("YES_WEAR")) {
                        conf.setSOCKET_MESSAGE("GET");  //спрашиваю новые хар-ки героя
                    }
                    if (cmd.equalsIgnoreCase("YES_THROW")) {
                        conf.setSOCKET_MESSAGE("GET");  //спрашиваю новые хар-ки героя
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("OK")) {
                        conf.setSOCKET_MESSAGE("MAX_HP"); //клиент спрашивает, кто первый напал
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("OK_OK_INVENTAR")) {
                        conf.setSOCKET_MESSAGE("INVENTAR"); //клиент спрашивает, кто первый напал
                    }
                    //серевер запрашивает предмет, который надо одеть или выбросить
                    if (cmd.equalsIgnoreCase("ID_OBJECT")) {
                        h.sendEmptyMessage(30);
                    }
                    //серевер запрашивает куда одеть предмет
                    if (cmd.equalsIgnoreCase("WHO_WEAR")) {
                        h.sendEmptyMessage(31);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                conf.setSOCKET_CONNECTED(false);
            }
        }
    }
}
