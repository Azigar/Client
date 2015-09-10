package ua.azigar.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.Layout;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import ua.azigar.client.Client.Shop;
import ua.azigar.client.Resources.DialogScreen;
import ua.azigar.client.Resources.ImageAdapter;
import ua.azigar.client.Resources.MyHero;
import ua.azigar.client.Resources.SocketConfig;

/**
 * Created by Azigar on 16.08.2015.
 */
public class ShopActivity extends ActionBarActivity {

    SocketConfig conf = new SocketConfig();
    static ProgressBar pbConnect;
    static TextView data_checking, txtMoney, txtGold, txtZag, txtPrise, txtInfo;
    static Gallery gallery;
    static Button btnLeft, btnRight;
    static ImageAdapter imageAdapter;
    static FrameLayout objLayout;
    EditText input;

    static ArrayList<String> idObj; //массыв для хранение ID предмета для создания галереи
    static ArrayList<String> infoObj;  //массыв для хранение информации о предмете
    static ArrayList<String> pvpLvlObj;  //массыв для хранение информации о необходимом ПвП-уровне героя
    static ArrayList<String> titleObj;  //массыв для хранение информации о звании предмета
    static ArrayList<String> vipLvlObj;  //массыв для хранение информации о необходимом ВИП-уровне игрока
    static ArrayList<String> lvlObj;  //массыв для хранение информации о уровене предмета
    static ArrayList<String> picObj;  //массыв для хранение информации  о картинтке предмета
    static ArrayList<String> moneyObj;  //массыв для хранение информации  о цене в монетах предмета
    static ArrayList<String> txtMoneyObj;  //массыв для хранение информации  о цене в монетах предмета
    static ArrayList<String> goldObj;  //массыв для хранение информации  о цене в голде предмета
    static ArrayList<String> txtGoldObj;  //массыв для хранение информации  о цене в голде предмета
    static ArrayList<String> qvObj;  //массыв для хранение информации о том квестовая вещь или нет
    static ArrayList<String> countObj;  //массыв для хранение информации о к-ве предметов

    static Intent intent;
    static SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:
    static final String PREF_LOCATION = "LOCATION";
    static Handler h;
    AlertDialog.Builder builder;
    static AlertDialog dialog;
    String idShop, nameShop, PVP_LVL, VIP_LVL, dialogMessage, count;
    int flag = 1, ID, freeInventar, sizeInvetar, MONEY;
    double GOLD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //убираю ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //убираю заголовок приложения
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //подключаем лауер-файл с элементами
        setContentView(R.layout.activity_shop  );
        //читаю intent-данные от предыдущего активити и сохраняю текущие значение здоровья и манны
        intent = getIntent();
        this.idShop = intent.getStringExtra("idShop");
        this.nameShop = intent.getStringExtra("nameShop");
        conf.setSHOP(idShop); //пишу магазин для отправки
        //подключаю глобальные переменные для хранения данных о герое
        final MyHero app = ((MyHero) getApplicationContext());
        //пишу в конф. последнее активити локации
        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        conf.setLAST_LOCAL(sPref.getString(PREF_LOCATION, "ua.azigar.client.intent.action.LocalActivity1")); //пишу в конф. последнее активити локации
        intent = new Intent(conf.getLAST_LOCAL()); //подключаю к новуму Intent последнее активити локации
        //пишу данные переменные сокета для отправки
        conf.setID(app.getID()); //cохраняю ID игрока
        PVP_LVL = app.getPVP_LVL(); //cохраняю пвп-уровень героя
        VIP_LVL = app.getVIP(); //cохраняю вип-уровень игрока

        builder = new AlertDialog.Builder(this);

        //инициализирую массивы
        idObj = new ArrayList<String>();
        infoObj = new ArrayList<String>();
        pvpLvlObj = new ArrayList<String>();
        titleObj = new ArrayList<String>();
        vipLvlObj = new ArrayList<String>();
        lvlObj = new ArrayList<String>();
        picObj = new ArrayList<String>();
        moneyObj = new ArrayList<String>();
        txtMoneyObj = new ArrayList<String>();
        goldObj = new ArrayList<String>();
        txtGoldObj = new ArrayList<String>();
        qvObj = new ArrayList<String>();
        countObj = new ArrayList<String>();

