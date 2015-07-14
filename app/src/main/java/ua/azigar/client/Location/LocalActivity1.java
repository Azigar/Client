package ua.azigar.client.Location;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import ua.azigar.client.Client.ExitGame;
import ua.azigar.client.Client.SocketConfig;
import ua.azigar.client.MainActivity;
import ua.azigar.client.R;
import ua.azigar.client.Resources.DialogScreen;
import ua.azigar.client.Resources.MyHero;
import ua.azigar.client.Resources.Progress_exp;
import ua.azigar.client.Resources.Progress_hp;
import ua.azigar.client.Resources.Progress_mana;
import ua.azigar.client.Resources.Progress_pvp;
import ua.azigar.client.Resources.Regeneration;

/**
 * Created by Azigar on 08.07.2015.
 */
public class LocalActivity1 extends ActionBarActivity {

    Handler h_hp, h_mana, h_exit;
    AlertDialog.Builder builder;
    SocketConfig conf = new SocketConfig(); //подключаю новый екземпляр conf для клиента регистрации
    Intent intent;
    Regeneration regeneration_hp, regeneration_mana;
    SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:
    final String LOCATION = "ua.azigar.client.intent.action.LocalActivity1";
    static final String PREF_LOCATION = "LOCATION";

    static TextView txtNameHero, txtTitle, txtMoney, txtGold, data_checking;
    static Progress_hp hpHero;
    static Progress_mana manaHero;
    static Progress_exp expHero;
    static Progress_pvp pvpHero;
    static ImageView imgVIP, imgLocal;
    static ListView listView;
    static ProgressBar pbConnect;

