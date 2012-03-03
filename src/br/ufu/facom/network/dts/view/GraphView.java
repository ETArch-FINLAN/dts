package br.ufu.facom.network.dts.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.apache.commons.collections15.Transformer;

import br.ufu.facom.network.dts.bean.Application;
import br.ufu.facom.network.dts.bean.core.Entity;
import br.ufu.facom.network.dts.topology.Host;
import br.ufu.facom.network.dts.topology.Link;
import br.ufu.facom.network.dts.topology.Switch;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class GraphView {
	private SparseMultigraph<Entity,Link> graphJung;
	private AbstractLayout<Entity,Link> layout;
	private VisualizationViewer<Entity,Link> vv;
	//private GraphZoomScrollPane grafoPanel;
	private EditingModalGraphMouse<Entity,Link> graphMouse;
	
	private Mode mode;
	
//	public static void main(String[] args) {
//		GraphView graph = new GraphView();
//		graph.getVv().getRenderContext().setEdgeLabelTransformer(GraphView.linkLabelTransformer(graph.getLayout()));
//		
//		Device devA = new Device("Router", UseType.L2, "a-me-ura-001-teste-01", "123.123.123.123", "Cisco", "","","", "", "");
//		Device devB = new Device("Router", UseType.L2, "a-me-ura-001-teste-02", "123.123.123.123", "Cisco", "","","", "", "");
//		Device devC = new Device("Router", UseType.L2, "a-me-ura-001-teste-03", "123.123.123.123", "Cisco", "","","", "", "");
//		graph.graphJung.addVertex(devA);
//		graph.graphJung.addVertex(devB);
//		
//		for(int i=0; i<4; i++){
//			Link link = new Link();
//			link.setType(LinkType.MULTIPLE);
//			link.setPortA(new Port("port"+i, "port"+i));
//			link.setPortB(new Port("port"+i, "port"+i));
//			graph.graphJung.addEdge(link, devA, devB);
//		}
//		
//		for(int i=5; i<9; i++){
//			Link link = new Link();
//			link.setType(LinkType.MULTIPLE);
//			link.setPortA(new Port("port"+i, "port"+i));
//			link.setPortB(new Port("port"+i, "port"+i));
//			graph.graphJung.addEdge(link, devB, devA);
//		}
//		
//		Link link = new Link();
//		link.setType(LinkType.SIMPLE);
//		link.setPortA(new Port("port32", "port12"));
//		link.setPortB(new Port("port32", "port12"));
//		graph.graphJung.addEdge(link, devB, devC);
//		
//		
//		
//		JDialog dialog = new JDialog();
//		dialog.setContentPane(graph.getGrafoPanel());
//		dialog.pack();
//		dialog.setVisible(true);
//		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//		
//	}
	
	@SuppressWarnings("rawtypes")
	public GraphView() {
		graphJung = new SparseMultigraph<Entity,Link>();
		layout = new KKLayout<Entity,Link>(graphJung);

		vv = new VisualizationViewer<Entity,Link>(layout);
		//grafoPanel = new GraphZoomScrollPane(vv);
		
		vv.getRenderContext().setEdgeLabelTransformer(GraphView.linkLabelTransformer(layout));
		vv.getRenderContext().setVertexLabelTransformer(GraphView.vertexLabelTransformer);
		vv.getRenderContext().setVertexIconTransformer(GraphView.vertexIconTransformer);
		vv.getRenderContext().setVertexShapeTransformer(GraphView.vertexShapeTransformer);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.S);
		vv.setBackground(Color.white);
		
		DefaultModalGraphMouse<Object, Object> gm = new DefaultModalGraphMouse<Object, Object>(){
			@Override
			public void mouseClicked(MouseEvent e) {
				
				super.mouseClicked(e);

				GraphElementAccessor<Entity,Link> pickSupport = vv.getPickSupport();
				//PickedState<String> pickedVertexState = vv.getPickedEdgeState();
				if (pickSupport != null) {
					Entity vertex = pickSupport.getVertex(vv.getGraphLayout(), e.getX(), e.getY());
					if(vertex == null){
						Link link = pickSupport.getEdge(vv.getGraphLayout(), e.getX(), e.getY());
						if (link == null){
							vv.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
							setMode(ModalGraphMouse.Mode.TRANSFORMING);
							return;
						}
					}
					vv.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					setMode(ModalGraphMouse.Mode.PICKING);
				}
			}
			/* (non-Javadoc)
			 * @see edu.uci.ics.jung.visualization.control.PluggableGraphMouse#mouseReleased(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				GraphElementAccessor<Entity,Link> pickSupport = vv.getPickSupport();
				if (pickSupport != null) {
					if(pickSupport.getVertex(vv.getGraphLayout(), e.getX(), e.getY()) == null){
						vv.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
						setMode(ModalGraphMouse.Mode.TRANSFORMING);
						return;
					}
					vv.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					setMode(ModalGraphMouse.Mode.PICKING);
				}
			}
		};
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm);
		vv.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		vv.setVisible(true);
	}

	// GET's and SET's
	public SparseMultigraph<Entity,Link> getGraphJung() {
		return graphJung;
	}

	public void setGraphJung(SparseMultigraph<Entity,Link> graphJung) {
		this.graphJung = graphJung;
	}

	public AbstractLayout<Entity,Link> getLayout() {
		return layout;
	}

	public void setLayout(AbstractLayout<Entity,Link> layout) {
		this.layout = layout;
	}

	public VisualizationViewer<Entity,Link> getVv() {
		return vv;
	}

	public void setVv(VisualizationViewer<Entity,Link> vv) {
		this.vv = vv;
	}

//	public GraphZoomScrollPane getGrafoPanel() {
//		return grafoPanel;
//	}
//
//	public void setGrafoPanel(GraphZoomScrollPane grafoPanel) {
//		this.grafoPanel = grafoPanel;
//	}

	public EditingModalGraphMouse<Entity,Link> getGraphMouse() {
		return graphMouse;
	}

	public void setGraphMouse(EditingModalGraphMouse<Entity,Link> graphMouse) {
		this.graphMouse = graphMouse;
	}

	public void writeImageToFile(File file, Dimension dim, String fileType) {
		
		BufferedImage bi = new BufferedImage((int)dim.getWidth(), (int)dim.getHeight(),	BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bi.createGraphics();
		
		vv.setSize((int)dim.getWidth(), (int)dim.getHeight());
		vv.paint(graphics);
		graphics.dispose();

		try {  			   
			ImageIO.write(bi, fileType, file);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Erro ao salvar arquivo\n"+e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	public Mode getMode() {
		return mode;
	}

	public static Transformer<Entity, Icon> vertexIconTransformer =  new Transformer<Entity, Icon>() {
		private final String path = "/img/";
		private final ImageIcon sw =  new ImageIcon(GraphView.class.getResource(path+"switch.png"));
		private final ImageIcon host =  new ImageIcon(GraphView.class.getResource(path+"host.png"));
		private final ImageIcon app =  new ImageIcon(GraphView.class.getResource(path+"application.png"));
		private final ImageIcon entity =  new ImageIcon(GraphView.class.getResource(path+"entity.png"));
		
		public Icon transform(Entity v) {
			if(v instanceof Switch)
				return sw;
			else if( v instanceof Host)
				return host;
			else if (v instanceof Application)
				return app;
			else
				return entity;
		}
	};

	// Coloca label acima dos links
	public static Transformer<Link, String> linkLabelTransformer(final AbstractLayout<Entity, Link> layout) {
		return new Transformer<Link, String>() {
			public String transform(Link e) {
				if(e.getPeerB() == null)
					if(e.getPeerA() == null)
						return "";
					else
						return e.getPeerA().getName();
				
				Entity devA = e.getPeerA().getParent();
				Entity devB = e.getPeerB().getParent();
				
				String str;
				if (layout.getX(devA) > layout.getX(devB))
					str = e.getPeerB().getName() + " - "	+ e.getPeerA().getName();
				else
					str = e.getPeerA().getName() + " - "	+ e.getPeerB().getName();
				return str;
			}
		};
	}
	
	// Coloca nome abaixo dos vertices
	public static Transformer<Entity, String> vertexLabelTransformer = new Transformer<Entity, String>() {
		public String transform(Entity d) {
			return d.getTitle() != null? d.getTitle() : "";
		}
	};

	// Altera o tamanho do Shape do vértice de acordo com o tamanho da imagem
	public static Transformer<Entity, Shape> vertexShapeTransformer = new Transformer<Entity, Shape>() {
		@Override
		public Shape transform(Entity dev) {
			return new Rectangle2D.Float(-25, -14, 50, 26);
		}
	};
}