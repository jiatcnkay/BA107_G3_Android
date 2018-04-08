package idv.wei.ba107_g3.cart;

import java.io.Serializable;

public class ReceiveVO implements Serializable {
    private String gift_no;
    private Integer giftr_amount;
    private String mem_no_self;
    private String mem_no_other;

    @Override
    // 要比對欲加入商品與購物車內商品的gift_no是否相同，true則值相同
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ReceiveVO)) {
            return false;
        }
        return (this.getGift_no().equals(((ReceiveVO)obj).getGift_no()) && this.getMem_no_other().equals(((ReceiveVO)obj).getMem_no_other()));
    }

    public String getGift_no() {
        return gift_no;
    }

    public void setGift_no(String gift_no) {
        this.gift_no = gift_no;
    }

    public Integer getGiftr_amount() {
        return giftr_amount;
    }

    public void setGiftr_amount(Integer giftr_amount) {
        this.giftr_amount = giftr_amount;
    }

    public String getMem_no_self() {
        return mem_no_self;
    }

    public void setMem_no_self(String mem_no_self) {
        this.mem_no_self = mem_no_self;
    }

    public String getMem_no_other() {
        return mem_no_other;
    }

    public void setMem_no_other(String mem_no_other) {
        this.mem_no_other = mem_no_other;
    }
}
