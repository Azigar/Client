package ua.azigar.client.Location;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ua.azigar.client.Client.ExitGame;
import ua.azigar.client.Client.Healing;
import ua.azigar.client.Resources.Database;
import ua.azigar.client.Resources.SocketConfig;
import ua.azigar.client.MainActivity;
import ua.azigar.client.R;
import ua.azigar.client.Resources.DialogScreen;
import ua.azigar.client.Resources.MyHero;
import ua.azigar.client.Resources.Progress_exp;
import ua.azigar.client.Resources.Progress_hp;
import ua.azigar.client.Resources.Progress_mana;
import ua.azigar.client.Resources.Progress_pvp;
import ua.azigar.client.Resources.Regeneration;

/**
 * Created by Azigar on 08.07.2015.
 */
public class LocalActivity1 extends ActionBarActivity {

    final String LOG_TAG = "myLogs";

    static Database db;
    static Handler h_hp, h_mana, h_exit, h_healing;
    static AlertDialog.Builder builder;
    static SocketConfig conf = new SocketConfig(); //подключаю новый екземпляр conf для клиента регистрации
    static Intent intent;
    static AlertDialog dialog;
    static Regeneration regeneration_hp, regeneration_mana;
    static SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:
    final String LOCATION = "ua.azigar.client.intent.action.LocalActivity1";
    static final String PREF_LOCATION = "LOCATION";
    static String dialogMessage = "";

    static TextView txtNameHero, txtTitle, txtMoney, txtGold, data_checking;
    static Progress_hp hpHero;
    static Progress_mana manaHero;
    static Progress_exp expHero;
    static Progress_pvp pvpHero;
    static ImageView imgVIP, imgLocal;
    static ListView listView;
    static ProgressBar pbConnect;

    // имена атрибутов для Map
    final String LIST_TEXT = "text";
    final String LIST_IMAGE = "image";
    // картинки для отображения в списке локации
    final int speak = R.drawable.speak2;
    final int go = R.drawable.go;
    final int atk = R.drawable.atk;
    final int shop = R.drawable.shop;
    final int newQuest = R.drawable.new_quest1;
    //массивы для списка
    String [] selection, nameQuest;
    int [] values, idQuest;

    int money, lvl, countNewQuest, countQuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //задаю програмно заголовок
        setTitle(R.string.local1);
        //убираю ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //подключаем лауер-файл с элементами
        setContentView(R.layout.activity_local1);
        //подключаю глобальные переменные для хранения данных о герое
        final MyHero app = ((MyHero)getApplicationContext());
        Log.d(LOG_TAG, "Первый запуск - " + app.getFIRST_START());

        //Подключение и работа с БД
        db = new Database(this); //подключаю класс
        db.open(); //поключаю БД
        countNewQuest = db.getCountNewQuests("1", app.getLVL(), app.getPVP_LVL()); //узнаю наличие новых квестов
        db.close();

        //пишу активную локацию в Preferences
        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        final SharedPreferences.Editor ed = sPref.edit();  //подключаю Editor для сохранение новых параметров в SharedPreferences
        ed.putString(PREF_LOCATION, LOCATION);
        ed.commit(); //сохраняю флаг о активной локации
        //пишу данные переменные сокета для отправки
        conf.setID(app.getID()); //cохраняю ID игрока
        conf.setNAME(app.getNAME()); //cохраняю имя игрока
        conf.setMANA(app.getMANA()); //сохраняю к-во маны героя
        conf.setHP(app.getHP()); //сохраняю к-во ОЗ героя
        conf.setMAX_HP(app.getMAX_HP()); //сохраняю мак. к-во ОЗ героя
        conf.setMAX_MANA(app.getMAX_MANA()); //сохраняю мак. к-во маны героя
        conf.setIS_MANA(app.getIS_MANA()); //сохраняю длаг о наличии магии у героя
        this.money = Integer.parseInt(app.getMONEY());
        this.lvl = Integer.parseInt(app.getLVL());

        builder = new AlertDialog.Builder(this);

