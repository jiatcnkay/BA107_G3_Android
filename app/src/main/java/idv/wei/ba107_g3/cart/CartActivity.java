package idv.wei.ba107_g3.cart;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.gift.GiftVO;
import idv.wei.ba107_g3.gift_discount.GiftDiscountVO;
import idv.wei.ba107_g3.main.Util;
import idv.wei.ba107_g3.member.MemberVO;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerview_gift;
    private TextView btn_coupons, total_amount, total_gift_first_money, coupons_money, total_gift_last_money;
    private Button btncheckout;
    private List<GiftDiscountVO> giftDlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //取得有限時優惠的商品LIST
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, Context.MODE_PRIVATE);
        String json = pref.getString("giftDlist", "");
        giftDlist = new Gson().fromJson(json.toString(), new TypeToken<List<GiftDiscountVO>>() {
        }.getType());
        find();
        getPrice();
        recyclerview_gift.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerview_gift.setAdapter(new CartAdapter(this));
    }

    private void find() {
        btn_coupons = findViewById(R.id.btn_coupons);
        total_amount = findViewById(R.id.total_amount);
        total_amount.setText(String.valueOf(Util.count));
        total_gift_first_money = findViewById(R.id.total_gift_first_money);
        coupons_money = findViewById(R.id.coupons_money);
        total_gift_last_money = findViewById(R.id.total_gift_last_money);
        btncheckout = findViewById(R.id.btncheckout);
        recyclerview_gift = findViewById(R.id.recyclerview_gift);
        recyclerview_gift.setHasFixedSize(true);
    }

    public void getPrice() {
        int amount = 0, price = 0, coupons = 0;
        for (int i = 0; i < Util.CART.size(); i++) {
            GiftVO giftVO = Util.CART.get(i);
            amount = giftVO.getGift_buy_qty();
            price += (giftVO.getGift_price() * amount);
            coupons = Integer.parseInt(coupons_money.getText().toString());
            //是否為折價商品，如是的話價格折價
            for (int j = 0; j < giftDlist.size(); j++) {
                if (giftVO.getGift_no().equals(giftDlist.get(j).getGift_no())) {
                    price = (int) (price * giftDlist.get(j).getGiftd_percent());
                }
            }
        }
        total_gift_first_money.setText("$" + String.valueOf(price));
        total_gift_last_money.setText("$" + String.valueOf(price + coupons));
    }

    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
        private Context context;

        public CartAdapter(Context context) {
            this.context = context;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView gift_pic, less_amount, add_amount, remove_gift;
            private TextView gift_name, gift_amount, gift_price;
            private LinearLayout add_person_layout;
            private Button btn_add_person;


            public ViewHolder(View itemView) {
                super(itemView);
                gift_pic = itemView.findViewById(R.id.gift_pic);
                less_amount = itemView.findViewById(R.id.less_amount);
                add_amount = itemView.findViewById(R.id.add_amount);
                remove_gift = itemView.findViewById(R.id.remove_gift);
                gift_name = itemView.findViewById(R.id.gift_name);
                gift_amount = itemView.findViewById(R.id.gift_amount);
                gift_price = itemView.findViewById(R.id.gift_price);
                add_person_layout = itemView.findViewById(R.id.add_person_layout);
                btn_add_person = itemView.findViewById(R.id.btn_add_person);

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(context).inflate(R.layout.cardview_cart, parent, false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
            int price = 0;
            final GiftVO giftVO = Util.CART.get(position);
            byte[] pic = giftVO.getGift_pic();
            Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
            viewHolder.gift_pic.setImageBitmap(bitmap);
            viewHolder.gift_name.setText(giftVO.getGift_name());
            viewHolder.gift_amount.setText(giftVO.getGift_buy_qty().toString());

            //設定價格
            price = (giftVO.getGift_price() * giftVO.getGift_buy_qty());
            viewHolder.gift_price.setText("$" + String.valueOf(price));
            //是否為折價商品，如是的話價格折價
            for (int i = 0; i < giftDlist.size(); i++) {
                if (giftVO.getGift_no().equals(giftDlist.get(i).getGift_no())) {
                    price = (int) (price * giftDlist.get(i).getGiftd_percent());
                    viewHolder.gift_price.setText("$" + String.valueOf(price));
                }
            }

            //數量增加會改變價格
            viewHolder.add_amount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int addprice = 0;
                    viewHolder.less_amount.setEnabled(true);
                    giftVO.setGift_buy_qty(giftVO.getGift_buy_qty() + 1);
                    viewHolder.gift_amount.setText(giftVO.getGift_buy_qty().toString());
                    //無限時優惠商品設定
                    addprice = (giftVO.getGift_price() * giftVO.getGift_buy_qty());
                    viewHolder.gift_price.setText("$" + String.valueOf(addprice));
                    //有限時優惠商品設定
                    for (int i = 0; i < giftDlist.size(); i++) {
                        if (giftVO.getGift_no().equals(giftDlist.get(i).getGift_no())) {
                            if(giftVO.getGift_buy_qty()>giftDlist.get(i).getGiftd_amount()){
                                Util.showMessage(CartActivity.this,getString(R.string.over_amount));
                                viewHolder.add_amount.setEnabled(false);
                                giftVO.setGift_buy_qty(giftVO.getGift_buy_qty() - 1);
                                viewHolder.gift_amount.setText(giftVO.getGift_buy_qty().toString());
                                int backprice =(int)(giftVO.getGift_price() * giftVO.getGift_buy_qty() * giftDlist.get(i).getGiftd_percent());
                                viewHolder.gift_price.setText("$" + String.valueOf(backprice));
                            }else {
                                addprice = (int) (addprice * giftDlist.get(i).getGiftd_percent());
                                viewHolder.gift_price.setText("$" + String.valueOf(addprice));
                            }
                        }
                    }
                    getPrice();
                }
            });
            //數量減少會改變價格
            viewHolder.less_amount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.add_amount.setEnabled(true);
                    if (giftVO.getGift_buy_qty() < 2) {
                        viewHolder.less_amount.setEnabled(false);
                        Util.showMessage(CartActivity.this, "選購數量不得小於1");
                    } else {
                        giftVO.setGift_buy_qty(giftVO.getGift_buy_qty() - 1);
                        viewHolder.gift_amount.setText(giftVO.getGift_buy_qty().toString());
                        int lessprice = (giftVO.getGift_price() * giftVO.getGift_buy_qty());
                        viewHolder.gift_price.setText("$" + String.valueOf(lessprice));
                        for (int i = 0; i < giftDlist.size(); i++) {
                            if (giftVO.getGift_no().equals(giftDlist.get(i).getGift_no())) {
                                lessprice = (int) (lessprice * giftDlist.get(i).getGiftd_percent());
                                viewHolder.gift_price.setText("$" + String.valueOf(lessprice));
                            }
                        }
                        getPrice();
                    }
                }
            });
            //移除商品
            viewHolder.remove_gift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = "確定要移除此商品嗎?";
                    new AlertDialog.Builder(CartActivity.this)
                            .setMessage(message)
                            .setPositiveButton("確定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Util.CART.remove(giftVO);
                                            Util.count--;
                                            total_amount.setText(String.valueOf(Util.count));
                                            getPrice();
                                            recyclerview_gift.getAdapter().notifyDataSetChanged();
                                        }
                                    })

                            .setNegativeButton("返回",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.cancel();
                                        }
                                    }).setCancelable(false).show();
                }
            });

            //增加送禮對象
            viewHolder.btn_add_person.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
                    Type listType = new TypeToken<List<MemberVO>>() {
                    }.getType();
                    List<MemberVO> List = new Gson().fromJson(pref.getString("sendList", "").toString(), listType);
                    List<String> sendList = new ArrayList<>();
                    for(MemberVO member : List){
                        sendList.add(member.getMemName());
                    }
                    Spinner spinner = new Spinner(CartActivity.this);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_spinner_item,sendList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    viewHolder.add_person_layout.addView(spinner);
                }
            });
        }

        @Override
        public int getItemCount() {
            return Util.CART.size();
        }
    }
}
