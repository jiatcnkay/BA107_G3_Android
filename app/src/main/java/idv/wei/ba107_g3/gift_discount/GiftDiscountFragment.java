package idv.wei.ba107_g3.gift_discount;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.activity.Gift;
import idv.wei.ba107_g3.gift.GiftVO;
import idv.wei.ba107_g3.main.Util;

import static idv.wei.ba107_g3.gift.GiftFragment.recyclerView_gift;
import static idv.wei.ba107_g3.main.Util.CART;

public class GiftDiscountFragment extends Fragment{
    public static RecyclerView recyclerView_giftdiscount;
    private List<GiftDiscountVO> giftDlist = new ArrayList<>();
    private List<GiftVO> giftD = new ArrayList<>();
    private GiftDAdapter giftDAdapter;
    private TextView count_giftd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_giftdiscount, null);
        count_giftd = view.findViewById(R.id.count_giftd);
        recyclerView_giftdiscount = view.findViewById(R.id.recyclerview_giftdiscount);
        recyclerView_giftdiscount.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GiftDlist GiftDlist = new GiftDlist();
        GiftDlist.execute();
    }

    private class GiftDlist extends AsyncTask<String,Void,List<GiftDiscountVO>> {

        @Override
        protected List<GiftDiscountVO> doInBackground(String... strings) {
            GiftDiscountDAO_interface dao = new GiftDiscountDAO();
            giftDlist = dao.getAll();
            return giftDlist;
        }

        @Override
        protected void onPostExecute(List<GiftDiscountVO> giftDiscountVOS) {
            super.onPostExecute(giftDiscountVOS);
            count_giftd.setText("共" + String.valueOf(giftDiscountVOS.size()) + "筆商品");
            SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE, Context.MODE_PRIVATE);
            pref.edit().putString("giftDlist",new Gson().toJson(giftDiscountVOS)).apply();
            GiftD giftD = new GiftD();
            giftD.execute();
        }
    }

    private class GiftD extends AsyncTask<String, Void, List<GiftVO>> {

        @Override
        protected List<GiftVO> doInBackground(String... strings) {
            GiftDiscountDAO_interface dao = new GiftDiscountDAO();
            giftD = dao.getGiftD();
            return giftD;
        }

        @Override
        protected void onPostExecute(List<GiftVO> gifts) {
            super.onPostExecute(gifts);
            giftD = gifts;
            recyclerView_giftdiscount.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
            giftDAdapter = new GiftDAdapter();
            recyclerView_giftdiscount.setAdapter(giftDAdapter);
            recyclerView_gift.getAdapter().notifyDataSetChanged();
        }
    }

    private class GiftDAdapter extends RecyclerView.Adapter<GiftDAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView giftd_pic,like,cancellike;
            private TextView giftd_name,giftd_price,giftd_amount,giftd_oldprice,giftd_endtime;
            private Button btnadd,giftd_percent;
            private CardView cardview_giftdiscount;

            public ViewHolder(View itemView) {
                super(itemView);
                giftd_pic = itemView.findViewById(R.id.giftd_pic);
                like = itemView.findViewById(R.id.like);
                cancellike = itemView.findViewById(R.id.cancellike);
                giftd_name = itemView.findViewById(R.id.giftd_name);
                giftd_price = itemView.findViewById(R.id.giftd_price);
                giftd_amount = itemView.findViewById(R.id.giftd_amount);
                giftd_percent = itemView.findViewById(R.id.giftd_percent);
                giftd_oldprice = itemView.findViewById(R.id.giftd_oldprice);
                giftd_endtime = itemView.findViewById(R.id.giftd_endtime);
                btnadd = itemView.findViewById(R.id.btnadd);
                cardview_giftdiscount = itemView.findViewById(R.id.cardview_giftdiscount);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(getContext()).inflate(R.layout.cardview_giftdiscount,parent,false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
            SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE, Context.MODE_PRIVATE);
            String json = pref.getString("giftDlist", "");
            giftDlist = new Gson().fromJson(json.toString(), new TypeToken<List<GiftDiscountVO>>() {
            }.getType());

            final GiftVO giftVO = giftD.get(position);
            final GiftDiscountVO giftDiscountVO = giftDlist.get(position);
            //設定折價數
            Double percent = giftDiscountVO.getGiftd_percent() * 100;
            if(percent%10==0) {
                int intpercent = (int)(giftDiscountVO.getGiftd_percent() * 10);
                viewHolder.giftd_percent.setText(String.valueOf(intpercent)+"折");
            }else {
                percent/=10;
                viewHolder.giftd_percent.setText(String.valueOf(percent)+"折");
            }
            //設定價格
            int price = giftVO.getGift_price();
            viewHolder.giftd_oldprice.setText("$"+String.valueOf(price).toString());
            viewHolder.giftd_oldprice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            price = (int) (price * (percent/100));
            viewHolder.giftd_price.setText("$"+String.valueOf(price));
            //設定名字及數量
            viewHolder.giftd_name.setText(giftVO.getGift_name());
            viewHolder.giftd_amount.setText(giftDiscountVO.getGiftd_amount().toString());
            //設定圖片
            byte[] gift_pic = giftVO.getGift_pic();
            Bitmap bitmap = BitmapFactory.decodeByteArray(gift_pic,0,gift_pic.length);
            viewHolder.giftd_pic.setImageBitmap(bitmap);
            //設定倒數器
            new CountDownTimer(giftDiscountVO.getGiftd_end().getTime()-System.currentTimeMillis(), 1000) {
                public void onTick(long mss) {
                    long days = (mss / 1000) / 60 / 60 / 24;
                    long hours = ((mss / 1000) / 60 / 60) % 24;
                    long minutes = ((mss / 1000) / 60) % 60;
                    long seconds = (mss / 1000) % 60;
                    viewHolder.giftd_endtime.setText("剩餘"+days+"天"+hours+"小時"+minutes+"分"+seconds+"秒");
                }
                public void onFinish() {
                    Util.showMessage(getContext(),giftVO.getGift_name()+" 限時優惠結束");
                    GiftDlist GiftDlist = new GiftDlist();
                    GiftDlist.execute();
                    recyclerView_gift.getAdapter().notifyDataSetChanged();
                }
            }.start();

            //設定加入購物車動作
            viewHolder.btnadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //判斷如果為限時商品但數量為0的話，不能加入購物車
                    for (int i = 0; i < giftDlist.size(); i++) {
                        if (giftVO.getGift_no().equals(giftDlist.get(i).getGift_no()) && giftDlist.get(i).getGiftd_amount()==0) {
                            Util.showMessage(getContext(),"數量為0不能加入購物車");
                            return;
                        }
                    }
                    int index = CART.indexOf(giftVO);
                    //找不到為-1表示第一次加入
                    if (index == -1) {
                        giftVO.setGift_buy_qty(1);
                        CART.add(giftVO);
                        Gift.count_cart.setVisibility(View.VISIBLE);
                        Gift.count_cart.setText(String.valueOf(++Util.count));
                    } else {
                        GiftVO orderGift = CART.get(index);
                        for (int i = 0; i < giftDlist.size(); i++) {
                            if (orderGift.getGift_no().equals(giftDlist.get(i).getGift_no())) {
                                if((orderGift.getGift_buy_qty() + 1)>giftDlist.get(i).getGiftd_amount()){
                                    orderGift.setGift_buy_qty(orderGift.getGift_buy_qty() - 1);
                                    Util.showMessage(getContext(),getString(R.string.over_amount));
                                    break;
                                }
                            }
                        }
                        orderGift.setGift_buy_qty(orderGift.getGift_buy_qty() + 1);
                    }
                    String text = "";
                    for (GiftVO orderGift : CART) {
                        text += "\n- " + orderGift.getGift_name() + " x "
                                + orderGift.getGift_buy_qty();
                    }
                    String message = getString(R.string.current_buy) + text;
                    new AlertDialog.Builder(getContext())
                            .setIcon(R.drawable.cart)
                            .setMessage(message)
                            .setPositiveButton(getString(R.string.confirm),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return giftD.size();
        }
    }

}
