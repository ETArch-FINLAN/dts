package br.ufu.facom.network.dts;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import br.ufu.facom.network.dlontology.FinSocket;
import br.ufu.facom.network.dlontology.msg.Message;
import br.ufu.facom.network.dts.bean.core.Entity;
import br.ufu.facom.network.dts.core.message.ServiceMessage;
import br.ufu.facom.network.dts.core.message.ServiceRequest;
import br.ufu.facom.network.dts.core.message.ServiceResponse;
import br.ufu.facom.network.dts.core.message.parser.SerializedParser;
import br.ufu.facom.network.dts.core.message.parser.ServiceMessageParser;
import br.ufu.facom.network.dts.service.ServiceMessageProcessor;
import br.ufu.facom.network.dts.storage.MemoryStorage;
import br.ufu.facom.network.dts.storage.Storage;
import br.ufu.facom.network.dts.topology.Host;
import br.ufu.facom.network.dts.topology.Link;
import br.ufu.facom.network.dts.topology.Port;
import br.ufu.facom.network.dts.topology.Switch;
import br.ufu.facom.network.dts.util.Constants;
import br.ufu.facom.network.dts.util.WorkspaceConfigPusher;
import br.ufu.facom.network.dts.view.DTSViewer;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class DTSAgent extends Entity implements Runnable{
	private static final Logger logger = Logger.getLogger(DTSAgent.class);
	//DL and Net - interfaces
	private FinSocket sock;

	//Mode
	private boolean passive;
	
	//Util
	private WorkspaceConfigPusher workspaceConfigPusher;
	private Storage storage = new MemoryStorage();
	private ServiceMessageParser smParser = new SerializedParser();
	private ServiceMessageProcessor smProcessor = new ServiceMessageProcessor(storage);
	
	//Util
	private boolean running;
	private String dtsTitle="DTS_FACOM";
	
	public DTSAgent(boolean createFrame){
		if(createFrame){
			Thread thread = new Thread(new Runnable() { 
	            public void run() { 
	            	new DTSViewer((MemoryStorage)storage);
	            } 
	        });
			
			thread.setDaemon(true);
			thread.start();
		}
	}
	
	public Message onMessage(Message inMessage, String titleSw, String portId) {
		if(inMessage.getDestination().equals(Constants.dtsWorkspace)){
			ServiceMessage serviceMessage = smParser.marshall(inMessage.getPayload());
			if(serviceMessage != null && serviceMessage instanceof ServiceRequest){
				ServiceResponse response = smProcessor.processServiceMessage(inMessage,(ServiceRequest)serviceMessage,storage.getHostBySwitchAndPort(titleSw, portId));
				if(response != null){
					logger.debug("Service request "+((ServiceRequest)serviceMessage).getService()+" Sucess:"+response.isSucess()+(!response.isSucess()?" Message:"+response.getMessage():""));
					return new Message(dtsTitle,inMessage.getSource(),smParser.unmarshall(response));
				}
			}
		}
		return null;
	}

	/**
	 * Topology Manager 
	 */
	public void registerSwitch(String title) {
		logger.debug("Trying add a new switch: "+title);
		if(!storage.containsSwitch(title))
			storage.addSwitch(title, new Switch(title));
	}

	public void addSwitchPort(String titleSwitch, String portId) {
		logger.debug("Trying add a new port: "+portId+" to switch: "+titleSwitch);
		if(storage.containsSwitch(titleSwitch) && !storage.getSwitch(titleSwitch).containsPort(portId)){
			storage.getSwitch(titleSwitch).addPort(portId, Integer.parseInt(portId));
		}
	}

	public void registerHost(String title) {
		logger.debug("Trying add a new host: "+title);
		if(!storage.containsHost(title))
			storage.addHost(title, new Host(title));
	}

	public void addLink(String titleSwitch1, String portIdSwtich1, String titleSwitch2, String portIdSwtich2) {
		logger.debug("Trying add a new link between the switches: "+titleSwitch1+" and "+titleSwitch2+". Ports "+portIdSwtich1+" and "+portIdSwtich2+" respectivaly.");
		if(storage.containsSwitch(titleSwitch1) && storage.containsSwitch(titleSwitch2)){
			Switch sw1 = storage.getSwitch(titleSwitch1);
			Switch sw2 = storage.getSwitch(titleSwitch2);
			
			if(sw1.containsPort(portIdSwtich1) && sw2.containsPort(portIdSwtich2)){
				Port p1 = sw1.getPort(portIdSwtich1);
				Port p2 = sw2.getPort(portIdSwtich2);
				
				if(p1.getLink() != null){
					if(p2.getLink() != null){
						if(!p1.getLink().equals(p2.getLink())){
							storage.deleteLink(p1.getLink());
							storage.deleteLink(p2.getLink());
							p1.setLink(null);
							p2.setLink(null);
						}else
							return;
					}else{
						storage.deleteLink(p1.getLink());
						p1.setLink(null);
					}
				}else{
					if(p2.getLink() != null){
						storage.deleteLink(p2.getLink());
						p2.setLink(null);
					}
				}
				
				Link link = new Link();
				link.setPeerA(p1);
				link.setPeerB(p2);
				
				p1.setLink(link);
				p2.setLink(link);
				storage.addLink(link);
			}
		}
	}
	
	public void attachHost(String hostTitle, String switchTitle, String switchPortId) {
		logger.debug("Trying to attach the host "+hostTitle+" to switch "+switchTitle+" on port "+switchPortId);
		if(storage.containsHost(hostTitle) && storage.containsSwitch(switchTitle) && storage.getSwitch(switchTitle).containsPort(switchPortId)){
			storage.getHost(hostTitle).addAccessPort(storage.getSwitch(switchTitle).getPort(switchPortId));
			storage.getSwitch(switchTitle).getPort(switchPortId).setAccessHost(storage.getHost(hostTitle));
			storage.addAtachmentHost(storage.getSwitch(switchTitle).getPort(switchPortId),storage.getHost(hostTitle));
		}
	}
	
	public void unregisterSwitch(String title) {
		logger.debug("Trying remove the switch: "+title);
		if(storage.containsSwitch(title))
			storage.deleteSwitch(title);
	}

	public void deleteSwitchPort(String titleSwitch, String portId) {
		logger.debug("Trying remove the port "+portId+" from switch: "+titleSwitch);
		
		if(storage.containsSwitch(titleSwitch) && storage.getSwitch(titleSwitch).containsPort(portId)){
			storage.getSwitch(titleSwitch).removePort(portId);
		}
	}

	public void unregisterHost(String title) {
		logger.debug("Trying remove the host: "+title);
		
		if(storage.containsHost(title))
			storage.deleteHost(title);
	}

	public void deleteLink(String titleSwitch1, String portIdSwtich1, String titleSwitch2, String portIdSwtich2) {
		logger.debug("Trying remove the link between the switches: "+titleSwitch1+" and "+titleSwitch2+". Ports "+portIdSwtich1+" and "+portIdSwtich2+" respectivaly.");
		if(storage.containsSwitch(titleSwitch1)){
			Switch sw = storage.getSwitch(titleSwitch1);
			if(sw.containsPort(portIdSwtich1)){
				Port p = sw.getPort(portIdSwtich1);
				
				if(p.getLink() != null){
					storage.deleteLink(p.getLink());
					p.setLink(null);
				}
			}
		}
		
		if(storage.containsSwitch(titleSwitch2)){
			Switch sw = storage.getSwitch(titleSwitch2);
			if(sw.containsPort(portIdSwtich2)){
				Port p = sw.getPort(portIdSwtich2);
				
				if(p.getLink() != null){
					storage.deleteLink(p.getLink());
					p.setLink(null);
				}
			}
		}
	}
	
	public void detachHost(String hostTitle, String switchTitle, String switchPortId) {
		logger.debug("Trying to detach the host "+hostTitle+" to switch "+switchTitle+" on port "+switchPortId);
		if(storage.containsHost(hostTitle) && storage.containsSwitch(switchTitle) && storage.getSwitch(switchTitle).containsPort(switchPortId)){
			storage.deleteAtachmentHost(storage.getSwitch(switchTitle).getPort(switchPortId), storage.getHost(hostTitle));
		}
	}
	
	/**
	 * Workspace Manager
	 */
	public void registerWorkspaceConfigPusher(WorkspaceConfigPusher workspaceConfigPusher){
		this.workspaceConfigPusher = workspaceConfigPusher;
		this.smProcessor.setWorkspaceConfigPusher(workspaceConfigPusher);
	}

	public WorkspaceConfigPusher getWorkspaceConfigPusher() {
		return workspaceConfigPusher;
	}
	
	
	
	/**
	 * Pure approach
	 */
	private boolean register(){
		//TODO
		
		if(passive){
			//Waits for the registration by another DTS 
		}else{
			//Register itself
		}
		
		return false;
	}
	
	private boolean createWorkspaceDTS(){
		//TODO
		
		if(passive){
			//Join in the current DTS workspace
		}else{
			//Create a workspace called DTS
		}
		
		return false;
	}
	
	public void stop(){
		this.running = false;
	}
	
	
	public boolean init(){
		if(register())  //Register a new DTS (any other DTS can deny this request)
			if(createWorkspaceDTS()) //JOIN in the DTS workspace. If it doesn't exists, just create the workspace as owner
				return true;
			else
				error("Workspace DTS creation failed!");
		else
			error("Register DTSA failed!");
		
		return false;
	}
	
	public boolean finish(){
		//TODO
		return false;
	}
	
	private void error(String string) {
		System.err.println(string);
	}

	public void run(){
		this.running = true;
		ExecutorService execSvc = Executors.newFixedThreadPool( 1 ); //Let's create a queue
		
		while(running){
			final Message msg = sock.read();
			if(msg != null && msg.getPayload().length > 0){
				execSvc.equals(new Runnable(){
					@Override
					public void run() {
						Message outMessage = onMessage(msg, null,null); //TODO We need to think about it at our architecture
						sock.write(outMessage);
					}
					
				});
			}
		}
		
		execSvc.shutdown();
		
		try {
			execSvc.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
