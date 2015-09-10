package ua.azigar.client.Resources;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import ua.azigar.client.R;

/**
 * Created by Azigar on 05.08.2015.
 * этот класс - кастомный адаптер для горизонтального списка - Галерея
 */
public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    ArrayList<String> objects;
    ImageView imageView;

    /*
    // Массив изображений
    private int[] mImageIds = {
            R.drawable.obj_150_1, R.drawable.obj2, R.drawable.obj3,
            R.drawable.obj4, R.drawable.obj5, R.drawable.obj6,};
            */

    //конструктор
    public ImageAdapter(Context c, ArrayList<String> products) {
        mContext = c;
        objects = products;
        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.MyGallery);
        mGalleryItemBackground = attr.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
        attr.recycle();
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }



    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        imageView = new ImageView(mContext);
        imageView.setImageResource(mContext.getResources().getIdentifier("obj" + objects.get(position), "drawable", mContext.getPackageName()));
        // Позиционирование по центру
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        // Размер по содержимому
        imageView.setLayoutParams(new Gallery.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //imageView.setBackgroundResource(mGalleryItemBackground);
        //imageView.setBackgroundResource(R.drawable.invent5); // мой собственный стиль
        imageView.setPadding(30, 2, 30, 2); /* NEW */
        return imageView;
    }
}
