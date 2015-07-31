package ua.azigar.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ua.azigar.client.Client.Hero;
import ua.azigar.client.Resources.SocketConfig;
import ua.azigar.client.Resources.MyHero;

/*
 * Created by Azigar on 17.07.2015.
 * класс-активити для отображение всех хар-к героя
 */

public class HeroActivity extends ActionBarActivity {

    Handler h;
    SocketConfig conf = new SocketConfig(); //подключаю новый екземпляр conf для клиента регистрации
    Intent intent;
    SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:
    static final String PREF_LOCATION = "LOCATION";

    static TextView name_lvl, pvp_lvl, exp, pvp_exp, txt_str, txt_min_uron, txt_max_uron, txt_dex, txt_inst, txt_def, txt_hp, txt_mana;
    static TextView str, min_uron, max_uron, dex, inst, def, hp, mana, data_checking;
    static TextView fraction, school, my_uron, enemy_uron, pve_v, pve_l, pvp_v, pvp_l;
    static ProgressBar pbConnect;
    static ImageView imgHero;

    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //убираю ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //убираю заголовок приложения
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //подключаем лауер-файл с элементами
        setContentView(R.layout.activity_hero);
        //подключаю глобальные переменные для хранения данных о герое
        final MyHero app = ((MyHero)getApplicationContext());
        //пишу в конф. последнее активити локации
        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        conf.setLAST_LOCAL(sPref.getString(PREF_LOCATION, "ua.azigar.client.intent.action.LocalActivity1")); //пишу в конф. последнее активити
        intent = new Intent(conf.getLAST_LOCAL()); //подключаю к новуму Intent последнее активити локации
        //пишу данные переменные сокета для отправки
        conf.setID(app.getID()); //cохраняю ID игрока
        //инициализирую обьекты
        imgHero = (ImageView) findViewById(R.id.imgHero);
            imgHero.setImageResource(getResources().getIdentifier(app.getAVATAR(), "drawable", getPackageName()));
        pbConnect = (ProgressBar) findViewById(R.id.pbConnect);
        data_checking = (TextView) findViewById(R.id.data_checking);
        name_lvl = (TextView) findViewById(R.id.name_lvl);
            name = app.getNAME() + " [" + app.getLVL() + "]"; //пишу имя и уровень героя
        pvp_lvl = (TextView) findViewById(R.id.pvp_lvl); //звание
            if (app.getTITLE().equalsIgnoreCase("") || app.getTITLE().equalsIgnoreCase(null)) {
                pvp_lvl.setText("нет звания");
            } else pvp_lvl.setText(app.getTITLE());
        exp = (TextView) findViewById(R.id.exp);
            exp.setText("Опыт - " + app.getEXP() + ", до следующего уровня осталось " + (Integer.parseInt(app.getMAX_EXP()) - Integer.parseInt(app.getEXP())));
        pvp_exp = (TextView) findViewById(R.id.pvp_exp);
            pvp_exp.setText("Храбрость - " + app.getPVP_EXP() + ", до следующего звания осталось " + (Integer.parseInt(app.getMAX_PVP_EXP()) - Integer.parseInt(app.getPVP_EXP())));
        txt_str = (TextView) findViewById(R.id.txt_str);
        txt_min_uron = (TextView) findViewById(R.id.txt_min_uron);
        txt_max_uron = (TextView) findViewById(R.id.txt_max_uron);
        txt_dex = (TextView) findViewById(R.id.txt_dex);
        txt_inst = (TextView) findViewById(R.id.txt_inst);
        txt_def = (TextView) findViewById(R.id.txt_def);
        txt_hp = (TextView) findViewById(R.id.txt_hp);
        txt_mana = (TextView) findViewById(R.id.txt_mana);
        str = (TextView) findViewById(R.id.str);
        min_uron = (TextView) findViewById(R.id.min_uron);
        max_uron = (TextView) findViewById(R.id.max_uron);
        dex = (TextView) findViewById(R.id.dex);
        inst = (TextView) findViewById(R.id.inst);
        def = (TextView) findViewById(R.id.def);
        hp = (TextView) findViewById(R.id.hp);
            hp.setText(app.getHP() + "/" + app.getMAX_HP());
        mana = (TextView) findViewById(R.id.mana);
            if (Integer.parseInt(app.getMAX_MANA()) == 1) mana.setText("0/0");
            else mana.setText(app.getMANA() + "/" + app.getMAX_MANA())  ;
        fraction = (TextView) findViewById(R.id.fraction);
        school = (TextView) findViewById(R.id.school);
        my_uron = (TextView) findViewById(R.id.my_uron);
        enemy_uron = (TextView) findViewById(R.id.enemy_uron);
        pve_v = (TextView) findViewById(R.id.pve_v);
        pve_l = (TextView) findViewById(R.id.pve_l);
        pvp_v = (TextView) findViewById(R.id.pvp_v);
        pvp_l = (TextView) findViewById(R.id.pvp_l);

