import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * @scr
 * @img
 * @log
 * @log.logCommand
 * @log.logMethod
 * @dp
 */
public class MobApp
{
    /**
     * @scr.label "First"
     * @scr.firstDisplay
     * @scr.nextButton alertScreen
     * @scr.exitButton
     * @scr.stringItem stringItem
     * @scr.textField textField1 textField2
     */
    Form form1;

    /**
     * @img.local
     * @img.name "/xrayLocal.png"
     */
    Image ray;

   /**
     * @scr.label "Image"
     * @scr.image ray
     * @scr.altText "This is an image Item"
     * @scr.layout ImageItem.LAYOUT_CENTER
     */
    ImageItem imageItem;

   /**
     * @scr.label "Second"
     * @scr.nextButton list1
     * @scr.backButton form1
     * @scr.exitButton
     * @scr.imageItem imageItem
     * @scr.textField textField2
     */
    Form form2;

    /**
     * @scr.label "Text1"
     * @scr.string "Hello"
     * @scr.constraints# TextField.ANY
     */
    TextField textField1;

    /**
     * @scr.label "Text2"
     * @scr.string "How are you"
     */
    TextField textField2;

    /**
     * @scr.label "List"
     * @scr.listType List.IMPLICIT
     * @scr.listElements "NextWindow" "Exit"
     * @scr.commandAction sampleListAction1
     */
    List list1;

    /**
     * @scr.label "List"
     * @scr.listType List.EXCLUSIVE
     * @scr.listElementArray elArray
     * @scr.commandAction sampleListAction2
     */
    List list2;

   /**
     * @scr.label "Alert"
     * @scr.alertText "This is an alert Screen"
     * @scr.alertImage# ray
     * @scr.alertType ERROR
     * @scr.alertTimeout Alert.FOREVER
     * @scr.alertNextScreen form2
     */
    Alert alertScreen;

   /**
     * @scr.label "String"
     * @scr.string "This is an string Item"
     */
    StringItem stringItem;

    /****************************************/
    /************ ADDED TO CLASS ************/
    /****************************************/
    String elArray[];

    /**
     * @begin
     */
    public void callList2()
    {
        elArray = new String[2];
        elArray[0] = "aaa";
        elArray[1] = "bbb";
    }

    /**** Methods for the List Commands ****/
    public void sampleListAction1(List list)
    {
        String selected = listElements[list.getSelectedIndex()];

        if(selected == "NextWindow")
        {
            callList2();
        }

        if(selected == "Exit")
        {
            commandAction(exitCommand, list);
        }

    }

    public void sampleListAction2(List list)
    {
        String selected = listElements[list.getSelectedIndex()];

        callMessageBox("Message" ,selected);
    }
}