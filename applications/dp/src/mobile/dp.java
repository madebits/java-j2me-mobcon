import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * @scr
 * @dp
 * @log
 * @log.logCommand
 * @log.logMethod
 * @enc
 * @enc.encrypt
 */
public class MobApp
{
    /**
     * @scr.label "Form0"
     * @scr.firstDisplay
     * @scr.exitButton
     * @scr.nextButton form1
     * @scr.textField welcome
     */
    Form form0;

    /**
     * @scr.label "WELCOME!"
     * @scr.string "First form is made of persistent fields, that can be saved locally or remotely on server(Saving remotely, means always saving locally too!!!)."
     */
    TextField welcome;

    /**
     * @scr.label "Form1"
     * @scr.exitButton
     * @scr.textField textField1 textField2
     * @scr.command saveCommand saveRemoteCommand loadCommand loadRemoteCommand
     */
    Form form1;

    /**
     * @dp.access
     * @dp.store
     */
    String text;


    /**
     * @dp.access
     * @dp.store
     */
    String text2;

    /**
     * @scr.addCommand
     * @scr.label "Save Local"
     * @scr.execute
        setText(textField1.getString());
        setText2(textField2.getString());
     */
    Command saveCommand;

    /**
     * @scr.addCommand
     * @scr.label "Save Remote"
     * @scr.execute
        setText(textField1.getString());
        setText2(textField2.getString());
        store();
     */
    Command saveRemoteCommand;

    /**
     * @scr.addCommand
     * @scr.label "Load Local"
     * @scr.execute
        callMessageBox("FromStore", store.storeToString());
     */
    Command loadCommand;

    /**
     * @scr.addCommand
     * @scr.label "Load Remote"
     * @scr.execute
        retrieve();
        callForm1();
     */
    Command loadRemoteCommand;

    /****************************************/
    /************ ADDED TO CLASS ************/
    /****************************************/
    TextField textField1;
    TextField textField2;

    /** TEXTFIELD ACTIONS **/
    public void callTextField1()
    {
        String text = "";
        text = getText();
        textField1 = new TextField("TextField1", text, 256, TextField.ANY);
    }

    public void callTextField2()
    {
        String text = "";
        text = getText2();
        textField2 = new TextField("TextField2", text, 256, TextField.ANY);
    }

}