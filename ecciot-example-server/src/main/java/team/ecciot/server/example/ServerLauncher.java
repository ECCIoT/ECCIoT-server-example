package team.ecciot.server.example;

import org.apache.log4j.BasicConfigurator;

import team.ecciot.server.example.comm.CommClient;

/**
 * Hello world!
 *
 */
public class ServerLauncher 
{
    public static void main( String[] args ) throws Exception
    {
    	//自动快速地使用缺省Log4j环境
        BasicConfigurator.configure();
        
        CommClient cc = new CommClient();
        cc.connect(Config.RTC_PORT, Config.RTC_ADDRESS);
        
        System.out.println( "Hello World!" );
    }
}
