package ua.azigar.client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import ua.azigar.client.Client.Inventar;
import ua.azigar.client.Resources.DialogScreen;
import ua.azigar.client.Resources.ImageAdapter;
import ua.azigar.client.Resources.MyHero;
import ua.azigar.client.Resources.SocketConfig;

/**
 * Created by Azigar on 05.08.2015.
 */
public class InventarActivity extends ActionBarActivity implements View.OnClickListener {

    SocketConfig conf = new SocketConfig();
    static ImageButton imgHelmet, imgArmor, imgBoots, imgRightHand, imgLeftHand, imgGloves, imgRightRing, imgAmulet, imgLeftRing;
    static ProgressBar pbConnect;
    static TextView data_checking, info, txtSizeInventar;
    static Gallery gallery;
    static Button btnWear, btnShoot, btnThrow;
    static FrameLayout invetarLayour;
    static ImageAdapter imageAdapter;
    static ArrayList<String> infoObjHero; //массыв для хранение информации о предмете, который одет на герое
    static ArrayList<String> lvlObjHero; //массыв для хранение информации о уровен предмета, который одет на герое
    static ArrayList<String> picObjHero; //массыв для хранение информации о картинке предмета, который одет на герое
    static ArrayList<String> twoObjHero;  //массыв для хранение информации о двуручности предмета, который одет на герое
    static ArrayList<String> idObjInventar; //массыв для хранение ID предмета для создания галереи
    static ArrayList<String> infoObjInventar;  //массыв для хранение информации о предмете в рюкзаке
    static ArrayList<String> wearObjInventar;  //массыв для хранение информации о возвожности одеть предмет в рюкзаке
    static ArrayList<String> lvlHeroInventar;  //массыв для хранение информации о необходимом уровне для героя, что бы одеть предмет в рюкзаке
    static ArrayList<String> lvlObjInventar;  //массыв для хранение информации  о уровене предмета в рюкзаке
    static ArrayList<String> picObjInventar;  //массыв для хранение информации  о картинтке предмета в рюкзаке
    static ArrayList<String> qObjInventar;  //массыв для хранение информации о необходимости спрашивать, на какую руку одеть вещь
    static ArrayList<String> qvObjInventar;  //массыв для хранение информации о том квестовая вещь или нет
    static int flagWear = 0;
    int ID;
    String dialogMessage;
    static Intent intent;
    static SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:
    static final String PREF_LOCATION = "LOCATION";
    static Handler h;
    AlertDialog.Builder builder;
    static AlertDialog dialog;
    static String wear, qwear, shoot, shoot_wear, lvlHero, freeInventar, sizeInventar, helmet, right_hand, left_hand, armor, boots, gloves, amulet, right_ring, left_ring, hp, mana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //убираю ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //убираю заголовок приложения
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //подключаем лауер-файл с элементами
        setContentView(R.layout.activity_inventar);
        //читаю intent-данные от предыдущего активити и сохраняю текущие значение здоровья и манны
        intent = getIntent();
        this.hp = intent.getStringExtra("hp");
        this.mana = intent.getStringExtra("mana");
        //подключаю глобальные переменные для хранения данных о герое
        final MyHero app = ((MyHero)getApplicationContext());
        //пишу в конф. последнее активити локации
        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        conf.setLAST_LOCAL(sPref.getString(PREF_LOCATION, "ua.azigar.client.intent.action.LocalActivity1")); //пишу в конф. последнее активити локации
        intent = new Intent(conf.getLAST_LOCAL()); //подключаю к новуму Intent последнее активити локации
        //пишу данные переменные сокета для отправки
        conf.setID(app.getID()); //cохраняю ID игрока
        lvlHero = app.getLVL(); //cохраняю уровень героя

        builder = new AlertDialog.Builder(this);

