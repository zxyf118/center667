package utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.lang.UUID;

public class StringUtil {
	public static boolean isNotEmpty(String s) {
		return s != null && !s.equals("");
	}
	
	public static boolean isEmpty(String s) {
		return s == null || s.equals("");
	}
	
	public static String getVerificationCode(int len) {
		String vCode = "";
  		Random ran = new Random();
  		for(int i = 0; i < len; i++) {
  			vCode += ran.nextInt(10);
  		}
  		vCode = "000000";	//TODO
  		return vCode;
	}
	
	public static boolean isMobileNumber(String s) {
		 String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0-2]|[5-9]))\\d{8}$";
		 Pattern p = Pattern.compile(regex);
		 Matcher m = p.matcher(s);
		 return m.matches();		 
	}
	
	public static String number2Ip(long number) {
    	long l = number >> 24 & 0xFF;
    	long m = number >> 16 & 0xFF;
    	long n = number >> 8 & 0xFF;
    	long o = number & 0xFF;
    	return l + "." + m + "."  +  n + "." +  o;
    }
    
    public static long ip2Number(String ip) {
    	String[] ipSplit = ip.split("\\.");
    	
    	return Long.parseLong(ipSplit[0]) * (int)Math.pow(256, 3) + 
    			Long.parseLong(ipSplit[1]) * (int)Math.pow(256, 2) + 
    			Long.parseLong(ipSplit[2]) * 256 + 
    			Long.parseLong(ipSplit[3]);
    }    
    
    public static String delBom(String s) {
    	char[] bomChar = s.toCharArray();
    	if(bomChar[0] != 65279) {
    		return s;
    	}
    	char[] noneBomchar = new char[bomChar.length - 1];
    	for (int j = 0; j < noneBomchar .length; j++) {
    	noneBomchar [j] = bomChar[j + 1];
    	}
    	String first = String.valueOf(noneBomchar );
    	return first;
    }
    
    public static boolean isMoney(String s) {
    	String regex = "^\\d+(\\.\\d+)?$";
    	return isMatcher(regex, s);
    }
    
    public static boolean isNumber(String s) {
    	String regex = "^\\+?[1-9][0-9]*$";
    	return isMatcher(regex, s);
    }
    
    public static boolean isMatcher(String regex, String s) {
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(s);
    	return m.find();
    }  
    
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed \\uxxxx encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
    
    public static String waterMarkString(String str, int bIndex, int len) {
    	if(isEmpty(str)) {
    		return str;
    	}
    	char[] cardNoCharArray = str.toCharArray();
		for(int i = bIndex; i < cardNoCharArray.length && i < bIndex + len; i++) {
			cardNoCharArray[i] = '*';
		}
		return String.valueOf(cardNoCharArray);
    }
    
    /**
     * 验证是否只包含数字和字母
     * @param s
     * @return
     */
    public static boolean isNumberOrEnglish(String s) {
    	String regex="^[a-zA-Z0-9]+$";
    	return isMatcher(regex, s);
    }
    
    public static boolean isNumberAndEnglish(String s) {
    	String regex=".*[a-zA-Z].*";
    	boolean a = isMatcher(regex, s);
    	String regex2=".*[0-9].*";
    	boolean b = isMatcher(regex2, s);
    	if (a && b) {
			return true;
		}
    	return false;
    }
    public static boolean isEmail(String s) {
    	String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
    	return isMatcher(regex, s);
    }
    
    public static boolean isIpV4(String s) {
    	String regex = "((1[0-9][0-9]\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)|([1-9][0-9]\\.)|([0-9]\\.)){3}((1[0-9][0-9])|(2[0-4][0-9])|(25[0-5])|([1-9][0-9])|([0-9]))";
    	return isMatcher(regex, s);
    }
    
    public static String delHTMLTag(String htmlStr){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定義script的正則表達式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定義style的正則表達式
        String regEx_html="<[^>]+>"; //定義HTML標籤的正則表達式

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //過濾script標籤

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //過濾style標籤

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //過濾html標籤

        return htmlStr.trim(); //返迴文本字符串
    }
    
    public static String newUserNickname() {
    	Random r= new Random();
    	return "NewUser_" + (char)(r.nextInt(26) + 65) 
    			+ (char)(r.nextInt(26) + 65) 
    			+ (char)(r.nextInt(26) + 97)
    			+ (char)(r.nextInt(26) + 97)
    			+ (char)(r.nextInt(26) + 65)
    			+ (char)(r.nextInt(26) + 65);
    }

	public static String uuid32() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static boolean limitLength(String str,int least,int max) {
		if (str.length() >= least && str.length() <= max) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		//System.out.println(isNotEmpty("1fd"));
		System.out.println(limitLength("131111111", 3, 8));
	}
}
