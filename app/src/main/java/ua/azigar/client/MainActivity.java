package ua.azigar.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity extends Activity {

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("finish", false)) finish(); //закрытие активити

        //подключаем лауер-файл с элементами
        setContentView(R.layout.activity_main);

        final String[] accn = getAccNames(); //пишу в массив все имена аакаунтов аккаунты
        if (accn.length==0) {  //если длина массива = 0 - аккаунтов нет
            builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error);
            builder.setMessage(R.string.no_accounts); // сообщение
            builder.setPositiveButton(R.string.Exit, new DialogInterface.OnClickListener() { // Кнопка Выход
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish(); //закрываю активити
                    Toast.makeText(MainActivity.this, R.string.ExitForGame, Toast.LENGTH_LONG).show();
                }
            }).create().show();
        } else {  //запуск диалог для выбора аккаута
            builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.Select_account);
            builder.setItems(accn, new DialogInterface.OnClickListener() {  //список выбора
                @Override
                public void onClick(DialogInterface dialog, final int which) {
                    Intent intent = new Intent("ua.azigar.client.intent.action.Authorization");
                    intent.putExtra("id", accn[which]);
                    startActivity(intent);
                }
            }).create().show();
        }
    }

    //подключаю AccountManager
    private Account[] getAccounts() {
        AccountManager am = AccountManager.get(this); // current Context
        Account[] accounts = am.getAccountsByType("com.google");
        return accounts;
    }

    //метод для вытягивания имена аккаунтов
    private String[] getAccNames() {
        Account[] accounts = getAccounts();
        String[] rez = new String[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            rez[i] = accounts[i].name;
        }
        return rez;
    }

    //обработка кнопки назад
    @Override
    public void onBackPressed ()
    {
        finish();
    }

    //обработка при уничтожении активити
    @Override
    protected void onDestroy() {
        Toast.makeText(this, R.string.ExitForGame, Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}
