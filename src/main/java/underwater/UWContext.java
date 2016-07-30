package underwater;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import agents.UWAgent;
import agents.UWMobileAgent;
import environment.MapLocationAccessibility;
import environment.UWEnvironment;
import environment.UWPaintNoEntryArea;
import environment.UWPaintNode;
import multiAUVpathplanning.GeneticAlgorithm;
import scenarioHelper.V2CHeatmap;
import yaes.framework.algorithm.search.IHeuristic;
import yaes.framework.simulation.AbstractContext;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.constSensorNetwork;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.agents.SensorRoutingHelper;
import yaes.sensornetwork.energymodel.RapaportCommunicationEnergyModel;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.model.SinkNode;
import yaes.sensornetwork.visualization.SensorNetworkWorldPainter;
import yaes.sensornetwork.visualization.paintEnvironment;
import yaes.ui.text.TextUi;
import yaes.ui.visualization.Visualizer;
import yaes.ui.visualization.painters.IPainter;
import yaes.ui.visualization.painters.IValueToColor;
import yaes.ui.visualization.painters.paintEnvironmentModel;
import yaes.ui.visualization.painters.paintMobileNode;
import yaes.util.ClassResourceHelper;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.environment.LinearColorToValue;
import yaes.world.physical.location.IMoving;
import yaes.world.physical.location.Location;
import yaes.world.physical.map.ArrangementHelper;
import yaes.world.physical.path.AbstractPathCost;
import yaes.world.physical.path.PathLength;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.pathplanning.AStarPP;
import yaes.world.physical.pathplanning.DistanceHeuristic;

/**
 * This method creates the context for the underwater sensor networks
 * 
 * @author Fahad Khan / Saad Khan
 *
 */
public class UWContext extends AbstractContext implements UWConstants, Serializable {
	private static final long serialVersionUID = -839305508680488108L;
	protected static final String SINK_NODE_NAME = "Robot";
	private int sensorNodeCount;
	private int sinkNodeCount;
	protected SensorNetworkWorld sensorWorld;
	protected SinkNode theSinkNode;
	protected double transmissionRange;
	private UWEnvironment environment;
	public static final String PROP_OBSTACLE = "obstacle";
	public static String PROP_DENSITY = "node-density";
	public static EnvironmentModel emGlobalCost;
	protected transient IPainter painterNode = null;
	private int pathNumber;

	public static int mapWidth;
	public static int mapHeight;

