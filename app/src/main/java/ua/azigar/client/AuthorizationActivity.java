package ua.azigar.client;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.Calendar;
import ua.azigar.client.Client.Authorization;
import ua.azigar.client.Resources.SocketConfig;
import ua.azigar.client.Resources.DialogScreen;
import ua.azigar.client.Resources.MyHero;
import ua.azigar.client.Resources.NoName;

/**
 * Created by Azigar on 06.07.2015.
 */
public class AuthorizationActivity extends ActionBarActivity {

    Handler h;
    Intent intent;
    Authorization authorization;
    SocketConfig conf = new SocketConfig(); //подключаю новый екземпляр conf для клиента регистрации
    AlertDialog dialog;
    SharedPreferences sPref;  //подключаю экземпляр класса SharedPreferences:

    final int FIRST_LIST = 0; // первый элемент списка
    int SEX, length; //пол героя
    String [] sex;
    String last_local;

    // определяем текущую дату
    final Calendar c = Calendar.getInstance();
    final int todayYear = c.get(Calendar.YEAR);
    final int todayMonth = c.get(Calendar.MONTH);
    final int todayDay = c.get(Calendar.DAY_OF_MONTH);
    int myYear, myMonth, myDay;
    String dateBirth = "", txtDateBirth = "";
    final int DIALOG_DATE = 1;

    static final String PREF_LOCATION = "LOCATION";

    static TextView textView1, textView2, data_checking;
    static EditText add_name_hero, add_pass;
    static Button btn_add_avatar, btn_create_hero, btn_dateb_birth;
    static ProgressBar pbConnectEnter;
    static ImageView avatar;
    static Spinner spinner;

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
        intent = getIntent();
        String id = intent.getStringExtra("id");  //сохраняю имя аккаунта в переменную
        id = id.substring(id.indexOf(""), id.indexOf("@")); //вытягиваю логин с Google-аккаунта
        app.setID(id);  //сохраняю ID в глобальную переменную
        conf.setID(id); //сохраняю ID в сокет-переменную

