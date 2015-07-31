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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ua.azigar.client.Client.Opponent;
import ua.azigar.client.Resources.SocketConfig;
import ua.azigar.client.Resources.DialogScreen;
import ua.azigar.client.Resources.MyHero;
import ua.azigar.client.Resources.NoName;


public class OpponentActivity extends ActionBarActivity {

    Handler h;
    AlertDialog dialog;
    SocketConfig conf = new SocketConfig(); //подключаю новый екземпляр conf для клиента регистрации
    Intent intent;
    SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:
    static final String PREF_LOCATION = "LOCATION";

    static TextView name_lvl, pvp_lvl, pvp_exp, txt_str, txt_min_uron, txt_max_uron, txt_dex, txt_inst, txt_def, txt_hp, txt_mana;
    static TextView str, min_uron, max_uron, dex, inst, def, hp, mana, data_checking;
    static TextView fraction, school, my_uron, enemy_uron, pve_v, pve_l, pvp_v, pvp_l;
    static ProgressBar pbConnect;
    static ImageView imgHero, imgVIP;
    static Button btnNext, btnAtk, btnSearch;
    static EditText edSearch;

    String dialogMessage;

    String idEnemy, lvlEnemy, hpEnemy, manaEnemy, nameEnemy, avatarEnemy, name, LAST, sexEnemy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //убираю ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //убираю заголовок приложения
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //подключаем лауер-файл с элементами
        setContentView(R.layout.activity_opponent);
        //подключаю глобальные переменные для хранения данных о герое
        final MyHero app = ((MyHero)getApplicationContext());
        //пишу в конф. последнее активити локации
        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        conf.setLAST_LOCAL(sPref.getString(PREF_LOCATION, "ua.azigar.client.intent.action.LocalActivity1")); //пишу в конф. последнее активити локации
        intent = new Intent(conf.getLAST_LOCAL()); //подключаю к новуму Intent последнее активити локации
        //пишу данные переменные сокета для отправки
        conf.setID(app.getID()); //cохраняю ID игрока
        conf.setNAME(app.getNAME()); //cохраняю имя героя
        //инициализирую обьекты
        imgHero = (ImageView) findViewById(R.id.imgHero);
        imgVIP = (ImageView) findViewById(R.id.imgVIP);
        pbConnect = (ProgressBar) findViewById(R.id.pbConnect);
        data_checking = (TextView) findViewById(R.id.data_checking);
        name_lvl = (TextView) findViewById(R.id.name_lvl);
        pvp_lvl = (TextView) findViewById(R.id.pvp_lvl); //звание
        pvp_exp = (TextView) findViewById(R.id.pvp_exp);
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
        mana = (TextView) findViewById(R.id.mana);
        fraction = (TextView) findViewById(R.id.fraction);
        school = (TextView) findViewById(R.id.school);
        my_uron = (TextView) findViewById(R.id.my_uron);
        enemy_uron = (TextView) findViewById(R.id.enemy_uron);
        pve_v = (TextView) findViewById(R.id.pve_v);
        pve_l = (TextView) findViewById(R.id.pve_l);
        pvp_v = (TextView) findViewById(R.id.pvp_v);
        pvp_l = (TextView) findViewById(R.id.pvp_l);
        btnAtk = (Button) findViewById(R.id.btnAtk);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        edSearch =  (EditText) findViewById(R.id.edSearch);

        Disable(); //прячу все элементы активити

