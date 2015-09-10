package ua.azigar.client.Resources;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

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
    public static final int DIALOG_PLAYER_PVP_UP_FIGHT = 6;
    public static final int DIALOG_NO_ACCOUNTS = 7;
    public static final int DIALOG_BOUNTY = 8;
    public static final int DIALOG_REGISTERED = 9;
    public static final int DIALOG_EXIT_GAME= 10;
    public static final int DIALOG_HOLIDAY = 11;
    public static final int DIALOG_BIRTHDAY = 12;
    public static final int DIALOG_OPPONENT_FOUND = 13;
    public static final int DIALOG_RESTART_GAME = 14;
    public static final int DIALOG_ENTER_PASS_AUTHOR = 15;
    public static final int DIALOG_DELETE_HERO = 16;
    public static final int DIALOG_ENTER_PASS = 17;
    public static final int DIALOG_ENTER_NAME_HERO = 18;
    public static final int DIALOG_TWO_HANDS = 19;
    public static final int DIALOG_NEW_PASS = 20;
    public static final int DIALOG_ENTER_COUNT_OBJ = 21;
    public static final int DIALOG_ERROR_ENTER_PASS = 22;
    public static final int DIALOG_ERROR_ENTER_DATA = 23;
    public static final int DIALOG_ERROR_ENTER_PASS2 = 24;
    public static final int DIALOG_NEW_NAME_HERO = 25;
    public static final int DIALOG_BEGIN_NAME_HERO = 26;
    public static final int DIALOG_NEW_GOOGLE = 27;
    public static final int DIALOG_ERROR_ENTER_COUNT_OBJ = 28;
    public static final int DIALOG_PLAYER_PVP_UP_QUEST = 29;

    static EditText input;

    public static AlertDialog getDialog(final Activity activity, int ID, final SocketConfig conf, String dialogMessage) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(android.R.drawable.ic_dialog_info); // иконка
        builder.setCancelable(false); //Запрещаем закрывать окошко кнопкой "back"
        input = new EditText(activity);


        switch (ID) {
            case DIALOG_ERROR_ENTER_COUNT_OBJ: // не верный ввод количества предметов на продажу
                builder.setTitle(R.string.error);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.begin, new DialogInterface.OnClickListener() { // Кнопка Повторить ввод
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("BEGIN");
                    }
                });
                return builder.create();


            case DIALOG_NEW_GOOGLE: // меняюм имя герою
                builder.setIcon(R.drawable.pass);
                builder.setTitle(R.string.newGoogle); //Задаём название (заголовок) диалога
                builder.setMessage(dialogMessage); // сообщение
                builder.setView(input); //Добавляем поле для ввода текста к диалогу
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    //Обрабатываем нажатие "позитивной" кнопки
                    public void onClick(DialogInterface dialog, int which) {
                        String acount = input.getText().toString();
                        if(acount.endsWith("@gmail.com") == true) { //если верно введен Гугл-аккаунт
                            acount = acount.substring(acount.indexOf(""), acount.indexOf("@")); //вытягиваю логин с Google-аккаунта
                            conf.setSOCKET_MESSAGE(acount);
                        }else{
                            String ff = "Вы неверно ввели Google-аккаунт.";
                            Toast.makeText(activity, ff, Toast.LENGTH_LONG).show();
                            conf.setSOCKET_MESSAGE("END");
                            dialog.dismiss(); // Отпускает диалоговое окно
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // Кнопка Выйти
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("END");
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });
                //Отображаем окошко
                return builder.create();


            case DIALOG_BEGIN_NAME_HERO: // не верно введен пароль
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.begin, new DialogInterface.OnClickListener() { // Кнопка Повторить ввод
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("BEGIN");
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // Кнопка Востановить
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("END");
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });
                return builder.create();


            case DIALOG_NEW_NAME_HERO: // меняюм имя герою
                builder.setIcon(R.drawable.pass);
                builder.setTitle(dialogMessage); //Задаём название (заголовок) диалога
                builder.setView(input); //Добавляем поле для ввода текста к диалогу
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    //Обрабатываем нажатие "позитивной" кнопки
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE(input.getText().toString());
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // Кнопка Выйти
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("END");
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });
                //Отображаем окошко
                return builder.create();


            case DIALOG_ERROR_ENTER_PASS2: // не верно введен пароль
                builder.setIcon(R.drawable.pass);
                builder.setTitle(R.string.error);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.begin, new DialogInterface.OnClickListener() { // Кнопка Повторить ввод
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("BEGIN");
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // Кнопка Востановить
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("END");
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });
                return builder.create();


            case DIALOG_ERROR_ENTER_DATA: // не верный ввод данных
                builder.setIcon(R.drawable.pass);
                builder.setTitle(R.string.error);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.begin, new DialogInterface.OnClickListener() { // Кнопка Повторить ввод
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("BEGIN");
                    }
                });
                builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() { // Кнопка Востановить
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


            case DIALOG_ERROR_ENTER_PASS: // не верный ввод данных
                builder.setIcon(R.drawable.pass);
                builder.setTitle(R.string.error);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.begin, new DialogInterface.OnClickListener() { // Кнопка Повторить ввод
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("BEGIN");
                    }
                });
                builder.setNeutralButton(R.string.reg_pass, new DialogInterface.OnClickListener() { // Кнопка Востановить
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("REG_PASS");
                    }
                });
                builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() { // Кнопка Востановить
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


            case DIALOG_ENTER_COUNT_OBJ: //указываю к-во вещей на продажу
                builder.setTitle(dialogMessage); //Задаём название (заголовок) диалога
                builder.setView(input); //Добавляем поле для ввода текста к диалогу
                input.setInputType(InputType.TYPE_CLASS_NUMBER); //только числа
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE(input.getText().toString());
                    }
                });
                return builder.create();


            case DIALOG_NEW_PASS: // показывает новый пароль
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


            case DIALOG_TWO_HANDS: // ввод уровня героя для востановления Пароля
                builder.setMessage(dialogMessage); // сообщение
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.right_hand, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("RIGHT");
                    }
                });
                builder.setNegativeButton(R.string.left_hand, new DialogInterface.OnClickListener() { // Кнопка Выйти
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("LEFT");
                    }
                });
                //Отображаем окошко
                return builder.create();


            case DIALOG_ENTER_NAME_HERO: // ввод имени героя для востановления Пароля
                builder.setTitle(dialogMessage); //Задаём название (заголовок) диалога
                builder.setIcon(R.drawable.pass); //Задаём иконку для диалога
                input = new EditText(activity); //Создаём поле для ввода текста
                builder.setView(input); //Добавляем поле для ввода текста к диалогу
                //Добавляем "позитивную" кнопку
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    //Обрабатываем нажатие "позитивной" кнопки
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE(input.getText().toString()); //Получаем текст из поля input и записываем в переменную name
                    }
                });
                builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() { // Кнопка Выйти
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
                //Отображаем окошко
                return builder.create();


            case DIALOG_ENTER_PASS: // ввод пароля
                builder.setTitle(dialogMessage);
                builder.setIcon(R.drawable.pass);
                input = new EditText(activity);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); //пароль
                builder.setView(input);
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    //Обрабатываем нажатие "позитивной" кнопки
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE(input.getText().toString());
                    }
                });
                builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() { // Кнопка Выйти
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("END");
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });
                //Отображаем окошко
                return builder.create();


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


            case DIALOG_ENTER_PASS_AUTHOR: // ввод пароля при авторизации
                builder.setTitle(dialogMessage);
                builder.setIcon(R.drawable.pass);
                input = new EditText(activity);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); //пароль
                builder.setView(input);
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    //Обрабатываем нажатие "позитивной" кнопки
                    public void onClick(DialogInterface dialog, int which) {
                        //Получаем текст из поля input и отправляем пароль серверу
                        conf.setSOCKET_MESSAGE(input.getText().toString());
                    }
                });
                builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() { // Кнопка Выйти
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("END");
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("finish", true);
                        activity.startActivity(intent);
                    }
                });
                //Отображаем окошко
                return builder.create();


            case DIALOG_RESTART_GAME: // о перезапуске приложения
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() { // Кнопка ОК
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
                builder.setIcon(R.drawable.gift);
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
                builder.setIcon(R.drawable.gift);
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
                builder.setIcon(R.drawable.gift);
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
                builder.setIcon(R.drawable.reg);
                builder.setTitle(R.string.velcom);
                builder.setMessage(R.string.registered); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("OK_REG"); // //запрашиваю имя героя и пишу данные о герое в Preferences
                    }
                });
                return builder.create();


            case DIALOG_PLAYER_LOSE: // //диалог на то, что перс проиграл бой  ПОРАЖЕНИЕ
                builder.setTitle(R.string.Lose);
                builder.setIcon(R.drawable.died);
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
                builder.setIcon(R.drawable.victory);
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
                builder.setIcon(R.drawable.lvl_up);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("UP_PVP");
                    }
                });
                return builder.create();

            case DIALOG_PLAYER_PVP_UP_FIGHT: // //диалог на то, что перс получил новое звание после Битвы
                builder.setTitle(R.string.pvp_up);
                builder.setIcon(R.drawable.pvp_up);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("REGEN");
                    }
                });
                return builder.create();

            case DIALOG_PLAYER_PVP_UP_QUEST: // //диалог на то, что перс получил новое звание после завершение квеста
                builder.setTitle(R.string.pvp_up);
                builder.setIcon(R.drawable.pvp_up);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });
                return builder.create();


            case DIALOG_NO_ACCOUNTS: // //диалог о том, что нет ни одного аккаунта Гугл
                builder.setTitle(R.string.error);
                builder.setIcon(R.drawable.reg);
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
                builder.setIcon(R.drawable.exit);
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
