package com.valtech.neo4jsample;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Create the test case
     *
     */
    public AppTest( )
    {
    }

    GraphDatabaseService graphDb;
    
    @Before
    public void prepareTestDatabase()
    {
        graphDb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
    }

    @After
    public void destroyTestDatabase()
    {
        graphDb.shutdown();
    }
    
    @Test
	public void testBigGraph()
	 throws Exception {
		App.bigGraph(graphDb);
	}

}
