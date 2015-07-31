package ua.azigar.client.Location;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import ua.azigar.client.SettingActivity;

/**
 * Created by Azigar on 08.07.2015.
 */
public class LocalActivity1 extends ActionBarActivity {

    Handler h_hp, h_mana, h_exit, h_healing;
    AlertDialog.Builder builder;
    SocketConfig conf = new SocketConfig(); //подключаю новый екземпляр conf для клиента регистрации
    Intent intent;
    AlertDialog dialog;
    Regeneration regeneration_hp, regeneration_mana;
    SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:
    final String LOCATION = "ua.azigar.client.intent.action.LocalActivity1";
    static final String PREF_LOCATION = "LOCATION";
    String dialogMessage = "";

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
    //массивы для списка
    String [] selection;
    int [] values;

    int money, lvl;

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
        this.money = Integer.parseInt(app.getMONEY());
        this.lvl = Integer.parseInt(app.getLVL());
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
            manaHero.setMaxValue(Integer.parseInt(app.getMAX_MANA()));
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
        values = new int[]{1, 2, 3, 3, 3}; //выбор действия (1-пойти, 2-говорить, 3-атаковать)
        // упаковываем данные в понятную для адаптера структуру
        ArrayList <HashMap<String, Object>> select = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> m;
        for (int i = 0; i < selection.length; i++) {
            m = new HashMap<String, Object>();
            m.put(LIST_TEXT, selection[i]); //текст
            int img = 0;
            switch (values[i]) {
                case 1:   img = go;     break;
                case 2:   img = speak;  break;
                case 3:   img = atk;    break;
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
                switch (position) {
                    case 0:
                        Toast.makeText(getApplicationContext(), selection[position], Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        Toast.makeText(getApplicationContext(), selection[position], Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        SickHeroPvE(app.getHP(), app.getMAX_HP(), "1", "1", "45", "0", selection[position], "bot_1", "1");
                        break;

                    case 3:
                        SickHeroPvE(app.getHP(), app.getMAX_HP(), "2", "1", "50", "0", selection[position], "bot_2", "1");
                        break;

                    case 4:
                        SickHeroPvE(app.getHP(), app.getMAX_HP(), "4", "1", "35", "0", selection[position], "bot_4", "2");
                        break;
                }
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
        if (Integer.parseInt(app.getHP()) < Integer.parseInt(app.getMAX_HP())) {
            regeneration_hp = new Regeneration(h_hp, Integer.parseInt(app.getMAX_HP()),
                    Integer.parseInt(app.getHP()), Integer.parseInt(app.getVIP()), 1); //запуск регенерации
        }

        //ханлер для прогрессБара на ману
        h_mana = new Handler() {
            public void handleMessage(android.os.Message msg) {
                manaHero.setValue(msg.what); //востанавливаю ману
                app.setMANA(String.valueOf(msg.what)); //пишу новое значение в глобальную переменную
                conf.setMANA(app.getMANA());
            }
        };
        if (Integer.parseInt(app.getMANA()) < Integer.parseInt(app.getMAX_MANA()) &&
                Integer.parseInt(app.getMAX_MANA()) > 1) {
            regeneration_mana = new Regeneration(h_mana, Integer.parseInt(app.getMAX_MANA()),
                    Integer.parseInt(app.getMANA()), Integer.parseInt(app.getVIP()), 0); //запуск регенерации
        }

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
                        conf.setSOCKET_MESSAGE("END"); //закрываю активити боя
                        //обновляюктивити
                        Reload();
                        break;

                    case 5:
                        dialogMessage = "Боги не слишат тебя";
                        dialog = DialogScreen.getDialog(LocalActivity1.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                        dialog.show();
                        if (Integer.parseInt(app.getHP()) < Integer.parseInt(app.getMAX_HP())) {
                            regeneration_hp = new Regeneration(h_hp, Integer.parseInt(app.getMAX_HP()),
                                    Integer.parseInt(app.getHP()), Integer.parseInt(app.getVIP()), 1); //запуск регенерации
                        }
                        if (Integer.parseInt(app.getMANA()) < Integer.parseInt(app.getMAX_MANA()) &&
                                Integer.parseInt(app.getMAX_MANA()) > 1) {
                            regeneration_mana = new Regeneration(h_mana, Integer.parseInt(app.getMAX_MANA()),
                                    Integer.parseInt(app.getMANA()), Integer.parseInt(app.getVIP()), 0); //запуск регенерации
                        }
                        break;

                    case 6:
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
                }
            }
        };
    }

    //метод для рестарта активити
    private void Reload() {
        Intent intent = getIntent();
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

            case R.id.Hero:
                StopRegen(); //принудительно закрываю потоки на реген
                intent = new Intent("ua.azigar.client.intent.action.HeroActivity");
                startActivity(intent);
                return true;

            case R.id.Invent:

                return true;

            case R.id.PvP:
                if ((money * lvl) >= (100 * lvl)) { //если достаточно денег для ПвП-боя
                    if (Integer.parseInt(conf.getHP()) < Integer.parseInt(conf.getMAX_HP())) { //если здоровья меньше мак. к-ва, то
                        builder = new AlertDialog.Builder(this);
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
                }else{
                    dialogMessage = "Недостаточно монег для проведения ПвП-боя.";
                    dialog = DialogScreen.getDialog(LocalActivity1.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                    dialog.show();
                }
                return true;

            case R.id.Settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;

            case R.id.Regen:
                if (Integer.parseInt(conf.getHP()) < Integer.parseInt(conf.getMAX_HP()) ||
                        Integer.parseInt(conf.getMANA()) < Integer.parseInt(conf.getMAX_MANA())) {
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
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.exit);
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
            regeneration_hp.interrupt(); //закрываю поток на востановление
            regeneration_hp = null;
        }
        if (regeneration_mana != null) {
            regeneration_mana.MyStop();
            regeneration_mana.interrupt(); //закрываю поток на востановление
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