	@Override
	public void initialize(SimulationInput sip, SimulationOutput sop) {
		super.initialize(sip, sop);
		initializeEnvironment(sip, sop);

		UWContext.mapWidth = sip.getParameterInt(MAP_WIDTH);
		UWContext.mapHeight = sip.getParameterInt(MAP_HEIGHT);

		// Creation of world
		this.sensorWorld = new SensorNetworkWorld(sop);
		this.sensorWorld.setEndOfTheWorldTime((int) sip.getStopTime() - 1);
		this.theWorld = sensorWorld;

		// Creation of Environment
		this.environment = new UWEnvironment();

		// start by extracting the values from the the simulation parameter
		this.sensorNodeCount = sip.getParameterInt(constSensorNetwork.SensorDeployment_SensorNodeCount);
		this.transmissionRange = sip.getParameterDouble(constSensorNetwork.SensorDeployment_TransmissionRange);
		this.sinkNodeCount = 4;
		// sip.getParameterInt(UWConstants.Mobile_SinkCount);
		this.random = new Random(sip.getParameterInt(randomSeed));

		// create the sensor nodes and sinkNode
		createSensorNodes();
		createMultipleSinkNodes();
		theSinkNode = createSinkNode();

		Random r = this.getRandom();
		for (int i = 0; i < sip.getParameterInt(UWConstants.NUM_HOTSPOTS); i++) {
			int randIndex = r.nextInt(this.sensorNodeCount);
			UWAgent agent = (UWAgent) this.getWorld().getSensorNodes().get(randIndex).getAgent();
			agent.setValueOfData(100);
			agent.setAgentVoI_DecayRate(0.4 + (0.7 - 0.4) * r.nextDouble());
			ArrayList<AbstractSensorAgent> neighbors = SensorRoutingHelper.getNeighbors(agent, this.getWorld());
			if (sip.getParameterEnum(LearningMethod.class).equals(LearningMethod.PROBABLISTIC_GREEDY))
				for (AbstractSensorAgent uwagent : neighbors) {
					((UWAgent) agent).setValueOfData(70);
					((UWAgent) agent).setAgentVoI_DecayRate(0.4 + (0.7 - 0.4) * r.nextDouble());
					;
				}
		}

		// create the paths
		// SensorRoutingHelper.createPathsForForwarderSensorAgents(
		// theSinkNode.getAgent(), sensorWorld);

		/*
		 * LearningMethod method = sip.getParameterEnum(LearningMethod.class);
		 * //Add ROI 1 Shape regionOfInterest = new Ellipse2D.Double(600, 220,
		 * 100, 100); this.getEnvironment().getShapes().add(regionOfInterest);
		 * this.getEnvironment().applyROI(sensorWorld, regionOfInterest, 20,
		 * 0.3); //Add ROI 2 regionOfInterest = new Ellipse2D.Double(1050, 1050,
		 * 100, 100); this.getEnvironment().getShapes().add(regionOfInterest);
		 * this.getEnvironment().applyROI(sensorWorld, regionOfInterest, 30,
		 * 0.5); //Add ROI 3 regionOfInterest = new Ellipse2D.Double(1300, 1300,
		 * 100, 100); this.getEnvironment().getShapes().add(regionOfInterest);
		 * this.getEnvironment().applyROI(sensorWorld, regionOfInterest, 100,
		 * 0.7); //Add ROI 4 regionOfInterest = new Ellipse2D.Double(220, 1050,
		 * 100, 100); this.getEnvironment().getShapes().add(regionOfInterest);
		 * this.getEnvironment().applyROI(sensorWorld, regionOfInterest, 100,
		 * 0.9);
		 */

		final int xRange = (int) (environment.getSensorDistributionArea().x
				+ environment.getSensorDistributionArea().width) - (int) environment.getSensorDistributionArea().x;
		final int yRange = (int) (environment.getSensorDistributionArea().y
				+ environment.getSensorDistributionArea().height) - (int) environment.getSensorDistributionArea().y;
		final double ratio = (double) xRange / (double) yRange;
		int columns = 1;
		int rows = 1;
		while (true) {
			if (rows * columns >= sensorWorld.getSensorNodes().size()) {
				break;
			}
			final double ratio2 = (double) columns / (double) rows;
			if (ratio < ratio2) {
				rows++;
			} else {
				columns++;
			}
		}

		// double Scale, double Velocity, int NAUVs, int VerticalNodes, int
		// HorizontalNodes
		GeneticAlgorithm GA1 = new GeneticAlgorithm(this.sensorWorld, sip.getParameterDouble(SINK_SPEED), sinkNodeCount,
				rows, columns);

		// create the visual representations
		if (sip.getParameterEnum(VisualDisplay.class) == VisualDisplay.YES) {
			// createVisualRepresentation(null);
			createRealVisualRepresentation(null);

		}
	}

	/*
	 * protected Environment createEnvironment(SimulationInput sip,
	 * SimulationOutput sop) { Environment retval =
	 * UWEnvironment.generateUWStandard(); return retval; }
	 */

	/**
	 * This method creates a mobile sink node
	 * 
	 * @return
	 */
	private SinkNode createSinkNode() {
		SinkNode sinkNode = new SinkNode();
		double sinkNodeX = sip.getParameterDouble(constSensorNetwork.SensorDeployment_SinkNodeX);
		double sinkNodeY = sip.getParameterDouble(constSensorNetwork.SensorDeployment_SinkNodeY);
		Location sinkNodeLocation = new Location(sinkNodeX, sinkNodeY);
		sinkNode.setName("UWMobile-Agent");
		sinkNode.setLocation(sinkNodeLocation);
		UWMobileAgent sinkAgent = new UWMobileAgent("SinkAgent", this.sensorWorld, sinkNodeLocation,
				new Location(380, 40));
		sinkAgent.setNode(sinkNode);
		sinkAgent.setRand(this.getRandom());
		sinkAgent.setMethod(sip.getParameterEnum(LearningMethod.class));
		sinkAgent.setSinkSpeed(sip.getParameterDouble(SINK_SPEED));

		AbstractPathCost pathCost = new PathLength();
		IHeuristic heuristic = new DistanceHeuristic(sinkAgent.getLocalDestination());
		PlannedPath path = sinkAgent.getPlannedPath();
		AStarPP aStar = new AStarPP(path, emGlobalCost, pathCost, heuristic, new MapLocationAccessibility());
		aStar.setReturnFirst(true);
		aStar.planPath(path, emGlobalCost);

		TextUi.println(sinkAgent.getPlannedPath());
		sinkAgent.setPlannedPath(path);
		sinkNode.setAgent(sinkAgent);
		sensorWorld.setSinkNode(sinkNode);
		return sinkNode;
	}

