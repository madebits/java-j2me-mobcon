import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * @scr
 * @log
 * @log.logCommand
 * @log.logMethod
 * @ctmixex
 */
public class MobApp
{
    /**
     * @scr.label "FirstScreen"
     * @scr.firstDisplay
     * @scr.nextButton form2
     * @scr.stringItem stringItem1
     */
    Form form1;


    /**
     * @scr.label "Second Screen"
     * @scr.exitButton
     * @scr.stringItem stringItem2
     */
    Form form2;

    /**
     * @scr.label ""
     * @scr.string "When no special MixTemplate is used, the application will abort when pressing next."
     */
    StringItem stringItem1;

    /**
     * @scr.label "Success! "
     * @scr.string "Mixing Transformer successfully applied"
     */
    StringItem stringItem2;

    /****************************************/
    /************ ADDED TO CLASS ************/
    /****************************************/

    public void callForm2()
    {
        doSomething();
    }
}