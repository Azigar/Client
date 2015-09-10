package ua.azigar.client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import ua.azigar.client.Client.ChangeAvatar;
import ua.azigar.client.Resources.SocketConfig;
import ua.azigar.client.Resources.DialogScreen;
import ua.azigar.client.Resources.MyHero;


public class AvatarActivity extends ActionBarActivity {

    static ListView listView;
    static ImageView imageView;
    static Button btn_add_avatar;
    static int SEX;
    static String sex;
    static String [] avatars;
    static final String men = "men";
    static final String women = "women";
    static String fileName, status, res, dialogMessage;
    double gold;
    Handler h;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;
    SocketConfig conf = new SocketConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //убираю ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //убираю заголовок приложения
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //подключаем лауер-файл с элементами
        setContentView(R.layout.activity_avatar);
        //подключаю глобальные переменные для хранения данных о герое
        final MyHero app = ((MyHero) getApplicationContext());
        //пишу данные переменные сокета для отправки
        conf.setID(app.getID()); //cохраняю ID игрока
        //читаю intent-данные от предыдущего активити)
        Intent intent = getIntent();
        sex = intent.getStringExtra("sex");  //сохраняю пол героя в переменную
        SEX = Integer.parseInt(sex);
        status = intent.getStringExtra("status");
        if (status.equalsIgnoreCase("CHANGE")) gold = Double.valueOf(app.getGOLD());  //cохраняю к-во голда
        //найдем View-элементы
        btn_add_avatar = (Button) findViewById(R.id.btn_add_avatar5);
        imageView = (ImageView) findViewById(R.id.imageView5);
        //imageView.setImageResource(R.drawable.men1);

        if (SEX == 1){
            imageView.setImageResource(R.drawable.men1);
            res = men;
            Check();
        }else{
            imageView.setImageResource(R.drawable.women1);
            res = women;
            Check();
        }

        listView = (ListView) findViewById(R.id.listView5);
        //устанавливаем режим выбора пунктов списка
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //создаю адаптер используя массив
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, avatars);
        //подключаю адаптер в список
        listView.setAdapter(adapter);
        //обработчик нажатия списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fileName = res + (position + 1);
                imageView.setImageResource(getResources().getIdentifier(fileName, "drawable", getPackageName())); //применяю рисунок по имени файла
            }
        });

        //подключаю Handler
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                String dialogMessage;
                switch (msg.what) {
                    case 1:  //подключение к серверу успешно
                        conf.setSOCKET_MESSAGE("ENTER");
                        break;

                    case 2:  //Не удалось подключится к серверу
                        Toast.makeText(AvatarActivity.this, R.string.No_connecting, Toast.LENGTH_LONG).show();
                        finish(); //закрываю активити
                        break;

                    case 3:   //сервер говорит к-во голда игрока
                        app.setGOLD((String) msg.obj);
                        conf.setSOCKET_MESSAGE("END"); //закрываю активити боя
                        progressDialog.dismiss();
                        Toast.makeText(AvatarActivity.this, R.string.change_avatar, Toast.LENGTH_LONG).show();
                        finish(); //закрываю активити
                        break;

                    case 4:  //сервер прислал аватар героя
                        app.setAVATAR((String) msg.obj);
                        conf.setSOCKET_MESSAGE("GOLD"); //запрашиваю защиту паролем
                        break;
                }
            }
        };
    }

    // определяем массив данных для списка типа String
    static void Check() {
        int count = 0;
        //если это активити вызвано для изменение аватара нового героя
        if (status.equalsIgnoreCase("REGEN")){
            avatars = new String[5];
            count = 5;
        }
        //если это активити вызвано для изменение аватара существующего героя
        if (status.equalsIgnoreCase("CHANGE")){
            avatars = new String[18];
            count = 18;
        }
        for (int i = 0; i < count; i++){ // определяем массив данных для списка типа String
            avatars[i] = "Аватар - " + (i + 1);
        }
    }

    //обработчи кнопки Выбрать Аватар
    public void onClickAddAvatar(View v) {
        if (status.equalsIgnoreCase("REGEN")) {
            Intent intent = new Intent();
            intent.putExtra("avatar", fileName);
            setResult(RESULT_OK, intent);
            finish();
        }
        if (status.equalsIgnoreCase("CHANGE")){
            if (gold >= 30){ //если хватает денег для изменение
                builder = new AlertDialog.Builder(this);
                dialogMessage = "Стоимость изменение аватара - 30 G. Вы согласны?";
                builder.setMessage(dialogMessage); // сообщение
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка Да
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog = new ProgressDialog(AvatarActivity.this);
                        progressDialog.setMessage("Ожидайте, применяю изменения");
                        progressDialog.show();
                        conf.setAVATAR(fileName);
                        new ChangeAvatar(h, conf);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // Кнопка НЕТ
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                }).create().show();
            } else {
                dialogMessage = "Стоимость изменение аватара - 30 G";
                dialog = DialogScreen.getDialog(AvatarActivity.this, DialogScreen.DIALOG_OK, conf, dialogMessage);
                dialog.show();
            }
        }
    }
}