	public void createMultipleSinkNodes() {
		for (int i = 0; i < sinkNodeCount; i++) {
			SinkNode sinkNode = new SinkNode();
			double sinkNodeX = sip.getParameterDouble(constSensorNetwork.SensorDeployment_SinkNodeX);
			double sinkNodeY = sip.getParameterDouble(constSensorNetwork.SensorDeployment_SinkNodeY);
			Location sinkNodeLocation = new Location(sinkNodeX, sinkNodeY);
			sinkNode.setName("UWMobile-Agent" + String.format("%02d", i));
			sinkNode.setLocation(sinkNodeLocation);
			UWMobileAgent sinkAgent = new UWMobileAgent("SinkAgent", this.sensorWorld, sinkNodeLocation,
					new Location(380, 40));

			sinkAgent.setNode(sinkNode);
			sinkAgent.setRand(this.getRandom());
			sinkAgent.setMethod(sip.getParameterEnum(LearningMethod.class));
			sinkAgent.setSinkSpeed(sip.getParameterDouble(SINK_SPEED));

			AbstractPathCost pathCost = new PathLength();
			IHeuristic heuristic = new DistanceHeuristic(sinkAgent.getLocalDestination());
			PlannedPath path = sinkAgent.getPlannedPath();
			AStarPP aStar = new AStarPP(path, emGlobalCost, pathCost, heuristic, new MapLocationAccessibility());
			aStar.setReturnFirst(true);
			aStar.planPath(path, emGlobalCost);

			TextUi.println(sinkAgent.getPlannedPath());
			sinkAgent.setPlannedPath(path);
			sinkNode.setAgent(sinkAgent);
		}
		// TODO: Cannot set one sink node for the sensor network
		// sensorWorld.setSinkNode(sinkNode);

	}

	/**
	 * This method creates the underwater sensor nodes
	 */
	protected void createSensorNodes() {
		for (int i = 0; i < sensorNodeCount; i++) {
			final SensorNode staticNode = new SensorNode();
			staticNode.setName("S-" + String.format("%02d", i));
			final UWAgent staticNodeAgent = createSensorNodeAgent(sip, staticNode, false);
			// set the energy model
			RapaportCommunicationEnergyModel cem = new RapaportCommunicationEnergyModel(
					RapaportCommunicationEnergyModel.PowerConsumptionScenario.HIGH_PATH_LOSS);
			staticNodeAgent.setEnergyParameters(cem, 100, 0, true);
			staticNodeAgent.setAgentVoI_DecayRate(sip.getParameterDouble(DECAYTIME));
			staticNodeAgent.setSampleDecision(sip.getParameterInt(CONTINUOUS_SAMPLING));

			// look for the initial location
			Location initialLoc = new Location(getRandom().nextInt(sip.getParameterInt(MAP_WIDTH)),
					getRandom().nextInt(sip.getParameterInt(MAP_HEIGHT)));
			while (isLocationOccupied(initialLoc, emGlobalCost, PROP_OBSTACLE))
				initialLoc = new Location(getRandom().nextInt(sip.getParameterInt(MAP_WIDTH)),
						getRandom().nextInt(sip.getParameterInt(MAP_HEIGHT)));
			staticNode.setLocation(initialLoc);

			staticNode.setAgent(staticNodeAgent);
			staticNodeAgent.setNode(staticNode);
			sensorWorld.addSensorNode(staticNode);
			sensorWorld.getDirectory().addAgent(staticNodeAgent);
		}
		// distribution of the sensor nodes if SensorArrangement is not
		// Benchmark
		distributeSensorNodes(sip);
	}