    String[] selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //задаю програмно заголовок
        setTitle(R.string.local1);
        //убираю ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //подключаем лауер-файл с элементами
        setContentView(R.layout.activity_local1);
        //подключаю глобальные переменные для хранения данных о герое
        final MyHero app = ((MyHero)getApplicationContext());
        //пишу активную локацию в Preferences
        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        final SharedPreferences.Editor ed = sPref.edit();  //подключаю Editor для сохранение новых параметров в SharedPreferences
        ed.putString(PREF_LOCATION, LOCATION);
        ed.commit(); //сохраняю флаг о активной локации
        //пишу данные переменные сокета для отправки
        conf.setID(app.getID()); //cохраняю ID игрока
        conf.setMANA(app.getMANA()); //сохраняю к-во маны героя
        conf.setHP(app.getHP()); //сохраняю к-во ОЗ героя
        //инициализирую обьекты
        pbConnect = (ProgressBar) findViewById(R.id.pbConnect);
        data_checking = (TextView) findViewById(R.id.data_checking);
        txtNameHero = (TextView) findViewById(R.id.txtNameHero);
            txtNameHero.setText(app.getNAME() + " [" + app.getLVL() + "]");
        txtTitle = (TextView) findViewById(R.id.txtTitle);
            txtTitle.setText(app.getTITLE());
        txtMoney = (TextView) findViewById(R.id.txtMoney);
            txtMoney.setText(app.getMONEY() + " монет");
        txtGold = (TextView) findViewById(R.id.txtGold);
            txtGold.setText(app.getGOLD() + " G");
        hpHero = (Progress_hp) findViewById(R.id.hpHero);
            hpHero.setMaxValue(Integer.parseInt(app.getMAX_HP()));
            hpHero.setValue(Integer.parseInt(app.getHP()));
        manaHero = (Progress_mana) findViewById(R.id.manaHero);
            manaHero.setMaxValue(Integer.parseInt(app.getMAX_MANA()));
            manaHero.setValue(Integer.parseInt(app.getMANA()));
        expHero = (Progress_exp) findViewById(R.id.expHero);
            expHero.setMaxValue(Integer.parseInt(app.getMAX_EXP()));
            expHero.setValue(Integer.parseInt(app.getEXP()));
        pvpHero = (Progress_pvp) findViewById(R.id.pvpHero);
            pvpHero.setMaxValue(Integer.parseInt(app.getMAX_PVP_EXP()));
            pvpHero.setValue(Integer.parseInt(app.getPVP_EXP()));
        imgLocal = (ImageView) findViewById(R.id.imgLocal);
        imgVIP = (ImageView) findViewById(R.id.imgVIP);
            switch (Integer.parseInt(app.getVIP())){
                case 0: imgVIP.setImageResource(R.drawable.vip0);break;
                case 1: imgVIP.setImageResource(R.drawable.vip1);break;
                case 2: imgVIP.setImageResource(R.drawable.vip2);break;
                case 3: imgVIP.setImageResource(R.drawable.vip3);break;
                case 4: imgVIP.setImageResource(R.drawable.vip4);break;
                case 5: imgVIP.setImageResource(R.drawable.vip5);break;

            }
        listView = (ListView)findViewById(R.id.listView);
        // устанавливаем режим выбора пунктов списка
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // определяем массив данных для списка типа String
        selection = getResources().getStringArray(R.array.select1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,	R.layout.list_item, selection);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.select1, R.layout.list_item);
        listView.setAdapter(adapter);
        //обработчик нажатия списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String n;
                switch (position) {
                    case 0:
                        Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        n = String.valueOf(((TextView) view).getText());
                        SickHero(app.getHP(), app.getMAX_HP(), "1", "1", "45", "0", n.substring(n.indexOf("--") + 3));
                        break;

                    case 3:
                        n = String.valueOf(((TextView) view).getText());
                        SickHero(app.getHP(), app.getMAX_HP(), "2", "1", "50", "0", n.substring(n.indexOf("--") + 3));
                        break;

                    case 4:
                        n = String.valueOf(((TextView) view).getText());
                        SickHero(app.getHP(), app.getMAX_HP(), "4", "1", "35", "0", n.substring(n.indexOf("--") + 3));
                        break;
                }
            }
        });

        DisableExit();

        //ханлер
        h_hp = new Handler() {
            public void handleMessage(android.os.Message msg) {
                hpHero.setValue(msg.what); //востанавливаю здоровье
                app.setHP(String.valueOf(msg.what)); //пишу новое значение в глобальную переменную
                conf.setHP(app.getHP());
            }
        };
        if (Integer.parseInt(app.getHP()) < Integer.parseInt(app.getMAX_HP())) {
            regeneration_hp = new Regeneration(h_hp, Integer.parseInt(app.getMAX_HP()),
                    Integer.parseInt(app.getHP()), Integer.parseInt(app.getVIP()), 1); //запуск регенерации
        }

        //ханлер
        h_mana = new Handler() {
            public void handleMessage(android.os.Message msg) {
                manaHero.setValue(msg.what); //востанавливаю ману
                app.setMANA(String.valueOf(msg.what)); //пишу новое значение в глобальную переменную
                conf.setMANA(app.getMANA());
            }
        };
        if (Integer.parseInt(app.getMANA()) < Integer.parseInt(app.getMAX_MANA()) &&
                Integer.parseInt(app.getMAX_MANA()) > 1) {
            regeneration_mana = new Regeneration(h_mana, Integer.parseInt(app.getMAX_MANA()),
                    Integer.parseInt(app.getMANA()), Integer.parseInt(app.getVIP()), 0); //запуск регенерации
        }

        //ханлер
        h_exit = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what){
                    case 1:
                        data_checking.setText("Сохраняю данные героя на сервер...");
                        conf.setSOCKET_MESSAGE("ENTER");  //отправляю команду, что покидаю игру
                        break;

                    case 2:
                        Toast.makeText(LocalActivity1.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        Exit();
                        break;

                    case 3:
                        Toast.makeText(LocalActivity1.this, R.string.synchronization, Toast.LENGTH_LONG).show();
                        Exit();
                        break;
                }
            }
        };
    }

    //метод определяет здоровье героя
    private void SickHero(String HP, String MAX_HP, final String id, final String lvl, final String hp, final String mana, final String name){
        boolean sickHero = true;
        if (Integer.parseInt(HP) < Integer.parseInt(MAX_HP)) { //если здоровья меньше мак. к-ва, то
            builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_info); // иконка
            builder.setMessage(R.string.Pleayer_sick); // сообщение
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка Да
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StopRegen(); //принудительно закрываю потоки на реген
                    Attack(id, lvl, hp, mana, name);
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // Отпускает диалоговое окно
                }
            }).create().show();
        } else {
            Attack(id, lvl, hp, mana, name);
        }
    }

    //прячу все элементы локации
    void Attack(String id, String lvl, String hp, String mana, String name) {
        intent = new Intent("ua.azigar.client.intent.action.FIGHT");
        intent.putExtra("id", id);
        intent.putExtra("lvl", lvl);
        intent.putExtra("hp", hp);
        intent.putExtra("mana", mana);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    //прячу все элементы локации
    static void DisableAll() {
        txtNameHero.setVisibility(View.GONE);
        txtTitle.setVisibility(View.GONE);
        txtMoney.setVisibility(View.GONE);
        txtGold.setVisibility(View.GONE);
        hpHero.setVisibility(View.GONE);
        manaHero.setVisibility(View.GONE);
        expHero.setVisibility(View.GONE);
        pvpHero.setVisibility(View.GONE);
        imgVIP.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        imgLocal.setVisibility(View.GONE);
    }

    //показываю все элементы для сохранения и закрытия иры
    static void EnableExit() {
        data_checking.setVisibility(View.VISIBLE);
        pbConnect.setVisibility(View.VISIBLE);
    }

    //прячу все элементы для сохранения и закрытия иры
    static void DisableExit() {
        pbConnect.setVisibility(View.GONE);
        data_checking.setVisibility(View.GONE);
    }

    //этот метод перейдет в предыдущее активити и очистит весь стек над ним. И посылаем метку закрытия
    private void Exit() {
        intent = new Intent(LocalActivity1.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("finish", true);
        LocalActivity1.this.startActivity(intent);
    }

    //прикрепляю меню к активити
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local, menu);
        return true;
    }

    //обработчик меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.Bank :

                return true;

            case R.id.Hero:

                return true;

            case R.id.Invent:

                return true;

            case R.id.PvP:

                return true;

            case R.id.Settings:

                return true;

            case R.id.About:

                return true;

            case R.id.Exit:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.exit);
                builder.setMessage(R.string.qExit); // сообщение
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StopRegen(); //принудительно закрываю потоки на реген
                        //убираю заголовок приложения, обьекты локации и показываю окно сохранение данных
                        ActionBar actionBar = getSupportActionBar();
                        actionBar.hide();
                        DisableAll();
                        EnableExit();
                        new ExitGame(h_exit, conf);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                    }).create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void StopRegen(){
        if (regeneration_hp != null) {
            regeneration_hp.MyStop();
            regeneration_hp.interrupt(); //закрываю поток на востановление
            regeneration_hp = null;
        }
        if (regeneration_mana != null) {
            regeneration_mana.MyStop();
            regeneration_mana.interrupt(); //закрываю поток на востановление
            regeneration_mana = null;
        }
    }

    //обработка остановки активити
    @Override
    protected void onStop() {
        super.onStop();
        StopRegen();
    }

    //обработка кнопки назад
    @Override
    public void onBackPressed () {
        //ничего не делать
    }

    //обработка закрытия (уничтожения) активити
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