        //ханлер
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                String m;
                switch (msg.what) {
                    case 1:  //подключение к серверу успешно
                        conf.setSOCKET_MESSAGE("ENTER");
                        break;

                    case 2:  //Не удалось подключится к серверу
                        startActivity(intent);
                        break;

                    case 3:  //Связь разорвалась
                         startActivity(intent);
                        break;

                    case 4:  //получил имя оппонента
                        nameEnemy = (String) msg.obj;
                        name = nameEnemy; //пишу имя
                        conf.setSOCKET_MESSAGE("LVL");
                        break;

                    case 5:  //получил уровень оппонента
                        lvlEnemy = (String) msg.obj;
                        name = name + " [" + lvlEnemy + "]";
                        conf.setSOCKET_MESSAGE("TITLE");
                        break;

                    case 6:  //получил звание оппонента
                        m = (String) msg.obj;
                        pvp_lvl.setText(m);
                        conf.setSOCKET_MESSAGE("PVP_EXP");
                        break;

                    case 7:  //получил Храбрость оппонента
                        m = (String) msg.obj;
                        pvp_exp.setText("Храбрость - " + m);
                        conf.setSOCKET_MESSAGE("HP");
                        break;

                    case 8:  //получил здоровье оппонента
                        hpEnemy = (String) msg.obj;
                        hp.setText(hpEnemy);
                        conf.setSOCKET_MESSAGE("MANA");
                        break;

                    case 9:  //получил ману оппонента
                        manaEnemy = (String) msg.obj;
                        mana.setText(manaEnemy);
                        conf.setSOCKET_MESSAGE("VIP");
                        break;

                    case 10:  //получил VIP оппонента
                        m = (String) msg.obj;
                        imgVIP.setImageResource(getResources().getIdentifier("vip" + m, "drawable", getPackageName()));
                        conf.setSOCKET_MESSAGE("AVATAR");
                        break;

                    case 11:  //получил аватар оппонента
                        avatarEnemy = (String) msg.obj;
                        imgHero.setImageResource(getResources().getIdentifier(avatarEnemy, "drawable", getPackageName()));
                        conf.setSOCKET_MESSAGE("RATING");
                        break;

                    case 12:  //получил рейтинг оппонента
                        m = (String) msg.obj;
                        name_lvl.setText(name + "   РЕЙТИНГ - " + m);
                        conf.setSOCKET_MESSAGE("STR");
                        break;

                    case 13:  //получил силу героя
                        m = (String) msg.obj;
                        str.setText(m);
                        int min = (int) (Integer.parseInt(m) / 3);
                        int max = (int) (Integer.parseInt(m) + (Integer.parseInt(m) / 3));
                        min_uron.setText(String.valueOf(min));
                        max_uron.setText(String.valueOf(max));
                        conf.setSOCKET_MESSAGE("DEX");
                        break;

                    case 14:  //получил ловкость героя
                        m = (String) msg.obj;
                        dex.setText(m);
                        conf.setSOCKET_MESSAGE("INST");
                        break;

                    case 15:  //получил интуицию героя
                        m = (String) msg.obj;
                        inst.setText(m);
                        conf.setSOCKET_MESSAGE("DEF");
                        break;

                    case 16:  //получил защиту героя
                        m = (String) msg.obj;
                        def.setText(m);
                        conf.setSOCKET_MESSAGE("MY_URON");
                        break;

                    case 17:  //получил мак. нанесенный урон героя
                        m = (String) msg.obj;
                        my_uron.setText("Мак. нанесенный урон - " + m);
                        conf.setSOCKET_MESSAGE("ENEMY_URON");
                        break;

                    case 18:  //получил мак. полученый урон от врагов
                        m = (String) msg.obj;
                        enemy_uron.setText("Мак. полученый урон - " + m);
                        conf.setSOCKET_MESSAGE("PVP_V");
                        break;

                    case 19:  //получил к-во побед в ПвП
                        m = (String) msg.obj;
                        pvp_v.setText("К-во PvP-побед - " + m);
                        conf.setSOCKET_MESSAGE("PVP_L");
                        break;

                    case 20:  //получил к-во поражений в ПвП
                        m = (String) msg.obj;
                        pvp_l.setText("К-во PvP-поражений - " + m);
                        conf.setSOCKET_MESSAGE("PVE_V");
                        break;

                    case 21:  //получил к-во побед в ПвЕ
                        m = (String) msg.obj;
                        pve_v.setText("К-во PvE-побед - " + m);
                        conf.setSOCKET_MESSAGE("PVE_L");
                        break;

                    case 22:  //получил к-во поражений в ПвЕ
                        m = (String) msg.obj;
                        pve_l.setText("К-во PvE-поражений - " + m);
                        conf.setSOCKET_MESSAGE("SEX");
                        break;

                    case 23:  //получил ID бота-клона
                        idEnemy = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("LAST");
                        break;

                    case 24:  //узнал, последний ли этот герой в списке
                        LAST = (String) msg.obj;
                        Enable(); //показываю главное окно
                        break;

                    case 25:  //сервер не нашел нужного героя
                        edSearch.setText("");
                        dialogMessage = "Героя \"" + conf.getNAME_ENEMY() + "\" в Мидгарде не существует.";
                        dialog = DialogScreen.getDialog(OpponentActivity.this, DialogScreen.DIALOG_OPPONENT_FOUND, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 26:  //получил пол героя
                        sexEnemy = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("ID");
                        break;
                }
            }
        };
        new Opponent(h, conf); //запуск сокет-конекта
    }

    //обработчи кнопки Напасть
    public void onClickAtk(View v) {
        intent = new Intent("ua.azigar.client.intent.action.FIGHT");
        intent.putExtra("id", idEnemy);
        intent.putExtra("lvl", lvlEnemy);
        intent.putExtra("hp", hpEnemy);
        intent.putExtra("mana", manaEnemy);
        intent.putExtra("name", nameEnemy);
        intent.putExtra("avatar", avatarEnemy);
        intent.putExtra("sex", sexEnemy);
        startActivity(intent);
    }

    //обработчи кнопки Следующий
    public void onClickNext(View v) {
        Disable();
        if (LAST.equalsIgnoreCase("NO_LAST")) { //если в списке еще есть оппоненты
            conf.setSOCKET_MESSAGE("NEXT"); //тогда пусть присылает данные следующего
        } else conf.setSOCKET_MESSAGE("SELECT"); //заново запрашиваю всех подходящих оппонентов
    }

    //обработчи кнопки Поиск по имени
    public void onClickSearch(View v) {
        boolean isYes = true;
        NoName noName = new NoName();
        //если имя не введено
        if(edSearch.getText().toString().equalsIgnoreCase("") || edSearch.getText().toString().equalsIgnoreCase(null)){
            dialogMessage = "Для поиска оппонента по имени, нужно все таки ввести имя";
            dialog = DialogScreen.getDialog(OpponentActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            isYes = false;
        }
        if(edSearch.getText().toString().equalsIgnoreCase(conf.getNAME())){  //но это имя этого героя
            dialogMessage = "Если хотите посмотреть хар-ки своего героя, то выберите соответствующий пункт меню";
            dialog = DialogScreen.getDialog(OpponentActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            isYes = false;
        }
        if (noName.NoName(edSearch.getText().toString()) == false){ //если имя героя совпадает с командой обмена данными
            dialogMessage = "Героя \"" + edSearch.getText().toString() + "\" в Мидгарде не существует.";
            dialog = DialogScreen.getDialog(OpponentActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            isYes = false;
        }
        if (isYes == true){
            noName = null;
            Disable();
            conf.setNAME_ENEMY(edSearch.getText().toString()); //сохраняю в конф. имя искаемого героя
            conf.setSOCKET_MESSAGE("SEARCH"); //запрашиваю поиск по имени
            noName = null;
        } else {
            edSearch.setText("");
            dialog.show();
        }
    }

    //показываю все элементы активити
    static void EnableAll() {
        imgHero.setVisibility(View.VISIBLE);
        name_lvl.setVisibility(View.VISIBLE);
        pvp_lvl.setVisibility(View.VISIBLE);
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
        imgVIP.setVisibility(View.VISIBLE);
        btnSearch.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        btnAtk.setVisibility(View.VISIBLE);
        edSearch.setVisibility(View.VISIBLE);
    }

    //прячу все элементы
    static void DisableAll() {
        imgHero.setVisibility(View.GONE);
        name_lvl.setVisibility(View.GONE);
        pvp_lvl.setVisibility(View.GONE);
        imgVIP.setVisibility(View.GONE);
        btnSearch.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        btnAtk.setVisibility(View.GONE);
        edSearch.setVisibility(View.GONE);
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

    //прячу все элементы загрузки
    static void EnableLoad() {
        pbConnect.setVisibility(View.VISIBLE);
        data_checking.setVisibility(View.VISIBLE);
    }

    //когда все данные загрузились
    static void Enable() {
        EnableAll();  //показываю все обьекты
        DisableLoad(); //прячу обьекты закрузки
    }

    //когда все данные загрузились
    static void Disable() {
        DisableAll();  //показываю все обьекты
        EnableLoad(); //прячу обьекты закрузки
    }

    //обработка кнопки назад
    @Override
    public void onBackPressed () {
        conf.setSOCKET_MESSAGE("END");
        startActivity(intent);
    }

    //обработка закрытия (уничтожения) активити
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