	/**
	 * This method creates a simple underwater sensor agent
	 * 
	 * @param sip
	 * @param staticNode
	 * @param isAnchor
	 * @return
	 */
	public UWAgent createSensorNodeAgent(SimulationInput sip, SensorNode staticNode, boolean isAnchor) {
		final UWAgent agent = new UWAgent(staticNode.getName(), this.sensorWorld);
		agent.setTransmissionRange(sip.getParameterDouble(constSensorNetwork.SensorDeployment_TransmissionRange));
		agent.setSensorRange(sip.getParameterDouble(constSensorNetwork.SensorDeployment_SensorRange));
		return agent;
	}

	/**
	 * Distributes the location of the sensor nodes based on the specification
	 * in the environment
	 * 
	 * @param sip
	 */
	private void distributeSensorNodes(SimulationInput sip) {
		ArrayList<IMoving> list2 = new ArrayList<IMoving>(sensorWorld.getSensorNodes());
		ArrangementHelper.arrangeInAGrid((int) environment.getSensorDistributionArea().x,
				(int) environment.getSensorDistributionArea().y,
				(int) (environment.getSensorDistributionArea().x + environment.getSensorDistributionArea().width),
				(int) (environment.getSensorDistributionArea().y + environment.getSensorDistributionArea().height),
				list2);

	}

	public void createRealVisualRepresentation(Visualizer existingVisualizer) {
		if (existingVisualizer != null) {
			this.visualizer = existingVisualizer;
			this.visualizer.setUpdatedInspector(true);
			this.visualizer.removeAllObjects();
			// Set the magnification of visual scenario
			this.visualizer.getVisualCanvas().setMagnify(5.0);
			this.visualizer.setVisible(true);
		} else {
			if (sip.getSimulationControlPanel() == null) {
				this.visualizer = new Visualizer(this.sip.getParameterInt(MAP_WIDTH),
						this.sip.getParameterInt(MAP_HEIGHT), null, "Underwater scenario", true);
				// Set the magnification of visual scenario
				this.visualizer.getVisualCanvas().changeMagnify(5.0);
				this.visualizer.setUpdatedInspector(true);
				this.visualizer.setVisible(true);
			} else {
				throw new Error("Simulation control panel not to supported");
			}
		}
		/*
		 * Create an environment painter, which also paints the heatmap
		 */
		boolean doPaintEM = true;
		if (doPaintEM) {
			paintEnvironmentModel paintEM = new paintEnvironmentModel();
			/*
			 * Add a specific color scheme for the heatmap
			 */
			IValueToColor v2c = new V2CHeatmap(V2CHeatmap.ColorScheme.UcfTwoColorProgression, 0, 100);
			paintEM.addV2C(PROP_DENSITY, v2c);
			this.visualizer.addObject(emGlobalCost, paintEM);
		}

		// this.visualizer.addObject(environment, new paintEnvironment());
		// add the painting of the sink node
		this.visualizer.addObject(sensorWorld.getSinkNode(), new paintMobileNode(20, Color.black, Color.red));
		// add the painting for the sensor nodes
		if (painterNode == null) {
			// painterNode = new paintSensorNode(sensorWorld);
			painterNode = new UWPaintNode(sensorWorld);
		}
		for (final SensorNode node : sensorWorld.getSensorNodes()) {
			this.visualizer.addObject(node, painterNode);
		}
	}

