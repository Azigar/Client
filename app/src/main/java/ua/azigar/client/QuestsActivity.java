package ua.azigar.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import ua.azigar.client.Resources.Database;
import ua.azigar.client.Resources.FragmentPagerAdapterQuest;
import ua.azigar.client.Resources.MyHero;
import ua.azigar.client.Resources.SocketConfig;

/**
 * Created by Azigar on 09.09.2015.
 */

public class QuestsActivity extends ActionBarActivity {

    ViewPager pager;
    Database db;
    Intent intent;
    SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:
    static final String PREF_LOCATION = "LOCATION";
    SocketConfig conf = new SocketConfig(); //подключаю новый екземпляр conf для клиента регистрации

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //убираю ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //убираю заголовок приложения
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_quests);

        final MyHero app = ((MyHero)getApplicationContext());
        //пишу в конф. последнее активити локации
        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        conf.setLAST_LOCAL(sPref.getString(PREF_LOCATION, "ua.azigar.client.intent.action.LocalActivity1")); //пишу в конф. последнее активити
        intent = new Intent(conf.getLAST_LOCAL()); //подключаю к новуму Intent последнее активити локации

        //Подключение и работа с БД
        db = new Database(this); //подключаю класс

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapterQuest(this, getSupportFragmentManager(), db));
    }

    //обработка кнопки назад
    @Override
    public void onBackPressed () { startActivity(intent); }

    //обработка закрытия (уничтожения) активити
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

