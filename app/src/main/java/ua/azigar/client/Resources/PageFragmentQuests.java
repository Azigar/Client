package ua.azigar.client.Resources;


import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Context;
import android.widget.TextView;

import ua.azigar.client.R;

/**
 * Created by Azigar on 08.09.2015.
 * Фрагметы для активики Квестов
 */

public class PageFragmentQuests extends Fragment {

    private static int pageNumber;
    private static Database db;
    private static int count;
    private static String[] name, nps_give, nps_get, definition;
    private static int[] repeat;
    private static Cursor c;
    private static Context context;
    private static AlertDialog dialog;
    private static SocketConfig conf;

    public static PageFragmentQuests newInstance(int page, Database database) {
        db = database;
        PageFragmentQuests fragment = new PageFragmentQuests();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragmentQuests() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //буду ловить номер страници, по которой я буду ориентироватся
        pageNumber = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    //метод для Создания заголовка взависимости от выбраной вкладки
    static String getTitle(Context ctxt, int position) {
        context = ctxt;
        String namePage = "";
        switch (position){
            case 0:
                namePage = "Активные";
                break;

            case 1:
                namePage = "Завершенные";
                break;
        }
        return namePage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //подключаю layout-файл который будет отображать фрагмент
        View result=inflater.inflate(R.layout.fragment_page_quest, container, false);

        //нахожу элементы
        TextView txtView = (TextView)result.findViewById(R.id.txtView);
        ListView listView = (ListView)result.findViewById(R.id.listView);
        //устанавливаем режим выбора пунктов списка
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        db.open(); //поключаю БД
        //меняю взависимости от выбраной страници
        switch (pageNumber){
            //выбрана страница АКТИВНЫЕ
            case 0:
                count = db.getCountActiveQuests(); //к-во активных квестов
                if(count > 0) c = db.getActiveQuests(); //получаю курсор активных квестов
                break;

            //выбрана страница ЗАВЕРШЕННЫЕ
            case 1:
                count = db.getCountPassedQuests(); //к-во завершенных квестов
                if(count > 0) c = db.getPassedQuests(); //получаю курсор завершенных квестов
                break;
        }

        if(count == 0){
            listView.setVisibility(View.GONE); //прячу список
            txtView.setVisibility(View.VISIBLE); //показываю текст о том, что список пуст
        }else{
            listView.setVisibility(View.VISIBLE); //показываю список
            txtView.setVisibility(View.GONE); //прячу текст о том, что список пуст

            //инициализация массивов
            name = new String[count];
            nps_give = new String[count];
            nps_get = new String[count];
            definition = new String[count];
            repeat = new int[count];

            //пишу данные с курсора в массивы
            if (c.moveToFirst()) { //стаю на первую строку курсора
                for (int i = 0; i < count; i++){
                    name[i] = c.getString(c.getColumnIndex("name"));
                    nps_give[i] = c.getString(c.getColumnIndex("nps_give"));
                    nps_get[i] = c.getString(c.getColumnIndex("nps_get"));
                    definition[i] = c.getString(c.getColumnIndex("definition"));
                    repeat[i] = c.getInt(c.getColumnIndex("repeat"));
                    c.moveToNext(); //+1 строка
                }
            }
            c.close();
            db.close();

            //создаю адаптер для списка используя массив
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_item, name);
            //подключаю адаптер в список
            listView.setAdapter(adapter);
            //обработчик нажатия спискаd
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String dialogMessage = "";
                    if(repeat[(int) id] == 1) dialogMessage = "Повторяющийся квест" + "\n";
                    dialogMessage = dialogMessage
                            + "Получил: " + nps_give[(int) id] + "\n"
                            + "Завершить: " + nps_get[(int) id] + "\n"
                            + "УСЛОВИЕ: " + "\n"
                            + definition[(int) id];
                    dialog = DialogScreen.getDialog((Activity) context, DialogScreen.DIALOG_OK, conf, dialogMessage);
                    dialog.show();
                }
            });
        }
        return result;
    }
}
