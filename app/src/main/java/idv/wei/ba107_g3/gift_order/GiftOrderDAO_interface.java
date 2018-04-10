package idv.wei.ba107_g3.gift_order;

import java.util.List;

import idv.wei.ba107_g3.gift_discount.GiftDiscountVO;

public interface GiftOrderDAO_interface {

    List<GiftDiscountVO> insert(String jsonGiftOrderVO, String jsonGiftOrderDetailVOList, String jsonGiftReceiveList);

}
