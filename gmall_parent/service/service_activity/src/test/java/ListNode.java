/**
 * Test
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/4/1 16:31
 **/

public class ListNode {
    int val;
    ListNode next = null;

    ListNode(int val) {
        this.val = val;
    }

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3= new ListNode(3);
        head.next=listNode2;
        listNode2.next=listNode3;
        Solution solution = new Solution();
        ListNode listNode = solution.ReverseList(head);
        while (listNode!=null){
            System.out.println(listNode.val);
            listNode=listNode.next;
        }
    }
}
class Solution {
    public ListNode ReverseList(ListNode head) {
        if(head==null)
            return null;
        ListNode pre=null,cur=head,temp=null;
        while(cur!=null){
            //保存下个节点到临时变量
            temp=cur.next;
            //指向pre
            cur.next=pre;
            //移动 pre, cur的位置
            pre=cur;
            cur=temp;
        }
        return pre;
    }
}
