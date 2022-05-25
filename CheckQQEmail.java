public class CheckQQEmail {
    public static boolean GetStr(String str)
    {
        String regex="[a-zA-Z0-9_]{2,10}@(qq|QQ|Qq|qQ).com";//QQ号码校验
        return !str.matches(regex);
    }
    public static boolean isNumber(String str)//是不是全数字
    {
        String regex="\\d+";
        return str.matches(regex);
    }
}
