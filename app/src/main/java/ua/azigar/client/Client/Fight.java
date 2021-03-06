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
 * Created by Azigar on 17.06.2015.
 */
public class Fight extends Thread {

    BufferedReader in;
    Socket socket;
    Handler h;
    SocketConfig conf;

    public Fight(Handler h1, SocketConfig conf) {
        this.h = h1;
        this.conf = conf;
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
                        threadOUT.interrupt(); //закрываю второй поток
                        socket.close(); //закрываю сокет
                        break;
                    }
                    //клиент спрашивает чей первый ход в пользу игрока
                    if (conf.getSOCKET_OUT() == "WHO_FIRST" && cmd.equalsIgnoreCase("FIRST_PLAYER")) {
                        msg.what = 4; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает чей первый ход в пользу опонента
                    if (conf.getSOCKET_OUT() == "WHO_FIRST" && cmd.equalsIgnoreCase("FIRST_ENEMY")) {
                        msg.what = 5; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает ОЗ опонента после нанесение удара
                    if (conf.getSOCKET_OUT() == "ATK_1" ||
                            conf.getSOCKET_OUT() == "ATK_2" ||
                                conf.getSOCKET_OUT() == "ATK_3") {
                        msg.what = 6; //пришел ответ от сервера
                        msg.arg1 = Integer.parseInt(cmd);
                        conf.setHP_ENEMY(cmd); //записываю значение ОЗ противника в сеттер
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает ОЗ игрока после нанесение удара опонентом
                    if (conf.getSOCKET_OUT() == "DEF_1" ||
                            conf.getSOCKET_OUT() == "DEF_2" ||
                                conf.getSOCKET_OUT() == "DEF_3") {
                        msg.what = 7; //пришел ответ от сервера
                        conf.setHP(cmd); //записываю значение ОЗ игрока в сеттер
                        msg.arg1 = Integer.parseInt(cmd);
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает коментарий после нанесение удара игроком
                    if (conf.getSOCKET_OUT() == "COMMENT_ATK") {
                        msg.what = 8; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент спрашивает коментарий после нанесение удара противником
                    if (conf.getSOCKET_OUT() == "COMMENT_DEF") {
                        msg.what = 25; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент проиграл бой
                    if ((conf.getSOCKET_OUT() == "LOSE")) {
                        msg.what = 26; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //клиент победил в бою
                    if (conf.getSOCKET_OUT() == "VICTORY") {
                        msg.what = 9; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //повысил ли клиент уровень
                    if (conf.getSOCKET_OUT() == "UP_LVL") {
                        msg.what = 10; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //повысил ли клиент звание
                    if (conf.getSOCKET_OUT() == "UP_PVP") {
                        msg.what = 11; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новый уровень
                    if (conf.getSOCKET_OUT() == "LVL") {
                        msg.what = 12; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новый уровень
                    if (conf.getSOCKET_OUT() == "PVP_LVL") {
                        msg.what = 27; //пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое звание
                    if (conf.getSOCKET_OUT() == "TITLE") {
                        msg.what = 13;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое мак. значение опыта
                    if (conf.getSOCKET_OUT() == "MAX_EXP") {
                        msg.what = 14;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                   //запрашиваю значение опыта
                    if (conf.getSOCKET_OUT() == "EXP") {
                        msg.what = 15;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое мак. значение ПвП-опыта
                    if (conf.getSOCKET_OUT() == "MAX_PVP_EXP") {
                        msg.what = 16;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое мак. значение ПвП-опыта
                    if (conf.getSOCKET_OUT() == "PVP_EXP") {
                        msg.what = 17;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое мак. значение здоровья
                    if (conf.getSOCKET_OUT() == "MAX_HP") {
                        msg.what = 18;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое значение здоровья
                    if (conf.getSOCKET_OUT() == "HP") {
                        msg.what = 19;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое мак. значение мани
                    if (conf.getSOCKET_OUT() == "MAX_MANA") {
                        msg.what = 20;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое мак. значение мани
                    if (conf.getSOCKET_OUT() == "MANA") {
                        msg.what = 21;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое мак. значение ПвП-опыта
                    if (conf.getSOCKET_OUT() == "MONEY") {
                        msg.what = 22;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю к-во голда
                    if (conf.getSOCKET_OUT() == "GOLD") {
                        msg.what = 23;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                        conf.setSOCKET_MESSAGE("END"); //закрываю сокет-поток
                    }
                    //запрашиваю стоимость исцеление
                    if (conf.getSOCKET_OUT() == "PRICE") {
                        msg.what = 24;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //согласен или не согласен оплатить исцеление
                    if ((conf.getSOCKET_OUT() == "YES" || conf.getSOCKET_OUT() == "NO") && cmd.equalsIgnoreCase("GOOD")) {
                        conf.setSOCKET_MESSAGE("GET");  //получаю новые данные о герое
                    }

                    ////КОМАНДЫ СЕРВЕРА
                    //сервер потверждает, что клиент ранен может оплатить исцеление
                    if (cmd.equalsIgnoreCase("REGEN")) {
                        conf.setSOCKET_MESSAGE("PRICE");  //спрашиваю цену за востановление
                    }
                    //сервер говорит, что герой либо здоров или у него нет денег
                    if (cmd.equalsIgnoreCase("NO_REGEN") || cmd.equalsIgnoreCase("NO_MONEY")) {
                        conf.setSOCKET_MESSAGE("GET");  //спрашиваю новые хар-ки героя
                    }
                    //сервер спрашивает ID игрока
                    if (cmd.equalsIgnoreCase("ID_PLAEYR")){
                        conf.setSOCKET_MESSAGE(conf.getID()); //отправляю ID игрока
                    }
                    //сервер готов авторизировать клиента
                    if (cmd.equalsIgnoreCase("YES_HERO")){
                        conf.setSOCKET_MESSAGE("SAVE_GAME"); //лтправляюактуальные значения хп и маны
                    }
                    //сервер запрашивает HP героя
                    if (cmd.equalsIgnoreCase("HP")) {
                        conf.setSOCKET_MESSAGE(conf.getHP());
                    }
                    //сервер запрашивает ману героя
                    if (cmd.equalsIgnoreCase("MANA")) {
                        conf.setSOCKET_MESSAGE(conf.getMANA());
                    }
                    //сервер спрашивает, что хочет клиент
                    if (cmd.equalsIgnoreCase("SAVE")) {
                        conf.setSOCKET_MESSAGE("FIGHT_ACTIVITY");
                    }
                    //сервер спрашивает ID противника
                    if (cmd.equalsIgnoreCase("ID_ENEMY")){
                        conf.setSOCKET_MESSAGE(conf.getID_ENEMY()); //отправляю ID противника
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("YOUR_QUESTIONS")) {
                        conf.setSOCKET_MESSAGE("WHO_FIRST"); //клиент спрашивает, кто первый напал
                    }
                    //теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("OK")) {
                        conf.setSOCKET_MESSAGE("LVL"); //клиент спрашивает, кто первый напал
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                conf.setSOCKET_CONNECTED(false);
            }
        }
    }
}


