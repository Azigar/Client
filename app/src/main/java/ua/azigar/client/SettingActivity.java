package ua.azigar.client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import ua.azigar.client.Client.ChangeAvatar;
import ua.azigar.client.Client.ChangeDef;
import ua.azigar.client.Client.ChangePass;
import ua.azigar.client.Client.NewGame;
import ua.azigar.client.Client.NewGoogle;
import ua.azigar.client.Client.NewNameHero;
import ua.azigar.client.Resources.DialogScreen;
import ua.azigar.client.Resources.SocketConfig;
import ua.azigar.client.Resources.MyHero;

/**
 * Created by Azigar on 26.07.2015.
 */
public class SettingActivity extends ActionBarActivity {

    Handler h;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;
    SocketConfig conf = new SocketConfig(); //подключаю новый екземпляр conf для клиента регистрации
    Intent intent;
    SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:
    static final String PREF_LOCATION = "LOCATION";
    double gold;
    static Button btnChangeAvatar, btnNewGame, btnChangePass, btnRegPass, btnChangeDef, btnChangeNameHero, btnChangeGoogle;
    final String[] changeGef = {"Вход без пароля", "Вход с паролем"};
    String dialogMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //убираю ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //убираю заголовок приложения
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //подключаем лауер-файл с элементами
        setContentView(R.layout.activity_setting);
        //подключаю глобальные переменные для хранения данных о герое
        final MyHero app = ((MyHero) getApplicationContext());
        //пишу в конф. последнее активити локации
        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        conf.setLAST_LOCAL(sPref.getString(PREF_LOCATION, "ua.azigar.client.intent.action.LocalActivity1")); //пишу в конф. последнее активити локации
        intent = new Intent(conf.getLAST_LOCAL()); //подключаю к новуму Intent последнее активити локации
        //пишу данные переменные сокета для отправки
        conf.setID(app.getID()); //cохраняю ID игрока
        conf.setSEX(app.getSEX());  //cохраняю пол героя
        gold = Double.valueOf(app.getGOLD());  //сохраняю голд

        builder = new AlertDialog.Builder(this);

        //инициализирую обьекты
        btnNewGame = (Button) findViewById(R.id.btnNewGame);
        btnChangeAvatar = (Button) findViewById(R.id.btnChangeAvatar);
        btnChangePass = (Button) findViewById(R.id.btnChangePass);
        btnRegPass = (Button) findViewById(R.id.btnRegPass);
        btnChangeDef = (Button) findViewById(R.id.btnChangeDef);
        btnChangeNameHero = (Button) findViewById(R.id.btnChangeNameHero);
        btnChangeGoogle = (Button) findViewById(R.id.btnChangeGoogle);