        final String PREF = app.getID(); // это будет имя файла настроек
        sPref = getSharedPreferences(PREF, Context.MODE_PRIVATE); //инициализирую SharedPreferences
        final SharedPreferences.Editor ed = sPref.edit();  //подключаю Editor для сохранение новых параметров в SharedPreferences
        last_local = sPref.getString(PREF_LOCATION, "ua.azigar.client.intent.action.LocalActivity1");
        ////найдем View-элементы
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        add_name_hero =  (EditText) findViewById(R.id.add_name_hero);
        add_pass =  (EditText) findViewById(R.id.add_pass);
        data_checking = (TextView) findViewById(R.id.data_checking);
        btn_add_avatar = (Button) findViewById(R.id.btn_add_avatar);
        btn_create_hero = (Button) findViewById(R.id.btn_create_hero);
        btn_dateb_birth = (Button) findViewById(R.id.btn_dateb_birth);
        pbConnectEnter = (ProgressBar) findViewById(R.id.pbConnectEnter);
        avatar = (ImageView) findViewById(R.id.avatar);
            avatar.setImageResource(R.drawable.men1);
        spinner = (Spinner) findViewById(R.id.spinner);
        //создаем массыв з ресурсов
        sex = getResources().getStringArray(R.array.sex);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sex);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(R.layout.list_author);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Пол героя");
        // выделяем первый элемент в списке
        spinner.setSelection(0);
        //обработчик нажатия списка
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //выбран первый элемент
                        SEX = 1;
                        avatar.setImageResource(R.drawable.men1); //показываю аватар
                        conf.setAVATAR("men1"); //пишу его в конф. на отправку
                        conf.setSEX(String.valueOf(SEX));
                        break;
                    case 1: //выбран второй элемент
                        SEX = 2;
                        avatar.setImageResource(R.drawable.women1);  //показываю аватар
                        conf.setAVATAR("women1");  //пишу его в конф. на отправку
                        conf.setSEX(String.valueOf(SEX));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //прячу все элементы для регистрации
        DisableAll();
        //подключаю Handler
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                String dialogMessage;
                switch (msg.what) {
                    case 1:  //подключение к серверу успешно
                        conf.setSOCKET_MESSAGE("ENTER");
                        break;

                    case 2:  //Не удалось подключится к серверу
                        Toast.makeText(AuthorizationActivity.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        finish(); //закрываю активити
                        break;

                    case 3:  //сервер говорит, что есть такой акк
                        conf.setSOCKET_MESSAGE("BOUNTY");  //запрос на ежедневный подарок
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
                        dialogMessage = "Герой с таким именем уже существует. Пожалуйста, введите новое имя.";
                        dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 13:  //сервер говорит, что регестрация прошла успешно
                        dialogMessage = "Регистрация Вашего героя прошла успешно.";
                        dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_REGISTERED, conf, dialogMessage);
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
                        conf.setSOCKET_MESSAGE("SEX"); //запрашиваю пол героя
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

                    case 22:  //сервер прислал подарок за ежелневный вход
                        dialogMessage = (String) msg.obj;
                        dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_BOUNTY, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 23:  //сервер прислал пол героя
                        app.setSEX((String) msg.obj);
                        conf.setSOCKET_MESSAGE("AVATAR"); //запрашиваю аватар героя
                        break;

                    case 24:  //сервер прислал аватар героя
                        app.setAVATAR((String) msg.obj);
                        conf.setSOCKET_MESSAGE("IS_PASS"); //запрашиваю защиту паролем
                        break;

                    case 25:  //сервер прислал подарок на праздник
                        dialogMessage = (String) msg.obj;
                        dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_HOLIDAY, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 26:  //сервер прислал подарок на день рождение
                        dialogMessage = (String) msg.obj;
                        dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_BIRTHDAY, conf, dialogMessage);
                        dialog.show();
                        break;

                    case 27:  //сервер говорит, что можно входить без пароля
                        conf.setSOCKET_MESSAGE("END"); //закрываю сокет-поток
                        //считываю с Preferences какая была последння локация и запускаю акктивити локации
                        intent = new Intent(last_local);
                        startActivity(intent);
                        break;

                    case 28:  //сервер спрашивает пароль
                        dialogMessage = "Введите пароль";
                        dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_ENTER_PASS, conf, dialogMessage);
                        dialog.show();
                        break;
                }
            }
        };
        //запуск клиента регистации
        authorization = new Authorization(h, conf);
    }

    //метод, который вызывается в showDialog для создания диалога  DatePickerDialog - выбор даты
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, todayYear, todayMonth, todayDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    //обработчик интерфейса DatePickerDialog, срабатывает на нажатие кнопки ОК в диалоге
    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int month, int day) {
            myYear = year;
            myMonth = month + 1;
            String sMonth = String.valueOf(myMonth);
            length = sMonth.length(); //считаю к-во символов
            if (length == 1) {
                sMonth = "0" + sMonth;
            }
            myDay = day;
            String sDay = String.valueOf(myDay);
            length = sDay.length(); //считаю к-во символов
            if (length == 1) {
                sDay = "0" + sDay;
            }
            if ((todayYear - myYear) < 16){ //если возраст игрока меньше 16 лет, тогда не пускам в игру
                Toast.makeText(AuthorizationActivity.this, R.string.small_age, Toast.LENGTH_LONG).show();
            } else {
                dateBirth = myYear + "-" + sMonth + "-" + sDay;
                txtDateBirth = sDay + "." + sMonth + "." + myYear;
                conf.setDATE_BIRTH(dateBirth); //пишу в конф. для отправки
                btn_dateb_birth.setText(txtDateBirth); //показываю выбраную дату на кнопке
            }
        }
    };

    //обработчи кнопки Дата рождения
    public void onClickDialogDate(View v) {
        showDialog(DIALOG_DATE); //запускаю DatePickerDialog
    }

    //обработчи кнопки Создать Героя
    public void onClickCreateHero(View v) {
        NoName noName = new NoName();
        String dialogMessage;
        boolean isYes = true;
        if (add_name_hero.getText().toString().equalsIgnoreCase("")){ //если не указали имя героя
            dialogMessage = "Укажите, пожалуйста, имя нового героя";
            dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            isYes = false;
        }
        if (add_name_hero.getText().toString().length() > 45) { //если имя героя больше 45 символов
            dialogMessage = "Длина имени не должна привышать 45 символов";
            dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            isYes = false;
            add_name_hero.setText("");
        }
        if (add_pass.getText().toString().equalsIgnoreCase("") ||
                add_pass.getText().toString().equalsIgnoreCase("Пароль") ||
                add_pass.getText().toString().equalsIgnoreCase("END") ||
                add_pass.getText().toString().equalsIgnoreCase("null")) { //если не ввели пароль
            dialogMessage = "Пожалуйста, введите пароль";
            dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            isYes = false;
            add_pass.setText("");
        }
        if (add_pass.getText().toString().length() > 15) { //если длина пароля превышает 15 символов
            dialogMessage = "Длина пароля не должна превышать 15 символов";
            dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            isYes = false;
            add_pass.setText("");
        }
        if (dateBirth.equalsIgnoreCase("")) { //если не указана дата рождения игрока
            dialogMessage = "Укажите, пожалуйста, дату рождения";
            dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            isYes = false;
        }
        if (noName.NoName(add_name_hero.getText().toString()) == false){ //если имя героя совпадает с командой обмена данными
            dialogMessage = "Извините, пожалуйста, герой в Мидгарде не может иметь такое имя";
            dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            isYes = false;
        }
        if (dateBirth.equalsIgnoreCase("")) { //если не указана дата рождения игрока
            dialogMessage = "Укажите, пожалуйста, дату рождения";
            dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
            isYes = false;
        }
        if (isYes == true) { //если все проло гладко
            conf.setSOCKET_MESSAGE(add_name_hero.getText().toString()); //отправляю имя нового героя
            conf.setPASS(add_name_hero.getText().toString());
            DisableAll(); //прячу все элементы для регистрации
            EnableDataChecking(); //показываю все элементы для загрузки
            noName = null;
        } else {
            dialog.show();
            noName = null;
        }
    }

    //обработчи кнопки Выбрать Аватар
    public void onClickAddAvatar(View v) {
        Intent intent = new Intent(this, AvatarActivity.class);
        intent.putExtra("sex", SEX);
        intent.putExtra("status", "REGEN");
        startActivityForResult(intent, 1);
    }

    //прячу все элементы для регистрации
    static void DisableAll() {
        textView1.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        add_pass.setVisibility(View.GONE);
        btn_add_avatar.setVisibility(View.GONE);
        btn_create_hero.setVisibility(View.GONE);
        add_name_hero.setVisibility(View.GONE);
        avatar.setVisibility(View.GONE);
        btn_dateb_birth.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
    }

    //показываю все элементы для регистрации
    static void EnableAll() {
        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);
        add_pass.setVisibility(View.VISIBLE);
        btn_add_avatar.setVisibility(View.VISIBLE);
        btn_create_hero.setVisibility(View.VISIBLE);
        add_name_hero.setVisibility(View.VISIBLE);
        avatar.setVisibility(View.VISIBLE);
        btn_dateb_birth.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
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

    //метод получение результата з AvatarActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        String fileName = data.getStringExtra("avatar");
        avatar.setImageResource(getResources().getIdentifier(fileName, "drawable", getPackageName()));
        conf.setAVATAR(fileName);
    }

    //обработка кнопки назад
    @Override
    public void onBackPressed () {
        dialog = DialogScreen.getDialog(AuthorizationActivity.this, DialogScreen.DIALOG_EXIT_GAME, conf, "");
        dialog.show();
    }

    //обработка закрытия (уничтожения) активити
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //закрываю сокет-поток
        if (authorization != null){
            conf.setSOCKET_CONNECTED(false); //закрываю второй поток, первый метод
            authorization.MyStop(); //закрываю второй поток, второй метод
            try {
                authorization.socket.close(); //закрываю сокет, а также второй поток, четвертый метод
            } catch (IOException e) {
                e.printStackTrace();
            }
            authorization.interrupt();
            authorization = null;
        }
    }
}
