package org.apache.commons.logging;
public class LogFactory {
 static final boolean ON = System.getenv("BALANA_LOG") != null;
 public static Log getLog(Class<?> c){ return getLog(c.getName()); }
 public static Log getLog(String n){ return new Log(){
  void p(String l,Object m,Throwable t){ if(ON){System.err.println("[balana "+l+"] "+n+": "+m); if(t!=null)t.printStackTrace();} }
  public boolean isDebugEnabled(){return false;} public boolean isErrorEnabled(){return ON;} public boolean isInfoEnabled(){return false;}
  public boolean isWarnEnabled(){return ON;} public boolean isTraceEnabled(){return false;} public boolean isFatalEnabled(){return ON;}
  public void debug(Object m){} public void debug(Object m,Throwable t){} public void info(Object m){} public void info(Object m,Throwable t){}
  public void trace(Object m){} public void trace(Object m,Throwable t){}
  public void error(Object m){p("E",m,null);} public void error(Object m,Throwable t){p("E",m,t);}
  public void warn(Object m){p("W",m,null);} public void warn(Object m,Throwable t){p("W",m,t);}
  public void fatal(Object m){p("F",m,null);} public void fatal(Object m,Throwable t){p("F",m,t);} }; }
}
