import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * @scr
 * @dp
 * @img
 * @ses
 * @ses.rememberLastDisplay
 * @ses.id getMobileID()
 * @log
 * @log.logCommand
 * @log.logMethod
 * @enc
 * @enc.encrypt
 */
public class MobApp
{
    /**
     * @scr.label "Login"
     * @scr.firstDisplay
     * @scr.exitbutton
     * @scr.textField TF_userName TF_passWord
     * @scr.command C_login
     */
    Form F_login;

    /**
     * @scr.label "Username"
     * @scr.string (String)context.get("userName")
     */
    TextField TF_userName;

    /**
     * @scr.label "Password"
     * @scr.string (String)context.get("userPwd")
     * @scr.constraints TextField.PASSWORD
     */
    TextField TF_passWord;

    /**
     * @scr.addCommand
     * @scr.label "Login"
     * @scr.execute
        context.put("userName", TF_userName.getString());
        context.put("userPwd", TF_passWord.getString());
        if(TF_passWord.getString().equals("pwd")) callF_welcome();
        else callA_badPassword();
     */
    Command C_login;

    /**
     * @scr.label "Start"
     * @scr.backButton F_login
     * @scr.nextButton F_choosePatient
     * @scr.stringItem SI_welcome1
     * @scr.stringItem SI_welcome2
     */
    Form F_welcome;

    /**
     * @scr.label ""
     * @scr.string "Welcome!\n"
     * @scr.fontStyle Font.STYLE_BOLD
     * @scr.fontSize Font.SIZE_LARGE
     * @scr.layout Item.LAYOUT_CENTER
     */
    StringItem SI_welcome1;

    /**
     * @scr.label ""
     * @scr.string "\nThis is a demonstration of an application developped with Mobile Containers!"
     * @scr.fontStyle# Font.STYLE_ITALIC
     * @scr.layout Item.LAYOUT_CENTER
     */
    StringItem SI_welcome2;

    /**
     * @scr.label "Choose a Patient"
     * @scr.backButton F_welcome
     * @scr.stringItem SI_choosePatient
     * @scr.choiceGroup CG_choosePatient
     */
    Form F_choosePatient;

     /**
     * @scr.label ""
     * @scr.string "Please choose the patient's ID to see the ray-image.\n\n"
     * @scr.fontStyle Font.STYLE_ITALIC
     * @scr.layout Item.LAYOUT_CENTER
     */
    StringItem SI_choosePatient;

   /**
     * @scr.label "Patient List"
     * @scr.listType Choice.EXCLUSIVE
     * @scr.listElementArray (String[])(this.retrieveDBEntryList())
     * @scr.commandAction choosePatientCG_Action
     */
    ChoiceGroup CG_choosePatient;

    /**
     * @scr.label "Patients Image"
     * @scr.exitButton
     * @scr.backButton F_choosePatient
     * @scr.command C_viewInterpretationCommand C_addInterpretationCommand
     * @scr.stringItem SI_patientName
     * @scr.imageItem II_ray
     * @ses.noBackEntry
     */
    Form F_patient;

    /**
     * @scr.label ""
     * @scr.string "Patient: "+getDbe().getPatientName()+"\n"
     * @scr.fontStyle Font.STYLE_BOLD
     * @scr.layout Item.LAYOUT_CENTER
     */
    StringItem SI_patientName;

    /**
     * @img.name getDbe().getImageName()
     * @img.width# 100
     * @img.height# 100
     * @img.maxcolors# 32
     * @img.maxsize# 25000
     */
    Image I_ray;

   /**
     * @scr.label ""
     * @scr.image I_ray
     * @scr.altText "This is an image Item"
     * @scr.layout Item.LAYOUT_CENTER
     */
    ImageItem II_ray;

    /**
     * @scr.label "View Interpretation"
     * @scr.exitButton
     * @scr.backButton F_patient
     * @scr.stringItem SI_interpretations
     * @ses.noBackEntry
     */
    Form F_viewInterpretation;

   /**
     * @scr.label "Interpretations for "+getDbe().getPatientName()+":\n\n"
     * @scr.string getDbe().getAllReviews()
     * @scr.fontStyle Font.STYLE_ITALIC
     * @scr.layout Item.LAYOUT_CENTER
     */
    StringItem SI_interpretations;

