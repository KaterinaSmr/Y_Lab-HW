package orgStructure;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class OrgStructureTest {
    public static void main(String[] args) throws IOException, URISyntaxException {
        OrgStructureParser orgStructureParser = new OrgStructureParserImpl();
        File file = new File(OrgStructureTest.class.getResource("org_structure.csv").toURI());
        Employee bigBoss = orgStructureParser.parseStructure(file);
        System.out.println(bigBoss);
    }
}
