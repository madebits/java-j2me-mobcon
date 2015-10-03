import java.util.TreeMap;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.microedition.lcdui.Image;

public class MobRayDataBase
{
    Connection conn;

    public MobRayDataBase()
    {
        connect();
    }

    public void connect()
    {
        try {
            Class.forName("org.hsql.jdbcDriver");
            conn = DriverManager.getConnection("jdbc:HypersonicSQL:MobRay","sa","");
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+e);
        }
    }

    public void init() throws SQLException
    {
        // Create a statement object
        Statement stat = conn.createStatement();

        // Try to drop the table
        try {
            stat.executeUpdate("DROP TABLE Patient");
            stat.executeUpdate("DROP TABLE Review");
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": Table doesnt exist.");
        }

        // For compatibility to other database, use varchar(255)
        // In Hypersonic SQL, length is unlimited, like Java Strings
        stat.execute("CREATE CACHED TABLE Patient ("
            +"patientId varchar(255),"
            +"securityLevel INTEGER,"
            +"patientName varchar(255),"
            +"imageName varchar(255),"
            +"revCnt INTEGER"
            +")");

        stat.execute("CREATE CACHED TABLE Review ("
            +"patientId varchar(255),"
            +"docName varchar(255),"
            +"review varchar(255)"
            +")");

        // Close the Statement object, it is no longer used
        stat.close();

        // Use a PreparedStatement because Path and Name could contain '
        PreparedStatement patient = conn.prepareCall("INSERT INTO Patient ("
            +"patientId,securityLevel,patientName,imageName, revCnt)"
            +" VALUES (?,?,?,?,?)"
            );
        patient.clearParameters();
        patient.setString(1,"1435");
        patient.setInt(2,0);
        patient.setString(3,"Alan Smith");
        patient.setString(4,"xray1.png");
        patient.setInt(5,1);
        patient.execute();

        patient.clearParameters();
        patient.setString(1,"7455");
        patient.setInt(2,0);
        patient.setString(3,"Kurt Meyer");
        patient.setString(4,"xray2.png");
        patient.setInt(5,2);
        patient.execute();

        patient.clearParameters();
        patient.setString(1,"0816");
        patient.setInt(2,0);
        patient.setString(3,"Mike Richardson");
        patient.setString(4,"xray3.png");
        patient.setInt(5,2);
        patient.execute();

        PreparedStatement review = conn.prepareCall("INSERT INTO Review ("
            +"patientId,docName,review)"
            +" VALUES (?,?,?)"
            );

        review.clearParameters();
        review.setString(1,"1435");
        review.setString(2,"Doc Holliday");
        review.setString(3,"Patient is good");
        review.execute();

        review.clearParameters();
        review.setString(1,"7455");
        review.setString(2,"Doc Howard");
        review.setString(3,"Bad Headache");
        review.execute();

        review.clearParameters();
        review.setString(1,"7455");
        review.setString(2,"Doc Porter");
        review.setString(3,"Patient is bad");
        review.execute();

        review.clearParameters();
        review.setString(1,"0816");
        review.setString(2,"Prof Stuart");
        review.setString(3,"Bad Headache");
        review.execute();

        review.clearParameters();
        review.setString(1,"0816");
        review.setString(2,"Doc Porter");
        review.setString(3,"Simulating");
        review.execute();

    }


    public DataBaseEntry get(String patientId)
    {
        DataBaseEntry dbe = new DataBaseEntry();

        try{
            Statement stat = conn.createStatement();

            ResultSet result=stat.executeQuery(
              "SELECT * FROM Patient WHERE "
              +"Patient.patientId='"+patientId+"'");

            result.next();

            dbe.setId(result.getString(1));

            dbe.setSecurityLevel(result.getInt(2));
            dbe.setPatientName(result.getString(3));
            dbe.setImageName(result.getString(4));

            int befCnt = result.getInt(5);
            result.close();

            if(befCnt > 0){
                result=stat.executeQuery(
                  "SELECT * FROM Patient,Review WHERE "
                  +"Patient.patientId=Review.patientId AND Patient.patientId='"+patientId+"'");

                result.next();

                int i;
                for(i=0; i < befCnt; i++)
                {
                    String doc = result.getString(7);
                    String interpretation = result.getString(8);
                    dbe.addReview(doc, interpretation);
                    result.next();
                }

                // Close the ResultSet - not really necessary, but recommended
                result.close();
            }
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+e);
        }

        return dbe;
    }

    public void add(DataBaseEntry dbe)
    {
        if(this.exists(dbe.getId()))
        {
            try{
                PreparedStatement patient = conn.prepareCall("DELETE FROM Patient "
                    +"WHERE patientId = ?"
                    );
                patient.clearParameters();
                patient.setString(1,dbe.getId());
                patient.execute();

                patient = conn.prepareCall("DELETE FROM Review "
                    +"WHERE patientId = ?"
                    );
                patient.clearParameters();
                patient.setString(1,dbe.getId());
                patient.execute();

            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+e);
            }
        }

        try{
            PreparedStatement patient = conn.prepareCall("INSERT INTO Patient ("
                +"patientId,securityLevel,patientName,imageName, revCnt)"
                +" VALUES (?,?,?,?,?)"
                );

            patient.clearParameters();
            patient.setString(1,dbe.getId());
            patient.setInt(2,dbe.getSecurityLevel());
            patient.setString(3,dbe.getPatientName());
            patient.setString(4,dbe.getImageName());
            patient.setInt(5, dbe.getRevCnt());
            patient.execute();

            PreparedStatement review = conn.prepareCall("INSERT INTO Review ("
                +"patientId,docName,review)"
                +" VALUES (?,?,?)"
                );

            if(dbe.reviews.size() > 0)
            {
                for (Enumeration e = (dbe.reviews).keys() ; e.hasMoreElements() ;) {
                    review.clearParameters();
                    review.setString(1,dbe.getId());
                    String doc = (String)e.nextElement();
                    review.setString(2,doc);
                    review.setString(3,dbe.getReview(doc));
                    review.execute();
                }
            }
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+e);
        }
    }

    private boolean exists(String patientId)
    {
        boolean ret = false;

        try{
            Statement stat = conn.createStatement();

            ResultSet result=stat.executeQuery(
              "SELECT * FROM Patient WHERE "
              +"Patient.patientId='"+patientId+"'");

            ret = result.next();

            result.close();
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+e);
        }

        return ret;
    }

    private boolean exists(String patientId, String docName)
    {
        boolean ret = false;

        try{
            Statement stat = conn.createStatement();

            ResultSet result=stat.executeQuery(
              "SELECT * FROM Patient,Review WHERE "
              +"Patient.patientId=Review.patientId "
              +"AND Patient.patientId='"+patientId+"' "
              +"AND Review.docName='"+docName+"'");

            ret = result.next();

            result.close();
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+e);
        }

        return ret;
    }

    public String[] entriesToString()
    {
        Vector outVec = new Vector();
        String[] out = new String[0];

        try{
            Statement stat = conn.createStatement();

            ResultSet result=stat.executeQuery(
              "SELECT patientId FROM Patient");

            while(result.next()) {
                // Print the first column of the result
                // could use also getString("Path")
                outVec.add(result.getString(1));
            }
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+e);
        }

        out = (String[])outVec.toArray(out);
        return out;
    }