    /**
     * @scr.label "Add Interpretation"
     * @scr.exitButton
     * @scr.backButton F_patient
     * @scr.command C_saveInterpretationCommand
     * @scr.textField TF_enterInterpretation
     * @scr.stringItem SI_interpretations
     * @ses.noBackEntry
     */
    Form F_addInterpretation;


    /**
     * @scr.label "Enter your Interpretation"
     * @scr.string ""
     * @scr.maxSize 100
     */
    TextField TF_enterInterpretation;

    /**
     * @scr.addCommand
     * @scr.label "View Interpretation"
     * @scr.execute
        callF_viewInterpretation();
     */
    Command C_viewInterpretationCommand;

    /**
     * @scr.addCommand
     * @scr.label "Add Interpretation"
     * @scr.execute
        callF_addInterpretation();
     */
    Command C_addInterpretationCommand;

    /**
     * @scr.addCommand
     * @scr.label "Save Interpretation"
     * @scr.execute
        DataBaseEntry dbe = getDbe();
        dbe.addReview((String)(context.get("userName")), TF_enterInterpretation.getString());
        setDbe(dbe);
        storeEntry(dbe);
        callA_interpretationSaved();
     */
    Command C_saveInterpretationCommand;

   /**
     * @scr.label "MESSAGE"
     * @scr.alertText "Your interpretation is saved succesfully!"
     * @scr.alertImage I_warning
     * @scr.alertType# ERROR
     * @scr.alertTimeout Alert.FOREVER
     * @scr.alertNextScreen F_choosePatient
     */
    Alert A_interpretationSaved;

   /**
     * @scr.label "ERROR"
     * @scr.alertText "Bad login. Check your name and your password."
     * @scr.alertImage I_warning
     * @scr.alertTimeout 5000
     * @scr.alertNextScreen F_login
     */
    Alert A_badPassword;

    /**
     * @img.local
     * @img.name "/warning.png"
     */
    Image I_warning;

    /**
     * @dp.access
     * @dp.store
     */
    DataBaseEntry dbe;
    /****************************************/
    /************ ADDED TO CLASS ************/
    /****************************************/


    /***** Added ChoiceGroup Actions *****/
    public void choosePatientCG_Action(ChoiceGroup cg)
    {
        String selected = cg.getString(cg.getSelectedIndex());

        DataBaseEntry dbe = new DataBaseEntry();
        retrieveEntry(selected, dbe);
        setDbe(dbe);

        callF_patient();
    }

    /*********************************/
    /**** Communication Commands *****/
    /*********************************/
    public void storeEntry(DataBaseEntry dbe)
    {
        try{
            NetMessage message = new NetMessage();
            message.setCID(CID);
            message.setPID("0");
            message.setIID("0");
            message.setOID("1");
            message.setData(dbe.object2record());

            CTX.sendNetMessage(message);
        } catch(Exception e){
            System.out.println("Error in storeEntry: "+e);
        }
    }

    public void retrieveEntry(String id, DataBaseEntry dbe)
    {
        try{
            NetMessage message = new NetMessage();
            message.setCID(CID);
            message.setPID("0");
            message.setIID("0");
            message.setOID("2");
            message.setData(id.getBytes());

            NetMessage ret = new NetMessage();
            ret = CTX.getNetMessageResult(message);

            dbe.record2object(ret.data);
        } catch(Exception e){
            System.out.println("Error in retrieveEntry: "+e);
        }
    }

    public Object retrieveDBEntryList()
    {
        try{
            StoreableStringArray ssa = new StoreableStringArray();
            NetMessage message = new NetMessage();
            message.setCID(CID);
            message.setPID("0");
            message.setIID("0");
            message.setOID("3");

            NetMessage ret = new NetMessage();
            ret = CTX.getNetMessageResult(message);

            ssa.record2object(ret.data);
            return ssa.getStringArray();
        } catch(Exception e){
            System.out.println("Error in retrieveDBEntryList: "+e);
            return null;
        }
    }

}