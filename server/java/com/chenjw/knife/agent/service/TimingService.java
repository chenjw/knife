package com.chenjw.knife.agent.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.chenjw.knife.agent.core.Lifecycle;
import com.chenjw.knife.core.Printer;

/**
 * 用于记录和计算方法调用的执行时间
 * 
 * @author chenjw
 *
 */
public class TimingService implements Lifecycle {

  private static final String DEFAULT_NAME = "DEFAULT";
  private static final Printer DEFAULT_PRINTER = new Printer() {
    @Override
    public void info(String str) {
      System.out.println(str);
    }

    @Override
    public void debug(String str) {
      System.out.println(str);
    }
  };
  private final Map<String, Long> RECORDS = new HashMap<String, Long>();
  private final List<TimeSegment> PAUSE_SEGMENT_RECORDS = new ArrayList<TimeSegment>();
  private volatile TimeSegment currentPauseSegment = null;

  public boolean isPauseing() {
    return currentPauseSegment == null;
  }

  /**
   * pause timing
   */
  public void pause() {
    if (currentPauseSegment == null) {
      currentPauseSegment = new TimeSegment();
      currentPauseSegment.setBegin(System.nanoTime());
    }
  }

  /**
   * resume timing
   */
  public void resume() {
    if (currentPauseSegment != null) {
      currentPauseSegment.setEnd(System.nanoTime());
      PAUSE_SEGMENT_RECORDS.add(currentPauseSegment);
      currentPauseSegment = null;
    }
  }

  /**
   * begin timing
   */
  public void begin() {
    begin(DEFAULT_NAME);
  }

  /**
   * 
   * begin timing
   * 
   * @param name
   */
  public void begin(String name) {
    RECORDS.put(name, System.nanoTime());
  }

  /**
   * end timing
   */
  public void end() {
    end(DEFAULT_NAME);
    releasePauseSegmentRecords();
  }

  private void releasePauseSegmentRecords() {
    long time = 0L;
    for (Entry<String, Long> entry : RECORDS.entrySet()) {
      if (time == 0 || entry.getValue() < time) {
        time = entry.getValue();
      }
    }
    if (time != 0) {
      Iterator<TimeSegment> it = PAUSE_SEGMENT_RECORDS.iterator();
      while (it.hasNext()) {
        TimeSegment segment = it.next();
        if (segment.getEnd() <= time) {
          it.remove();
        }
      }
    }
  }

  /**
   * 
   * end timing
   * 
   * @param name
   */
  public void end(String name) {
    RECORDS.remove(name);
  }

  public long getMillisInterval() {
    return getMillisInterval(DEFAULT_NAME);
  }

  public long getMillisInterval(String name) {
    return getNanosInterval(name) / 1000000;
  }

  public long getNanosInterval() {
    return getNanosInterval(DEFAULT_NAME);
  }

  public long getNanosInterval(String name) {
    Long begin = RECORDS.get(name);
    if (begin == null) {
      throw new RuntimeException(name + " must begin first!");
    }
    long end = System.nanoTime();
    // count stop time
    long stopTime = 0;
    for (TimeSegment stopTimeSegment : PAUSE_SEGMENT_RECORDS) {
      stopTime += stopTimeSegment.countSubTime(begin, end);
    }
    if (currentPauseSegment != null) {
      TimeSegment stopTimeSegment = new TimeSegment();
      stopTimeSegment.setBegin(currentPauseSegment.getBegin());
      stopTimeSegment.setEnd(end);
      stopTime += stopTimeSegment.countSubTime(begin, end);
    }
    // System.out.println("stop=" + stopTime / 1000000);
    // System.out.println("time=" + (end - begin) / 1000000);
    // except stop time
    return end - begin - stopTime;
  }

  public void printMillis() {
    printMillis(DEFAULT_PRINTER, DEFAULT_NAME);
  }

  public void printMillis(Printer printer) {
    printMillis(printer, DEFAULT_NAME);
  }

  public void printMillis(String name) {
    printMillis(DEFAULT_PRINTER, name);
  }

  public void printMillis(Printer printer, String name) {
    printer.info(name + " use " + getMillisInterval(name) + " ms");
  }

  public void printNanos() {
    printNanos(DEFAULT_PRINTER, DEFAULT_NAME);
  }

  public void printNanos(String name) {
    printNanos(DEFAULT_PRINTER, name);
  }

  public void printNanos(Printer printer, String name) {
    printer.info(name + " use " + getNanosInterval(name) + " ns");
  }

  private static class TimeSegment {
    private long begin;
    private long end;

    public long countSubTime(long s, long e) {
      long cs = s > begin ? s : begin;
      long ce = e < end ? e : end;
      long i = ce - cs;
      if (i < 0) {
        i = 0;
      }
      return i;
    }

    public long getEnd() {
      return end;
    }

    public long getBegin() {
      return begin;
    }

    public void setBegin(long begin) {
      this.begin = begin;
    }

    public void setEnd(long end) {
      this.end = end;
    }

  }

  @Override
  public void init() {}

  @Override
  public void clear() {
    RECORDS.clear();
    PAUSE_SEGMENT_RECORDS.clear();
    currentPauseSegment = null;
  }

  @Override
  public void close() {

  }
}
