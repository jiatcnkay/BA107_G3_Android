package idv.wei.ba107_g3.gift_order;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import idv.wei.ba107_g3.gift_discount.GiftDiscountVO;
import idv.wei.ba107_g3.main.Util;

public class GiftOrderDAO implements GiftOrderDAO_interface {
    private static final String TAG = "GiftOrderDAO";

    @Override
    public List<GiftDiscountVO> insert(String jsonGiftOrderVO, String jsonGiftOrderDetailVOList, String jsonGiftReceiveList) {
        String urlString = Util.URL + "GiftOrderServlet";
        DataOutputStream dos = null;
        HttpURLConnection connection = null;
        StringBuilder inStr = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.connect();
            dos = new DataOutputStream(connection.getOutputStream());
            String req = "action=insertGiftOrder&jsonGiftOrderVO="+jsonGiftOrderVO+"&goDetailList="+jsonGiftOrderDetailVOList+"&gReceiveList="+jsonGiftReceiveList;
            dos.writeBytes(req);
            dos.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                inStr = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    inStr.append(line);
                }
                br.close();
            } else {
                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }if(inStr != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<GiftDiscountVO>>(){
            }.getType();
            return gson.fromJson(inStr.toString(),listType);
        }
        return null;
    }
}
