package ua.azigar.client.Resources;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;

import ua.azigar.client.Client.ChangeAvatar;
import ua.azigar.client.MainActivity;
import ua.azigar.client.R;

/**
 * Created by Azigar on 02.07.2015.
 */
public class DialogScreen {

    // Идентификаторы диалоговых окон
    public static final int DIALOG_OK = 1;
    public static final int DIALOG_PLAYER_SICK = 2;
    public static final int DIALOG_PLAYER_LOSE = 3;
    public static final int DIALOG_PLAYER_VICTORY = 4;
    public static final int DIALOG_PLAYER_LVL_UP = 5;
    public static final int DIALOG_PLAYER_PVP_UP = 6;
    public static final int DIALOG_NO_ACCOUNTS = 7;
    public static final int DIALOG_BOUNTY = 8;
    public static final int DIALOG_REGISTERED = 9;
    public static final int DIALOG_EXIT_GAME= 10;
    public static final int DIALOG_HOLIDAY = 11;
    public static final int DIALOG_BIRTHDAY = 12;
    public static final int DIALOG_OPPONENT_FOUND = 13;
    public static final int DIALOG_NEW_GAME = 14;
    public static final int DIALOG_ENTER_PASS = 15;
    public static final int DIALOG_DELETE_HERO = 16;

    public static AlertDialog getDialog(final Activity activity, int ID, final SocketConfig conf, String dialogMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        switch (ID) {
            case DIALOG_DELETE_HERO: // //диалог о выходе приложения
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("END");
                        //этот метод перейдет в предыдущее активити и очистит весь стек над ним. И посылаем метку закрытия
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("finish", true);
                        activity.startActivity(intent);
                    }
                });
                return builder.create();


            case DIALOG_ENTER_PASS: // о том, что оппонент найден
                //Задаём название (заголовок) диалога
                builder.setTitle(dialogMessage);
                //Запрещаем закрывать окошко кнопкой "back"
                builder.setCancelable(false);
                //Задаём иконку для диалога
                builder.setIcon(R.drawable.pass);
                //Создаём поле для ввода текста
                final EditText input = new EditText(activity);
                //Добавляем поле для ввода текста к диалогу
                builder.setView(input);
                //Добавляем "позитивную" кнопку
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    //Обрабатываем нажатие "позитивной" кнопки
                    public void onClick(DialogInterface dialog, int which) {
                        //Получаем текст из поля input и записываем в переменную name
                        conf.setSOCKET_MESSAGE(input.getText().toString());
                    }
                });
                //Отображаем окошко
                return builder.create();


            case DIALOG_NEW_GAME: // о том, что клиент желает заново начать игру
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("END");
                        //этот метод перейдет в предыдущее активити и очистит весь стек над ним. И посылаем метку закрытия
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("finish", true);
                        activity.startActivity(intent);
                    }
                });
                return builder.create();

            case DIALOG_OPPONENT_FOUND: // о том, что оппонент найден
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("SELECT");
                    }
                });
                return builder.create();

            case DIALOG_BIRTHDAY: // //диалог поздравление с днем рождения
                builder.setTitle(R.string.birthday);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("GET");
                    }
                });
                return builder.create();

            case DIALOG_HOLIDAY: // //диалог поздравление с праздником
                builder.setTitle(R.string.holiday);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("BIRTHDAY");
                    }
                });
                return builder.create();

            case DIALOG_BOUNTY: //диалог-подарок за ежедневный вход
                builder.setTitle(R.string.daily);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("HOLIDAY");
                    }
                });
                return builder.create();

            case DIALOG_REGISTERED: // //диалог о том, что реестрация прошла успешно
                builder.setTitle(R.string.velcom);
                builder.setMessage(R.string.registered); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE(conf.getID()); // //запрашиваю имя героя и пишу данные о герое в Preferences
                    }
                });
                return builder.create();

            case DIALOG_PLAYER_LOSE: // //диалог на то, что перс проиграл бой  ПОРАЖЕНИЕ
                builder.setTitle(R.string.Lose);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("REGEN");
                    }
                });
                return builder.create();

            case DIALOG_PLAYER_VICTORY: // //диалог на то, что перс победил в бою  ПОБЕДА
                builder.setTitle(R.string.TitleVictory);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("UP_LVL");
                    }
                });
                return builder.create();

            case DIALOG_PLAYER_LVL_UP: // //диалог на то, что перс аппнулся
                builder.setTitle(R.string.lvl_up);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("UP_PVP");
                    }
                });
                return builder.create();

            case DIALOG_PLAYER_PVP_UP: // //диалог на то, что перс получил новое звание
                builder.setTitle(R.string.pvp_up);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("REGEN");
                    }
                });
                return builder.create();

            case DIALOG_NO_ACCOUNTS: // //диалог о том, что нет ни одного аккаунта Гугл
                builder.setTitle(R.string.error);
                builder.setMessage(R.string.no_accounts); // сообщение
                builder.setPositiveButton(R.string.Exit, new DialogInterface.OnClickListener() { // Кнопка Выход
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish(); //закрываю активити
                    }
                });
                return builder.create();

            case DIALOG_EXIT_GAME: // //диалог о выходе приложения
                builder.setTitle(R.string.exit);
                builder.setMessage(R.string.qExit); // сообщение
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("END");
                        //этот метод перейдет в предыдущее активити и очистит весь стек над ним. И посылаем метку закрытия
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("finish", true);
                        activity.startActivity(intent);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });
                return builder.create();

            case DIALOG_PLAYER_SICK: // //диалог о том, что герой ранен и боги могут его исцелить
                //builder.setTitle(R.string.good);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.donate, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("YES"); //оплатить за исцелене
                    }
                });
                builder.setNegativeButton(R.string.no_donate, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                        conf.setSOCKET_MESSAGE("NO"); //оплатить за исцелене
                    }
                });
                return builder.create();

            case DIALOG_OK: // стандартный информационный диалог (Окейка)
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });
                return builder.create();

            default:
                return null;
        }
    }
}
