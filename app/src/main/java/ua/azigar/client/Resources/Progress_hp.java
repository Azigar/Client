package ua.azigar.client.Resources;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Azigar on 28.06.2015.
 */
public class Progress_hp extends TextView {

    // Максимальное значение шкалы
    private int mMaxValue = 100;

    // Конструкторы
    public Progress_hp(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public Progress_hp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Progress_hp(Context context) {
        super(context);
    }

    // Установка максимального значения
    public void setMaxValue(int maxValue){
        mMaxValue = maxValue;
    }

    // Установка значения
    public synchronized void setValue(int value) {
        // Установка новой надписи
        this.setText("жизнь " + String.valueOf(value) + "/" + mMaxValue);

        // Drawable, отвечающий за фон
        LayerDrawable background = (LayerDrawable) this.getBackground();

        // Достаём Clip, отвечающий за шкалу, по индексу 1
        ClipDrawable barValue = (ClipDrawable) background.getDrawable(1);

        // Устанавливаем уровень шкалы
        int newClipLevel = (int) (value * 10000 / mMaxValue);
        barValue.setLevel(newClipLevel);

        // Уведомляем об изменении Drawable
        drawableStateChanged();
    }
}
