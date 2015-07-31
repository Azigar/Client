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
import ua.azigar.client.Client.NewGame;
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
    static Button btnChangeAvatar, btnNewGame;

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
        //инициализирую обьекты
        btnNewGame = (Button) findViewById(R.id.btnNewGame);
        btnChangeAvatar = (Button) findViewById(R.id.btnChangeAvatar);

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
                        dialogMessage = "Сбой системы. Не удалось удалить героя";
                        Toast.makeText(SettingActivity.this, dialogMessage, Toast.LENGTH_LONG).show();
                        conf.setSOCKET_MESSAGE("END");
                        finish(); //закрываю активити
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
                }
            }
        };
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
            builder = new AlertDialog.Builder(this);
            dialogMessage = "Ваш герой будет безвозвратно удалён. Вы действительно хитите начать игру заново?";
            builder.setMessage(dialogMessage); // сообщение
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка Да
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog = new ProgressDialog(SettingActivity.this);
                    progressDialog.setMessage("Ожидайте, применяю изменения");
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
