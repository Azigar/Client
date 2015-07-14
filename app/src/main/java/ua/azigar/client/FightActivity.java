package ua.azigar.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ua.azigar.client.Client.Fight;
import ua.azigar.client.Client.SocketConfig;
import ua.azigar.client.Resources.MyHero;
import ua.azigar.client.Resources.Progress_hp;
import ua.azigar.client.Resources.DialogScreen;
import ua.azigar.client.Resources.Progress_mana;


@SuppressWarnings("ResourceType")
public class FightActivity extends ActionBarActivity implements View.OnClickListener {

    Handler h, h_img_enemy;
    SocketConfig conf = new SocketConfig();
    Fight fight;
    AlertDialog dialog;
    Intent intent;
    SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:
    static final String PREF_LOCATION = "LOCATION";

    String id_Enemy, name_Enemy, hp_Enemy, mana_Enemy, lvl_Enemy; //стати противника

    static Timer timer;
    static int time = 0;
    String dialogMessage = "";

    //переменные для элементов
    static TextView СomentFight, TimerView, hodView, txtHod, heroView, enemyView, textView1, textView2, ComentConnect;
    static Button btnDefHead, btnDefBody, btnDefFeet, btnAtkHead, btnAtkBody, btnAtkFeet;
    static ProgressBar pbConnect, pbConnectEnter;
    static Progress_hp hpHero, hpEnemy;
    static Progress_mana manaHero, manaEnemy;
    static ImageView imgEnemy, imgHero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //убираю ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //убираю заголовок приложения
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //подключаем лауер-файл с элементами
        setContentView(R.layout.activity_fight);
        //определяю опонента в зависимости от выбора кнопки (читаю intent-данные от предыдущего активити)
        intent = getIntent();
        this.id_Enemy = intent.getStringExtra("id");
        conf.setID_ENEMY(id_Enemy);
        this.lvl_Enemy = intent.getStringExtra("lvl");
        this.hp_Enemy = intent.getStringExtra("hp");
        this.name_Enemy = intent.getStringExtra("name");
        this.mana_Enemy = intent.getStringExtra("mana");
           if (Integer.parseInt(mana_Enemy) == 0) {
               mana_Enemy = "1";
           }
        //подключаю глобальные переменные для хранения данных о герое
        final MyHero app = ((MyHero)getApplicationContext());
        //пишу данные переменные сокета
        conf.setID(app.getID()); //cохраняю ID игрока
        conf.setMANA(app.getMANA()); //сохраняю к-во маны героя
        conf.setHP(app.getHP()); //сохраняю к-во ОЗ героя

        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        conf.setLAST_LOCAL(sPref.getString(PREF_LOCATION, "ua.azigar.client.intent.action.LocalActivity1")); //пишу в конф. последнее активити
        intent = new Intent(conf.getLAST_LOCAL());

        //найдем View-элементы
        TimerView = (TextView) findViewById(R.id.txtTimer);
        heroView = (TextView) findViewById(R.id.nameHero1);
            heroView.setText(app.getNAME() + " [" + app.getLVL() + "]"); //вивод имени игрока
        enemyView = (TextView) findViewById(R.id.nameHero2);
            enemyView.setText(name_Enemy + " [" + lvl_Enemy + "]"); //вивод имени противника
        hodView = (TextView) findViewById(R.id.txtHod);
        txtHod = (TextView) findViewById(R.id.txtHodView);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        СomentFight = (TextView) findViewById(R.id.txtComentFight);
        ComentConnect = (TextView) findViewById(R.id.txtComentConnect);
        btnDefHead = (Button) findViewById(R.id.btnDefHead);
        btnDefBody = (Button) findViewById(R.id.btnDefBody);
        btnDefFeet = (Button) findViewById(R.id.btnDefFeet);
        btnAtkHead = (Button) findViewById(R.id.btnAtkHead);
        btnAtkBody = (Button) findViewById(R.id.btnAtkBody);
        btnAtkFeet = (Button) findViewById(R.id.btnAtkFeet);
        pbConnect = (ProgressBar) findViewById(R.id.pbConnect);
        pbConnectEnter = (ProgressBar) findViewById(R.id.pbConnectEnter);
        hpHero = (Progress_hp) findViewById(R.id.hpHero1);
            hpHero.setMaxValue(Integer.parseInt(app.getMAX_HP()));
            hpHero.setValue(Integer.parseInt(app.getHP()));
        hpEnemy = (Progress_hp) findViewById(R.id.hpHero2);
            hpEnemy.setMaxValue(Integer.parseInt(hp_Enemy));
            hpEnemy.setValue(Integer.parseInt(hp_Enemy));
        manaHero = (Progress_mana) findViewById(R.id.manaHero1);
            manaHero.setMaxValue(Integer.parseInt(app.getMAX_MANA()));
            manaHero.setValue(Integer.parseInt(app.getMANA()));
        manaEnemy = (Progress_mana) findViewById(R.id.manaHero2);
            manaEnemy.setMaxValue(Integer.parseInt(mana_Enemy));
            manaEnemy.setValue(Integer.parseInt(mana_Enemy));
        imgEnemy = (ImageView) findViewById(R.id.imgEnemy);
            ImgEnemy(Integer.parseInt(id_Enemy));
        imgHero = (ImageView) findViewById(R.id.imgHero);
            imgHero.setImageResource(R.drawable.men);

