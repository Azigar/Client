package ua.azigar.client.Client;

import android.app.Activity;
import android.app.ProgressDialog;
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
 * Created by Azigar on 15.07.2015.
 * класс для исцеление героя. Происходит сокет-конект к серверу и запись в БД мак. значений ОЗ и маны.
 */
public class Healing extends Thread {
    BufferedReader in;
    Socket socket;
    SocketConfig conf;
    Handler h;
    private ProgressDialog progressDialog;
    private Activity activity;

    public Healing (Handler h1, SocketConfig con, Activity act) {  //главный
        this.h = h1;
        this.conf = con;
        conf.setSOCKET_CONNECTED(false);
        this.activity = act;
        //создаю прогресс-далог
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(conf.getNAME() + " молится богам...");
        progressDialog.show();
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
                progressDialog.dismiss();  //закрываю диалог
                conf.setSOCKET_CONNECTED(false);
            } catch (IOException e) {
                e.printStackTrace();
                h.sendEmptyMessage(2);  //отсылаю в UI сообщение
                progressDialog.dismiss(); //закрываю диалог
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
        while (conf.getSOCKET_CONNECTED()==true) {
            Message msg = new Message();  //создаю меседж-обьект
            try {
                //переменная для получение данных
                String cmd = new String(in.readLine());

                if (cmd.equalsIgnoreCase(null)) { //если нет сообщения от сервера
                    socket.close(); //закрываю сокет
                    threadOUT.interrupt(); //закрываю второй поток
                    h.sendEmptyMessage(2);
                    progressDialog.dismiss(); //закрываю диалог
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
                    //запрашиваю новое значение здоровья
                    if (conf.getSOCKET_OUT() == "HP") {
                        msg.what = 7;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое мак. значение мани
                    if (conf.getSOCKET_OUT() == "MANA") {
                        msg.what = 8;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю новое мак. значение ПвП-опыта
                    if (conf.getSOCKET_OUT() == "MONEY") {
                        msg.what = 6;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //запрашиваю стоимость исцеление
                    if (conf.getSOCKET_OUT() == "GOLD") {
                        msg.what = 4;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                        progressDialog.dismiss(); //закрываю диалог
                        break;
                    }
                    //запрашиваю стоимость исцеление
                    if (conf.getSOCKET_OUT() == "PRICE") {
                        msg.what = 3;//пришел ответ от сервера
                        msg.obj = cmd;
                        h.sendMessage(msg);
                    }
                    //согласен оплатить исцеление
                    if (conf.getSOCKET_OUT() == "YES" && cmd.equalsIgnoreCase("YES_PAY")) {
                        conf.setSOCKET_MESSAGE("GET");  //спрашиваю цену за востановление

                    }
                    //не согласен оплатить исцеление
                    if (conf.getSOCKET_OUT() == "NO" && cmd.equalsIgnoreCase("GOOD")) {
                        progressDialog.dismiss(); //закрываю диалог
                        h.sendEmptyMessage(9);
                        break;
                    }
                    //сервер потверждает, что клиент ранен может оплатить исцеление
                    if (cmd.equalsIgnoreCase("REGEN")) {
                        conf.setSOCKET_MESSAGE("PRICE");  //спрашиваю цену за востановление

                    }
                    //сервер е смог ничего сделать
                    if (cmd.equalsIgnoreCase("NO_REGEN")) {
                        progressDialog.dismiss(); //закрываю диалог
                        h.sendEmptyMessage(9);

                    }
                    //сервер говорит, что у героя нет денег на исцеление
                    if (cmd.equalsIgnoreCase("NO_MONEY")) {
                        progressDialog.dismiss(); //закрываю диалог
                        h.sendEmptyMessage(5);
                    }
                    ////КОМАНДЫ СЕРВЕРА
                    //сервер запрашивает ID игрока
                    if (cmd.equalsIgnoreCase("ID_PLAEYR")) {
                        conf.setSOCKET_MESSAGE(conf.getID());
                    }
                    if (cmd.equalsIgnoreCase("YES_HERO")) {
                        conf.setSOCKET_MESSAGE("SAVE_GAME");
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
                        conf.setSOCKET_MESSAGE("REGEN");
                    }
                    //сервер "согласен" отвечать на вопросы по GET - теперь клиент будет задавать вопросы
                    if (cmd.equalsIgnoreCase("OK")) {
                        conf.setSOCKET_MESSAGE("HP"); //клиент спрашивает, кто первый напал
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                conf.setSOCKET_CONNECTED(false);
            }
        }
    }

}
