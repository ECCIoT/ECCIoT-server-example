package team.ecciot.server.example.comm;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;
import team.ecciot.lib.args.builder.CmdBuilder;
import team.ecciot.lib.args.callback.IDevice2ServerEventCallback;
import team.ecciot.lib.args.callback.IRtc2ServerEventCallback;
import team.ecciot.lib.args.callback.ITerminal2ServerEventCallback;
import team.ecciot.lib.args.model.impl.AlarmArgs;
import team.ecciot.lib.args.model.impl.BindDeviceArgs;
import team.ecciot.lib.args.model.impl.CheckDeviceIdentityArgs;
import team.ecciot.lib.args.model.impl.CheckTerminalIdentityArgs;
import team.ecciot.lib.args.model.impl.CloseDeviceArgs;
import team.ecciot.lib.args.model.impl.CommunicationOutageArgs;
import team.ecciot.lib.args.model.impl.ControlDeviceArgs;
import team.ecciot.lib.args.model.impl.DeviceStateChangedArgs;
import team.ecciot.lib.args.model.impl.RequestRefreshArgs;
import team.ecciot.lib.args.model.impl.ReturnDeviceQueryResultArgs;
import team.ecciot.lib.args.model.impl.ReturnTerminalQueryResultArgs;
import team.ecciot.lib.args.model.impl.TerminalStateChangedArgs;
import team.ecciot.lib.args.model.impl.UpdateItemsDataArgs;
import team.ecciot.server.example.manager.Group;
import team.ecciot.server.example.manager.GroupManager;
import team.ecciot.server.example.model.DeviceModel;
import team.ecciot.server.example.model.TerminalModel;
import team.ecciot.server.example.utils.ChannelUtils;

public class ParserCallbackHandler implements IRtc2ServerEventCallback, IDevice2ServerEventCallback, ITerminal2ServerEventCallback {

	public static Logger LOGGER = LogManager.getLogger(ParserCallbackHandler.class);
	
	// 与RTC通信的信道
	private Channel channelRtc;
		
	public ParserCallbackHandler(Channel channelRtc) {
		this.channelRtc = channelRtc;
	}
	
	@Override
	public void InvalidActionInstruction(String action, String content) {
	}

	@Override
	public void Terminal_ControlDevice(ControlDeviceArgs args) {
		LOGGER.info("终端控制设备命令。args:" + args);
		// 这里有安全隐患，需要获取发送方的信息验证权限（是否在同一个组内）
		// 命令中附带的token可以在rtc中被验证，但是这样无法弥补自定义命令的安全性问题
		// 解决方法应该是在rtc转发数据时进行包装

		// 不安全：进行直接转发
		String itemId = args.getItemID();
		JSONObject json = CmdBuilder.build(args, "device", itemId);
		ChannelUtils.sendMessage(channelRtc, json.toJSONString());
	}

	@Override
	public void Terminal_BindDevice(BindDeviceArgs args) {
		LOGGER.info("终端绑定设备命令。args:" + args);
	}

	@Override
	public void Device_Alarm(AlarmArgs args) {
		LOGGER.info("设备警报命令。args:" + args);
	}

	@Override
	public void RTC_DeviceStateChanged(DeviceStateChangedArgs args) {
		LOGGER.info("RTC连接设备状态发生改变。args:" + args);
		if (args.isState()) {
			/* 新设备上线 */
			// 获取设备属性
			CheckDeviceIdentityArgs cdia = args.getDeviceInfo();
			// 通过设备的itemid查找设备对应的GroupId
			String groupId = GroupManager.getInstance().getDevice4GroupId().get(cdia.getItemID());
			if (groupId == null) {
				LOGGER.info("新设备尚未注册，拒绝通信。itmeId:" + cdia.getItemID());
				// 构建设备状态控制指令
				CloseDeviceArgs cda = new CloseDeviceArgs();
				cda.setItemID(cdia.getItemID());
				cda.setState(false);
				JSONObject json = CmdBuilder.build(cda);
				ChannelUtils.sendMessage(channelRtc, json.toJSONString());
				return;
			}
			// 通过groupId获取Group
			Group group = GroupManager.getInstance().getGroupById(groupId);
			// 创建信息的设备模型
			DeviceModel dm = new DeviceModel();
			dm.setItemID(cdia.getItemID());
			dm.setModel(cdia.getModel());
			dm.setVersion(cdia.getVersion());
			dm.setData(new JSONObject());
			dm.setState(true);
			// 将设备模型加入group
			group.getDeviceModelList().add(dm);
			LOGGER.info(String.format("用户组[%s] - 设备[%s]已上线。", groupId, cdia.getItemID()));
		} else {
			/* 设备已下线 */
			CheckDeviceIdentityArgs cdia = args.getDeviceInfo();
			// 通过设备的itemid查找设备对应的GroupId
			String groupId = GroupManager.getInstance().getDevice4GroupId().get(cdia.getItemID());
			if (groupId == null) {
				LOGGER.info("未知设备已退出通信。args:" + args);
				return;
			}
			// 通过groupId获取Group
			Group group = GroupManager.getInstance().getGroupById(groupId);
			// 获取设备模型
			DeviceModel dm = group.getDeviceById(cdia.getItemID());
			dm.setState(false);
			LOGGER.info(String.format("用户组[%s] - 设备[%s]已离线。", groupId, cdia.getItemID()));
		}
	}

