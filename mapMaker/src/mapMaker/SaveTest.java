package mapMaker;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.File;
import org.junit.jupiter.api.Test;

class SaveTest {

	@Test
	void test() {
		Map testMap=new Map(2,2);
		String localPath=new File("").getAbsolutePath();
		System.out.print(localPath+"\n");//debug
		String testData=localPath+File.separator+"src"+File.separator+"testData"+File.separator;//file of test data
		System.out.print(testData);
		try {//test creating tiles 
			testMap.getTileSet().createTile("city", 
					testData+"city.png");
			testMap.getTileSet().createTile("forest", 
					testData+"forest.png");
			testMap.getTileSet().createTile("grass",
					testData+"grass.png");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException while loading immages:"+e.getMessage());
		}//end try/catch
		//test adding tiles
		testMap.replaceTile(0, 0, 0, "grass");
		testMap.replaceTile(0, 1, 0, "grass");
		testMap.replaceTile(1, 0, 0, "forest");
		testMap.replaceTile(1, 1, 0, "city");
		
		try {//test saving
			testMap.saveMap(testData+"testSave.txt");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Fail durring save operations:"+e.getMessage());
		}
		//test loading
		try {
			Map loadedMap=new Map(testData+"testSave.txt");
			System.out.print(loadedMap.getTile(0, 0, 0).getName());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Fail durring load operations:"+e.getMessage());
		}

	}//end test

}//end SaveTest