        //подключаю Handler
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                String dialogMessage;
                switch (msg.what) {
                    case 1:  //подключение к серверу успешно
                        conf.setSOCKET_MESSAGE("ENTER");
                        break;

                    case 2:  //Не удалось подключится к серверу
                        Toast.makeText(SettingActivity.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        finish(); //закрываю активити
                        break;

                    case 3:  //Не получилось удалить героя
                        progressDialog.dismiss();
                        dialogMessage = "Сбой системы. Не удалось выполнить Ваш запрос";
                        Toast.makeText(SettingActivity.this, dialogMessage, Toast.LENGTH_LONG).show();
                        conf.setSOCKET_MESSAGE("END");
                        //finish(); //закрываю активити
                        break;

                    case 4:  //Герой успешно удалён
                        progressDialog.dismiss();
                        dialogMessage = "Герой " + app.getNAME() + " успешно удалён. Будет произведен выход с игры";
                        dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_DELETE_HERO, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 5:  //сервер спрашивает пароль
                        dialogMessage = "Введите пароль";
                        dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_ENTER_PASS, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 6:  //сервер спрашивает пароль
                        dialogMessage = "Введите новый пароль";
                        dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_ENTER_PASS, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 7:  //Ваш запрос успешно выполнен
                        progressDialog.dismiss();
                        dialogMessage = "Ваш запрос успешно выполнен";
                        Toast.makeText(SettingActivity.this, dialogMessage, Toast.LENGTH_LONG).show();
                        conf.setSOCKET_MESSAGE("END");
                        break;

                    case 8:  //выход с диалогового окна
                        progressDialog.dismiss();
                        break;

                    case 9:  //не верный пароль
                        dialogMessage = "Не верный пароль";
                        dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_ERROR_ENTER_PASS2, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 10:  //сервер ждет новое имя
                        dialogMessage = "Введите новое имя";
                        dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_NEW_NAME_HERO, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 11:  //новое имя занято
                        dialogMessage = "Герой с таким именем давно живет в Мидгарде. Пожалуйста, введите новое имя.";
                        dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_BEGIN_NAME_HERO, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 12:  //сервер прислал имя героя
                        app.setNAME((String) msg.obj);
                        conf.setSOCKET_MESSAGE("GOLD"); //запрашиваю к-во голда у героя
                        progressDialog.dismiss();
                        dialogMessage = "Ваш запрос успешно выполнен";
                        Toast.makeText(SettingActivity.this, dialogMessage, Toast.LENGTH_LONG).show();
                        conf.setSOCKET_MESSAGE("END");
                        break;

                    case 13:  //сервер спрашивает имя нового аккаунта
                        dialogMessage = "Очень внимательно вводите имя нового Google-аккаунта, что бы не потерять Вашего героя. Формат Google-аккаунта: имя_аккаунта@gmail.com.";
                        dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_NEW_GOOGLE, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 14:  //сервер говорит, что указаный акк занят
                        dialogMessage = "К этому Google-аккаунту уже привьязан герой";
                        dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_BEGIN_NAME_HERO, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 15:  //сервер говорит, что указаный акк занят
                        dialogMessage = "Ваш запрос успешно выполнен. Заходите в игру с другого аккаунта";
                        dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_RESTART_GAME, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 16:  //сервер говорит, что указаный акк занят
                        app.setGOLD((String) msg.obj);
                        progressDialog.dismiss();
                        dialogMessage = "Ваш запрос успешно выполнен";
                        Toast.makeText(SettingActivity.this, dialogMessage, Toast.LENGTH_LONG).show();
                        conf.setSOCKET_MESSAGE("END");
                        break;
                }
            }
        };
    }

    //обработчи кнопки Изменить уровень защиты
    public void onClickChangeDef(View v) {
        final String[] cmd = new String[1];
        dialogMessage = "Уровень защиты";
        builder.setTitle(dialogMessage); // сообщение
        builder.setCancelable(false); //Запрещаем закрывать окошко кнопкой "back"
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() { // Кнопка НЕТ
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Отпускает диалоговое окно
            }
        });
        //добавлю переключатели
        builder.setSingleChoiceItems(changeGef, -1, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //сравниваю по тексту в ячейках массива
                cmd[0] = changeGef[item]; //запоминаю выбор
            }
        });
        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка НЕТ
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = new ProgressDialog(SettingActivity.this);
                progressDialog.setMessage("Ожидайте, применяю изменения...");
                progressDialog.show();
                if (cmd[0].equalsIgnoreCase("Вход без пароля")){
                    new ChangeDef(h, conf, "ENTER_NO_PASS");
                }else{
                    new ChangeDef(h, conf, "ENTER_YES_PASS");
                }
            }
        });
        builder.create().show();
    }

    //обработчи кнопки Изменить GOOGLE
    public void onClickChangeGoogle(View v) {
        if (gold >= 50){ //если хватает денег для изменение
            dialogMessage = "Цена услуги - 50 G. Вы согласны?";
            builder.setMessage(dialogMessage); // сообщение
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка Да
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog = new ProgressDialog(SettingActivity.this);
                    progressDialog.setMessage("Ожидайте...");
                    progressDialog.show();
                    new NewGoogle(h, conf);
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // Отпускает диалоговое окно
                }
            }).create().show();
        } else {
            dialogMessage = "Необходимо иметь не меньше 50 G";
            dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            dialog.show();
        }
    }

    //обработчи кнопки Изменить имя героя
    public void onClickChangeNameHero(View v) {
        if (gold >= 100){ //если хватает денег для изменение
            dialogMessage = "Цена услуги - 100 G. Вы согласны?";
            builder.setMessage(dialogMessage); // сообщение
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка Да
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog = new ProgressDialog(SettingActivity.this);
                    progressDialog.setMessage("Ожидайте...");
                    progressDialog.show();
                    new NewNameHero(h, conf);
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // Отпускает диалоговое окно
                }
            }).create().show();
        } else {
            dialogMessage = "Необходимо иметь не меньше 100 G";
            dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            dialog.show();
        }
    }

    //обработчи кнопки Изменить пароль
    public void onClickChangePass(View v) {
        progressDialog = new ProgressDialog(SettingActivity.this);
        progressDialog.setMessage("Ожидайте...");
        progressDialog.show();
        new ChangePass(h, conf);
    }

    //обработчи кнопки Востановить пароль
    public void onClickRegPass(View v) {
        dialogMessage = "Если Вы забыли пароль, тогда измените уровень защиты на \"Вход с паролем\" и перезапустите игру. " +
                "Когда будет запрос на ввод пароля, введите что либо и в следующем окне нажмите \"Востановить\"." +
                "Для востановления пароля необходимо знать имя героя, Вашу дату рождения и дату регистрации.";
        dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
        dialog.show();
    }

    //обработчи кнопки Изменить аватар
    public void onClickChangeAvatar(View v) {
        Intent intent = new Intent(this, AvatarActivity.class);
        intent.putExtra("sex", conf.getSEX());
        intent.putExtra("status", "CHANGE");
        startActivity(intent);
    }

    //обработчи кнопки Начать сначала
    public void onClickNewGame(View v) {
        if (gold >= 10){ //если хватает денег для изменение
            dialogMessage = "Ваш герой будет безвозвратно удалён. Вы действительно хотите начать игру заново?";
            builder.setMessage(dialogMessage); // сообщение
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка Да
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog = new ProgressDialog(SettingActivity.this);
                    progressDialog.setMessage("Ожидайте, применяю изменения...");
                    progressDialog.show();
                    new NewGame(h, conf);
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // Отпускает диалоговое окно
                }
            }).create().show();
        } else {
            dialogMessage = "Необходимо иметь не меньше 10 G";
            dialog = DialogScreen.getDialog(SettingActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            dialog.show();
        }
    }

    //обработка кнопки назад
    @Override
    public void onBackPressed () { startActivity(intent); }
}
