package ua.azigar.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ua.azigar.client.Client.Authorization;
import ua.azigar.client.Client.SocketConfig;
import ua.azigar.client.Resources.DialogScreen;
import ua.azigar.client.Resources.MyHero;

/**
 * Created by Azigar on 06.07.2015.
 */
public class AuthorizationActivity extends ActionBarActivity {

    Handler h;
    Authorization authorization;
    SocketConfig conf = new SocketConfig(); //подключаю новый екземпляр conf для клиента регистрации
    AlertDialog dialog;
    SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:

    static final String PREF_LOCATION = "LOCATION";
    final int GALLERY_REQUEST = 1;

    static TextView textView, textView3, data_checking;
    static EditText add_name_hero;
    static Button btn_add_avatar, btn_create_hero;
    static ProgressBar pbConnectEnter;
    static ImageView avatar;

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
        setContentView(R.layout.activity_authorization);

        final MyHero app = ((MyHero)getApplicationContext()); //подключаю глобальные переменные для хранения данных о герое

        //читаю intent-данные от предыдущего активити)
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");  //сохраняю имя аккаунта в переменную
        id = id.substring(id.indexOf(""), id.indexOf("@")); //вытягиваю логин с Google-аккаунта
        app.setID(id);  //сохраняю ID в глобальную переменную
        conf.setID(id); //сохраняю ID в сокет-переменную

        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        final SharedPreferences.Editor ed = sPref.edit();  //подключаю Editor для сохранение новых параметров в SharedPreferences

        ////найдем View-элементы
        textView = (TextView) findViewById(R.id.textView);
        textView3 = (TextView) findViewById(R.id.textView3);
        add_name_hero =  (EditText) findViewById(R.id.add_name_hero);
        data_checking = (TextView) findViewById(R.id.data_checking);
        btn_add_avatar = (Button) findViewById(R.id.btn_add_avatar);
        btn_create_hero = (Button) findViewById(R.id.btn_create_hero);
        pbConnectEnter = (ProgressBar) findViewById(R.id.pbConnectEnter);
        avatar = (ImageView) findViewById(R.id.avatar);

        DisableAll(); //прячу все элементы для регистрации

