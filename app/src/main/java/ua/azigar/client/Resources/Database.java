package ua.azigar.client.Resources;

/**
 * Created by Azigar on 30.08.2015.
 * класс для создание БД квестов и SQL-запросы
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database {

    final String LOG_TAG = "myLogs";

    private static final String DB_NAME = "gameDB";
    private static final int DB_VERSION = 1;

    private final Context context;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public Database(Context ctx) {
        context = ctx;
    }

    // открыть подключение
    public void open() {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    // вывод в лог данных из курсора
    void logCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) { //стаем на первую запись курсора
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) { //пишу название поля
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "  |  ");
                    }
                    Log.d(LOG_TAG, str);
                } while (c.moveToNext()); //переход
            }
        } else
            Log.d(LOG_TAG, "Курсор пустой");
    }

    //запрос, на к-во активных квестов
    public int getCountActiveQuests() {
        int count = 0;
        Cursor c;
        String sqlQuery = "SELECT COUNT(*) AS kol FROM execute"     //выбираю к-во записей с таблици execute
                + " WHERE executed = 0;";                           //где квесты только взяты на выполнение (активные)
        c = db.rawQuery(sqlQuery, null);
        if (c.moveToFirst()) {  //стаю на первую строку курсора
            count = c.getInt(c.getColumnIndex("kol")); //получаю значения по имени поля
        }
        Log.d(LOG_TAG, "--- запрос, на к-во активных квестов ---");
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
        return count;
    }

    //запрос, на к-во завершенных квестов
    public int getCountPassedQuests() {
        int count = 0;
        Cursor c;
        String sqlQuery = "SELECT COUNT(*) AS kol FROM execute"     //выбираю к-во записей с таблици execute
                + " WHERE executed = 1 AND taken <> 0;";            //где квесты завершенные кроме НУЛЕВОГО квеста
        c = db.rawQuery(sqlQuery, null);
        if (c.moveToFirst()) {  //стаю на первую строку курсора
            count = c.getInt(c.getColumnIndex("kol")); //получаю значения по имени поля
        }
        Log.d(LOG_TAG, "--- запрос, на к-во завершенных квестов ---");
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
        return count;
    }

    //запрос, информация о завершенных квестах
    public Cursor getPassedQuests() {
        Cursor c;
        String sqlQuery = "SELECT q.name as name, nps_give.name_nps as nps_give," +
                            " nps_get.name_nps as nps_get, q.definition, q.repeat"
                + " FROM quests as q"
                + " inner join nps as nps_give"
                + " on q.nps_give = nps_give.nps"
                + " inner join nps as nps_get"
                + " on q.nps_get = nps_get.nps"
                + " WHERE q.quest IN (SELECT taken FROM execute WHERE executed = 1);";     //где id-квестов встречается в execute (взятые квесты) и эти все квесты завершенные
        c = db.rawQuery(sqlQuery, null);
        Log.d(LOG_TAG, "--- запрос, информация о завершенных квестах ---");
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
        return db.rawQuery(sqlQuery, null);
    }

    //запрос, информация о завершенных квестах
    public Cursor getActiveQuests() {
        Cursor c;
        String sqlQuery = "SELECT q.name as name, nps_give.name_nps as nps_give," +
                " nps_get.name_nps as nps_get, q.definition, q.repeat"
                + " FROM quests as q"
                + " inner join nps as nps_give"
                + " on q.nps_give = nps_give.nps"
                + " inner join nps as nps_get"
                + " on q.nps_get = nps_get.nps"
                + " WHERE q.quest IN (SELECT taken FROM execute WHERE executed = 0);";     //где id-квестов встречается в execute (взятые квесты) и эти все квесты не завершенные
        c = db.rawQuery(sqlQuery, null);
        Log.d(LOG_TAG, "--- запрос, информация о активных квестах ---");
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
        return db.rawQuery(sqlQuery, null);
    }

    //запрос, что бы узнать пройден ли определённый квест (0-Нет, 1-Да)
    public int getIsQuest(String quest) {
        int isQuest = 0;
        Cursor c;
        String sqlQuery = "SELECT COUNT(*) AS kol FROM execute"     //выбираю к-во записей с таблици execute
                + " WHERE taken = " + quest                         //где взятый квест равен указаному квесту
                + " AND executed = 1";                              //и он пройден
        c = db.rawQuery(sqlQuery, null);
        if (c.moveToFirst()) {  //стаю на первую строку курсора
            isQuest = c.getInt(c.getColumnIndex("kol")); //получаю значения по имени поля
        }
        Log.d(LOG_TAG, "--- запрос, что бы узнать пройден ли определённый квест (0-Нет, 1-Да) ---");
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
        return isQuest;
    }

    //запрос, что бы узнать Выбраный Квест Новый (0) или уже взят на выполнение (1)
    public int getIsNewQuest(String quest) {
        int isNew = 0;
        Cursor c;
        String sqlQuery = "SELECT COUNT(*) AS kol FROM execute"     //выбираю к-во записей с таблици execute
                + " WHERE taken = " + quest;                        //где взятый квест равен указаному квесту
        c = db.rawQuery(sqlQuery, null);
        if (c.moveToFirst()) {  //стаю на первую строку курсора
            isNew = c.getInt(c.getColumnIndex("kol")); //получаю значения по имени поля
        }
        Log.d(LOG_TAG, "--- запрос, что бы узнать Выбраный Квест Новый (0) или уже взят на выполнение (1) ---");
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
        return isNew;
    }

    // делаем запрос всех данных из таблицы Quests, получаем Cursor и выводим данные в лог
    public void LogQuests() {
        Cursor c;
        Log.d(LOG_TAG, "--- ТАблица Quests ---");
        c = db.query("quests", null, null, null, null, null, null);
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
    }

    // делаем запрос всех данных из таблицы Quests, получаем Cursor и выводим данные в лог
    public void LogNPS() {
        Cursor c;
        Log.d(LOG_TAG, "--- Таблица NPS ---");
        c = db.query("nps", null, null, null, null, null, null);
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
    }

    // делаем запрос всех данных из таблицы Execute, получаем Cursor и выводим данные в лог
    public void LogExecute() {
        Cursor c;
        Log.d(LOG_TAG, "--- ТАблица Execute ---");
        c = db.query("execute", null, null, null, null, null, null);
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
    }

    // закрыть подключение
    public void close() {
        if (dbHelper!=null) dbHelper.close();
    }

    //очистить таблицу Quests
    public void clearQuests() {
        db.delete("quests", null, null);
    }

    //очистить таблицу Execute
    public void clearExecute() {
        db.delete("execute", null, null);
    }

    //очистить таблицу NPS
    public void clearNPS() {
        db.delete("nps", null, null);
    }

    //к-во новых квестов для определённого NPS
    public int getCountNewQuests(String nps, String lvl, String pvp) {
        int count = 0;
        Cursor c;
        String sqlQuery = "SELECT COUNT(*) AS kol FROM quests"              //выбираю к-во записей с таблици quests
            + " WHERE quest NOT IN (SELECT taken FROM execute)"             //где id-квестов не встречается в execute (взятые квесты)
            + " AND isq IN (SELECT taken FROM execute WHERE executed = 1)"  //и в execute пройдены все нужные квесты
            + " AND nps_give = " + nps                                      //и NPS староста поселка Фразен
            + " AND lvl <= " + lvl                                          //и необходимый уровень меньше или равен уровню героя
            + " AND pvp <= " + pvp;                                         //и необходимый ПвП-уровень меньше или равен ПвП-уровню героя
        c = db.rawQuery(sqlQuery, null);
        if (c.moveToFirst()) {  //стаю на первую строку курсора
            count = c.getInt(c.getColumnIndex("kol")); //получаю значения по имени поля
        }
        Log.d(LOG_TAG, "--- к-во новых квестов для определённого NPS ---");
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
        return count;
    }

    //к-во новых квестов и к-во квестов, которые надо сдавать для определённого NPS
    public int getCountQuests(String nps, String lvl, String pvp) {
        int count = 0;
        Cursor c;
        String sqlQuery = "SELECT COUNT(*) AS kol FROM quests"                  //выбираю к-во записей с таблици quests
            + " WHERE (quest NOT IN (SELECT taken FROM execute)"                //где id-квестов не встречается в execute (взятые квесты)
            + " AND isq IN (SELECT taken FROM execute WHERE executed = 1)"      //и в execute пройдены все нужные квесты
            + " AND nps_give = " + nps                                          //и указаный NPS
            + " AND lvl <= " + lvl                                              //и необходимый уровень меньше или равен уровню героя
            + " AND pvp <= " + pvp + ")"                                        //и необходимый ПвП-уровень меньше или равен ПвП-уровню героя
            + " OR (quest IN (SELECT taken FROM execute WHERE executed = 0)"    //або все квесты, которые взяты на выполнение
            + " AND nps_get = " + nps + ")";                                    //и их нужно сдавать указаному NPS
        c = db.rawQuery(sqlQuery, null);
        if (c.moveToFirst()) { //стаю на первую строку курсора
            count = c.getInt(c.getColumnIndex("kol")); // получаю значения по имени поля
        }
        Log.d(LOG_TAG, "--- к-во новых квестов и к-во квестов, которые надо сдавать для определённого NPS ---");
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
        return count;
    }

    //вывожу в курсор все возможные новые и взятые квесты для определённого NPS
    public Cursor getCursorQuests(String nps, String lvl, String pvp) {
        Cursor c;
        String sqlQuery = "SELECT * FROM quests"                                    //выбираю к-во записей с таблици quests
                + " WHERE (quest NOT IN (SELECT taken FROM execute)"                //где id-квестов не встречается в execute (взятые квесты)
                + " AND isq IN (SELECT taken FROM execute WHERE executed = 1)"      //и в execute пройдены все нужные квесты
                + " AND nps_give = " + nps                                          //и указаный NPS
                + " AND lvl <= " + lvl                                              //и необходимый уровень меньше или равен уровню героя
                + " AND pvp <= " + pvp + ")"                                        //и необходимый ПвП-уровень меньше или равен ПвП-уровню героя
                + " OR (quest IN (SELECT taken FROM execute WHERE executed = 0)"    //або все квесты, которые взяты на выполнение
                + " AND nps_get = " + nps + ")";                                    //и их нужно сдавать указаному NPS
        c = db.rawQuery(sqlQuery, null);
        Log.d(LOG_TAG, "--- вывожу в курсор все возможные новые и взятые квесты для определённого NPS ---");
        logCursor(c);
        c.close();
        Log.d(LOG_TAG, "--- ---");
        return db.rawQuery(sqlQuery, null);
    }

    //заполнение таблици данными
    public void insert(String table, ContentValues cv) {
        db.insert(table, null, cv);
    }



    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {

            // создаем таблицу Quests
            db.execSQL("create table quests ("
                    + "quest integer primary key, "
                    + "name text, "
                    + "isq integer, "
                    + "nps_give integer, "
                    + "nps_get integer, "
                    + "lvl integer, "
                    + "pvp integer, "
                    + "repeat integer, "
                    + "definition text);");

            // создаем таблицу NPS
            db.execSQL("create table nps ("
                    + "nps integer primary key,"
                    + "name_nps text);");

            // создаем таблицу Execute
            db.execSQL("create table execute ("
                    + "taken integer primary key,"
                    + "executed integer);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
