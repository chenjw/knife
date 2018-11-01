package com.chenjw.knife.client.console;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.chenjw.knife.utils.FileHelper;
import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.History;
import jline.SimpleCompletor;

public class JlineCommandConsole extends CommandConsoleTemplate {
  private static final String OUT_PREFIX = "knife>";
  private ConsoleReader reader;
  private boolean needClearConsole = false;
  private File historyFile = FileHelper.createTempFile("knife_history.store");

  public JlineCommandConsole() {
    try {
      
      reader = new ConsoleReader(System.in, new OutputStreamWriter(System.out));
      reader.setUseHistory(true);
      //System.out.println("aaaaa=>"+FileHelper.readFileToString(historyFile, "UTF-8"));
      History history=  new History(historyFile);
      //System.out.println("bbbbb=>"+FileHelper.readFileToString(historyFile, "UTF-8"));
      //System.out.println(history.getHistoryList());
      reader.setHistory(history);
      //System.out.println(history.getHistoryList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.start();
  }

  @Override
  public void close() throws Exception {
    System.in.close();

  }

  @Override
  public String readConsoleLine() {
    String line = null;
    while (true) {
      try {
        line = reader.readLine();

      } catch (Exception e) {
        e.printStackTrace();
      }

      if (line != null) {
        return line;
      }
    }
  }

  @Override
  public int writeConsoleLine(String line) {
    if (needClearConsole) {
      try {
        reader.clearScreen();
        // reader.flushConsole();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      needClearConsole = false;
    }
    String text = OUT_PREFIX + line + "\n";
    try {
      reader.printString(text);
      reader.flushConsole();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // System.out.print(text);
    writeLog(text);
    return text.length();
  }

  @Override
  public void clearConsole() {
    needClearConsole = true;


    // System.out.print(StringHelper.repeat("\b", charNum));
  }



  public void setCompletors(String[]... strs) {
    // 更新
    updateCompletors(strs);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void updateCompletors(String[]... strs) {

    // 清除旧的
    Collection cc = reader.getCompletors();
    if (cc != null) {
      for (Object o : cc.toArray(new Object[cc.size()])) {
        Completor c = (Completor) o;
        reader.removeCompletor(c);
      }
    }

    // 生成新的
    List<Completor> completors = new ArrayList<Completor>();
    if (strs != null) {
      for (String[] comp : strs) {
        completors.add(new SimpleCompletor(comp));
      }
    }
    reader.addCompletor(new ArgumentCompletor(completors));
  }

}