        CloseBtnAtk(); CloseBtnDef(); //Закриваем все кнопочки
        DisableAll(); //прячем все обьекти кроме прогрессБара и комента

        // присваиваем обработчик кнопкам
        btnDefHead.setOnClickListener(this);
        btnDefBody.setOnClickListener(this);
        btnDefFeet.setOnClickListener(this);
        btnAtkHead.setOnClickListener(this);
        btnAtkBody.setOnClickListener(this);
        btnAtkFeet.setOnClickListener(this);

        h = new Handler() {
            Thread t;
            String cmd;
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:  //подключение к серверу успешно
                        conf.setSOCKET_MESSAGE("ENTER");
                        break;

                    case 2:  //Не удалось подключится к серверу
                        Toast.makeText(FightActivity.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        //finish(); //закрываю активити
                        break;

                    case 3:  //Связь разорвалась
                        Toast.makeText(FightActivity.this, R.string.No_connected, Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        //finish(); //закрываю активити
                        break;

                    case 4: //если первый ход игрока
                        Downloaded(); //открываю окно боя
                        txtHod(app.getNAME());  //текстовка о том, кто первый напал
                        TxtFist(app.getNAME(), name_Enemy);  //комент
                        OpenBtnAtk();  //открываю кнопки на атаку
                        Timer(1); //запуск таймера для первого игрока
                        break;

                    case 5:  //если первый ход опонента
                        Downloaded(); //открываю окно боя
                        txtHod(name_Enemy);  //текстовка о том, кто первый напал
                        TxtFist(name_Enemy, app.getNAME()); //комент
                        OpenBtnDef();  //открываю кнопки на защиту
                        Timer(2); //запуск таймера для первого игрока
                        break;

                    case 6:  //пришло ОЗ противника после удара
                        hpEnemy.setValue(msg.arg1);
                        conf.setSOCKET_MESSAGE("COMMENT_ATK");  //запрашиваю комент
                        break;

                    case 7:  //пришло ОЗ игрока после удара опонента
                        hpHero.setValue(msg.arg1);
                        conf.setSOCKET_MESSAGE("COMMENT_DEF");  //запрашиваю комент
                        break;

                    case 8:  //пришел комент после удара игрока
                        cmd = (String) msg.obj;  //пишу комент в переменную
                        //если противник погиб
                        if (Death(Integer.parseInt(conf.getHP_ENEMY()), app.getNAME(), name_Enemy, cmd)==true){  //определяю смерть игрока
                            //ожидаю 3 сек, что бы клиент ознакомился с коментом
                            t = new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        TimeUnit.SECONDS.sleep(3);
                                        conf.setSOCKET_MESSAGE("VICTORY");  //оповещаю сервер о смерти противника
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            t.start();
                            pbConnect.setVisibility(View.VISIBLE);  //включаю прогрессБар
                        }else{
                            СomentFight.setText("" + msg.obj); //комент
                            txtHod(name_Enemy); // Вывести что ход соперника
                            OpenBtnDef(); // включить кнопки на защиту
                            pbConnect.setVisibility(View.GONE); //выключаю прогрессБар
                            Timer(2);  //запуск таймера для второго игрока
                        }
                        break;

                    case 9:   //пришлел комент о победе
                        dialogMessage = (String) msg.obj;
                        dialog = DialogScreen.getDialog(FightActivity.this, DialogScreen.DIALOG_PLAYER_VICTORY, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 10:  //пришот ответ на лвл-апп
                        cmd = (String) msg.obj;
                        if (cmd.equalsIgnoreCase("NO_LVL_UP")) {
                            conf.setSOCKET_MESSAGE("UP_PVP");
                        } else {
                            dialogMessage = cmd;
                            dialog = DialogScreen.getDialog(FightActivity.this, DialogScreen.DIALOG_PLAYER_LVL_UP, conf, dialogMessage);
                            dialog.show();
                        }
                        break;

                    case 11:  //пришот ответ на pvp-апп
                        cmd = (String) msg.obj;
                        if (cmd.equalsIgnoreCase("NO_PVP_UP")) {
                            conf.setSOCKET_MESSAGE("REGEN");
                        } else {
                            dialogMessage = cmd;
                            dialog = DialogScreen.getDialog(FightActivity.this, DialogScreen.DIALOG_PLAYER_PVP_UP, conf, dialogMessage);
                            dialog.show();
                        }
                        break;

                    case 12:   //сервер прислал уровень героя
                        app.setLVL((String) msg.obj);
                        conf.setSOCKET_MESSAGE("TITLE"); //запрашиваю звание героя
                        break;

                    case 13:   //сервер прислал звание героя
                        app.setTITLE((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MAX_EXP"); //запрашиваю мак. значение опыта героя
                        break;

                    case 14:   //сервер говорит мак. к-во опыта игрока
                        app.setMAX_EXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("EXP"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 15:   //сервер говорит текущее к-во опыта игрока
                        app.setEXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MAX_PVP_EXP"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 16:   //сервер говорит мак. к-во pvp-опыта героя
                        app.setMAX_PVP_EXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("PVP_EXP"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 17:   //сервер говорит текущее к-во pvp-опыта героя
                        app.setPVP_EXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MAX_HP"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 18:   //сервер говорит мак. к-во ОЗ героя
                        app.setMAX_HP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("HP"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 19:   //сервер говорит текущее к-во ОЗ героя
                        app.setHP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MAX_MANA"); //запрашиваю к-во маны героя
                        break;

                    case 20:   //сервер мак. к-во мани героя
                        app.setMAX_MANA((String) msg.obj);
                        if (Integer.parseInt(app.getMAX_MANA()) == 0) {
                            app.setMAX_MANA("1");
                        }
                        conf.setSOCKET_MESSAGE("MANA"); //запрашиваю к-во маны героя
                        break;

                    case 21:   //сервер говорит к-во маны героя
                        app.setMANA((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MONEY"); //запрашиваю к-во монет героя
                        break;

                    case 22:   //сервер говорит к-во монет игрока
                        app.setMONEY((String) msg.obj);
                        conf.setSOCKET_MESSAGE("GOLD"); //запрашиваю к-во голда у героя
                        break;

                    case 23:   //сервер говорит к-во голда игрока
                        app.setGOLD((String) msg.obj);
                        conf.setSOCKET_MESSAGE("END"); //закрываю активити боя
                        startActivity(intent);
                        //finish(); //закрываю активити
                        break;

                    case 24:   //сервер говорит цену за востановление
                        cmd = (String) msg.obj;
                        dialogMessage = "Боги могут исцелить Вашего героя за " + cmd;
                        dialog = DialogScreen.getDialog(FightActivity.this, DialogScreen.DIALOG_PLAYER_SICK, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 25:   //пришел комент после удара противника
                        cmd = (String) msg.obj;
                        //если погиб игрок
                        if (Death(Integer.parseInt(conf.getHP()), name_Enemy, app.getNAME(), cmd)==true){  //определяю смерть игрока
                            //ожидаю 2 сек, что бы клиент ознакомился с коментом удара
                            t = new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        TimeUnit.SECONDS.sleep(3);
                                        conf.setSOCKET_MESSAGE("LOSE");  //оповещаю сервер о смерти игрока
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            t.start();
                            pbConnect.setVisibility(View.VISIBLE);  //включаю прогрессБар
                        }else{
                            СomentFight.setText("" + msg.obj);
                            txtHod(app.getNAME()); // Вывести что ход соперника
                            OpenBtnAtk(); // включить кнопки на атаку
                            pbConnect.setVisibility(View.GONE);
                            Timer(1);  //запуск таймера для второго игрока
                        }
                        break;


                    case 26:   //пришлел комент о поражении
                        dialogMessage = (String) msg.obj;
                        dialog = DialogScreen.getDialog(FightActivity.this, DialogScreen.DIALOG_PLAYER_LOSE, conf, dialogMessage);
                        dialog.show();
                        break;


                }
            }
        };
        fight = new Fight(h, conf, app.getID(), id_Enemy); //запуск клиента для боя с параметрами на екземпляр хандлера и конфига
    }

    //смерть опонента
    private boolean Death(int hp, String nameAtk, String nameDef, String comm){
        boolean death = false;
        //если защитника убили
        if (hp == 0) {
            StopTimer();
            TimerView.setText("");  //очищаю значение TimerView
            pbConnect.setVisibility(View.GONE);
            CloseBtnAtk();
            CloseBtnDef();
            int txt = randTxt();
            switch (txt) {
                case 1:
                    СomentFight.setText(comm + ". "+nameAtk + " убил " + nameDef);
                    break;
                case 2:
                    СomentFight.setText(comm + ". "+nameDef + " погиб");
                    break;
            }
            death = true;
        }
        return death;
    }

    static void Downloaded() {
        EnableAll();  //показываю все обьекты
        DisableLoad(); //прячу обьекты закрузки
    }

    //прячу все элементы
    static void DisableAll() {
        textView1.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        heroView.setVisibility(View.GONE);
        enemyView.setVisibility(View.GONE);
        txtHod.setVisibility(View.GONE);
        hodView.setVisibility(View.GONE);
        btnDefHead.setVisibility(View.GONE);
        btnDefBody.setVisibility(View.GONE);
        btnDefFeet.setVisibility(View.GONE);
        btnAtkHead.setVisibility(View.GONE);
        btnAtkBody.setVisibility(View.GONE);
        btnAtkFeet.setVisibility(View.GONE);
        hpHero.setVisibility(View.GONE);
        hpEnemy.setVisibility(View.GONE);
        manaHero.setVisibility(View.GONE);
        manaEnemy.setVisibility(View.GONE);
        pbConnect.setVisibility(View.GONE);
        СomentFight.setVisibility(View.GONE);
        imgEnemy.setVisibility(View.GONE);
        imgHero.setVisibility(View.GONE);
    }

    //прячу все элементы
    static void DisableLoad() {
        pbConnectEnter.setVisibility(View.GONE);
        ComentConnect.setVisibility(View.GONE);
    }

    //показываю все элементы активити боя
    static void EnableAll() {
        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);
        heroView.setVisibility(View.VISIBLE);
        enemyView.setVisibility(View.VISIBLE);
        txtHod.setVisibility(View.VISIBLE);
        hodView.setVisibility(View.VISIBLE);
        btnDefHead.setVisibility(View.VISIBLE);
        btnDefBody.setVisibility(View.VISIBLE);
        btnDefFeet.setVisibility(View.VISIBLE);
        btnAtkHead.setVisibility(View.VISIBLE);
        btnAtkBody.setVisibility(View.VISIBLE);
        btnAtkFeet.setVisibility(View.VISIBLE);
        hpHero.setVisibility(View.VISIBLE);
        hpEnemy.setVisibility(View.VISIBLE);
        manaHero.setVisibility(View.VISIBLE);
        manaEnemy.setVisibility(View.VISIBLE);
        СomentFight.setVisibility(View.VISIBLE);
        imgEnemy.setVisibility(View.VISIBLE);
        imgHero.setVisibility(View.VISIBLE);
    }

    //текстовка о том, кто первый напал
    static void TxtFist(String nameAtk, String nameDef) {
        int txt = randTxt();
        switch (txt) {
            case 1:
                СomentFight.setText(nameAtk + " яросно бросился на " + nameDef);
                break;
            case 2:
                СomentFight.setText(nameAtk + " первым настиг " + nameDef);
                break;
        }
    }

    //рандом текста
    private static int randTxt() {
        Random random = new Random();
        return random.nextInt(2) + 1;
    }

    private void ThreeSeconds() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    //обработка кнопки назад
    @Override
    public void onBackPressed () {
        //ничего не делать
    }

    //обработка при уничтожении активити
    @Override
    protected void onDestroy() {
        //conf.setSOCKET_MESSAGE("END");
        super.onDestroy();
    }

    //обработчик нажатий на кнопки
    @Override
    public void onClick(View v) {
        // по id определеяем кнопку, вызвавшую этот обработчик
        switch (v.getId()) {
            case R.id.btnDefHead:
                Def();
                conf.setSOCKET_MESSAGE("DEF_1");    //отправляю команду с направление защиты
                break;
            case R.id.btnDefBody:
                Def();
                conf.setSOCKET_MESSAGE("DEF_2");
                break;
            case R.id.btnDefFeet:
                Def();
                conf.setSOCKET_MESSAGE("DEF_3");
                break;
            case R.id.btnAtkHead:
                Atk();
                conf.setSOCKET_MESSAGE("ATK_1");  //отправляю команду с направление атаки
                break;
            case R.id.btnAtkBody:
                Atk();
                conf.setSOCKET_MESSAGE("ATK_2");
                break;
            case R.id.btnAtkFeet:
                Atk();
                conf.setSOCKET_MESSAGE("ATK_3");
                break;
         }
    }

    //метод таймера с остановкой в нужное время
    void Timer(final int i){
        timer = new Timer();  //подключаю класс Timer
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        if (time==10){  //если время ожидания прошло
                            if (i==1){  //если таймер запустил второй игрок для первого
                                Atk(); //метод для кнопки атаки с рандомным выбором направления
                                switch (randUdar()) {
                                    case 1:
                                        conf.setSOCKET_MESSAGE("ATK_1");    //отправляю команду с направление защиты
                                        break;
                                    case 2:
                                        conf.setSOCKET_MESSAGE("ATK_2");    //отправляю команду с направление защиты
                                        break;
                                    case 3:
                                        conf.setSOCKET_MESSAGE("ATK_3");    //отправляю команду с направление защиты
                                        break;
                                }
                            } else {
                                Def(); //метод для кнопки защиты с рандомным выбором направления
                                switch (randUdar()) {
                                    case 1:
                                        conf.setSOCKET_MESSAGE("DEF_1");    //отправляю команду с направление защиты
                                        break;
                                    case 2:
                                        conf.setSOCKET_MESSAGE("DEF_2");    //отправляю команду с направление защиты
                                        break;
                                    case 3:
                                        conf.setSOCKET_MESSAGE("DEF_3");    //отправляю команду с направление защиты
                                        break;
                                }
                            }
                        }
                        TimerView.setText("");
                        TimerView.setText(TimerView.getText() + " " + time);
                    }
                });
            }
        }, 0,1000); //0-сразу начать отсчет, 1000 - через 1000 милисекунд запускать run()
    }

    //текстовка Начало нового хода
    private static void txtHod(String name) {
        hodView.setText(name);
    }

    //Включить на атаку
    static void OpenBtnAtk(){
        btnAtkHead.setEnabled(true);
        btnAtkBody.setEnabled(true);
        btnAtkFeet.setEnabled(true);
    }

    //Выключить кнопки на атаку
    static void CloseBtnAtk(){
        btnAtkHead.setEnabled(false);
        btnAtkBody.setEnabled(false);
        btnAtkFeet.setEnabled(false);
    }

    //Включить кнопки на защиту
    static void OpenBtnDef(){
        btnDefHead.setEnabled(true);
        btnDefBody.setEnabled(true);
        btnDefFeet.setEnabled(true);
    }

    //Выключить кнопки на защиту
    static void CloseBtnDef(){
        btnDefHead.setEnabled(false);
        btnDefBody.setEnabled(false);
        btnDefFeet.setEnabled(false);
    }

    //метод для кнопок атаки
    void Atk() {
        StopTimer(); //остановка таймера
        TimerView.setText("");  //очищаю значение TimerView
        pbConnect.setVisibility(View.VISIBLE);  //включаю прогрессБар
        CloseBtnAtk();  //Выключить кнопки на защиту
    }

    //метод для кнопок защиты
    void Def(){
        StopTimer();  //остановка таймера
        TimerView.setText("");  //очищаю значение TimerView
        pbConnect.setVisibility(View.VISIBLE);  //включаю прогрессБар
        CloseBtnDef();  //Выключить кнопки на защиту
    }

    //события для остановки таймера
    static void StopTimer(){
        timer.cancel(); //останавливаю таймер
        TimerView.setText(""); //очищаю View-элемент
        time=0; //обнуляю счетчик таймера
    }

    //рандом направление атаки/защиты
    private static int randUdar() {
        Random random = new Random();
        return random.nextInt(3) + 1;
    }

    //рандом направление атаки/защиты
    private static void ImgEnemy(int id) {
        switch (id) {
            case 1:  //попытка подключение к серверу
                imgEnemy.setImageResource(R.drawable.bot_1);
                break;

            case 2:  //попытка подключение к серверу
                imgEnemy.setImageResource(R.drawable.bot_2);
                break;

            case 4:  //попытка подключение к серверу
                imgEnemy.setImageResource(R.drawable.bot_4);
                break;
        }
    }
}

