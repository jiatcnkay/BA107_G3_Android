package idv.wei.ba107_g3.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.Toast;

public class Util {
    public static String URL = "http://10.0.2.2:8081/BA107_G3/";
    //public static String URL = "http://192.168.197.35:8081/BA107_G3/";
    public final static String PREF_FILE = "preference";

    public static void showMessage(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap, float roundPx)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getWidth(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getWidth());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static String getAge(String memAge){
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy");
        String formatDate = df.format(new java.util.Date());
        Integer year = Integer.parseInt(formatDate);
        int age = Integer.parseInt(memAge.substring(0,4));
        return String.valueOf(year-age);
    }
}