        //найдем View-элементы
        objLayout = (FrameLayout) findViewById(R.id.objLayout);
        pbConnect = (ProgressBar) findViewById(R.id.pbConnect);
        data_checking = (TextView) findViewById(R.id.data_checking);
        gallery = (Gallery) findViewById(R.id.gallery);
        txtMoney = (TextView) findViewById(R.id.txtMoney);
        txtGold = (TextView) findViewById(R.id.txtGold);
        txtZag = (TextView) findViewById(R.id.txtZag);
        txtPrise = (TextView) findViewById(R.id.txtPrise);
        txtInfo = (TextView) findViewById(R.id.txtInfo);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);

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
                        Toast.makeText(ShopActivity.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        finish(); //закрываю активити
                        break;

                    case 3:  //системная ошибка
                        startActivity(intent);
                        break;

                    case 4:  //получил ID записи в магазине
                        m = (String) msg.obj;
                        idObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_INFO_OBJ_SHOP");
                        break;

                    case 5:  //получил инфу о предмете в магазине
                        m = (String) msg.obj;
                        infoObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_PVP_OBJ_SHOP");
                        break;

                    case 6:  //получил необходимый пвп-лвл
                        m = (String) msg.obj;
                        pvpLvlObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_VIP_OBJ_SHOP");
                        break;

                    case 7:  //получил необходимый вип-лвл
                        m = (String) msg.obj;
                        vipLvlObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_LVL_OBJ_SHOP");
                        break;

                    case 8:  //получил уровень предмета в магазине
                        m = (String) msg.obj;
                        lvlObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_TITLE_OBJ_SHOP");
                        break;

                    case 9:  //получил картинку предмета в магазине
                        m = (String) msg.obj;
                        picObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_MONEY_OBJ_SHOP");
                        break;

                    case 10:  //получил цена в монетах предмета в магазине
                        m = (String) msg.obj;
                        moneyObj.add(m);
                        if(m.equalsIgnoreCase("0")) txtMoneyObj.add("");
                        else txtMoneyObj.add(m + " монет");
                        conf.setSOCKET_MESSAGE("GET_GOLD_OBJ_SHOP");
                        break;

                    case 11:  //получил цена в голде предмета в магазине
                        m = (String) msg.obj;
                        goldObj.add(m);
                        if(m.equalsIgnoreCase("0")) txtGoldObj.add("");
                        else txtGoldObj.add(m + " G");
                        conf.setSOCKET_MESSAGE("LAST_OBJ_SHOP");
                        break;

                    case 12:  //получил звание необходимое для покупки
                        m = (String) msg.obj;
                        titleObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_PIC_OBJ_SHOP");
                        break;

                    case 13:  //сервер прислал ответ на последнюю вещь в списке МАГАЗИН
                        m = (String) msg.obj;
                        if (m.equalsIgnoreCase("NO_LAST_OBJ_SHOP")){ //если надо еще узнавать инфу о предметах
                            conf.setSOCKET_MESSAGE("NEXT_OBJ_SHOP");
                        }else{
                            flag = 1;
                            MONEY = Integer.parseInt(app.getMONEY());
                            GOLD = Double.parseDouble(app.getGOLD());
                            btnLeft.setText("КУПИТЬ");
                            btnRight.setText("РЮКЗАК");
                            txtZag.setText(nameShop);
                            txtGold.setText(app.getGOLD() + " G");
                            txtMoney.setText(app.getMONEY() + " монет");
                            //загружаю галерею         // Устанавливаем адаптер
                            imageAdapter = new ImageAdapter(ShopActivity.this, picObj); //создаю адаптер для галереи
                            gallery.setAdapter(imageAdapter); //подключаю адаптер к галереи
                            gallery.setSelection(0); // выделяю первый элемент галереи
                            // Устанавливаем действия, которые будут выполнены при выделении элемента
                            gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                                    objLayout.setBackgroundResource(getResources().getIdentifier("inventg" + lvlObj.get((int) id), "drawable", getPackageName()));
                                    conf.setID_OBJ(idObj.get((int) id)); //пишу ID записи в магазине в переменную для КУПИТЬ
                                    txtInfo.setText(infoObj.get((int) id)); //пишу информацию о предмете
                                    txtPrise.setText("Цена: " + txtMoneyObj.get((int) id) + "  " + txtGoldObj.get((int) id));
                                    ID = (int) id;
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                }
                            });
                            Enable();
                        }
                        break;

                    case 14:  //получил к-во предметов в рюкзаке
                        m = (String) msg.obj;
                        freeInventar = Integer.parseInt(m);
                        conf.setSOCKET_MESSAGE("SIZE_INVENTAR");
                        break;

                    case 15:  //получил мак. к-во предметов в рюкзаке
                        m = (String) msg.obj;
                        sizeInvetar = Integer.parseInt(m);
                        switch (flag){
                            case 1: conf.setSOCKET_MESSAGE("GET_SHOP"); break;
                            case 2: conf.setSOCKET_MESSAGE("INVENTAR"); break;
                        }
                        break;

                    case 16:  //получил новое к-во монет
                        m = (String) msg.obj;
                        app.setMONEY(m);
                        conf.setSOCKET_MESSAGE("GOLD");
                        break;

                    case 17:  //получил новое к-во голда
                        m = (String) msg.obj;
                        app.setGOLD(m);
                        switch (flag){
                            case 1: Toast.makeText(ShopActivity.this, "Вещь уже в рюкзаке", Toast.LENGTH_LONG).show(); break;
                            case 2: Toast.makeText(ShopActivity.this, "Успешно продали вещь", Toast.LENGTH_LONG).show(); break;
                        }
                        conf.setSOCKET_MESSAGE("SHOP");
                        break;

                    case 18:  //нет предметов в инвентаре
                        flag = 2;
                        MONEY = Integer.parseInt(app.getMONEY());
                        GOLD = Double.parseDouble(app.getGOLD());
                        btnLeft.setText("ПРОДАТЬ");
                        btnRight.setText("МАГАЗИН");
                        txtZag.setText("РЮКЗАК");
                        txtGold.setText(app.getGOLD() + " G");
                        txtMoney.setText(app.getMONEY() + " монет");
                        txtPrise.setText(freeInventar + "/" + sizeInvetar);
                        picObj.add("0");
                        //загружаю галерею
                        imageAdapter  = new ImageAdapter(ShopActivity.this, picObj); //создаю адаптер для галереи
                        gallery.setAdapter(imageAdapter); //подключаю адаптер к галереи
                        gallery.setBackgroundResource(R.drawable.inventg1);
                        // Устанавливаем действия, которые будут выполнены при выделении элемента
                        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                                btnLeft.setEnabled(false); //неактивная кнопка ПРОДАТЬ
                                txtInfo.setText("РЮКЗАК ПУСТ"); //пишу информацию о предмете
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                            }
                        });
                        Enable();
                        break;

                    case 19:  //сервер прислал ID предмета с рюкзака
                        m = (String) msg.obj;
                        idObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_QVEST_OBJ_INVENTAR");
                        break;

                    case 20:  //сервер пристал квестовая вещь или нет
                        m = (String) msg.obj;
                        qvObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_LVL_OBJ_INVENTAR");
                        break;

                    case 21:  //сервер прислал уровень предмета в рюкзаке
                        m = (String) msg.obj;
                        lvlObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_PIC_OBJ_INVENTAR");
                        break;

                    case 22:  //сервер прислал картинку предмета в рюкзаке
                        m = (String) msg.obj;
                        picObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_COUNT_OBJ_INVENTAR");
                        break;

                    case 23:  //сервер прислал инфу о предмете в рюкзаке
                        m = (String) msg.obj;
                        infoObj.add(m);
                        conf.setSOCKET_MESSAGE("LAST_INFO_OBJ_INVENTAR");
                        break;

                    case 24:  //сервер прислал ответ на последнюю вещь в списке РЮКЗАК
                        m = (String) msg.obj;
                        if (m.equalsIgnoreCase("NO_LAST_INFO_OBJ_INVENTAR")){ //если надо еще узнавать инфу о предметах
                            conf.setSOCKET_MESSAGE("NEXT_INFO_OBJ_INVENTAR");
                        }else{
                            flag = 2;
                            MONEY = Integer.parseInt(app.getMONEY());
                            GOLD = Double.parseDouble(app.getGOLD());
                            btnLeft.setText("ПРОДАТЬ");
                            btnLeft.setEnabled(true);
                            btnRight.setText("МАГАЗИН");
                            txtZag.setText("РЮКЗАК");
                            txtGold.setText(app.getGOLD() + " G");
                            txtMoney.setText(app.getMONEY() + " монет");
                            txtPrise.setText(freeInventar + "/" + sizeInvetar);
                            //загружаю галерею         // Устанавливаем адаптер
                            imageAdapter = new ImageAdapter(ShopActivity.this, picObj); //создаю адаптер для галереи
                            gallery.setAdapter(imageAdapter); //подключаю адаптер к галереи
                            //Выбираю элемет
                            if(idObj.size() > 0) gallery.setSelection(0); // выделяю первый элемент галереи
                            if(idObj.size() > 2) gallery.setSelection(1);
                            if(idObj.size() > 4) gallery.setSelection(2);
                            // Устанавливаем действия, которые будут выполнены при выделении элемента
                            gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                                    objLayout.setBackgroundResource(getResources().getIdentifier("inventg" + lvlObj.get((int) id), "drawable", getPackageName()));
                                    conf.setID_OBJ(idObj.get((int) id)); //пишу ID ячейки в переменную для ПРОДАТЬ
                                    txtInfo.setText(infoObj.get((int) id)); //пишу информацию о предмете
                                    count = countObj.get((int) id); //пишу информацию о к-ве в одной ячейке
                                    ID = (int) id;
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                }
                            });
                            Enable();
                        }
                        break;

                    case 25:  //сервер прислал к-во предметов
                        m = (String) msg.obj;
                        countObj.add(m);
                        conf.setSOCKET_MESSAGE("GET_INFO_OBJ_INVENTAR");
                        break;

                    case 26:  //сервер спрашивает к-во предметов на продажу
                        dialogMessage = "Введите количество от 1 до " + count;
                        dialog = DialogScreen.getDialog(ShopActivity.this, DialogScreen.DIALOG_ENTER_COUNT_OBJ, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 27:  //не верное к-во предметов на продажу
                        dialogMessage = "Нужно ввести количество от 1 до " + count;
                        dialog = DialogScreen.getDialog(ShopActivity.this, DialogScreen.DIALOG_ERROR_ENTER_COUNT_OBJ, conf, dialogMessage);
                        dialog.show();
                        break;
                }
            }
        };
        new Shop(h, conf);
    }

    //обработчи кнопки Купить/Продать
    public void onClickLeft(View v) {
        boolean f = true, p = true;
        switch (flag){
            case 1: //режим КУПИТЬ
                dialogMessage = "Для покупки необходимо достичь ";
                if(MONEY < Integer.parseInt(moneyObj.get(ID)) || GOLD < Double.parseDouble(goldObj.get(ID))){
                    dialogMessage = "У Вас недостаточно денежных средств";
                    f = false;
                    p = false;
                }
                if (freeInventar == sizeInvetar && f == true && p == true) { //если рюкзак забит
                    dialogMessage = FullInventar();
                    f = false;
                    p = false;
                }
                if ((Integer.parseInt(PVP_LVL) < Integer.parseInt(pvpLvlObj.get(ID))) && p == true) { //если не хватает ПвП-уровня
                    dialogMessage = dialogMessage + "звания " + titleObj.get(ID) + " ";
                    f = false;
                }
                if ((Integer.parseInt(VIP_LVL) < Integer.parseInt(vipLvlObj.get(ID))) && p == true) { //если не хватает ВИП-уровня
                    if(f == false) dialogMessage = dialogMessage + " и ";
                    dialogMessage = dialogMessage + "VIP" + vipLvlObj.get(ID);
                    f = false;
                }
                if (f == false){
                    dialog = DialogScreen.getDialog(ShopActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                    dialog.show();
                }else{
                    builder.setMessage("Вы действительно хотите приобрести эту вещь?"); // сообщение
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка Да
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            txtInfo.setText("");
                            data_checking.setText("Покупаю новую вещь...");
                            Disable(); //включаю вид загрузки
                            clearArrayList(); //очищаю все динамичесике массивы
                            conf.setSOCKET_MESSAGE("BUY");
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

            case 2: //режим ПРОДАТЬ
                if(Integer.parseInt(qvObj.get(ID)) == 1){ //если вещь квестовая
                    dialogMessage = "Эта вещь нужна для выполнения задания, её нельзя продать";
                    f = false;
                }
                if (f == false){
                    dialog = DialogScreen.getDialog(ShopActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                    dialog.show();
                }else{
                    if (Integer.parseInt(lvlObj.get(ID)) > 1){ //если это НЕОБЫЧНЯ ВЕЩЬ
                        String txtLvlObj = "";
                        switch (Integer.parseInt(lvlObj.get(ID))){
                            case 2: txtLvlObj = "РЕДКУЮ ВЕЩЬ"; break;
                            case 3: txtLvlObj = "ГЕРОИЧЕСКУЮ ВЕЩЬ"; break;
                            case 4: txtLvlObj = "ЛЕГЕНДАРНУЮ ВЕЩЬ"; break;
                            case 5: txtLvlObj = "VIP-ПРЕДМЕТ"; break;
                        }
                        builder.setMessage("Вы действительно хотите продать " + txtLvlObj + " ?"); // сообщение
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка Да
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtInfo.setText("");
                                data_checking.setText("Продаю старую вещь...");
                                Disable(); //включаю вид загрузки
                                clearArrayList(); //очищаю все динамичесике массивы
                                conf.setSOCKET_MESSAGE("SALE");
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Отпускает диалоговое окно
                            }
                        }).create().show();
                    }else {
                        txtInfo.setText("");
                        data_checking.setText("Продаю старую вещь...");
                        Disable(); //включаю вид загрузки
                        clearArrayList(); //очищаю все динамичесике массивы
                        conf.setSOCKET_MESSAGE("SALE");
                    }
                }
                break;
        }
    }

    //обработчи кнопки РЮКЗАК/МАГАЗИН
    public void onClickRight(View v) {
        switch (flag){
            case 1: //режим МАГАЗИНА
                flag = 2;
                txtInfo.setText("");
                data_checking.setText("Достаю вещи с рюкзака...");
                Disable(); //включаю вид загрузки
                clearArrayList(); //очищаю все динамичесике массивы
                conf.setSOCKET_MESSAGE("FREE_INVENTAR");
                break;

            case 2: //режим РБКЗАКА
                flag = 1;
                txtInfo.setText("");
                data_checking.setText("Осматриваюсь в магазине...");
                Disable(); //включаю вид загрузки
                clearArrayList(); //очищаю все динамичесике массивы
                conf.setSOCKET_MESSAGE("SHOP");
                break;
        }
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
                message = "Наверное, лучше что-то продать.";
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

    //показываю все элементы активити
    static void EnableAll() {
        objLayout.setVisibility(View.VISIBLE);
        txtMoney.setVisibility(View.VISIBLE);
        txtGold.setVisibility(View.VISIBLE);
        txtZag.setVisibility(View.VISIBLE);
        txtPrise.setVisibility(View.VISIBLE);
        txtInfo.setVisibility(View.VISIBLE);
        btnLeft.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.VISIBLE);
        gallery.setVisibility(View.VISIBLE);
    }

    //прячу все элементы
    static void DisableAll() {
        objLayout.setVisibility(View.GONE);
        txtMoney.setVisibility(View.GONE);
        txtGold.setVisibility(View.GONE);
        txtZag.setVisibility(View.GONE);
        txtPrise.setVisibility(View.GONE);
        txtInfo.setVisibility(View.GONE);
        btnLeft.setVisibility(View.GONE);
        btnRight.setVisibility(View.GONE);
        gallery.setVisibility(View.GONE);
    }

    //прячу все элементы загрузки
    static void DisableLoad() {
        pbConnect.setVisibility(View.GONE);
        data_checking.setVisibility(View.GONE);
    }

    //очищаю все динамические массивы
    static void clearArrayList() {
        idObj = new ArrayList<String>();
        infoObj = new ArrayList<String>();
        pvpLvlObj = new ArrayList<String>();
        titleObj = new ArrayList<String>();
        vipLvlObj = new ArrayList<String>();
        lvlObj = new ArrayList<String>();
        picObj = new ArrayList<String>();
        moneyObj = new ArrayList<String>();
        txtMoneyObj = new ArrayList<String>();
        goldObj = new ArrayList<String>();
        txtGoldObj = new ArrayList<String>();
        qvObj = new ArrayList<String>();
        countObj = new ArrayList<String>();
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
