package ua.azigar.client;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ua.azigar.client.Client.Quest;
import ua.azigar.client.Resources.Database;
import ua.azigar.client.Resources.DialogScreen;
import ua.azigar.client.Resources.MyHero;
import ua.azigar.client.Resources.SocketConfig;
import ua.azigar.client.Resources.TxtQuests;

/**
 * Created by Azigar on 03.09.2015.
 * клас для отображения диалога квеста
 */

public class QuestActivity extends ActionBarActivity {

    static ImageView imageView;
    static Button btnGetQuest, btnCancel;
    static TextView data_checking, txtQuest;
    static Handler h;
    static ProgressBar pbConnect;
    static SocketConfig conf = new SocketConfig();
    static Intent intent;
    static SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:
    static final String PREF_LOCATION = "LOCATION";
    static AlertDialog dialog;
    static int isNew, idQuest;
    static String nps;
    static TxtQuests txt = new TxtQuests();
    static Quest quest;
    static AlertDialog.Builder builder;
    static ArrayList<Integer> taken; //массыв для хранение информации о взятых квестах
    static ArrayList<Integer> executed; //массыв для хранение информации о выполнении взятых квестов
    static Database db;
    static ContentValues cv = new ContentValues(); // создаем объект для данных (пара: ключ - значение)
    static boolean isUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //убираю ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //убираю заголовок приложения
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //подключаем лауер-файл с элементами
        setContentView(R.layout.activity_quest);

        db = new Database(this); //подключаю класс
        db.open();
        taken = new ArrayList<Integer>();
        executed = new ArrayList<Integer>();

        //читаю intent-данные от предыдущего активити)
        intent = getIntent();
        nps = intent.getStringExtra("nps");  //сохраняю NPS
        idQuest = intent.getIntExtra("idQuest", 1);
        conf.setID_QUEST(String.valueOf(idQuest));
        isNew = intent.getIntExtra("isNew", 1);

        //подключаю глобальные переменные для хранения данных о герое
        final MyHero app = ((MyHero) getApplicationContext());

        //пишу в конф. последнее активити локации
        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        conf.setLAST_LOCAL(sPref.getString(PREF_LOCATION, "ua.azigar.client.intent.action.LocalActivity1")); //пишу в конф. последнее активити
        intent = new Intent(conf.getLAST_LOCAL()); //подключаю к новуму Intent последнее активити локации

        //пишу данные переменные сокета для отправки
        conf.setID(app.getID()); //cохраняю ID игрока

        //найдем View-элементы
        btnGetQuest = (Button) findViewById(R.id.btnGetQuest);
            if(isNew == 0) btnGetQuest.setText("Принять");
            else btnGetQuest.setText("Завершить");
        btnCancel = (Button) findViewById(R.id.btnCancel);
        imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageResource(getResources().getIdentifier("nps" + nps, "drawable", getPackageName())); //применяю рисунок по имени файла
        pbConnect = (ProgressBar) findViewById(R.id.pbConnect);
        data_checking = (TextView) findViewById(R.id.data_checking);
        txtQuest = (TextView) findViewById(R.id.txtQuest);
            txtQuest.setText(txt.TxtQuests(idQuest, isNew, app.getNAME(), app.getLVL(), app.getSEX()));

        DisableLoad(); //показываю все элементы загрузки