/******************************************************************************************/


    public void getResultLike(String search) throws SQLException
    {
        // Create a statement object
        Statement stat = conn.createStatement();

        // Now execute the search query
        // UCASE: This is a case insensitive search
        // ESCAPE ':' is used so it can be easily searched for '\'
        ResultSet result=stat.executeQuery(
          "SELECT Path,Name FROM Patient WHERE Path LIKE '%"+search+"%'");

        // Moves to the next record until no more records
        while(result.next()) {
            // Print the first column of the result
            // could use also getString("Path")
            System.out.println(result.getString(1)+" "+result.getString(2));
        }

        // Close the ResultSet - not really necessary, but recommended
        result.close();

    }

    public void getResult(String search) throws SQLException
    {
        // Create a statement object
        Statement stat = conn.createStatement();

        // Now execute the search query
        // UCASE: This is a case insensitive search
        // ESCAPE ':' is used so it can be easily searched for '\'
        ResultSet result=stat.executeQuery(
          "SELECT Path,Name FROM Patient WHERE Path IS '"+search+"'");

        // Moves to the next record until no more records
        while(result.next()) {
            // Print the first column of the result
            // could use also getString("Path")
            System.out.println(result.getString(1)+" "+result.getString(2));
        }

        // Close the ResultSet - not really necessary, but recommended
        result.close();

    }

    public void showAll() throws SQLException
    {
        // Create a statement object
        Statement stat = conn.createStatement();

        // Now execute the search query
        // UCASE: This is a case insensitive search
        // ESCAPE ':' is used so it can be easily searched for '\'
        ResultSet result=stat.executeQuery(
          "SELECT * FROM Patient,Review where Patient.patientId=Review.patientId");

        // Moves to the next record until no more records
        while(result.next()) {
            // Print the first column of the result
            // could use also getString("Path")
            System.out.println(result.getString(1)+" "+result.getInt(2)+" "+result.getString(3)+" "+result.getString(4)+" "+result.getString(5)+" "+result.getString(6)+" "+result.getString(7)+" "+result.getString(8));
        }

        // Close the ResultSet - not really necessary, but recommended
        result.close();


    }

    public void close()
    {
        try
        {
            conn.close();
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+e);
        }
    }

}