        //инициализирую массивы
        idObjInventar = new ArrayList<String>(); //массыв для хранение ID предмета для создания галереи
        infoObjHero = new ArrayList<String>(); //массыв для хранение информации о предмете, который одет на герое
        lvlObjHero = new ArrayList<String>();
        picObjHero = new ArrayList<String>();
        twoObjHero = new ArrayList<String>();  //массыв для хранение информации о двуручности предмета, который одет на герое
        infoObjInventar = new ArrayList<String>();  //массыв для хранение информации о предмете в рюкзаке
        wearObjInventar = new ArrayList<String>();  //массыв для хранение информации о возвожности одеть предмет в рюкзаке
        lvlHeroInventar = new ArrayList<String>();  //массыв для хранение информации о необходимом уровне для героя, что бы одеть предмет в рюкзаке
        lvlObjInventar = new ArrayList<String>();
        picObjInventar = new ArrayList<String>();
        qObjInventar = new ArrayList<String>();  //массыв для хранение информации о необходимости спрашивать, на какую руку одеть вещь
        qvObjInventar = new ArrayList<String>();

        //найдем View-элементы
        invetarLayour = (FrameLayout) findViewById(R.id.invetarLayour);
        imgGloves = (ImageButton) findViewById(R.id.imgGloves);
        imgRightRing = (ImageButton) findViewById(R.id.imgRightRing);
        imgAmulet = (ImageButton) findViewById(R.id.imgAmulet);
        imgLeftRing = (ImageButton) findViewById(R.id.imgLeftRing);
        imgHelmet = (ImageButton) findViewById(R.id.imgHelmet);
        imgArmor = (ImageButton) findViewById(R.id.imgArmor);
        imgBoots = (ImageButton) findViewById(R.id.imgBoots);
        imgRightHand = (ImageButton) findViewById(R.id.imgRightHand);
        imgLeftHand = (ImageButton) findViewById(R.id.imgLeftHand);
        pbConnect = (ProgressBar) findViewById(R.id.pbConnect);
        data_checking = (TextView) findViewById(R.id.data_checking);
        info = (TextView) findViewById(R.id.info);
        txtSizeInventar = (TextView) findViewById(R.id.txtSizeInventar);
        gallery = (Gallery) findViewById(R.id.gallery);
        btnWear = (Button) findViewById(R.id.btnWear);
        btnShoot = (Button) findViewById(R.id.btnShoot);
        btnThrow = (Button) findViewById(R.id.btnThrow);