        //подключаю Handler
        h = new Handler() {

            Thread t;
            String cmd;
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:  //подключение к серверу успешно
                        conf.setSOCKET_MESSAGE("ENTER");
                        break;

                    case 2:  //Не удалось подключится к серверу
                        Toast.makeText(AuthorizationActivity.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        finish(); //закрываю активити
                        break;

                    case 3:  //сервер говорит, что есть такой акк
                        conf.setSOCKET_MESSAGE("GET");
                        break;

                    case 4:  //сервер говорит мак. к-во опыта игрока
                        app.setMAX_EXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("EXP"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 5:  //сервер говорит текущее к-во опыта игрока
                        app.setEXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MAX_PVP_EXP"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 6:  //сервер говорит мак. к-во pvp-опыта героя
                        app.setMAX_PVP_EXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("PVP_EXP"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 7:  //сервер говорит текущее к-во pvp-опыта героя
                        app.setPVP_EXP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MAX_HP"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 8:  //сервер говорит мак. к-во ОЗ героя
                        app.setMAX_HP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("HP"); //запрашиваю текущее значенеи опыта героя
                        break;

                    case 9:  //сервер говорит текущее к-во ОЗ героя
                        app.setHP((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MAX_MANA"); //запрашиваю мак. к-во маны героя
                        break;

                    case 10:  //сервер говорит к-во маны героя
                        app.setMANA((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MONEY"); //запрашиваю к-во монет героя
                        break;

                    case 11:  //сервер говорит, что нет такого акк
                        app.setFIRST_START("1");
                        ed.putString(PREF_LOCATION, "ua.azigar.client.intent.action.LocalActivity1"); //сохраняю флаг о загрузке первой локации.
                        ed.commit();
                        EnableAll(); //показываю активити регистрации
                        DisableDataChecking(); //и прячу все элементы для загрузки
                        break;

                    case 12:  //сервер говорит, что такое имя существует
                        EnableAll(); //показываю активити регистрации
                        DisableDataChecking(); //и прячу все элементы для загрузки
                        add_name_hero.setText(""); //очищаю окошко ввода имени
                        dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_NAME_EXISTS, conf, "");
                        dialog.show();
                        break;

                    case 13:  //сервер говорит, что регестрация прошла успешно
                        dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_REGISTERED, conf, "");
                        dialog.show();
                        break;

                    case 14:  //сервер говорит к-во монет игрока
                        app.setMONEY((String) msg.obj);
                        conf.setSOCKET_MESSAGE("GOLD"); //запрашиваю к-во голда у героя
                        break;

                    case 15:  //сервер прислал имя героя
                        app.setNAME((String) msg.obj);
                        conf.setSOCKET_MESSAGE("LVL"); //запрашиваю уровень героя
                        break;

                    case 16:  //сервер прислал уровень героя
                        app.setLVL((String) msg.obj);
                        conf.setSOCKET_MESSAGE("TITLE"); //запрашиваю звание героя
                        break;

                    case 17:  //сервер прислал звание героя
                        app.setTITLE((String) msg.obj);
                        conf.setSOCKET_MESSAGE("MAX_EXP"); //запрашиваю мак. значение опыта героя
                        break;

                    case 18:  //сервер прислал к-во голда героя
                        app.setGOLD((String) msg.obj);
                        conf.setSOCKET_MESSAGE("VIP"); //запрашиваю вип-уровень игрока
                        break;

                    case 19:  //сервер прислал вип-уровень игрока
                        app.setVIP((String) msg.obj);
                        //закрываю сокет-поток
                        //считываю с Preferences какая была последння локация и запускаю акктивити локации
                        String last_local = sPref.getString(PREF_LOCATION, "ua.azigar.client.intent.action.LocalActivity1");
                        Intent intent = new Intent(last_local);
                        startActivity(intent);
                        break;

                    case 20:  //сервер мак. к-во мани героя
                        app.setMAX_MANA((String) msg.obj);
                        if (Integer.parseInt(app.getMAX_MANA()) == 0) {
                            app.setMAX_MANA("1");
                        }
                        conf.setSOCKET_MESSAGE("MANA"); //запрашиваю к-во маны героя
                        break;

                    case 21:  //сервер прислал вип-уровень игрока
                        Toast.makeText(AuthorizationActivity.this, R.string.No_connected, Toast.LENGTH_LONG).show();
                        finish(); //закрываю активити
                        break;
                }
            }
        };
        //запуск клиента регистации
        authorization = new Authorization(h, conf);
    }

    public void onClickCreateHero(View v) {  //обработчи кнопки Создать Героя
        conf.setSOCKET_MESSAGE(add_name_hero.getText().toString()); //отправляю имя нового героя
        DisableAll(); //прячу все элементы для регистрации
        EnableDataChecking(); //показываю все элементы для загрузки
    }

    public void onClickAddAvatar(View v) {  //обработчи кнопки Загрузить Аватар
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    //загрузка аватара
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap galleryPic = null;
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    avatar.setImageURI(selectedImage);
                }
        }
    }

    //прячу все элементы для регистрации
    static void DisableAll() {
        textView.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        btn_add_avatar.setVisibility(View.GONE);
        btn_create_hero.setVisibility(View.GONE);
        add_name_hero.setVisibility(View.GONE);
        avatar.setVisibility(View.GONE);
    }

    //показываю все элементы для регистрации
    static void EnableAll() {
        textView.setVisibility(View.VISIBLE);
        textView3.setVisibility(View.VISIBLE);
        btn_add_avatar.setVisibility(View.VISIBLE);
        btn_create_hero.setVisibility(View.VISIBLE);
        add_name_hero.setVisibility(View.VISIBLE);
        avatar.setVisibility(View.VISIBLE);
    }

    //прячу все элементы для загрузки
    static void DisableDataChecking() {
        pbConnectEnter.setVisibility(View.GONE);
        data_checking.setVisibility(View.GONE);
    }

    //показываю все элементы для загрузки
    static void EnableDataChecking() {
        data_checking.setVisibility(View.VISIBLE);
        pbConnectEnter.setVisibility(View.VISIBLE);
    }

    //обработка кнопки назад
    @Override
    public void onBackPressed ()
    {
        dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_EXIT_GAME, conf, "");
        dialog.show();
    }

    //обработка закрытия (уничтожения) активити
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //закрываю сокет-поток
        if (authorization != null){
            authorization.interrupt();
            authorization = null;
        }
    }
}