        DisableAll(); //прячу все элементы активити

        //ханлер
        h = new Handler() {
            Thread t;
            String cmd;
            public void handleMessage(android.os.Message msg) {
                String m;
                switch (msg.what) {
                    case 1:  //подключение к серверу успешно
                        conf.setSOCKET_MESSAGE("ENTER");
                        break;

                    case 2:  //Не удалось подключится к серверу
                        Toast.makeText(HeroActivity.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        break;

                    case 3:  //Связь разорвалась
                        Toast.makeText(HeroActivity.this, R.string.No_connected, Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        break;

                    case 4:  //получил рейтинг героя
                        m = (String) msg.obj;
                        name_lvl.setText(name + "   РЕЙТИНГ - " + m);
                        conf.setSOCKET_MESSAGE("STR");
                        break;

                    case 5:  //получил звание героя
                        m = (String) msg.obj;
                        pvp_lvl.setText(m);
                        conf.setSOCKET_MESSAGE("STR");
                        break;

                    case 6:  //получил силу героя
                        m = (String) msg.obj;
                        str.setText(m);
                        int min = (int) (Integer.parseInt(m) / 3);
                        int max = (int) (Integer.parseInt(m) + (Integer.parseInt(m) / 3));
                        min_uron.setText(String.valueOf(min));
                        max_uron.setText(String.valueOf(max));
                        conf.setSOCKET_MESSAGE("DEX");
                        break;

                    case 7:  //получил ловкость героя
                        m = (String) msg.obj;
                        dex.setText(m);
                        conf.setSOCKET_MESSAGE("INST");
                        break;

                    case 8:  //получил интуицию героя
                        m = (String) msg.obj;
                        inst.setText(m);
                        conf.setSOCKET_MESSAGE("DEF");
                        break;

                    case 9:  //получил защиту героя
                        m = (String) msg.obj;
                        def.setText(m);
                        conf.setSOCKET_MESSAGE("MY_URON");
                        break;

                    case 10:  //получил мак. нанесенный урон героя
                        m = (String) msg.obj;
                        my_uron.setText("Мак. нанесенный урон - " + m);
                        conf.setSOCKET_MESSAGE("ENEMY_URON");
                        break;

                    case 11:  //получил мак. полученый урон от врагов
                        m = (String) msg.obj;
                        enemy_uron.setText("Мак. полученый урон - " + m);
                        conf.setSOCKET_MESSAGE("PVP_V");
                        break;

                    case 12:  //получил к-во побед в ПвП
                        m = (String) msg.obj;
                        pvp_v.setText("К-во PvP-побед - " + m);
                        conf.setSOCKET_MESSAGE("PVP_L");
                        break;

                    case 13:  //получил к-во поражений в ПвП
                        m = (String) msg.obj;
                        pvp_l.setText("К-во PvP-поражений - " + m);
                        conf.setSOCKET_MESSAGE("PVE_V");
                        break;

                    case 14:  //получил к-во побед в ПвЕ
                        m = (String) msg.obj;
                        pve_v.setText("К-во PvE-побед - " + m);
                        conf.setSOCKET_MESSAGE("PVE_L");
                        break;

                    case 15:  //получил к-во поражений в ПвЕ
                        m = (String) msg.obj;
                        pve_l.setText("К-во PvE-поражений - " + m);
                        Downloaded(); //показываю главное окно
                        conf.setSOCKET_MESSAGE("END");
                        break;
                }
            }
        };
        new Hero(h, conf); //запуск сокет-конекта
    }

    //показываю все элементы активити
    static void EnableAll() {
        imgHero.setVisibility(View.VISIBLE);
        name_lvl.setVisibility(View.VISIBLE);
        pvp_lvl.setVisibility(View.VISIBLE);
        exp.setVisibility(View.VISIBLE);
        pvp_exp.setVisibility(View.VISIBLE);
        txt_str.setVisibility(View.VISIBLE);
        txt_min_uron.setVisibility(View.VISIBLE);
        txt_max_uron.setVisibility(View.VISIBLE);
        txt_dex.setVisibility(View.VISIBLE);
        txt_inst.setVisibility(View.VISIBLE);
        txt_def.setVisibility(View.VISIBLE);
        txt_hp.setVisibility(View.VISIBLE);
        txt_mana.setVisibility(View.VISIBLE);
        str.setVisibility(View.VISIBLE);
        min_uron.setVisibility(View.VISIBLE);
        max_uron.setVisibility(View.VISIBLE);
        dex.setVisibility(View.VISIBLE);
        inst.setVisibility(View.VISIBLE);
        def.setVisibility(View.VISIBLE);
        hp.setVisibility(View.VISIBLE);
        mana.setVisibility(View.VISIBLE);
        fraction.setVisibility(View.VISIBLE);
        school.setVisibility(View.VISIBLE);
        my_uron.setVisibility(View.VISIBLE);
        enemy_uron.setVisibility(View.VISIBLE);
        pve_v.setVisibility(View.VISIBLE);
        pve_l.setVisibility(View.VISIBLE);
        pvp_v.setVisibility(View.VISIBLE);
        pvp_l.setVisibility(View.VISIBLE);
    }

    //прячу все элементы
    static void DisableAll() {
        imgHero.setVisibility(View.GONE);
        name_lvl.setVisibility(View.GONE);
        pvp_lvl.setVisibility(View.GONE);
        exp.setVisibility(View.GONE);
        pvp_exp.setVisibility(View.GONE);
        txt_str.setVisibility(View.GONE);
        txt_min_uron.setVisibility(View.GONE);
        txt_max_uron.setVisibility(View.GONE);
        txt_dex.setVisibility(View.GONE);
        txt_inst.setVisibility(View.GONE);
        txt_def.setVisibility(View.GONE);
        txt_hp.setVisibility(View.GONE);
        txt_mana.setVisibility(View.GONE);
        str.setVisibility(View.GONE);
        min_uron.setVisibility(View.GONE);
        max_uron.setVisibility(View.GONE);
        dex.setVisibility(View.GONE);
        inst.setVisibility(View.GONE);
        def.setVisibility(View.GONE);
        hp.setVisibility(View.GONE);
        mana.setVisibility(View.GONE);
        fraction.setVisibility(View.GONE);
        school.setVisibility(View.GONE);
        my_uron.setVisibility(View.GONE);
        enemy_uron.setVisibility(View.GONE);
        pve_v.setVisibility(View.GONE);
        pve_l.setVisibility(View.GONE);
        pvp_v.setVisibility(View.GONE);
        pvp_l.setVisibility(View.GONE);
    }

    //прячу все элементы загрузки
    static void DisableLoad() {
        pbConnect.setVisibility(View.GONE);
        data_checking.setVisibility(View.GONE);
    }

    //когда все данные загрузились
    static void Downloaded() {
        EnableAll();  //показываю все обьекты
        DisableLoad(); //прячу обьекты закрузки
    }

    //обработка кнопки назад
    @Override
    public void onBackPressed () { startActivity(intent); }

    //обработка закрытия (уничтожения) активити
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
