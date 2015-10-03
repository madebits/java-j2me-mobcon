import java.util.TreeMap;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.io.InputStreamReader;
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

public class AddEntry
{
    Connection conn;

    public AddEntry()
    {
        System.out.println("MobRay-Systems\n");
        System.out.println("Welcome!");
        String choice ="";

        while(!choice.equals("e"))
        {
            try{
                System.out.println("\nDo you want to add a new (p)atient, a new (i)nterpretation or (e)xit?");
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in)) ;
                choice = in.readLine();

                if(choice.equals("p")){
                    addPatient();
                }
                if(choice.equals("i")){
                    addInterpretation();
                }
                if(choice.equals("e")){
                    break;
                }
            }catch(Exception e){
                System.out.println(this.getClass().getName()+": "+e);
            }
        }
    }

    public void addPatient() throws Exception
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in)) ;

        System.out.println("Patient ID:");
        String id = in.readLine();

        System.out.println("Patient Name:");
        String name = in.readLine();

        System.out.println("Image Name:");
        String image = in.readLine();

        try {
            Class.forName("org.hsql.jdbcDriver");
            conn = DriverManager.getConnection("jdbc:HypersonicSQL:MobRay","sa","");
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+e);
        }

        DataBaseEntry dbe = new DataBaseEntry();
        dbe.setId(id);
        dbe.setPatientName(name);
        dbe.setSecurityLevel(0);
        dbe.setImageName(image);
        //dbe.addReview("sss", "ccc");

        this.add(dbe);

        try
        {
            conn.close();
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+e);
        }

    }

    public void addInterpretation() throws Exception
    {
        try {
            Class.forName("org.hsql.jdbcDriver");
            conn = DriverManager.getConnection("jdbc:HypersonicSQL:MobRay","sa","");
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+e);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in)) ;

        System.out.println("Patient ID:");
        String id = in.readLine();

        DataBaseEntry dbe = new DataBaseEntry();

        try
        {
            dbe = this.get(id);
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": Patient does not exist!");
            try
            {
                conn.close();
            }catch(Exception ex){
                System.out.println(this.getClass().getName()+": "+ex);
            }

            return;
        }

        System.out.println("The Patient is: \n"+dbe);

        System.out.println("Doctor Name:");
        String docName = in.readLine();

        System.out.println("Interpretation:");
        String inter = in.readLine();

        dbe.addReview(docName, inter);

        this.add(dbe);

        try
        {
            conn.close();
        }catch(Exception e){
            System.out.println(this.getClass().getName()+": "+e);
        }

    }

    public DataBaseEntry get(String patientId) throws Exception
    {
        DataBaseEntry dbe = new DataBaseEntry();

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



    public static void main(String[] args)
    {
        AddEntry ae = new AddEntry();

        System.out.println("Finished");
    }
}