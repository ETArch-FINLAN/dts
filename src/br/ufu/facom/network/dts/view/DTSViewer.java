package br.ufu.facom.network.dts.view;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.*;
import javax.swing.*;
import java.util.*;

import org.apache.log4j.Logger;

import br.ufu.facom.network.dts.bean.Application;
import br.ufu.facom.network.dts.bean.core.Entity;
import br.ufu.facom.network.dts.bean.core.Workspace;
import br.ufu.facom.network.dts.storage.MemoryStorage;
import br.ufu.facom.network.dts.storage.StorageListener;
import br.ufu.facom.network.dts.topology.Host;
import br.ufu.facom.network.dts.topology.Link;
import br.ufu.facom.network.dts.topology.Port;
import br.ufu.facom.network.dts.topology.Switch;


public class DTSViewer extends JFrame{
	private static final Logger logger = Logger.getLogger(DTSViewer.class);
	private static final long serialVersionUID = 1L;

	private JDesktopPane desktop;
	private JInternalFrame topology;
	private JInternalFrame console;
	private JInternalFrame entities;
	private JInternalFrame workspaces;
	
	private MemoryStorage storage;
	
	static {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DTSViewer(MemoryStorage storage){
		this.storage = storage; 
		createGraphPanel();
		createWorkspacePanel();
		createEntitiesPanel();
		createConsolePanel();

		createDesktopPanel();

		this.setTitle("DTS Viewer");
		this.add(desktop, BorderLayout.CENTER);
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void createEntitiesPanel() {
		entities = new JInternalFrame("Entities", true, false, true, false);
		entities.setLayout(new FlowLayout());

		final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Entities");
		final JTree tree = new JTree(root);
		ImageIcon leafIcon = new ImageIcon(DTSViewer.class.getResource("/img/entIcon.png"));
		if (leafIcon != null) {
		    DefaultTreeCellRenderer renderer = 
		        new DefaultTreeCellRenderer();
		    renderer.setLeafIcon(leafIcon);
		    tree.setCellRenderer(renderer);
		}
		
		storage.addStorageListener(new StorageListener() {
			@Override
			public void onDeleteSwitch(String title) {
			}
			
			@Override
			public void onDeleteLink(Link link) {
			}
			
			@Override
			public void onDeleteHost(String title) {
			}
			
			@Override
			public void onDeleteEntity(String title) {
				for(int i=0; i<root.getChildCount(); i++){
					if(root.getChildAt(i).toString().equals(title)){
						root.remove(i);
						break;
					}
				}
				
				tree.repaint();
				for(int i=0; i< tree.getRowCount(); i++)
					tree.collapseRow(i);
				
			}
			
			@Override
			public void onAddWorkspace(String titleWs, Workspace workspace) {
			}
			
			@Override
			public void onAddSwitch(String title, Switch switch1) {
			}
			
			@Override
			public void onAddLink(Link link) {
			}
			
			@Override
			public void onAddHost(String title, Host host) {
			}
			
			@Override
			public void onAddEntityToHost(Entity entity, Host host) {
			}
			
			@Override
			public void onAddEntity(String title, Entity host) {
				DefaultMutableTreeNode child1 = new DefaultMutableTreeNode(title);
				root.add(child1);
				tree.repaint();
				for(int i=0; i< tree.getRowCount(); i++)
					tree.expandRow(i);
			}
			
			@Override
			public void onAddAtachmentHost(Port port, Host host) {
			}

			@Override
			public void onUpdateWorkspace(Workspace workspace) {
			}

			@Override
			public void onDeleteWorkspace(Workspace workspace) {
			}

			@Override
			public void onDeleteAtachmentHost(Port port, Host host) {
			}
		});
		
		entities.setContentPane(new JScrollPane(tree));
		entities.setVisible(true);
	}

	private void createDesktopPanel() {
		desktop = new JDesktopPane();
		desktop.setDesktopManager(new DefaultDesktopManager() {
			private static final long serialVersionUID = 6601942386838815390L;
			public void dragFrame(JComponent f, int newX, int newY) {};
		});
		
		desktop.add(topology);
		desktop.add(entities);
		desktop.add(workspaces);
		desktop.add(console);
		
		desktop.addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				int w = desktop.getWidth();
				int h = desktop.getHeight();
				
				topology.setBounds(0, 0, w-250, h-200);
				entities.setBounds(w-250, 0, 250, (h-200)/2);
				workspaces.setBounds(w-250, (h-200)/2, 250, (h-200)/2);
				console.setBounds(0, h-200, w, 200);

				topology.setMinimumSize(new Dimension((int) (w*0.3), h-200));
				topology.setMaximumSize(new Dimension(w-250, h-200));
				workspaces.setMinimumSize(new Dimension(250, (h-200)/4));
				workspaces.setMaximumSize(new Dimension((int) (w*0.7), (h-200)/2));
				entities.setMinimumSize(new Dimension(250, (h-200)/4));
				entities.setMaximumSize(new Dimension((int) (w*0.7), (h-200)/2));
				console.setMinimumSize(new Dimension(w, 200));
				console.setMaximumSize(new Dimension(w, (int) (h*0.8)));
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
	}


	private void createConsolePanel() {
		console = new JInternalFrame("Console", true, false, true, false);
		JTextArea area = new JTextArea();
		TextAreaAppender.setTextArea(area);
		console.setContentPane(new JScrollPane(area));
		console.setVisible(true);
	}

	private void createWorkspacePanel() {
		workspaces = new JInternalFrame("Workspaces", true, false, true, false);
		workspaces.setLayout(new FlowLayout());

		final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Workspaces");
		final JTree tree = new JTree(root);
		ImageIcon leafIcon = new ImageIcon(DTSViewer.class.getResource("/img/wsIcon.png"));
		if (leafIcon != null) {
		    DefaultTreeCellRenderer renderer = 
		        new DefaultTreeCellRenderer();
		    renderer.setLeafIcon(leafIcon);
		    tree.setCellRenderer(renderer);
		}
		
		storage.addStorageListener(new StorageListener() {
			@Override
			public void onDeleteSwitch(String title) {
			}
			
			@Override
			public void onDeleteLink(Link link) {
			}
			
			@Override
			public void onDeleteHost(String title) {
			}
			
			@Override
			public void onDeleteEntity(String title) {
			}
			
			@Override
			public void onAddWorkspace(String titleWs, Workspace workspace) {
				DefaultMutableTreeNode child1 = new DefaultMutableTreeNode(titleWs);
				
				for(Entity entity : workspace.getMembers()){
					child1.add(new DefaultMutableTreeNode(entity.getTitle()));
				}
				
				root.add(child1);
				
				tree.repaint();
				for(int i=0; i< tree.getRowCount(); i++)
					tree.expandRow(i);
			}
			
			@Override
			public void onAddSwitch(String title, Switch switch1) {
			}
			
			@Override
			public void onAddLink(Link link) {
			}
			
			@Override
			public void onAddHost(String title, Host host) {
			}
			
			@Override
			public void onAddEntityToHost(Entity entity, Host host) {
			}
			
			@Override
			public void onAddEntity(String title, Entity host) {
			}
			
			@Override
			public void onAddAtachmentHost(Port port, Host host) {
			}

			@Override
			public void onUpdateWorkspace(Workspace workspace) {
				for(int i=0; i<root.getChildCount(); i++){
					if(root.getChildAt(i).toString().equals(workspace.getTitle())){
						root.remove(i);
						
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(workspace.getTitle());
						for(Entity entity : workspace.getMembers()){
							node.add(new DefaultMutableTreeNode(entity.getTitle()));
						}
						
						root.add(node);
						break;
					}
				}
				
				tree.repaint();
				for(int i=0; i< tree.getRowCount(); i++)
					tree.expandRow(i);
			}

			@Override
			public void onDeleteWorkspace(Workspace workspace) {
				for(int i=0; i<root.getChildCount(); i++){
					if(root.getChildAt(i).toString().equals(workspace.getTitle())){
						root.remove(i);
						break;
					}
				}
				
				tree.repaint();
				for(int i=0; i< tree.getRowCount(); i++)
					tree.collapseRow(i);
			}

			@Override
			public void onDeleteAtachmentHost(Port port, Host host) {
			}
		});
		
		workspaces.setContentPane(new JScrollPane(tree));
		workspaces.setVisible(true);
	}

	private void createGraphPanel() {
		topology = new JInternalFrame("Topology", true, false, true, false);
		final GraphView gv = new GraphView();

		storage.addStorageListener(new StorageListener() {
			
			@Override
			public void onDeleteSwitch(String title) {
				gv.getGraphJung().removeVertex(storage.getSwitch(title));
				gv.getVv().repaint();
			}
			
			@Override
			public void onDeleteLink(Link link) {
				gv.getGraphJung().removeEdge(link);
				gv.getVv().repaint();
			}
			
			@Override
			public void onDeleteHost(String title) {
				gv.getGraphJung().removeVertex(storage.getHost(title));
				gv.getVv().repaint();
			}
			
			@Override
			public void onDeleteEntity(String title) {
				gv.getGraphJung().removeVertex(storage.getEntity(title));
				gv.getVv().repaint();
			}
			
			@Override
			public void onAddWorkspace(String titleWs, Workspace workspace) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAddSwitch(String title, Switch switch1) {
				gv.getGraphJung().addVertex(switch1);
				gv.getVv().repaint();
			}
			
			@Override
			public void onAddLink(Link link) {
				gv.getGraphJung().addEdge(link, link.getPeerA().getParent(), link.getPeerB().getParent());
				gv.getVv().repaint();
			}
			
			@Override
			public void onAddHost(String title, Host host) {
				gv.getGraphJung().addVertex(host);
				gv.getVv().repaint();
			}
			
			@Override
			public void onAddEntityToHost(Entity entity, Host host) {
				gv.getGraphJung().addEdge(new Link(), entity, host);
				gv.getVv().repaint();
			}
			
			@Override
			public void onAddEntity(String title, Entity host) {
				gv.getGraphJung().addVertex(storage.getEntity(title));
				gv.getVv().repaint();
			}

			@Override
			public void onAddAtachmentHost(Port port, Host host) {
				Link link = new Link();
				link.setPeerA(port);
				gv.getGraphJung().addEdge(link,port.getParent(), host);
				gv.getVv().repaint();
			}

			@Override
			public void onUpdateWorkspace(Workspace workspace) {
			}

			@Override
			public void onDeleteWorkspace(Workspace workspace) {
			}

			@Override
			public void onDeleteAtachmentHost(Port port, Host host) {
				for(Link link : gv.getGraphJung().findEdgeSet(port.getParent(), host))
					if(link.getPeerA().equals(port)){
						gv.getGraphJung().removeEdge(link);
						gv.getGraphJung().removeVertex(host);
						break;
					}
			}
		});
		
		gv.getVv().setPreferredSize(topology.getSize());
		topology.add(gv.getVv());
		topology.setVisible(true);
	}
	
	public static void main(String[] args) throws InterruptedException {
		MemoryStorage storage = new MemoryStorage();
		
		new DTSViewer(storage);
		
		Thread.sleep(1000);
		storage.addSwitch("1", new Switch("1"));
		logger.info("Info1");
		
		Thread.sleep(1000);
		storage.addSwitch("2", new Switch("2"));
		logger.info("Info2");
		
		Thread.sleep(1000);
		storage.addSwitch("3", new Switch("3"));
		storage.addEntity("Teste",new Host("1"));
		logger.info("Info3");
		
		Thread.sleep(1000);
		storage.addSwitch("4", new Switch("4"));
		logger.info("Info4");
	}
}