	@Override
	public void createVisualRepresentation(Visualizer existingVisualizer) {
		//
		// create the visualizer covering the full considered area
		//
		if (existingVisualizer != null) {
			visualizer = existingVisualizer;
			visualizer.removeAllObjects();
		} else {
			if (sip.getSimulationControlPanel() == null) {
				visualizer = new Visualizer((int) environment.getFullArea().getWidth(),
						(int) environment.getFullArea().getHeight(), null, "Sensor network", true);
			} else {
				visualizer = new Visualizer((int) environment.getFullArea().getWidth(),
						(int) environment.getFullArea().getHeight(), null, "Sensor network", true, true);
				sip.getSimulationControlPanel().addTab("Visual", visualizer);
			}
		}
		visualizer.addObject(environment, new paintEnvironment());
		// add the painting of the sink node
		visualizer.addObject(sensorWorld.getSinkNode(), new paintMobileNode(20, Color.black, Color.red));
		// add the painting for the sensor nodes
		if (painterNode == null) {
			// painterNode = new paintSensorNode(sensorWorld);
			painterNode = new UWPaintNode(sensorWorld);
		}
		for (final SensorNode node : sensorWorld.getSensorNodes()) {
			visualizer.addObject(node, painterNode);
		}
		// add the painting for the sensor world
		visualizer.addObject(sensorWorld, new SensorNetworkWorldPainter());
		visualizer.addObject(environment, new UWPaintNoEntryArea());
	}

	/**
	 * Initializes the Environment in the scenario
	 * 
	 * @param sip
	 * @param sop
	 */
	@SuppressWarnings("static-access")
	private void initializeEnvironment(SimulationInput sip, SimulationOutput sop) {
		String obstacleMapFile = sip.getParameterString(MAP_OBSTACLES);
		String backgroundMapFile = sip.getParameterString(MAP_BACKGROUND);
		String backgroundColoredMapFile = sip.getParameterString(MAP_COLORED_BACKGROUND);
		try {
			UWContext.emGlobalCost = createEM(obstacleMapFile, backgroundMapFile, backgroundColoredMapFile);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Utility function to create a specific environment model corresponding to
	 * the RCTA stuff. It is a static function to allow being called from
	 * outside (for instance from unit tests)
	 * 
	 * @param obstacleMapFile
	 * @return
	 * @throws URISyntaxException
	 */
	private EnvironmentModel createEM(String obstacleMapFile, String backgroundMapFile, String backgroundColoredMapFile)
			throws URISyntaxException {
		URL url = this.getClass().getClassLoader().getResource(obstacleMapFile);
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(obstacleMapFile);
		System.out.println(url.getPath());

		File fileObstacles = new File(ClassLoader.getSystemClassLoader().getResource("/" + obstacleMapFile).toURI());
		LinearColorToValue lctv = new LinearColorToValue(0, 100);

		EnvironmentModel retval = new EnvironmentModel("TheModel", 0, 0, sip.getParameterInt(MAP_WIDTH),
				sip.getParameterInt(MAP_HEIGHT), 1, 1);
		retval.createProperty(PROP_OBSTACLE);
		// no obstacles in single value
		// lctv = new LinearColorToValue(0, 0);
		retval.loadDataFromImage(PROP_OBSTACLE, fileObstacles, lctv);

		if (backgroundColoredMapFile != null) {
			File fileBackground = new File(this.getClass().getResource(backgroundColoredMapFile).toURI());
			try {
				retval.loadBackgroundImage(fileBackground);
			} catch (IOException ioex) {
				TextUi.errorPrint("could not load background image file:" + fileBackground);
			}
		}

		// retval.loadDataFromImage(PROP_OBSTACLE, fileObstacles, lctv);

		// create the property for the heatmap
		// retval.createProperty(PROP_DENSITY);
		return retval;
	}

	public UWEnvironment getEnvironment() {
		return environment;
	}

	public void setEnvironment(UWEnvironment environment) {
		this.environment = environment;
	}

	@Override
	public SensorNetworkWorld getWorld() {
		return this.sensorWorld;
	}

	/**
	 * Checks if a particular location is occupied or not.
	 * 
	 * @param loc
	 * @param includeZones
	 * @return
	 */
	public static boolean isLocationOccupied(Location loc, EnvironmentModel envModel, String property) {
		if (loc == null)
			return false;

		double val = (double) envModel.getPropertyAt(property, loc.getX(), loc.getY());
		// TextUi.println(val);
		if (val > 0)
			return true;

		if (loc.getX() > UWContext.mapWidth || loc.getX() < 0 || loc.getY() > UWContext.mapHeight || loc.getY() < 0)
			return true;
		return false;
	}
}