        //подключаю Handler
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                String dialogMessage, m;
                switch (msg.what) {
                    case 1:  //подключение к серверу успешно
                        conf.setSOCKET_MESSAGE("ENTER");
                        break;

                    case 2:  //Не удалось подключится к серверу
                        Toast.makeText(QuestActivity.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        finish(); //закрываю активити
                        break;

                    case 3:   //ошибка запроса
                        Toast.makeText(QuestActivity.this, R.string.error3, Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        break;

                    case 4:  //сервер прислал уровень героя
                        app.setLVL((String) msg.obj);
                          conf.setSOCKET_MESSAGE("PVP_LVL");
                        break;

                    case 5: //сервер прислал пвп-уровень героя
                        app.setPVP_LVL((String) msg.obj);
                        conf.setSOCKET_MESSAGE("TITLE");
                        break;

                    case 6: //сервер звание игрока
                        app.setTITLE((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MAX_EXP");
                        break;

                    case 7: //сервер говорит мак. к-во опыта игрока
                        app.setMAX_EXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("EXP");
                        break;

                    case 8:  //сервер говорит текущее к-во опыта игрока
                        app.setEXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MAX_PVP_EXP");
                        break;

                    case 9:  //сервер говорит мак. к-во pvp-опыта героя
                        app.setMAX_PVP_EXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("PVP_EXP");
                        break;

                    case 10:  //сервер говорит текущее к-во pvp-опыта героя
                        app.setPVP_EXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MAX_HP");
                        break;

                    case 11:  //сервер говорит мак. к-во ОЗ героя
                        app.setMAX_HP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("HP");
                        break;

                    case 12:  //сервер говорит текущее к-во ОЗ героя
                        if(isUp == true){
                            app.setHP((String) msg.obj);
                            if(Integer.parseInt(app.getHP()) > Integer.parseInt(app.getMAX_HP())){
                                app.setHP(app.getMAX_HP());
                            }
                        }
                        conf.setSOCKET_MESSAGE("MAX_MANA");
                        break;

                    case 13: //сервер мак. к-во мани героя
                        app.setMAX_MANA((String) msg.obj);
                        if (Integer.parseInt(app.getMAX_MANA()) == 0) {
                            app.setIS_MANA("0");
                            app.setMAX_MANA("1");
                        }else{
                            app.setIS_MANA("1");
                        }
                        conf.setSOCKET_MESSAGE("MANA"); //запрашиваю к-во маны героя
                        break;

                    case 14: //сервер говорит к-во маны героя
                        if(isUp == true){
                            app.setMANA((String) msg.obj);
                            if(Integer.parseInt(app.getMANA()) > Integer.parseInt(app.getMAX_MANA())){
                                app.setMANA(app.getMAX_MANA());
                            }
                        }
                        conf.setSOCKET_MESSAGE("MONEY");
                        break;

                    case 15: //сервер говорит к-во монет игрока
                        app.setMONEY((String) msg.obj);
                        conf.setSOCKET_MESSAGE("GOLD");
                        break;

                    case 16:  //сервер прислал к-во голда героя
                        app.setGOLD((String) msg.obj);
                        conf.setSOCKET_MESSAGE("EXECUTE");
                        break;

                    case 17:  //сервер пристал ID-квеста
                        m = (String) msg.obj;
                        taken.add(Integer.parseInt(m));
                        conf.setSOCKET_MESSAGE("GET_EXECUTE");
                        break;

                    case 18:  //сервер пристал флаг прохождение квеста
                        m = (String) msg.obj;
                        executed.add(Integer.parseInt(m));
                        conf.setSOCKET_MESSAGE("LAST_EXECUTE");
                        break;

                    case 19:  //сервер прислал ответ на последний в списке КВЕСТ
                        m = (String) msg.obj;
                        if (m.equalsIgnoreCase("NO_LAST_EXECUTE")){ //если надо еще узнавать инфу о квестах
                            conf.setSOCKET_MESSAGE("NEXT_EXECUTE");
                        }else{
                            db.clearExecute(); //очищаю таблицу Выполнение квестов
                            AddTableExecute(); //ввожу данные в таблицу
                            db.LogExecute();
                            db.close();
                            conf.setSOCKET_MESSAGE("END");
                            startActivity(intent);
                        }
                        break;

                    case 20:  //пришот ответ на лвл-апп
                        m = (String) msg.obj;
                        if (m.equalsIgnoreCase("NO_LVL_UP")) {
                            conf.setSOCKET_MESSAGE("UP_PVP");
                        } else {
                            isUp = true;
                            dialogMessage = m;
                            dialog = DialogScreen.getDialog(QuestActivity.this, DialogScreen.DIALOG_PLAYER_LVL_UP, conf, dialogMessage);
                            dialog.show();
                        }
                        break;

                    case 21:  //пришот ответ на pvp-апп
                        m = (String) msg.obj;
                        if (m.equalsIgnoreCase("NO_PVP_UP")) {
                            isNew = 2;
                            btnGetQuest.setWidth(250);
                            btnGetQuest.setText("Забрать награду");
                            txtQuest.setText(txt.TxtQuests(idQuest, isNew, app.getNAME(), app.getLVL(), app.getSEX()));
                            Loading();
                            btnCancel.setVisibility(View.GONE);
                        } else {
                            isUp = true;
                            isNew = 2;
                            btnGetQuest.setWidth(250);
                            btnGetQuest.setText("Забрать награду");
                            txtQuest.setText(txt.TxtQuests(idQuest, isNew, app.getNAME(), app.getLVL(), app.getSEX()));
                            Loading();
                            btnCancel.setVisibility(View.GONE);
                            dialogMessage = m;
                            dialog = DialogScreen.getDialog(QuestActivity.this, DialogScreen.DIALOG_PLAYER_PVP_UP_QUEST, conf, dialogMessage);
                            dialog.show();
                        }
                        break;

                    case 22:  //сервер проверил квест и выдал награду
                        break;

                    case 23:  //квест не прошел проверку
                        isNew = 3;
                        btnGetQuest.setText("Хорошо");
                        txtQuest.setText(txt.TxtQuests(idQuest, isNew, app.getNAME(), app.getLVL(), app.getSEX()));
                        Loading();
                        break;

                    case 24:  //квест не прошел проверку
                        builder = new AlertDialog.Builder(QuestActivity.this);
                        builder.setIcon(android.R.drawable.ic_dialog_info); // иконка
                        builder.setCancelable(false); //Запрещаем закрывать окошко кнопкой "back"
                        builder.setMessage(R.string.full_inventar); // сообщение
                        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка Да
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                conf.setSOCKET_MESSAGE("END");
                                startActivity(intent);
                            }
                        });
                        builder.create().show();
                        break;
                }
            }
        };
    }

    // заполняю таблицу Execute
    private static void AddTableExecute() {
        for (int i = 0; i < taken.size(); i++) {
            cv.clear();
            cv.put("taken", taken.get(i));
            cv.put("executed", executed.get(i));
            db.insert("execute", cv);
        }
        taken = new ArrayList<Integer>();
        executed = new ArrayList<Integer>();
        db.LogExecute();
    }

    //обработчи кнопки Принять/Завершить
    public void onClickGetQuest(View v) {
        switch (isNew){
            case 0: //новый квест, буду принимать задания
                Load();
                quest = new Quest(h, conf);
                break;

            case 1: //квест уже принят, буду завершать задания
                conf.setEXE_QUEST("TAKEN_QUEST");
                Load();
                quest = new Quest(h, conf);
                break;

            case 2: //задание прошло проверку и получаю награду
                Load();
                conf.setSOCKET_MESSAGE("GET");
                break;

            case 3: //задание не прошло проверку
                conf.setSOCKET_MESSAGE("END");
                startActivity(intent);
                break;
        }
    }

    //обработчи кнопки Отказатся
    public void onClickCancel(View v) {
        switch (isNew){
            case 0:
                startActivity(intent);
                break;

            case 1:
                conf.setEXE_QUEST("CANCEL_QUEST");
                Load();
                quest = new Quest(h, conf);
                break;

            case 3:
                Load();
                conf.setSOCKET_MESSAGE("CANCEL_QUEST");
                break;
        }
    }

    //прячу все элементы
    static void DisableAll() {
        imageView.setVisibility(View.GONE);
        btnGetQuest.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        txtQuest.setVisibility(View.GONE);
    }

    //показываю все элементы
    static void EnableAll() {
        imageView.setVisibility(View.VISIBLE);
        btnGetQuest.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        txtQuest.setVisibility(View.VISIBLE);
    }

    //прячу все элементы загрузки
    static void DisableLoad() {
        pbConnect.setVisibility(View.GONE);
        data_checking.setVisibility(View.GONE);
    }

    //показываю все элементы загрузки
    static void EnableLoad() {
        pbConnect.setVisibility(View.VISIBLE);
        data_checking.setVisibility(View.VISIBLE);
    }

    //режим загрузки
    static void Load() {
        DisableAll();
        EnableLoad();
    }

    //режим диалога
    static void Loading() {
        EnableAll();
        DisableLoad();
    }

    //обработка кнопки назад
    @Override
    public void onBackPressed () { }

    //обработка закрытия (уничтожения) активити
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
