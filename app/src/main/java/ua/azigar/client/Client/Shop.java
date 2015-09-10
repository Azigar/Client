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
 * Created by Azigar on 19.08.2015.
 */
public class Shop extends Thread {

    BufferedReader in;
    Socket socket;
    SocketConfig conf;
    Handler h;

    public Shop(Handler h1, SocketConfig con) {  //главный
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
                    if (conf.getSOCKET_OUT() == "GET_ID_OBJ_SHOP") {
                        msg.what = 4; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_INFO_OBJ_SHOP") {
                        msg.what = 5; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_PVP_OBJ_SHOP") {
                        msg.what = 6; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_VIP_OBJ_SHOP") {
                        msg.what = 7; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_LVL_OBJ_SHOP") {
                        msg.what = 8; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_TITLE_OBJ_SHOP") {
                        msg.what = 12; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_PIC_OBJ_SHOP") {
                        msg.what = 9; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_MONEY_OBJ_SHOP") {
                        msg.what = 10; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_GOLD_OBJ_SHOP") {
                        msg.what = 11; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "LAST_OBJ_SHOP") {
                        msg.what = 13; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "FREE_INVENTAR") {
                        msg.what = 14; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "SIZE_INVENTAR") {
                        msg.what = 15; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во монет игрока
                    if (conf.getSOCKET_OUT() == "MONEY") {
                        msg.what = 16; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает к-во голда игрока
                    if (conf.getSOCKET_OUT() == "GOLD") {
                        msg.what = 17; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_ID_INVENTAR") {
                        msg.what = 19; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    if (conf.getSOCKET_OUT() == "GET_QVEST_OBJ_INVENTAR") {
                        msg.what = 20; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает, уровень предмета
                    if (conf.getSOCKET_OUT() == "GET_LVL_OBJ_INVENTAR") {
                        msg.what = 21; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает, не последняя это вещь?
                    if (conf.getSOCKET_OUT() == "GET_PIC_OBJ_INVENTAR") {
                        msg.what = 22; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает, не последняя это вещь?
                    if (conf.getSOCKET_OUT() == "GET_COUNT_OBJ_INVENTAR") {
                        msg.what = 25; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает, не последняя это вещь?
                    if (conf.getSOCKET_OUT() == "GET_INFO_OBJ_INVENTAR") {
                        msg.what = 23; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает, не последняя это вещь?
                    if (conf.getSOCKET_OUT() == "LAST_INFO_OBJ_INVENTAR") {
                        msg.what = 24; //пришел ответ от сервера
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
                        conf.setSOCKET_MESSAGE("SHOP");
                    }
                    //сервер оповещает об ошибке запроса
                    if (cmd.equalsIgnoreCase("ERROR")) {
                        h.sendEmptyMessage(3);
                    }
                    //сервер запрашивает магазин
                    if (cmd.equalsIgnoreCase("WHO_SHOP")) {
                        conf.setSOCKET_MESSAGE(conf.getSHOP());
                    }
                    if (cmd.equalsIgnoreCase("OK_SHOP")) {
                        conf.setSOCKET_MESSAGE("FREE_INVENTAR");
                    }
                    if (cmd.equalsIgnoreCase("YES_SHOP")) {
                        conf.setSOCKET_MESSAGE("GET_ID_OBJ_SHOP");
                    }
                    if (cmd.equalsIgnoreCase("YES_NEXT_OBJ_SHOP")) {
                        conf.setSOCKET_MESSAGE("GET_ID_OBJ_SHOP");
                    }
                    if (cmd.equalsIgnoreCase("ID_BUY")) {
                        conf.setSOCKET_MESSAGE(conf.getID_OBJ());
                    }
                    if (cmd.equalsIgnoreCase("ID_INVENTAR")) {
                        conf.setSOCKET_MESSAGE(conf.getID_OBJ());
                    }
                    if (cmd.equalsIgnoreCase("YES_BUY")) {
                        conf.setSOCKET_MESSAGE("GET_HERO");
                    }
                    if (cmd.equalsIgnoreCase("OK_GET_HERO")) {
                        conf.setSOCKET_MESSAGE("GET"); //получаю новые данные о герое
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("OK")) {
                        conf.setSOCKET_MESSAGE("MONEY");
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("OK_OK_SHOP")) {
                        conf.setSOCKET_MESSAGE("SHOP");
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("OK_OK_INVENTAR")) {
                        conf.setSOCKET_MESSAGE("INVENTAR"); //клиент спрашивает, кто первый напал
                    }
                    //сервер ждет первой команды в инвентаре
                    if (cmd.equalsIgnoreCase("OK_INVENTAR")) {
                        conf.setSOCKET_MESSAGE("GET_INVENTAR"); //получить вещи которые одеты
                    }
                    //сервер не нашел предметов в рюкзаке
                    if (cmd.equalsIgnoreCase("NO_OBJECTS")) {
                        h.sendEmptyMessage(18);
                    }
                    //сервер нашел предметы в инвентаре
                    if (cmd.equalsIgnoreCase("YES_OBJECTS")) {
                        conf.setSOCKET_MESSAGE("GET_ID_INVENTAR");
                    }
                    //сервер записал всю инфу в массив и готов её передать
                    if (cmd.equalsIgnoreCase("YES_NEXT_INFO_OBJ_INVENTAR")) {
                        conf.setSOCKET_MESSAGE("GET_ID_INVENTAR"); //получить вещи которые одеты
                    }
                    //сервер спрашивает имя героя для востановления пароля
                    if (cmd.equalsIgnoreCase("COUNT")) {
                        h.sendEmptyMessage(26);
                    }
                    //сервер спрашивает имя героя для востановления пароля
                    if (cmd.equalsIgnoreCase("ERROR_COUNT")) {
                        h.sendEmptyMessage(27);
                    }
                    if (cmd.equalsIgnoreCase("YES_SALE")) {
                        conf.setSOCKET_MESSAGE("GET_HERO");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                conf.setSOCKET_CONNECTED(false);
            }
        }
    }
}