	@Override
	public void RTC_ReturnDeviceQueryResult(ReturnDeviceQueryResultArgs args) {
		LOGGER.info("RTC返回当前在线设备列表。args:" + args);
	}

	@Override
	public void RTC_ReturnTerminalQueryResult(ReturnTerminalQueryResultArgs args) {
		LOGGER.info("RTC返回当前在线终端列表。args:" + args);
	}

	@Override
	public void RTC_CommunicationOutage(CommunicationOutageArgs args) {
		LOGGER.info("RTC主动中断了通信，信息：" + args.getMessage());
	}

	@Override
	public void RTC_TerminalStateChanged(TerminalStateChangedArgs args) {
		LOGGER.info("RTC连接设备状态发生改变。args:" + args);
		if (args.isState()) {
			/* 新设备上线 */
			// 获取设备属性
			CheckTerminalIdentityArgs ctia = args.getTerminalInfo();
			// 通过设备的itemid查找设备对应的GroupId
			String groupId = GroupManager.getInstance().getTermianl4GroupId().get(ctia.getToken());
			if (groupId == null) {
				LOGGER.info("新终端尚未注册，拒绝通信。itmeId:" + ctia.getToken());
				return;
			}
			// 通过groupId获取Group
			Group group = GroupManager.getInstance().getGroupById(groupId);
			// 创建信息的设备模型
			TerminalModel tm = new TerminalModel();
			tm.setToken(ctia.getToken());
			tm.setVersion(ctia.getVersion());
			tm.setState(true);
			// 将设备模型加入group
			group.getTerminalModelList().add(tm);
			LOGGER.info(String.format("用户组[%s] - 终端[%s]已上线。", groupId, ctia.getToken()));
		} else {
			/* 设备已下线 */
			CheckTerminalIdentityArgs cdia = args.getTerminalInfo();
			// 通过设备的 查找设备对应的GroupId
			String groupId = GroupManager.getInstance().getTermianl4GroupId().get(cdia.getToken());
			if (groupId == null) {
				LOGGER.info("未知终端已退出通信。args:" + args);
				return;
			}
			// 通过groupId获取Group
			Group group = GroupManager.getInstance().getGroupById(groupId);
			// 获取设备模型
			TerminalModel tm = group.getTerminalById(cdia.getToken());
			tm.setState(false);
			LOGGER.info(String.format("用户组[%s] - 终端[%s]已离线。", groupId, cdia.getToken()));
		}
	}

	@Override
	public void Terminal_RequestRefresh(RequestRefreshArgs args) {
		// 通过设备的itemid查找设备对应的GroupId
		String groupId = GroupManager.getInstance().getTermianl4GroupId().get(args.getToken());
		// 通过groupId获取Group
		Group group = GroupManager.getInstance().getGroupById(groupId);
		// 数据有效性检查
		if (group == null)
			return;
		// 获取用户组设备列表
		ArrayList<DeviceModel> list = group.getDeviceModelList();
		// 转存数据至专用的模型
		JSONArray jarr = (JSONArray) JSONArray.toJSON(list);
		// 构建设备状态控制指令
		UpdateItemsDataArgs uida = new UpdateItemsDataArgs();
		uida.setItemsData(jarr);
		uida.setDate(new Date().toString());
		JSONObject json = CmdBuilder.build(uida);
		ChannelUtils.sendMessage(channelRtc, json.toJSONString());
	}

}
