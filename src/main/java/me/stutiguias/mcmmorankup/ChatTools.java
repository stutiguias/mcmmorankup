package me.stutiguias.mcmmorankup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;

/* zrocwebs: string, array and formatting utilities */
public class ChatTools
{
   
  public static final int lineLength = 54;

  
  public static String getAltColor(String c) {      
	return ChatColor.translateAlternateColorCodes('&', c);	  
  }  
  
  public static String formatTitle(String title, String titleLine, String hlColor, String hColor, Boolean hBold, String haColor, Boolean haBold) { 	
	String line = getAltColor(hlColor) + titleLine;	
	int pivot = (line.length() / 2);
	String center = getAltColor(haColor) + (haBold ? ChatColor.BOLD : ChatColor.RESET) + ".[ " +
	                getAltColor(hColor) + (hBold ? ChatColor.BOLD : ChatColor.RESET) + title +
	                getAltColor(haColor) + (haBold ? ChatColor.BOLD : ChatColor.RESET) + " ]." +
	                getAltColor(hlColor);
	String out = line.substring(0, pivot - center.length() / 2);
	out = out + center + line.substring(pivot + center.length() / 2);
	
	return out;
  }
  
  public static String parseSingleLineString(String str) {
	  return str.replaceAll("&", "§");
  }

  public static String stripColour(String s) {
	String out = "";
	for (int i = 0; i < s.length() - 1; i++) {
	  String c = s.substring(i, i + 1);
	  if (c.equals("§"))
	    i++;
	  else
	    out = out + c;
	}
	return out;
  }

  public static String formatCommand( String requirement, String command, String subCommand, String help, int colored) {
	    String out = "  ";
	    if (requirement.length() > 0)
	      out = out + ChatColor.DARK_RED +"" + requirement + ": ";
	    out = out + ChatColor.DARK_GREEN +"" + command;
	    if (subCommand.length() > 0)
	      out = out + ChatColor.GREEN +"" + subCommand;
	    if (help.length() > 0)
	      if(colored!=1) {
	    	  out = out + ChatColor.WHITE +" : " + help;
	      } else {
	    	  out = out + " : " + help;
	      }
	    return out;
  }
	  
  public static String formatTitleSpc(String title, String sourceLine, boolean spc, String spacer) {    
	String line ="";
	if(spc) {
		int l = sourceLine.length();
	
		String padder="";
		for (int i=0; i<l; i++){
			padder = padder + spacer;
		}
		line = padder;
	} else {
		line = sourceLine;
	}	
	
	int pivot = (line.length() / 2);
	String center = title;
	String out = line.substring(0, pivot - center.length() / 2);
	out = out + center + line.substring(pivot + center.length() / 2);
	
	return out;
  }
  
  
  public static List<String> listArr(Object[] args)
  {
    return list(Arrays.asList(args));
  }

  public static List<String> listArr(Object[] args, String prefix) {
    return list(Arrays.asList(args), prefix);
  }

  public static List<String> list(List<Object> args)
  {
    return list(args, "");
  }

  public static List<String> list(List<Object> args, String prefix)
  {
    if (args.size() > 0) {
      String line = "";
      for (int i = 0; i < args.size() - 1; i++)
        line = line + args.get(i) + ", ";
      line = line + args.get(args.size() - 1).toString();

      return color(prefix + line);
    }

    return new ArrayList<String>();
  }

  public static List<String> wordWrap(String[] tokens) {
    List<String> out = new ArrayList<String>();
    out.add("");

    String[] arrayOfString = tokens; int j = tokens.length; for (int i = 0; i < j; i++) { String s = arrayOfString[i];
      if (stripColour((String)out.get(out.size() - 1)).length() + stripColour(s).length() + 1 > 54)
        out.add("");
      out.set(out.size() - 1, (String)out.get(out.size() - 1) + s + " ");
    }

    return out;
  }

  public static List<String> color(String line) {
    List<String> out = wordWrap(line.split(" "));

    String c = "f";
    for (int i = 0; i < out.size(); i++) {
      if ((!((String)out.get(i)).startsWith("§")) && (!c.equalsIgnoreCase("f"))) {
        out.set(i, "§" + c + (String)out.get(i));
      }
      for (int index = 0; index < 54; index++)
        try {
          if (((String)out.get(i)).substring(index, index + 1).equalsIgnoreCase("§"))
            c = ((String)out.get(i)).substring(index + 1, index + 2);
        }
        catch (Exception localException) {
        }
    }
    return out;
  }


  /*
  public static String formatLine(int titleLineLen, int lineDataLen, String lineData) {    
	  //123456789012345678901234567890123456789012345678901234567890123456 		 = 66 smaller chars.
	  //╚════════════════════════════════════════════════════════╝
	  String out = "";
	  for (int i=0; i < (titleLineLen - lineDataLen); i++) {
		  //System.out.println("lineLen-10: " + ((titleLineLen - lineDataLen) - 10) + "line= " + lineData);
		  out = out + " ";		  
	  }
	  
	  return lineData + out + "╣";
  }
  */
  
}
