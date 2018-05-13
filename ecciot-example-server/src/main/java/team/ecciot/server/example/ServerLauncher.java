package team.ecciot.server.example;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import team.ecciot.server.example.comm.CommClient;

/**
 * Hello world!
 *
 */
public class ServerLauncher 
{
	public static Logger LOGGER = LogManager.getLogger(ServerLauncher.class);
	
    public static void main( String[] args ) throws Exception
    {
    	//自动快速地使用缺省Log4j环境
        BasicConfigurator.configure();
        
        LOGGER.info("正在连接服务器……");
        
        CommClient cc = new CommClient();
        cc.connect(Config.RTC_PORT, Config.RTC_ADDRESS);
        
        LOGGER.info("Start.");
    }
}
