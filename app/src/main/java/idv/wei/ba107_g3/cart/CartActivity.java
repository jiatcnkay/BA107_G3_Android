package idv.wei.ba107_g3.cart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.Iterator;
import java.util.List;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.gift.GiftVO;
import idv.wei.ba107_g3.gift_discount.GiftDiscountVO;
import idv.wei.ba107_g3.main.Util;
import idv.wei.ba107_g3.member.LoginActivity;
import idv.wei.ba107_g3.member.MemberVO;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerview_gift;
    private TextView btn_coupons, total_amount, total_gift_first_money, coupons_money, total_gift_last_money;
    private Button btncheckout;
    private List<GiftDiscountVO> giftDlist;
    private ArrayList<ReceiveVO> receiveList = new ArrayList<>();
    private String mem_no_self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //取得有限時優惠的商品LIST
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, Context.MODE_PRIVATE);
        if (pref.getBoolean("login", false)) {
            MemberVO member = new Gson().fromJson(pref.getString("loginMem", "").toString(), MemberVO.class);
            mem_no_self = member.getMemNo();
        }
        giftDlist = new Gson().fromJson(pref.getString("giftDlist", "").toString(), new TypeToken<List<GiftDiscountVO>>() {
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
        int amount = 0, price = 0, coupons = 0, total = 0;
        for (int i = 0; i < Util.CART.size(); i++) {
            GiftVO giftVO = Util.CART.get(i);
            amount = giftVO.getGift_buy_qty();
            price = (giftVO.getGift_price() * amount);
            coupons = Integer.parseInt(coupons_money.getText().toString());
            //是否為折價商品，如是的話價格折價
            for (int j = 0; j < giftDlist.size(); j++) {
                if (giftVO.getGift_no().equals(giftDlist.get(j).getGift_no())) {
                    price = (int) (price * (giftDlist.get(j).getGiftd_percent()));
                    break;
                }
            }
            total += price;
        }
        total_gift_first_money.setText("$" + String.valueOf(total));
        total_gift_last_money.setText("$" + String.valueOf(total + coupons));
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
            final Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
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
                            if (giftVO.getGift_buy_qty() > giftDlist.get(i).getGiftd_amount()) {
                                Util.showMessage(CartActivity.this, getString(R.string.over_amount));
                                viewHolder.add_amount.setEnabled(false);
                                giftVO.setGift_buy_qty(giftVO.getGift_buy_qty() - 1);
                                viewHolder.gift_amount.setText(giftVO.getGift_buy_qty().toString());
                                int backprice = (int) (giftVO.getGift_price() * giftVO.getGift_buy_qty() * giftDlist.get(i).getGiftd_percent());
                                viewHolder.gift_price.setText("$" + String.valueOf(backprice));
                            } else {
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
                    int amount = 0;
                    if (receiveList.size() != 0) {
                        for (int i = 0 ;i < receiveList.size() ;i++) {
                            if(giftVO.getGift_no().equals(receiveList.get(i).getGift_no()))
                            amount += receiveList.get(i).getGiftr_amount();
                        }
                        if (giftVO.getGift_buy_qty() == amount) {
                            viewHolder.less_amount.setEnabled(false);
                            Util.showMessage(CartActivity.this, "選購數量不得低於贈送數量");
                            return;
                        }
                    }
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

//            //移除商品
//            viewHolder.remove_gift.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String message = "確定要移除此商品嗎?";
//                    new AlertDialog.Builder(CartActivity.this)
//                            .setMessage(message)
//                            .setPositiveButton("確定",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog,
//                                                            int which) {
//                                            Log.e("tag", "receiveList=" + receiveList.size());
//                                            Iterator<ReceiveVO> iterator = receiveList.iterator();
//                                            while(iterator.hasNext()){
//                                                if(giftVO.getGift_no().equals(iterator.next().getGift_no()))
//                                                    iterator.remove();
//                                            }
//                                            Log.e("tag", "receiveList=" + receiveList.size());
//                                            for(ReceiveVO receiveVO : receiveList){
//                                                Log.e("tag", "ReceiveVO=" + receiveVO.getGift_no());
//                                                Log.e("tag", "ReceiveVO=" + receiveVO.getGiftr_amount());
//                                                Log.e("tag", "ReceiveVO=" + receiveVO.getMem_no_self());
//                                                Log.e("tag", "ReceiveVO=" + receiveVO.getMem_no_other());
//                                            }
//                                            Util.CART.remove(giftVO);
//                                            Util.count--;
//                                            total_amount.setText(String.valueOf(Util.count));
//                                            getPrice();
//                                            recyclerview_gift.getAdapter().notifyDataSetChanged();
//
//                                        }
//                                    })
//
//                            .setNegativeButton("返回",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog,
//                                                            int which) {
//                                            dialog.cancel();
//                                        }
//                                    }).setCancelable(false).show();
//                }
//            });

            //增加送禮對象
            viewHolder.btn_add_person.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int amount = 0;
                    if (receiveList.size() != 0) {
                        for (int i = 0; i < receiveList.size(); i++) {
                            if (giftVO.getGift_no().equals(receiveList.get(i).getGift_no())) {
                                amount += receiveList.get(i).getGiftr_amount();
                            }
                        }
                    }
                    //取得送禮名單
                    if (receiveList.size() == 0 || (amount < giftVO.getGift_buy_qty())) {
                        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                        Type listType = new TypeToken<List<MemberVO>>() {
                        }.getType();
                        final List<MemberVO> List = new Gson().fromJson(pref.getString("sendList", "").toString(), listType);
                        final List<String> sendList = new ArrayList<>();
                        if (List != null) {
                            for (MemberVO member : List) {
                                sendList.add(member.getMemName());
                            }
                            //製作下拉式送禮名單
                            final Spinner namespinner = new Spinner(CartActivity.this);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_spinner_item, sendList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            namespinner.setAdapter(adapter);
                            namespinner.setSelection(0, true);

                            //製作下拉式贈送數目
                            String[] number = {"請選擇", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
                            final Spinner numspinner = new Spinner(CartActivity.this);
                            ArrayAdapter<String> numadapter = new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_spinner_item, number);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            numspinner.setAdapter(numadapter);
                            numspinner.setSelection(0, true);
                            numspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    int buy_amount = 0;
                                    if (position != 0) {
                                        for (int i = 0; i < receiveList.size(); i++) {
                                            if (giftVO.getGift_no().equals(receiveList.get(i).getGift_no())) {
                                                buy_amount += receiveList.get(i).getGiftr_amount();
                                            }
                                        }
                                        int amount = Integer.parseInt(parent.getItemAtPosition(position).toString());
                                        if (amount > giftVO.getGift_buy_qty() || (buy_amount + amount) > giftVO.getGift_buy_qty()) {
                                            Util.showMessage(CartActivity.this, "不得超過購買數量");
                                            numspinner.setSelection(0, true);
                                            return;
                                        }
                                    }
                                    if (position != 0 && namespinner.getSelectedItemPosition() != 0) {
                                        Util.showMessage(CartActivity.this, "請勾選左邊空格確認");
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                            //移除按鈕
                            ImageView close = new ImageView(CartActivity.this);
                            close.setImageResource(R.drawable.ic_close);

                            //確認按鈕
                            final ImageView confirm = new ImageView(CartActivity.this);
                            confirm.setBackgroundResource(R.drawable.not_confirm);
                            confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (namespinner.getSelectedItemPosition() == 0 || numspinner.getSelectedItemPosition() == 0) {
                                        Util.showMessage(CartActivity.this, "請完成右邊選項");
                                    } else {
                                        int buy_amount = 0;
                                        for (int i = 0; i < receiveList.size(); i++) {
                                            if (giftVO.getGift_no().equals(receiveList.get(i).getGift_no())) {
                                                buy_amount += receiveList.get(i).getGiftr_amount();
                                            }
                                        }
                                        int amount = Integer.parseInt(numspinner.getSelectedItem().toString());
                                        if (amount > giftVO.getGift_buy_qty() || (buy_amount + amount) > giftVO.getGift_buy_qty()) {
                                            Util.showMessage(CartActivity.this, "不得超過購買數量");
                                            numspinner.setSelection(0, true);
                                            return;
                                        }
                                        confirm.setBackgroundResource(R.drawable.confirm);
                                        namespinner.setEnabled(false);
                                        numspinner.setEnabled(false);
                                        confirm.setEnabled(false);
                                        String mem_no_other = List.get(namespinner.getSelectedItemPosition()).getMemNo();
                                        addReceive(giftVO, mem_no_other, amount);
                                    }
                                }
                            });

                            //把剛剛新增的view放到這個新增的layout
                            final LinearLayout linearLayout = new LinearLayout(CartActivity.this);
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            viewHolder.add_person_layout.addView(linearLayout);
                            linearLayout.addView(confirm);
                            linearLayout.addView(namespinner);
                            linearLayout.addView(numspinner);
                            linearLayout.addView(close);

                            //等layout設置好後，建立移除按鈕監聽器
                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (namespinner.getSelectedItemPosition() != 0 && numspinner.getSelectedItemPosition() != 0) {
                                        int amount = Integer.parseInt(numspinner.getSelectedItem().toString());
                                        String mem_no_other = List.get(namespinner.getSelectedItemPosition()).getMemNo();
                                        lessReceive(giftVO, mem_no_other, amount);
                                    }
                                    viewHolder.less_amount.setEnabled(true);
                                    linearLayout.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            Util.showMessage(CartActivity.this, "請先登入");
                            Intent loginIntent = new Intent(CartActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                        }
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
                                            Log.e("tag", "receiveList=" + receiveList.size());
                                            Iterator<ReceiveVO> iterator = receiveList.iterator();
                                            while(iterator.hasNext()){
                                                if(giftVO.getGift_no().equals(iterator.next().getGift_no()))
                                                    iterator.remove();
                                            }
                                            Log.e("tag", "receiveList=" + receiveList.size());
                                            for(ReceiveVO receiveVO : receiveList){
                                                Log.e("tag", "ReceiveVO=" + receiveVO.getGift_no());
                                                Log.e("tag", "ReceiveVO=" + receiveVO.getGiftr_amount());
                                                Log.e("tag", "ReceiveVO=" + receiveVO.getMem_no_self());
                                                Log.e("tag", "ReceiveVO=" + receiveVO.getMem_no_other());
                                            }
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
        }

        @Override
        public int getItemCount() {
            return Util.CART.size();
        }
    }

    public void addReceive(GiftVO giftVO, String mem_no_other, int amount) {
        ReceiveVO receiveVO = new ReceiveVO();
        receiveVO.setGift_no(giftVO.getGift_no());
        receiveVO.setMem_no_self(mem_no_self);
        receiveVO.setMem_no_other(mem_no_other);
        int index = receiveList.indexOf(receiveVO);
        //找不到為-1表示第一次加入
        if (index == -1) {
            receiveVO.setGiftr_amount(amount);
            receiveList.add(receiveVO);
            Log.e("tag", "ReceiveVO1=" + receiveVO.getGift_no());
            Log.e("tag", "ReceiveVO1=" + receiveVO.getGiftr_amount());
            Log.e("tag", "ReceiveVO1=" + receiveVO.getMem_no_self());
            Log.e("tag", "ReceiveVO1=" + receiveVO.getMem_no_other());
        } else {
            ReceiveVO orderReceive = receiveList.get(index);
            int order_amount = orderReceive.getGiftr_amount();
            orderReceive.setGiftr_amount(order_amount + amount);
            Log.e("tag", "ReceiveVO=" + orderReceive.getGift_no());
            Log.e("tag", "ReceiveVO=" + orderReceive.getGiftr_amount());
            Log.e("tag", "ReceiveVO=" + orderReceive.getMem_no_self());
            Log.e("tag", "ReceiveVO=" + orderReceive.getMem_no_other());
        }
    }

    public void lessReceive(GiftVO giftVO, String mem_no_other, int amount) {
        ReceiveVO receiveVO = new ReceiveVO();
        receiveVO.setGift_no(giftVO.getGift_no());
        receiveVO.setMem_no_self(mem_no_self);
        receiveVO.setMem_no_other(mem_no_other);
        int index = receiveList.indexOf(receiveVO);
        if (index == -1)
            return;
        ReceiveVO orderReceive = receiveList.get(index);
        int order_amount = orderReceive.getGiftr_amount();
        if (order_amount - amount == 0) {
            receiveList.remove(orderReceive);
            return;
        }
        orderReceive.setGiftr_amount(order_amount - amount);
        Log.e("tag", "ReceiveVO=" + orderReceive.getGift_no());
        Log.e("tag", "ReceiveVO=" + orderReceive.getGiftr_amount());
        Log.e("tag", "ReceiveVO=" + orderReceive.getMem_no_self());
        Log.e("tag", "ReceiveVO=" + orderReceive.getMem_no_other());
    }
}
