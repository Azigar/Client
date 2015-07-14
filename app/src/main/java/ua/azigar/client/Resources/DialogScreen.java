package ua.azigar.client.Resources;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import ua.azigar.client.Client.SocketConfig;
import ua.azigar.client.MainActivity;
import ua.azigar.client.R;

/**
 * Created by Azigar on 02.07.2015.
 */
public class DialogScreen {

    // Идентификаторы диалоговых окон
    //public static final int DIALOG_LEAVE_FIGHT = 1;
    public static final int DIALOG_PLAYER_SICK = 2;
    public static final int DIALOG_PLAYER_LOSE = 3;
    public static final int DIALOG_PLAYER_VICTORY = 4;
    public static final int DIALOG_PLAYER_LVL_UP = 5;
    public static final int DIALOG_PLAYER_PVP_UP = 6;
    public static final int DIALOG_NO_ACCOUNTS = 7;
    public static final int DIALOG_NAME_EXISTS = 8;
    public static final int DIALOG_REGISTERED = 9;
    public static final int DIALOG_EXIT_GAME= 10;

    public static AlertDialog getDialog(final Activity activity, int ID, final SocketConfig conf, String dialogMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        switch (ID) {
            case DIALOG_PLAYER_LOSE: // //диалог на то, что перс проиграл бой  ПОРАЖЕНИЕ
                //умеет показывать активити
                //View view = activity.getLayoutInflater().inflate(R.layout.activity_main, null); // Получаем layout по его ID
                //builder.setView(view);

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
                        Toast.makeText(activity, R.string.ExitForGame, Toast.LENGTH_LONG).show();
                    }
                });
                return builder.create();

            case DIALOG_NAME_EXISTS: // //диалог о том, что введеное имя нового героя уже существует в БД
                builder.setTitle(R.string.error);
                builder.setMessage(R.string.name_exists); // сообщение
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
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

            case DIALOG_PLAYER_SICK: // //диалог о выходе приложения
                builder.setTitle(R.string.good);
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conf.setSOCKET_MESSAGE("YES"); //оплатить за исцелене
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                        conf.setSOCKET_MESSAGE("NO"); //оплатить за исцелене
                    }
                });
                return builder.create();

            default:
                return null;
        }
    }
}
