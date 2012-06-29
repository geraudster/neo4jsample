package com.valtech.neo4jsample;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String DB_PATH = "target/data/graphdb";
	private static final int MAX_NB_NODES = 10;

	public static void main( String[] args )
    {
//		simpleInsert();
//		firstSearch();
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		registerShutdownHook( graphDb );

		bigGraph(graphDb);
		
		graphDb.shutdown();	
    }

	public static void firstSearch(GraphDatabaseService graphDb) {
		// TODO Auto-generated method stub
		Index<Node> nodeIndex = graphDb.index().forNodes( "nodes" );
		
		Node firstNode;
		Node secondNode;
		Relationship relation;
		
		Transaction tx = graphDb.beginTx();
		try {
			// Gustave -[:KNOWS]-> Arthur
			firstNode = graphDb.createNode();
			firstNode.setProperty("name", "Gustave");
			
			secondNode = graphDb.createNode();
			secondNode.setProperty("name", "Arthur");
			relation = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
			
nodeIndex.add(firstNode, "name", "Gustave");
nodeIndex.add(secondNode, "name", "Arthur");

Node requestedNode = nodeIndex.get("name", "Arthur").getSingle();
Relationship incomingRelation = requestedNode.getSingleRelationship(RelTypes.KNOWS, Direction.INCOMING);

System.out.printf("%s <-[:%s]- %s",
		requestedNode.getProperty("name"),
		incomingRelation.getType(),
		incomingRelation.getStartNode().getProperty("name"));

			// let's remove the data
			firstNode.getSingleRelationship( RelTypes.KNOWS, Direction.OUTGOING ).delete();
			firstNode.delete();
			secondNode.delete();
			
			tx.success();
		} finally {
			tx.finish();
		}
	
	}

	public static void simpleInsert() {
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		registerShutdownHook( graphDb );
		
		Node firstNode;
		Node secondNode;
		Relationship relation;
		
		Transaction tx = graphDb.beginTx();
		try {
			// Gustave -[:KNOWS]-> Arthur
			firstNode = graphDb.createNode();
			firstNode.setProperty("name", "Gustave");
			secondNode = graphDb.createNode();
			secondNode.setProperty("name", "Arthur");
			relation = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
			
			System.out.printf("%s -[:%s]-> %s",
					firstNode.getProperty("name"),
					relation.getType(),
					secondNode.getProperty("name"));
		
			// let's remove the data
			firstNode.getSingleRelationship( RelTypes.KNOWS, Direction.OUTGOING ).delete();
			firstNode.delete();
			secondNode.delete();
			
			tx.success();
		} finally {
			tx.finish();
		}
		
		graphDb.shutdown();
	}

	public static void bigGraph(GraphDatabaseService graphDb) {
		Index<Node> nodeIndex = graphDb.index().forNodes( "nodes" );
		
		String nodeId;
		Node firstNode;
		Node secondNode;
		Relationship relation;
		
		int nbRelations = 0;
		int randomId = 0;
		Transaction tx = graphDb.beginTx();
		try {
			nodeId = "Node 0";
			firstNode = graphDb.createNode();
			firstNode.setProperty("name", nodeId);
			nodeIndex.add(firstNode, "name", nodeId);

			for(int i = 1; i<MAX_NB_NODES; i++)
			{
				nodeId = "Node " + i;
				firstNode = graphDb.createNode();
				firstNode.setProperty("name", nodeId);
				nodeIndex.add(firstNode, "name", nodeId);
				nbRelations = (int) (Math.random() * 10);
				for(int j = 0; j < nbRelations; j++){
					randomId = (int) (Math.random() * (i-1));
					secondNode = nodeIndex.get("name", "Node " + randomId).getSingle();
					firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
				}
			}

			// friends of node 2
			Node node2 = nodeIndex.get("name", "Node 2").getSingle();
			Traverser friends = getFriends(node2);
			String output = "";
			int numberOfFriends = 0;
			for(Path friendPath : friends) {
				output += "At depth " + friendPath.length() + " => "
			              + friendPath.endNode()
			                      .getProperty( "name" ) + "\n";
			    numberOfFriends++;
			}
			System.out.printf("Number of friends: %d\n", numberOfFriends);
			System.out.println(output);
			tx.success();
		} finally {
			tx.finish();
		}
		
	}

	private static Traverser getFriends(
	        final Node person )
	{
	    TraversalDescription td = Traversal.description()
	            .breadthFirst()
	            .relationships( RelTypes.KNOWS, Direction.INCOMING)
	            .evaluator( Evaluators.excludeStartPosition() );
	    return td.traverse( person );
	}

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// TODO Auto-generated method stub
		// Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running example before it's completed)
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );		
	}
}
