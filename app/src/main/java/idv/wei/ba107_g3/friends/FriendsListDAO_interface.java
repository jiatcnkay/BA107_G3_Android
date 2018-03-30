package idv.wei.ba107_g3.friends;

import java.util.List;

import idv.wei.ba107_g3.member.MemberVO;

public interface FriendsListDAO_interface {
    public void insert(FriendsListVO frilistVO);
    public void update(FriendsListVO frilistVO);
    public void delete(String mem_no_self,String mem_no_other); //解除好友
    public FriendsListVO findByPrimaryKey(String mem_no_self,String mem_no_other);
    public List<FriendsListVO> getAll();
    public List<MemberVO> getMemberFriends(String mem_no);

}