        // присваиваем обработчик кнопкам
        imgGloves.setOnClickListener(this);
        imgRightRing.setOnClickListener(this);
        imgAmulet.setOnClickListener(this);
        imgLeftRing.setOnClickListener(this);
        imgHelmet.setOnClickListener(this);
        imgArmor.setOnClickListener(this);
        imgBoots.setOnClickListener(this);
        imgRightHand.setOnClickListener(this);
        imgLeftHand.setOnClickListener(this);
        btnWear.setOnClickListener(this);
        btnShoot.setOnClickListener(this);
        btnThrow.setOnClickListener(this);

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
                        Toast.makeText(InventarActivity.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        finish(); //закрываю активити
                        break;

                    case 3:  //системная ошибка
                        startActivity(intent);
                        break;

                    case 4:  //получил шлем героя
                        helmet = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("RIGHT_HAND");
                        break;

                    case 5:  //получил оружие в правой руке
                        right_hand = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("LEFT_HAND");
                        break;

                    case 6:  //получил оружие в левой руке
                        left_hand = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("ARMOR");
                        break;

                    case 7:  //получил бронинь
                        armor = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("BOOTS");
                        break;

                    case 8:  //получил обувь
                        boots = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("GLOVES");
                        break;

                    case 9:  //получил рукавицы
                        gloves = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("AMULET");
                        break;

                    case 10:  //получил амулет
                        amulet = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("RIGHT_RING");
                        break;

                    case 11:  //получил правое кольцо
                        right_ring = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("LEFT_RING");
                        break;

                    case 12:  //получил левое кольцо
                        left_ring = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("FREE_INVENTAR");
                        break;

                    case 13:  //сервер пристал одеваемый премет или нет
                        m = (String) msg.obj;
                        wearObjInventar.add(m);
                        conf.setSOCKET_MESSAGE("GET_LVL_HERO_INVENTAR");
                        break;

                    case 14:  //нет предметов в инвентаре
                        flagWear = 1;
                        //обнуляю массивы
                        idObjInventar = new ArrayList<String>(); //массыв для хранение ID предмета для создания галереи
                        qObjInventar = new ArrayList<String>();  //массыв для хранение информации о необходимости спрашивать, на какую руку одеть вещь
                        picObjInventar = new ArrayList<String>();
                        picObjInventar.add("0");
                        //загружаю галерею
                        imageAdapter  = new ImageAdapter(InventarActivity.this, picObjInventar); //создаю адаптер для галереи
                        gallery.setAdapter(imageAdapter); //подключаю адаптер к галереи
                        invetarLayour.setBackgroundResource(getResources().getIdentifier("inventg1", "drawable", getPackageName()));
                        gallery.setBackgroundResource(R.drawable.inventg1);
                        // Устанавливаем действия, которые будут выполнены при выделении элемента
                        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                                btnThrow.setVisibility(View.GONE);
                                btnShoot.setVisibility(View.GONE);
                                btnWear.setVisibility(View.GONE);
                                info.setText("РЮКЗАК ПУСТ");
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                            }
                        });
                        Enable();
                        break;

                    case 15:  //сервер прислал инфу о предмете на герое
                        m = (String) msg.obj;
                        infoObjHero.add(m);
                        conf.setSOCKET_MESSAGE("GET_LVL_OBJ_HERO");
                        break;

                    case 16:  //сервер прислал ответ на последнюю вещь в списке ГЕРОЙ
                        m = (String) msg.obj;
                        if (m.equalsIgnoreCase("NO_LAST_INFO_OBJ_HERO")){ //если надо еще узнавать инфу о предметах
                            conf.setSOCKET_MESSAGE("NEXT_INFO_OBJ_HERO");
                        }else{
                            //метод прорисовки вещей, одетых на герое
                            if (Integer.parseInt(helmet) > 0){
                                imgHelmet.setBackgroundResource(getResources().getIdentifier("invent" + lvlObjHero.get(0), "drawable", getPackageName()));
                                imgHelmet.setImageResource(getResources().getIdentifier("obj" + picObjHero.get(0), "drawable", getPackageName()));
                            }else{
                                imgHelmet.setImageResource(getResources().getIdentifier("helmet", "drawable", getPackageName()));
                                imgHelmet.setBackgroundResource(getResources().getIdentifier("invent1", "drawable", getPackageName()));
                            }
                            if (Integer.parseInt(right_hand) > 0){
                                imgRightHand.setBackgroundResource(getResources().getIdentifier("invent" + lvlObjHero.get(1), "drawable", getPackageName()));
                                imgRightHand.setImageResource(getResources().getIdentifier("obj" + picObjHero.get(1), "drawable", getPackageName()));
                            }else{
                                imgRightHand.setImageResource(getResources().getIdentifier("right_hand", "drawable", getPackageName()));
                                imgRightHand.setBackgroundResource(getResources().getIdentifier("invent1", "drawable", getPackageName()));
                            }
                            if (Integer.parseInt(left_hand) > 0){
                                imgLeftHand.setBackgroundResource(getResources().getIdentifier("invent" + lvlObjHero.get(2), "drawable", getPackageName()));
                                imgLeftHand.setImageResource(getResources().getIdentifier("obj" + picObjHero.get(2), "drawable", getPackageName()));
                            }else{
                                imgLeftHand.setImageResource(getResources().getIdentifier("left_hand", "drawable", getPackageName()));
                                imgLeftHand.setBackgroundResource(getResources().getIdentifier("invent1", "drawable", getPackageName()));
                            }
                            if (Integer.parseInt(armor) > 0){
                                imgArmor.setImageResource(getResources().getIdentifier("obj" + picObjHero.get(3), "drawable", getPackageName()));
                                imgArmor.setBackgroundResource(getResources().getIdentifier("invent" + lvlObjHero.get(3), "drawable", getPackageName()));
                            }else{
                                imgArmor.setImageResource(getResources().getIdentifier("armor", "drawable", getPackageName()));
                                imgArmor.setBackgroundResource(getResources().getIdentifier("invent1", "drawable", getPackageName()));
                            }
                            if (Integer.parseInt(boots) > 0){
                                imgBoots.setImageResource(getResources().getIdentifier("obj" + picObjHero.get(4), "drawable", getPackageName()));
                                imgBoots.setBackgroundResource(getResources().getIdentifier("invent" + lvlObjHero.get(4), "drawable", getPackageName()));
                            }else{
                                imgBoots.setImageResource(getResources().getIdentifier("boots", "drawable", getPackageName()));
                                imgBoots.setBackgroundResource(getResources().getIdentifier("invent1", "drawable", getPackageName()));
                            }
                            if (Integer.parseInt(gloves) > 0){
                                imgGloves.setImageResource(getResources().getIdentifier("obj" + picObjHero.get(5), "drawable", getPackageName()));
                                imgGloves.setBackgroundResource(getResources().getIdentifier("invent" + lvlObjHero.get(5), "drawable", getPackageName()));
                            }else{
                                imgGloves.setImageResource(getResources().getIdentifier("gloves", "drawable", getPackageName()));
                                imgGloves.setBackgroundResource(getResources().getIdentifier("invent1", "drawable", getPackageName()));
                            }
                            if (Integer.parseInt(amulet) > 0){
                                imgAmulet.setImageResource(getResources().getIdentifier("obj" + picObjHero.get(6), "drawable", getPackageName()));
                                imgAmulet.setBackgroundResource(getResources().getIdentifier("invent" + lvlObjHero.get(6), "drawable", getPackageName()));
                            }else{
                                imgAmulet.setImageResource(getResources().getIdentifier("amulet", "drawable", getPackageName()));
                                imgAmulet.setBackgroundResource(getResources().getIdentifier("invent1", "drawable", getPackageName()));
                            }
                            if (Integer.parseInt(right_ring) > 0){
                                imgRightRing.setImageResource(getResources().getIdentifier("obj" + picObjHero.get(7), "drawable", getPackageName()));
                                imgRightRing.setBackgroundResource(getResources().getIdentifier("invent" + lvlObjHero.get(7), "drawable", getPackageName()));
                            }else{
                                imgRightRing.setImageResource(getResources().getIdentifier("right_ring", "drawable", getPackageName()));
                                imgRightRing.setBackgroundResource(getResources().getIdentifier("invent1", "drawable", getPackageName()));
                            }
                            if (Integer.parseInt(left_ring) > 0){
                                imgLeftRing.setImageResource(getResources().getIdentifier("obj" + picObjHero.get(8), "drawable", getPackageName()));
                                imgLeftRing.setBackgroundResource(getResources().getIdentifier("invent" + lvlObjHero.get(8), "drawable", getPackageName()));
                            }else{
                                imgLeftRing.setImageResource(getResources().getIdentifier("left_ring", "drawable", getPackageName()));
                                imgLeftRing.setBackgroundResource(getResources().getIdentifier("invent1", "drawable", getPackageName()));
                            }
                            conf.setSOCKET_MESSAGE("GET_INVENTAR");
                        }
                        break;

                    case 17:  //сервер прислал ID предмета с рюкзака
                        m = (String) msg.obj;
                        idObjInventar.add(m);
                        conf.setSOCKET_MESSAGE("GET_WEAR_OBJ_INVENTAR");
                        break;

                    case 18:  //сервер прислал инфу о предмете в рюкзаке
                        m = (String) msg.obj;
                        infoObjInventar.add(m);
                        conf.setSOCKET_MESSAGE("LAST_INFO_OBJ_INVENTAR");
                        break;

                    case 19:  //сервер прислал ответ на последнюю вещь в списке РЮКЗАК
                        m = (String) msg.obj;
                        if (m.equalsIgnoreCase("NO_LAST_INFO_OBJ_INVENTAR")){ //если надо еще узнавать инфу о предметах
                            conf.setSOCKET_MESSAGE("NEXT_INFO_OBJ_INVENTAR");
                        }else{
                            flagWear = 0;
                            //загружаю галерею         // Устанавливаем адаптер
                            imageAdapter = new ImageAdapter(InventarActivity.this, picObjInventar); //создаю адаптер для галереи
                            gallery.setAdapter(imageAdapter); //подключаю адаптер к галереи
                            //Выбираю элемет
                            if(idObjInventar.size() > 0) gallery.setSelection(0); // выделяю первый элемент галереи
                            if(idObjInventar.size() > 2) gallery.setSelection(1);
                            if(idObjInventar.size() > 4) gallery.setSelection(2);
                            // Устанавливаем действия, которые будут выполнены при выделении элемента
                            gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                                    gallery.setBackgroundResource(getResources().getIdentifier("inventg" + lvlObjInventar.get((int) id), "drawable", getPackageName()));
                                    invetarLayour.setBackgroundResource(getResources().getIdentifier("inventg" + lvlObjInventar.get((int) id), "drawable", getPackageName()));
                                        wear = idObjInventar.get((int) id); //пишу ID предмета в переменную для ОДЕТЬ
                                        qwear = qObjInventar.get((int) id); //запоминаю, надо задавать вопрос куда одеть или нет
                                    ID = (int) id;
                                    DisEn2((int) id); //меняю кнопки местами
                                    info.setText(infoObjInventar.get((int) id));
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                }
                            });
                            Enable();
                        }
                        break;

                    case 20:  //сервер пристал необходимый уровень героя для предмета в рюкзаке
                        m = (String) msg.obj;
                        lvlHeroInventar.add(m);
                        conf.setSOCKET_MESSAGE("GET_QUESTION_OBJ_INVENTAR");
                        break;

                    case 21:  //сервер запрашивает ID предмета для снятия
                        conf.setSOCKET_MESSAGE(shoot);
                        break;

                    case 22:  //сервер прислал друвурчность предмета
                        m = (String) msg.obj;
                        twoObjHero.add(m);
                        conf.setSOCKET_MESSAGE("LAST_INFO_OBJ_HERO");
                        break;

                    case 23:  //сервер говорит, что рюкзак полный
                        if(idObjInventar.size() == 1) gallery.setSelection(0); // выделяю первый элемент галереи
                        if(idObjInventar.size() > 2) gallery.setSelection(1);
                        if(idObjInventar.size() > 4) gallery.setSelection(2);
                        Enable();
                        dialogMessage = FullInventar();
                        dialog = DialogScreen.getDialog(InventarActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 24:  //сервер спрашивает с какого места на теле героя снимать предмет
                        conf.setSOCKET_MESSAGE(shoot_wear);
                        break;

                    case 25:   //сервер говорит мак. к-во ОЗ героя
                        app.setMAX_HP((String) msg.obj);
                        if(Integer.parseInt(hp) > Integer.parseInt(app.getMAX_HP())) { //если текущее значение ОЗ больше чем новое мак. значение ОЗ
                            app.setHP(app.getMAX_HP());
                        }
                        conf.setSOCKET_MESSAGE("MAX_MANA"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 26:   //сервер говорит мак. к-во мани героя
                        app.setMAX_MANA((String) msg.obj);
                        if (Integer.parseInt(app.getMAX_MANA()) == 0) {
                            app.setIS_MANA("0");
                            app.setMAX_MANA("1");
                        }else{
                            app.setIS_MANA("1");
                        }
                        conf.setSOCKET_MESSAGE("INVENTAR"); //запрашиваю к-во маны героя
                        break;

                    case 27:   //сервер прислал к-во вещей в рюкзаке
                        freeInventar = (String) msg.obj;
                        conf.setSOCKET_MESSAGE("SIZE_INVENTAR"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 28:   //сервер прислал размер рюкзака
                        sizeInventar = (String) msg.obj;
                        txtSizeInventar.setText(freeInventar + "/" + sizeInventar);
                        conf.setSOCKET_MESSAGE("INFO_OBJ_HERO"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 29:  //сервер пристал куда одевать вещь (по-умолчанию или спрашивать на какую руку)
                        m = (String) msg.obj;
                        qObjInventar.add(m);
                        conf.setSOCKET_MESSAGE("GET_QVEST_OBJ_INVENTAR");
                        break;

                    case 30:  //отсылаю предмет который надо одеть или выбросить
                        conf.setSOCKET_MESSAGE(wear);
                        break;

                    case 31:  //отсылаю куда одеть предмет
                        if (Integer.parseInt(qwear) == 1){ //если надо выбирать куда одеть
                            dialogMessage = "Выберите руку";
                            dialog = DialogScreen.getDialog(InventarActivity.this, DialogScreen.DIALOG_TWO_HANDS, conf, dialogMessage);
                            dialog.show();
                        }else{
                            conf.setSOCKET_MESSAGE("POH");
                        }
                        break;

                    case 32:  //сервер пристал квестовая вещь или нет
                        m = (String) msg.obj;
                        qvObjInventar.add(m);
                        conf.setSOCKET_MESSAGE("GET_LVL_OBJ_INVENTAR");
                        break;

                    case 33:  //сервер прислал уровень предмета
                        m = (String) msg.obj;
                        lvlObjHero.add(m);
                        conf.setSOCKET_MESSAGE("GET_PIC_OBJ_HERO");
                        break;

                    case 34:  //сервер прислал уровень предмета
                        m = (String) msg.obj;
                        picObjHero.add(m);
                        conf.setSOCKET_MESSAGE("GET_TWO_OBJ_HERO");
                        break;

                    case 35:  //сервер прислал уровень предмета в рюкзаке
                        m = (String) msg.obj;
                        lvlObjInventar.add(m);
                        conf.setSOCKET_MESSAGE("GET_PIC_OBJ_INVENTAR");
                        break;

                    case 36:  //сервер прислал картинку предмета в рюкзаке
                        m = (String) msg.obj;
                        picObjInventar.add(m);
                        conf.setSOCKET_MESSAGE("GET_INFO_OBJ_INVENTAR");
                        break;
                }
            }
        };
        new Inventar(h, conf);
    }

    //рандом текста
    private static int randTxt() {
        Random random = new Random();
        return random.nextInt(3) + 1;
    }

    //текстовка о полном рюкзаке
    private String FullInventar() {
        String message = "";
        int txt = randTxt();
        switch (txt) {
            case 1:
                message = "Если я сниму эту вещь, то мне некуда будет её положить.";
                break;
            case 2:
                message = "Для начала освободи рюкзак.";
                break;
            case 3:
                message = "Рюкзак то нерезиновый.";
                break;
        }
        return message;
    }

    //обработчик нажатий на кнопки
    @Override
    public void onClick(View v) {
        // по id определеяем кнопку, вызвавшую этот обработчик
        switch (v.getId()) {
            case R.id.imgHelmet:
                shoot = helmet;
                shoot_wear = "SW_HELMET";
                DisEn1();
                info.setText(infoObjHero.get(0));
                break;

            case R.id.imgRightHand:
                shoot = right_hand;
                shoot_wear = "SW_RIGHT_HAND";
                DisEn1();
                info.setText(infoObjHero.get(1));
                break;

            case R.id.imgLeftHand:
                shoot = left_hand;
                shoot_wear = "SW_LEFT_HAND";
                DisEn1();
                info.setText(infoObjHero.get(2));
                 break;

            case R.id.imgArmor:
                shoot = armor;
                shoot_wear = "SW_ARMOR";
                DisEn1();
                info.setText(infoObjHero.get(3));
                break;

            case R.id.imgBoots:
                shoot = boots;
                shoot_wear = "SW_BOOTS";
                DisEn1();
                info.setText(infoObjHero.get(4));
                break;

            case R.id.imgGloves:
                shoot = gloves;
                shoot_wear = "SW_GLOVES";
                DisEn1();
                info.setText(infoObjHero.get(5));
                break;

            case R.id.imgAmulet:
                shoot = amulet;
                shoot_wear = "SW_AMULET";
                DisEn1();
                info.setText(infoObjHero.get(6));
                break;

            case R.id.imgRightRing:
                shoot = right_ring;
                shoot_wear = "SW_RIGHT_RING";
                DisEn1();
                info.setText(infoObjHero.get(7));
                break;

            case R.id.imgLeftRing:
                shoot = left_ring;
                shoot_wear = "SW_LEFT_RING";
                DisEn1();
                info.setText(infoObjHero.get(8));
                break;

            case R.id.btnWear:  //обработчик кнопки НАДЕТЬ
                boolean btn_wear = true;
                if (Integer.parseInt(wearObjInventar.get(ID)) != 1 && Integer.parseInt(qvObjInventar.get(ID)) == 1 && btn_wear == true){
                    btn_wear = false;  //если выбраную вещь нельзя одеть
                    dialogMessage = "Выбраный предмет нельзя одеть";
                }
                if ((Integer.parseInt(lvlHeroInventar.get(ID)) > Integer.parseInt(lvlHero)) && btn_wear == true){
                    btn_wear = false;  //если вещь старше по уровню
                    dialogMessage = "Что бы одеть эту вещь необходим " + lvlHeroInventar.get(ID) + "-й уровень";
                }
                if (btn_wear == true){
                    info.setText("");
                    data_checking.setText("Герой одевается...");
                    Disable(); //включаю вид загрузки
                    clearArrayList(); //очищаю все динамичесике массивы
                    conf.setSOCKET_MESSAGE("WEAR");
                }else{
                    dialog = DialogScreen.getDialog(InventarActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                    dialog.show();
                }
                break;

            case R.id.btnShoot: //обработчик кнопки СНЯТЬ
                if(Integer.parseInt(freeInventar) < Integer.parseInt(sizeInventar)) {//если есть свободное место в рюкзаке
                    info.setText("");
                    data_checking.setText("Герой раздевается...");
                    Disable(); //включаю вид загрузки
                    clearArrayList(); //очищаю все динамичесике массивы
                    conf.setSOCKET_MESSAGE("SHOOT");
                }else{
                    dialog = DialogScreen.getDialog(InventarActivity.this, DialogScreen.DIALOG_OK, conf, FullInventar());
                    dialog.show();
                }
                break;

            case R.id.btnThrow:  //обработчик кнопки Выбросить
                if (Integer.parseInt(qvObjInventar.get(ID)) == 1){ //если вещь квестовая
                    dialogMessage = "Квестовый предмет невозможо выбросить.";
                    dialog = DialogScreen.getDialog(InventarActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                    dialog.show();
                }else{
                    builder.setMessage("Вы действительно хотите выбросить эту вещь?"); // сообщение
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка Да
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            info.setText("");
                            data_checking.setText("Выбрасываю ненужную вещь...");
                            Disable(); //включаю вид загрузки
                            clearArrayList(); //очищаю все динамичесике массивы
                            conf.setSOCKET_MESSAGE("THROW");
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // Отпускает диалоговое окно
                        }
                    }).create().show();
                }
                break;
        }
    }

    //показываю все элементы активити
    static void EnableAll() {
        imgAmulet.setVisibility(View.VISIBLE);
        imgGloves.setVisibility(View.VISIBLE);
        imgRightRing.setVisibility(View.VISIBLE);
        imgLeftRing.setVisibility(View.VISIBLE);
        imgHelmet.setVisibility(View.VISIBLE);
        imgArmor.setVisibility(View.VISIBLE);
        imgBoots.setVisibility(View.VISIBLE);
        imgRightHand.setVisibility(View.VISIBLE);
        imgLeftHand.setVisibility(View.VISIBLE);
        invetarLayour.setVisibility(View.VISIBLE);
        gallery.setVisibility(View.VISIBLE);
        info.setVisibility(View.VISIBLE);
        txtSizeInventar.setVisibility(View.VISIBLE);
    }

    //прячу НАДЕТЬ и ВЫБРОСИТЬ и показываю СНЯТЬ
    static void DisEn1() {
        btnThrow.setVisibility(View.GONE);
        btnWear.setVisibility(View.GONE);
        btnShoot.setVisibility(View.GONE);
        if(Integer.parseInt(shoot) != 0) btnShoot.setVisibility(View.VISIBLE); //если в ячейке нет предмета
    }

    //прячу СНЯТЬ и показываю НАДЕТЬ и ВЫБРОСИТЬ
    void DisEn2(int id) {
        btnThrow.setVisibility(View.GONE);
        btnShoot.setVisibility(View.GONE);
        btnWear.setVisibility(View.GONE);
        if (flagWear == 0){ //если в рюкзаке не пусто
            btnWear.setVisibility(View.VISIBLE);
            btnThrow.setVisibility(View.VISIBLE);
        }
    }

    //прячу все элементы
    static void DisableAll() {
        imgAmulet.setVisibility(View.GONE);
        imgGloves.setVisibility(View.GONE);
        imgRightRing.setVisibility(View.GONE);
        imgLeftRing.setVisibility(View.GONE);
        imgHelmet.setVisibility(View.GONE);
        imgArmor.setVisibility(View.GONE);
        imgBoots.setVisibility(View.GONE);
        imgRightHand.setVisibility(View.GONE);
        imgLeftHand.setVisibility(View.GONE);
        invetarLayour.setVisibility(View.GONE);
        gallery.setVisibility(View.GONE);
        info.setVisibility(View.GONE);
        btnShoot.setVisibility(View.GONE);
        btnWear.setVisibility(View.GONE);
        btnThrow.setVisibility(View.GONE);
        txtSizeInventar.setVisibility(View.GONE);
    }

    //прячу все элементы загрузки
    static void DisableLoad() {
        pbConnect.setVisibility(View.GONE);
        data_checking.setVisibility(View.GONE);
    }

    //очищаю все динамические массивы
    static void clearArrayList() {
        idObjInventar = new ArrayList<String>(); //массыв для хранение ID предмета для создания галереи
        infoObjHero = new ArrayList<String>(); //массыв для хранение информации о предмете, который одет на герое
        lvlObjHero = new ArrayList<String>();
        picObjHero = new ArrayList<String>();
        twoObjHero = new ArrayList<String>();  //массыв для хранение информации о двуручности предмета, который одет на герое
        infoObjInventar = new ArrayList<String>();  //массыв для хранение информации о предмете в рюкзаке
        wearObjInventar = new ArrayList<String>();  //массыв для хранение информации о возвожности одеть предмет в рюкзаке
        lvlHeroInventar = new ArrayList<String>();  //массыв для хранение информации о необходимом уровне для героя, что бы одеть предмет в рюкзаке
        lvlObjInventar = new ArrayList<String>();
        picObjInventar = new ArrayList<String>();
        qObjInventar = new ArrayList<String>();  //массыв для хранение информации о необходимости спрашивать, на какую руку одеть вещь
        qvObjInventar = new ArrayList<String>();
        imageAdapter = null;
    }

    //показываю все элементы загрузки
    static void EnableLoad() {
        pbConnect.setVisibility(View.VISIBLE);
        data_checking.setVisibility(View.VISIBLE);
    }

    //когда все данные загрузились
    static void Enable() {
        EnableAll();  //показываю все обьекты
        DisableLoad(); //прячу обьекты закрузки
    }

    //когда начинается загрузка загрузились
    static void Disable() {
        DisableAll();  //прячу все обьекты
        EnableLoad(); //показываю обьекты закрузки
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




