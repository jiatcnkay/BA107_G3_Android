package idv.wei.ba107_g3.member;

import java.util.List;

public interface MemberDAO_interface {
    Boolean isMember(String account,String password);
    void memberUpdate(MemberVO member);
    MemberVO memberSelect(String account);
    List<MemberVO> getLike(String gender,String county,String emotion,String interest);
    List<MemberVO> getAll();
}
