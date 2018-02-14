package wbif.sjx.ModularImageAnalysis;

import wbif.sjx.ModularImageAnalysis.Object.Obj;
import wbif.sjx.ModularImageAnalysis.Object.ObjCollection;

/**
 * Created by sc13967 on 12/02/2018.
 */
public abstract class ExpectedObjects {
    public abstract int[][] getCoordinates3D();

    public ObjCollection getObjects(String objectName, boolean eightBit, double dppXY, double dppZ, String calibratedUnits) {
        // Initialising object store
        ObjCollection testObjects = new ObjCollection(objectName);

        // Adding all provided coordinates to each object
        int[][] coordinates = getCoordinates3D();
        for (int i = 0;i<coordinates.length;i++) {
            int ID = eightBit ? coordinates[i][0] : coordinates[i][1];
            int x = coordinates[i][2];
            int y = coordinates[i][3];
            int z = coordinates[i][5];
            int t = coordinates[i][6];

            testObjects.putIfAbsent(ID,new Obj(objectName,ID,dppXY,dppZ,calibratedUnits));

            Obj testObject = testObjects.get(ID);
            testObject.addCoord(x,y,z);
            testObject.setT(t);

        }

        return testObjects;

    }
}