        //инициализирую обьекты
        pbConnect = (ProgressBar) findViewById(R.id.pbConnect);
        data_checking = (TextView) findViewById(R.id.data_checking);
        txtNameHero = (TextView) findViewById(R.id.txtNameHero);
            txtNameHero.setText(app.getNAME() + " [" + app.getLVL() + "]");
        txtTitle = (TextView) findViewById(R.id.txtTitle);
            if (app.getTITLE().equalsIgnoreCase("нет звания")) {
                txtTitle.setText("");
            }else txtTitle.setText(app.getTITLE());
        txtMoney = (TextView) findViewById(R.id.txtMoney);
            txtMoney.setText(app.getMONEY() + " монет");
        txtGold = (TextView) findViewById(R.id.txtGold);
            txtGold.setText(app.getGOLD() + " G");
        hpHero = (Progress_hp) findViewById(R.id.hpHero);
            hpHero.setMaxValue(Integer.parseInt(app.getMAX_HP()));
            hpHero.setValue(Integer.parseInt(app.getHP()));
        manaHero = (Progress_mana) findViewById(R.id.manaHero);
            manaHero.setMaxValue(Integer.parseInt(app.getMAX_MANA()), Integer.parseInt(app.getIS_MANA()));
            manaHero.setValue(Integer.parseInt(app.getMANA()));
        expHero = (Progress_exp) findViewById(R.id.expHero);
            expHero.setMaxValue(Integer.parseInt(app.getMAX_EXP()));
            expHero.setValue(Integer.parseInt(app.getEXP()));
        pvpHero = (Progress_pvp) findViewById(R.id.pvpHero);
            pvpHero.setMaxValue(Integer.parseInt(app.getMAX_PVP_EXP()));
            pvpHero.setValue(Integer.parseInt(app.getPVP_EXP()));
        imgLocal = (ImageView) findViewById(R.id.imgLocal);
        imgVIP = (ImageView) findViewById(R.id.imgVIP);
            imgVIP.setImageResource(getResources().getIdentifier("vip" + app.getVIP(), "drawable", getPackageName()));
        listView = (ListView)findViewById(R.id.listView);
        // устанавливаем режим выбора пунктов списка
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // определяем массив данных для списка типа String
        selection = getResources().getStringArray(R.array.select1);
        // определяем массив данных для списка типа int
        values = new int[]{1, 4, 2, 3, 3, 3}; //выбор действия (1-пойти, 2-говорить, 3-атаковать, 4 - магазин)
        // упаковываем данные в понятную для адаптера структуру
        ArrayList <HashMap<String, Object>> select = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> m;
        for (int i = 0; i < selection.length; i++) {
            m = new HashMap<String, Object>();
            m.put(LIST_TEXT, selection[i]); //текст
            int img = 0;
            switch (values[i]) {
                case 1:   img = go;     break;
                case 2:
                    if(countNewQuest > 0) img = newQuest;
                    else img = speak;   break;
                case 3:   img = atk;    break;
                case 4:   img = shop;   break;
            }
            m.put(LIST_IMAGE, img); //картинка
            select.add(m);
        }
        // создаем адаптер для списка с картинками
        SimpleAdapter adapter = new SimpleAdapter(this, select, R.layout.item, new String[]{
                LIST_TEXT,         //текст
                LIST_IMAGE          //картинка
        }, new int[]{
                R.id.select, //ссылка на объект отображающий текст
                R.id.img}); //ссылка на объект отображающий картинку
        //подключаю адаптер в список
        listView.setAdapter(adapter);
        //обработчик нажатия списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Cursor c;
                db.open(); //поключаю БД
                switch (position) {
                    //пойти
                    case 0:
                        Toast.makeText(getApplicationContext(), selection[position], Toast.LENGTH_SHORT).show();
                        break;

                    //магазин
                    case 1:
                        //определяю, пройден ли нужный квест (Надоедливые мухи)
                        countQuest = db.getIsQuest("2");
                        if(countQuest == 0){ //если этот квест не пройден
                            dialog = DialogScreen.getDialog(LocalActivity1.this, DialogScreen.DIALOG_OK, conf, NoShop());
                            dialog.show();
                        } else {
                            StopRegen(); //принудительно закрываю потоки на реген
                            intent = new Intent("ua.azigar.client.intent.action.ShopActivity");
                            intent.putExtra("idShop", "1"); //передаю параметром текущее значение ОЗ и маны
                            intent.putExtra("nameShop", selection[position]);
                            startActivity(intent);
                        }
                        break;

                    //NPS
                    case 2:
                        StopRegen(); //принудительно закрываю потоки на реген
                        final String NPS = "1";  //код Старосты посёлка
                        //к-во квестов которые может дать или которые можно сдать
                        countQuest = db.getCountQuests(NPS, app.getLVL(), app.getPVP_LVL());
                        if(countQuest > 0){ //если есть такие квесты у этого NPS
                            //ID и название квестов которые может дать или которые можно сдать этому NPS;
                            c = db.getCursorQuests(NPS, app.getLVL(), app.getPVP_LVL());
                            //запись из курсора в массив
                            nameQuest = new String[countQuest];
                            idQuest = new int[countQuest];
                            if (c.moveToFirst()) { //стаю на первую строку курсора
                                for (int i = 0; i < countQuest; i++){
                                    idQuest[i] = c.getInt(c.getColumnIndex("quest"));
                                    nameQuest[i] = c.getString(c.getColumnIndex("name"));
                                    c.moveToNext(); //+1 строка
                                }
                            }
                            c.close();
                            //создаю диалог с выбором квеста на основе массива названий квестов
                            builder = new AlertDialog.Builder(LocalActivity1.this);
                            builder.setTitle("Выберите задание"); // заголовок для диалога
                            builder.setItems(nameQuest, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    db.open(); //поключаю БД
                                    //проверяю выбранный квест - новый или уже взят на выполнение (что бы сдать или от него отказатся)
                                    int isNew = db.getIsNewQuest(String.valueOf(idQuest[item]));
                                    db.close();
                                    intent = new Intent("ua.azigar.client.intent.action.QuestActivity");
                                    intent.putExtra("isNew", isNew);
                                    intent.putExtra("idQuest", idQuest[item]);
                                    intent.putExtra("nps", NPS);
                                    startActivity(intent);
                                }
                            });
                            builder.setCancelable(false);
                            builder.create().show();
                        }else{
                            dialog = DialogScreen.getDialog(LocalActivity1.this, DialogScreen.DIALOG_OK, conf, NoQuests());
                            dialog.show();
                        }
                        break;

                    //BOT
                    case 3:
                        SickHeroPvE(app.getHP(), app.getMAX_HP(), "1", "1", "20", "1", selection[position], "bot_1", "2");
                        break;

                    //BOT
                    case 4:
                        SickHeroPvE(app.getHP(), app.getMAX_HP(), "2", "1", "50", "1", selection[position], "bot_2", "1");
                        break;

                }
                db.close();
            }
        });

        DisableExit();

        //ханлер для прогрессБара на здоровье
        h_hp = new Handler() {
            public void handleMessage(android.os.Message msg) {
                hpHero.setValue(msg.what); //востанавливаю здоровье
                app.setHP(String.valueOf(msg.what)); //пишу новое значение в глобальную переменную
                conf.setHP(app.getHP());
            }
        };
        /*if (Integer.parseInt(app.getHP()) < Integer.parseInt(app.getMAX_HP())) {
            regeneration_hp = new Regeneration(h_hp, Integer.parseInt(app.getMAX_HP()),
                    Integer.parseInt(app.getHP()), Integer.parseInt(app.getVIP()), 1); //запуск регенерации
        }*/



        //ханлер для прогрессБара на ману
        h_mana = new Handler() {
            public void handleMessage(android.os.Message msg) {
                manaHero.setValue(msg.what); //востанавливаю ману
                app.setMANA(String.valueOf(msg.what)); //пишу новое значение в глобальную переменную
                conf.setMANA(app.getMANA());
            }
        };
        /*if (Integer.parseInt(app.getMANA()) < Integer.parseInt(app.getMAX_MANA()) &&
                Integer.parseInt(app.getIS_MANA()) == 1) { //если мана меньше мак. и герой владеет магией
            regeneration_mana = new Regeneration(h_mana, Integer.parseInt(app.getMAX_MANA()),
                    Integer.parseInt(app.getMANA()), Integer.parseInt(app.getVIP()), 0); //запуск регенерации
        }*/

        //ханлер для процесса коректного выхода с игры
        h_exit = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what){
                    case 1:
                        data_checking.setText("Сохраняю данные героя на сервер...");
                        conf.setSOCKET_MESSAGE("ENTER");  //отправляю команду
                        break;

                    case 2:
                        Toast.makeText(LocalActivity1.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        Exit();
                        break;

                    case 3:
                        Toast.makeText(LocalActivity1.this, R.string.synchronization, Toast.LENGTH_LONG).show();
                        Exit();
                        break;
                }
            }
        };

        //ханлер для процесса быстрого исцелене богами
        h_healing = new Handler() {
            public void handleMessage(android.os.Message msg) {
                String cmd = null;
                switch (msg.what){
                    case 1:
                        conf.setSOCKET_MESSAGE("ENTER");  //отправляю команду, что покидаю игру
                        break;

                    case 2:
                        Toast.makeText(LocalActivity1.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        break;

                    case 3:
                        cmd = (String) msg.obj;
                        TxtRegen(cmd);
                        dialog = DialogScreen.getDialog(LocalActivity1.this, DialogScreen.DIALOG_PLAYER_SICK, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 4:
                        app.setGOLD((String) msg.obj);
                        conf.setSOCKET_MESSAGE("END");
                        Reload(); //обновляю активити
                        break;

                    case 5: //сервер говорит к-во голда героя
                        dialogMessage = "Боги не слишат тебя";
                        builder = new AlertDialog.Builder(LocalActivity1.this);
                        builder.setCancelable(false); //Запрещаем закрывать окошко кнопкой "back"
                        builder.setMessage(dialogMessage); // сообщение
                        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка Да
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Regen(Integer.parseInt(app.getHP()), Integer.parseInt(app.getMAX_HP()),
                                        Integer.parseInt(app.getMANA()), Integer.parseInt(app.getMAX_MANA()),
                                        Integer.parseInt(app.getVIP()), Integer.parseInt(app.getIS_MANA()));
                            }
                        })
                        .create().show();
                        break;

                    case 6: //сервер говорит к-во моней героя
                        app.setMONEY((String) msg.obj);
                        conf.setSOCKET_MESSAGE("GOLD"); //запрашиваю к-во голда у героя
                        break;

                    case 7:   //сервер говорит текущее к-во ОЗ героя
                        app.setHP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MANA"); //запрашиваю к-во маны героя
                        break;

                    case 8:   //сервер говорит к-во маны героя
                        app.setMANA((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MONEY"); //запрашиваю к-во монет героя
                        break;

                    case 9:   //отказался оплачивать за реген
                        conf.setSOCKET_MESSAGE("END");
                        //заново запускаю реген
                        Regen(Integer.parseInt(app.getHP()), Integer.parseInt(app.getMAX_HP()),
                                Integer.parseInt(app.getMANA()), Integer.parseInt(app.getMAX_MANA()),
                                Integer.parseInt(app.getVIP()), Integer.parseInt(app.getIS_MANA()));
                        break;
                }
            }
        };
        if(Integer.parseInt(app.getFIRST_START()) == 1) {  //если это первый запуск игры и новый игрок
            app.setFIRST_START("0");
            intent = new Intent("ua.azigar.client.intent.action.QuestActivity");
            intent.putExtra("isNew", 0);  //новый квест
            intent.putExtra("idQuest", 1);  //квест Знакомство
            intent.putExtra("nps", 1);  //NPS - Староста
            startActivity(intent);
        }
        else {
            //если есть новый квест, то будет сообщение
            if (countNewQuest > 0)
                Toast.makeText(LocalActivity1.this, NewQuests(), Toast.LENGTH_LONG).show();
        }
        Regen(Integer.parseInt(app.getHP()), Integer.parseInt(app.getMAX_HP()),
                Integer.parseInt(app.getMANA()), Integer.parseInt(app.getMAX_MANA()),
                    Integer.parseInt(app.getVIP()), Integer.parseInt(app.getIS_MANA()));
    }

    private static void Regen(int hp, int max_hp, int mana, int max_mana, int vip, int is_mana) {
        if (hp < max_hp) {
            regeneration_hp = new Regeneration(h_hp, max_hp, hp, vip, 1); //запуск регенерации жизни
        }
        if ((mana < max_mana) && is_mana == 1) { //если мана меньше мак. и герой владеет магией
            regeneration_mana = new Regeneration(h_mana, max_mana, mana, vip, 2); //запуск регенерации маны
        }
    }

    //текстовка - Магазин закрыт
    private String NoShop() {
        String message = "";
        int txt = randTxt();
        switch (txt) {
            case 1:
                message = "Здесь не место чужакам!";
                break;
            case 2:
                message = "У меня закрыто!";
                break;
            case 3:
                message = "Никого нет!";
                break;
        }
        return message;
    }

    //текстовка - нет новых квестов
    private String NoQuests() {
        String message = "";
        int txt = randTxt();
        switch (txt) {
            case 1:
                message = "Ты не видишь, что я занят?";
                break;
            case 2:
                message = "Мне нечего тебе сказать.";
                break;
            case 3:
                message = "Сейчас у меня нет для тебя работы.";
                break;
        }
        return message;
    }

    //текстовка о новом квесте
    private String NewQuests() {
        String message = "";
        int txt = randTxt();
        switch (txt) {
            case 1:
                message = "Староста хочет с тобой поговорить.";
                break;
            case 2:
                message = "У Старосты для тебя есть новое задание.";
                break;
            case 3:
                message = "Поговори со Старостой.";
                break;
        }
        return message;
    }

    //метод для рестарта активити
    private void Reload() {
        Log.d(LOG_TAG, "----РЕФРЕШ АКТИВИТИ ---- ");
        intent = getIntent();
        overridePendingTransition(0, 0);//4
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//5
        finish();//6
        overridePendingTransition(0, 0);//7
        startActivity(intent);//8
    }

    //текстовка о исцелении
    private void TxtRegen(String money) {
        int txt = randTxt();
        switch (txt) {
            case 1:
                dialogMessage = "Боги могут исцелить тебя за " + money + ".";
                break;
            case 2:
                dialogMessage = "Приподнеси в жертву богам " + money + " и они исцелят тебя.";
                break;
            case 3:
                dialogMessage = "Для исцеление пожертвуй богам " + money + ".";
                break;
        }
    }

    //рандом текста
    private static int randTxt() {
        Random random = new Random();
        return random.nextInt(3) + 1;
    }

    //метод определяет здоровье героя
    private void SickHeroPvE(String HP, String MAX_HP, final String id, final String lvl,
                             final String hp, final String mana, final String name, final String fileName, final String sex){
        if (Integer.parseInt(HP) < Integer.parseInt(MAX_HP)) { //если здоровья меньше мак. к-ва, то
            builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_info); // иконка
            builder.setCancelable(false); //Запрещаем закрывать окошко кнопкой "back"
            builder.setMessage(R.string.Pleayer_sick); // сообщение
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка Да
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StopRegen(); //принудительно закрываю потоки на реген
                    Attack(id, lvl, hp, mana, name, fileName, sex);
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // Отпускает диалоговое окно
                }
            }).create().show();
        } else {
            Attack(id, lvl, hp, mana, name, fileName, sex);
        }
    }

    //запускаю активити боя
    void Attack(String id, String lvl, String hp, String mana, String name, String fileName, String sex) {
        intent = new Intent("ua.azigar.client.intent.action.FIGHT");
        intent.putExtra("id", id);
        intent.putExtra("lvl", lvl);
        intent.putExtra("hp", hp);
        intent.putExtra("mana", mana);
        intent.putExtra("name", name);
        intent.putExtra("avatar", fileName);
        intent.putExtra("sex", sex);
        startActivity(intent);
    }

    //прячу все элементы локации
    static void DisableAll() {
        txtNameHero.setVisibility(View.GONE);
        txtTitle.setVisibility(View.GONE);
        txtMoney.setVisibility(View.GONE);
        txtGold.setVisibility(View.GONE);
        hpHero.setVisibility(View.GONE);
        manaHero.setVisibility(View.GONE);
        expHero.setVisibility(View.GONE);
        pvpHero.setVisibility(View.GONE);
        imgVIP.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        imgLocal.setVisibility(View.GONE);
    }

    //показываю все элементы для сохранения и закрытия иры
    static void EnableExit() {
        data_checking.setVisibility(View.VISIBLE);
        pbConnect.setVisibility(View.VISIBLE);
    }

    //прячу все элементы для сохранения и закрытия иры
    static void DisableExit() {
        pbConnect.setVisibility(View.GONE);
        data_checking.setVisibility(View.GONE);
    }

    //этот метод перейдет в предыдущее активити и очистит весь стек над ним. И посылаем метку закрытия
    private void Exit() {
        intent = new Intent(LocalActivity1.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("finish", true);
        LocalActivity1.this.startActivity(intent);
    }

    //прикрепляю меню к активити
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local, menu);
        return true;
    }

    //обработчик меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.Bank :

                return true;

            case R.id.Quests:
                StopRegen(); //принудительно закрываю потоки на реген
                intent = new Intent("ua.azigar.client.intent.action.QuestsActivity");
                startActivity(intent);
                return true;

            case R.id.Hero:
                StopRegen(); //принудительно закрываю потоки на реген
                intent = new Intent("ua.azigar.client.intent.action.HeroActivity");
                startActivity(intent);
                return true;

            case R.id.Invent:
                StopRegen(); //принудительно закрываю потоки на реген
                intent = new Intent("ua.azigar.client.intent.action.InventarActivity");
                intent.putExtra("hp", conf.getHP()); //передаю параметром текущее значение ОЗ и маны
                intent.putExtra("mana", conf.getMANA());
                startActivity(intent);
                return true;

            case R.id.PvP:
                if(lvl < 3) { //ПвП доступен с 3-го уровня
                    dialogMessage = "ПвП-бои доступны с 3-го уровня.";
                    dialog = DialogScreen.getDialog(LocalActivity1.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                    dialog.show();
                }else {
                    if ((money * lvl) >= (100 * lvl)) { //если достаточно денег для ПвП-боя
                        if (Integer.parseInt(conf.getHP()) < Integer.parseInt(conf.getMAX_HP())) { //если здоровья меньше мак. к-ва, то
                            builder.setIcon(android.R.drawable.ic_dialog_info); // иконка
                            builder.setMessage(R.string.Pleayer_sick2); // сообщение
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка Да
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StopRegen(); //принудительно закрываю потоки на реген
                                    intent = new Intent("ua.azigar.client.intent.action.OpponentActivity");
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Отпускает диалоговое окно
                                }
                            }).create().show();
                        } else {
                            intent = new Intent("ua.azigar.client.intent.action.OpponentActivity");
                            startActivity(intent);
                        }
                    } else {
                        dialogMessage = "Недостаточно монет для проведения ПвП-боя.";
                        dialog = DialogScreen.getDialog(LocalActivity1.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                        dialog.show();
                    }
                }
                return true;

            case R.id.Settings:
                StopRegen(); //принудительно закрываю потоки на реген
                intent = new Intent("ua.azigar.client.intent.action.SettingActivity");
                startActivity(intent);
                return true;

            case R.id.Regen:
                Log.d(LOG_TAG, "Поточное значение HP - " + conf.getHP());
                Log.d(LOG_TAG, "Максимальное значение HP - " + conf.getMAX_HP());
                Log.d(LOG_TAG, "Поточное значение MANA - " + conf.getMANA());
                Log.d(LOG_TAG, "Максимальное значение MANA - " + conf.getMAX_MANA());
                if (Integer.parseInt(conf.getHP()) < Integer.parseInt(conf.getMAX_HP()) ||
                        (Integer.parseInt(conf.getMANA()) < Integer.parseInt(conf.getMAX_MANA()) &&
                                Integer.parseInt(conf.getIS_MANA()) == 1)) {
                    StopRegen(); //принудительно закрываю потоки на реген
                    new Healing(h_healing, conf, this); //запускаю процесс востановление
                } else {
                    dialogMessage = "В здоровом теле здоровый дух";
                    dialog = DialogScreen.getDialog(LocalActivity1.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                    dialog.show();
                }
                return true;

            case R.id.About:

                return true;

            case R.id.Exit:
                builder.setTitle(R.string.exit);
                builder.setCancelable(false); //Запрещаем закрывать окошко кнопкой "back"
                builder.setMessage(R.string.qExit); // сообщение
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StopRegen(); //принудительно закрываю потоки на реген
                        //убираю заголовок приложения, обьекты локации и показываю окно сохранение данных
                        ActionBar actionBar = getSupportActionBar();
                        actionBar.hide();
                        DisableAll();
                        EnableExit();
                        new ExitGame(h_exit, conf);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                    }).create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void StopRegen(){
        if (regeneration_hp != null) {
            regeneration_hp.MyStop();
            regeneration_hp = null;
        }
        if (regeneration_mana != null) {
            regeneration_mana.MyStop();
            regeneration_mana = null;
        }
    }

    //обработка остановки активити
    @Override
    protected void onStop() {
        super.onStop();
        StopRegen();
    }

    //обработка кнопки назад
    @Override
    public void onBackPressed () {
        //ничего не делать
    }

    //обработка закрытия (уничтожения) активити
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
